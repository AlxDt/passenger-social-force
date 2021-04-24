package com.crowdsimulation.controller.screen.service.main;

import com.crowdsimulation.controller.screen.service.InitializeScreenService;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class InitializeWelcomeService extends InitializeScreenService {
    public static void initializeWelcomeService(
            Label rowLabel,
            Spinner<Integer> rowSpinner,
            Label columnLabel,
            Spinner<Integer> columnSpinner,
            Button createBlankStationButton,
            Button loadStationButton
    ) {
        final int minimumRows = 10;
        final int maximumRows = 100;

        final int minimumColumns = 106;
        final int maximumColumns = 220;

        rowLabel.setLabelFor(rowSpinner);
        rowSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                minimumRows, maximumRows
        ));

        rowSpinner.setEditable(true);
        rowSpinner.getValueFactory().setValue(60);

        // A hacky way to commit the typed value when focus is lost from the spinner
        rowSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                rowSpinner.increment(0);
            }
        });

        columnLabel.setLabelFor(columnSpinner);
        columnSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                minimumColumns, maximumColumns
        ));

        columnSpinner.setEditable(true);
        columnSpinner.getValueFactory().setValue(106);

        // A hacky way to commit the typed value when focus is lost from the spinner
        columnSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                columnSpinner.increment(0);
            }
        });
    }
}
