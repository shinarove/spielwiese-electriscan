package ch.zhaw.it23a.pm2.userinterface.controller;

import ch.zhaw.it23a.pm2.UnknownPropertyException;
import ch.zhaw.it23a.pm2.tablemodel.records.HouseholdRecord;
import ch.zhaw.it23a.pm2.userinterface.controller.editor.HouseholdEditorController;
import ch.zhaw.it23a.pm2.userinterface.model.ElectriScanModel;
import ch.zhaw.it23a.pm2.userinterface.model.property.ElectriScanProperty;
import ch.zhaw.it23a.pm2.tablemodel.Household;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * The ElectriScanController class is responsible for managing the user interface of the ElectriScan application.
 * It handles user interactions and updates the model accordingly.
 * It also listens for changes in the model and updates the view.
 * <p>
 * The controller manages several views including the device overview, cost calculation, and solar panel overview.
 * It also handles the addition, editing, and removal of households.
 */
public class ElectriScanController {
    /**
     * Logger for logging messages.
     */
    private static final Logger logger = Logger.getLogger(ElectriScanController.class.getName());
    /**
     * The placeholder for the device overview.
     */
    @FXML
    private AnchorPane deviceOverview;
    /**
     * The placeholder for the cost overview.
     */
    @FXML
    private AnchorPane costOverview;
    /**
     * The placeholder for the solar panel overview.
     */
    @FXML
    private AnchorPane solarPanelOverview;
    /**
     * Holds the labels of different households from household directory.
     */
    @FXML
    private VBox householdOverview;
    /**
     * The text area to display information for the user.
     */
    @FXML
    private TextArea textOutput;
    /**
     * The primary stage of the ElectriScan application.
     */
    private Stage primaryStage;
    /**
     * The model for the ElectriScan application.
     */
    private ElectriScanModel electriScanModel;
    /**
     * The saved id of the clicked household.
     */
    private int clickedHouseholdId;
    /**
     * The saved label of the clicked household.
     */
    private Label clickedHousehold;

    /**
     * Initializes the controller by setting the clicked household id and label to 0 and null.
     */
    public void initialize() {
        clickedHouseholdId = 0;
        clickedHousehold = null;
    }

    /**
     * Sets the model and primary stage for the ElectriScanController.
     * It also adds property change listeners to the model to handle changes in the text output and other properties.
     * The method also initializes the device overview and solar panel overview.
     *The property change listeners handle the following properties:
     * {@link ElectriScanProperty#LOAD_HOUSEHOLD} - Does nothing
     * {@link ElectriScanProperty#EDIT_HOUSEHOLD} - Changes the text in the {@link #clickedHousehold} with the new name.
     * {@link ElectriScanProperty#REMOVE_HOUSEHOLD} - Reloads the household directory and if the new value is null, resets the clicked household id and label.
     * {@link ElectriScanProperty#LOAD_DIRECTORY} - Clears the {@link #householdOverview}
     * and adds the new households label to the {@link #householdOverview} from the model.
     * Unknown Property - Throws an {@link UnknownPropertyException}.
     * @param electriScanModel The model for the ElectriScan application.
     * @param primaryStage     The primary stage of the ElectriScan application.
     */
    public void setModelAndPrimaryStage(ElectriScanModel electriScanModel, Stage primaryStage) {
        this.electriScanModel = electriScanModel;
        this.primaryStage = primaryStage;
        this.electriScanModel.getTextOutput().addPropertyChangeListener(evt -> textOutput.appendText(evt.getNewValue() + "\n"));

        this.electriScanModel.addPropertyChangeListener(evt -> {
            logger.info("Property changed: " + evt.getPropertyName());
            switch (ElectriScanProperty.parseProperty(evt.getPropertyName())) {
                case LOAD_HOUSEHOLD -> {
                }
                case EDIT_HOUSEHOLD -> clickedHousehold.setText((String) evt.getNewValue());
                case REMOVE_HOUSEHOLD -> {
                    electriScanModel.loadFileDirectory();
                    if (evt.getOldValue() == null) {
                        clickedHouseholdId = 0;
                        clickedHousehold = null;
                    }
                }
                case LOAD_DIRECTORY -> {
                    householdOverview.getChildren().clear();
                    for (final int householdId : electriScanModel.getHouseholdOverview().keySet()) {
                        String household = electriScanModel.getHouseholdOverview().get(householdId);
                        Label householdLabel = createHouseholdLabel(householdId, household);
                        householdOverview.getChildren().add(householdLabel);
                    }
                }
                case null -> throw new UnknownPropertyException("Unknown property: " + evt.getPropertyName());
            }
        });

        this.primaryStage.setOnCloseRequest(event -> electriScanModel.tearDown());
        this.electriScanModel.loadFileDirectory();

        initializeDeviceOverview();
        initializeSolarPanelOverview();
        initializeCostOverview();
    }

    /**
     * Creates a new label for a household with the given id and name.
     * The label is clickable and when clicked, the style of the label changes to bold
     * and the id of the clicked household is saved.
     *
     * @param householdId   The id of the household.
     * @param householdName The name of the household.
     * @return The created label for the household.
     */
    private Label createHouseholdLabel(int householdId, String householdName) {
        logger.info("Create household label: " + householdId + ", " + householdName);
        Label householdLabel = new Label(householdName);
        householdLabel.setOnMouseClicked(event -> {
            if (clickedHousehold != null) {
                // Ändere den Style des vorherigen Labels zurück
                clickedHousehold.setStyle("-fx-font-weight: normal;");
            }
            clickedHouseholdId = householdId;
            clickedHousehold = (Label) event.getSource();
            // Ändere den Style des aktuellen Labels
            clickedHousehold.setStyle("-fx-font-weight: bold;");
        });
        return householdLabel;
    }

    /**
     * Initializes the device overview window by loading the FXML file
     * and putting it into the {@link #deviceOverview} anchor pane.
     */
    private void initializeDeviceOverview() {
        try {
            FXMLLoader loader = new FXMLLoader(DeviceOverviewController.class.getResource("DeviceOverviewWindow.fxml"));
            Parent rootNode = loader.load();

            DeviceOverviewController controller = loader.getController();
            controller.setModelAndPrimaryStage(electriScanModel.getDeviceOverviewModel(), primaryStage);

            deviceOverview.getChildren().add(rootNode);

            AnchorPane.setTopAnchor(rootNode, 0.0);
            AnchorPane.setRightAnchor(rootNode, 0.0);
            AnchorPane.setBottomAnchor(rootNode, 0.0);
            AnchorPane.setLeftAnchor(rootNode, 0.0);
        } catch (IOException e) {
            System.err.println("Error while initialize device overview window: " + e.getMessage());
        }
    }

    /**
     * Initializes the cost overview window by loading the FXML file
     * and putting it into the {@link #costOverview} anchor pane.
     */
    private void initializeCostOverview() {
        try {
            FXMLLoader loader = new FXMLLoader(CostCalculationController.class.getResource("CostOverviewWindow.fxml"));
            Parent rootNode = loader.load();

            CostCalculationController controller = loader.getController();
            controller.setModelAndPrimaryStage(electriScanModel.getCostOverviewModel(), primaryStage);

            costOverview.getChildren().add(rootNode);

            AnchorPane.setTopAnchor(rootNode, 0.0);
            AnchorPane.setRightAnchor(rootNode, 0.0);
            AnchorPane.setBottomAnchor(rootNode, 0.0);
            AnchorPane.setLeftAnchor(rootNode, 0.0);
        } catch (IOException e) {
            System.err.println("Error while initialize cost overview window: " + e.getMessage());
        }
    }

    /**
     * Initializes the device overview window by loading the FXML file
     * and putting it into the {@link #solarPanelOverview} anchor pane.
     */
    private void initializeSolarPanelOverview() {
        try {
            FXMLLoader loader = new FXMLLoader(SolarPanelOverviewController.class.getResource("SolarPanelOverviewWindow.fxml"));
            Parent rootNode = loader.load();

            SolarPanelOverviewController controller = loader.getController();
            controller.setModelAndPrimaryStage(electriScanModel.getSolarPanelOverviewModel(), primaryStage);

            solarPanelOverview.getChildren().add(rootNode);

            AnchorPane.setTopAnchor(rootNode, 0.0);
            AnchorPane.setRightAnchor(rootNode, 0.0);
            AnchorPane.setBottomAnchor(rootNode, 0.0);
            AnchorPane.setLeftAnchor(rootNode, 0.0);
        } catch (IOException e) {
            System.err.println("Error while initialize solar panel overview window: " + e.getMessage());
        }
    }

    /**
     * Imports a household from a file.
     *
     * @param event the action event
     */
    @FXML
    void importHousehold(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Haushalt importieren aus...");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON-Dateien", "*.json"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            electriScanModel.importHousehold(selectedFile.getAbsolutePath());
        } else {
            logger.info("Import abgebrochen.");
            electriScanModel.getTextOutput().set("Import abgebrochen.");
        }
    }

    /**
     * Exports a household to a directory.
     */
    @FXML
    void exportHousehold() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Haushalt exportieren in...");
        File selectedDirectory = directoryChooser.showDialog(primaryStage);
        if (selectedDirectory != null) {
            electriScanModel.exportHousehold(selectedDirectory.getAbsolutePath());
        } else {
            logger.info("Export abgebrochen.");
            electriScanModel.getTextOutput().set("Export abgebrochen.");
        }
    }

    /**
     * Refreshes the household overview by loading the household directory from the model.
     *
     * @param event the action event
     */
    @FXML
    void refreshHouseholdOverview(ActionEvent event) {
        electriScanModel.loadFileDirectory();
    }

    /**
     * Quits the application by closing the primary stage.
     *
     * @param event the action event
     */
    @FXML
    void quitApplication(ActionEvent event) {
        primaryStage.close();
        electriScanModel.tearDown();
    }

    /**
     * Shows the help window with the help text.
     *
     * @param event the action event
     */
    @FXML
    void help(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(HelpPanelController.class.getResource("HelpPanelWindow.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();

            HelpPanelController controller = loader.getController();
            stage.initOwner(primaryStage);
            stage.initModality(Modality.WINDOW_MODAL);
            controller.setStage(stage);


            stage.setScene(scene);
            stage.setTitle("Hilfe");
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            System.err.println("Error while initialize HelpPanelController overview window: " + e.getMessage());
        }
    }

    /**
     * Handles the action event for adding a household.
     * This method calls the {@link ElectriScanModel#addHousehold()} method to add a new household.
     *
     * @param event the action event
     */
    @FXML
    void addHousehold(ActionEvent event) {
        this.electriScanModel.addHousehold();
    }

    /**
     * Handles the action event for editing a household.
     * This method first checks if a household is loaded. If not, it sets an error message in the model's text output.
     * If a household is loaded, it disables the primary stage and opens the HouseholdEditorController to edit the household.
     * If the household record is not null after editing, it calls the model's editHousehold method with the new household record.
     * Finally, it re-enables the primary stage.
     *
     * @param event the action event
     */
    @FXML
    void editHousehold(ActionEvent event) {
        Household household = electriScanModel.getHousehold();
        if (household == null) {
            electriScanModel.getTextOutput().set("Kein Haushalt geladen! Um einen Haushalt zu bearbeiten," +
                    "müssen Sie einen Haushalt aus der Liste laden");
        } else {
            HouseholdRecord householdRecord = HouseholdEditorController.show(primaryStage, household.toHouseholdRecord());
            if (householdRecord != null) {
                electriScanModel.editHousehold(householdRecord);
            }
        }
    }

    /**
     * Handles the action event for removing a household.
     * This method first checks if a household is selected. If not, it sets an error message in the model's text output.
     * If a household is selected, it disables the primary stage and shows a warning message to the user.
     * If the user accepts the warning, it calls the model's removeHousehold method with the selected household id.
     * Finally, it re-enables the primary stage.
     *
     * @param event the action event
     */
    @FXML
    void removeHousehold(ActionEvent event) {
        if (clickedHouseholdId == 0) {
            electriScanModel.getTextOutput().set("Kein Haushalt ausgewählt! Um einen Haushalt zu löschen," +
                    "wählen Sie bitte einen Haushalt aus der Liste aus.");
        } else {
            primaryStage.getScene().getRoot().setDisable(true);
            String warningMessage = "Möchtest du den Haushalt wirklich löschen?\n" +
                    "Alle darin enthaltene Räume und Geräte werden unwiderruflich gelöscht!";
            boolean changeAccepted = WarningController.showWarning(warningMessage);
            primaryStage.getScene().getRoot().setDisable(false);

            if (changeAccepted) {
                electriScanModel.removeHousehold(clickedHouseholdId);
            } else {
                electriScanModel.getTextOutput().set("Löschen abgebrochen.");
            }
        }
    }

    /**
     * Handles the action event for loading a household.
     * This method first checks if a household is selected. If not, it sets an error message in the model's text output.
     * If a household is selected, it calls the model's loadHousehold method with the selected household id.
     *
     * @param event the action event
     */
    @FXML
    void loadClickedHousehold(ActionEvent event) {
        if (clickedHouseholdId == 0) {
            electriScanModel.getTextOutput().set("Kein Haushalt ausgewählt! Um einen Haushalt zu laden," +
                    "wählen Sie bitte einen Haushalt aus der Liste aus.");
        } else {
            electriScanModel.loadHousehold(clickedHouseholdId);
        }
    }
}
