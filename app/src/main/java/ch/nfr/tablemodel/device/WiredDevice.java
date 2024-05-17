package ch.nfr.tablemodel.device;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import static ch.nfr.tablemodel.ChangeProperty.EDIT_DEVICE;

/**
 * Represents a wired device.
 */
public class WiredDevice extends Device {

    /**
     * The property change support.
     */
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Creates a new wired device.
     *
     * @param id the device id
     * @param ownerID the owner id
     * @param deviceName the device name
     */
    public WiredDevice(int id, int ownerID, String deviceName, DeviceCategory deviceCategory, Consumption consumption) {
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
     * Checks if the consumption is an instance of ElectricConsumption.
     *
     * @param consumption the consumption
     * @return the consumption
     */
    @Override
    protected Consumption requireRightConsumption(Consumption consumption) {
        if (!(consumption instanceof ElectricConsumption)) {
            throw new IllegalArgumentException("Invalid consumption type");
        }
        return consumption;
    }

    /**
     * Returns the electric power in milliwatt.
     *
     * @return the electric power in milliwatt
     */
    public ElectricConsumption getElectricConsumption() {
        return (ElectricConsumption) getConsumption();
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
