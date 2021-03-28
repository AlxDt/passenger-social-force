package com.crowdsimulation.controller.screen.feature.portalcontroller;

import com.crowdsimulation.controller.Main;
import com.crowdsimulation.controller.screen.ScreenController;
import com.crowdsimulation.controller.screen.main.service.InitializeElevatorSetupService;
import com.crowdsimulation.controller.screen.main.service.InitializePortalFloorSelectorService;
import com.crowdsimulation.model.core.environment.station.Floor;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.elevator.ElevatorShaft;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PortalFloorSelectorController extends ScreenController {
    public static final String OUTPUT_KEY = "floor";

    @FXML
    private Spinner<Integer> floorSpinner;

    @FXML
    private Button proceedButton;

    @FXML
    private Text promptText;

    @FXML
    public void proceedAction() {
        Stage stage = (Stage) proceedButton.getScene().getWindow();

        // Take note of the values in the form
        Floor floor = Main.simulator.getStation().getFloors().get(floorSpinner.getValue() - 1);

        this.getWindowOutput().put(OUTPUT_KEY, floor);

        // Close the window
        this.setClosedWithAction(true);
        stage.close();
    }

    public void setElements() {
        InitializePortalFloorSelectorService.initializePortalFloorSelector(
                promptText,
                floorSpinner,
                proceedButton
        );
    }
}
