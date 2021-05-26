package com.crowdsimulation.controller.graphics;

import com.crowdsimulation.controller.Controller;
import com.crowdsimulation.controller.Main;
import com.crowdsimulation.controller.graphics.amenity.footprint.AmenityFootprint;
import com.crowdsimulation.controller.graphics.amenity.graphic.AmenityGraphic;
import com.crowdsimulation.controller.graphics.amenity.graphic.GraphicLocation;
import com.crowdsimulation.controller.screen.feature.main.MainScreenController;
import com.crowdsimulation.model.core.agent.passenger.Passenger;
import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.headful.QueueingFloorField;
import com.crowdsimulation.model.core.environment.station.patch.location.Location;
import com.crowdsimulation.model.core.environment.station.patch.location.MatrixPosition;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Drawable;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.miscellaneous.Track;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.miscellaneous.Wall;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.Queueable;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.StationGate;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.TrainDoor;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.elevator.ElevatorPortal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.escalator.EscalatorPortal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.stairs.StairPortal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.TicketBooth;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable.Security;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable.Turnstile;
import com.crowdsimulation.model.simulator.Simulator;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GraphicsController extends Controller {
    private static final Image AMENITY_SPRITE_SHEET = new Image(AmenityGraphic.AMENITY_SPRITE_SHEET_URL);

    private static final int FLOOR_FIELD_COLOR_HUE = 115;
    private static final String TOOLTIP_TEMPLATE = "Row %r, column %c\n\n%p";

    public static Floor floorNextPortal;
    public static List<Amenity.AmenityBlock> firstPortalAmenityBlocks;

    public static AmenityFootprint currentAmenityFootprint;

    public static double tileSize;

    private static Patch markedPatch;
    private static boolean showTooltip;
    private static Tooltip tooltip;

    // Denotes whether the listeners for the drawing interface has already been drawn, in order to prevent its
    // double-drawing
    public static boolean listenersDrawn;

    static {
        GraphicsController.markedPatch = null;

        GraphicsController.showTooltip = false;
        GraphicsController.tooltip = new Tooltip(GraphicsController.TOOLTIP_TEMPLATE);

        GraphicsController.currentAmenityFootprint = null;

        GraphicsController.floorNextPortal = null;
        GraphicsController.firstPortalAmenityBlocks = null;
    }

    // Send a request to draw the station view on the canvas
    public static void requestDrawStationView(
            StackPane canvases,
            Floor floor,
            boolean background
    ) {
        javafx.application.Platform.runLater(() -> {
            // Tell the JavaFX thread that we'd like to draw on the canvas
            drawStationView(canvases, floor, background);
        });
    }

    // Send a request to draw part of the station view on the canvas
    public static void requestDrawPartialStationView(
            StackPane canvases,
            Floor floor,
            int rowStart,
            int columnStart,
            int rowEnd,
            int columnEnd,
            boolean background
    ) {
        javafx.application.Platform.runLater(() -> {
            // Tell the JavaFX thread that we'd like to draw on the canvas
            drawStationView(canvases, floor, rowStart, columnStart, rowEnd, columnEnd, background);
        });
    }

    // Send a request to draw the mouse listeners on top of the canvases
    public static void requestDrawListeners(StackPane canvases) {
        javafx.application.Platform.runLater(() -> {
            // Tell the JavaFX thread that we'd like to draw on the canvas
            drawListeners(canvases);
        });
    }

    // Draw all that is requested on the station view on the canvases
    private static void drawStationView(
            StackPane canvases,
            Floor floor,
            int rowStart,
            int columnStart,
            int rowEnd,
            int columnEnd,
            boolean background
    ) {
        // Get the canvases and their graphics contexts
        final Canvas backgroundCanvas = (Canvas) canvases.getChildren().get(0);
        final Canvas foregroundCanvas = (Canvas) canvases.getChildren().get(1);
        final Canvas markingsCanvas = (Canvas) canvases.getChildren().get(2);

        final GraphicsContext backgroundGraphicsContext = backgroundCanvas.getGraphicsContext2D();
        final GraphicsContext foregroundGraphicsContext = foregroundCanvas.getGraphicsContext2D();
        final GraphicsContext markingsGraphicsContext = markingsCanvas.getGraphicsContext2D();

        // Get the height and width of the canvases
        final double canvasWidth = backgroundCanvas.getWidth();
        final double canvasHeight = backgroundCanvas.getHeight();

        // Clear everything in the respective canvas
        if (!background) {
            foregroundGraphicsContext.clearRect(
                    rowStart * tileSize,
                    columnStart * tileSize,
                    rowEnd * tileSize,
                    columnEnd * tileSize
            );
        } else {
            backgroundGraphicsContext.clearRect(
                    rowStart * tileSize,
                    columnStart * tileSize,
                    rowEnd * tileSize,
                    columnEnd * tileSize
            );

            backgroundGraphicsContext.setFill(Color.WHITE);
            backgroundGraphicsContext.fillRect(
                    0,
                    0,
                    canvasWidth,
                    canvasHeight
            );
        }

        boolean drawGraphicTransparently;

        // Draw all the patches of this floor
        for (int row = rowStart; row < rowEnd; row++) {
            for (int column = columnStart; column < columnEnd; column++) {
                drawGraphicTransparently = false;

                // Get the current patch
                Patch currentPatch = floor.getPatch(row, column);

                // Draw only the background (the environment) if requested
                // Draw only the foreground (the passengers) if otherwise
                if (background) {
                    // Draw graphics corresponding to whatever is in the content of the patch
                    // If the patch has no amenity on it, just draw a blank patch
                    Amenity.AmenityBlock patchAmenityBlock = currentPatch.getAmenityBlock();
                    Color patchColor = null;
                    Image firstPortalImage = null;

                    boolean currentPatchInFirstPortalBlock = false;
                    Amenity.AmenityBlock firstPortalAmenityBlock = null;

                    if (patchAmenityBlock == null) {
                        // Check if the current patch is in the first portal position, if it exists
                        if (GraphicsController.firstPortalAmenityBlocks != null) {
                            for (Amenity.AmenityBlock amenityBlock : GraphicsController.firstPortalAmenityBlocks) {
                                if (amenityBlock.getPatch().getMatrixPosition().equals(currentPatch.getMatrixPosition())) {
                                    currentPatchInFirstPortalBlock = true;
                                    firstPortalAmenityBlock = amenityBlock;

                                    break;
                                }
                            }
                        }

                        // Draw the marker for first portal reference, if any has been drawn
                        if (!currentPatchInFirstPortalBlock) {
                            // There isn't an amenity on this patch, so just use the color corresponding to a blank
                            // patch
                            patchColor = Color.WHITE;

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
                            // Draw a de-saturated version of the first portal here
                            drawGraphicTransparently = true;

                            if (Main.simulator.getBuildSubcategory() == Simulator.BuildSubcategory.ESCALATOR) {
                                switch (GraphicsController.currentAmenityFootprint.getCurrentRotation().getOrientation()) {
                                    case UP:
                                        firstPortalImage
                                                = new Image(
                                                "com/crowdsimulation/view/image/amenity/escalator/single/blank/escalator_front.png"
                                        );

                                        break;
                                    case RIGHT:
                                        firstPortalImage
                                                = new Image(
                                                "com/crowdsimulation/view/image/amenity/escalator/single/blank/escalator_right.png"
                                        );

                                        break;
                                    case DOWN:
                                        firstPortalImage
                                                = new Image(
                                                "com/crowdsimulation/view/image/amenity/escalator/single/blank/escalator_rear.png"
                                        );

                                        break;
                                    case LEFT:
                                        firstPortalImage
                                                = new Image(
                                                "com/crowdsimulation/view/image/amenity/escalator/single/blank/escalator_left.png"
                                        );

                                        break;
                                }
                            } else if (Main.simulator.getBuildSubcategory() == Simulator.BuildSubcategory.STAIRS) {
                                switch (GraphicsController.currentAmenityFootprint.getCurrentRotation().getOrientation()) {
                                    case UP:
                                        firstPortalImage
                                                = new Image(
                                                "com/crowdsimulation/view/image/amenity/stair/single/front/stair_single_front.png"
                                        );

                                        break;
                                    case RIGHT:
                                        firstPortalImage
                                                = new Image(
                                                "com/crowdsimulation/view/image/amenity/stair/single/right/stair_single_right.png"
                                        );

                                        break;
                                    case DOWN:
                                        firstPortalImage
                                                = new Image(
                                                "com/crowdsimulation/view/image/amenity/stair/single/rear/stair_single_rear.png"
                                        );

                                        break;
                                    case LEFT:
                                        firstPortalImage
                                                = new Image(
                                                "com/crowdsimulation/view/image/amenity/stair/single/left/stair_single_left.png"
                                        );

                                        break;
                                }
                            } else if (Main.simulator.getBuildSubcategory() == Simulator.BuildSubcategory.ELEVATOR) {
                                switch (GraphicsController.currentAmenityFootprint.getCurrentRotation().getOrientation()) {
                                    case UP:
                                        firstPortalImage
                                                = new Image(
                                                "com/crowdsimulation/view/image/amenity/elevator/closed/front/elevator_closed_front.png"
                                        );

                                        break;
                                    case RIGHT:
                                        firstPortalImage
                                                = new Image(
                                                "com/crowdsimulation/view/image/amenity/elevator/closed/right/elevator_closed_right.png"
                                        );

                                        break;
                                    case DOWN:
                                        firstPortalImage
                                                = new Image(
                                                "com/crowdsimulation/view/image/amenity/elevator/closed/rear/elevator_closed_rear.png"
                                        );

                                        break;
                                    case LEFT:
                                        firstPortalImage
                                                = new Image(
                                                "com/crowdsimulation/view/image/amenity/elevator/closed/left/elevator_closed_left.png"
                                        );

                                        break;
                                }
                            }
                        }
                    } else {
                        Amenity patchAmenity = currentPatch.getAmenityBlock().getParent();

                        // There is an amenity on this patch, so draw it according to its corresponding color
                        // If floor field drawing is on, only color amenities which are of the current class
                        if (Main.simulator.isFloorFieldDrawing()) {
                            // Only color the current amenity - unsaturate the rest
                            if (!patchAmenity.equals(Main.simulator.getCurrentFloorFieldTarget())) {
                                drawGraphicTransparently = true;
                            }
                        }
                    }

                    // Draw the patch
                    if (patchAmenityBlock != null) {
                        Amenity patchAmenity = currentPatch.getAmenityBlock().getParent();

                        if (patchAmenityBlock.hasGraphic()) {
                            Drawable drawablePatchAmenity = (Drawable) patchAmenity;

                            // Add transparency if needed
                            if (drawGraphicTransparently) {
                                backgroundGraphicsContext.setGlobalAlpha(0.2);
                            }

                            GraphicLocation graphicLocation = drawablePatchAmenity.getGraphicLocation();

                            backgroundGraphicsContext.drawImage(
                                    AMENITY_SPRITE_SHEET,
                                    graphicLocation.getSourceX(),
                                    graphicLocation.getSourceY(),
                                    graphicLocation.getSourceWidth(),
                                    graphicLocation.getSourceHeight(),
                                    column * tileSize + drawablePatchAmenity.getGraphicObject()
                                            .getAmenityGraphicOffset().getColumnOffset() * tileSize,
                                    row * tileSize + drawablePatchAmenity.getGraphicObject()
                                            .getAmenityGraphicOffset().getRowOffset() * tileSize,
                                    tileSize * drawablePatchAmenity.getGraphicObject().getAmenityGraphicScale()
                                            .getColumnSpan(),
                                    tileSize * drawablePatchAmenity.getGraphicObject().getAmenityGraphicScale()
                                            .getRowSpan()
                            );

                            // Reset transparency if previously added
                            if (drawGraphicTransparently) {
                                backgroundGraphicsContext.setGlobalAlpha(1.0);
                            }
                        }
                    } else if (currentPatchInFirstPortalBlock) {
                        if (firstPortalAmenityBlock.hasGraphic()) {
                            Drawable drawablePatchAmenity = (Drawable) firstPortalAmenityBlock.getParent();

                            backgroundGraphicsContext.setGlobalAlpha(0.2);

                            backgroundGraphicsContext.drawImage(
                                    firstPortalImage,
                                    column * tileSize,
                                    row * tileSize,
                                    tileSize * drawablePatchAmenity.getGraphicObject().getAmenityGraphicScale()
                                            .getColumnSpan(),
                                    tileSize * drawablePatchAmenity.getGraphicObject().getAmenityGraphicScale()
                                            .getRowSpan()
                            );

                            backgroundGraphicsContext.setGlobalAlpha(1.0);
                        }
                    } else {
                        backgroundGraphicsContext.setFill(patchColor);
                        backgroundGraphicsContext.fillRect(column * tileSize, row * tileSize, tileSize, tileSize);
                    }
                } else {
                    // Draw passengers, if any
                    final double passengerDiameter = tileSize * 0.5;

                    for (Passenger passenger : floor.getPassengersInFloor()) {
                        // TODO: Draw passengers
                        foregroundGraphicsContext.fillOval(
                                passenger.getPassengerMovement().getPosition().getX()
                                        * tileSize - passengerDiameter * 0.5,
                                passenger.getPassengerMovement().getPosition().getY()
                                        * tileSize - passengerDiameter * 0.5,
                                passengerDiameter,
                                passengerDiameter
                        );
                    }
                }
            }
        }

        // If this amenity is also the currently selected amenity in the simulator, draw a circle around
        // said amenity
        final double CIRCLE_DIAMETER = 250.0;

        backgroundGraphicsContext.setStroke(Color.BLACK);
        backgroundGraphicsContext.setLineWidth(1.0);

        Amenity currentAmenity = Main.simulator.getCurrentAmenity();

        if (currentAmenity != null
                && currentAmenity.getAmenityBlocks().get(0).getPatch().getFloor() == Main.simulator.getCurrentFloor()
                && !Main.simulator.isFloorFieldDrawing()) {
            double row = currentAmenity.getAmenityBlocks().get(0).getPatch().getMatrixPosition().getRow();
            double column = currentAmenity.getAmenityBlocks().get(0).getPatch().getMatrixPosition().getColumn();

            backgroundGraphicsContext.strokeOval(
                    (column * tileSize - CIRCLE_DIAMETER * 0.5 + tileSize * 0.5),
                    (row * tileSize - CIRCLE_DIAMETER * 0.5 + tileSize * 0.5),
                    CIRCLE_DIAMETER,
                    CIRCLE_DIAMETER
            );
        }
    }

    // Draw all that is needed on the station view on the canvases
    private static void drawStationView(StackPane canvases, Floor floor, boolean background) {
        // Draw the entire station view
        drawStationView(
                canvases,
                floor,
                0,
                0,
                floor.getRows(),
                floor.getColumns(),
                background
        );
    }

    // Draw the mouse listeners over the canvases
    // These listeners allows the user to graphically interact with the station amenities
    private static void drawListeners(StackPane canvases) {
        // Get the background and markings canvases
        final Canvas backgroundCanvas = (Canvas) canvases.getChildren().get(0);
        final Canvas markingsCanvas = (Canvas) canvases.getChildren().get(2);

        // Draw listeners for the canvas (used for the detection of the orientation when drawing ticket booths)
        backgroundCanvas.getScene().setOnKeyPressed(e -> {
            Patch currentPatch = GraphicsController.markedPatch;

            switch (e.getCode()) {
                // Build mode-related shortcut keys
                case DIGIT1:
                case DIGIT2:
                case DIGIT3:
                    if (!Main.simulator.getRunning().get()
                            && !Main.simulator.isFloorFieldDrawing()
                            && !Main.simulator.isPortalDrawing()) {

                        if (e.getCode() == KeyCode.DIGIT1) {
                            // Draw mode
                            if (!Main.simulator.getRunning().get()
                                    && !Main.simulator.isFloorFieldDrawing()
                                    && !Main.simulator.isPortalDrawing()) {
                                MainScreenController.switchBuildMode(Simulator.BuildState.DRAWING);
                            }
                        } else if (e.getCode() == KeyCode.DIGIT2) {
                            // Edit one mode
                            if (!Main.simulator.getRunning().get()
                                    && !Main.simulator.isFloorFieldDrawing()
                                    && !Main.simulator.isPortalDrawing()) {
                                MainScreenController.switchBuildMode(Simulator.BuildState.EDITING_ONE);
                            }
                        } else if (e.getCode() == KeyCode.DIGIT3) {
                            // Edit all mode
                            if (!Main.simulator.getRunning().get()
                                    && !Main.simulator.isFloorFieldDrawing()
                                    && !Main.simulator.isPortalDrawing()) {
                                MainScreenController.switchBuildMode(Simulator.BuildState.EDITING_ALL);
                            }
                        }
                    }

                    // Every time the keyboard is pressed, update the markings as if the mouse were moved
                    if (currentPatch != null) {
                        updateMarkings(backgroundCanvas, markingsCanvas, currentPatch, true);
                    }

                    break;
                // Drawing rotation shortcut keys
                case X:
                case Z:
                    if (e.getCode() == KeyCode.X) {
                        // Rotate counterclockwise
                        if (Main.simulator.getBuildState() == Simulator.BuildState.DRAWING
                                && !Main.simulator.isPortalDrawing()
                                && !Main.simulator.isFloorFieldDrawing()) {
                            GraphicsController.currentAmenityFootprint.rotateCounterclockwise();
                        }
                    } else {
                        // Rotate clockwise
                        if (Main.simulator.getBuildState() == Simulator.BuildState.DRAWING
                                && !Main.simulator.isPortalDrawing()
                                && !Main.simulator.isFloorFieldDrawing()) {
                            GraphicsController.currentAmenityFootprint.rotateClockwise();
                        }
                    }

                    // Every time the keyboard is pressed, update the markings as if the mouse were moved
                    if (currentPatch != null) {
                        updateMarkings(backgroundCanvas, markingsCanvas, currentPatch, true);
                    }

                    break;
                // Edit one/all shortcut keys
                case ENTER:
                    // Save amenity
                    if (Main.simulator.getBuildState() != Simulator.BuildState.DRAWING
                            && !Main.simulator.isPortalDrawing()
                            && !Main.simulator.isFloorFieldDrawing()
                            && Main.simulator.getBuildCategory() != Simulator.BuildCategory.MISCELLANEOUS) {
                        if (Main.simulator.getBuildState() == Simulator.BuildState.EDITING_ONE
                                && Main.simulator.getCurrentAmenity() != null
                                || Main.simulator.getBuildState() == Simulator.BuildState.EDITING_ALL) {
                            if (Main.simulator.getBuildCategory() != Simulator.BuildCategory.STAIRS_AND_ELEVATORS) {
                                // Non-portal amenities
                                Main.mainScreenController.saveAmenityAction();
                            } else {
                                // Portal amenities
                                try {
                                    Main.mainScreenController.editPortalAction();
                                } catch (IOException ioException) {
                                    ioException.printStackTrace();
                                }
                            }
                        }
                    }

                    break;
                case DELETE:
                case BACK_SPACE:
                    // Delete amenity
                    if (Main.simulator.getBuildState() != Simulator.BuildState.DRAWING
                            && !Main.simulator.isPortalDrawing()
                            && !Main.simulator.isFloorFieldDrawing()) {
                        if (Main.simulator.getBuildState() == Simulator.BuildState.EDITING_ONE
                                && Main.simulator.getCurrentAmenity() != null
                                || Main.simulator.getBuildState() == Simulator.BuildState.EDITING_ALL) {
                            if (Main.simulator.getBuildCategory() != Simulator.BuildCategory.STAIRS_AND_ELEVATORS) {
                                // Non-portal amenities
                                Main.mainScreenController.deleteAmenityAction();
                            } else {
                                // Portal amenities
                                Main.mainScreenController.deletePortalAction();
                            }
                        }
                    }

                    break;
                // Add floor field shortcut keys
                case F:
                    if (
                            Main.simulator.getBuildState() == Simulator.BuildState.EDITING_ONE
                                    && !Main.simulator.isPortalDrawing()
                                    && !Main.simulator.isFloorFieldDrawing()
                                    && (Main.simulator.getBuildSubcategory() == Simulator.BuildSubcategory.SECURITY
                                    || Main.simulator.getBuildSubcategory() == Simulator.BuildSubcategory.TICKET_BOOTH
                                    || Main.simulator.getBuildSubcategory() == Simulator.BuildSubcategory.TURNSTILE
                                    || Main.simulator.getBuildSubcategory() == Simulator.BuildSubcategory.ELEVATOR
                                    || Main.simulator.getBuildSubcategory()
                                    == Simulator.BuildSubcategory.TRAIN_BOARDING_AREA
                            )
                    ) {
                        Main.mainScreenController.addFloorFieldsAction();
                    }

                    break;
            }
        });

        backgroundCanvas.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            // If there are no subcategories, erase all markings
            // Update the visual markings
            Patch currentPatch = retrievePatchFromMouseClick(event);

            if (currentPatch != null) {
                updateMarkings(backgroundCanvas, markingsCanvas, currentPatch, false);
            }
        });

        // Draw listeners for the drawing mechanisms
        backgroundCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            // If there are no subcategories, erase all markings
            if (Main.simulator.getBuildSubcategory() != Simulator.BuildSubcategory.NONE) {
                // Get the patch coordinates from the mouse click coordinates
                Patch currentPatch = retrievePatchFromMouseClick(event);

                // Set the amenity on the patch as the current amenity of the simulation, if within bounds
                if (currentPatch != null) {
                    Amenity.AmenityBlock currentAmenityBlock = currentPatch.getAmenityBlock();

                    if (currentAmenityBlock == null) {
                        Main.simulator.setCurrentAmenity(null);
                    } else {
                        Main.simulator.setCurrentAmenity(currentAmenityBlock.getParent());
                    }

                    // Actions for left click
                    if (event.getButton() == MouseButton.PRIMARY) {
                        // Commence building or editing on that patch
                        try {
                            Main.mainScreenController.buildOrEdit(currentPatch);

                            // Redraw the station view
                            drawStationView(canvases, Main.simulator.getCurrentFloor(), true);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        backgroundCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            // If there are no subcategories, erase all markings
            if (Main.simulator.getBuildSubcategory() != Simulator.BuildSubcategory.NONE) {
                // Only allow dragging when drawing obstacles or tracks
                if (
                        Main.simulator.getBuildSubcategory() == Simulator.BuildSubcategory.OBSTACLE
                                || Main.simulator.getBuildSubcategory() == Simulator.BuildSubcategory.TRAIN_TRACK
                ) {
                    // Get the patch coordinates from the mouse click coordinates
                    Patch currentPatch = retrievePatchFromMouseClick(event);

                    // Only proceed when the mouse is dragged within bounds
                    if (currentPatch != null) {
                        // Update the visual markings
                        updateMarkings(backgroundCanvas, markingsCanvas, currentPatch, false);

                        // Set the amenity on the patch as the current amenity of the simulation
                        Amenity.AmenityBlock currentAmenityBlock = currentPatch.getAmenityBlock();

                        if (currentAmenityBlock == null) {
                            Main.simulator.setCurrentAmenity(null);
                        } else {
                            Main.simulator.setCurrentAmenity(currentAmenityBlock.getParent());
                        }

                        // When dragging, only draw on patches without amenities on them
                        if (Main.simulator.getCurrentAmenity() == null) {
                            // Actions for left click
                            if (event.getButton() == MouseButton.PRIMARY) {
                                // Commence building or editing on that patch
                                try {
                                    Main.mainScreenController.buildOrEdit(currentPatch);

                                    drawStationView(canvases, Main.simulator.getCurrentFloor(), true);
                                } catch (IOException | InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        });

        backgroundCanvas.setOnScroll(event -> {
            // Use the scroll wheel to set the intensity value
            if (Main.simulator.isFloorFieldDrawing()) {
                final double zoomFactor = 0.00075;
                double newValue
                        = MainScreenController.normalFloorFieldController.getIntensity()
                        + event.getDeltaY() * zoomFactor;

                if (newValue < 0.1) {
                    newValue = 0.1;
                } else if (newValue > 1.0) {
                    newValue = 1.0;
                }

                MainScreenController.normalFloorFieldController.setIntensity(newValue);

                // Prevent the scroll wheel from also scrolling the view
                event.consume();
            }
        });
    }

    private static void updateMarkings(
            Canvas backgroundCanvas,
            Canvas markingsCanvas,
            Patch currentPatch,
            boolean force
    ) {
        // If there are no subcategories, erase all markings
        if (Main.simulator.getBuildSubcategory() != Simulator.BuildSubcategory.NONE) {
            // Show the tooltip if drawing is active
            if (!GraphicsController.showTooltip) {
                GraphicsController.showTooltip = true;

                Tooltip.install(backgroundCanvas, GraphicsController.tooltip);
            }

            if (GraphicsController.markedPatch == null) {
                // Update the tooltip content
                updateTooltip(currentPatch);

                // Light this patch up
                GraphicsController.markedPatch = currentPatch;

                markPatch(markingsCanvas, currentPatch);
            } else {
                // If the force parameter is set to true, update the markings whether the cursor has left the patch or
                // not - this is so a rotation made is visible by just a keypress (without needing to leave the current
                // patch like it otherwise does to avoid refreshing unnecessarily)
                if (force || GraphicsController.markedPatch != currentPatch) {
                    // Unmark the previously marked patch
                    unmarkPatch(markingsCanvas);

                    // Update the tooltip content
                    updateTooltip(currentPatch);

                    // Then mark this patch
                    GraphicsController.markedPatch = currentPatch;

                    markPatch(markingsCanvas, currentPatch);
                }
            }
        } else {
            // Erase marking
            if (GraphicsController.markedPatch != null) {
                unmarkPatch(markingsCanvas);

                GraphicsController.markedPatch = null;
            }

            // Erase tooltip
            GraphicsController.showTooltip = false;
            Tooltip.uninstall(backgroundCanvas, GraphicsController.tooltip);
        }
    }

    private static void updateTooltip(Patch currentPatch) {
        // Set the tooltip text
        String newText = GraphicsController.TOOLTIP_TEMPLATE;

        newText = newText.replace(
                "%r",
                String.valueOf(currentPatch.getMatrixPosition().getRow())
        );

        newText = newText.replace(
                "%c",
                String.valueOf(currentPatch.getMatrixPosition().getColumn())
        );

        Amenity.AmenityBlock amenityBlock = currentPatch.getAmenityBlock();

        // Show the amenity (or "empty patch", if there aren't any)
        if (amenityBlock == null) {
            newText = newText.replace("%p", "Empty patch");
        } else {
            Amenity amenity = amenityBlock.getParent();

            newText = newText.replace("%p", amenity.toString());
        }

        String finalNewText = newText;

        GraphicsController.tooltip.setText(finalNewText);
        GraphicsController.tooltip.setX(currentPatch.getPatchCenterCoordinates().getX() * tileSize);
        GraphicsController.tooltip.setY(currentPatch.getPatchCenterCoordinates().getY() * tileSize);
    }

    private static Patch retrievePatchFromMouseClick(MouseEvent event) {
        // Get the patch coordinates from the mouse click coordinates
        MatrixPosition matrixPosition = Location.screenCoordinatesToMatrixPosition(
                Main.simulator.getStation(),
                event.getX(),
                event.getY(),
                tileSize
        );

        // When the position given is a null, this means the mouse has been dragged out of bounds
        if (matrixPosition != null) {
            // Retrieve the patch at that location
            return Main.simulator.getCurrentFloor().getPatch(matrixPosition);
        } else {
            return null;
        }
    }

    private static void markPatch(Canvas markingsCanvas, Patch patch) {
        GraphicsContext graphicsContext = markingsCanvas.getGraphicsContext2D();

        Color markingColor = Color.hsb(0, 0, 0.25, 0.5);
        Color extraMarkingColor = Color.hsb(80, 1.0, 0.5, 0.5);
        Color strokeColor = Color.hsb(0, 0, 0.25, 0.1);

        // Draw the markings at that patch location
        int cursorRow = patch.getMatrixPosition().getRow();
        int cursorColumn = patch.getMatrixPosition().getColumn();

        if (Main.simulator.getBuildState() == Simulator.BuildState.DRAWING) {
            boolean isDrawingTrainTrack
                    = Main.simulator.getBuildSubcategory() == Simulator.BuildSubcategory.TRAIN_TRACK;

            // Only draw rectangles when not drawing train tracks
            if (!isDrawingTrainTrack) {
                // Draw the rectangles
                // Get the footprint corresponding to the current class and the current rotation
                if (GraphicsController.currentAmenityFootprint != null) {
                    // Draw rectangles in each offset in the footprint
                    for (AmenityFootprint.Rotation.AmenityBlockTemplate amenityBlockTemplate :
                            GraphicsController.currentAmenityFootprint.getCurrentRotation().getAmenityBlockTemplates()) {
                        // Compute for the position of the patch using the offset data
                        int patchRow = cursorRow + amenityBlockTemplate.getOffset().getRowOffset();
                        int patchColumn = cursorColumn + amenityBlockTemplate.getOffset().getColumnOffset();

                        // If there is an attractor on this patch, draw a different color
                        if (!amenityBlockTemplate.isAttractor()) {
                            graphicsContext.setFill(markingColor);
                        } else {
                            graphicsContext.setFill(extraMarkingColor);
                        }

                        // Draw the rectangle there
                        graphicsContext.fillRect(patchColumn * tileSize, patchRow * tileSize, tileSize, tileSize);
                    }
                }
            }

            // Draw the crosshairs
            graphicsContext.setStroke(strokeColor);

            // If there are train tracks being drawn, double the width of the strokes, and don't draw the vertical line
            // anymore
            if (!isDrawingTrainTrack) {
                graphicsContext.setLineWidth(tileSize);

                graphicsContext.strokeLine(
                        0,
                        cursorRow * tileSize + tileSize * 0.5,
                        markingsCanvas.getWidth(),
                        cursorRow * tileSize + tileSize * 0.5
                );
                graphicsContext.strokeLine(
                        cursorColumn * tileSize + tileSize * 0.5,
                        0,
                        cursorColumn * tileSize + tileSize * 0.5,
                        markingsCanvas.getHeight()
                );
            } else {
                graphicsContext.setLineWidth(tileSize * 2);

                graphicsContext.strokeLine(
                        0,
                        cursorRow * tileSize,
                        markingsCanvas.getWidth(),
                        cursorRow * tileSize
                );
            }
        } else {
            graphicsContext.fillRect(cursorColumn * tileSize, cursorRow * tileSize, tileSize, tileSize);
        }
    }

    private static void unmarkPatch(Canvas markingsCanvas) {
        GraphicsContext graphicsContext = markingsCanvas.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, markingsCanvas.getWidth(), markingsCanvas.getHeight());
    }

    public static void updateCurrentAmenityFootprint() {
        if (Main.simulator.getBuildSubcategory() == Simulator.BuildSubcategory.STATION_ENTRANCE_EXIT) {
            GraphicsController.currentAmenityFootprint = StationGate.stationGateFootprint;
        } else if (Main.simulator.getBuildSubcategory() == Simulator.BuildSubcategory.SECURITY) {
            GraphicsController.currentAmenityFootprint = Security.securityFootprint;
        } else if (Main.simulator.getBuildSubcategory() == Simulator.BuildSubcategory.TURNSTILE) {
            GraphicsController.currentAmenityFootprint = Turnstile.turnstileFootprint;
        } else if (Main.simulator.getBuildSubcategory() == Simulator.BuildSubcategory.TRAIN_BOARDING_AREA) {
            GraphicsController.currentAmenityFootprint = TrainDoor.trainDoorFootprint;
        } else if (Main.simulator.getBuildSubcategory() == Simulator.BuildSubcategory.TRAIN_TRACK) {
            GraphicsController.currentAmenityFootprint = Track.trackFootprint;
        } else if (Main.simulator.getBuildSubcategory() == Simulator.BuildSubcategory.TICKET_BOOTH) {
            GraphicsController.currentAmenityFootprint = TicketBooth.ticketBoothFootprint;
        } else if (Main.simulator.getBuildSubcategory() == Simulator.BuildSubcategory.STAIRS) {
            GraphicsController.currentAmenityFootprint = StairPortal.stairPortalFootprint;
        } else if (Main.simulator.getBuildSubcategory() == Simulator.BuildSubcategory.ESCALATOR) {
            GraphicsController.currentAmenityFootprint = EscalatorPortal.escalatorPortalFootprint;
        } else if (Main.simulator.getBuildSubcategory() == Simulator.BuildSubcategory.ELEVATOR) {
            GraphicsController.currentAmenityFootprint = ElevatorPortal.elevatorPortalFootprint;
        } else if (Main.simulator.getBuildSubcategory() == Simulator.BuildSubcategory.OBSTACLE) {
            GraphicsController.currentAmenityFootprint = Wall.wallFootprint;
        }
    }
}
