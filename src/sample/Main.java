package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static final int DELAY_IN_MS = 50;

    private static final int ROWS = 60;
    private static final int COLUMNS = 106;

    public static final Floor FLOOR = new Floor(ROWS, COLUMNS);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Setup GUI
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Crowd dynamics");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
