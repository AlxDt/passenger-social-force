package sample;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Controller {


    private final DrawState[] stateSequences = new DrawState[]{
            DrawState.START,
            DrawState.CHECKPOINT,
            DrawState.GOAL,
            DrawState.OBSTACLE,
//            DrawState.QUEUE_PATH,
//            DrawState.QUEUE_ENTRANCE
    };

    private boolean hasStarted;
    private DrawState drawState;
    private int sequence;
    private int index;
    private int modeIndex;

    @FXML
    private Canvas canvas;

    @FXML
    private ChoiceBox<String> drawChoiceBox;

    @FXML
    private Button startButton;

    @FXML
    private Button nextButton;

    @FXML
    private Button stepButton;

    @FXML
    private Text numAgentsText;

    @FXML
    private Pane overlay;

    @FXML
    private StackPane stackPane;

    @FXML
    private ToggleButton spawnButton;

    @FXML
    private ToggleButton trainDoorsOpenButton;

    private double tileSize;
    private GraphicsContext graphicsContext;
    private List<String[]> stringChoices;

    private int numAgents;

    public Controller() {
        this.hasStarted = false;
    }

    @FXML
    private void initialize() {
        tileSize = canvas.getWidth() / Main.WALKWAY.getCols();
        graphicsContext = canvas.getGraphicsContext2D();

        final String[] startItems = {"Start"};
        final String[] checkpointItems = {"Waypoint", "Gate"};
        final String[] goalItems = {"Exit"};
        final String[] obstacleItems = {"Obstacle"};

        stringChoices = new ArrayList<>();

        stringChoices.add(startItems);
        stringChoices.add(checkpointItems);
        stringChoices.add(goalItems);
        stringChoices.add(obstacleItems);

        this.sequence = -1;
        this.index = -1;
        this.modeIndex = 0;

        this.drawState = stateSequences[modeIndex];

        drawChoiceBox.setItems(FXCollections.observableArrayList(stringChoices.get(modeIndex)));
        drawChoiceBox.getSelectionModel().select(0);

        // Draw visible grid
//        graphicsContext.setFill(Color.TURQUOISE);
//        graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        drawInterface(graphicsContext, tileSize);
        drawListeners(startItems, tileSize);
    }

    @FXML
    private void step() {
        nextStep();

        drawInterface(graphicsContext, tileSize);
    }

    @FXML
    private void next() {
        nextMode();

        this.drawState = stateSequences[modeIndex];

        drawChoiceBox.setItems(FXCollections.observableArrayList(stringChoices.get(modeIndex)));
        drawChoiceBox.getSelectionModel().select(0);

        drawInterface(graphicsContext, tileSize);
    }

    @FXML
    private void play() throws InterruptedException {
        this.hasStarted = true;
        stackPane.getChildren().remove(overlay);

//        // Diffuse goals
//        Main.WALKWAY.diffuseGoals();

        // Take note of the passengers removed from the simulation
        List<Passenger> passengersRemoved = new ArrayList<>();

        // Start running
        // Prepare the random number generator
        Random rng = new Random();

        // 10% chance of passengers being generated
        final double CHANCE_PER_TICK = 0.1;

        new Thread(() -> {
            while (true) {
                // Make the starting patches randomly generate passengers
                // But only do it when there is no passenger at the start patch
                for (Patch start : Main.WALKWAY.getStarts()) {
                    if (spawnButton.isSelected() && rng.nextDouble() < CHANCE_PER_TICK) {
                        // Generate a passenger
                        Passenger passenger = new Passenger(
                                start.getPatchCenterCoordinates().getX(),
                                start.getPatchCenterCoordinates().getY(),
                                Main.WALKWAY.getNumGoals()
                        );

//                            // Position the newly created passenger in the coordinates of the start row and column
//                            Main.WALKWAY.positionPassenger(passenger, startRow, startColumn);

                        // Add the newly created passenger to the list of passengers
                        Main.WALKWAY.getPassengers().add(passenger);

                        // Increment the current number of passengers
                        this.numAgents++;
                    }
                }

                // Update the number of agents displayed in the interface
                updateNumAgents();

                // Make each passenger move towards the higher gradient
                for (Passenger passenger : Main.WALKWAY.getPassengers()) {
                    // Only move if the passenger is not waiting
                    if (!passenger.isWaiting()) {
                        double headingGoal;

                        // Set the nearest goal to this passenger
                        // This internally computes for the nearest goal to it
                        passenger.setNearestGoal();

                        // Take note of the heading towards the goal patch
                        // Get the x and y coordinates of the patch in question
                        // These coordinates should be in the center of the patch
                        double patchX = passenger.getGoal().getPatchCenterCoordinates().getX();
                        double patchY = passenger.getGoal().getPatchCenterCoordinates().getY();

                        headingGoal = passenger.headingTowards(patchX, patchY);

                        // Try to choose a leader if this passenger doesn't already have one
                        if (passenger.getLeader() == null) {
                            // Try to choose a leader
                            boolean leaderChosen = passenger.setLeader();

                            // If a leader has been chosen, take note of the heading to that leader
                            if (leaderChosen) {
                                Passenger leader = passenger.getLeader();

                                double headingLeader = passenger.headingTowards(
                                        leader.getPosition().getX(),
                                        leader.getPosition().getY()
                                );

                                // Set this passenger's final heading to the angular mean of the two headings
                                double meanHeading = Passenger.meanHeading(headingGoal, headingLeader);

                                // Add random perturbations for realistic movement
                                meanHeading += new Random().nextGaussian() * Math.toRadians(10);

                                passenger.setHeading(meanHeading);
                            } else {
                                // Add random perturbations for realistic movement
                                headingGoal += new Random().nextGaussian() * Math.toRadians(10);

                                // If a leader has not been chosen, continue moving solo
                                passenger.setHeading(headingGoal);
                            }
                        } else {
                            Passenger leader = passenger.getLeader();

                            double headingLeader = passenger.headingTowards(
                                    leader.getPosition().getX(),
                                    leader.getPosition().getY()
                            );

                            // Set this passenger's final heading to the angular mean of the two headings
                            double meanHeading = Passenger.meanHeading(headingGoal, headingLeader);

                            // Add random perturbations for realistic movement
                            meanHeading += new Random().nextGaussian() * Math.toRadians(10);

                            passenger.setHeading(meanHeading);
                        }

//                            // Choose the patch with the highest gradient
//                            Patch chosenPatch = passenger.choosePatch(
//                                    Main.WALKWAY.getRows(),
//                                    Main.WALKWAY.getCols(),
//                                    false
//                            );
//
//                            // Take note of the heading towards the patch with the highest gradient
//                            headingBestPatch = passenger.headingTowards(chosenPatch);

                        // Set the heading to the mean of the above headings
//                            double meanHeading = Passenger.meanHeading(headingGoal, headingBestPatch);

//                            passenger.setHeading(headingGoal);

                        //

                        // Make this passenger move, if allowable
                        if (passenger.shouldMove(1.5)) {
                            passenger.move();
                        } else {
                            passenger.setHeading(passenger.getHeading() + new Random().nextGaussian() * Math.toRadians(70));
                        }
                    }

                    // Every movement, check if the leader, if any, is still ahead
                    if (passenger.getLeader() != null
                            && !passenger.isWithinFieldOfView(passenger.getLeader(), Math.toRadians(20.0))) {
                        // If not, remove it as a leader
                        passenger.clearLeader();
                    }

                    // Check if the passenger is at its goal
                    if (Main.WALKWAY.checkGoal(passenger)) {
                        // Check if the goal the passenger is on allows this passenger to pass
                        if (Main.WALKWAY.checkPass(passenger, trainDoorsOpenButton.isSelected())) {
                            // If it is, increment its goals reached counter
                            passenger.reachGoal();

                            // If it has no more goals left, this passenger should be removed
                            if (passenger.getGoalsLeft() == 0) {
                                passengersRemoved.add(passenger);
                            }

                            // Allow the passenger to move again
                            passenger.setWaiting(false);
                        } else {
                            // Do not allow the passenger to move
                            passenger.setWaiting(true);
                        }
                    }
                }

                // Remove all passengers in goals
                for (Passenger removedPassenger : passengersRemoved) {
//                        // Remove the passenger from the world
//                        Main.WALKWAY.removePassenger(passenger);

                    // Remove the passenger from its current patch
                    removedPassenger.getCurrentPatch().getPassengers().remove(removedPassenger);

                    // Remove the passenger from the passengers list
                    Main.WALKWAY.getPassengers().remove(removedPassenger);

                    // Passengers whose leader is this removed passenger should also have their references to that
                    // leader cleared
                    for (Passenger passenger : Main.WALKWAY.getPassengers()) {
                        if (passenger.getLeader() == removedPassenger) {
                            passenger.clearLeader();
                        }
                    }

                    this.numAgents--;
                }

                // Clear the list of passengers to be removed, as they have already been removed
                passengersRemoved.clear();

                // Update the number of agents displayed in the interface
                updateNumAgents();

                // Print the region
//                    Main.WALKWAY.printRegion(0);
                drawInterface(graphicsContext, tileSize);

                try {
                    Thread.sleep(Main.DELAY_IN_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

//                    System.out.println("clear");
            }
        }).start();
    }

    private void updateNumAgents() {
        Platform.runLater(() -> this.numAgentsText.setText(this.numAgents + " agents"));
    }

    private void drawListeners(String[] items, double tileSize) {
        for (int row = 0; row < Main.WALKWAY.getRows(); row++) {
            for (int column = 0; column < Main.WALKWAY.getCols(); column++) {
                Rectangle rectangle = new Rectangle(column * tileSize, row * tileSize, tileSize, tileSize);
                rectangle.setFill(Color.DARKGRAY);
                rectangle.setOpacity(0.0);
                rectangle.setStyle("-fx-cursor: hand;");

                rectangle.getProperties().put("row", row);
                rectangle.getProperties().put("column", column);

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
                    int patchColumn = (int) rectangle.getProperties().get("column");

                    String choice = drawChoiceBox.getSelectionModel().getSelectedItem();

                    switch (drawState) {
                        case START:
                            if (choice.equals("Start")) {
                                Main.WALKWAY.setStatus(patchRow, patchColumn, Patch.Status.START, this.sequence);
                            }

                            break;
                        case CHECKPOINT:
                            switch (choice) {
                                case "Waypoint":
                                    Main.WALKWAY.setStatus(patchRow, patchColumn, Patch.Status.WAYPOINT, this.sequence);

                                    break;
                                case "Gate":
                                    Main.WALKWAY.setStatus(patchRow, patchColumn, Patch.Status.GATE, this.sequence);

                                    break;
                            }

                            break;
                        case GOAL:
                            if (choice.equals("Exit")) {
                                Main.WALKWAY.setStatus(patchRow, patchColumn, Patch.Status.EXIT, this.sequence);
                            }
                        case OBSTACLE:
                            if (choice.equals("Obstacle")) {
                                Main.WALKWAY.setStatus(patchRow, patchColumn, Patch.Status.OBSTACLE, this.sequence);
                            }
                    }

                    // Redraw grid
                    drawInterface(graphicsContext, tileSize);
                });

                overlay.getChildren().add(rectangle);
            }
        }
    }

    private void drawInterface(GraphicsContext graphicsContext, double tileSize) {
        Platform.runLater(() -> {
            graphicsContext.setFill(Color.WHITE);

            for (int row = 0; row < Main.WALKWAY.getRows(); row++) {
                for (int column = 0; column < Main.WALKWAY.getCols(); column++) {
                    switch (Main.WALKWAY.getPatch(row, column).getStatus()) {
                        case CLEAR:
                            graphicsContext.setFill(Color.WHITE);

                            break;
                        case START:
                            graphicsContext.setFill(Color.BLUE);

                            break;
                        case WAYPOINT:
                            graphicsContext.setFill(Color.GRAY);

                            break;
                        case GATE:
                            graphicsContext.setFill(Color.GREEN);

                            break;
                        case EXIT:
                            graphicsContext.setFill(Color.YELLOW);

                            break;
                        case OBSTACLE:
                            graphicsContext.setFill(Color.BLACK);

                            break;
                    }

                    graphicsContext.fillRect(column * tileSize, row * tileSize, tileSize, tileSize);
                }
            }

            // Draw passengers, if any
            final double passengerRadius = 10;

            for (Passenger passenger : Main.WALKWAY.getPassengers()) {
                graphicsContext.setFill(passenger.getColor());

                graphicsContext.fillOval(
                        passenger.getPosition().getX() * tileSize - passengerRadius / 2.0
                        /*- passengerRadius / 2.0*/,
                        passenger.getPosition().getY() * tileSize - passengerRadius / 2.0
                        /*- passengerRadius / 2.0*/, 10, 10
                );
//            System.out.println(passenger.getX() + ", " + passenger.getY());
            }

            // Check whether it is ready to go to the next step or mode
            if (modeIndex == 0) {
                stepButton.setDisable(true);
                nextButton.setDisable(Main.WALKWAY.getStarts().size() == 0);
            } else if (modeIndex == 1) {
                stepButton.setDisable(Main.WALKWAY.getGoals().size() == 0/* || Main.region.getGoals().get(Main.region.getGoals().size() - 1).size() == 0*/);
                nextButton.setDisable(Main.WALKWAY.getGoals().size() == 0/* || Main.region.getGoals().get(Main.region.getGoals().size() - 1).size() == 0*/);
            } else if (modeIndex == 2) {
                stepButton.setDisable(true);
                nextButton.setDisable(Main.WALKWAY.getGoals().get(Main.WALKWAY.getGoals().size() - 1).size() == 0 || hasStarted);
            } else {
                stepButton.setDisable(true);
                nextButton.setDisable(true);
            }

            // Check whether it is ready to start
            if (Main.WALKWAY.getGoals().size() == 0) {
                startButton.setDisable(true);
            } else {
                startButton.setDisable(Main.WALKWAY.getGoals().get(Main.WALKWAY.getGoals().size() - 1).size() == 0 || hasStarted);
            }

            spawnButton.setDisable(!hasStarted);
            trainDoorsOpenButton.setDisable(!hasStarted);
            drawChoiceBox.setDisable(hasStarted);
        });
    }

    private void nextMode() {
        nextStep();

        this.modeIndex++;
    }

    private void nextStep() {
        this.sequence++;
        this.index = 0;
    }

    public enum DrawState {
        START,
        CHECKPOINT,
        QUEUE_ENTRANCE,
        QUEUE_PATH,
        GOAL,
        OBSTACLE
    }
}
