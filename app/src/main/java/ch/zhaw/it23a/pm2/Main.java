// This Java source file was generated by the Gradle 'init' task.
package ch.zhaw.it23a.pm2;

import ch.zhaw.it23a.pm2.userinterface.controller.ElectriScanController;
import ch.zhaw.it23a.pm2.userinterface.model.ElectriScanModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main class of the ElectriScan application.
 * This class is the entry point of the application and starts the main window.
 */
public class Main extends Application {

    /**
     * Main method of the ElectriScan application.
     * This method starts the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Start method of the ElectriScan application.
     * @param stage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     */
    @Override
    public void start(Stage stage) {
        mainWindow(stage);
    }

    /**
     * Opens the main window of the ElectriScan application.
     * @param primaryStage the primary stage for this application
     */
    private void mainWindow(Stage primaryStage) {
        ElectriScanModel electriScanModel = new ElectriScanModel();

        try {
            FXMLLoader loader = new FXMLLoader(ElectriScanController.class.getResource("ElectriScanWindow.fxml"));
            Scene scene = new Scene(loader.load());

            ElectriScanController controller = loader.getController();
            controller.setModelAndPrimaryStage(electriScanModel, primaryStage);

            primaryStage.setScene(scene);
            primaryStage.setTitle("ElectriScan");
            primaryStage.setMinWidth(640 + 15);
            primaryStage.setMinHeight(400 + 45);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Error while opening main window: " + e.getMessage());
        }
    }
}
