package com.crowdsimulation.controller.screen.main;

import com.crowdsimulation.controller.Main;
import com.crowdsimulation.controller.graphics.GraphicsController;
import com.crowdsimulation.controller.screen.ScreenController;
import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.Station;
import com.crowdsimulation.model.core.environment.station.patch.Patch;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.Amenity;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.obstacle.TicketBooth;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.StationGate;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.TrainDoor;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.Elevator;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.EscalatorPortal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.StairPortal;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.Security;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.TicketBoothTransactionArea;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.Turnstile;
import com.crowdsimulation.model.simulator.Simulator;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class MainScreenController extends ScreenController {
    // Logistical variables
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
    // Entrances and exits
    // Station entrance/exit
    @FXML
    private ChoiceBox<Simulator.BuildState> stationGateBuildModeChoiceBox;

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
    private ChoiceBox<Simulator.BuildState> securityBuildModeChoiceBox;

    @FXML
    private CheckBox securityEnableCheckBox;

    @FXML
    private CheckBox securityBlockPassengerCheckBox;

    @FXML
    private Label securityIntervalLabel;

    @FXML
    private Spinner<Integer> securityIntervalSpinner;

    @FXML
    private Button deleteSecurityButton;

    // Test tab variables
    // Simulation controls
    @FXML
    private ToggleButton playButton;

    // Passenger controls
    // Platform controls

    @FXML
    public void initialize() {
        // Initialize all UI elements and label references
        UIInitializer.initializeBuildTab(
                stationGateBuildModeChoiceBox,
                stationGateEnableCheckBox,
                stationGateModeLabel,
                stationGateModeChoiceBox,
                stationGateSpawnLabel,
                stationGateSpawnSpinner,
                saveStationGateButton,
                deleteStationGateButton,
                securityBuildModeChoiceBox,
                securityEnableCheckBox,
                securityBlockPassengerCheckBox,
                securityIntervalLabel,
                securityIntervalSpinner,
                null,
                deleteSecurityButton,
                buildTabPane
        );

        UIInitializer.initializeTestTab(
                playButton
        );
    }

    @FXML
    public void loadStationAction() {
        // TODO: Display a dialog box for loading a station from a file
        Station station = new Station();

        // Set the station to be worked on to that station
        Main.simulator.setStation(station);

        // Set the current floor to the lowermost floor of the station
        Main.simulator.setCurrentFloor(station.getFloors().get(0));

        // Draw the interface
        drawInterface(Main.simulator.getCurrentFloor(), true);
    }

    @FXML
    // Edit a station gate
    public void saveStationGateAction() {
        // Edit just one station gate
        if (Main.simulator.getBuildState().get() == Simulator.BuildState.EDITING_ONE) {
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
        } else {
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
        }
    }

    @FXML
    public void deleteStationGateAction() {
        // Remove just one station gate
        if (Main.simulator.getBuildState().get() == Simulator.BuildState.EDITING_ONE) {
            // Delete this amenity from the patch that contains it
            Main.simulator.getCurrentAmenity().get().getPatch().setAmenity(null);

            // Also delete this amenity from the list of station gates in this floor
            Main.simulator.getCurrentFloor().getStationGates().remove(
                    Main.simulator.getCurrentAmenity().get()
            );
        } else {
            // Remove all station gates
            for (StationGate stationGate : Main.simulator.getCurrentFloor().getStationGates()) {
                // Delete this amenity from the patch that contains it
                stationGate.getPatch().setAmenity(null);
            }

            // Clear the list of station gates in this floor
            Main.simulator.getCurrentFloor().getStationGates().clear();
        }

        // Hence, the simulator won't have this amenity anymore
        Main.simulator.setCurrentAmenity(null);
        Main.simulator.setCurrentClass(null);

        // Redraw the interface
        drawInterface(Main.simulator.getCurrentFloor(), false);
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
                buildCategory = Simulator.BuildCategory.FLOOR_FIELDS;

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
                case "Queueing floor field":
                    buildSubcategory = Simulator.BuildSubcategory.QUEUEING_FLOOR_FIELD;

                    break;
                case "Platform floor field":
                    buildSubcategory = Simulator.BuildSubcategory.PLATFORM_FLOOR_FIELD;

                    break;
                case "Stair floor field":
                    buildSubcategory = Simulator.BuildSubcategory.STAIR_FLOOR_FIELD;

                    break;
                case "Elevator floor field":
                    buildSubcategory = Simulator.BuildSubcategory.ELEVATOR_FLOOR_FIELD;

                    break;
            }
        }

        return buildSubcategory;
    }

    // Draw the interface
    private void drawInterface(Floor currentFloor, boolean drawListeners) {
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

    // Contains actions for building or editing
    public void buildOrEdit(Patch currentPatch) {
        // Get the current operation mode, category, and subcategory
        Simulator.OperationMode operationMode = Main.simulator.getOperationMode().get();
        Simulator.BuildCategory buildCategory = Main.simulator.getBuildCategory();
        Simulator.BuildSubcategory buildSubcategory = Main.simulator.getBuildSubcategory();
        Simulator.BuildState buildState = Main.simulator.getBuildState().get();

        // If the operation mode is building, the user either wants to draw or edit (one or all)
        if (operationMode == Simulator.OperationMode.BUILDING) {
            // Draw depending on the category and subcategory
            switch (buildCategory) {
                case ENTRANCES_AND_EXITS:
                case STAIRS_AND_ELEVATORS:
                case CONCOURSE_AMENITIES:
                case PLATFORM_AMENITIES:
                    switch (buildSubcategory) {
                        case STATION_ENTRANCE_EXIT:
                            Main.simulator.setCurrentClass(StationGate.class);

                            switch (buildState) {
                                case DRAWING:
                                    setEnableStationGateControls(true);

                                    // Only add if the current patch doesn't already have an amenity
                                    if (Main.simulator.getCurrentAmenity().isNull().get()) {
                                        // Prepare the amenity that will be placed on the station
                                        StationGate stationGateToAdd = new StationGate(
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
                                    }

                                    break;
                                case EDITING_ONE:
                                    // Only edit if there is already a station gate on that patch
                                    if (Main.simulator.getCurrentAmenity().get() instanceof StationGate) {
                                        setEnableStationGateControls(true);

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
                                        setEnableStationGateControls(false);
                                    }

                                    break;
                                case EDITING_ALL:
                                    // No specific values need to be set here because all station gates will be edited
                                    // once save is clicked
                                    setEnableStationGateControls(true);

                                    if (Main.simulator.getCurrentAmenity().isNotNull().get()) {
                                        // If clicked on an existing amenity, switch to editing mode, then open that
                                        // amenity's controls
                                        goToAmenityControls(Main.simulator.getCurrentAmenity().get());
                                    }

                                    break;
                            }

                            break;
                        case SECURITY:
/*                            // Prepare the amenity that will be placed on the station
                            Amenity newAmenity = null;

                            newAmenity = new Security(
                                    securityEnableCheckBox.isSelected(),
                                    securityIntervalSpinner.getValue(),
                                    securityBlockPassengerCheckBox.isSelected()
                            );

                            // Set the amenity on that patch
                            currentPatch.setAmenity(newAmenity);*/

                            break;
                        case STAIRS:
                            break;
                        case ESCALATOR:
                            break;
                        case ELEVATOR:
                            break;
                        case TICKET_BOOTH:
                            break;
                        case TURNSTILE:
                            break;
                        case TRAIN_BOARDING_AREA:
                            break;
                    }

                    break;
                case FLOOR_FIELDS:
                    break;
            }
        } else {
            // If the operation mode is testing, the user wants to edit (one or all)
            System.out.println("edit");
        }
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

    // Enable/disable the security gate controls
    public void setEnableStationGateControls(boolean enable) {
        if (enable) {
            stationGateEnableCheckBox.setDisable(false);
            stationGateModeChoiceBox.setDisable(false);
            stationGateSpawnSpinner.setDisable(false);
        } else {
            stationGateEnableCheckBox.setDisable(true);
            stationGateModeChoiceBox.setDisable(true);
            stationGateSpawnSpinner.setDisable(true);
        }
    }

    // Given an amenity, open the subcategory (category) and titled pane (subcategory) that contains that amenity
    // in the interface
    public void goToAmenityControls(Amenity amenity) {
        Simulator.BuildCategory buildCategory = null;
        Simulator.BuildSubcategory buildSubcategory = null;

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

                        stationGateBuildModeChoiceBox.getSelectionModel().select(Simulator.BuildState.EDITING_ONE);

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

                accordion = (Accordion) buildTabPane.getTabs().get(1).getContent();
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

                accordion = (Accordion) buildTabPane.getTabs().get(1).getContent();
                titledPanes = accordion.getPanes();

                titledPanes.get(0).setExpanded(true);

                break;
            case FLOOR_FIELDS:
                buildTabPane.getSelectionModel().select(4);

                // TODO: Deal with floor fields
/*                switch (buildSubcategory) {
                    case QUEUEING_FLOOR_FIELD:
                        break;
                    case PLATFORM_FLOOR_FIELD:
                        break;
                    case STAIR_FLOOR_FIELD:
                        break;
                    case ELEVATOR_FLOOR_FIELD:
                        break;
                }*/

                break;
        }
    }
}
