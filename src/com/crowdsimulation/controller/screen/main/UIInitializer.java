package com.crowdsimulation.controller.screen.main;

import com.crowdsimulation.controller.Main;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.StationGate;
import com.crowdsimulation.model.simulator.Simulator;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

public class UIInitializer {
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
            ChoiceBox<Simulator.BuildState> stationGateBuildModeChoiceBox,
            CheckBox stationGateEnableCheckBox,
            Label stationGateModeLabel,
            ChoiceBox<StationGate.StationGateMode> stationGateModeChoiceBox,
            Label stationGateSpawnLabel,
            Spinner<Integer> stationGateSpinner,
            Button saveStationGateButton,
            Button deleteStationGateButton,
            ChoiceBox<Simulator.BuildState> securityBuildModeChoiceBox,
            CheckBox securityEnableCheckBox,
            CheckBox securityBlockPassengerCheckBox,
            Label securityIntervalLabel,
            Spinner<Integer> securityIntervalSpinner,
            Button saveSecurityButton,
            Button deleteSecurityButton,
            TabPane buildTabPane
    ) {
        // Initialize categories
        initializeEntrancesAndExits(
                stationGateBuildModeChoiceBox,
                stationGateEnableCheckBox,
                stationGateModeLabel,
                stationGateModeChoiceBox,
                stationGateSpawnLabel,
                stationGateSpinner,
                saveStationGateButton,
                deleteStationGateButton,
                securityBuildModeChoiceBox,
                securityEnableCheckBox,
                securityBlockPassengerCheckBox,
                securityIntervalLabel,
                securityIntervalSpinner,
                saveSecurityButton,
                deleteSecurityButton
        );

        // Initialize listeners
        initializeCategoryListeners(buildTabPane);
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
            ChoiceBox<Simulator.BuildState> stationGateBuildModeChoiceBox,
            CheckBox stationGateEnableCheckBox,
            Label stationGateModeLabel,
            ChoiceBox<StationGate.StationGateMode> stationGateModeChoiceBox,
            Label stationGateSpawnLabel,
            Spinner<Integer> stationGateSpinner,
            Button saveStationGateButton,
            Button deleteStationGateButton,
            ChoiceBox<Simulator.BuildState> securityBuildModeChoiceBox,
            CheckBox securityEnableCheckBox,
            CheckBox securityBlockPassengerCheckBox,
            Label securityIntervalLabel,
            Spinner<Integer> securityIntervalSpinner,
            Button saveSecurityButton,
            Button deleteSecurityButton
    ) {
        initializeStationEntranceExit(
                stationGateBuildModeChoiceBox,
                stationGateEnableCheckBox,
                stationGateModeLabel,
                stationGateModeChoiceBox,
                stationGateSpawnLabel,
                stationGateSpinner,
                saveStationGateButton,
                deleteStationGateButton
        );

        initializeSecurity(
                securityBuildModeChoiceBox,
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
            ChoiceBox<Simulator.BuildState> stationGateBuildModeChoiceBox,
            CheckBox stationGateEnableCheckBox,
            Label stationGateModeLabel,
            ChoiceBox<StationGate.StationGateMode> stationGateModeChoiceBox,
            Label stationGateSpawnLabel,
            Spinner<Integer> stationGateSpinner,
            Button saveStationGateButton,
            Button deleteStationGateButton
    ) {
        stationGateBuildModeChoiceBox.setItems(BUILD_MODE_CHOICEBOX_ITEMS);
        stationGateBuildModeChoiceBox.getSelectionModel().select(0);

        stationGateModeLabel.setLabelFor(stationGateModeChoiceBox);

        stationGateModeChoiceBox.setItems(FXCollections.observableArrayList(
                StationGate.StationGateMode.ENTRANCE,
                StationGate.StationGateMode.EXIT,
                StationGate.StationGateMode.ENTRANCE_AND_EXIT
        ));
        stationGateModeChoiceBox.getSelectionModel().select(0);

        stationGateBuildModeChoiceBox.setOnAction(event -> {
            Simulator.BuildState newBuildState = stationGateBuildModeChoiceBox.getSelectionModel().getSelectedItem();
            Main.simulator.setBuildState(newBuildState);

            /*Main.simulator.setCurrentAmenity(null);
            Main.simulator.setCurrentClass(null);*/

            if (newBuildState != Simulator.BuildState.EDITING_ONE) {
                Main.mainScreenController.setEnableStationGateControls(true);
            }
        });

        stationGateSpawnLabel.setLabelFor(stationGateSpinner);

        stationGateSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        1,
                        100,
                        50)
        );

        boolean evaluateClassEquality = Main.simulator.getCurrentAmenity().isNotNull().get();

        // Disable when drawing or ((current amenity is null or current amenity is of the wrong type)
        // and not in the edit all mode)
        BooleanBinding bindingValue =
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

        saveStationGateButton.disableProperty().bind(bindingValue);
        deleteStationGateButton.disableProperty().bind(bindingValue);
    }

    // Initialize the security UI controls
    private static void initializeSecurity(
            ChoiceBox<Simulator.BuildState> securityBuildModeChoiceBox,
            CheckBox securityEnableCheckBox,
            CheckBox securityBlockPassengerCheckBox,
            Label securityIntervalLabel,
            Spinner<Integer> securityIntervalSpinner,
            Button saveSecurityButton,
            Button deleteSecurityButton
    ) {
        securityBuildModeChoiceBox.setItems(BUILD_MODE_CHOICEBOX_ITEMS);
        securityBuildModeChoiceBox.getSelectionModel().select(0);

        securityBuildModeChoiceBox.setOnAction(event -> {
            Simulator.BuildState newBuildState = securityBuildModeChoiceBox.getSelectionModel().getSelectedItem();
            Main.simulator.setBuildState(newBuildState);
        });

        securityIntervalLabel.setLabelFor(securityIntervalSpinner);

        securityIntervalSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        0,
                        60,
                        5)
        );

        deleteSecurityButton.disableProperty().bind(
                Bindings.equal(
                        Main.simulator.getOperationMode(),
                        Simulator.OperationMode.TESTING
                )
        );
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

                        Main.simulator.setBuildState(
                                getCurrentBuildStateInAccordion(currentAccordion)
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

                        if (currentAccordion.getExpandedPane() != null) {
                            Main.simulator.setBuildState(
                                    getCurrentBuildStateInAccordion(currentAccordion)
                            );
                        }
                    }
                });
            }
        }
    }

    private static Simulator.BuildState getCurrentBuildStateInAccordion(Accordion currentAccordion) {
        VBox outerVBox = (VBox) currentAccordion.getExpandedPane().getContent();
        VBox innerVBox = (VBox) outerVBox.getChildren().get(0);
        ChoiceBox<Simulator.BuildState> buildStateChoiceBox
                = (ChoiceBox<Simulator.BuildState>) innerVBox.getChildren().get(0);

        return buildStateChoiceBox.getSelectionModel().getSelectedItem();
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
