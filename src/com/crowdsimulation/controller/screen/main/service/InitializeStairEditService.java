package com.crowdsimulation.controller.screen.main.service;

import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.gate.portal.escalator.EscalatorShaft;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.text.Text;

public class InitializeStairEditService extends InitializeScreenService {
    public static void initializeStairEdit(
            Text promptText,
            CheckBox stairEnableCheckBox,
            Label stairMoveLabel,
            Spinner<Integer> stairMoveSpinner,
            Button proceedButton
    ) {
        // Set elements
        stairMoveLabel.setLabelFor(stairMoveSpinner);
        stairMoveSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        10,
                        60
                )
        );
    }
}
