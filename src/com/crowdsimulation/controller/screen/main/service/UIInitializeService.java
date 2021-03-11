package com.crowdsimulation.controller.screen.main.service;

import com.crowdsimulation.controller.Main;
import com.crowdsimulation.controller.screen.main.MainScreenController;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.obstacle.TicketBooth;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.StationGate;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.Security;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.goal.TicketBoothTransactionArea;
import com.crowdsimulation.model.simulator.Simulator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

public class UIInitializeService {
    private static final ObservableList<Simulator.BuildState> BUILD_MODE_CHOICEBOX_ITEMS;

    static {
        BUILD_MODE_CHOICEBOX_ITEMS = FXCollections.observableArrayList(
                Simulator.BuildState.DRAWING,
                Simulator.BuildState.EDITING_ONE,
                Simulator.BuildState.EDITING_ALL
        );
    }

    // Initialize the build tab UI controls
    public static void initializeBuildTab(
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
            // Concourse amenities
            // Ticket booth
            CheckBox ticketBoothEnableCheckBox,
            Label ticketBoothModeLabel,
            ChoiceBox<TicketBoothTransactionArea.TicketBoothType> ticketBoothModeChoiceBox,
            Label ticketBoothIntervalLabel,
            Spinner<Integer> ticketBoothIntervalSpinner,
            Button saveTicketBoothButton,
            Button deleteTicketBoothButton,
            // Tab pane
            TabPane buildTabPane
    ) {
        // Initialize the build mode choice box
        initializeBuildModeCheckbox(
                buildModeChoiceBox
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
                deleteSecurityButton
        );

        initializeConcourseAmenities(
                ticketBoothEnableCheckBox,
                ticketBoothModeLabel,
                ticketBoothModeChoiceBox,
                ticketBoothIntervalLabel,
                ticketBoothIntervalSpinner,
                saveTicketBoothButton,
                deleteTicketBoothButton
        );

        // Initialize listeners
        initializeCategoryListeners(buildTabPane);
    }

    // Initialize the build mode choice box
    private static void initializeBuildModeCheckbox(ChoiceBox<Simulator.BuildState> buildModeChoiceBox) {
        buildModeChoiceBox.setItems(BUILD_MODE_CHOICEBOX_ITEMS);
        buildModeChoiceBox.getSelectionModel().select(0);

        buildModeChoiceBox.setOnAction(event -> {
            Simulator.BuildState newBuildState = buildModeChoiceBox.getSelectionModel().getSelectedItem();

            Main.simulator.setBuildState(newBuildState);
        });
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
            Button deleteSecurityButton
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
                deleteSecurityButton
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
/*        stationGateBuildModeChoiceBox.setItems(BUILD_MODE_CHOICEBOX_ITEMS);
        stationGateBuildModeChoiceBox.getSelectionModel().select(0);*/

//        boolean evaluateClassEquality = Main.simulator.getCurrentAmenity().isNotNull().get();

/*        stationGateBuildModeChoiceBox.setOnAction(event -> {
            Simulator.BuildState newBuildState = stationGateBuildModeChoiceBox.getSelectionModel().getSelectedItem();
            Main.simulator.setBuildState(newBuildState);

            Main.simulator.setCurrentClass(StationGate.class);
//            Main.mainScreenController.checkEnableStationGateControls();
        });*/

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

        stationGateEnableCheckBox.disableProperty().bind(MainScreenController.SPECIFIC_CONTROLS_BINDING);
        stationGateModeChoiceBox.disableProperty().bind(MainScreenController.SPECIFIC_CONTROLS_BINDING);
        stationGateSpinner.disableProperty().bind(MainScreenController.SPECIFIC_CONTROLS_BINDING);

        saveStationGateButton.disableProperty().bind(MainScreenController.SAVE_DELETE_BINDING);
        deleteStationGateButton.disableProperty().bind(MainScreenController.SAVE_DELETE_BINDING);
    }

    // Initialize the security UI controls
    private static void initializeSecurity(
            CheckBox securityEnableCheckBox,
            CheckBox securityBlockPassengerCheckBox,
            Label securityIntervalLabel,
            Spinner<Integer> securityIntervalSpinner,
            Button saveSecurityButton,
            Button deleteSecurityButton
    ) {
        securityIntervalLabel.setLabelFor(securityIntervalSpinner);

        securityIntervalSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        0,
                        60,
                        5)
        );

        securityEnableCheckBox.disableProperty().bind(MainScreenController.SPECIFIC_CONTROLS_BINDING);
        securityBlockPassengerCheckBox.disableProperty().bind(MainScreenController.SPECIFIC_CONTROLS_BINDING);
        securityIntervalSpinner.disableProperty().bind(MainScreenController.SPECIFIC_CONTROLS_BINDING);

        saveSecurityButton.disableProperty().bind(MainScreenController.SAVE_DELETE_BINDING);
        deleteSecurityButton.disableProperty().bind(MainScreenController.SAVE_DELETE_BINDING);
    }

    // Initialize the concourse amenities build category UI controls
    private static void initializeConcourseAmenities(
            CheckBox ticketBoothEnableCheckBox,
            Label ticketBoothModeLabel,
            ChoiceBox<TicketBoothTransactionArea.TicketBoothType> ticketBoothModeChoiceBox,
            Label ticketBoothIntervalLabel,
            Spinner<Integer> ticketBoothIntervalSpinner,
            Button saveTicketBoothButton,
            Button deleteTicketBoothButton
    ) {
        initializeTicketBooth(
                ticketBoothEnableCheckBox,
                ticketBoothModeLabel,
                ticketBoothModeChoiceBox,
                ticketBoothIntervalLabel,
                ticketBoothIntervalSpinner,
                saveTicketBoothButton,
                deleteTicketBoothButton
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
            Button deleteTicketBoothButton
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

        ticketBoothEnableCheckBox.disableProperty().bind(MainScreenController.SPECIFIC_CONTROLS_BINDING);
        ticketBoothModeChoiceBox.disableProperty().bind(MainScreenController.SPECIFIC_CONTROLS_BINDING);
        ticketBoothIntervalSpinner.disableProperty().bind(MainScreenController.SPECIFIC_CONTROLS_BINDING);

        saveTicketBoothButton.disableProperty().bind(MainScreenController.SAVE_DELETE_BINDING);
        deleteTicketBoothButton.disableProperty().bind(MainScreenController.SAVE_DELETE_BINDING);
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
                    } else {
                        // If not, just check if this pane is the previous subcategory and the new subcategory is null
                        // The previous state was either from an expanded subcategory or has been replaced with another
                        // subcategory
                        if (currentTitledPane == oldValue && currentAccordion.getExpandedPane() == null) {
                            Main.simulator.setBuildSubcategory(
                                    MainScreenController.getBuildSubcategory(buildTabPane.getSelectionModel())
                            );
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
                Main.simulator.getOperationMode().set(Simulator.OperationMode.TESTING);
            } else {
                Main.simulator.getOperationMode().set(Simulator.OperationMode.BUILDING);
            }
        });
    }
}
