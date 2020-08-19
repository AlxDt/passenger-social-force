package sample;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Controller {
    private final Rectangle[][] rectangles = new Rectangle[Main.region.getRows()][Main.region.getCols()];
    private final List<Passenger> passengers = new ArrayList<>();
    private boolean hasStarted;
    private DrawState drawState;

    @FXML
    private Canvas canvas;

    @FXML
    private ChoiceBox<String> drawChoiceBox;

    @FXML
    private Button startButton;

    @FXML
    private Pane overlay;

    @FXML
    private StackPane stackPane;

    private double tileSize;
    private GraphicsContext graphicsContext;

    public Controller() {
        this.hasStarted = false;
    }

    @FXML
    private void initialize() {
        tileSize = canvas.getWidth() / Main.region.getCols();
        graphicsContext = canvas.getGraphicsContext2D();

        // Set choice box goals
        final String[] goals = {"Clear", "Start", "Goal", "Obstacle"};
//        final String[] startItems = {"Start", "Clear"};
//        final String[] goalItems = {"Waypoint", "Queue", "Exit", "Clear"};
//        final String[] obstacleItems = {"Obstacle", "Clear"};

//        drawChoiceBox.setItems(FXCollections.observableArrayList(startItems));
        drawChoiceBox.setItems(FXCollections.observableArrayList(goals));
        drawChoiceBox.getSelectionModel().select(1);

        // Draw visible grid
//        graphicsContext.setFill(Color.TURQUOISE);
//        graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        drawGrid(graphicsContext, tileSize);

        // Draw listeners
//        drawListeners(goalItems, tileSize);
        drawListeners(goals, tileSize);

//        for (int col = 0; col < Main.region.getCols(); col++) {
//            graphicsContext.strokeLine(
//                    col * tileSize,
//                    0,
//                    col * tileSize,
//                    canvas.getHeight()
//            );
//        }
//
//        for (int row = 0; row < Main.region.getRows(); row++) {
//            graphicsContext.strokeLine(
//                    0,
//                    row * tileSize,
//                    canvas.getWidth(),
//                    row * tileSize
//            );
//        }
    }

    @FXML
    private void play() throws InterruptedException {
        this.hasStarted = true;
        stackPane.getChildren().remove(overlay);

        // Diffuse goals
        Main.region.diffuseGoals();

        // Start running
        List<Passenger> passengersRemoved = new ArrayList<>();

        Random rng = new Random();

        final double CHANCE_PER_TICK = 0.1;

        Platform.runLater(() -> {
            new Thread(() -> {
                // Get start rows and cols
                int startRow = Main.region.getStart().getMatrixPosition().getRow();
                int startCol = Main.region.getStart().getMatrixPosition().getCol();

                while (true) {
                    // Make the starting patches randomly generate passengers
                    // But only do it when there is no passenger at the start patch
                    if (rng.nextDouble() < CHANCE_PER_TICK/* && passengers.size() == 0*/
                            && Main.region.getPatch(startRow, startCol).getPassenger() == null) {
                        Passenger passenger = new Passenger(startCol, startRow, Main.region.getNumGoals());
//                        passenger.prepareToNextGoal(startRow, startCol);

                        Main.region.positionPassenger(passenger, startRow, startCol);
                        passengers.add(passenger);
                    }

                    // Make each passenger move towards the higher gradient
                    for (Passenger passenger : passengers) {
                        Patch chosenPatch = passenger.choosePatch(Main.region.getRows(), Main.region.getCols(),
                                false);

                        // If there are available patches to move to
                        if (chosenPatch != null) {
                            // If the next patch has a passenger on it, choose another patch randomly
                            // But only do this within a limited amount of attempts
                            int attempts = 3;

                            while (attempts > 0) {
                                // Check whether there is an available patch to move to
                                if (chosenPatch != null) {
                                    // Check whether there are passengers on that patch
                                    if (chosenPatch.getPassenger() != null) {
                                        // If there are, choose another patch and try again
                                        chosenPatch = passenger.choosePatch(Main.region.getRows(), Main.region.getCols(),
                                                true);
                                    } else {
                                        // If the patch is free, then use go to that patch
                                        break;
                                    }
                                } else {
                                    // If there isn't any, try another patch
                                    chosenPatch = passenger.choosePatch(Main.region.getRows(), Main.region.getCols(),
                                            true);
                                }

                                attempts--;
                            }

                            // If all attempts are used to no avail, just don't move at all
                            if (attempts == 0) {
                                continue;
                            }

//                        Patch chosenPatch = passenger.choosePatch();

                            if (chosenPatch == null) {
                                System.out.println(attempts);
                            }

                            // Move to chosen patch
                            Main.region.movePassenger(
                                    passenger,
                                    chosenPatch.getMatrixPosition().getRow(),
                                    chosenPatch.getMatrixPosition().getCol()
                            );

                            // Check if the passenger is at its goal
                            if (Main.region.checkGoal(passenger)) {
                                // If it is, increment its goals reached counter
                                passenger.reachGoal();

                                // If it has no more goals left, remove the passenger
                                if (passenger.getGoalsLeft() == 0) {
                                    passengersRemoved.add(passenger);
                                }/* else {
                                // If there still are goals, plan towards it again
                                passenger.prepareToNextGoal(
                                        chosenPatch.getMatrixPosition().getRow(),
                                        chosenPatch.getMatrixPosition().getCol()
                                );
                            }*/
                            }
                        }
                    }

                    // Remove all passengers in goal
                    for (Passenger passenger : passengersRemoved) {
                        // Remove the passenger from the world
                        Main.region.removePassenger(passenger);

                        // Remove the passenger from the passengers list
                        passengers.remove(passenger);
                    }

                    passengersRemoved.clear();

                    // Print the region
//                    Main.region.printRegion(true, 0);
                    drawGrid(graphicsContext, tileSize);

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

//                    System.out.println("clear");
                }
            }).start();
        });
    }

    private void drawListeners(String[] items, double tileSize) {
        for (int row = 0; row < Main.region.getRows(); row++) {
            for (int col = 0; col < Main.region.getCols(); col++) {
                Rectangle rectangle = new Rectangle(col * tileSize, row * tileSize, tileSize, tileSize);
                rectangle.setFill(Color.DARKGRAY);
                rectangle.setOpacity(0.0);
                rectangle.setStyle("-fx-cursor: hand;");

                rectangle.getProperties().put("row", row);
                rectangle.getProperties().put("col", col);

                rectangle.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> {
                    FadeTransition fadeTransition = new FadeTransition(Duration.millis(100), rectangle);
                    fadeTransition.setFromValue(0.0);
                    fadeTransition.setToValue(0.5);
                    fadeTransition.play();
                });

                rectangle.addEventFilter(MouseEvent.MOUSE_EXITED, e -> {
                    FadeTransition fadeTransition = new FadeTransition(Duration.millis(100), rectangle);
                    fadeTransition.setFromValue(0.5);
                    fadeTransition.setToValue(0.0);
                    fadeTransition.play();
                });

                rectangle.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
                    int patchRow = (int) rectangle.getProperties().get("row");
                    int patchCol = (int) rectangle.getProperties().get("col");

                    String choice = drawChoiceBox.getSelectionModel().getSelectedItem();

//                    switch (drawState) {
//                        case START:
//                            switch (choice) {
//                                case "Start":
//                                    Main.region.setStatus(patchRow, patchCol, Patch.Status.START);
//
//                                    break;
//                                case "Clear":
//                                    Main.region.setStatus(patchRow, patchCol, Patch.Status.CLEAR);
//
//                                    break;
//                            }
//
//                            break;
//                        case GOAL:
//                            switch (choice) {
//                                case "Waypoint":
//                                    Main.region.setStatus(patchRow, patchCol, Patch.Status.START);
//
//                                    break;
//                                case "Clear":
//                                    Main.region.setStatus(patchRow, patchCol, Patch.Status.CLEAR);
//
//                                    break;
//                            }
//
//                            break;
//                        case OBSTACLE:
//                            break;
//                    }

                    // Clear
                    if (choice.equals(items[0])) {
                        Main.region.setStatus(patchRow, patchCol, Patch.Status.CLEAR);
                    } else if (choice.equals(items[1])) {
                        // Start
                        Main.region.setStatus(patchRow, patchCol, Patch.Status.START);
                    } else if (choice.equals(items[2])) {
                        // Goal
                        Main.region.setStatus(patchRow, patchCol, Patch.Status.GOAL);
                    } else if (choice.equals(items[3])) {
                        // Obstacle
                        Main.region.setStatus(patchRow, patchCol, Patch.Status.OBSTACLE);
                    }

                    // Redraw grid
                    drawGrid(graphicsContext, tileSize);
                });

                overlay.getChildren().add(rectangle);
            }
        }
    }

    private void drawGrid(GraphicsContext graphicsContext, double tileSize) {
        graphicsContext.setFill(Color.WHITE);

        for (int row = 0; row < Main.region.getRows(); row++) {
            for (int col = 0; col < Main.region.getCols(); col++) {
                switch (Main.region.getPatch(row, col).getStatus()) {
                    case CLEAR:
//                        if ((row + col) % 2 == 0) {
                        graphicsContext.setFill(Color.WHITE);
//                        } else {
//                            graphicsContext.setFill(Color.GRAY);
//                        }

                        break;
                    case START:
                        graphicsContext.setFill(Color.BLUE);

                        break;
                    case GOAL:
                        graphicsContext.setFill(Color.YELLOW);

                        break;
                    case OBSTACLE:
                        graphicsContext.setFill(Color.BLACK);

                        break;
                }

                graphicsContext.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);
            }
        }

        // Draw passengers, if any
        final double passengerRadius = 10;

        for (Passenger passenger : passengers) {
            graphicsContext.setFill(passenger.getColor());

            graphicsContext.fillOval(
                    passenger.getX() * tileSize + tileSize / 2.0 - passengerRadius / 2.0,
                    passenger.getY() * tileSize + tileSize / 2.0 - passengerRadius / 2.0, 10, 10
            );
//            System.out.println(passenger.getX() + ", " + passenger.getY());
        }

        // Check whether it is ready to start
        startButton.setDisable(Main.region.getStart() == null || Main.region.getNumGoals() <= 0 || hasStarted);
    }

    public enum DrawState {
        START,
        GOAL,
        OBSTACLE
    }
}
