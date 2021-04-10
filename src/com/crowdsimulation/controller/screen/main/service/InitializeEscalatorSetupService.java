package com.crowdsimulation.controller.screen.main.service;

import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.escalator.EscalatorShaft;
import javafx.collections.FXCollections;
import javafx.scene.control.*;

public class InitializeEscalatorSetupService extends InitializeScreenService {
    public static void initializeEscalatorSetup(
            CheckBox escalatorEnableCheckbox,
            Label escalatorMoveLabel,
            Spinner<Integer> escalatorMoveSpinner,
            Label escalatorDirectionLabel,
            ChoiceBox<EscalatorShaft.EscalatorDirection> escalatorDirectionChoiceBox,
            Button proceedButton
    ) {
        // Set elements
        escalatorMoveLabel.setLabelFor(escalatorMoveSpinner);
        escalatorMoveSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        10,
                        60
                )
        );

        escalatorDirectionLabel.setLabelFor(escalatorDirectionChoiceBox);

        escalatorDirectionChoiceBox.setItems(FXCollections.observableArrayList(
                EscalatorShaft.EscalatorDirection.UP,
                EscalatorShaft.EscalatorDirection.DOWN
        ));
        escalatorDirectionChoiceBox.getSelectionModel().select(0);
    }
}
