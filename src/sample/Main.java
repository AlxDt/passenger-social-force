package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static final int DELAY_IN_MS = 50;

    private static final int ROWS = 30;
    private static final int COLS = 30;

    private static final double diffusionPercentage = 0.001;
    private static final int diffusionPasses = 1000;

    public static final Walkway WALKWAY = new Walkway(ROWS, COLS, diffusionPercentage, diffusionPasses);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Setup GUI
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Passenger social force");
        primaryStage.setScene(new Scene(root, ROWS * 20, COLS * 20));
        primaryStage.show();
    }
}
