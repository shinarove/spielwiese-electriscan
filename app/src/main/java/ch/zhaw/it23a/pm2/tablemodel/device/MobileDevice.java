package ch.zhaw.it23a.pm2.tablemodel.device;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import static ch.zhaw.it23a.pm2.tablemodel.ChangeProperty.EDIT_DEVICE;


/**
 * Represents a mobile device.
 */
public class MobileDevice extends Device {
    /**
     * The property change support.
     */
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Creates a new mobile device.
     *
     * @param id the device id
     * @param ownerID the owner id
     * @param deviceName the device name
     */
    public MobileDevice(int id, int ownerID, String deviceName, DeviceCategory deviceCategory, Consumption consumption) {
        super(id, ownerID, deviceName, deviceCategory, consumption);
    }

    /**
     * Edits the device.
     * @param deviceName the device name
     * @param deviceCategory the device category
     * @param consumption the consumption
     */
    @Override
    public void editDevice(String deviceName, DeviceCategory deviceCategory, Consumption consumption) {
        super.setName(deviceName);
        super.setCategory(deviceCategory);
        super.setConsumption(consumption);
        propertyChangeSupport.firePropertyChange(EDIT_DEVICE.name(), null, this);
    }

    /**
     * Check if the consumption is a instance of BatteryConsumption.
     * @param consumption the consumption
     * @return the consumption
     */
    @Override
    protected Consumption requireRightConsumption(Consumption consumption) {
        if (!(consumption instanceof BatteryConsumption)) {
            throw new IllegalArgumentException("Invalid consumption type");
        }
        return consumption;
    }

    /**
     * Returns the battery capacity in milliwatt seconds.
     *
     * @return the battery capacity in milliwatt seconds
     */
    public BatteryConsumption getBatteryConsumption() {
        return (BatteryConsumption) getConsumption();
    }

    /**
     * Adds a property change listener.
     *
     * @param listener the property change listener
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
}
