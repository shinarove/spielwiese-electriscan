package ch.zhaw.it23a.pm2.userinterface.controller.editor;

import ch.zhaw.it23a.pm2.tablemodel.records.RoomRecord;
import ch.zhaw.it23a.pm2.tablemodel.RoomType;
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
 * This class is responsible for managing the room editor window.
 * It is used through two static method to add a new room or edit an existing room.
 */
public class RoomEditorController {
    /** The used logger for this class. */
    private static final Logger logger = Logger.getLogger(RoomEditorController.class.getName());
    /** The choice box for the room type. */
    @FXML
    private ChoiceBox<String> roomType;
    /** The text field for the room name. */
    @FXML
    private TextField roomName;
    /** The text field for the area. */
    @FXML
    private TextField area;
    /** The label for the error output. */
    @FXML
    private Label errorOutput;
    /** The temporary room record to store the changes. */
    private RoomRecord tempRoom;
    /** The stage which this scene is displayed in. */
    private Stage stage;

    /**
     * Initialize the room editor window.
     * It adds the room types to the choice box, with their German name.
     */
    public void initialize() {
        for (RoomType type : RoomType.values()) {
            roomType.getItems().add(new String(type.getGermanName().getBytes(), StandardCharsets.UTF_8));
        }
    }

    /**
     * Displays the room editor window as a modal dialog.
     *
     * @param primaryStage The primary stage of the application.
     * @return The RoomRecord object representing the room added or edited, or null if an error occurs.
     */
    public static RoomRecord show(Stage primaryStage){
        try {
            FXMLLoader loader = new FXMLLoader(RoomEditorController.class.getResource("RoomEditorWindow.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();

            RoomEditorController controller = loader.getController();
            controller.stage = stage;
            controller.stage.initOwner(primaryStage);
            controller.stage.initModality(Modality.WINDOW_MODAL);


            stage.setScene(scene);
            stage.setTitle(new String("Raum hinzufügen".getBytes(), StandardCharsets.UTF_8));
            stage.setResizable(false);
            stage.showAndWait();

            return controller.tempRoom;
        } catch (IOException e) {
            logger.warning("Error while initialize room editor window: " + e.getMessage());
        }
        return null;
    }

    /**
     * Displays the room editor window as a modal dialog.
     * Sets the initial values of the room record to the text fields and choice box.
     *
     * @param primaryStage The primary stage of the application.
     * @param roomRecord The room record for the initialized values.
     * @return The RoomRecord object representing the room added or edited, or null if an error occurs.
     */
    public static RoomRecord show(Stage primaryStage, RoomRecord roomRecord){
        try {
            FXMLLoader loader = new FXMLLoader(RoomEditorController.class.getResource("RoomEditorWindow.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();

            RoomEditorController controller = loader.getController();
            controller.stage = stage;
            controller.stage.initOwner(primaryStage);
            controller.stage.initModality(Modality.WINDOW_MODAL);

            controller.roomType.setValue(new String(roomRecord.roomType().getGermanName().getBytes(), StandardCharsets.UTF_8));
            controller.roomName.setText(roomRecord.roomName());
            controller.area.setText(String.valueOf(roomRecord.area()));

            stage.setScene(scene);
            stage.setTitle(new String("Raum bearbeiten".getBytes(), StandardCharsets.UTF_8));
            stage.setResizable(false);
            stage.showAndWait();

            return controller.tempRoom;
        } catch (IOException e) {
            logger.warning("Error while initialize room editor window: " + e.getMessage());
        }
        return null;
    }

    /**
     * Cancels the room editing.
     * This method is called when the user clicks the cancel button.
     * It closes the current stage, discarding any changes made by the user.
     *
     * @param event The action event that triggered this method.
     */
    @FXML
    void cancel(ActionEvent event) {
        logger.info("Room editing canceled.");
        stage.close();
    }

    /**
     * Handles the save action when the user clicks the save button.
     * This method validates the user input from the text fields and the choice box. If any field is left empty or contains invalid data,
     * an error message will be displayed. If all fields contain valid data, the method saves the changes to the model.
     * The following validations are performed:
     * <ul>
     *     <li>None of the fields should be empty.</li>
     *     <li>The area should be a number greater than 1.</li>
     *     <li>The room type should be selected from the choice box.</li>
     * </ul>
     *
     * @param event The action event that triggered this method.
     */
    @FXML
    void save(ActionEvent event) {
        if (roomName.getText().isEmpty() || area.getText().isEmpty() || roomType.getValue() == null) {
            setErrorOutput("Bitte füllen Sie alle Felder aus.");
        } else if (!area.getText().matches("\\d+(?:\\.\\d)?")) {
            setErrorOutput("Bitte geben Sie eine Zahl für die Fläche ein.");
        } else {
            RoomType type = RoomType.parseRoomType(roomType.getValue());
            String name = roomName.getText();
            double roomSizeInM2 = Double.parseDouble(area.getText());

            tempRoom = new RoomRecord(type, name, roomSizeInM2);
            logger.info("Room changes saved with values: " + tempRoom + ".");
            stage.close();
        }
    }

    /**
     * Set the error output on the {@link #errorOutput}.
     * Makes sure that the error message is encoded with UTF-8.
     *
     * @param errorMessage The error message to be displayed.
     */
    private void setErrorOutput(String errorMessage) {
        errorOutput.setText(new String(errorMessage.getBytes(), StandardCharsets.UTF_8));
    }
}
