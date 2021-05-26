package com.crowdsimulation.controller.screen.feature.floorfield;

import com.crowdsimulation.controller.Main;
import com.crowdsimulation.controller.screen.ScreenController;
import com.crowdsimulation.controller.screen.alert.AlertController;
import com.crowdsimulation.controller.screen.service.floorfield.InitializeNormalFloorFieldService;
import com.crowdsimulation.model.core.environment.station.patch.floorfield.headful.QueueingFloorField;
import com.crowdsimulation.model.core.environment.station.patch.patchobject.passable.Queueable;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;

import java.util.List;

public class NormalFloorFieldController extends ScreenController {

    @FXML
    private Text promptText;

    @FXML
    private Label modeLabel;

    @FXML
    private ChoiceBox<FloorFieldMode> modeChoiceBox;

    @FXML
    private Label directionLabel;

    @FXML
    private ChoiceBox<QueueingFloorField.FloorFieldState> floorFieldStateChoiceBox;

    @FXML
    private Label intensityLabel;

    @FXML
    private Slider intensitySlider;

    @FXML
    private TextField intensityTextField;

    @FXML
    private Button validateButton;

    @FXML
    private Button deleteAllButton;

    private Parent root;

    private final SimpleDoubleProperty intensity;
    private final SimpleObjectProperty<FloorFieldMode> floorFieldMode;

    private QueueingFloorField.FloorFieldState floorFieldState;

    public NormalFloorFieldController() {
        this.root = null;

        this.intensity = new SimpleDoubleProperty(1.0);
        this.floorFieldMode = new SimpleObjectProperty<>(FloorFieldMode.DRAWING);

        this.floorFieldState = null;
    }

    @FXML
    public void validateAction() {
        // Check whether the floor fields of the current queueable are complete
        Queueable target = Main.simulator.getCurrentFloorFieldTarget();

        if (target.isFloorFieldsComplete()) {
            AlertController.showSimpleAlert(
                    "Floor fields valid",
                    "Floor fields valid",
                    "The floor fields of this amenity are complete.",
                    Alert.AlertType.INFORMATION
            );
        } else {
            AlertController.showSimpleAlert(
                    "Floor fields invalid",
                    "Floor fields invalid",
                    "The floor fields of this amenity are incomplete.",
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    public void deleteAllAction() {
        // In the main controller, clear the floor field of the current target given the floor field state
        Main.mainScreenController.deleteFloorFieldAction();
    }

    @Override
    public void setElements() {
        InitializeNormalFloorFieldService.initializeNormalFloorField(
                promptText,
                modeLabel,
                modeChoiceBox,
                directionLabel,
                floorFieldStateChoiceBox,
                intensityLabel,
                intensitySlider,
                intensityTextField,
                validateButton,
                deleteAllButton
        );

        intensitySlider.valueProperty().bindBidirectional(intensity);

        StringConverter<Number> stringConverter = new NumberStringConverter();
        intensityTextField.textProperty().bindBidirectional(intensity, stringConverter);

        // Restrict the value of the text field to numbers [0.1, 1.0]
        intensityTextField.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue) {
                String intensityText = intensityTextField.getText();

                try {
                    double tentativeIntensity = Double.parseDouble(intensityText);

                    if (tentativeIntensity < 0.1) {
                        intensityTextField.setText("0.1");
                    } else if (tentativeIntensity > 1.0) {
                        intensityTextField.setText("1.0");
                    }
                } catch (NumberFormatException ex) {
                    intensityTextField.setText("1.0");
                }
            }
        }));
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

    public QueueingFloorField.FloorFieldState getFloorFieldState() {
        return floorFieldState;
    }

    public void setFloorFieldState(QueueingFloorField.FloorFieldState floorFieldState) {
        this.floorFieldState = floorFieldState;
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

        String finalPromptString = promptString;

        promptText.setText(finalPromptString);
    }

    // For what stages need to do when the window is closed
    protected void closeAction() {
        Main.mainScreenController.endFloorFieldDrawing(false);
    }

    public void updateDirectionChoiceBox() {
        // Initialize the elements of the choice box with directions based on the floor field states of the current
        // target
        List<QueueingFloorField.FloorFieldState> floorFieldStates
                = Main.simulator.getCurrentFloorFieldTarget().retrieveFloorFieldStates();

        floorFieldStateChoiceBox.setItems(FXCollections.observableArrayList(
                floorFieldStates
        ));

        floorFieldStateChoiceBox.getSelectionModel().select(0);
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
