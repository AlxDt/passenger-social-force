package com.crowdsimulation.controller.screen.main;

import com.crowdsimulation.controller.Main;
import com.crowdsimulation.controller.graphics.GraphicsController;
import com.crowdsimulation.controller.screen.ScreenController;
import com.crowdsimulation.controller.screen.alert.AlertController;
import com.crowdsimulation.controller.screen.feature.floorfield.NormalFloorFieldController;
import com.crowdsimulation.controller.screen.feature.portal.PortalFloorSelectorController;
import com.crowdsimulation.controller.screen.feature.portal.edit.ElevatorEditController;
import com.crowdsimulation.controller.screen.feature.portal.setup.ElevatorSetupController;
import com.crowdsimulation.controller.screen.feature.portal.setup.PortalSetupController;
import com.crowdsimulation.controller.screen.main.service.InitializeMainScreenService;
import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.Station;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.headful.QueueingFloorField;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.obstacle.TicketBooth;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.obstacle.Wall;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.Portal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.StationGate;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.TrainDoor;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.PortalShaft;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.elevator.ElevatorPortal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.elevator.ElevatorShaft;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.escalator.EscalatorPortal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.stairs.StairPortal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable.Security;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.TicketBoothTransactionArea;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable.Turnstile;
import com.crowdsimulation.model.simulator.Simulator;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import sun.security.krb5.internal.Ticket;

import java.io.IOException;
import java.util.List;

public class MainScreenController extends ScreenController {
    // Operational variables
    @FXML
    private TabPane sidebar;

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

    @FXML
    private Button addFloorFieldsSecurityButton;

    // Stairs and elevators
    // Stairs

    // Escalator

    // Elevator
    @FXML
    private Button addElevatorButton;

    @FXML
    private Button editElevatorButton;

    @FXML
    private Button deleteElevatorButton;

    @FXML
    private Button addFloorFieldsElevatorButton;

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

    @FXML
    private Button addFloorFieldsTicketBoothButton;

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

    @FXML
    private Button addFloorFieldsTrainDoorButton;

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

    public static NormalFloorFieldController normalFloorFieldController;

    public MainScreenController() {
        try {
            // Display the elevator setup prompt
            FXMLLoader loader = ScreenController.getLoader(
                    getClass(),
                    "/com/crowdsimulation/view/NormalFloorFieldInterface.fxml");
            Parent root = loader.load();

            MainScreenController.normalFloorFieldController = loader.getController();
            MainScreenController.normalFloorFieldController.setElements();
            MainScreenController.normalFloorFieldController.setRoot(root);

            MainScreenController.normalFloorFieldController.setX(Screen.getPrimary().getBounds().getWidth() * 0.75);
            MainScreenController.normalFloorFieldController.setY(Screen.getPrimary().getBounds().getHeight() * 0.25);
        } catch (IOException ex) {
            MainScreenController.normalFloorFieldController = null;

            ex.printStackTrace();
        }
    }

    @Override
    protected void closeAction() {

    }

    @FXML
    public void initialize() {
        // Initialize all UI elements and label references
        InitializeMainScreenService.initializeTopBar(
                floorBelowButton,
                floorAboveButton
        );

        InitializeMainScreenService.initializeSidebar(
                sidebar
        );

        InitializeMainScreenService.initializeBuildTab(
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
                addFloorFieldsSecurityButton,
                // Stairs and elevators
                // Stairs
                // Escalator
                // Elevator
                addElevatorButton,
                editElevatorButton,
                deleteElevatorButton,
                addFloorFieldsElevatorButton,
                // Concourse amenities
                // Ticket booth
                ticketBoothEnableCheckBox,
                ticketBoothModeLabel,
                ticketBoothModeChoiceBox,
                ticketBoothIntervalLabel,
                ticketBoothIntervalSpinner,
                saveTicketBoothButton,
                deleteTicketBoothButton,
                addFloorFieldsTicketBoothButton,
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
                addFloorFieldsTrainDoorButton,
                // Build tab
                buildTabPane
        );

        InitializeMainScreenService.initializeTestTab(
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
        deleteAmenityInFloor(Main.simulator.getBuildState() == Simulator.BuildState.EDITING_ONE);

        // Hence, the simulator won't have this amenity anymore
        Main.simulator.setCurrentAmenity(null);
        Main.simulator.setCurrentClass(null);

        // Redraw the interface
        drawInterface(false);
    }

    @FXML
    // Save amenities
    public void saveAmenityAction() {
        saveAmenityInFloor(Main.simulator.getBuildState() == Simulator.BuildState.EDITING_ONE);

        // Reset the current amenity and class to nulls to disable the save and delete buttons
        Main.simulator.setCurrentAmenity(null);
        Main.simulator.setCurrentClass(null);
    }

    @FXML
    // Add a portal
    public void addPortalAction() throws IOException {
        switch (Main.simulator.getBuildSubcategory()) {
            case STAIRS:
                break;
            case ESCALATOR:
                break;
            case ELEVATOR:
                // Only add an elevator when there are multiple floors
                if (Main.simulator.getStation().getFloors().size() > 1) {
                    // Display the elevator setup prompt
                    FXMLLoader loader = ScreenController.getLoader(
                            getClass(),
                            "/com/crowdsimulation/view/ElevatorSetupInterface.fxml");
                    Parent root = loader.load();

                    ElevatorSetupController elevatorSetupController = loader.getController();
                    elevatorSetupController.setElements();

                    // Show the window
                    elevatorSetupController.showWindow(
                            root,
                            "Elevator setup",
                            true,
                            false
                    );

                    // Only proceed when this window is closed through the proceed action
                    if (elevatorSetupController.isClosedWithAction()) {
                        // Prompt the user that it is now time to draw the first elevator
                        AlertController.showSimpleAlert(
                                "Add first elevator",
                                "Draw the first elevator",
                                "After closing this window, please draw the first elevator on this floor." +
                                        " Click X to cancel this operation.",
                                Alert.AlertType.INFORMATION
                        );

                        beginPortalDrawing(elevatorSetupController);
                    }
                } else {
                    AlertController.showSimpleAlert(
                            "Elevator addition failed",
                            "Unable to add elevator",
                            "You may only add elevators when there are more than one floors in the station.",
                            Alert.AlertType.INFORMATION
                    );
                }

                break;
        }
    }

    @FXML
    // Edit elevator
    public void editPortalAction() throws IOException {
        switch (Main.simulator.getBuildSubcategory()) {
            case STAIRS:
                break;
            case ESCALATOR:
                break;
            case ELEVATOR:
                // Display the elevator setup prompt
                FXMLLoader loader = ScreenController.getLoader(
                        getClass(),
                        "/com/crowdsimulation/view/ElevatorEditInterface.fxml");
                Parent root = loader.load();

                ElevatorEditController elevatorEditController = loader.getController();
                elevatorEditController.setElements();

                // Show the window
                elevatorEditController.showWindow(
                        root,
                        (Main.simulator.getBuildState() == Simulator.BuildState.EDITING_ONE)
                                ? "Edit an elevator" : "Edit all elevators",
                        true,
                        false
                );

                // Only proceed when this window is closed through the proceed action
                if (elevatorEditController.isClosedWithAction()) {
                    // Extract the modified elevator shaft from the window
                    ElevatorShaft elevatorShaft = (ElevatorShaft) elevatorEditController.getWindowOutput().get(
                            ElevatorEditController.OUTPUT_KEY
                    );

                    // Determine whether we need to edit one or all
                    boolean editingOne = Main.simulator.getBuildState() == Simulator.BuildState.EDITING_ONE;

                    if (editingOne) {
                        // Apply the changes to the elevator shaft to its component elevators
                        saveSingleAmenityInFloor(elevatorShaft);
                    } else {
                        // Apply the changes to the elevator shaft to all elevator shafts and all their component elevators
                        saveAllAmenitiesInFloor(elevatorShaft);
                    }

                    // Prompt the user that the elevator has been successfully edited
                    if (editingOne) {
                        AlertController.showSimpleAlert(
                                "Elevator edited",
                                "Elevator successfully edited",
                                "The elevator has been successfully edited.",
                                Alert.AlertType.INFORMATION
                        );
                    } else {
                        AlertController.showSimpleAlert(
                                "Elevators edited",
                                "Elevators successfully edited",
                                "The elevators have been successfully edited.",
                                Alert.AlertType.INFORMATION
                        );
                    }
                }

                break;
        }
    }

    @FXML
    // Delete elevator
    public void deletePortalAction() {
        boolean editingOne = Main.simulator.getBuildState() == Simulator.BuildState.EDITING_ONE;
        boolean confirm;

        switch (Main.simulator.getBuildSubcategory()) {
            case STAIRS:
                break;
            case ESCALATOR:
                break;
            case ELEVATOR:
                // Show a dialog to confirm floor deletion
                if (editingOne) {
                    confirm = AlertController.showConfirmationAlert(
                            "Are you sure?",
                            "Are you sure you want to delete this elevator?",
                            "This will remove this elevator from all its serviced floors. This operation cannot be undone."
                    );
                } else {
                    confirm = AlertController.showConfirmationAlert(
                            "Are you sure?",
                            "Are you sure you want to delete all elevators?",
                            "This will remove all elevators from their serviced floors. This operation cannot be undone."
                    );
                }

                // Determine whether we need to edit one or all
                if (confirm) {
                    if (editingOne) {
                        // Delete this elevator
                        deleteSingleAmenityInFloor(
                                ((ElevatorPortal) Main.simulator.getCurrentAmenity()).getElevatorShaft()
                        );
                    } else {
                        // Delete all elevators
                        deleteAllAmenitiesInFloor();
                    }

                    // Prompt the user that the elevator has been successfully edited
                    if (editingOne) {
                        AlertController.showSimpleAlert(
                                "Elevator deleted",
                                "Elevator successfully deleted",
                                "The elevator has been successfully deleted.",
                                Alert.AlertType.INFORMATION
                        );
                    } else {
                        AlertController.showSimpleAlert(
                                "All elevators deleted",
                                "All elevators successfully deleted",
                                "All elevators have been successfully deleted.",
                                Alert.AlertType.INFORMATION
                        );
                    }

                    // Redraw the interface
                    drawInterface(false);
                }

                break;
        }
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
        switchFloor(newFloor);

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
        switchFloor(newFloor);

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
                if (Main.simulator.getCurrentFloorIndex() > 0) {
                    floorToSwitchTo = floors.get(
                            Main.simulator.getCurrentFloorIndex() - 1
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
                switchFloor(floorToSwitchTo);
            }
        } else {
            // Show a dialog box to tell the user that deleting a singular floor is not allowed
            AlertController.showSimpleAlert(
                    "Floor deletion failed",
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
                Main.simulator.getCurrentFloorIndex() - 1
        );

        // Switch to that floor
        switchFloor(floorBelow);
    }

    @FXML
    // Switch view to the floor above
    public void switchToFloorAboveAction() {
        // Get the floor above
        Floor floorAbove = Main.simulator.getStation().getFloors().get(
                Main.simulator.getCurrentFloorIndex() + 1
        );

        // Switch to that floor
        switchFloor(floorAbove);
    }

    @FXML
    // Add floor fields
    public void addFloorFieldsAction() {
        // Turn on the floor fields drawing mode
        beginFloorFieldDrawing();

        // Commence adding the floor fields
        addFloorFields();
    }

    // Add floor fields
    private void addFloorFields() {
        switch (Main.simulator.getBuildSubcategory()) {
            case SECURITY:
            case ELEVATOR:
            case TICKET_BOOTH:
            case TRAIN_BOARDING_AREA:
                // Show the window to edit the parameters of the floor field
                MainScreenController.normalFloorFieldController.showWindow(
                        MainScreenController.normalFloorFieldController.getRoot(),
                        "Floor fields",
                        false,
                        true
                );

                break;
            case TURNSTILE:
                break;
        }
    }

    // Clear floor fields and redraw interface
    public void clearFloorFieldAction() {
        clearFloorField();

        drawInterface(false);
    }

    // Clear an entire floor field
    private void clearFloorField() {
        // Clear the floor field of the current target given the current floor field state
        QueueingFloorField.clearFloorField(
                Main.simulator.getCurrentFloorFieldTarget().retrieveFloorField(
                        Main.simulator.getCurrentFloorFieldState()
                ),
                Main.simulator.getCurrentFloorFieldState()
        );
    }

    // Enable floor fields drawing
    private void beginFloorFieldDrawing() {
        Main.simulator.setFloorFieldDrawing(true);

        // Reset switch floors buttons
        resetSwitchFloorButtons();

        // Redraw interface
        drawInterface(false);
    }

    // Disable floor fields drawing
    public void endFloorFieldDrawing(boolean windowClosedAutomatically) {
        Main.simulator.setFloorFieldDrawing(false);

        // If the window will be closed manually, the current patch hasn't changed - the window is just closed
        // So don't reset the current floor field targets and states
        // Otherwise, proceed with the reset
        if (windowClosedAutomatically) {
            Main.simulator.setCurrentFloorFieldTarget(null);
            Main.simulator.setCurrentFloorFieldState(null);
        }

        // If the window is to be closed automatically, do so
        if (windowClosedAutomatically) {
            MainScreenController.normalFloorFieldController.closeWindow();
        }

        // Reset switch floors buttons
        resetSwitchFloorButtons();

        // Redraw interface
        drawInterface(false);
    }

    // Switch to a given floor
    private void switchFloor(Floor floor) {
        // Make the given floor the current floor
        Main.simulator.setCurrentFloor(floor);

        // Update the index of the current floor
        Main.simulator.setCurrentFloorIndex(
                Main.simulator.getStation().getFloors().indexOf(floor)
        );

        // Switch to that floor by redrawing the interface
        drawInterface(false);

        // Update the top bar
        updateTopBar();

        // Reset switch floor buttons
        resetSwitchFloorButtons();
    }

    // Reset the switch floor buttons
    private void resetSwitchFloorButtons() {
        // Check if the above and below switch floor buttons may be enabled
        floorBelowButton.setDisable(
                Main.simulator.isPortalDrawing()
                        || Main.simulator.isFloorFieldDrawing()
                        || Main.simulator.getCurrentFloorIndex() == 0
        );

        floorAboveButton.setDisable(
                Main.simulator.isPortalDrawing()
                        || Main.simulator.isFloorFieldDrawing()
                        || Main.simulator.getCurrentFloorIndex() == Main.simulator.getStation().getFloors().size() - 1
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
                "Floor #" + (Main.simulator.getCurrentFloorIndex() + 1)
        );
    }

    // Set the prompt text
    public void updatePromptText() {
        String operationModeText = "";
        String buildStateText = "";
        String amenityText = "";

        boolean testing = Simulator.OperationMode.TESTING.equals(Main.simulator.getOperationMode());
        boolean editingAll = Simulator.BuildState.EDITING_ALL.equals(Main.simulator.getBuildState());
        boolean noAmenity = Simulator.BuildSubcategory.NONE.equals(Main.simulator.getBuildSubcategory());

        switch (Main.simulator.getOperationMode()) {
            case BUILDING:
                operationModeText = "Building";

                break;
            case TESTING:
                operationModeText = "Testing";

                break;
        }

        switch (Main.simulator.getBuildState()) {
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

        switch (Main.simulator.getBuildSubcategory()) {
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
            saveSingleAmenityInFloor(Main.simulator.getCurrentAmenity());
        } else {
            saveAllAmenitiesInFloor(null);
        }
    }

    // Save the current amenity in a floor
    private void saveSingleAmenityInFloor(Amenity amenityToSave) {
        switch (Main.simulator.getBuildSubcategory()) {
            case STATION_ENTRANCE_EXIT:
                StationGate stationGateToEdit = (StationGate) amenityToSave;

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
                Security securityToEdit = (Security) amenityToSave;

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
                ElevatorShaft elevatorShaftToEdit = (ElevatorShaft) amenityToSave;

                // Retrieve portal components
                Portal lowerPortal = elevatorShaftToEdit.getLowerPortal();
                Portal upperPortal = elevatorShaftToEdit.getUpperPortal();

                // Apply the changes from the elevator shaft to these portals
                // Only the enabled option is reflected to these portals
                lowerPortal.setEnabled(elevatorShaftToEdit.isEnabled());
                upperPortal.setEnabled(elevatorShaftToEdit.isEnabled());

                break;
            case TICKET_BOOTH:
                TicketBoothTransactionArea ticketBoothTransactionAreaToEdit
                        = (TicketBoothTransactionArea) amenityToSave;

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
                Turnstile turnstileToEdit = (Turnstile) amenityToSave;

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
                    TrainDoor trainDoorToEdit = (TrainDoor) amenityToSave;

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
                            "Train boarding area addition failed",
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
    private void saveAllAmenitiesInFloor(PortalShaft portalShaft) {
        switch (Main.simulator.getBuildSubcategory()) {
            case STATION_ENTRANCE_EXIT:
                // Edit all station gates
                for (StationGate stationGateToEdit : Main.simulator.getCurrentFloor().getStationGates()) {
                    stationGateToEdit.setEnabled(
                            stationGateEnableCheckBox.isSelected()
                    );

                    stationGateToEdit.setChancePerSecond(
                            stationGateSpawnSpinner.getValue() / 100.0
                    );

                    stationGateToEdit.setStationGateMode(
                            stationGateModeChoiceBox.getValue()
                    );
                }

                break;
            case SECURITY:
                // Edit all ticket booths
                for (Security securityToEdit : Main.simulator.getCurrentFloor().getSecurities()) {
                    securityToEdit.setEnabled(
                            securityEnableCheckBox.isSelected()
                    );

                    securityToEdit.setBlockEntry(
                            securityBlockPassengerCheckBox.isSelected()
                    );

                    securityToEdit.setWaitingTime(
                            securityIntervalSpinner.getValue()
                    );
                }

                break;
            case STAIRS:
                break;
            case ESCALATOR:
                break;
            case ELEVATOR:
                // Edit all escalators
                ElevatorShaft elevatorShaftReference = (ElevatorShaft) portalShaft;

                for (ElevatorShaft elevatorShaftToEdit : Main.simulator.getStation().getElevatorShafts()) {
                    // Mirror each elevator shaft to the reference shaft
                    elevatorShaftToEdit.setEnabled(elevatorShaftReference.isEnabled());
                    elevatorShaftToEdit.setOpenDelayTime(elevatorShaftReference.getOpenDelayTime());
                    elevatorShaftToEdit.setDoorOpenTime(elevatorShaftReference.getDoorOpenTime());
                    elevatorShaftToEdit.setMoveTime(elevatorShaftReference.getMoveTime());
                    elevatorShaftToEdit.setElevatorDirection(elevatorShaftReference.getElevatorDirection());

                    // Retrieve portal components
                    Portal lowerPortal = elevatorShaftToEdit.getLowerPortal();
                    Portal upperPortal = elevatorShaftToEdit.getUpperPortal();

                    // Apply the changes from the elevator shaft to these portals
                    lowerPortal.setEnabled(elevatorShaftReference.isEnabled());
                    upperPortal.setEnabled(elevatorShaftReference.isEnabled());
                }

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
                            "Train boarding area addition failed",
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
            deleteSingleAmenityInFloor(Main.simulator.getCurrentAmenity());
        } else {
            deleteAllAmenitiesInFloor();
        }
    }

    // Delete the current amenity in a floor
    private void deleteSingleAmenityInFloor(Amenity amenityToDelete) {
        // If the amenity to be deleted is a ticket booth transaction area, there are some extra steps
        // to be made
        TicketBooth ticketBoothToDelete = null;

        if (amenityToDelete instanceof TicketBoothTransactionArea) {
            // Get the ticket booth from this patch
            ticketBoothToDelete
                    = ((TicketBoothTransactionArea) Main.simulator.getCurrentAmenity()).getTicketBooth();

            // Delete the ticket booth from the patch that contain it
            ticketBoothToDelete.getPatch().setAmenity(null);
        }

        // Delete this amenity from the patch that contains it
        // Portal shafts do not have a patch, so ignore if it is
        if (!(amenityToDelete instanceof PortalShaft)) {
            amenityToDelete.getPatch().setAmenity(null);
        }

        // Also delete this amenity from its list in this floor
        switch (Main.simulator.getBuildSubcategory()) {
            case STATION_ENTRANCE_EXIT:
//                if ()
                Main.simulator.getCurrentFloor().getStationGates().remove(
                        (StationGate) amenityToDelete
                );

                break;
            case SECURITY:
                Main.simulator.getCurrentFloor().getSecurities().remove(
                        (Security) amenityToDelete
                );

                break;
            case STAIRS:
                break;
            case ESCALATOR:
                break;
            case ELEVATOR:
                ElevatorShaft elevatorShaftToDelete = (ElevatorShaft) amenityToDelete;

                // Retrieve portal components
                ElevatorPortal upperPortal = (ElevatorPortal) elevatorShaftToDelete.getUpperPortal();
                ElevatorPortal lowerPortal = (ElevatorPortal) elevatorShaftToDelete.getLowerPortal();

                // Remove the portals from their patches in their respective floors
                if (Main.simulator.getFirstPortal() == null) {
                    // Portal drawing completed, deleting portal from portal shaft
                    if (upperPortal != null) {
                        upperPortal.getPatch().setAmenity(null);
                    }

                    if (lowerPortal != null) {
                        lowerPortal.getPatch().setAmenity(null);
                    }

                    // Remove elevator shaft
                    Main.simulator.getStation().getElevatorShafts().remove(
                            (ElevatorShaft) elevatorShaftToDelete
                    );
                } else {
                    // Portal drawing uncompleted, deleting portal from simulator
                    ElevatorPortal portal = (ElevatorPortal) Main.simulator.getFirstPortal();
                    portal.getPatch().setAmenity(null);
                }

                break;
            case TICKET_BOOTH:
                Main.simulator.getCurrentFloor().getTicketBooths().remove(
                        ticketBoothToDelete
                );

                break;
            case TURNSTILE:
                Main.simulator.getCurrentFloor().getTurnstiles().remove(
                        (Turnstile) amenityToDelete
                );

                break;
            case TRAIN_BOARDING_AREA:
                Main.simulator.getCurrentFloor().getTrainDoors().remove(
                        (TrainDoor) amenityToDelete
                );

                break;
            case WALL:
                break;
        }
    }

    // Delete all instances of an amenity in a floor
    private void deleteAllAmenitiesInFloor() {
        switch (Main.simulator.getBuildSubcategory()) {
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
                for (ElevatorShaft elevatorShaft : Main.simulator.getStation().getElevatorShafts()) {
                    // Retrieve portal components
                    ElevatorPortal upperPortal = (ElevatorPortal) elevatorShaft.getUpperPortal();
                    ElevatorPortal lowerPortal = (ElevatorPortal) elevatorShaft.getLowerPortal();

                    // Portal from portal shaft
                    if (upperPortal != null) {
                        upperPortal.getPatch().setAmenity(null);
                    }

                    if (lowerPortal != null) {
                        lowerPortal.getPatch().setAmenity(null);
                    }
                }

                // Remove elevator shafts
                Main.simulator.getStation().getElevatorShafts().clear();

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

    // Commence with adding a portal
    private void beginPortalDrawing(PortalSetupController portalSetupController) {
        if (portalSetupController instanceof ElevatorSetupController) {
            ElevatorShaft elevatorShaft
                    = (ElevatorShaft) portalSetupController.getWindowOutput().get(ElevatorSetupController.OUTPUT_KEY);

            Main.simulator.setProvisionalPortalShaft(elevatorShaft);
        }

        // Portal drawing shall now commence
        Main.simulator.setPortalDrawing(true);

        // Finally, reset the switch floor buttons
        resetSwitchFloorButtons();
    }

    // End adding a portal
    private void endPortalDrawing(boolean completed) {
        PortalShaft portalShaft = Main.simulator.getProvisionalPortalShaft();

        // If the portal drawing has been completed, register the portal shaft to its respective amenity list
        if (!completed) {
            // If the portal drawing sequence was not completed, discard the prematurely added
            // portals to maintain a consistent state
            deleteSingleAmenityInFloor(portalShaft);
        }

        // Discard the portal shaft
        // If the portal drawing was completed in its entirety, this portal shaft will live on in portals that have been
        // added despite having its reference removed from the simulator
        Main.simulator.setProvisionalPortalShaft(null);

        // Portal drawing is now disabled
        Main.simulator.setPortalDrawing(false);
        Main.simulator.setFirstPortalDrawn(false);
        Main.simulator.setFirstPortal(null);

        // Finally, reset the switch floor buttons
        resetSwitchFloorButtons();
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
                case "Wall":
                    buildSubcategory = Simulator.BuildSubcategory.WALL;

                    break;
            }
        }

        return buildSubcategory;
    }

    // Contains actions for building or editing
    public void buildOrEdit(Patch currentPatch) throws IOException {
        // Get the current operation mode, category, and subcategory
        Simulator.OperationMode operationMode = Main.simulator.getOperationMode();
        Simulator.BuildCategory buildCategory = Main.simulator.getBuildCategory();
        Simulator.BuildSubcategory buildSubcategory = Main.simulator.getBuildSubcategory();
        Simulator.BuildState buildState = Main.simulator.getBuildState();

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
                                    if (Main.simulator.currentAmenityProperty().isNull().get()) {
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
                                        goToAmenityControls(Main.simulator.getCurrentAmenity());

                                        // Then revisit this method as if that amenity was clicked
                                        buildOrEdit(currentPatch);
                                    }

                                    break;
                                case EDITING_ONE:
                                    // Only edit if there is already a station gate on that patch
                                    if (Main.simulator.getCurrentAmenity() instanceof StationGate) {
                                        Main.simulator.setCurrentClass(StationGate.class);

                                        StationGate stationGateToEdit
                                                = (StationGate) Main.simulator.getCurrentAmenity();

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
                                        if (Main.simulator.currentAmenityProperty().isNotNull().get()) {
                                            // If clicked on an existing amenity, switch to editing mode, then open that
                                            // amenity's controls
                                            goToAmenityControls(Main.simulator.getCurrentAmenity());

                                            // Then revisit this method as if that amenity was clicked
                                            buildOrEdit(currentPatch);
                                        }
                                    }

                                    break;
                                case EDITING_ALL:
                                    // No specific values need to be set here because all amenities will be edited
                                    // once save is clicked
                                    // If there is no amenity there, just do nothing
                                    if (Main.simulator.currentAmenityProperty().isNotNull().get()) {
                                        // If clicked on an existing amenity, switch to editing mode, then open that
                                        // amenity's controls
                                        goToAmenityControls(Main.simulator.getCurrentAmenity());

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
                                    if (Main.simulator.currentAmenityProperty().isNull().get()) {
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
                                        goToAmenityControls(Main.simulator.getCurrentAmenity());

                                        // Then revisit this method as if that amenity was clicked
                                        buildOrEdit(currentPatch);
                                    }

                                    break;
                                case EDITING_ONE:
                                    // When in adding floor fields mode, draw a floor field instead
                                    // Otherwise, just enable the controls in the sidebar
                                    if (!Main.simulator.isFloorFieldDrawing()) {
                                        // Only edit if there is already a security gate on that patch
                                        if (Main.simulator.getCurrentAmenity() instanceof Security) {
                                            Main.simulator.setCurrentClass(Security.class);

                                            Security securityToEdit
                                                    = (Security) Main.simulator.getCurrentAmenity();

                                            // Take note of this amenity as the one that will own the floor fields once
                                            // drawn
                                            Main.simulator.setCurrentFloorFieldTarget(securityToEdit);

                                            // Also take note of the current floor field state
                                            QueueingFloorField.FloorFieldState floorFieldState
                                                    = securityToEdit.getSecurityFloorFieldState();

                                            Main.simulator.setCurrentFloorFieldState(floorFieldState);

                                            // Set the forms to the proper position
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
                                            if (Main.simulator.currentAmenityProperty().isNotNull().get()) {
                                                // If clicked on an existing amenity, switch to editing mode, then open that
                                                // amenity's controls
                                                goToAmenityControls(Main.simulator.getCurrentAmenity());

                                                // Then revisit this method as if that amenity was clicked
                                                buildOrEdit(currentPatch);
                                            }
                                        }
                                    } else {
                                        // If there is an empty patch here, draw the floor field value
                                        if (Main.simulator.currentAmenityProperty().isNull().get()) {
                                            // Define the target and the floor field state
                                            Security target = (Security) Main.simulator.getCurrentFloorFieldTarget();

                                            // If a floor field value is supposed to be drawn, then go ahead and draw
                                            if (MainScreenController.normalFloorFieldController.getFloorFieldMode()
                                                    == NormalFloorFieldController.FloorFieldMode.DRAWING) {
                                                if (!QueueingFloorField.addFloorFieldValue(
                                                        currentPatch,
                                                        target,
                                                        Main.simulator.getCurrentFloorFieldState(),
                                                        MainScreenController.normalFloorFieldController.getIntensity()
                                                )) {
                                                    // Let the user know if the addition of the floor field value has
                                                    // failed
                                                    AlertController.showSimpleAlert(
                                                            "Floor field value addition failed",
                                                            "Failed to add a floor field value here",
                                                            "A floor field may only have a single patch with a"
                                                                    + " value of 1.0.",
                                                            Alert.AlertType.ERROR
                                                    );
                                                }
                                            } else {
                                                // If a floor field value is supposed to be deleted, then go ahead and
                                                // delete it
                                                QueueingFloorField.deleteFloorFieldValue(
                                                        currentPatch,
                                                        target,
                                                        Main.simulator.getCurrentFloorFieldState()
                                                );
                                            }
                                        } else {
                                            // If it is a different amenity, turn off floor fields mode
                                            endFloorFieldDrawing(true);

                                            // Switch to editing mode, then open that amenity's controls
                                            goToAmenityControls(Main.simulator.getCurrentAmenity());

                                            // Then revisit this method as if that amenity was clicked
                                            buildOrEdit(currentPatch);
                                        }
                                    }

                                    break;
                                case EDITING_ALL:
                                    // No specific values need to be set here because all amenities will be edited
                                    // once save is clicked
                                    // If there is no amenity there, just do nothing
                                    if (Main.simulator.currentAmenityProperty().isNotNull().get()) {
                                        // If clicked on an existing amenity, switch to editing mode, then open that
                                        // amenity's controls
                                        goToAmenityControls(Main.simulator.getCurrentAmenity());

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
                            switch (buildState) {
                                case DRAWING:
                                    // Only add if the current patch doesn't already have an amenity
                                    if (Main.simulator.currentAmenityProperty().isNull().get()) {
                                        Main.simulator.setCurrentClass(ElevatorPortal.class);

                                        // If a first portal has already been added, add the second portal this
                                        // click
                                        if (!Main.simulator.isFirstPortalDrawn()) {
                                            // Only add an portal when there are multiple floors
                                            if (Main.simulator.getStation().getFloors().size() > 1) {
                                                // If the user has clicked on a patch without the portal being setup yet,
                                                // show the setup first, then automatically put the first portal where the
                                                // user clicked
                                                if (!Main.simulator.isPortalDrawing()) {
                                                    // Display the portal setup prompt
                                                    FXMLLoader loader = ScreenController.getLoader(
                                                            getClass(),
                                                            "/com/crowdsimulation/view" +
                                                                    "/ElevatorSetupInterface.fxml");
                                                    Parent root = loader.load();

                                                    ElevatorSetupController elevatorSetupController;

                                                    elevatorSetupController = loader.getController();
                                                    elevatorSetupController.setElements();

                                                    // Show the window
                                                    elevatorSetupController.showWindow(
                                                            root,
                                                            "Elevator setup",
                                                            true,
                                                            false
                                                    );

                                                    // Only proceed when this window is closed through the proceed action
                                                    if (elevatorSetupController.isClosedWithAction()) {
                                                        beginPortalDrawing(elevatorSetupController);
                                                    }
                                                }

                                                // Only continue when the portal setup has been completed
                                                if (Main.simulator.isPortalDrawing()) {
                                                    // Setup has already been shown, so we may now draw the first portal in
                                                    // peace
                                                    // Prepare the first portal that will be placed on this floor
                                                    ElevatorPortal.ElevatorPortalFactory elevatorPortalFactory
                                                            = new ElevatorPortal.ElevatorPortalFactory();

                                                    ElevatorPortal elevatorPortalToAdd = elevatorPortalFactory.create(
                                                            currentPatch,
                                                            Main.simulator.getProvisionalPortalShaft().isEnabled(),
                                                            Main.simulator.getCurrentFloor(),
                                                            Main.simulator.getProvisionalPortalShaft()
                                                    );

                                                    // Set the amenity on that patch
                                                    currentPatch.setAmenity(elevatorPortalToAdd);

                                                    // The first portal has now been drawn
                                                    Main.simulator.setFirstPortalDrawn(true);
                                                    Main.simulator.setFirstPortal(elevatorPortalToAdd);

                                                    // Redraw the interface to briefly show the newly added elevator
                                                    drawInterface(false);

                                                    // Show the window for choosing the floor where the next portal will be
                                                    FXMLLoader loader = ScreenController.getLoader(
                                                            getClass(),
                                                            "/com/crowdsimulation/view" +
                                                                    "/PortalFloorSelectorInterface.fxml");
                                                    Parent root = loader.load();

                                                    PortalFloorSelectorController portalFloorSelectorController
                                                            = loader.getController();
                                                    portalFloorSelectorController.setElements();

                                                    portalFloorSelectorController.showWindow(
                                                            root,
                                                            "Choose the floor of the second elevator",
                                                            true,
                                                            true
                                                    );

                                                    // Only continue when the floor selection has been completed
                                                    if (portalFloorSelectorController.isClosedWithAction()) {
                                                        // A floor has already been chosen, now retrieve that floor and go there
                                                        Floor chosenFloor = (Floor) portalFloorSelectorController
                                                                .getWindowOutput()
                                                                .get(PortalFloorSelectorController.OUTPUT_KEY);

                                                        // After the chosen floor was selected, we may now set the
                                                        // provisional portal shaft with one of the portals, now that we
                                                        // know which one is the portal located in the upper or lower
                                                        // floor
                                                        List<Floor> floors = Main.simulator.getStation().getFloors();

                                                        // If the current floor is lower than the chosen floor, this
                                                        // current floor will be the lower portal
                                                        if (floors.indexOf(Main.simulator.getCurrentFloor())
                                                                < floors.indexOf(chosenFloor)) {
                                                            Main.simulator.getProvisionalPortalShaft().setLowerPortal(
                                                                    elevatorPortalToAdd
                                                            );
                                                        } else {
                                                            Main.simulator.getProvisionalPortalShaft().setUpperPortal(
                                                                    elevatorPortalToAdd
                                                            );
                                                        }

                                                        // Switch to that floor
                                                        switchFloor(chosenFloor);

                                                        // Prompt the user that it is now time to draw the second portal
                                                        AlertController.showSimpleAlert(
                                                                "Add second elevator",
                                                                "Draw the second elevator",
                                                                "After closing this window, please draw the" +
                                                                        " second elevator on this floor. Click X to" +
                                                                        " cancel this operation.",
                                                                Alert.AlertType.INFORMATION
                                                        );
                                                    } else {
                                                        // Cancel portal adding
                                                        // Also delete the earlier added portal shafts and portals
                                                        endPortalDrawing(false);
                                                    }
                                                }
                                            } else {
                                                AlertController.showSimpleAlert(
                                                        "Elevator addition failed",
                                                        "Unable to add elevator",
                                                        "You may only add elevators when there are more than one floors in the station.",
                                                        Alert.AlertType.INFORMATION
                                                );
                                            }
                                        } else {
                                            // Prepare the second portal that will be placed on this floor
                                            ElevatorPortal.ElevatorPortalFactory elevatorPortalFactory
                                                    = new ElevatorPortal.ElevatorPortalFactory();

                                            ElevatorPortal elevatorPortalToAdd = elevatorPortalFactory.create(
                                                    currentPatch,
                                                    Main.simulator.getProvisionalPortalShaft().isEnabled(),
                                                    Main.simulator.getCurrentFloor(),
                                                    Main.simulator.getProvisionalPortalShaft()
                                            );

                                            // Set the amenity on that patch
                                            currentPatch.setAmenity(elevatorPortalToAdd);

                                            // If the upper portal has already been set, then this portal will be the
                                            // lower one (and vice versa)
                                            if (Main.simulator.getProvisionalPortalShaft().getUpperPortal() == null) {
                                                Main.simulator.getProvisionalPortalShaft().setUpperPortal(
                                                        elevatorPortalToAdd
                                                );
                                            } else {
                                                Main.simulator.getProvisionalPortalShaft().setLowerPortal(
                                                        elevatorPortalToAdd
                                                );
                                            }

                                            // Register the provisional shaft to the station
                                            Main.simulator.getStation().getElevatorShafts().add(
                                                    (ElevatorShaft) Main.simulator.getProvisionalPortalShaft()
                                            );

                                            // Finish adding the portal
                                            endPortalDrawing(true);

                                            // Again, briefly redraw the interface to let the user have a glimpse at
                                            // the newly added elevator
                                            drawInterface(false);

                                            // Let the user know that portal addition has been successful
                                            AlertController.showSimpleAlert(
                                                    "Elevator addition successful",
                                                    "Elevator successfully added",
                                                    "The elevator has been successfully added.",
                                                    Alert.AlertType.INFORMATION
                                            );
                                        }
                                    } else {
                                        // If clicked on an existing amenity, switch to editing mode, then open that
                                        // amenity's controls
                                        goToAmenityControls(Main.simulator.getCurrentAmenity());

                                        // Then revisit this method as if that amenity was clicked
                                        buildOrEdit(currentPatch);
                                    }

                                    break;
                                case EDITING_ONE:
                                    // When in adding floor fields mode, draw a floor field instead
                                    // Otherwise, just enable the controls in the sidebar
                                    if (!Main.simulator.isFloorFieldDrawing()) {
                                        // Only edit if there is already a elevator on that patch
                                        if (Main.simulator.getCurrentAmenity() instanceof ElevatorPortal) {
                                            Main.simulator.setCurrentClass(ElevatorPortal.class);

                                            ElevatorPortal elevatorPortalToEdit
                                                    = (ElevatorPortal) Main.simulator.getCurrentAmenity();

                                            // Take note of this amenity as the one that will own the floor fields once
                                            // drawn
                                            Main.simulator.setCurrentFloorFieldTarget(elevatorPortalToEdit);

                                            // Also take note of the current floor field state
                                            QueueingFloorField.FloorFieldState floorFieldState
                                                    = elevatorPortalToEdit.getElevatorPortalFloorFieldState();

                                            Main.simulator.setCurrentFloorFieldState(floorFieldState);

                                            // Do nothing afterwards, editing is handled by the sidebar button
                                        } else {
                                            // If there is no amenity there, just do nothing
                                            if (Main.simulator.currentAmenityProperty().isNotNull().get()) {
                                                // If clicked on an existing amenity, switch to editing mode, then open that
                                                // amenity's controls
                                                goToAmenityControls(Main.simulator.getCurrentAmenity());

                                                // Then revisit this method as if that amenity was clicked
                                                buildOrEdit(currentPatch);
                                            }
                                        }
                                    } else {
                                        // If there is an empty patch here, draw the floor field value
                                        if (Main.simulator.currentAmenityProperty().isNull().get()) {
                                            // Define the target and the floor field state
                                            ElevatorPortal target =
                                                    (ElevatorPortal) Main.simulator.getCurrentFloorFieldTarget();

                                            // If a floor field value is supposed to be drawn, then go ahead and draw
                                            if (MainScreenController.normalFloorFieldController.getFloorFieldMode()
                                                    == NormalFloorFieldController.FloorFieldMode.DRAWING) {
                                                if (!QueueingFloorField.addFloorFieldValue(
                                                        currentPatch,
                                                        target,
                                                        Main.simulator.getCurrentFloorFieldState(),
                                                        MainScreenController.normalFloorFieldController.getIntensity()
                                                )) {
                                                    // Let the user know if the addition of the floor field value has
                                                    // failed
                                                    AlertController.showSimpleAlert(
                                                            "Floor field value addition failed",
                                                            "Failed to add a floor field value here",
                                                            "A floor field may only have a single patch with a"
                                                                    + " value of 1.0.",
                                                            Alert.AlertType.ERROR
                                                    );
                                                }
                                            } else {
                                                // If a floor field value is supposed to be deleted, then go ahead and
                                                // delete it
                                                QueueingFloorField.deleteFloorFieldValue(
                                                        currentPatch,
                                                        target,
                                                        Main.simulator.getCurrentFloorFieldState()
                                                );
                                            }
                                        } else {
                                            // If it is a different amenity, turn off floor fields mode
                                            endFloorFieldDrawing(true);

                                            // Switch to editing mode, then open that amenity's controls
                                            goToAmenityControls(Main.simulator.getCurrentAmenity());

                                            // Then revisit this method as if that amenity was clicked
                                            buildOrEdit(currentPatch);
                                        }
                                    }

                                    break;
                                case EDITING_ALL:
                                    // No specific values need to be set here because all amenities will be edited
                                    // once save is clicked
                                    // If there is no amenity there, just do nothing
                                    if (Main.simulator.currentAmenityProperty().isNotNull().get()) {
                                        // If clicked on an existing amenity, switch to editing mode, then open that
                                        // amenity's controls
                                        goToAmenityControls(Main.simulator.getCurrentAmenity());

                                        // Then revisit this method as if that amenity was clicked
                                        buildOrEdit(currentPatch);
                                    }

                                    break;
                            }

                            break;
                        case TICKET_BOOTH:
                            switch (buildState) {
                                case DRAWING:
                                    // Only add if the current patch and the extra patch doesn't already have an amenity
                                    if (Main.simulator.currentAmenityProperty().isNull().get()) {
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
                                        goToAmenityControls(Main.simulator.getCurrentAmenity());

                                        // Then revisit this method as if that amenity was clicked
                                        buildOrEdit(currentPatch);
                                    }

                                    break;
                                case EDITING_ONE:
                                    // When in adding floor fields mode, draw a floor field instead
                                    // Otherwise, just enable the controls in the sidebar
                                    if (!Main.simulator.isFloorFieldDrawing()) {
                                        // Only edit if there is already a ticket booth or its transaction area on that
                                        // patch
                                        if (Main.simulator.getCurrentAmenity() instanceof TicketBooth
                                                || Main.simulator.getCurrentAmenity()
                                                instanceof TicketBoothTransactionArea) {
                                            Main.simulator.setCurrentClass(TicketBoothTransactionArea.class);

                                            TicketBoothTransactionArea ticketBoothTransactionAreaToEdit;

                                            // See whether this patch is a ticket booth or its transaction area
                                            // Resolve for the transaction area
                                            if (Main.simulator.getCurrentAmenity()
                                                    instanceof TicketBooth) {
                                                ticketBoothTransactionAreaToEdit
                                                        = ((TicketBooth) Main.simulator.getCurrentAmenity())
                                                        .getTicketBoothTransactionArea();

                                                Main.simulator.setCurrentAmenity(ticketBoothTransactionAreaToEdit);
                                            } else {
                                                ticketBoothTransactionAreaToEdit
                                                        = (TicketBoothTransactionArea)
                                                        Main.simulator.getCurrentAmenity();
                                            }

                                            // Take note of this amenity as the one that will own the floor fields once
                                            // drawn
                                            Main.simulator.setCurrentFloorFieldTarget(ticketBoothTransactionAreaToEdit);

                                            // Also take note of the current floor field state
                                            QueueingFloorField.FloorFieldState floorFieldState
                                                    = ticketBoothTransactionAreaToEdit
                                                    .getTicketBoothTransactionAreaFloorFieldState();

                                            Main.simulator.setCurrentFloorFieldState(floorFieldState);

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
                                            if (Main.simulator.currentAmenityProperty().isNotNull().get()) {
                                                // If clicked on an existing amenity, switch to editing mode, then open that
                                                // amenity's controls
                                                goToAmenityControls(Main.simulator.getCurrentAmenity());

                                                // Then revisit this method as if that amenity was clicked
                                                buildOrEdit(currentPatch);
                                            }
                                        }
                                    } else {
                                        // If there is an empty patch here, draw the floor field value
                                        if (Main.simulator.currentAmenityProperty().isNull().get()) {
                                            // Define the target and the floor field state
                                            TicketBoothTransactionArea target
                                                    = (TicketBoothTransactionArea) Main.simulator
                                                    .getCurrentFloorFieldTarget();

                                            // If a floor field value is supposed to be drawn, then go ahead and draw
                                            if (MainScreenController.normalFloorFieldController.getFloorFieldMode()
                                                    == NormalFloorFieldController.FloorFieldMode.DRAWING) {
                                                if (!QueueingFloorField.addFloorFieldValue(
                                                        currentPatch,
                                                        target,
                                                        Main.simulator.getCurrentFloorFieldState(),
                                                        MainScreenController.normalFloorFieldController.getIntensity()
                                                )) {
                                                    // Let the user know if the addition of the floor field value has
                                                    // failed
                                                    AlertController.showSimpleAlert(
                                                            "Floor field value addition failed",
                                                            "Failed to add a floor field value here",
                                                            "A floor field may only have a single patch with a"
                                                                    + " value of 1.0.",
                                                            Alert.AlertType.ERROR
                                                    );
                                                }
                                            } else {
                                                // If a floor field value is supposed to be deleted, then go ahead and
                                                // delete it
                                                QueueingFloorField.deleteFloorFieldValue(
                                                        currentPatch,
                                                        target,
                                                        Main.simulator.getCurrentFloorFieldState()
                                                );
                                            }
                                        } else {
                                            // If it is a different amenity, turn off floor fields mode
                                            endFloorFieldDrawing(true);

                                            // Switch to editing mode, then open that amenity's controls
                                            goToAmenityControls(Main.simulator.getCurrentAmenity());

                                            // Then revisit this method as if that amenity was clicked
                                            buildOrEdit(currentPatch);
                                        }
                                    }

                                    break;
                                case EDITING_ALL:
                                    // No specific values need to be set here because all amenities will be edited
                                    // once save is clicked

                                    // If there is no amenity there, just do nothing
                                    if (Main.simulator.currentAmenityProperty().isNotNull().get()) {
                                        // If clicked on an existing amenity, switch to editing mode, then open that
                                        // amenity's controls
                                        goToAmenityControls(Main.simulator.getCurrentAmenity());

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
                                    if (Main.simulator.currentAmenityProperty().isNull().get()) {
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
                                        goToAmenityControls(Main.simulator.getCurrentAmenity());

                                        // Then revisit this method as if that amenity was clicked
                                        buildOrEdit(currentPatch);
                                    }

                                    break;
                                case EDITING_ONE:
                                    // Only edit if there is already a turnstile on that patch
                                    if (Main.simulator.getCurrentAmenity() instanceof Turnstile) {
                                        Main.simulator.setCurrentClass(Turnstile.class);

                                        Turnstile turnstileToEdit
                                                = (Turnstile) Main.simulator.getCurrentAmenity();

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
                                        if (Main.simulator.currentAmenityProperty().isNotNull().get()) {
                                            // If clicked on an existing amenity, switch to editing mode, then open that
                                            // amenity's controls
                                            goToAmenityControls(Main.simulator.getCurrentAmenity());

                                            // Then revisit this method as if that amenity was clicked
                                            buildOrEdit(currentPatch);
                                        }
                                    }

                                    break;
                                case EDITING_ALL:
                                    // No specific values need to be set here because all amenities will be edited
                                    // once save is clicked
                                    // If there is no amenity there, just do nothing
                                    if (Main.simulator.currentAmenityProperty().isNotNull().get()) {
                                        // If clicked on an existing amenity, switch to editing mode, then open that
                                        // amenity's controls
                                        goToAmenityControls(Main.simulator.getCurrentAmenity());

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
                                    if (Main.simulator.currentAmenityProperty().isNull().get()) {
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
                                                    "Train boarding area addition failed",
                                                    "No carriages selected",
                                                    "Please select the train carriage(s) supported by" +
                                                            " the train boarding area to be added",
                                                    Alert.AlertType.ERROR
                                            );
                                        }
                                    } else {
                                        // If clicked on an existing amenity, switch to editing mode, then open that
                                        // amenity's controls
                                        goToAmenityControls(Main.simulator.getCurrentAmenity());

                                        // Then revisit this method as if that amenity was clicked
                                        buildOrEdit(currentPatch);
                                    }

                                    break;
                                case EDITING_ONE:
                                    // When in adding floor fields mode, draw a floor field instead
                                    // Otherwise, just enable the controls in the sidebar
                                    if (!Main.simulator.isFloorFieldDrawing()) {
                                        // Only edit if there is already a train door on that patch
                                        if (Main.simulator.getCurrentAmenity() instanceof TrainDoor) {
                                            Main.simulator.setCurrentClass(TrainDoor.class);

                                            TrainDoor trainDoorToEdit
                                                    = (TrainDoor) Main.simulator.getCurrentAmenity();

                                            // Take note of this amenity as the one that will own the floor fields once
                                            // drawn
                                            Main.simulator.setCurrentFloorFieldTarget(trainDoorToEdit);

                                            // Also take note of the current floor field state
                                            QueueingFloorField.FloorFieldState floorFieldState
                                                    = trainDoorToEdit.getTrainDoorFloorFieldState();

                                            Main.simulator.setCurrentFloorFieldState(floorFieldState);

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
                                            if (Main.simulator.currentAmenityProperty().isNotNull().get()) {
                                                // If clicked on an existing amenity, switch to editing mode, then open that
                                                // amenity's controls
                                                goToAmenityControls(Main.simulator.getCurrentAmenity());

                                                // Then revisit this method as if that amenity was clicked
                                                buildOrEdit(currentPatch);
                                            }
                                        }
                                    } else {
                                        // If there is an empty patch here, draw the floor field value
                                        if (Main.simulator.currentAmenityProperty().isNull().get()) {
                                            // Define the target and the floor field state
                                            TrainDoor target = (TrainDoor) Main.simulator.getCurrentFloorFieldTarget();

                                            // If a floor field value is supposed to be drawn, then go ahead and draw
                                            if (MainScreenController.normalFloorFieldController.getFloorFieldMode()
                                                    == NormalFloorFieldController.FloorFieldMode.DRAWING) {
                                                if (!QueueingFloorField.addFloorFieldValue(
                                                        currentPatch,
                                                        target,
                                                        Main.simulator.getCurrentFloorFieldState(),
                                                        MainScreenController.normalFloorFieldController.getIntensity()
                                                )) {
                                                    // Let the user know if the addition of the floor field value has
                                                    // failed
                                                    AlertController.showSimpleAlert(
                                                            "Floor field value addition failed",
                                                            "Failed to add a floor field value here",
                                                            "A floor field may only have a single patch with a"
                                                                    + " value of 1.0.",
                                                            Alert.AlertType.ERROR
                                                    );
                                                }
                                            } else {
                                                // If a floor field value is supposed to be deleted, then go ahead and
                                                // delete it
                                                QueueingFloorField.deleteFloorFieldValue(
                                                        currentPatch,
                                                        target,
                                                        Main.simulator.getCurrentFloorFieldState()
                                                );
                                            }
                                        } else {
                                            // If it is a different amenity, turn off floor fields mode
                                            endFloorFieldDrawing(true);

                                            // Switch to editing mode, then open that amenity's controls
                                            goToAmenityControls(Main.simulator.getCurrentAmenity());

                                            // Then revisit this method as if that amenity was clicked
                                            buildOrEdit(currentPatch);
                                        }
                                    }

                                    break;
                                case EDITING_ALL:
                                    // No specific values need to be set here because all amenities will be edited
                                    // once save is clicked
                                    // If there is no amenity there, just do nothing
                                    if (Main.simulator.currentAmenityProperty().isNotNull().get()) {
                                        // If clicked on an existing amenity, switch to editing mode, then open that
                                        // amenity's controls
                                        goToAmenityControls(Main.simulator.getCurrentAmenity());

                                        // Then revisit this method as if that amenity was clicked
                                        buildOrEdit(currentPatch);
                                    }

                                    break;
                            }

                            break;
                        case WALL:
                            break;
                        case NONE:
                            if (Main.simulator.currentAmenityProperty().isNotNull().get()) {
                                // If clicked on an existing amenity, switch to editing mode, then open that
                                // amenity's controls
                                goToAmenityControls(Main.simulator.getCurrentAmenity());

                                // Then revisit this method as if that amenity was clicked
                                buildOrEdit(currentPatch);
                            }

                            break;
                    }

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
                        || amenity instanceof ElevatorPortal) {
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
            case WALLS:
                buildTabPane.getSelectionModel().select(4);

                accordion = (Accordion) buildTabPane.getTabs().get(4).getContent();
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
