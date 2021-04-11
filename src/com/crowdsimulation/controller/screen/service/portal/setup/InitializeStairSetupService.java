package com.crowdsimulation.controller.screen.service.portal.setup;

import com.crowdsimulation.controller.screen.service.InitializeScreenService;
import javafx.scene.control.*;
import javafx.scene.text.Text;

public class InitializeStairSetupService extends InitializeScreenService {
    public static void initializeStairSetup(
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
