package com.crowdsimulation.controller.graphics;

import com.crowdsimulation.controller.Controller;
import com.crowdsimulation.controller.Main;
import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.obstacle.TicketBooth;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.obstacle.Wall;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.StationGate;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.TrainDoor;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.Elevator;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.EscalatorPortal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.StairPortal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.Security;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.TicketBoothTransactionArea;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.Turnstile;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;

public class GraphicsController extends Controller {
    private static final HashMap<Class<?>, Color> PATCH_COLORS = new HashMap<>();

    static {
        // The designated colors of the patch amenities (or lack thereof)
        PATCH_COLORS.put(null, Color.WHITE); // Empty patch

        PATCH_COLORS.put(StationGate.class, Color.BLUE); // Station gate
        PATCH_COLORS.put(TrainDoor.class, Color.RED); // Train door

        PATCH_COLORS.put(StairPortal.class, Color.VIOLET); // Stairs
        PATCH_COLORS.put(EscalatorPortal.class, Color.DARKVIOLET); // Escalator
        PATCH_COLORS.put(Elevator.class, Color.DEEPPINK); // Elevator

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
                Patch patch = Main.simulator.getCurrentFloor().getPatch(row, column);

                // Draw only the background (the environment) if requested
                // Draw only the foreground (the passengers) if otherwise
                if (background) {
                    // Draw graphics corresponding to whatever is in the content of the patch
                    // If the patch has no amenity on it, just draw a blank patch
                    Amenity patchAmenity = patch.getAmenity();
                    Color amenityColor;

                    if (patchAmenity == null) {
                        // TODO: Consider floor fields
                        // There isn't an amenity on this patch, so just use the color corresponding to a blank patch
                        amenityColor = PATCH_COLORS.get(null);
                    } else {
                        // There is an amenity on this patch, so draw it according to its corresponding color
                        amenityColor = PATCH_COLORS.get(patchAmenity.getClass());
                    }

                    // Set the color
                    backgroundGraphicsContext.setFill(amenityColor);

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
    }

    // Draw the mouse listeners over the canvases
    // These listeners allows the user to graphically interact with the station amenities
    private static void drawListeners(StackPane canvases, Pane overlay, Floor floor, double tileSize) {
        // Get the background canvas
        final Canvas backgroundCanvas = (Canvas) canvases.getChildren().get(0);

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
                rectangle.setStyle("-fx-cursor: hand;");
                rectangle.getProperties().put("row", row);
                rectangle.getProperties().put("column", column);

                final int columnCopy = column;
                final int rowCopy = row;

                // Actions for when the mouse enters a listener
                rectangle.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> {
                    rectangle.setOpacity(1.0);

                    int patchRow;
                    int patchColumn;

                    patchRow = (int) rectangle.getProperties().get("row");
                    patchColumn = (int) rectangle.getProperties().get("column");

                    // Get the patch where the mouse is currently on
                    Patch patch = Main.simulator.getCurrentFloor().getPatch(patchRow, patchColumn);

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
                });

                // Actions for when the mouse exits a listener
                rectangle.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
                    rectangle.setOpacity(0.0);
                });

                // Actions for when the mouse clicks a listener
                rectangle.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                    int patchRow;
                    int patchColumn;

                    patchRow = (int) rectangle.getProperties().get("row");
                    patchColumn = (int) rectangle.getProperties().get("column");

                    // Get the patch where the mouse is currently on
                    Patch currentPatch = Main.simulator.getCurrentFloor().getPatch(patchRow, patchColumn);

                    // Set the amenity on the patch as the current amenity of the simulation
                    Main.simulator.getCurrentAmenity().set(currentPatch.getAmenity());

                    // Then get the amenity which may be in that patch
                    Amenity patchAmenity = currentPatch.getAmenity();

                    // Actions for left click
                    if (event.getButton() == MouseButton.PRIMARY) {
                        // Commence building or editing on that patch
                        Main.mainScreenController.buildOrEdit(currentPatch);

                        // Redraw the station view
                        drawStationView(canvases, floor, tileSize, true);
                    } else if (event.getButton() == MouseButton.SECONDARY) {
                        // Actions for right click
                        // TODO: If there is no amenity in that patch, check if there is a floor field on it (depending on
                        // what is selected in the floor field menu)
                        if (patchAmenity == null) {

                        } else {
                            // If there is an amenity in that patch, show the context menu to delete it
                            ContextMenu deleteContextMenu = new ContextMenu();

                            MenuItem menuItem = new MenuItem("Delete");
                            menuItem.setOnAction(event2 -> {
                                // Delete amenity on this patch
                                Main.mainScreenController.deleteStationGateAction();
                            });

                            deleteContextMenu.getItems().add(menuItem);

                            rectangle.setOnContextMenuRequested(event2 -> {
                                deleteContextMenu.show(
                                        rectangle,
                                        columnCopy * tileSize + 5 * tileSize,
                                        rowCopy * tileSize + 10 * tileSize
                                );
                            });
                        }
                    }
                });

                overlay.getChildren().add(rectangle);
            }
        }
    }
}
