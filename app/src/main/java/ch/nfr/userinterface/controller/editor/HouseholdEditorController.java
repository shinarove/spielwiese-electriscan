package ch.nfr.userinterface.controller.editor;

import ch.nfr.tablemodel.records.HouseholdRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * This class is responsible for managing the household editor window.
 * It is used through a static method to edit an existing household.
 */
public class HouseholdEditorController {
    /** The used logger for this class. */
    private static final Logger logger = Logger.getLogger(HouseholdEditorController.class.getName());
    /** The text field for the household name. */
    @FXML
    private TextField householdName;
    /** The text field for the number of persons. */
    @FXML
    private TextField numberOfPerson;
    /** The text field for the postal code. */
    @FXML
    private TextField postalCode;
    /** The label for the error output. */
    @FXML
    private Label errorOutput;
    /** The temporary household record to store the changes. */
    private HouseholdRecord tempHousehold;
    /** The stage which this scene is displayed in. */
    private Stage stage;

    /**
     * Displays the household editor window as a modal dialog.
     *
     * @param primaryStage The primary stage of the application.
     * @param householdRecord The household record to be edited.
     * @return The HouseholdRecord object representing the household edited, or null if an error occurs.
     */
    public static HouseholdRecord show(Stage primaryStage, HouseholdRecord householdRecord){
        try {
            FXMLLoader loader = new FXMLLoader(HouseholdEditorController.class.getResource("HouseholdEditorWindow.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();

            HouseholdEditorController controller = loader.getController();
            controller.stage = stage;
            controller.stage.initOwner(primaryStage);
            controller.stage.initModality(Modality.WINDOW_MODAL);

            controller.householdName.setText(householdRecord.householdName());
            controller.numberOfPerson.setText(String.valueOf(householdRecord.numberOfResidents()));
            controller.postalCode.setText(String.valueOf(householdRecord.postalCode()));

            stage.setScene(scene);
            stage.setTitle("Haushalt bearbeiten");
            stage.setResizable(false);
            stage.showAndWait();

            return controller.tempHousehold;
        } catch (IOException e) {
            logger.severe("Error loading the Household Editor window: " + e.getMessage());
        }
        return null;
    }

    /**
     * Cancels the room editing.
     * <p>
     * This method is called when the user clicks the cancel button.
     * It closes the current stage, discarding any changes made by the user.
     *
     * @param event The action event that triggered this method.
     */
    @FXML
    void cancel(ActionEvent event) {
        stage.close();
    }

    /**
     * Handles the save action when the user clicks the save button.
     * <p>
     * This method validates the user input from the text fields. If any field is left empty or contains invalid data,
     * an error message will be displayed. If all fields contain valid data, the method saves the changes to the model.
     * The following validations are performed:
     * <ul>
     *     <li>None of the fields should be empty.</li>
     *     <li>The number of persons should greater than 1.</li>
     *     <li>The postal code should be a 4-digit number from 1000 to 9658.</li>
     * </ul>
     *
     * @param event The action event that triggered this method.
     */
    @FXML
    void save(ActionEvent event) {
        if (householdName.getText().isEmpty() || numberOfPerson.getText().isEmpty() || postalCode.getText().isEmpty()) {
            setErrorOutput("Bitte füllen Sie alle Felder aus.");
        } else if (!numberOfPerson.getText().matches("([1-9]|10)")) {
            setErrorOutput("Bitte geben Sie eine Ganzzahl zwischen 1 und 10 an.");
        } else if (!postalCode.getText().matches("^([1-8]\\d{3})|(9[0-5]\\d{2})|(96[0-4]\\d)|(965[0-8])$")) {
            setErrorOutput("Bitte geben Sie eine gültige Postleitzahl an. (1000-9658)");
        } else {
            String name = householdName.getText();
            short postalCode;
            int numberOfPerson;
            try {
                postalCode = Short.parseShort(this.postalCode.getText());
                numberOfPerson = Integer.parseInt(this.numberOfPerson.getText());
            } catch (NumberFormatException e) {
                setErrorOutput("Bitte geben Sie eine Ganzzahl an.");
                return;
            }

            tempHousehold = new HouseholdRecord(name, postalCode, numberOfPerson);
            logger.info("Household changes saved.");
            stage.close();
        }
    }

    /**
     * Set the error output on the {@link #errorOutput}.
     * Makes sure that the error message is encoded with UTF-8.
     *
     * @param errorMessage the error message to be displayed.
     */
    private void setErrorOutput(String errorMessage) {
        errorOutput.setText(new String(errorMessage.getBytes(), StandardCharsets.UTF_8));
    }
}
