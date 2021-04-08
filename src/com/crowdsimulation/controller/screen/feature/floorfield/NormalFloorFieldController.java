package com.crowdsimulation.controller.screen.feature.floorfield;

import com.crowdsimulation.controller.Main;
import com.crowdsimulation.controller.screen.ScreenController;
import com.crowdsimulation.controller.screen.main.MainScreenController;
import com.crowdsimulation.controller.screen.main.service.InitializeNormalFloorFieldService;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.headful.QueueingFloorField;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.text.Text;

public class NormalFloorFieldController extends ScreenController {

    @FXML
    private Text promptText;

    @FXML
    private Label modeLabel;

    @FXML
    private ChoiceBox<FloorFieldMode> modeChoiceBox;

    @FXML
    private Label intensityLabel;

    @FXML
    private Slider intensitySlider;

    @FXML
    private Button deleteAllButton;

    private Parent root;

    private final SimpleDoubleProperty intensity;
    private final SimpleObjectProperty<FloorFieldMode> floorFieldMode;

    public NormalFloorFieldController() {
        this.root = null;

        this.intensity = new SimpleDoubleProperty(1.0);
        this.floorFieldMode = new SimpleObjectProperty<>(FloorFieldMode.DRAWING);
    }

    @FXML
    public void deleteAllAction() {
        // In the main controller, clear the floor field of the current target given the floor field state
        Main.mainScreenController.clearFloorFieldAction();
    }

    public void setElements() {
        InitializeNormalFloorFieldService.initializeNormalFloorField(
                promptText,
                modeLabel,
                modeChoiceBox,
                intensityLabel,
                intensitySlider,
                deleteAllButton
        );

        intensitySlider.valueProperty().bindBidirectional(intensity);
    }

    public Parent getRoot() {
        return root;
    }

    public void setRoot(Parent root) {
        this.root = root;
    }

    public double getIntensity() {
        return intensity.get();
    }

    public SimpleDoubleProperty intensityProperty() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity.set(intensity);
    }

    public FloorFieldMode getFloorFieldMode() {
        return floorFieldMode.get();
    }

    public SimpleObjectProperty<FloorFieldMode> floorFieldModeProperty() {
        return floorFieldMode;
    }

    public void setFloorFieldMode(FloorFieldMode floorFieldMode) {
        this.floorFieldMode.set(floorFieldMode);
    }

    public static void updatePromptText(Text promptText, NormalFloorFieldController.FloorFieldMode floorFieldMode) {
        String promptString = null;

        switch (floorFieldMode) {
            case DRAWING:
                promptString = "Click on an empty patch to draw a floor field value on it.";

                break;
            case DELETING:
                promptString = "Click on a patch with a floor field value to delete it. Click the Delete All button to" +
                        " delete all floor fields in this amenity.";

                break;
        }

        promptText.setText(promptString);
    }

    // For what stages need to do when the window is closed
    protected void closeAction() {
        Main.mainScreenController.endFloorFieldDrawing(false);
    }

    public enum FloorFieldMode {
        DRAWING("Drawing"),
        DELETING("Deleting");

        private final String name;

        FloorFieldMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
