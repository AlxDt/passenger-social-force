package com.crowdsimulation.controller.graphics;

import com.crowdsimulation.controller.Controller;
import com.crowdsimulation.controller.Main;
import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.headful.QueueingFloorField;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.obstacle.TicketBooth;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.obstacle.Wall;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.Queueable;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.StationGate;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.TrainDoor;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.elevator.ElevatorPortal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.escalator.EscalatorPortal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.stairs.StairPortal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.TicketBoothTransactionArea;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable.Security;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable.Turnstile;
import com.crowdsimulation.model.simulator.Simulator;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GraphicsController extends Controller {
    private static final HashMap<Class<?>, Color> PATCH_COLORS = new HashMap<>();
    private static final int FLOOR_FIELD_COLOR_HUE = 115;

    public static TicketBoothTransactionArea.DrawOrientation drawTicketBoothOrientation;
    public static Rectangle extraRectangle;
    public static Patch extraPatch;
    public static boolean validTicketBoothDraw;

    static {
        GraphicsController.drawTicketBoothOrientation = TicketBoothTransactionArea.DrawOrientation.UP;
        GraphicsController.extraRectangle = null;
        GraphicsController.extraPatch = null;
        GraphicsController.validTicketBoothDraw = true;

        // The designated colors of the patch amenities (or lack thereof)
        PATCH_COLORS.put(null, Color.WHITE); // Empty patch

        PATCH_COLORS.put(StationGate.class, Color.BLUE); // Station gate
        PATCH_COLORS.put(TrainDoor.class, Color.RED); // Train door

        PATCH_COLORS.put(StairPortal.class, Color.VIOLET); // Stairs
        PATCH_COLORS.put(EscalatorPortal.class, Color.DARKVIOLET); // Escalator
        PATCH_COLORS.put(ElevatorPortal.class, Color.DEEPPINK); // Elevator

        PATCH_COLORS.put(Security.class, Color.DEEPSKYBLUE); // Security gate
        PATCH_COLORS.put(TicketBooth.class, Color.GREEN); // Ticket booth
        PATCH_COLORS.put(TicketBoothTransactionArea.class, Color.GRAY); // Ticket booth transaction area
        PATCH_COLORS.put(Turnstile.class, Color.ORANGE); // Turnstile

        PATCH_COLORS.put(Wall.class, Color.BLACK); // Wall
    }

    // Send a request to draw the station view on the canvas
    public static void requestDrawStationView(
            StackPane canvases,
            Floor floor,
            double tileSize,
            boolean background) {
        javafx.application.Platform.runLater(() -> {
            // Tell the JavaFX thread that we'd like to draw on the canvas
            drawStationView(canvases, floor, tileSize, background);
        });
    }

    // Send a request to draw the mouse listeners on top of the canvases
    public static void requestDrawListeners(StackPane canvases, Pane overlay, Floor floor, double tileSize) {
        javafx.application.Platform.runLater(() -> {
            // Tell the JavaFX thread that we'd like to draw on the canvas
            drawListeners(canvases, overlay, floor, tileSize);
        });
    }

    // Draw all that is needed on the station view on the canvases
    private static void drawStationView(StackPane canvases, Floor floor, double tileSize, boolean background) {
        // Get the canvases and their graphics contexts
        final Canvas backgroundCanvas = (Canvas) canvases.getChildren().get(0);
        final Canvas foregroundCanvas = (Canvas) canvases.getChildren().get(1);

        final GraphicsContext backgroundGraphicsContext = backgroundCanvas.getGraphicsContext2D();
        final GraphicsContext foregroundGraphicsContext = foregroundCanvas.getGraphicsContext2D();

        // Get the height and width of the canvases
        final double canvasWidth = backgroundCanvas.getWidth();
        final double canvasHeight = backgroundCanvas.getHeight();

        // Clear everything in the foreground canvas, if all dynamic elements are to be drawn
        if (!background) {
            foregroundGraphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);
        }

        // Draw all the patches of this floor
        for (int row = 0; row < floor.getRows(); row++) {
            for (int column = 0; column < floor.getColumns(); column++) {
                // Get the current patch
                Patch currentPatch = Main.simulator.getCurrentFloor().getPatch(row, column);

                // Draw only the background (the environment) if requested
                // Draw only the foreground (the passengers) if otherwise
                if (background) {
                    // Draw graphics corresponding to whatever is in the content of the patch
                    // If the patch has no amenity on it, just draw a blank patch
                    Amenity patchAmenity = currentPatch.getAmenity();
                    Color patchColor;

                    if (patchAmenity == null) {
                        // There isn't an amenity on this patch, so just use the color corresponding to a blank patch
                        patchColor = PATCH_COLORS.get(null);

                        // Show the floor fields of the current target with the current floor field state
                        Queueable target = Main.simulator.getCurrentFloorFieldTarget();
                        QueueingFloorField.FloorFieldState floorFieldState
                                = Main.simulator.getCurrentFloorFieldState();

                        Map<Queueable, Map<QueueingFloorField.FloorFieldState, Double>> floorFieldValues
                                = currentPatch.getFloorFieldValues();
                        Map<QueueingFloorField.FloorFieldState, Double> floorFieldStateDoubleMap
                                = floorFieldValues.get(target);

                        // Draw something if there is a target associated with this patch
                        if (floorFieldStateDoubleMap != null) {
                            // If the current patch's floor field state matches the current floor field state, draw
                            // a green patch
                            if (floorFieldStateDoubleMap.get(floorFieldState) != null) {
                                double value = floorFieldStateDoubleMap.get(floorFieldState);

                                // Map the colors of this patch to the its field value's intensity
                                patchColor = Color.hsb(
                                        FLOOR_FIELD_COLOR_HUE,
                                        Main.simulator.isFloorFieldDrawing() ? value : 0.1,
                                        1.0
                                );
                            } else {
                                // There is a floor field value here with the same target, but it is not of the
                                // current floor field state
                                // Hence, just draw an unsaturated patch
                                patchColor = Color.hsb(
                                        FLOOR_FIELD_COLOR_HUE,
                                        0.1,
                                        1.0
                                );
                            }
                        } else if (!floorFieldValues.isEmpty()) {
                            // If there isn't a floor field with the current target, but the list of floor field
                            // values isn't empty, there are still other floor field values on this patch
                            // Hence, just draw an unsaturated patch
                            patchColor = Color.hsb(
                                    FLOOR_FIELD_COLOR_HUE,
                                    0.1,
                                    1.0
                            );
                        }
                    } else {
                        // There is an amenity on this patch, so draw it according to its corresponding color
                        patchColor = PATCH_COLORS.get(patchAmenity.getClass());

                        // If floor field drawing is on, only color amenities which are of the current class
                        if (Main.simulator.isFloorFieldDrawing()) {
                            // Only color the current amenity - unsaturate the rest
                            if (!patchAmenity.equals(Main.simulator.getCurrentFloorFieldTarget())) {
                                double hue = patchColor.getHue();

                                patchColor = Color.hsb(
                                        hue,
                                        0.15,
                                        1.0
                                );
                            }
                        }
                    }

                    // Set the color
                    backgroundGraphicsContext.setFill(patchColor);

                    // Draw the patch
                    backgroundGraphicsContext.fillRect(column * tileSize, row * tileSize, tileSize, tileSize);
                } else {
/*                    // TODO: Draw passengers
                    foregroundGraphicsContext.fillOval(
                            30 * tileSize - 2 * 0.5,
                            30 * tileSize - 2 * 0.5,
                            2,
                            2
                    );*/
                }
            }
        }

        // If this amenity is also the currently selected amenity in the simulator, draw a circle around
        // said amenity
        final double CIRCLE_DIAMETER = 100.0;

        Amenity currentAmenity = Main.simulator.getCurrentAmenity();

        if (currentAmenity != null
                && currentAmenity.getPatch().getFloor() == Main.simulator.getCurrentFloor()
                && !Main.simulator.isFloorFieldDrawing()) {
            double row = currentAmenity.getPatch().getMatrixPosition().getRow();
            double column = currentAmenity.getPatch().getMatrixPosition().getColumn();

            backgroundGraphicsContext.strokeOval(
                    (column * tileSize - CIRCLE_DIAMETER * 0.5 + tileSize * 0.5),
                    (row * tileSize - CIRCLE_DIAMETER * 0.5 + tileSize * 0.5),
                    CIRCLE_DIAMETER,
                    CIRCLE_DIAMETER
            );
        }
    }

    // Draw the mouse listeners over the canvases
    // These listeners allows the user to graphically interact with the station amenities
    private static void drawListeners(StackPane canvases, Pane overlay, Floor floor, double tileSize) {
        // Get the background canvas
        final Canvas backgroundCanvas = (Canvas) canvases.getChildren().get(0);

        // Draw listeners for the canvas (used for the detection of the orientation when drawing ticket booths)
        backgroundCanvas.getScene().setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case W:
                    GraphicsController.drawTicketBoothOrientation = TicketBoothTransactionArea.DrawOrientation.UP;

                    break;
                case D:
                    GraphicsController.drawTicketBoothOrientation = TicketBoothTransactionArea.DrawOrientation.RIGHT;

                    break;
                case S:
                    GraphicsController.drawTicketBoothOrientation = TicketBoothTransactionArea.DrawOrientation.DOWN;

                    break;
                case A:
                    GraphicsController.drawTicketBoothOrientation = TicketBoothTransactionArea.DrawOrientation.LEFT;

                    break;
            }
        });

        // Initialize the rectangle matrix to be used as listeners
        final Rectangle[][] rectangles
                = new Rectangle[
                Main.simulator.getCurrentFloor().getRows()]
                [
                Main.simulator.getCurrentFloor().getColumns()
                ];

        // Draw listeners for each patch
        for (int row = 0; row < Main.simulator.getCurrentFloor().getRows(); row++) {
            for (int column = 0; column < Main.simulator.getCurrentFloor().getColumns(); column++) {
                // Individually initialize each listener in the matrix
                rectangles[row][column] = new Rectangle(column * tileSize, row * tileSize, tileSize, tileSize);

                Rectangle rectangle = rectangles[row][column];

                rectangle.setFill(Color.DARKGRAY);
                rectangle.setOpacity(0.0);
                rectangle.getProperties().put("row", row);
                rectangle.getProperties().put("column", column);

                final int columnCopy = column;
                final int rowCopy = row;

                // Actions for when the mouse enters a listener
                rectangle.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> {
                    // This listener should only be active when there is a subcategory present
                    if (Main.simulator.getBuildSubcategory() != Simulator.BuildSubcategory.NONE) {
                        int patchRow;
                        int patchColumn;

                        patchRow = (int) rectangle.getProperties().get("row");
                        patchColumn = (int) rectangle.getProperties().get("column");

                        // Get the patch where the mouse is currently on
                        Patch patch = Main.simulator.getCurrentFloor().getPatch(patchRow, patchColumn);

                        rectangle.setOpacity(1.0);
                        rectangle.setStyle("-fx-cursor: hand;");

                        if (Main.simulator.getBuildSubcategory() == Simulator.BuildSubcategory.TICKET_BOOTH
                                && Main.simulator.getBuildState() == Simulator.BuildState.DRAWING) {
                            Rectangle extraRectangle;

                            switch (GraphicsController.drawTicketBoothOrientation) {
                                case UP:
                                    if (rowCopy - 1 >= 0) {
                                        extraRectangle = rectangles[rowCopy - 1][columnCopy];
                                        extraRectangle.setOpacity(1.0);
                                        extraRectangle.setFill(Color.LIGHTGRAY);

                                        GraphicsController.extraPatch = Main.simulator.getCurrentFloor().getPatch(
                                                rowCopy - 1,
                                                columnCopy
                                        );

                                        GraphicsController.extraRectangle = extraRectangle;
                                        GraphicsController.validTicketBoothDraw
                                                = patch.getAmenity() == null && extraPatch.getAmenity() == null;
                                    } else {
                                        GraphicsController.validTicketBoothDraw = false;
                                    }

                                    break;
                                case RIGHT:
                                    if (columnCopy + 1 < Main.simulator.getCurrentFloor().getColumns()) {
                                        extraRectangle = rectangles[rowCopy][columnCopy + 1];
                                        extraRectangle.setOpacity(1.0);
                                        extraRectangle.setFill(Color.LIGHTGRAY);

                                        GraphicsController.extraPatch = Main.simulator.getCurrentFloor().getPatch(
                                                rowCopy,
                                                columnCopy + 1
                                        );

                                        GraphicsController.extraRectangle = extraRectangle;
                                        GraphicsController.validTicketBoothDraw
                                                = patch.getAmenity() == null && extraPatch.getAmenity() == null;
                                    } else {
                                        GraphicsController.validTicketBoothDraw = false;
                                    }

                                    break;
                                case DOWN:
                                    if (rowCopy + 1 < Main.simulator.getCurrentFloor().getRows()) {
                                        extraRectangle = rectangles[rowCopy + 1][columnCopy];
                                        extraRectangle.setOpacity(1.0);
                                        extraRectangle.setFill(Color.LIGHTGRAY);

                                        GraphicsController.extraPatch = Main.simulator.getCurrentFloor().getPatch(
                                                rowCopy + 1,
                                                columnCopy
                                        );

                                        GraphicsController.extraRectangle = extraRectangle;
                                        GraphicsController.validTicketBoothDraw
                                                = patch.getAmenity() == null && extraPatch.getAmenity() == null;
                                    } else {
                                        GraphicsController.validTicketBoothDraw = false;
                                    }

                                    break;
                                case LEFT:
                                    if (columnCopy - 1 >= 0) {
                                        extraRectangle = rectangles[rowCopy][columnCopy - 1];
                                        extraRectangle.setOpacity(1.0);
                                        extraRectangle.setFill(Color.LIGHTGRAY);

                                        GraphicsController.extraPatch = Main.simulator.getCurrentFloor().getPatch(
                                                rowCopy,
                                                columnCopy - 1
                                        );

                                        GraphicsController.extraRectangle = extraRectangle;
                                        GraphicsController.validTicketBoothDraw
                                                = patch.getAmenity() == null && extraPatch.getAmenity() == null;
                                    } else {
                                        GraphicsController.validTicketBoothDraw = false;
                                    }

                                    break;
                            }
                        }

                        // Create a tooltip to show the contents of this patch, if any
                        final StringBuilder tooltipText = new StringBuilder();

                        tooltipText
                                .append("Row ").append(patchRow).append(", column ").append(patchColumn).append("\n\n");

                        Amenity patchAmenity = patch.getAmenity();

                        if (patchAmenity == null) {
                            // There isn't an amenity on this patch
                            tooltipText.append("Empty patch");
                        } else {
                            // There is an amenity on this patch, so label accordingly
                            tooltipText.append(patchAmenity.toString());
                        }

                        final Tooltip tooltip = new Tooltip(tooltipText.toString());

                        Tooltip.install(rectangle, tooltip);
                    }
                });

                // Actions for when the mouse exits a listener
                rectangle.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
                    // This listener should only be active when there is a subcategory present
                    if (Main.simulator.getBuildSubcategory() != Simulator.BuildSubcategory.NONE) {
                        rectangle.setOpacity(0.0);
                        rectangle.setStyle("-fx-cursor: default;");

                        if (extraRectangle != null) {
                            extraRectangle.setOpacity(0.0);
                            extraRectangle.setFill(Color.DARKGRAY);
                        }
                    }
                });

                // Actions for when the mouse clicks a listener
                rectangle.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                    // This listener should only be active when there is a subcategory present
                    if (Main.simulator.getBuildSubcategory() != Simulator.BuildSubcategory.NONE) {
                        int patchRow;
                        int patchColumn;

                        patchRow = (int) rectangle.getProperties().get("row");
                        patchColumn = (int) rectangle.getProperties().get("column");

                        // Get the patch where the mouse is currently on
                        Patch currentPatch = Main.simulator.getCurrentFloor().getPatch(patchRow, patchColumn);

                        // Special case: if a ticket booth is currently being drawmn get the extra patch as well, assuming
                        // a valid ticket booth drawing spot
                        if (
                                GraphicsController.validTicketBoothDraw
                                        && Main.simulator.getBuildSubcategory() == Simulator.BuildSubcategory.TICKET_BOOTH
                                        && extraRectangle != null
                        ) {
                            int extraPatchRow = (int) GraphicsController.extraRectangle.getProperties().get("row");
                            int extraPatchColumn = (int) GraphicsController.extraRectangle.getProperties().get("column");

                            GraphicsController.extraPatch = Main.simulator.getCurrentFloor().getPatch(
                                    extraPatchRow,
                                    extraPatchColumn
                            );
                        }

                        // Set the amenity on the patch as the current amenity of the simulation
                        Main.simulator.setCurrentAmenity(currentPatch.getAmenity());

                        // Actions for left click
                        if (event.getButton() == MouseButton.PRIMARY) {
                            // Commence building or editing on that patch
                            try {
                                Main.mainScreenController.buildOrEdit(currentPatch);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            // Redraw the station view
                            drawStationView(canvases, floor, tileSize, true);
                        }
                    }
                });

                overlay.getChildren().add(rectangle);
            }
        }
    }
}
