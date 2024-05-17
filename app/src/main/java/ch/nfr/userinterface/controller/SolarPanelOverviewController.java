package ch.nfr.userinterface.controller;

import ch.nfr.UnknownPropertyException;
import ch.nfr.tablemodel.Household;
import ch.nfr.tablemodel.SolarPanel;
import ch.nfr.tablemodel.TableModel;
import ch.nfr.tablemodel.records.SolarRecord;
import ch.nfr.userinterface.controller.editor.SolarPanelEditorController;
import ch.nfr.userinterface.model.SolarPanelOverviewModel;
import ch.nfr.userinterface.model.property.ElectriScanProperty;
import ch.nfr.userinterface.model.property.SolarPanelOverviewProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.nio.charset.StandardCharsets;

/**
 * Controller for the SolarPanelOverview.
 */
public class SolarPanelOverviewController extends OverviewController{
    /** The label for the error output. */
    @FXML
    private Label errorOutput;
    /** The label for the information output. */
    @FXML
    private Label informationLabel;
    /** The text flow for the information output. */
    @FXML
    private TextFlow informationText;
    /** The tree view for the households and solar panels. */
    @FXML
    private TreeItem<TableModel> rootHousehold;
    /** The tree view for the households and solar panels. */
    @FXML
    private TreeView<TableModel> solarPanelOverview;
    /** The primary stage of the ElectriScan application. */
    private Stage primaryStage;
    /** The model for the SolarPanelOverviewController. */
    private SolarPanelOverviewModel solarPanelOverviewModel;
    /** The clicked item in the tree view. */
    private TableModel clickedItem;

    /**
     * Initializes the SolarPanelOverviewController.
     */
    public void initialize() {
        rootHousehold = new TreeItem<>();
        rootHousehold.setExpanded(true);
        solarPanelOverview.setRoot(rootHousehold);

        solarPanelOverview.setOnMouseClicked(event -> {
            errorOutput.setText("");
            clickedItem = solarPanelOverview.getSelectionModel().getSelectedItem().getValue();
            if (clickedItemIsHousehold()) {
                informationLabel.setText(getStringInUTF8("Haushalt Informationen:"));
                fillHouseholdInformation(informationText, clickedItem);
            } else if (clickedItemIsSolarPanel()) {
                informationLabel.setText(getStringInUTF8("SolarPanel Informationen:"));
                fillSolarPanelInformation();
            }
        });
    }

    /**
     * Sets the model and the primary stage for the SolarPanelOverviewController.
     *
     * @param solarPanelOverviewModel The model for the SolarPanelOverviewController.
     * @param primaryStage            The primary stage for the SolarPanelOverviewController.
     */
    public void setModelAndPrimaryStage(SolarPanelOverviewModel solarPanelOverviewModel, Stage primaryStage) {
        this.solarPanelOverviewModel = solarPanelOverviewModel;
        this.primaryStage = primaryStage;

        this.solarPanelOverviewModel.addPropertyChangeListenerElectriScan(evt -> {
            switch (ElectriScanProperty.parseProperty(evt.getPropertyName())) {
                case LOAD_HOUSEHOLD -> {
                    Household household = (Household) evt.getNewValue();
                    rootHousehold.getChildren().clear();
                    rootHousehold.setValue(household);

                    buildTreeView(household);
                    informationLabel.setText("");
                    informationText.getChildren().clear();
                }
                case EDIT_HOUSEHOLD -> {
                    if (clickedItemIsHousehold()) {
                        fillHouseholdInformation(informationText, clickedItem);
                    }
                    solarPanelOverview.refresh();
                }
                case LOAD_DIRECTORY -> {
                }
                case REMOVE_HOUSEHOLD -> {
                    rootHousehold.getChildren().clear();
                    informationLabel.setText("");
                    informationText.getChildren().clear();
                }
                case null -> throw new UnknownPropertyException("Unknown property: " + evt.getPropertyName());
            }
        });

        this.solarPanelOverviewModel.addPropertyChangeListener(evt -> {
            switch (SolarPanelOverviewProperty.parseProperty(evt.getPropertyName())) {
                case ADD_SOLAR_PANEL -> {
                    SolarPanel solarPanel = (SolarPanel) evt.getNewValue();
                    TreeItem<TableModel> solarPanelitem = new TreeItem<>(solarPanel);
                    rootHousehold.getChildren().add(solarPanelitem);
                }
                case EDIT_SOLAR_PANEL -> {
                    fillSolarPanelInformation();
                }
                case REMOVE_SOLAR_PANEL -> {
                    int solarPanelid = ((SolarPanel) evt.getOldValue()).getId();
                    rootHousehold.getChildren().removeIf(solarPanelitem -> ((SolarPanel) solarPanelitem.getValue()).getId() == solarPanelid);
                }
                case null -> throw new UnknownPropertyException("Unknown property: " + evt.getPropertyName());
            }
        });


    }

    /**
     * Builds the tree view for the households and solar panels.
     *
     * @param household The household to be displayed in the tree view.
     */
    private void buildTreeView(Household household) {
        for (SolarPanel solarPanel : household.getAllSolarPanels()) {
            TreeItem<TableModel> solarPanelitem = new TreeItem<>(solarPanel);
            rootHousehold.getChildren().add(solarPanelitem);
        }
    }

    /**
     * Fills the information text with the solar panel information.
     */
    private void fillSolarPanelInformation() {
        informationText.getChildren().clear();
        SolarPanel solarPanel = (SolarPanel) clickedItem;
        String name = solarPanel.getName();
        double area = solarPanel.getArea();
        String orientation = solarPanel.getOrientation().getGermanName();

        String firstPart = new String("Name:\t\t\t".getBytes(), StandardCharsets.UTF_8);
        String thirdPart = new String(
                ("\nFläche:\t\t\t" + area + " m²" +
                        "\nAusrichtung:\t\t" + orientation)
                        .getBytes(), StandardCharsets.UTF_8);

        String information = firstPart + name + thirdPart;
        Text text = new Text(information);
        informationText.getChildren().add(text);
    }

    /**
     * Adds a new SolarPanel to the household.
     *
     * @param event The event that triggers the action.
     */
    @FXML
    void addSolarPanel(ActionEvent event) {
        if (solarPanelOverviewModel.getHousehold() == null) {
            setErrorOutput("Es wurde kein Haushalt geladen. Bitte laden Sie zuerst einen Haushalt.");
        } else {
            SolarRecord solarRecord = SolarPanelEditorController.show(primaryStage);
            if (solarRecord != null) {
                solarPanelOverviewModel.addSolarPanel(solarRecord);
            }
        }
    }

    /**
     * Edits a SolarPanel in the household.
     *
     * @param event The event that triggers the action.
     */
    @FXML
    void editSolarPanel(ActionEvent event) {
        if (!clickedItemIsSolarPanel()) {
            setErrorOutput("Bitte wählen Sie einen SolarPanel aus!");
        } else {
            SolarPanel solarPanel = (SolarPanel) clickedItem;
            SolarRecord solarRecord = SolarPanelEditorController.show(primaryStage, solarPanel.toRecord());
            if (solarRecord != null) {
                solarPanelOverviewModel.editSolarPanel(solarPanel.getId(), solarRecord);
            }
            solarPanelOverview.refresh();
        }
    }

    /**
     * Removes a SolarPanel from the household.
     *
     * @param event The event that triggers the action.
     */
    @FXML
    void removeSolarPanel(ActionEvent event) {
        if (!clickedItemIsSolarPanel()) {
            setErrorOutput("Bitte wählen Sie einen SolarPanel aus!");
        } else {
            primaryStage.getScene().getRoot().setDisable(true);
            String warningMessage = "Möchtest du den SolarPanel wirklich löschen?";
            boolean changeAccepted = WarningController.showWarning(warningMessage);
            primaryStage.getScene().getRoot().setDisable(false);
            if (changeAccepted) {
                solarPanelOverviewModel.removeSolarPanel(((SolarPanel) clickedItem).getId());
            } else {
                setErrorOutput("Löschen des SolarPanels abgebrochen!");
            }
        }
    }

    /**
     * Sets the error output label to the given error message.
     *
     * @param errorMessage The error message to be displayed.
     */
    private void setErrorOutput(String errorMessage) {
        errorOutput.setText(getStringInUTF8(errorMessage));
    }

    /**
     * Returns the given string in UTF-8 encoding.
     *
     * @param string The string to be encoded.
     * @return The string in UTF-8 encoding.
     */
    private String getStringInUTF8(String string) {
        return new String(string.getBytes(), StandardCharsets.UTF_8);
    }

    /**
     * Checks if the clicked item is a household.
     *
     * @return True if the clicked item is a household, false otherwise.
     */
    private boolean clickedItemIsHousehold() {
        return clickedItem instanceof Household;
    }

    /**
     * Checks if the clicked item is a solar panel.
     *
     * @return True if the clicked item is a solar panel, false otherwise.
     */
    private boolean clickedItemIsSolarPanel() {
        return clickedItem instanceof SolarPanel;
    }

}
