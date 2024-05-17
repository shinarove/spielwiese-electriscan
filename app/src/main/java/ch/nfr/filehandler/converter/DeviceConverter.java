package ch.nfr.filehandler.converter;


import ch.nfr.calculator.units.EnergyUnit;
import ch.nfr.calculator.units.TimeUnit;
import ch.nfr.filehandler.property.DeviceProperty;
import ch.nfr.tablemodel.device.*;
import org.json.JSONObject;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * This class converts a device object to a json object and vice versa.
 */
public class DeviceConverter implements JsonConverter {

    /**
     * The ownerID of the device.
     */
    private int ownerID;
    /**
     * The json object of the device.
     */
    private JSONObject jsonObject;

    /**
     * The property change support of the device.
     */
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);


    /**
     * Reads the device properties from the json object and creates a device object.
     * @param jsonObject The json object to read the device properties from.
     * @return The device object created from the json object.
     */
    @Override
    public Device readJson(JSONObject jsonObject) {
        if (!hasDeviceProperties(jsonObject))
            throw new RuntimeException("Device properties are missing in the json file");
        if (!areDevicePropertiesValidTypes(jsonObject))
            throw new RuntimeException("Device properties have invalid types in the json file");
        return createDevice(jsonObject);
    }

    /**
     * Converts a device object to a json object.
     * @return The json object created from the device object.
     */
    @Override
    public JSONObject toJson() {
        return this.jsonObject;
    }

    /**
     * Writes the device properties to the json object.
     * @param propertyName The name of the property to write.
     * @param oldValue The old value of the property.
     * @param newValue The new value of the property.
     */
    @Override
    public void writeJson(String propertyName, Object oldValue, Object newValue) {
        switch (propertyName) {
            case "EDIT_DEVICE": {
                for (DeviceProperty property : DeviceProperty.values()) {
                    jsonObject.put(property.name(), property.getValue((Device) newValue));
                }
                break;
            }
        }
        propertyChangeSupport.firePropertyChange("DEVICE_CHANGES",null, jsonObject);
    }

    /**
     * Checks if the json object has all device properties.
     * @param jsonObject The json object to check.
     * @return True if the json object has all device properties, false otherwise.
     */
    private boolean hasDeviceProperties(JSONObject jsonObject) {
        for (DeviceProperty property : DeviceProperty.values()) {
            if (!jsonObject.has(property.name())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the device properties in the json object have valid types.
     * @param jsonObject The json object to check.
     * @return True if the device properties in the json object have valid types, false otherwise.
     */
    private boolean areDevicePropertiesValidTypes(JSONObject jsonObject) {
        for (DeviceProperty property : DeviceProperty.values()) {
            Object value = jsonObject.get(property.name());
            if (!property.isValidType(value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a device object from the json object.
     * @param jsonObject The json object to create the device object from.
     * @return The device object created from the json object.
     */
    private Device createDevice(JSONObject jsonObject) {
        return jsonObject.getBoolean(DeviceProperty.IS_WIRED.name()) ? createWiredDevice(jsonObject) : createMobileDevice(jsonObject);
    }

    /**
     * Creates a wired device object from the json object.
     * @param jsonObject The json object to create the wired device object from.
     * @return The wired device object created from the json object.
     */
    private WiredDevice createWiredDevice(JSONObject jsonObject) {
        int id = jsonObject.getInt(DeviceProperty.DEVICE_ID.name());
        String deviceName = jsonObject.getString(DeviceProperty.DEVICE_NAME.name());
        long powerConsumption = jsonObject.getLong(DeviceProperty.POWER_CONSUMPTION.name());
        long usage = jsonObject.getLong(DeviceProperty.USAGE.name());
        TimeUnit usageUnit = DeviceProperty.getTimeUnit(jsonObject.getString(DeviceProperty.USAGE_UNIT.name()));
        TimeUnit usagePerUnit = DeviceProperty.getTimeUnit(jsonObject.getString(DeviceProperty.USAGE_PER_UNIT.name()));
        EnergyUnit powerConsumptionUnit = DeviceProperty.getEnergyUnit(jsonObject.getString(DeviceProperty.POWER_CONSUMPTION_UNIT.name()));
        DeviceCategory deviceCategory = DeviceProperty.getDeviceCategory(jsonObject.getString(DeviceProperty.DEVICE_CATEGORY.name()));

        ElectricConsumption electricConsumption = new ElectricConsumption(powerConsumption, usage, usageUnit, usagePerUnit, powerConsumptionUnit);
        WiredDevice wiredDevice = new  WiredDevice(id, ownerID, deviceName, deviceCategory, electricConsumption);
        wiredDevice.addPropertyChangeListener(evt -> this.writeJson(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue()));
        this.jsonObject = jsonObject;
        return wiredDevice;
    }

    /**
     * Creates a mobile device object from the json object.
     * @param jsonObject The json object to create the mobile device object from.
     * @return The mobile device object created from the json object.
     */
    private MobileDevice createMobileDevice(JSONObject jsonObject) {
        int id = jsonObject.getInt(DeviceProperty.DEVICE_ID.name());
        String deviceName = jsonObject.getString(DeviceProperty.DEVICE_NAME.name());
        long batteryCapacity = jsonObject.getLong(DeviceProperty.BATTERY_CAPACITY.name());
        EnergyUnit batteryCapacityUnit = DeviceProperty.getEnergyUnit(jsonObject.getString(DeviceProperty.BATTERY_CAPACITY_UNIT.name()));
        long chargingCycle = jsonObject.getLong(DeviceProperty.CHARGING_CYCLE.name());
        TimeUnit chargingTimeUnit = DeviceProperty.getTimeUnit(jsonObject.getString(DeviceProperty.CHARGING_CYCLE_UNIT.name()));
        DeviceCategory deviceCategory = DeviceProperty.getDeviceCategory(jsonObject.getString(DeviceProperty.DEVICE_CATEGORY.name()));

        BatteryConsumption batteryConsumption = new BatteryConsumption(chargingCycle, batteryCapacity, chargingTimeUnit, batteryCapacityUnit);
        MobileDevice mobileDevice = new MobileDevice(id, ownerID, deviceName, deviceCategory, batteryConsumption);
        mobileDevice.addPropertyChangeListener(evt -> this.writeJson(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue()));
        this.jsonObject = jsonObject;
        return mobileDevice;
    }

    /**
     * Sets the ownerID of the device.
     * @param ownerID The ownerID of the device.
     */
    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }

    /**
     * Converts a device object to a json object.
     * @param device The device object to convert.
     * @param ownerID The ownerID of the device.
     * @return The json object created from the device object.
     */
    public JSONObject toJson(Device device, int ownerID) {
        jsonObject = new JSONObject();
        for (DeviceProperty property : DeviceProperty.values()){
            jsonObject.put(property.name(), property.getValue(device));
        }
        device.addPropertyChangeListener(evt -> this.writeJson(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue()));
        this.ownerID = ownerID;
        return jsonObject;
    }

    /**
     * Adds a property change listener to the device.
     * @param listener The property change listener to add.
     * @return The device converter with the added property change listener.
     */
    public DeviceConverter addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
        return this;
    }
}
