package ch.nfr.tablemodel;

import ch.nfr.tablemodel.records.SolarRecord;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import static ch.nfr.tablemodel.ChangeProperty.EDIT_SOLAR_PANEL;

/**
 * Represents a solar panel.
 */
public class SolarPanel implements TableModel {
    /**
     * The id of the solar panel.
     */
    private final int id;
    /**
     * The name of the solar panel.
     */
    private String solarPanelName;
    /**
     * The area of the solar panel.
     */
    private double area;
    /**
     * The orientation of the solar panel.
     */
    private Orientation orientation;
    /**
     * The property change support.
     */
    private final PropertyChangeSupport propertyChangeSupport= new PropertyChangeSupport(this);

    /**
     * Constructor for the solar panel.
     * @param id The id of the solar panel.
     * @param solarPanelName The name of the solar panel.
     * @param solarPanelArea The area of the solar panel.
     * @param orientation The orientation of the solar panel.
     */
    public SolarPanel(int id, String solarPanelName, double solarPanelArea, Orientation orientation) {
        this.id = id;
        this.solarPanelName = solarPanelName;
        this.area = solarPanelArea;
        this.orientation = orientation;
    }

    /**
     * Get the id of the solar panel.
     *
     * @return The id of the solar panel.
     */
    public int getId() {
        return id;
    }

    /**
     * Get the name of the solar panel.
     *
     * @return The name of the solar panel.
     */
    public String getName() {
        return solarPanelName;
    }

    /**
     * Get the area of the solar panel.
     *
     * @return The area of the solar panel.
     */
    public double getArea() {
        return area;
    }

    /**
     * Get the orientation of the solar panel.
     *
     * @return The orientation of the solar panel.  
    */
    public Orientation getOrientation() {
        return orientation;
    }

    /**
     * Edits the solar panel.
     *
     * @param solarPanelName the name of the solar panel
     * @param area the area of the solar panel
     * @param orientation the orientation of the solar panel
     */
    public void editSolarPanel(String solarPanelName, double area, Orientation orientation){
        this.orientation = orientation;
        this.area = area;
        this.solarPanelName = solarPanelName;

        propertyChangeSupport.firePropertyChange(EDIT_SOLAR_PANEL.name(), null, this);
    }

    /**
     * Adds a property change listener.
     *
     * @param listener the listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Converts the solar panel to a record.
     * @return the record
     */
    public SolarRecord toRecord() {
        return new SolarRecord(solarPanelName, area, orientation);
    }

    /**
     * Convert the solar panel to a string. The value of the string is equal to the solar panel name.
     * @return The string representation of the solar panel.
     */
    @Override
    public String toString() {
        return solarPanelName;
    }
}
