package ch.nfr.filehandler.converter;

import ch.nfr.filehandler.property.SolarPanelProperty;
import ch.nfr.tablemodel.Orientation;
import ch.nfr.tablemodel.SolarPanel;
import org.json.JSONObject;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * This class converts a solar panel object to a json object and vice versa.
 */
public class SolarPanelConverter implements JsonConverter {
    /**
     * The json object of the solar panel.
     */
    private JSONObject jsonObject;
    /**
     * The property change support of the solar panel.
     */
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Reads the solar panel properties from the json object and creates a solar panel object.
     * @param jsonObject The json object to read the table model properties from.
     * @return The solar panel object created from the json object.
     */
    @Override
    public SolarPanel readJson(JSONObject jsonObject) {
        if (!hasSolarPanelProperties(jsonObject))
            throw new RuntimeException("Solar panel properties are missing in the json file");
        if (!areSolarPanelPropertiesValidTypes(jsonObject))
            throw new RuntimeException("Solar panel properties have invalid types in the json file");
        return createSolarPanel(jsonObject);
    }

    /**
     * Converts a solar panel object to a json object.
     * @return The json object created from the solar panel object.
     */
    @Override
    public JSONObject toJson() {
        return this.jsonObject;
    }

    /**
     * Writes the solar panel properties to the json object.
     * @param propertyName The name of the property to write.
     * @param oldValue The old value of the property.
     * @param newValue The new value of the property.
     */
    @Override
    public void writeJson(String propertyName, Object oldValue, Object newValue) {
        switch (propertyName) {
            case "EDIT_SOLAR_PANEL": {
                for (SolarPanelProperty property : SolarPanelProperty.values()) {
                    jsonObject.put(property.name(), property.getValue((SolarPanel) newValue));
                }
                break;
            }
        }
        propertyChangeSupport.firePropertyChange("SOLAR_PANEL_CHANGES",null, jsonObject);
    }

    /**
     * Checks if the json object has all solar panel properties.
     * @param jsonObject The json object to check.
     * @return True if the json object has all solar panel properties, false otherwise.
     */
    private boolean hasSolarPanelProperties(JSONObject jsonObject) {
        for (SolarPanelProperty property : SolarPanelProperty.values()) {
            if (!jsonObject.has(property.name())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the solar panel properties have valid types.
     * @param jsonObject The json object to check.
     * @return True if the solar panel properties have valid types, false otherwise.
     */
    private boolean areSolarPanelPropertiesValidTypes(JSONObject jsonObject) {
        for (SolarPanelProperty property : SolarPanelProperty.values()) {
            Object value = jsonObject.get(property.name());
            if (!property.isValidType(value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a solar panel object from the json object.
     * @param jsonObject The json object to create the solar panel object from.
     * @return The solar panel object created from the json object.
     */
    private SolarPanel createSolarPanel(JSONObject jsonObject) {
        int solarPanelId = jsonObject.getInt(SolarPanelProperty.SOLAR_PANEL_ID.name());
        String solarPanelName = jsonObject.getString(SolarPanelProperty.SOLAR_PANEL_NAME.name());
        double solarPanelArea = jsonObject.getDouble(SolarPanelProperty.SOLAR_PANEL_AREA.name());
        Orientation orientation = SolarPanelProperty.getOrientation(jsonObject.getString(SolarPanelProperty.ORIENTATION.name()));
        this.jsonObject = jsonObject;
        SolarPanel solarPanel = new SolarPanel(solarPanelId, solarPanelName, solarPanelArea, orientation);
        solarPanel.addPropertyChangeListener(evt -> writeJson(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue()));
        return solarPanel;
    }

    /**
     * Converts a solar panel object to a json object.
     * @param solarPanel The solar panel object to convert.
     * @return The json object created from the solar panel object.
     */
    public JSONObject toJson(SolarPanel solarPanel) {
        jsonObject = new JSONObject();
        for (SolarPanelProperty property : SolarPanelProperty.values()) {
            jsonObject.put(property.name(), property.getValue(solarPanel));
        }
        solarPanel.addPropertyChangeListener(evt -> writeJson(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue()));
        return jsonObject;
    }

    /**
     * Adds a property change listener to the solar panel.
     * @param listener The property change listener to add.
     * @return The solar panel converter with the added property change listener.
     */
    public SolarPanelConverter addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
        return this;
    }
}
