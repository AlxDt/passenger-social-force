package com.crowdsimulation.controller.screen.main;

import com.crowdsimulation.controller.Main;
import com.crowdsimulation.controller.graphics.GraphicsController;
import com.crowdsimulation.controller.screen.ScreenController;
import com.crowdsimulation.controller.screen.alert.AlertController;
import com.crowdsimulation.controller.screen.main.service.UIInitializeService;
import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.Station;
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
import com.crowdsimulation.model.simulator.Simulator;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Optional;

public class MainScreenController extends ScreenController {
    // Operational variables
    @FXML
    private TabPane operationTabPane;

    @FXML
    private TabPane buildTabPane;

    @FXML
    private StackPane interfaceStackPane;

    // Canvas variables
    @FXML
    private Canvas backgroundCanvas;

    @FXML
    private Canvas foregroundCanvas;

    @FXML
    private Pane overlay;

    // Save tab variables
    @FXML
    private Button loadStationButton;

    // Build tab variables
    @FXML
    private ChoiceBox<Simulator.BuildState> buildModeChoiceBox;

    @FXML
    private Label buildModeLabel;

    // Entrances and exits
    // Station entrance/exit
    @FXML
    private CheckBox stationGateEnableCheckBox;

    @FXML
    private Label stationGateModeLabel;

    @FXML
    private ChoiceBox<StationGate.StationGateMode> stationGateModeChoiceBox;

    @FXML
    private Label stationGateSpawnLabel;

    @FXML
    private Spinner<Integer> stationGateSpawnSpinner;

    @FXML
    private Button saveStationGateButton;

    @FXML
    private Button deleteStationGateButton;

    // Security
    @FXML
    private CheckBox securityEnableCheckBox;

    @FXML
    private CheckBox securityBlockPassengerCheckBox;

    @FXML
    private Label securityIntervalLabel;

    @FXML
    private Spinner<Integer> securityIntervalSpinner;

    @FXML
    private Button saveSecurityButton;

    @FXML
    private Button deleteSecurityButton;

    // Stairs and elevators
    // Stairs

    // Escalator

    // Elevator

    // Concourse amenities
    // Ticket booth
    @FXML
    private CheckBox ticketBoothEnableCheckBox;

    @FXML
    private Label ticketBoothModeLabel;

    @FXML
    private ChoiceBox<TicketBoothTransactionArea.TicketBoothType> ticketBoothModeChoiceBox;

    @FXML
    private Label ticketBoothIntervalLabel;

    @FXML
    private Spinner<Integer> ticketBoothIntervalSpinner;

    @FXML
    private Button saveTicketBoothButton;

    @FXML
    private Button deleteTicketBoothButton;

    // Turnstile
    @FXML
    private CheckBox turnstileEnableCheckBox;

    @FXML
    private CheckBox turnstileBlockPassengerCheckBox;

    @FXML
    private Label turnstileDirectionLabel;

    @FXML
    private ChoiceBox<Turnstile.TurnstileMode> turnstileDirectionChoiceBox;

    @FXML
    private Label turnstileIntervalLabel;

    @FXML
    private Spinner<Integer> turnstileIntervalSpinner;

    @FXML
    private Button saveTurnstileButton;

    @FXML
    private Button deleteTurnstileButton;

    // Platform amenities
    // Train boarding area
    @FXML
    private CheckBox trainDoorEnableCheckBox;

    @FXML
    private Label trainDoorDirectionLabel;

    @FXML
    private ChoiceBox<TrainDoor.TrainDoorPlatform> trainDoorDirectionChoiceBox;

    @FXML
    private Label trainDoorCarriageLabel;

    @FXML
    // TODO: Retrieve carriages from database
    private ListView<TrainDoor.TrainDoorCarriage> trainDoorCarriageListView;

    @FXML
    private Button saveTrainDoorButton;

    @FXML
    private Button deleteTrainDoorButton;

    // Floor fields
    // Queueing floor field

    // Walls
    // Wall

    // Test tab variables
    // Simulation controls
    @FXML
    private ToggleButton playButton;

    // Passenger controls
    // Platform controls

    // Top bar
    // Top bar text prompt
    @FXML
    private Text stationNameText;

    @FXML
    private Button floorBelowButton;

    @FXML
    private Text floorNumberText;

    @FXML
    private Button floorAboveButton;

    @FXML
    private Text promptText;

    // Binding variables
    public static BooleanBinding SAVE_DELETE_BINDING;
    public static BooleanBinding SPECIFIC_CONTROLS_BINDING;

    static {
        boolean evaluateClassEquality = Main.simulator.getCurrentAmenity().isNotNull().get();

        MainScreenController.SAVE_DELETE_BINDING =
                Bindings.or(
                        Bindings.equal(
                                Main.simulator.getBuildState(),
                                Simulator.BuildState.DRAWING
                        ),
                        Bindings.and(
                                Bindings.or(
                                        Bindings.isNull(
                                                Main.simulator.getCurrentAmenity()
                                        ),
                                        (evaluateClassEquality) ?
                                                Bindings.equal(
                                                        Main.simulator.getCurrentAmenity().get().getClass(),
                                                        Main.simulator.getCurrentClass()
                                                ) :
                                                Bindings.createBooleanBinding(() -> false)
                                ),
                                Bindings.notEqual(
                                        Main.simulator.getBuildState(),
                                        Simulator.BuildState.EDITING_ALL
                                )
                        )
                );

        MainScreenController.SPECIFIC_CONTROLS_BINDING =
                Bindings.and(
                        Bindings.equal(
                                Main.simulator.getBuildState(),
                                Simulator.BuildState.EDITING_ONE
                        ),
                        Bindings.or(
                                Bindings.isNull(
                                        Main.simulator.getCurrentAmenity()
                                ),
                                (evaluateClassEquality) ?
                                        Bindings.notEqual(
                                                Main.simulator.getCurrentAmenity().get().getClass(),
                                                Main.simulator.getCurrentClass()
                                        ) :
                                        Bindings.createBooleanBinding(() -> false)
                        )
                );
    }

    @FXML
    public void initialize() {
        // Initialize all UI elements and label references
        UIInitializeService.initializeBuildTab(
                buildModeLabel,
                buildModeChoiceBox,
                // Entrances/exits
                // Station gate
                stationGateEnableCheckBox,
                stationGateModeLabel,
                stationGateModeChoiceBox,
                stationGateSpawnLabel,
                stationGateSpawnSpinner,
                saveStationGateButton,
                deleteStationGateButton,
                // Security
                securityEnableCheckBox,
                securityBlockPassengerCheckBox,
                securityIntervalLabel,
                securityIntervalSpinner,
                saveSecurityButton,
                deleteSecurityButton,
                // Concourse amenities
                // Ticket booth
                ticketBoothEnableCheckBox,
                ticketBoothModeLabel,
                ticketBoothModeChoiceBox,
                ticketBoothIntervalLabel,
                ticketBoothIntervalSpinner,
                saveTicketBoothButton,
                deleteTicketBoothButton,
                // Turnstile
                turnstileEnableCheckBox,
                turnstileBlockPassengerCheckBox,
                turnstileDirectionLabel,
                turnstileDirectionChoiceBox,
                turnstileIntervalLabel,
                turnstileIntervalSpinner,
                saveTurnstileButton,
                deleteTurnstileButton,
                // Platform amenities
                // Train doors
                trainDoorEnableCheckBox,
                trainDoorDirectionLabel,
                trainDoorDirectionChoiceBox,
                trainDoorCarriageLabel,
                trainDoorCarriageListView,
                saveTrainDoorButton,
                deleteTrainDoorButton,
                // Build tab
                buildTabPane
        );

        UIInitializeService.initializeTopTab(
                floorBelowButton,
                floorAboveButton
        );

        UIInitializeService.initializeTestTab(
                playButton
        );
    }

    @FXML
    // TODO: Load a station from a file
    public void loadStationAction() {
        // TODO: Display a dialog box for loading a station from a file
        Station station = new Station();

        // Set the station to be worked on to that station
        Main.simulator.setStation(station);

        // Set the current floor to the lowermost floor of the station
        Main.simulator.setCurrentFloor(station.getFloors().get(0));

        // Set the index of the current floor
        Main.simulator.setCurrentFloorIndex(0);

        // Update the top bar, reflecting the name and other details of the current station
        updateTopBar();

        // Draw the interface
        drawInterface(true);
    }

    @FXML
    // Remove amenities
    public void deleteAmenityAction() {
        deleteAmenityInFloor(Main.simulator.getBuildState().get() == Simulator.BuildState.EDITING_ONE);

        // Hence, the simulator won't have this amenity anymore
        Main.simulator.setCurrentAmenity(null);
        Main.simulator.setCurrentClass(null);

        // Redraw the interface
        drawInterface(false);
    }

    @FXML
    // Save amenities
    public void saveAmenityAction() {
        saveAmenityInFloor(Main.simulator.getBuildState().get() == Simulator.BuildState.EDITING_ONE);

        // Reset the current amenity and class to nulls to disable the save and delete buttons
        Main.simulator.setCurrentAmenity(null);
        Main.simulator.setCurrentClass(null);
    }

    @FXML
    // Add a floor below this current floor
    public void addFloorBelowAction() {
        // Retrieve the floors of the current station
        Floor currentFloor = Main.simulator.getCurrentFloor();
        List<Floor> currentStationFloors = Main.simulator.getStation().getFloors();

        // Add the floor below
        Floor newFloor = Floor.addFloorAboveOrBelow(
                currentStationFloors,
                currentFloor,
                false,
                Station.ROWS,
                Station.COLUMNS
        );

        // Switch to that new floor
        switchFloor(newFloor, false);

        // Show a dialog box to confirm that a floor was added
        AlertController.showSimpleAlert(
                "Floor added",
                "Floor added successfully",
                "A new floor has been added successfully. The view has been switched to that new floor.",
                Alert.AlertType.INFORMATION
        );
    }

    @FXML
    // Add a floor above this current floor
    public void addFloorAboveAction() {
        // Retrieve the floors of the current station
        Floor currentFloor = Main.simulator.getCurrentFloor();
        List<Floor> currentStationFloors = Main.simulator.getStation().getFloors();

        // Add the floor below
        Floor newFloor = Floor.addFloorAboveOrBelow(
                currentStationFloors,
                currentFloor,
                true,
                Station.ROWS,
                Station.COLUMNS
        );

        // Switch to that new floor
        switchFloor(newFloor, false);

        // Show a dialog box to confirm that a floor was added
        AlertController.showSimpleAlert(
                "Floor added",
                "Floor added successfully",
                "A new floor has been added successfully. The view has been switched to that new floor.",
                Alert.AlertType.INFORMATION
        );
    }

    @FXML
    // Delete current floor
    public void deleteFloorAction() {
        List<Floor> floors = Main.simulator.getStation().getFloors();

        // Only allow deletion of floors when there are more than one
        // i.e., you can't delete the only floor you're working on
        if (floors.size() > 1) {
            // Show a dialog to confirm floor deletion
            boolean confirm = AlertController.showConfirmationAlert(
                    "Are you sure?",
                    "Are you sure you want to delete this floor?",
                    "All amenities within this floor will be deleted. Stairs, escalators, and elevators" +
                            " going to or coming from this floor will also be deleted from other floors. This" +
                            " operation cannot be undone."
            );

            if (confirm) {
                Floor floorToBeRemoved = Main.simulator.getCurrentFloor();
                Floor floorToSwitchTo;

                // Delete the current floor
                Floor.removeFloor(
                        floors,
                        floorToBeRemoved
                );

                // Switch to the floor below the floor to be deleted, if any
                if (Main.simulator.getCurrentFloorIndex().get() > 0) {
                    floorToSwitchTo = floors.get(
                            Main.simulator.getCurrentFloorIndex().get() - 1
                    );

                    // Show a dialog box to confirm that a floor was added
                    AlertController.showSimpleAlert(
                            "Floor deleted",
                            "Floor deleted successfully",
                            "The floor has been deleted successfully. The view has been switched to the floor below" +
                                    " the deleted floor.",
                            Alert.AlertType.INFORMATION
                    );
                } else {
                    // If there is no floor below it, switch to the floor above it
                    floorToSwitchTo = floors.get(0);

                    // Show a dialog box to confirm that a floor was added
                    AlertController.showSimpleAlert(
                            "Floor deleted",
                            "Floor deleted successfully",
                            "The floor has been deleted successfully. The view has been switched to the floor above" +
                                    " the deleted floor.",
                            Alert.AlertType.INFORMATION
                    );
                }

                // Switch to the floor below
                switchFloor(floorToSwitchTo, false);
            }
        } else {
            // Show a dialog box to tell the user that deleting a singular floor is not allowed
            AlertController.showSimpleAlert(
                    "Deletion failed",
                    "Floor deletion failed",
                    "You may not delete the only floor in this station.",
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    // Switch view to the floor below
    public void switchToFloorBelowAction() {
        // Get the floor below
        Floor floorBelow = Main.simulator.getStation().getFloors().get(
                Main.simulator.getCurrentFloorIndex().get() - 1
        );

        // Switch to that floor
        switchFloor(floorBelow, false);
    }

    @FXML
    // Switch view to the floor above
    public void switchToFloorAboveAction() {
        // Get the floor above
        Floor floorAbove = Main.simulator.getStation().getFloors().get(
                Main.simulator.getCurrentFloorIndex().get() + 1
        );

        // Switch to that floor
        switchFloor(floorAbove, false);
    }

    // Switch to a given floor
    private void switchFloor(Floor floor, boolean showListeners) {
        // Make the given floor the current floor
        Main.simulator.setCurrentFloor(floor);

        // Update the index of the current floor
        Main.simulator.setCurrentFloorIndex(
                Main.simulator.getStation().getFloors().indexOf(floor)
        );

        // Switch to that floor by redrawing the interface
        drawInterface(showListeners);

        // Update the top bar
        updateTopBar();

        // Check if the above and below switch floor buttons may be enabled
        floorBelowButton.setDisable(
                Main.simulator.getCurrentFloorIndex().get() == 0
        );

        floorAboveButton.setDisable(
                Main.simulator.getCurrentFloorIndex().get() == Main.simulator.getStation().getFloors().size() - 1
        );
    }

    // Update the top bar as a while
    private void updateTopBar() {
        updateStationNameText();
        updateFloorNumberText();
        updatePromptText();
    }

    // Set the station name text
    private void updateStationNameText() {
        stationNameText.setText(
                Main.simulator.getStation().getName()
        );
    }

    // Set the floor number text
    public void updateFloorNumberText() {
        floorNumberText.setText(
                "Floor #" + (Main.simulator.getCurrentFloorIndex().get() + 1)
        );
    }

    // Set the prompt text
    public void updatePromptText() {
        String operationModeText = "";
        String buildStateText = "";
        String amenityText = "";

        boolean testing = Simulator.OperationMode.TESTING.equals(Main.simulator.getOperationMode().get());
        boolean editingAll = Simulator.BuildState.EDITING_ALL.equals(Main.simulator.getBuildState().get());
        boolean noAmenity = Simulator.BuildSubcategory.NONE.equals(Main.simulator.getBuildSubcategory().get());

        switch (Main.simulator.getOperationMode().get()) {
            case BUILDING:
                operationModeText = "Building";

                break;
            case TESTING:
                operationModeText = "Testing";

                break;
        }

        switch (Main.simulator.getBuildState().get()) {
            case DRAWING:
                buildStateText = ((!noAmenity) ? "Drawing" : "draw");

                break;
            case EDITING_ONE:
                buildStateText = ((!noAmenity) ? "Editing" : "edit");

                break;
            case EDITING_ALL:
                buildStateText = ((!noAmenity) ? "Editing all" : "edit all");

                break;
        }

        switch (Main.simulator.getBuildSubcategory().get()) {
            case NONE:
                amenityText = "none";

                break;
            case STATION_ENTRANCE_EXIT:
                amenityText = "station entrance/exit" + ((editingAll) ? "s" : "");

                break;
            case SECURITY:
                amenityText = "security gate" + ((editingAll) ? "s" : "");

                break;
            case STAIRS:
                amenityText = "stairs";

                break;
            case ESCALATOR:
                amenityText = "escalator" + ((editingAll) ? "s" : "");

                break;
            case ELEVATOR:
                amenityText = "elevator" + ((editingAll) ? "s" : "");

                break;
            case TICKET_BOOTH:
                amenityText = "ticket booth" + ((editingAll) ? "s" : "");

                break;
            case TURNSTILE:
                amenityText = "turnstile" + ((editingAll) ? "s" : "");

                break;
            case TRAIN_BOARDING_AREA:
                amenityText = "train boarding area" + ((editingAll) ? "s" : "");

                break;
            case FLOOR:
                amenityText = "floor" + ((editingAll) ? "s" : "");

                break;
            case QUEUEING_FLOOR_FIELD:
                amenityText = "queueing floor field" + ((editingAll) ? "s" : "");

                break;
            case WALL:
                amenityText = "wall" + ((editingAll) ? "s" : "");

                break;
        }

        promptText.setText(
                operationModeText + ((testing) ?
                        ""
                        :
                        ": "
                                + ((!noAmenity) ?
                                buildStateText + " " + amenityText
                                :
                                "Ready to " + buildStateText
                        )
                )
        );
    }

    // Save a single amenity or all instances of an amenity in a floor
    private void saveAmenityInFloor(boolean singleAmenity) {
        // Distinguish whether only a single amenity will be saved or not
        if (singleAmenity) {
            saveSingleAmenityInFloor();
        } else {
            saveAllAmenitiesInFloor();
        }
    }

    // Save the current amenity in a floor
    private void saveSingleAmenityInFloor() {
        switch (Main.simulator.getBuildSubcategory().get()) {
            case STATION_ENTRANCE_EXIT:
                StationGate stationGateToEdit
                        = (StationGate) Main.simulator.getCurrentAmenity().get();

                stationGateToEdit.setEnabled(
                        stationGateEnableCheckBox.isSelected()
                );

                stationGateToEdit.setChancePerSecond(
                        stationGateSpawnSpinner.getValue() / 100.0
                );

                stationGateToEdit.setStationGateMode(
                        stationGateModeChoiceBox.getValue()
                );

                break;
            case SECURITY:
                Security securityToEdit
                        = (Security) Main.simulator.getCurrentAmenity().get();

                securityToEdit.setEnabled(
                        securityEnableCheckBox.isSelected()
                );

                securityToEdit.setBlockEntry(
                        securityBlockPassengerCheckBox.isSelected()
                );

                securityToEdit.setWaitingTime(
                        securityIntervalSpinner.getValue()
                );

                break;
            case STAIRS:
                break;
            case ESCALATOR:
                break;
            case ELEVATOR:
                break;
            case TICKET_BOOTH:
                TicketBoothTransactionArea ticketBoothTransactionAreaToEdit
                        = (TicketBoothTransactionArea) Main.simulator.getCurrentAmenity().get();

                ticketBoothTransactionAreaToEdit.setEnabled(
                        ticketBoothEnableCheckBox.isSelected()
                );

                ticketBoothTransactionAreaToEdit.setTicketBoothType(
                        ticketBoothModeChoiceBox.getValue()
                );

                ticketBoothTransactionAreaToEdit.setWaitingTime(
                        ticketBoothIntervalSpinner.getValue()
                );

                break;
            case TURNSTILE:
                Turnstile turnstileToEdit
                        = (Turnstile) Main.simulator.getCurrentAmenity().get();

                turnstileToEdit.setEnabled(
                        turnstileEnableCheckBox.isSelected()
                );

                turnstileToEdit.setBlockEntry(
                        turnstileBlockPassengerCheckBox.isSelected()
                );

                turnstileToEdit.setTurnstileMode(
                        turnstileDirectionChoiceBox.getValue()
                );

                turnstileToEdit.setWaitingTime(
                        turnstileIntervalSpinner.getValue()
                );

                break;
            case TRAIN_BOARDING_AREA:
                if (!trainDoorCarriageListView.getSelectionModel().isEmpty()) {
                    TrainDoor trainDoorToEdit
                            = (TrainDoor) Main.simulator.getCurrentAmenity().get();

                    trainDoorToEdit.setEnabled(
                            trainDoorEnableCheckBox.isSelected()
                    );

                    trainDoorToEdit.setPlatform(
                            trainDoorDirectionChoiceBox.getValue()
                    );

                    trainDoorToEdit.setTrainDoorCarriagesSupported(
                            trainDoorCarriageListView.getSelectionModel().getSelectedItems()
                    );
                } else {
                    AlertController.showSimpleAlert(
                            "No carriages selected",
                            "No carriages selected",
                            "Please select the train carriage(s) supported by this train boarding area",
                            Alert.AlertType.ERROR
                    );
                }

                break;
            case WALL:
                break;
        }
    }

    // Save all instances of an amenity in a floor
    private void saveAllAmenitiesInFloor() {
        switch (Main.simulator.getBuildSubcategory().get()) {
            case STATION_ENTRANCE_EXIT:
                // Edit all station gates
                for (StationGate stationGate : Main.simulator.getCurrentFloor().getStationGates()) {
                    stationGate.setEnabled(
                            stationGateEnableCheckBox.isSelected()
                    );

                    stationGate.setChancePerSecond(
                            stationGateSpawnSpinner.getValue() / 100.0
                    );

                    stationGate.setStationGateMode(
                            stationGateModeChoiceBox.getValue()
                    );
                }

                break;
            case SECURITY:
                // Edit all ticket booths
                for (Security security : Main.simulator.getCurrentFloor().getSecurities()) {
                    security.setEnabled(
                            securityEnableCheckBox.isSelected()
                    );

                    security.setBlockEntry(
                            securityBlockPassengerCheckBox.isSelected()
                    );

                    security.setWaitingTime(
                            securityIntervalSpinner.getValue()
                    );
                }

                break;
            case STAIRS:
                break;
            case ESCALATOR:
                break;
            case ELEVATOR:
                break;
            case TICKET_BOOTH:
                // Edit all ticket booths
                for (TicketBooth ticketBoothToEdit : Main.simulator.getCurrentFloor().getTicketBooths()) {
                    TicketBoothTransactionArea ticketBoothTransactionAreaToEdit
                            = ticketBoothToEdit.getTicketBoothTransactionArea();

                    ticketBoothTransactionAreaToEdit.setEnabled(
                            ticketBoothEnableCheckBox.isSelected()
                    );

                    ticketBoothTransactionAreaToEdit.setTicketBoothType(
                            ticketBoothModeChoiceBox.getValue()
                    );

                    ticketBoothTransactionAreaToEdit.setWaitingTime(
                            ticketBoothIntervalSpinner.getValue()
                    );
                }

                break;
            case TURNSTILE:
                // Edit all turnstiles
                for (Turnstile turnstileToEdit : Main.simulator.getCurrentFloor().getTurnstiles()) {
                    turnstileToEdit.setEnabled(
                            turnstileEnableCheckBox.isSelected()
                    );

                    turnstileToEdit.setBlockEntry(
                            turnstileBlockPassengerCheckBox.isSelected()
                    );

                    turnstileToEdit.setTurnstileMode(
                            turnstileDirectionChoiceBox.getValue()
                    );

                    turnstileToEdit.setWaitingTime(
                            turnstileIntervalSpinner.getValue()
                    );
                }

                break;
            case TRAIN_BOARDING_AREA:
                // Edit all train doors
                if (!trainDoorCarriageListView.getSelectionModel().isEmpty()) {
                    for (TrainDoor trainDoorToEdit : Main.simulator.getCurrentFloor().getTrainDoors()) {
                        trainDoorToEdit.setEnabled(
                                trainDoorEnableCheckBox.isSelected()
                        );

                        trainDoorToEdit.setPlatform(
                                trainDoorDirectionChoiceBox.getValue()
                        );

                        trainDoorToEdit.setTrainDoorCarriagesSupported(
                                trainDoorCarriageListView.getSelectionModel().getSelectedItems()
                        );
                    }
                } else {
                    AlertController.showSimpleAlert(
                            "No carriages selected",
                            "No carriages selected",
                            "Please select the train carriage(s) supported by these train boarding areas",
                            Alert.AlertType.ERROR
                    );
                }

                break;
            case WALL:
                break;
        }
    }

    // Delete a single amenity or all instances of an amenity in a floor
    private void deleteAmenityInFloor(boolean singleAmenity) {
        // Distinguish whether only a single amenity will be deleted or not
        if (singleAmenity) {
            deleteSingleAmenityInFloor();
        } else {
            deleteAllAmenitiesInFloor();
        }
    }

    // Delete the current amenity in a floor
    private void deleteSingleAmenityInFloor() {
        // If the amenity to be deleted is a ticket booth transaction area, there are some extra steps
        // to be made
        TicketBooth ticketBoothToDelete = null;

        if (Main.simulator.getCurrentAmenity().get() instanceof TicketBoothTransactionArea) {
            // Get the ticket booth from this patch
            ticketBoothToDelete
                    = ((TicketBoothTransactionArea) Main.simulator.getCurrentAmenity().get()).getTicketBooth();

            // Delete the ticket booth from the patch that contain it
            ticketBoothToDelete.getPatch().setAmenity(null);
        }

        // Delete this amenity from the patch that contains it
        Main.simulator.getCurrentAmenity().get().getPatch().setAmenity(null);

        // Also delete this amenity from the list of station gates in this floor
        switch (Main.simulator.getBuildSubcategory().get()) {
            case STATION_ENTRANCE_EXIT:
                Main.simulator.getCurrentFloor().getStationGates().remove(
                        (StationGate) Main.simulator.getCurrentAmenity().get()
                );

                break;
            case SECURITY:
                Main.simulator.getCurrentFloor().getSecurities().remove(
                        (Security) Main.simulator.getCurrentAmenity().get()
                );

                break;
            case STAIRS:
                break;
            case ESCALATOR:
                break;
            case ELEVATOR:
                break;
            case TICKET_BOOTH:
                Main.simulator.getCurrentFloor().getTicketBooths().remove(
                        ticketBoothToDelete
                );

                break;
            case TURNSTILE:
                Main.simulator.getCurrentFloor().getTurnstiles().remove(
                        (Turnstile) Main.simulator.getCurrentAmenity().get()
                );

                break;
            case TRAIN_BOARDING_AREA:
                Main.simulator.getCurrentFloor().getTrainDoors().remove(
                        (TrainDoor) Main.simulator.getCurrentAmenity().get()
                );

                break;
            case WALL:
                break;
        }
    }

    // Delete all instances of an amenity in a floor
    private void deleteAllAmenitiesInFloor() {
        switch (Main.simulator.getBuildSubcategory().get()) {
            case STATION_ENTRANCE_EXIT:
                for (StationGate stationGate : Main.simulator.getCurrentFloor().getStationGates()) {
                    stationGate.getPatch().setAmenity(null);
                }

                Main.simulator.getCurrentFloor().getStationGates().clear();

                break;
            case SECURITY:
                for (Security security : Main.simulator.getCurrentFloor().getSecurities()) {
                    security.getPatch().setAmenity(null);
                }

                Main.simulator.getCurrentFloor().getSecurities().clear();

                break;
            case STAIRS:
                break;
            case ESCALATOR:
                break;
            case ELEVATOR:
                break;
            case TICKET_BOOTH:
                for (TicketBooth ticketBooth : Main.simulator.getCurrentFloor().getTicketBooths()) {
                    ticketBooth.getPatch().setAmenity(null);
                    ticketBooth.getTicketBoothTransactionArea().getPatch().setAmenity(null);
                }

                Main.simulator.getCurrentFloor().getTicketBooths().clear();

                break;
            case TURNSTILE:
                for (Turnstile turnstile : Main.simulator.getCurrentFloor().getTurnstiles()) {
                    turnstile.getPatch().setAmenity(null);
                }

                Main.simulator.getCurrentFloor().getTurnstiles().clear();

                break;
            case TRAIN_BOARDING_AREA:
                for (TrainDoor trainDoor : Main.simulator.getCurrentFloor().getTrainDoors()) {
                    trainDoor.getPatch().setAmenity(null);
                }

                Main.simulator.getCurrentFloor().getTrainDoors().clear();

                break;
            case WALL:
                break;
        }
    }

    public static Simulator.BuildCategory getBuildCategory(SingleSelectionModel<Tab> currentTabSelectionModel) {
        Simulator.BuildCategory buildCategory = null;

        switch (currentTabSelectionModel.getSelectedIndex()) {
            case 0:
                buildCategory = Simulator.BuildCategory.ENTRANCES_AND_EXITS;

                break;
            case 1:
                buildCategory = Simulator.BuildCategory.STAIRS_AND_ELEVATORS;

                break;
            case 2:
                buildCategory = Simulator.BuildCategory.CONCOURSE_AMENITIES;

                break;
            case 3:
                buildCategory = Simulator.BuildCategory.PLATFORM_AMENITIES;

                break;
            case 4:
                buildCategory = Simulator.BuildCategory.FLOORS_AND_FLOOR_FIELDS;

                break;
            case 5:
                buildCategory = Simulator.BuildCategory.WALLS;

                break;
        }

        return buildCategory;
    }

    public static Simulator.BuildSubcategory getBuildSubcategory(
            SingleSelectionModel<Tab> currentTabSelectionModel) {
        // Get the build subcategory
        Simulator.BuildSubcategory buildSubcategory = null;

        Accordion currentAccordion = (Accordion) currentTabSelectionModel.getSelectedItem().getContent();
        TitledPane currentTitledPane = currentAccordion.getExpandedPane();

        // If there is no expanded subcategory, the current subcategory is none
        if (currentTitledPane == null) {
            buildSubcategory = Simulator.BuildSubcategory.NONE;
        } else {
            String titledPaneTitle = currentAccordion.getExpandedPane().getText();

            switch (titledPaneTitle) {
                case "Station entrance/exit":
                    buildSubcategory = Simulator.BuildSubcategory.STATION_ENTRANCE_EXIT;

                    break;
                case "Security":
                    buildSubcategory = Simulator.BuildSubcategory.SECURITY;

                    break;
                case "Stairs":
                    buildSubcategory = Simulator.BuildSubcategory.STAIRS;

                    break;
                case "Escalator":
                    buildSubcategory = Simulator.BuildSubcategory.ESCALATOR;

                    break;
                case "Elevator":
                    buildSubcategory = Simulator.BuildSubcategory.ELEVATOR;

                    break;
                case "Ticket booth":
                    buildSubcategory = Simulator.BuildSubcategory.TICKET_BOOTH;

                    break;
                case "Turnstile":
                    buildSubcategory = Simulator.BuildSubcategory.TURNSTILE;

                    break;
                case "Train boarding area":
                    buildSubcategory = Simulator.BuildSubcategory.TRAIN_BOARDING_AREA;

                    break;
                case "Floor":
                    buildSubcategory = Simulator.BuildSubcategory.FLOOR;

                    break;
                case "Floor field":
                    buildSubcategory = Simulator.BuildSubcategory.QUEUEING_FLOOR_FIELD;

                    break;
                case "Wall":
                    buildSubcategory = Simulator.BuildSubcategory.WALL;

                    break;
            }
        }

        return buildSubcategory;
    }

    // Contains actions for building or editing
    public void buildOrEdit(Patch currentPatch) {
        // Get the current operation mode, category, and subcategory
        Simulator.OperationMode operationMode = Main.simulator.getOperationMode().get();
        Simulator.BuildCategory buildCategory = Main.simulator.getBuildCategory().get();
        Simulator.BuildSubcategory buildSubcategory = Main.simulator.getBuildSubcategory().get();
        Simulator.BuildState buildState = Main.simulator.getBuildState().get();

        // If the operation mode is building, the user either wants to draw or edit (one or all)
        if (operationMode == Simulator.OperationMode.BUILDING) {
            // Draw depending on the category and subcategory
            switch (buildCategory) {
                case ENTRANCES_AND_EXITS:
                case STAIRS_AND_ELEVATORS:
                case CONCOURSE_AMENITIES:
                case PLATFORM_AMENITIES:
                case WALLS:
                    switch (buildSubcategory) {
                        case STATION_ENTRANCE_EXIT:
                            switch (buildState) {
                                case DRAWING:
                                    // Only add if the current patch doesn't already have an amenity
                                    if (Main.simulator.getCurrentAmenity().isNull().get()) {
                                        Main.simulator.setCurrentClass(StationGate.class);

                                        // Prepare the amenity that will be placed on the station
                                        StationGate.StationGateFactory stationGateFactory
                                                = new StationGate.StationGateFactory();

                                        StationGate stationGateToAdd = (StationGate) stationGateFactory.create(
                                                currentPatch,
                                                stationGateEnableCheckBox.isSelected(),
                                                stationGateSpawnSpinner.getValue() / 100.0,
                                                stationGateModeChoiceBox.getValue()
                                        );

                                        // Set the amenity on that patch
                                        currentPatch.setAmenity(stationGateToAdd);

                                        // Add this station gate to the list of all station gates on this floor
                                        Main.simulator.getCurrentFloor().getStationGates().add(stationGateToAdd);
                                    } else {
                                        // If clicked on an existing amenity, switch to editing mode, then open that
                                        // amenity's controls
                                        goToAmenityControls(Main.simulator.getCurrentAmenity().get());

                                        // Then revisit this method as if that amenity was clicked
                                        buildOrEdit(currentPatch);
                                    }

                                    break;
                                case EDITING_ONE:
                                    // Only edit if there is already a station gate on that patch
                                    if (Main.simulator.getCurrentAmenity().get() instanceof StationGate) {
                                        Main.simulator.setCurrentClass(StationGate.class);

                                        StationGate stationGateToEdit
                                                = (StationGate) Main.simulator.getCurrentAmenity().get();

                                        stationGateEnableCheckBox.setSelected(
                                                stationGateToEdit.isEnabled()
                                        );

                                        stationGateSpawnSpinner.getValueFactory().setValue(
                                                (int) (stationGateToEdit.getChancePerSecond() * 100)
                                        );

                                        stationGateModeChoiceBox.setValue(
                                                stationGateToEdit.getStationGateMode()
                                        );
                                    } else {
                                        // If there is no amenity there, just do nothing
                                        if (Main.simulator.getCurrentAmenity().isNotNull().get()) {
                                            // If clicked on an existing amenity, switch to editing mode, then open that
                                            // amenity's controls
                                            goToAmenityControls(Main.simulator.getCurrentAmenity().get());

                                            // Then revisit this method as if that amenity was clicked
                                            buildOrEdit(currentPatch);
                                        }
                                    }

                                    break;
                                case EDITING_ALL:
                                    // No specific values need to be set here because all station gates will be edited
                                    // once save is clicked
                                    // If there is no amenity there, just do nothing
                                    if (Main.simulator.getCurrentAmenity().isNotNull().get()) {
                                        // If clicked on an existing amenity, switch to editing mode, then open that
                                        // amenity's controls
                                        goToAmenityControls(Main.simulator.getCurrentAmenity().get());

                                        // Then revisit this method as if that amenity was clicked
                                        buildOrEdit(currentPatch);
                                    }

                                    break;
                            }

                            break;
                        case SECURITY:
                            switch (buildState) {
                                case DRAWING:
                                    // Only add if the current patch doesn't already have an amenity
                                    if (Main.simulator.getCurrentAmenity().isNull().get()) {
                                        Main.simulator.setCurrentClass(Security.class);

                                        // Prepare the amenity that will be placed on the station
                                        Security.SecurityFactory securityFactory
                                                = new Security.SecurityFactory();

                                        Security securityToAdd = (Security) securityFactory.create(
                                                currentPatch,
                                                securityEnableCheckBox.isSelected(),
                                                securityIntervalSpinner.getValue(),
                                                securityBlockPassengerCheckBox.isSelected()
                                        );

                                        // Set the amenity on that patch
                                        currentPatch.setAmenity(securityToAdd);

                                        // Add this station gate to the list of all security gates on this floor
                                        Main.simulator.getCurrentFloor().getSecurities().add(securityToAdd);
                                    } else {
                                        // If clicked on an existing amenity, switch to editing mode, then open that
                                        // amenity's controls
                                        goToAmenityControls(Main.simulator.getCurrentAmenity().get());

                                        // Then revisit this method as if that amenity was clicked
                                        buildOrEdit(currentPatch);
                                    }

                                    break;
                                case EDITING_ONE:
                                    // Only edit if there is already a station gate on that patch
                                    if (Main.simulator.getCurrentAmenity().get() instanceof Security) {
                                        Main.simulator.setCurrentClass(Security.class);

                                        Security securityToEdit
                                                = (Security) Main.simulator.getCurrentAmenity().get();

                                        securityEnableCheckBox.setSelected(
                                                securityToEdit.isEnabled()
                                        );

                                        securityBlockPassengerCheckBox.setSelected(
                                                securityToEdit.isBlockEntry()
                                        );

                                        securityIntervalSpinner.getValueFactory().setValue(
                                                securityToEdit.getWaitingTime()
                                        );
                                    } else {
                                        // If there is no amenity there, just do nothing
                                        if (Main.simulator.getCurrentAmenity().isNotNull().get()) {
                                            // If clicked on an existing amenity, switch to editing mode, then open that
                                            // amenity's controls
                                            goToAmenityControls(Main.simulator.getCurrentAmenity().get());

                                            // Then revisit this method as if that amenity was clicked
                                            buildOrEdit(currentPatch);
                                        }
                                    }

                                    break;
                                case EDITING_ALL:
                                    // No specific values need to be set here because all station gates will be edited
                                    // once save is clicked
                                    // If there is no amenity there, just do nothing
                                    if (Main.simulator.getCurrentAmenity().isNotNull().get()) {
                                        // If clicked on an existing amenity, switch to editing mode, then open that
                                        // amenity's controls
                                        goToAmenityControls(Main.simulator.getCurrentAmenity().get());

                                        // Then revisit this method as if that amenity was clicked
                                        buildOrEdit(currentPatch);
                                    }

                                    break;
                            }

                            break;
                        case STAIRS:
                            break;
                        case ESCALATOR:
                            break;
                        case ELEVATOR:
                            break;
                        case TICKET_BOOTH:
                            switch (buildState) {
                                case DRAWING:
                                    // Only add if the current patch and the extra patch doesn't already have an amenity
                                    if (Main.simulator.getCurrentAmenity().isNull().get()) {
                                        Main.simulator.setCurrentClass(TicketBoothTransactionArea.class);

                                        // Only add if the current location is valid
                                        if (GraphicsController.validTicketBoothDraw) {
                                            Patch extraPatch = GraphicsController.extraPatch;

                                            // Prepare the amenities that will be placed on the station
                                            TicketBooth.TicketBoothFactory ticketBoothFactory
                                                    = new TicketBooth.TicketBoothFactory();

                                            TicketBoothTransactionArea.TicketBoothTransactionAreaFactory
                                                    ticketBoothTransactionAreaFactory
                                                    = new TicketBoothTransactionArea
                                                    .TicketBoothTransactionAreaFactory();

                                            TicketBooth ticketBoothToAdd
                                                    = (TicketBooth) ticketBoothFactory.create(currentPatch);

                                            TicketBoothTransactionArea ticketBoothTransactionAreaToAdd
                                                    = (TicketBoothTransactionArea)
                                                    ticketBoothTransactionAreaFactory.create(
                                                            extraPatch,
                                                            ticketBoothEnableCheckBox.isSelected(),
                                                            ticketBoothIntervalSpinner.getValue(),
                                                            ticketBoothToAdd,
                                                            ticketBoothModeChoiceBox.getValue()
                                                    );

                                            ticketBoothToAdd.setTicketBoothTransactionArea(
                                                    ticketBoothTransactionAreaToAdd
                                            );

                                            // Set the amenities on their respective patches
                                            currentPatch.setAmenity(ticketBoothToAdd);
                                            extraPatch.setAmenity(ticketBoothTransactionAreaToAdd);

                                            // Add this station gate to the list of all ticket booths on this floor
                                            Main.simulator.getCurrentFloor().getTicketBooths().add(ticketBoothToAdd);
                                        }
                                    } else {
                                        // If clicked on an existing amenity, switch to editing mode, then open that
                                        // amenity's controls
                                        goToAmenityControls(Main.simulator.getCurrentAmenity().get());

                                        // Then revisit this method as if that amenity was clicked
                                        buildOrEdit(currentPatch);
                                    }

                                    break;
                                case EDITING_ONE:
                                    // Only edit if there is already a ticket booth or its transaction area on that
                                    // patch
                                    if (Main.simulator.getCurrentAmenity().get() instanceof TicketBooth
                                            || Main.simulator.getCurrentAmenity().get()
                                            instanceof TicketBoothTransactionArea) {
                                        Main.simulator.setCurrentClass(TicketBoothTransactionArea.class);

                                        TicketBoothTransactionArea ticketBoothTransactionAreaToEdit;

                                        // See whether this patch is a ticket booth or its transaction area
                                        // Resolve for the transaction area
                                        if (Main.simulator.getCurrentAmenity().get()
                                                instanceof TicketBooth) {
                                            ticketBoothTransactionAreaToEdit
                                                    = ((TicketBooth) Main.simulator.getCurrentAmenity().get())
                                                    .getTicketBoothTransactionArea();

                                            Main.simulator.setCurrentAmenity(ticketBoothTransactionAreaToEdit);
                                        } else {
                                            ticketBoothTransactionAreaToEdit
                                                    = (TicketBoothTransactionArea)
                                                    Main.simulator.getCurrentAmenity().get();
                                        }

                                        ticketBoothEnableCheckBox.setSelected(
                                                ticketBoothTransactionAreaToEdit.isEnabled()
                                        );

                                        ticketBoothModeChoiceBox.setValue(
                                                ticketBoothTransactionAreaToEdit.getTicketBoothType()
                                        );

                                        ticketBoothIntervalSpinner.getValueFactory().setValue(
                                                ticketBoothTransactionAreaToEdit.getWaitingTime()
                                        );
                                    } else {
                                        // If there is no amenity there, just do nothing
                                        if (Main.simulator.getCurrentAmenity().isNotNull().get()) {
                                            // If clicked on an existing amenity, switch to editing mode, then open that
                                            // amenity's controls
                                            goToAmenityControls(Main.simulator.getCurrentAmenity().get());

                                            // Then revisit this method as if that amenity was clicked
                                            buildOrEdit(currentPatch);
                                        }
                                    }

                                    break;
                                case EDITING_ALL:
                                    // No specific values need to be set here because all station gates will be edited
                                    // once save is clicked

                                    // If there is no amenity there, just do nothing
                                    if (Main.simulator.getCurrentAmenity().isNotNull().get()) {
                                        // If clicked on an existing amenity, switch to editing mode, then open that
                                        // amenity's controls
                                        goToAmenityControls(Main.simulator.getCurrentAmenity().get());

                                        // Then revisit this method as if that amenity was clicked
                                        buildOrEdit(currentPatch);
                                    }

                                    break;
                            }

                            break;
                        case TURNSTILE:
                            switch (buildState) {
                                case DRAWING:
                                    // Only add if the current patch doesn't already have an amenity
                                    if (Main.simulator.getCurrentAmenity().isNull().get()) {
                                        Main.simulator.setCurrentClass(Turnstile.class);

                                        // Prepare the amenity that will be placed on the station
                                        Turnstile.TurnstileFactory turnstileFactory = new Turnstile.TurnstileFactory();

                                        Turnstile turnstileToAdd = (Turnstile) turnstileFactory.create(
                                                currentPatch,
                                                turnstileEnableCheckBox.isSelected(),
                                                turnstileIntervalSpinner.getValue(),
                                                turnstileBlockPassengerCheckBox.isSelected(),
                                                turnstileDirectionChoiceBox.getValue()
                                        );

                                        // Set the amenity on that patch
                                        currentPatch.setAmenity(turnstileToAdd);

                                        // Add this station gate to the list of all turnstiles on this floor
                                        Main.simulator.getCurrentFloor().getTurnstiles().add(turnstileToAdd);
                                    } else {
                                        // If clicked on an existing amenity, switch to editing mode, then open that
                                        // amenity's controls
                                        goToAmenityControls(Main.simulator.getCurrentAmenity().get());

                                        // Then revisit this method as if that amenity was clicked
                                        buildOrEdit(currentPatch);
                                    }

                                    break;
                                case EDITING_ONE:
                                    // Only edit if there is already a station gate on that patch
                                    if (Main.simulator.getCurrentAmenity().get() instanceof Turnstile) {
                                        Main.simulator.setCurrentClass(Turnstile.class);

                                        Turnstile turnstileToEdit
                                                = (Turnstile) Main.simulator.getCurrentAmenity().get();

                                        turnstileEnableCheckBox.setSelected(
                                                turnstileToEdit.isEnabled()
                                        );

                                        turnstileBlockPassengerCheckBox.setSelected(
                                                turnstileToEdit.isBlockEntry()
                                        );

                                        turnstileDirectionChoiceBox.setValue(
                                                turnstileToEdit.getTurnstileMode()
                                        );

                                        turnstileIntervalSpinner.getValueFactory().setValue(
                                                turnstileToEdit.getWaitingTime()
                                        );
                                    } else {
                                        // If there is no amenity there, just do nothing
                                        if (Main.simulator.getCurrentAmenity().isNotNull().get()) {
                                            // If clicked on an existing amenity, switch to editing mode, then open that
                                            // amenity's controls
                                            goToAmenityControls(Main.simulator.getCurrentAmenity().get());

                                            // Then revisit this method as if that amenity was clicked
                                            buildOrEdit(currentPatch);
                                        }
                                    }

                                    break;
                                case EDITING_ALL:
                                    // No specific values need to be set here because all station gates will be edited
                                    // once save is clicked
                                    // If there is no amenity there, just do nothing
                                    if (Main.simulator.getCurrentAmenity().isNotNull().get()) {
                                        // If clicked on an existing amenity, switch to editing mode, then open that
                                        // amenity's controls
                                        goToAmenityControls(Main.simulator.getCurrentAmenity().get());

                                        // Then revisit this method as if that amenity was clicked
                                        buildOrEdit(currentPatch);
                                    }

                                    break;
                            }

                            break;
                        case TRAIN_BOARDING_AREA:
                            switch (buildState) {
                                case DRAWING:
                                    // Only add if the current patch doesn't already have an amenity
                                    if (Main.simulator.getCurrentAmenity().isNull().get()) {
                                        Main.simulator.setCurrentClass(TrainDoor.class);

                                        if (!trainDoorCarriageListView.getSelectionModel().isEmpty()) {
                                            // Prepare the amenity that will be placed on the station
                                            TrainDoor.TrainDoorFactory trainDoorFactory
                                                    = new TrainDoor.TrainDoorFactory();

                                            TrainDoor trainDoorToAdd = (TrainDoor) trainDoorFactory.create(
                                                    currentPatch,
                                                    trainDoorEnableCheckBox.isSelected(),
                                                    trainDoorDirectionChoiceBox.getSelectionModel().getSelectedItem(),
                                                    trainDoorCarriageListView.getSelectionModel().getSelectedItems()
                                            );

                                            // Set the amenity on that patch
                                            currentPatch.setAmenity(trainDoorToAdd);

                                            // Add this station gate to the list of all train doors on this floor
                                            Main.simulator.getCurrentFloor().getTrainDoors().add(trainDoorToAdd);
                                        } else {
                                            AlertController.showSimpleAlert(
                                                    "No carriages selected",
                                                    "No carriages selected",
                                                    "Please select the train carriage(s) supported by" +
                                                            " the train boarding area to be added",
                                                    Alert.AlertType.ERROR
                                            );
                                        }
                                    } else {
                                        // If clicked on an existing amenity, switch to editing mode, then open that
                                        // amenity's controls
                                        goToAmenityControls(Main.simulator.getCurrentAmenity().get());

                                        // Then revisit this method as if that amenity was clicked
                                        buildOrEdit(currentPatch);
                                    }

                                    break;
                                case EDITING_ONE:
                                    // Only edit if there is already a station gate on that patch
                                    if (Main.simulator.getCurrentAmenity().get() instanceof TrainDoor) {
                                        Main.simulator.setCurrentClass(TrainDoor.class);

                                        TrainDoor trainDoorToEdit
                                                = (TrainDoor) Main.simulator.getCurrentAmenity().get();

                                        trainDoorEnableCheckBox.setSelected(
                                                trainDoorToEdit.isEnabled()
                                        );

                                        trainDoorDirectionChoiceBox.setValue(
                                                trainDoorToEdit.getPlatform()
                                        );

                                        trainDoorCarriageListView.getSelectionModel().clearSelection();

                                        for (TrainDoor.TrainDoorCarriage trainDoorCarriage
                                                : trainDoorToEdit.getTrainDoorCarriagesSupported()) {
                                            trainDoorCarriageListView.getSelectionModel().select(trainDoorCarriage);
                                        }
                                    } else {
                                        // If there is no amenity there, just do nothing
                                        if (Main.simulator.getCurrentAmenity().isNotNull().get()) {
                                            // If clicked on an existing amenity, switch to editing mode, then open that
                                            // amenity's controls
                                            goToAmenityControls(Main.simulator.getCurrentAmenity().get());

                                            // Then revisit this method as if that amenity was clicked
                                            buildOrEdit(currentPatch);
                                        }
                                    }

                                    break;
                                case EDITING_ALL:
                                    // No specific values need to be set here because all station gates will be edited
                                    // once save is clicked
                                    // If there is no amenity there, just do nothing
                                    if (Main.simulator.getCurrentAmenity().isNotNull().get()) {
                                        // If clicked on an existing amenity, switch to editing mode, then open that
                                        // amenity's controls
                                        goToAmenityControls(Main.simulator.getCurrentAmenity().get());

                                        // Then revisit this method as if that amenity was clicked
                                        buildOrEdit(currentPatch);
                                    }

                                    break;
                            }

                            break;
                        case WALL:
                            break;
                        case NONE:
                            if (Main.simulator.getCurrentAmenity().isNotNull().get()) {
                                // If clicked on an existing amenity, switch to editing mode, then open that
                                // amenity's controls
                                goToAmenityControls(Main.simulator.getCurrentAmenity().get());

                                // Then revisit this method as if that amenity was clicked
                                buildOrEdit(currentPatch);
                            }

                            break;
                    }

                    break;
                case FLOORS_AND_FLOOR_FIELDS:
                    break;
            }
        } else {
            // If the operation mode is testing, the user wants to edit (one or all)
            System.out.println("test");
        }
    }

    // Given an amenity, open the subcategory (category) and titled pane (subcategory) that contains that amenity
    // in the interface
    public void goToAmenityControls(Amenity amenity) {
        Simulator.BuildCategory buildCategory = null;
        Simulator.BuildSubcategory buildSubcategory = null;

        // Set the choice box to the editing modes
        buildModeChoiceBox.getSelectionModel().select(Simulator.BuildState.EDITING_ONE);

        // Get category and subcategory
        if (
                amenity instanceof StationGate
                        || amenity instanceof Security
        ) {
            buildCategory = Simulator.BuildCategory.ENTRANCES_AND_EXITS;

            if (amenity instanceof StationGate) {
                buildSubcategory = Simulator.BuildSubcategory.STATION_ENTRANCE_EXIT;
            } else {
                buildSubcategory = Simulator.BuildSubcategory.SECURITY;
            }
        } else if (
                amenity instanceof StairPortal
                        || amenity instanceof EscalatorPortal
                        || amenity instanceof Elevator) {
            buildCategory = Simulator.BuildCategory.STAIRS_AND_ELEVATORS;

            if (amenity instanceof StairPortal) {
                buildSubcategory = Simulator.BuildSubcategory.STAIRS;
            } else if (amenity instanceof EscalatorPortal) {
                buildSubcategory = Simulator.BuildSubcategory.ESCALATOR;
            } else {
                buildSubcategory = Simulator.BuildSubcategory.ELEVATOR;
            }
        } else if (
                amenity instanceof TicketBooth
                        || amenity instanceof TicketBoothTransactionArea
                        || amenity instanceof Turnstile
        ) {
            buildCategory = Simulator.BuildCategory.CONCOURSE_AMENITIES;

            if (amenity instanceof TicketBooth || amenity instanceof TicketBoothTransactionArea) {
                buildSubcategory = Simulator.BuildSubcategory.TICKET_BOOTH;
            } else {
                buildSubcategory = Simulator.BuildSubcategory.TURNSTILE;
            }
        } else if (
                amenity instanceof TrainDoor
        ) {
            buildCategory = Simulator.BuildCategory.PLATFORM_AMENITIES;
            buildSubcategory = Simulator.BuildSubcategory.TRAIN_BOARDING_AREA;
        } else if (
                amenity instanceof Wall
        ) {
            buildCategory = Simulator.BuildCategory.WALLS;
            buildSubcategory = Simulator.BuildSubcategory.WALL;
        }

        // TODO: Deal with floor fields result
        if (buildCategory == null) {
            return;
        }

        Accordion accordion;
        ObservableList<TitledPane> titledPanes;

        // Then open the respective tab and titled pane
        switch (buildCategory) {
            case ENTRANCES_AND_EXITS:
                buildTabPane.getSelectionModel().select(0);

                accordion = (Accordion) buildTabPane.getTabs().get(0).getContent();
                titledPanes = accordion.getPanes();

                switch (buildSubcategory) {
                    case STATION_ENTRANCE_EXIT:
                        titledPanes.get(0).setExpanded(true);

                        break;
                    case SECURITY:
                        titledPanes.get(1).setExpanded(true);

                        break;
                }

                break;
            case STAIRS_AND_ELEVATORS:
                buildTabPane.getSelectionModel().select(1);

                accordion = (Accordion) buildTabPane.getTabs().get(1).getContent();
                titledPanes = accordion.getPanes();

                switch (buildSubcategory) {
                    case STAIRS:
                        titledPanes.get(0).setExpanded(true);

                        break;
                    case ESCALATOR:
                        titledPanes.get(1).setExpanded(true);

                        break;
                    case ELEVATOR:
                        titledPanes.get(2).setExpanded(true);

                        break;
                }

                break;
            case CONCOURSE_AMENITIES:
                buildTabPane.getSelectionModel().select(2);

                accordion = (Accordion) buildTabPane.getTabs().get(2).getContent();
                titledPanes = accordion.getPanes();

                switch (buildSubcategory) {
                    case TICKET_BOOTH:
                        titledPanes.get(0).setExpanded(true);

                        break;
                    case TURNSTILE:
                        titledPanes.get(1).setExpanded(true);

                        break;
                }

                break;
            case PLATFORM_AMENITIES:
                buildTabPane.getSelectionModel().select(3);

                accordion = (Accordion) buildTabPane.getTabs().get(3).getContent();
                titledPanes = accordion.getPanes();

                titledPanes.get(0).setExpanded(true);

                break;
            case FLOORS_AND_FLOOR_FIELDS:
                buildTabPane.getSelectionModel().select(4);

                // TODO: Deal with floor fields
/*                switch (buildSubcategory) {
                    case QUEUEING_FLOOR_FIELD:
                        break;
                }*/

                break;
            case WALLS:
                buildTabPane.getSelectionModel().select(5);

                accordion = (Accordion) buildTabPane.getTabs().get(5).getContent();
                titledPanes = accordion.getPanes();

                titledPanes.get(0).setExpanded(true);

                break;
        }

        // Switch the class to the class of the current amenity
        Main.simulator.setCurrentClass(amenity.getClass());
    }

    // Draw the interface
    private void drawInterface(boolean drawListeners) {
        final double tileSize = backgroundCanvas.getHeight() / Main.simulator.getCurrentFloor().getRows();

        // Initially draw the station environment, showing the current floor
        drawStationViewFloorBackground(Main.simulator.getCurrentFloor(), tileSize);

        // TODO: Then draw the passengers in the station

        // Then draw the mouse listeners over the station view
        if (drawListeners) {
            drawListeners(Main.simulator.getCurrentFloor(), tileSize);
        }
    }

    // Draw the station view background given a current floor
    private void drawStationViewFloorBackground(Floor currentFloor, double tileSize) {
        // Draw each station in the train system onto its respective tab
        GraphicsController.requestDrawStationView(
                interfaceStackPane,
                currentFloor,
                tileSize,
                true
        );
    }

    // Draw the mouse listeners
    private void drawListeners(Floor currentFloor, double tileSize) {
        // Draw each mouse listener along with their corresponding actions
        GraphicsController.requestDrawListeners(
                interfaceStackPane,
                overlay,
                currentFloor,
                tileSize
        );
    }
}
