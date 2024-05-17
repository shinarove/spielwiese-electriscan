package ch.nfr.userinterface.controller.editor;

import ch.nfr.tablemodel.Orientation;
import ch.nfr.tablemodel.records.SolarRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * <p>This class is the controller for the SolarPanel Editor in the GUI.
 * It handles the user interactions for editing a solar panel.</p>
 */
public class SolarPanelEditorController {

    /**
     * Logger for logging messages.
     */
    private static final Logger logger = Logger.getLogger(SolarPanelEditorController.class.getName());

    /**
     * Text field for the solar panel error messages.
     */
    @FXML
    private Label errorOutput;

    /**
     * Choice box for the orientation.
     */
    @FXML
    private ChoiceBox<String> orientation;

    /**
     * Text field for the solar panel area.
     */
    @FXML
    private TextField solarPanelArea;

    /**
     * Text field for the solar panel name.
     * The solar panel name is the name of the solar panel.
     * Stage for the solar panel editor.
     */
    @FXML
    private TextField solarPanelName;
    /**
     * The temporary solar record to store the changes.
     */
    private SolarRecord tempSolar;
    /**
     * The stage of the SolarPanelEditorController
     */
    private Stage stage;

    /**
     * Shows the SolarPanel editor window
     *
     * @return the SolarRecord or null if the input is invalid
     */
    public static SolarRecord show(Stage primaryStage) {
        try {
            logger.info("Loading the SolarPanel editor window");
            FXMLLoader loader = new FXMLLoader(SolarPanelEditorController.class.getResource("SolarPanelEditorWindow.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();

            SolarPanelEditorController controller = loader.getController();
            controller.stage = stage;
            controller.stage.initOwner(primaryStage);
            controller.stage.initModality(Modality.WINDOW_MODAL);

            stage.setScene(scene);
            stage.setTitle(new String("SolarPanel hinzufügen".getBytes(), StandardCharsets.UTF_8));
            stage.setResizable(false);
            stage.showAndWait();

            return controller.tempSolar;
        } catch (IOException e) {
            logger.severe("Error loading the SolarPanel editor window: " + e.getMessage());
        }
        return null;
    }

    /**
     * Shows the SolarPanel editor window
     *
     * @return the SolarRecord or null if the input is invalid
     */
    public static SolarRecord show(Stage primaryStage, SolarRecord solarRecord) {
        try {
            logger.info("Loading the SolarPanel editor window");
            FXMLLoader loader = new FXMLLoader(SolarPanelEditorController.class.getResource("SolarPanelEditorWindow.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();

            SolarPanelEditorController controller = loader.getController();
            controller.stage = stage;
            controller.stage.initOwner(primaryStage);
            controller.stage.initModality(Modality.WINDOW_MODAL);

            controller.solarPanelName.setText(solarRecord.solarPanelName());
            controller.solarPanelArea.setText(String.valueOf(solarRecord.area()));
            controller.orientation.setValue(new String(solarRecord.orientation().getGermanName().getBytes(), StandardCharsets.UTF_8));

            stage.setScene(scene);
            stage.setTitle(new String("SolarPanel hinzufügen".getBytes(), StandardCharsets.UTF_8));
            stage.setResizable(false);
            stage.showAndWait();

            return controller.tempSolar;
        } catch (IOException e) {
            logger.severe("Error loading the SolarPanel editor window: " + e.getMessage());
        }
        return null;

    }

    /**
     * Adds the orientations to the choice box
     */
    public void initialize() {
        for (Orientation orientation : Orientation.values()) {
            this.orientation.getItems().add(new String(orientation.getGermanName().getBytes(), StandardCharsets.UTF_8));
            if (orientation == Orientation.NORTH) {
                this.orientation.setValue(new String(orientation.getGermanName().getBytes(), StandardCharsets.UTF_8));
            }
        }
    }


    /**
     * Closes the window
     *
     * @param event the event that triggered the method
     */
    @FXML
    void cancel(ActionEvent event) {
        stage.close();
    }

    /**
     * Saves the input and closes the window
     *
     * @param event the event that triggered the method
     */
    @FXML
    void save(ActionEvent event) {
        boolean conversionFailure = false;
        if (!solarPanelName.getText().isEmpty() && !solarPanelArea.getText().isEmpty()) {
            double area = 0;
            try {
                area = Double.parseDouble(solarPanelArea.getText());
            } catch (NumberFormatException e) {
                errorOutput.setText(new String("Bitte geben Sie eine gültige Fläche ein.".getBytes(), StandardCharsets.UTF_8));
                conversionFailure = true;
            }
            if (!conversionFailure) {
                System.out.println(orientation.getValue());
                tempSolar = new SolarRecord(solarPanelName.getText(), area,
                        Orientation.parseOrientation(orientation.getValue()));
                stage.close();
            }

        } else {
            errorOutput.setText(new String("Bitte füllen Sie alle Felder aus.".getBytes(), StandardCharsets.UTF_8));
        }
    }
}

