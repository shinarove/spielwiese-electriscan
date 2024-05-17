package ch.zhaw.it23a.pm2.userinterface.model;

import ch.zhaw.it23a.pm2.UnknownPropertyException;
import ch.zhaw.it23a.pm2.tablemodel.Household;
import ch.zhaw.it23a.pm2.userinterface.model.property.ElectriScanProperty;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import static ch.zhaw.it23a.pm2.userinterface.model.property.ElectriScanProperty.*;

/**
 * Is the abstract class for the secondary views on the ElectriScan Application.
 */
public abstract class SecondaryModel {
    /** A property change support to inform listeners about changes in the {@link ElectriScanModel}. */
    private final PropertyChangeSupport propertyChangeElectriScan = new PropertyChangeSupport(this);
    /** The text output from the {@link ElectriScanModel} */
    private ElectriScanModel.TextOutput textOutput;
    /** The loaded household to use. */
    private Household household;

    /**
     * Initializes the listener on the ElectriScanModel.
     * @param electriScanModel the ElectriScanModel to listen to.
     */
    public void initializeListenerOn(ElectriScanModel electriScanModel) {
        this.textOutput = electriScanModel.getTextOutput();
        electriScanModel.addPropertyChangeListener(evt -> {
            switch (ElectriScanProperty.parseProperty(evt.getPropertyName())) {
                case LOAD_HOUSEHOLD -> {
                    this.household = (Household) evt.getNewValue();
                    propertyChangeElectriScan.firePropertyChange(LOAD_HOUSEHOLD.name(),
                            evt.getOldValue(), evt.getNewValue());
                }
                case EDIT_HOUSEHOLD -> {
                    propertyChangeElectriScan.firePropertyChange(EDIT_HOUSEHOLD.name(),
                            evt.getOldValue(), evt.getNewValue());
                }
                case REMOVE_HOUSEHOLD -> {
                    if (evt.getOldValue() == null) {
                        this.household = null;
                        propertyChangeElectriScan.firePropertyChange(REMOVE_HOUSEHOLD.name(),
                                evt.getOldValue(), evt.getNewValue());
                    }
                }
                case LOAD_DIRECTORY -> {}
                case null -> throw new UnknownPropertyException("Unknown property: " + evt.getPropertyName());
            }
        });
    }

    /**
     * Returns the household.
     * @return the household.
     */
    public Household getHousehold() {
        return household;
    }

    /**
     * Sets the message in the text output.
     * @param message the message to set.
     */
    protected void setMessageInTextOutput(String message) {
        textOutput.set(message);
    }

    /**
     * Converts a message to UTF-8.
     *
     * @param message to convert.
     * @return the message in UTF-8.
     */
    protected String utf8(String message) {
        return new String(message.getBytes(), java.nio.charset.StandardCharsets.UTF_8);
    }

    /**
     * Adds a PropertyChangeListener to the ElectriScanModel.
     * @param listener to be added.
     */
    public void addPropertyChangeListenerElectriScan(PropertyChangeListener listener) {
        propertyChangeElectriScan.addPropertyChangeListener(listener);
    }
}
