package com.crowdsimulation.controller.screen.main.service;

import com.crowdsimulation.controller.Main;
import com.crowdsimulation.controller.screen.main.MainScreenController;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.StationGate;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.TrainDoor;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.TicketBoothTransactionArea;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.blockable.Turnstile;
import com.crowdsimulation.model.simulator.Simulator;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

import java.util.List;

public class InitializeMainScreenService extends InitializeScreenService {
    private static final ObservableList<Simulator.BuildState> BUILD_MODE_CHOICEBOX_ITEMS;

    // Binding variables
    public static BooleanBinding SAVE_DELETE_BINDING;
    public static BooleanBinding SPECIFIC_CONTROLS_BINDING;
    public static BooleanBinding DRAW_ONLY_BINDING;
    public static BooleanBinding ADD_FLOOR_FIELD_BINDING;
    public static BooleanBinding PORTAL_DRAW_IN_PROGRESS_BINDING;
    public static BooleanBinding FLOOR_FIELD_DRAW_IN_PROGRESS_BINDING;
    public static BooleanBinding AMENITY_NOT_EQUALS_SUBCATEGORY;

    static {
        BUILD_MODE_CHOICEBOX_ITEMS = FXCollections.observableArrayList(
                Simulator.BuildState.DRAWING,
                Simulator.BuildState.EDITING_ONE,
                Simulator.BuildState.EDITING_ALL
        );

        AMENITY_NOT_EQUALS_SUBCATEGORY = Bindings.createBooleanBinding(() -> {
                    if (Bindings.isNotNull(Main.simulator.currentAmenityProperty()).get()
                            && Bindings.isNotNull(Main.simulator.buildSubcategoryClassProperty()).get()) {
                        return !Main.simulator.buildSubcategoryClassProperty().get().equals(
                                Main.simulator.getCurrentAmenity().getClass()
                        );
                    } else {
                        return false;
                    }
                }, Main.simulator.currentAmenityProperty(), Main.simulator.buildSubcategoryClassProperty()
        );
/*                Bindings.and(
                        Bindings.isNotNull(Main.simulator.currentAmenityProperty()),
                        Bindings.notEqual(
                                Main.simulator.buildSubcategoryClassProperty(),
                                Main.simulator.getCurrentAmenity().getClass()
                        )
                );*/

        InitializeMainScreenService.SAVE_DELETE_BINDING =
                Bindings.or(
                        Bindings.equal(
                                Main.simulator.buildStateProperty(),
                                Simulator.BuildState.DRAWING
                        ),
                        Bindings.and(
                                Bindings.or(
                                        Bindings.isNull(
                                                Main.simulator.currentAmenityProperty()
                                        ),
                                        AMENITY_NOT_EQUALS_SUBCATEGORY),
                                Bindings.notEqual(
                                        Main.simulator.buildStateProperty(),
                                        Simulator.BuildState.EDITING_ALL
                                )
                        )
                );

        InitializeMainScreenService.SPECIFIC_CONTROLS_BINDING =
                Bindings.and(
                        Bindings.equal(
                                Main.simulator.buildStateProperty(),
                                Simulator.BuildState.EDITING_ONE
                        ),
                        Bindings.or(
                                Bindings.isNull(
                                        Main.simulator.currentAmenityProperty()
                                ),
                                AMENITY_NOT_EQUALS_SUBCATEGORY)
                );

        InitializeMainScreenService.DRAW_ONLY_BINDING =
                Bindings.or(
                        Bindings.notEqual(
                                Main.simulator.buildStateProperty(),
                                Simulator.BuildState.DRAWING
                        ),
                        Main.simulator.portalDrawingProperty()
                );

        InitializeMainScreenService.PORTAL_DRAW_IN_PROGRESS_BINDING =
                Bindings.equal(
                        Main.simulator.portalDrawingProperty(),
                        Bindings.createBooleanBinding(() -> true)
                );

        InitializeMainScreenService.FLOOR_FIELD_DRAW_IN_PROGRESS_BINDING =
                Bindings.equal(
                        Main.simulator.floorFieldDrawingProperty(),
                        Bindings.createBooleanBinding(() -> true)
                );

        InitializeMainScreenService.ADD_FLOOR_FIELD_BINDING =
                Bindings.or(
                        Bindings.or(
                                Bindings.isNull(
                                        Main.simulator.currentAmenityProperty()
                                ),
                                AMENITY_NOT_EQUALS_SUBCATEGORY),
                        Bindings.notEqual(
                                Main.simulator.buildStateProperty(),
                                Simulator.BuildState.EDITING_ONE
                        )
                );
    }

    // Initialize the build tab UI controls
    public static void initializeSidebar(TabPane sideBar) {
        sideBar.disableProperty().bind(
                Bindings.or(
                        InitializeMainScreenService.PORTAL_DRAW_IN_PROGRESS_BINDING,
                        InitializeMainScreenService.FLOOR_FIELD_DRAW_IN_PROGRESS_BINDING
                )
        );
    }

    public static void initializeBuildTab(
            Label buildModeLabel,
            ChoiceBox<Simulator.BuildState> buildModeChoiceBox,
            // Entrances/exits
            // Station gate
            CheckBox stationGateEnableCheckBox,
            Label stationGateModeLabel,
            ChoiceBox<StationGate.StationGateMode> stationGateModeChoiceBox,
            Label stationGateSpawnLabel,
            Spinner<Integer> stationGateSpinner,
            Button saveStationGateButton,
            Button deleteStationGateButton,
            // Security
            CheckBox securityEnableCheckBox,
            CheckBox securityBlockPassengerCheckBox,
            Label securityIntervalLabel,
            Spinner<Integer> securityIntervalSpinner,
            Button saveSecurityButton,
            Button deleteSecurityButton,
            Button addFloorFieldsSecurityButton,
            // Stairs and elevators
            // Stairs
            Button addStairButton,
            Button editStairButton,
            Button deleteStairButton,
            // Escalator
            Button addEscalatorButton,
            Button editEscalatorButton,
            Button deleteEscalatorButton,
            // Elevator
            Button addElevatorButton,
            Button editElevatorButton,
            Button deleteElevatorButton,
            Button addFloorFieldsElevatorButton,
            // Concourse amenities
            // Ticket booth
            CheckBox ticketBoothEnableCheckBox,
            Label ticketBoothModeLabel,
            ChoiceBox<TicketBoothTransactionArea.TicketBoothType> ticketBoothModeChoiceBox,
            Label ticketBoothIntervalLabel,
            Spinner<Integer> ticketBoothIntervalSpinner,
            Button saveTicketBoothButton,
            Button deleteTicketBoothButton,
            Button addFloorFieldsTicketBoothButton,
            // Turnstile
            CheckBox turnstileEnableCheckBox,
            CheckBox turnstileBlockPassengerCheckBox,
            Label turnstileDirectionLabel,
            ChoiceBox<Turnstile.TurnstileMode> turnstileDirectionChoiceBox,
            Label turnstileIntervalLabel,
            Spinner<Integer> turnstileIntervalSpinner,
            Button saveTurnstileButton,
            Button deleteTurnstileButton,
            Button addFloorFieldsTurnstileButton,
            // Platform amenities
            CheckBox trainDoorEnableCheckBox,
            Label trainDoorDirectionLabel,
            ChoiceBox<TrainDoor.TrainDoorPlatform> trainDoorDirectionChoiceBox,
            Label trainDoorCarriageLabel,
            ListView<TrainDoor.TrainDoorCarriage> trainDoorCarriageListView,
            Button saveTrainDoorButton,
            Button deleteTrainDoorButton,
            Button addFloorFieldsTrainDoorButton,
            // Tab pane
            TabPane buildTabPane
    ) {
        // Initialize the build mode choice box
        initializeBuildModeChoiceBox(
                buildModeChoiceBox,
                buildModeLabel
        );

        // Initialize categories
        initializeEntrancesAndExits(
                stationGateEnableCheckBox,
                stationGateModeLabel,
                stationGateModeChoiceBox,
                stationGateSpawnLabel,
                stationGateSpinner,
                saveStationGateButton,
                deleteStationGateButton,
                securityEnableCheckBox,
                securityBlockPassengerCheckBox,
                securityIntervalLabel,
                securityIntervalSpinner,
                saveSecurityButton,
                deleteSecurityButton,
                addFloorFieldsSecurityButton
        );

        initializeStairsElevators(
                addStairButton,
                editStairButton,
                deleteStairButton,
                addEscalatorButton,
                editEscalatorButton,
                deleteEscalatorButton,
                addElevatorButton,
                editElevatorButton,
                deleteElevatorButton,
                addFloorFieldsElevatorButton
        );

        initializeConcourseAmenities(
                ticketBoothEnableCheckBox,
                ticketBoothModeLabel,
                ticketBoothModeChoiceBox,
                ticketBoothIntervalLabel,
                ticketBoothIntervalSpinner,
                saveTicketBoothButton,
                deleteTicketBoothButton,
                addFloorFieldsTicketBoothButton,
                turnstileEnableCheckBox,
                turnstileBlockPassengerCheckBox,
                turnstileDirectionLabel,
                turnstileDirectionChoiceBox,
                turnstileIntervalLabel,
                turnstileIntervalSpinner,
                saveTurnstileButton,
                deleteTurnstileButton,
                addFloorFieldsTurnstileButton
        );

        initializePlatformAmenities(
                trainDoorEnableCheckBox,
                trainDoorDirectionLabel,
                trainDoorDirectionChoiceBox,
                trainDoorCarriageLabel,
                trainDoorCarriageListView,
                saveTrainDoorButton,
                deleteTrainDoorButton,
                addFloorFieldsTrainDoorButton
        );

        // Initialize listeners
        initializeCategoryListeners(buildTabPane);
    }

    // Initialize the build mode choice box
    private static void initializeBuildModeChoiceBox(
            ChoiceBox<Simulator.BuildState> buildModeChoiceBox,
            Label buildModeLabel
    ) {
        buildModeLabel.setLabelFor(buildModeChoiceBox);

        buildModeChoiceBox.setItems(BUILD_MODE_CHOICEBOX_ITEMS);
        buildModeChoiceBox.getSelectionModel().select(0);

        buildModeChoiceBox.setOnAction(event -> {
            Simulator.BuildState newBuildState = buildModeChoiceBox.getSelectionModel().getSelectedItem();

            Main.simulator.setBuildState(newBuildState);

            Main.mainScreenController.updatePromptText();
        });
    }

    // Initialize the build tab UI controls
    public static void initializeTopBar(
            Button floorBelowButton,
            Button floorAboveButton
    ) {
        floorBelowButton.setDisable(true);
        floorAboveButton.setDisable(true);
    }

    // Initialize the test tab UI controls
    public static void initializeTestTab(
            ToggleButton playButton
    ) {
        // Initialize simulation controls
        initializeSimulationControls(playButton);
    }

    // Initialize the entrances and exits build category UI controls
    private static void initializeEntrancesAndExits(
            CheckBox stationGateEnableCheckBox,
            Label stationGateModeLabel,
            ChoiceBox<StationGate.StationGateMode> stationGateModeChoiceBox,
            Label stationGateSpawnLabel,
            Spinner<Integer> stationGateSpinner,
            Button saveStationGateButton,
            Button deleteStationGateButton,
            CheckBox securityEnableCheckBox,
            CheckBox securityBlockPassengerCheckBox,
            Label securityIntervalLabel,
            Spinner<Integer> securityIntervalSpinner,
            Button saveSecurityButton,
            Button deleteSecurityButton,
            Button addFloorFieldsSecurityButton
    ) {
        initializeStationEntranceExit(
                stationGateEnableCheckBox,
                stationGateModeLabel,
                stationGateModeChoiceBox,
                stationGateSpawnLabel,
                stationGateSpinner,
                saveStationGateButton,
                deleteStationGateButton
        );

        initializeSecurity(
                securityEnableCheckBox,
                securityBlockPassengerCheckBox,
                securityIntervalLabel,
                securityIntervalSpinner,
                saveSecurityButton,
                deleteSecurityButton,
                addFloorFieldsSecurityButton
        );
    }

    // Initialize the station entrance/exit UI controls
    private static void initializeStationEntranceExit(
            CheckBox stationGateEnableCheckBox,
            Label stationGateModeLabel,
            ChoiceBox<StationGate.StationGateMode> stationGateModeChoiceBox,
            Label stationGateSpawnLabel,
            Spinner<Integer> stationGateSpinner,
            Button saveStationGateButton,
            Button deleteStationGateButton
    ) {
        stationGateModeLabel.setLabelFor(stationGateModeChoiceBox);

        stationGateModeChoiceBox.setItems(FXCollections.observableArrayList(
                StationGate.StationGateMode.ENTRANCE,
                StationGate.StationGateMode.EXIT,
                StationGate.StationGateMode.ENTRANCE_AND_EXIT
        ));
        stationGateModeChoiceBox.getSelectionModel().select(0);

        stationGateSpawnLabel.setLabelFor(stationGateSpinner);

        stationGateSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        1,
                        100,
                        50)
        );

        stationGateEnableCheckBox.disableProperty().bind(InitializeMainScreenService.SPECIFIC_CONTROLS_BINDING);
        stationGateModeChoiceBox.disableProperty().bind(InitializeMainScreenService.SPECIFIC_CONTROLS_BINDING);
        stationGateSpinner.disableProperty().bind(InitializeMainScreenService.SPECIFIC_CONTROLS_BINDING);

        saveStationGateButton.disableProperty().bind(InitializeMainScreenService.SAVE_DELETE_BINDING);
        deleteStationGateButton.disableProperty().bind(InitializeMainScreenService.SAVE_DELETE_BINDING);
    }

    // Initialize the security UI controls
    private static void initializeSecurity(
            CheckBox securityEnableCheckBox,
            CheckBox securityBlockPassengerCheckBox,
            Label securityIntervalLabel,
            Spinner<Integer> securityIntervalSpinner,
            Button saveSecurityButton,
            Button deleteSecurityButton,
            Button addFloorFieldsSecurityButton
    ) {
        securityIntervalLabel.setLabelFor(securityIntervalSpinner);

        securityIntervalSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        0,
                        60,
                        5)
        );

        securityEnableCheckBox.disableProperty().bind(InitializeMainScreenService.SPECIFIC_CONTROLS_BINDING);
        securityBlockPassengerCheckBox.disableProperty().bind(InitializeMainScreenService.SPECIFIC_CONTROLS_BINDING);
        securityIntervalSpinner.disableProperty().bind(InitializeMainScreenService.SPECIFIC_CONTROLS_BINDING);

        saveSecurityButton.disableProperty().bind(InitializeMainScreenService.SAVE_DELETE_BINDING);
        deleteSecurityButton.disableProperty().bind(InitializeMainScreenService.SAVE_DELETE_BINDING);

        addFloorFieldsSecurityButton.disableProperty().bind(
                InitializeMainScreenService.ADD_FLOOR_FIELD_BINDING
        );
    }

    // Initialize the stairs and elevators build category UI controls
    private static void initializeStairsElevators(
            Button addStairButton,
            Button editStairButton,
            Button deleteStairButton,
            Button addEscalatorButton,
            Button editEscalatorButton,
            Button deleteEscalatorButton,
            Button addElevatorButton,
            Button editElevatorButton,
            Button deleteElevatorButton,
            Button addFloorFieldsElevatorButton
    ) {
        initializeStairs(
                addStairButton,
                editStairButton,
                deleteStairButton
        );

        initializeEscalators(
                addEscalatorButton,
                editEscalatorButton,
                deleteEscalatorButton
        );

        initializeElevators(
                addElevatorButton,
                editElevatorButton,
                deleteElevatorButton,
                addFloorFieldsElevatorButton
        );
    }

    // Initialize the stairs controls
    private static void initializeStairs(
            Button addStairButton,
            Button editStairButton,
            Button deleteStairButton
    ) {
        addStairButton.disableProperty().bind(InitializeMainScreenService.DRAW_ONLY_BINDING);

        editStairButton.disableProperty().bind(InitializeMainScreenService.SAVE_DELETE_BINDING);
        deleteStairButton.disableProperty().bind(InitializeMainScreenService.SAVE_DELETE_BINDING);
    }

    // Initialize the escalators controls
    private static void initializeEscalators(
            Button addEscalatorButton,
            Button editEscalatorButton,
            Button deleteEscalatorButton
    ) {
        addEscalatorButton.disableProperty().bind(InitializeMainScreenService.DRAW_ONLY_BINDING);

        editEscalatorButton.disableProperty().bind(InitializeMainScreenService.SAVE_DELETE_BINDING);
        deleteEscalatorButton.disableProperty().bind(InitializeMainScreenService.SAVE_DELETE_BINDING);
    }

    // Initialize the elevators controls
    private static void initializeElevators(
            Button addElevatorButton,
            Button editElevatorButton,
            Button deleteElevatorButton,
            Button addFloorFieldsElevatorButton
    ) {
        addElevatorButton.disableProperty().bind(InitializeMainScreenService.DRAW_ONLY_BINDING);

        editElevatorButton.disableProperty().bind(InitializeMainScreenService.SAVE_DELETE_BINDING);
        deleteElevatorButton.disableProperty().bind(InitializeMainScreenService.SAVE_DELETE_BINDING);

        addFloorFieldsElevatorButton.disableProperty().bind(InitializeMainScreenService.ADD_FLOOR_FIELD_BINDING);
    }

    // Initialize the concourse amenities build category UI controls
    private static void initializeConcourseAmenities(
            CheckBox ticketBoothEnableCheckBox,
            Label ticketBoothModeLabel,
            ChoiceBox<TicketBoothTransactionArea.TicketBoothType> ticketBoothModeChoiceBox,
            Label ticketBoothIntervalLabel,
            Spinner<Integer> ticketBoothIntervalSpinner,
            Button saveTicketBoothButton,
            Button deleteTicketBoothButton,
            Button addFloorFieldsTicketBoothButton,
            CheckBox turnstileEnableCheckBox,
            CheckBox turnstileBlockPassengerCheckBox,
            Label turnstileDirectionLabel,
            ChoiceBox<Turnstile.TurnstileMode> turnstileDirectionChoiceBox,
            Label turnstileIntervalLabel,
            Spinner<Integer> turnstileIntervalSpinner,
            Button saveTurnstileButton,
            Button deleteTurnstileButton,
            Button addFloorFieldsTurnstileButton
    ) {
        initializeTicketBooth(
                ticketBoothEnableCheckBox,
                ticketBoothModeLabel,
                ticketBoothModeChoiceBox,
                ticketBoothIntervalLabel,
                ticketBoothIntervalSpinner,
                saveTicketBoothButton,
                deleteTicketBoothButton,
                addFloorFieldsTicketBoothButton
        );

        initializeTurnstile(
                turnstileEnableCheckBox,
                turnstileBlockPassengerCheckBox,
                turnstileDirectionLabel,
                turnstileDirectionChoiceBox,
                turnstileIntervalLabel,
                turnstileIntervalSpinner,
                saveTurnstileButton,
                deleteTurnstileButton,
                addFloorFieldsTurnstileButton
        );
    }

    // Initialize the ticket booth controls
    private static void initializeTicketBooth(
            CheckBox ticketBoothEnableCheckBox,
            Label ticketBoothModeLabel,
            ChoiceBox<TicketBoothTransactionArea.TicketBoothType> ticketBoothModeChoiceBox,
            Label ticketBoothIntervalLabel,
            Spinner<Integer> ticketBoothIntervalSpinner,
            Button saveTicketBoothButton,
            Button deleteTicketBoothButton,
            Button addFloorFieldsTicketBoothButton
    ) {
        ticketBoothModeLabel.setLabelFor(ticketBoothModeChoiceBox);

        ticketBoothModeChoiceBox.setItems(FXCollections.observableArrayList(
                TicketBoothTransactionArea.TicketBoothType.SINGLE_JOURNEY,
                TicketBoothTransactionArea.TicketBoothType.STORED_VALUE,
                TicketBoothTransactionArea.TicketBoothType.ALL_TICKET_TYPES
        ));
        ticketBoothModeChoiceBox.getSelectionModel().select(0);

        ticketBoothIntervalLabel.setLabelFor(ticketBoothIntervalSpinner);

        ticketBoothIntervalSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        5,
                        60,
                        5)
        );

        ticketBoothEnableCheckBox.disableProperty().bind(InitializeMainScreenService.SPECIFIC_CONTROLS_BINDING);
        ticketBoothModeChoiceBox.disableProperty().bind(InitializeMainScreenService.SPECIFIC_CONTROLS_BINDING);
        ticketBoothIntervalSpinner.disableProperty().bind(InitializeMainScreenService.SPECIFIC_CONTROLS_BINDING);

        saveTicketBoothButton.disableProperty().bind(InitializeMainScreenService.SAVE_DELETE_BINDING);
        deleteTicketBoothButton.disableProperty().bind(InitializeMainScreenService.SAVE_DELETE_BINDING);

        addFloorFieldsTicketBoothButton.disableProperty().bind(
                InitializeMainScreenService.ADD_FLOOR_FIELD_BINDING
        );
    }

    // Initialize the turnstile controls
    private static void initializeTurnstile(
            CheckBox turnstileEnableCheckBox,
            CheckBox turnstileBlockPassengerCheckBox,
            Label turnstileDirectionLabel,
            ChoiceBox<Turnstile.TurnstileMode> turnstileDirectionChoiceBox,
            Label turnstileIntervalLabel,
            Spinner<Integer> turnstileIntervalSpinner,
            Button saveTurnstileButton,
            Button deleteTurnstileButton,
            Button addFloorFieldsTurnstileButton
    ) {
        turnstileDirectionLabel.setLabelFor(turnstileDirectionChoiceBox);

        turnstileDirectionChoiceBox.setItems(FXCollections.observableArrayList(
                Turnstile.TurnstileMode.BOARDING,
                Turnstile.TurnstileMode.ALIGHTING,
                Turnstile.TurnstileMode.BIDIRECTIONAL
        ));
        turnstileDirectionChoiceBox.getSelectionModel().select(0);

        turnstileIntervalLabel.setLabelFor(turnstileIntervalSpinner);

        turnstileIntervalSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        1,
                        10,
                        3)
        );

        turnstileEnableCheckBox.disableProperty().bind(InitializeMainScreenService.SPECIFIC_CONTROLS_BINDING);
        turnstileBlockPassengerCheckBox.disableProperty().bind(InitializeMainScreenService.SPECIFIC_CONTROLS_BINDING);
        turnstileDirectionChoiceBox.disableProperty().bind(InitializeMainScreenService.SPECIFIC_CONTROLS_BINDING);
        turnstileIntervalSpinner.disableProperty().bind(InitializeMainScreenService.SPECIFIC_CONTROLS_BINDING);

        saveTurnstileButton.disableProperty().bind(InitializeMainScreenService.SAVE_DELETE_BINDING);
        deleteTurnstileButton.disableProperty().bind(InitializeMainScreenService.SAVE_DELETE_BINDING);

        addFloorFieldsTurnstileButton.disableProperty().bind(InitializeMainScreenService.ADD_FLOOR_FIELD_BINDING);
    }

    // Initialize the platform amenities build category UI controls
    private static void initializePlatformAmenities(
            CheckBox trainDoorEnableCheckBox,
            Label trainDoorDirectionLabel,
            ChoiceBox<TrainDoor.TrainDoorPlatform> trainDoorDirectionChoiceBox,
            Label trainDoorCarriageLabel,
            ListView<TrainDoor.TrainDoorCarriage> trainDoorCarriageListView,
            Button saveTrainDoorButton,
            Button deleteTrainDoorButton,
            Button addFloorFieldsTrainDoorButton
    ) {
        initializeTrainBoardingArea(
                trainDoorEnableCheckBox,
                trainDoorDirectionLabel,
                trainDoorDirectionChoiceBox,
                trainDoorCarriageLabel,
                trainDoorCarriageListView,
                saveTrainDoorButton,
                deleteTrainDoorButton,
                addFloorFieldsTrainDoorButton
        );
    }

    // Initialize the train boarding area controls
    private static void initializeTrainBoardingArea(
            CheckBox trainDoorEnableCheckBox,
            Label trainDoorDirectionLabel,
            ChoiceBox<TrainDoor.TrainDoorPlatform> trainDoorDirectionChoiceBox,
            Label trainDoorCarriageLabel,
            ListView<TrainDoor.TrainDoorCarriage> trainDoorCarriagesListView,
            Button saveTrainDoorButton,
            Button deleteTrainDoorButton,
            Button addFloorFieldsTrainDoorButton
    ) {
        trainDoorDirectionLabel.setLabelFor(trainDoorDirectionChoiceBox);

        trainDoorDirectionChoiceBox.setItems(FXCollections.observableArrayList(
                TrainDoor.TrainDoorPlatform.NORTHBOUND,
                TrainDoor.TrainDoorPlatform.SOUTHBOUND,
                TrainDoor.TrainDoorPlatform.WESTBOUND,
                TrainDoor.TrainDoorPlatform.EASTBOUND
        ));
        trainDoorDirectionChoiceBox.getSelectionModel().select(0);

        trainDoorCarriageLabel.setLabelFor(trainDoorCarriagesListView);

        trainDoorCarriagesListView.setItems(FXCollections.observableArrayList(
                TrainDoor.TrainDoorCarriage.LRT_1_FIRST_GENERATION,
                TrainDoor.TrainDoorCarriage.LRT_1_SECOND_GENERATION,
                TrainDoor.TrainDoorCarriage.LRT_1_THIRD_GENERATION,
                TrainDoor.TrainDoorCarriage.LRT_2_FIRST_GENERATION,
                TrainDoor.TrainDoorCarriage.MRT_3_FIRST_GENERATION,
                TrainDoor.TrainDoorCarriage.MRT_3_SECOND_GENERATION
        ));
        trainDoorCarriagesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        trainDoorEnableCheckBox.disableProperty().bind(InitializeMainScreenService.SPECIFIC_CONTROLS_BINDING);
        trainDoorDirectionChoiceBox.disableProperty().bind(InitializeMainScreenService.SPECIFIC_CONTROLS_BINDING);
        trainDoorCarriagesListView.disableProperty().bind(InitializeMainScreenService.SPECIFIC_CONTROLS_BINDING);

        saveTrainDoorButton.disableProperty().bind(InitializeMainScreenService.SAVE_DELETE_BINDING);
        deleteTrainDoorButton.disableProperty().bind(InitializeMainScreenService.SAVE_DELETE_BINDING);

        addFloorFieldsTrainDoorButton.disableProperty().bind(InitializeMainScreenService.ADD_FLOOR_FIELD_BINDING);
    }

    // Iterate through each build subtab to set the listeners for the changing of build categories and subcategories
    private static void initializeCategoryListeners(TabPane buildTabPane) {
        List<Tab> buildSubtabs = buildTabPane.getTabs();

        for (Tab subtab : buildSubtabs) {
            // Get the accordion within this tab and its titled panes
            Accordion currentAccordion = (Accordion) subtab.getContent();
            List<TitledPane> currentTitledPanes = currentAccordion.getPanes();

            for (TitledPane currentTitledPane : currentTitledPanes) {
                currentAccordion.expandedPaneProperty().addListener((observable, oldValue, newValue) -> {
                    // If the this pane is expanded, and the current pane is the one recently expanded, update the
                    // current subcategory
                    // The previous state was either from no expanded subcategories or from another subcategory
                    if (currentTitledPane.isExpanded() && currentTitledPane == newValue) {
                        Main.simulator.setBuildSubcategory(
                                MainScreenController.getBuildSubcategory(buildTabPane.getSelectionModel())
                        );

                        Main.mainScreenController.updatePromptText();
                    } else {
                        // If not, just check if this pane is the previous subcategory and the new subcategory is null
                        // The previous state was either from an expanded subcategory or has been replaced with another
                        // subcategory
                        if (currentTitledPane == oldValue && currentAccordion.getExpandedPane() == null) {
                            Main.simulator.setBuildSubcategory(
                                    MainScreenController.getBuildSubcategory(buildTabPane.getSelectionModel())
                            );

                            Main.mainScreenController.updatePromptText();
                        }
                    }
                });

                subtab.setOnSelectionChanged(event -> {
                    if (subtab.isSelected()) {
                        // Update the categories and subcategories
                        Main.simulator.setBuildCategory(
                                MainScreenController.getBuildCategory(buildTabPane.getSelectionModel())
                        );

                        Main.simulator.setBuildSubcategory(
                                MainScreenController.getBuildSubcategory(buildTabPane.getSelectionModel())
                        );

                        Main.mainScreenController.updatePromptText();
                    }
                });
            }
        }
    }

    private static void initializeSimulationControls(
            ToggleButton playButton
    ) {
        playButton.setOnAction(event -> {
            if (playButton.isSelected()) {
                Main.simulator.setOperationMode(Simulator.OperationMode.TESTING);

                Main.mainScreenController.updatePromptText();
            } else {
                Main.simulator.setOperationMode(Simulator.OperationMode.BUILDING);

                Main.mainScreenController.updatePromptText();
            }
        });
    }
}
