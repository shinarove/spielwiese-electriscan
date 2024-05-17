package ch.nfr.userinterface.controller;

import javafx.fxml.FXML;
import javafx.stage.Stage;

/**
 * This class represents the help panel window.
 * It handles the user interactions for the help panel.
 */
public class HelpPanelController {
    /** The primary stage of the application */
    private Stage stage;

    /**
     * Sets the stage of the application.
     * @param stage the current stage of the application
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Closes the help panel window.
     */
    @FXML
    public void understoodButton() {
        stage.close();
    }


}
