package com.crowdsimulation.controller.screen.feature.portal.setup;

import com.crowdsimulation.controller.screen.service.portal.setup.InitializeElevatorSetupService;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.elevator.ElevatorShaft;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ElevatorSetupController extends PortalSetupController {
    public static final String OUTPUT_KEY = "elevator_shaft";

    @FXML
    private CheckBox elevatorEnableCheckbox;

    @FXML
    private Label elevatorDelayLabel;

    @FXML
    private Spinner<Integer> elevatorDelaySpinner;

    @FXML
    private Label elevatorOpenLabel;

    @FXML
    private Spinner<Integer> elevatorOpenSpinner;

    @FXML
    private Label elevatorMoveLabel;

    @FXML
    private Spinner<Integer> elevatorMoveSpinner;

    @FXML
    private Label elevatorDirectionLabel;

    @FXML
    private ChoiceBox<ElevatorShaft.ElevatorDirection> elevatorDirectionChoiceBox;

    @FXML
    private Button proceedButton;

    @FXML
    public void proceedAction() {
        Stage stage = (Stage) proceedButton.getScene().getWindow();

        // Take note of the values in the form
        boolean enabled = elevatorEnableCheckbox.isSelected();
        int delayTime = elevatorDelaySpinner.getValue();
        int openTime = elevatorOpenSpinner.getValue();
        int moveTime = elevatorMoveSpinner.getValue();
        ElevatorShaft.ElevatorDirection elevatorDirection = elevatorDirectionChoiceBox.getValue();

        // Prepare the provisional elevator shaft
        // If the user chooses not to go through with the elevator, this shaft will
        // simply be discarded
        ElevatorShaft.ElevatorShaftFactory elevatorShaftFactory =
                new ElevatorShaft.ElevatorShaftFactory();

        ElevatorShaft elevatorShaft = elevatorShaftFactory.create(
                enabled,
                moveTime,
                delayTime,
                openTime,
                elevatorDirection
        );

        this.getWindowOutput().put(OUTPUT_KEY, elevatorShaft);

        // Close the window
        this.setClosedWithAction(true);
        stage.close();
    }

    @Override
    public void setElements() {
        InitializeElevatorSetupService.initializeElevatorSetup(
                elevatorEnableCheckbox,
                elevatorDelayLabel,
                elevatorDelaySpinner,
                elevatorOpenLabel,
                elevatorOpenSpinner,
                elevatorMoveLabel,
                elevatorMoveSpinner,
                elevatorDirectionLabel,
                elevatorDirectionChoiceBox,
                proceedButton
        );
    }

    @Override
    protected void closeAction() {

    }
}
