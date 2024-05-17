package ch.zhaw.it23a.pm2.userinterface.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Controller for handling warning messages in the application.
 * This class is responsible for displaying warning messages to the user and waiting for their response.
 * The warning messages are displayed in a new window.
 * The class uses JavaFX for creating and managing the warning window.
 */
public class WarningController {
    /** Label to display the warning message. */
    @FXML
    private Label warningMessage;

    /** Boolean to check if the change is accepted. */
    private boolean changeIsAccepted = false;

    /** Stage of the warning window. */
    private Stage stage;

    /**
     * Displays a warning message to the user and waits for their response.
     * The warning message is displayed in a new window.
     *
     * @param message The warning message to be displayed.
     * @return true if the user accepts the warning and wants to proceed, false otherwise.
     */
    public static boolean showWarning(String message) {
        boolean result = false;
        try {
            FXMLLoader loader = new FXMLLoader(WarningController.class.getResource("WarningWindow.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();

            WarningController controller = loader.getController();
            controller.warningMessage.setText(new String(message.getBytes(), StandardCharsets.UTF_8));
            controller.stage = stage;

            stage.setScene(scene);
            stage.setTitle("Warnung");
            stage.setResizable(false);
            stage.showAndWait();

            result = controller.changeIsAccepted;
        } catch (IOException e) {
            System.err.println("Error while initialize warning window: " + e.getMessage());
        }
        return result;
    }

    /**
     * Closes the warning window and sets the changeIsAccepted boolean to true.
     * @param event The event that triggered the method.
     */
    @FXML
    void acceptOperation(ActionEvent event) {
        changeIsAccepted = true;
        stage.close();
    }

    /**
     * Closes the warning window and sets the changeIsAccepted boolean to false.
     * @param event The event that triggered the method.
     */
    @FXML
    void cancelOperation(ActionEvent event) {
        changeIsAccepted = false;
        stage.close();
    }
}
