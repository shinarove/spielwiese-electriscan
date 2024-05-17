package ch.nfr.userinterface.model;

import static ch.nfr.userinterface.model.property.ElectriScanProperty.*;

import ch.nfr.filehandler.FileHandler;
import ch.nfr.filehandler.JsonHandler;
import ch.nfr.tablemodel.Household;
import ch.nfr.tablemodel.records.HouseholdRecord;
import ch.nfr.userinterface.controller.CostCalculationController;
import ch.nfr.userinterface.controller.DeviceOverviewController;
import ch.nfr.userinterface.controller.SolarPanelOverviewController;
import ch.nfr.userinterface.model.property.ElectriScanProperty;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The ElectriScanModel class represents the main model for the ElectriScan application.
 * It manages the state and behavior related to the households, including loading, editing, and removing households.
 * It also manages the models for the DeviceOverview, SolarPanelOverview, and CostOverview.
 * The class uses a FileHandler for operations related to file handling.
 * It also provides a TextOutput for displaying information to the user.
 * <p>
 * The class uses {@link PropertyChangeSupport} to notify listeners of changes to its state.
 */
public class ElectriScanModel {
    /** Logger */
    private static final Logger logger = Logger.getLogger(ElectriScanModel.class.getName());

    /** PropertyChangeSupport */
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /** The used FileHandler */
    private final FileHandler fileHandler = new JsonHandler();

    /** A TextOutput to save a message in */
    private final TextOutput textOutput = new TextOutput();

    /** The model for the {@link DeviceOverviewController} */
    private DeviceOverviewModel deviceOverviewModel;

    /** The model for the {@link SolarPanelOverviewController} */
    private SolarPanelOverviewModel solarPanelOverviewModel;

    /** The model for the {@link CostCalculationController} */
    private CostCalculationModel costCalculationModel;

    /** A map containing the household name and the corresponding id value. */
    private final Map<Integer, String> householdOverview = new HashMap<>();

    /** The id of the currently loaded household. The id represents a key value in the {@link ElectriScanModel#householdOverview} */
    private int householdId;

    /** The currently loaded household. */
    private Household household;

    /**
     * Constructor to initialize the different models.
     */
    public ElectriScanModel() {
        initializeModels();
        householdId = 0;
        household = null;
    }

    /**
     * First creates the different models and then initializes the listeners.
     * These models are listening to changes in the {@link ElectriScanModel}.
     */
    private void initializeModels() {
        deviceOverviewModel = new DeviceOverviewModel();
        solarPanelOverviewModel = new SolarPanelOverviewModel();
        costCalculationModel = new CostCalculationModel();

        deviceOverviewModel.initializeListenerOn(this);
        solarPanelOverviewModel.initializeListenerOn(this);
        costCalculationModel.initializeListenerOn(this);
    }

    /**
     * Getter for the {@link TextOutput}.
     * @return the {@link TextOutput} instance
     */
    public TextOutput getTextOutput() {
        return textOutput;
    }

    /**
     * Getter for the {@link DeviceOverviewModel}.
     * @return the {@link DeviceOverviewModel} instance
     */
    public DeviceOverviewModel getDeviceOverviewModel() {
        return deviceOverviewModel;
    }

    /**
     * Getter for the {@link SolarPanelOverviewModel}.
     * @return the {@link SolarPanelOverviewModel} instance
     */
    public SolarPanelOverviewModel getSolarPanelOverviewModel() {
        return solarPanelOverviewModel;
    }

    /**
     * Getter for the {@link CostCalculationModel}.
     * @return the {@link CostCalculationModel} instance
     */
    public CostCalculationModel getCostOverviewModel() {
        return costCalculationModel;
    }

    /**
     * Getter for the household overview.
     * @return the household overview
     */
    public Map<Integer, String> getHouseholdOverview() {
        return householdOverview;
    }

    /**
     * Getter for the currently loaded household.
     * @return the currently loaded household
     */
    public Household getHousehold() {
        return household;
    }

    /**
     * Adds a new household through creating a new household with the {@link FileHandler}.
     * Reloads the household overview map afterward and set an information text in the {@link TextOutput}.
     */
    public void addHousehold(){
        fileHandler.createHousehold();
        loadFileDirectory();
        textOutput.set("Neuer Default Haushalt wurde erfolgreich erstellt.");
    }

    /**
     * Loads the new household with the given id.
     * If the given id is equals to the currently loaded household id, an error message is set in the {@link TextOutput}.
     * <p>
     * If not the household is loaded with the {@link FileHandler} and the listeners are informed
     * with the property name {@link ElectriScanProperty#LOAD_HOUSEHOLD} and the old and new {@link Household}.
     *
     * @param householdId the id of the household to load
     */
    public void loadHousehold(int householdId) {
        if (this.householdId == householdId) {
            textOutput.set("Haushalt: " + householdOverview.get(householdId) + " wurde bereits geladen.");
        } else {
            try {
                this.householdId = householdId;
                Household oldHousehold = household;
                this.household = fileHandler.switchHousehold(householdId);

                logger.info("Household set: " + household);
                textOutput.set("Haushalt: " + householdOverview.get(householdId) + " wurde geladen.");
                propertyChangeSupport.firePropertyChange(LOAD_HOUSEHOLD.name(),
                        oldHousehold, household);
            } catch (FileNotFoundException e) {
                logger.severe("Error loading household: " + householdId);
                textOutput.set("Haushalt: " + householdOverview.get(householdId) + " konnte nicht geladen werden.");
            }
        }
    }

    /**
     * Edits the currently loaded household with the given {@link HouseholdRecord}.
     * Informs listeners with the property name {@link ElectriScanProperty#EDIT_HOUSEHOLD} and the old and new household name.
     * Sets an information text in the {@link TextOutput}.
     *
     * @param householdRecord the record containing the new household information
     */
    public void editHousehold(HouseholdRecord householdRecord) {
        String oldHouseholdName = household.getName();
        String newHouseholdName = householdRecord.householdName();
        household.editHousehold(newHouseholdName, householdRecord.postalCode(), householdRecord.numberOfResidents());

        propertyChangeSupport.firePropertyChange(EDIT_HOUSEHOLD.name(),
                oldHouseholdName, newHouseholdName);
        textOutput.set("Haushalt: " + newHouseholdName + " wurde erfolgreich bearbeitet.");
    }

    /**
     * Removes the currently loaded household with the given id.
     * If the given id is equals to the currently loaded household id, the household id is set to 0 and the household is set to null.
     * Informs listeners with the property name {@link ElectriScanProperty#REMOVE_HOUSEHOLD} and the old and new value set to null.
     * <p>
     * If otherwise, the listeners are informed with the property name {@link ElectriScanProperty#REMOVE_HOUSEHOLD},
     * old value = old household id and the new value = 0.
     * Deletes the household with the {@link FileHandler} and sets an information text in the {@link TextOutput}.
     *
     * @param householdId the id of the household to remove
     */
    public void removeHousehold(int householdId) {
        if (this.householdId == householdId) {
            this.householdId = 0;
            household = null;
            propertyChangeSupport.firePropertyChange(REMOVE_HOUSEHOLD.name(),
                    null, null);
        } else {
            propertyChangeSupport.firePropertyChange(REMOVE_HOUSEHOLD.name(),
                    householdId, 0);
        }
        fileHandler.deleteHousehold(householdId);
        loadFileDirectory();
        textOutput.set("Haushalt: " + householdOverview.get(householdId) +
                new String(" wurde erfolgreich gelöscht.".getBytes(), StandardCharsets.UTF_8));

    }



    /**
     * Loads the household overview map with the {@link FileHandler}.
     * Manually added files will be included in the overview.
     * <p>
     * Informs listeners with the property name {@link ElectriScanProperty#LOAD_DIRECTORY}
     * and the old and new version of the {@link #householdOverview}.
     */
    public void loadFileDirectory() {
        Map<Integer, String> oldHouseholdOverview = new HashMap<>(householdOverview);
        householdOverview.clear();
        householdOverview.putAll(fileHandler.getFilesInFolder());
        propertyChangeSupport.firePropertyChange(LOAD_DIRECTORY.name(),
                oldHouseholdOverview, householdOverview);
        textOutput.set("Haushaltsverzeichnis wurde geladen.");
        logger.info("Household directory loaded: " + householdOverview);
    }

    /**
     * Adds a new property change listener to the {@link PropertyChangeSupport}.
     *
     * @param listener the listener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Tear down the {@link FileHandler} terminates the auto-save feature.
     */
    public void tearDown() {
        fileHandler.tearDown();
    }

    /**
     * Exports the currently loaded household to a file with the given path.
     *
     * @param absolutePath the path to export the household to
     */
    public void exportHousehold(String absolutePath) {
        if (household != null){
            fileHandler.exportHousehold(absolutePath);
            textOutput.set("Haushalt: " + household.getName() + " wurde erfolgreich exportiert. Pfad: " + absolutePath);
        } else {
            textOutput.set("Kein Haushalt zum exportieren vorhanden. Sie müssen zuerst einen Haushalt laden.");
        }
    }

    /**
     * Imports a household from a file with the given path.
     *
     * @param absolutePath the path to import the household from
     */
    public void importHousehold(String absolutePath) {
        try {
            fileHandler.importHousehold(absolutePath);
            loadFileDirectory();

            textOutput.set("Haushalt wurde erfolgreich importiert. Pfad: " + absolutePath);
        } catch (Exception e) {
            textOutput.set("Haushalt konnte nicht importiert werden. Pfad: " + absolutePath);
        }
    }

    /**
     * Inner class representing the text output.
     */
    public static class TextOutput {
        /** PropertyChangeSupport */
        private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
        /** The text to display. */
        private String text;

        /**
         * Getter for the text.
         *
         * @return the text
         */
        public String get() {
            return text;
        }

        /**
         * Setter for the text.
         *
         * @param newText the new text to set
         */
        public void set(String newText) {
            String oldText = this.text;
            this.text = newText;
            propertyChangeSupport.firePropertyChange("text", oldText, newText);
        }

        /**
         * Adds a new property change listener to the {@link PropertyChangeSupport}.
         *
         * @param listener the listener to add
         */
        public void addPropertyChangeListener(PropertyChangeListener listener) {
            propertyChangeSupport.addPropertyChangeListener(listener);
        }
    }
}
