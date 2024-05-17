package ch.nfr.userinterface.model;

import ch.nfr.tablemodel.SolarPanel;
import ch.nfr.tablemodel.records.SolarRecord;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.logging.Logger;

import static ch.nfr.userinterface.model.property.SolarPanelOverviewProperty.*;

/**
 * <p>This class is the model for the SolarPanel Overview in the GUI.
 * It handles the user interactions for adding, editing and removing a solar panel.</p>
 */
public class SolarPanelOverviewModel extends SecondaryModel {

    /**
     * Logger for logging messages.
     */
    private static final Logger logger = Logger.getLogger(SolarPanelOverviewModel.class.getName());

    /**
     * The SolarPanelCalculator to calculate the solar panel values.
     */
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);


    /**
     * Adds a new SolarPanel to the household.
     *
     * @param solarRecord to be added as a new SolarPanel.
     */
    public void addSolarPanel(SolarRecord solarRecord) {
        logger.info("add solar panel to household.");
        int solarPanelId = getHousehold().getFreeSolarPanelId();
        SolarPanel newSolarPanel = new SolarPanel(solarPanelId, solarRecord.solarPanelName(),
                solarRecord.area(), solarRecord.orientation());
        getHousehold().addSolarPanel(newSolarPanel);
        propertyChangeSupport.firePropertyChange(ADD_SOLAR_PANEL.name(), null, newSolarPanel);
        setMessageInTextOutput("Solaranlage: " + newSolarPanel.getName() + utf8(" wurde hinzugefügt."));
    }

    /**
     * Edits a SolarPanel in the household.
     *
     * @param solarPanelId of the SolarPanel to be edited.
     * @param solarRecord  with the new values for the SolarPanel.
     */
    public void editSolarPanel(int solarPanelId, SolarRecord solarRecord) {
        SolarPanel newSolarPanel = getHousehold().getSolarPanel(solarPanelId);
        newSolarPanel.editSolarPanel(solarRecord.solarPanelName(), solarRecord.area(), solarRecord.orientation());
        propertyChangeSupport.firePropertyChange(EDIT_SOLAR_PANEL.name(), null, newSolarPanel);
        setMessageInTextOutput("Solaranlage: " + newSolarPanel.getName() + utf8(" wurde verändert."));
    }

    /**
     * Removes a SolarPanel from the household.
     *
     * @param solarPanelId of the SolarPanel to be deleted.
     */
    public void removeSolarPanel(int solarPanelId) {
        SolarPanel newSolarPanel = getHousehold().getSolarPanel(solarPanelId);

        propertyChangeSupport.firePropertyChange(REMOVE_SOLAR_PANEL.name(), newSolarPanel, null);
        setMessageInTextOutput("Solaranlage: " + newSolarPanel.getName() + utf8(" wurde gelöscht."));
    }

    /**
     * Adds a PropertyChangeListener to the SolarPanelOverviewModel.
     *
     * @param listener to be added.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
}
