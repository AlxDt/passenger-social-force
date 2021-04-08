package com.crowdsimulation.controller.screen.main.service;

import com.crowdsimulation.controller.screen.feature.floorfield.NormalFloorFieldController;
import com.crowdsimulation.controller.screen.main.MainScreenController;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.text.Text;

public class InitializeNormalFloorFieldService {
    private static final BooleanBinding DRAWING_FLOOR_FIELD_MODE;

    static {
        DRAWING_FLOOR_FIELD_MODE = Bindings.notEqual(
                MainScreenController.normalFloorFieldController.floorFieldModeProperty(),
                NormalFloorFieldController.FloorFieldMode.DRAWING
        );
    }

    public static void initializeNormalFloorField(
            Text promptText,
            Label modeLabel,
            ChoiceBox<NormalFloorFieldController.FloorFieldMode> modeChoiceBox,
            Label intensityLabel,
            Slider intensitySlider,
            Button validateButton,
            Button deleteAllButton
    ) {
        modeLabel.setLabelFor(modeChoiceBox);

        modeChoiceBox.setItems(FXCollections.observableArrayList(
                NormalFloorFieldController.FloorFieldMode.DRAWING,
                NormalFloorFieldController.FloorFieldMode.DELETING
        ));
        modeChoiceBox.getSelectionModel().select(0);

        NormalFloorFieldController.FloorFieldMode initialFloorFieldMode
                = modeChoiceBox.getSelectionModel().getSelectedItem();
        MainScreenController.normalFloorFieldController.setFloorFieldMode(initialFloorFieldMode);

        NormalFloorFieldController.updatePromptText(promptText, initialFloorFieldMode);

        modeChoiceBox.setOnAction(event -> {
            NormalFloorFieldController.FloorFieldMode updatedFloorFieldMode
                    = modeChoiceBox.getSelectionModel().getSelectedItem();

            MainScreenController.normalFloorFieldController.setFloorFieldMode(updatedFloorFieldMode);

            NormalFloorFieldController.updatePromptText(promptText, updatedFloorFieldMode);
        });

        validateButton.disableProperty().bind(DRAWING_FLOOR_FIELD_MODE);
        deleteAllButton.disableProperty().bind(Bindings.not(DRAWING_FLOOR_FIELD_MODE));

        intensityLabel.setLabelFor(intensitySlider);
        intensitySlider.disableProperty().bind(DRAWING_FLOOR_FIELD_MODE);
    }
}
