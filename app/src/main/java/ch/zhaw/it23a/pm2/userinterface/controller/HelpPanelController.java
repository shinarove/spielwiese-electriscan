package ch.zhaw.it23a.pm2.userinterface.controller;

import ch.zhaw.it23a.pm2.userinterface.model.ElectriScanModel;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import java.util.logging.Logger;

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
