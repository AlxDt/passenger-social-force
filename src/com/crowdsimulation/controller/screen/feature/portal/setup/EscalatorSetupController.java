package com.crowdsimulation.controller.screen.feature.portal.setup;

import com.crowdsimulation.controller.screen.main.service.InitializeEscalatorSetupService;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.escalator.EscalatorShaft;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EscalatorSetupController extends PortalSetupController {
    public static final String OUTPUT_KEY = "escalator_shaft";

    @FXML
    private CheckBox escalatorEnableCheckBox;

    @FXML
    private Label escalatorMoveLabel;

    @FXML
    private Spinner<Integer> escalatorMoveSpinner;

    @FXML
    private Label escalatorDirectionLabel;

    @FXML
    private ChoiceBox<EscalatorShaft.EscalatorDirection> escalatorDirectionChoiceBox;

    @FXML
    private Button proceedButton;

    @FXML
    public void proceedAction() {
        Stage stage = (Stage) proceedButton.getScene().getWindow();

        // Take note of the values in the form
        boolean enabled = escalatorEnableCheckBox.isSelected();
        int moveTime = escalatorMoveSpinner.getValue();
        EscalatorShaft.EscalatorDirection escalatorDirection = escalatorDirectionChoiceBox.getValue();

        // Prepare the provisional escalator shaft
        // If the user chooses not to go through with the elevator, this shaft will
        // simply be discarded
        EscalatorShaft.EscalatorShaftFactory escalatorShaftFactory
                = new EscalatorShaft.EscalatorShaftFactory();

        EscalatorShaft escalatorShaft = escalatorShaftFactory.create(
                null,
                enabled,
                moveTime,
                escalatorDirection
        );

        this.getWindowOutput().put(OUTPUT_KEY, escalatorShaft);

        // Close the window
        this.setClosedWithAction(true);
        stage.close();
    }

    public void setElements() {
        InitializeEscalatorSetupService.initializeEscalatorSetup(
                escalatorEnableCheckBox,
                escalatorMoveLabel,
                escalatorMoveSpinner,
                escalatorDirectionLabel,
                escalatorDirectionChoiceBox,
                proceedButton
        );
    }

    @Override
    protected void closeAction() {

    }
}