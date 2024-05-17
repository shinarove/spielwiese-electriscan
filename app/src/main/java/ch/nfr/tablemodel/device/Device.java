package ch.nfr.tablemodel.device;

import ch.nfr.tablemodel.TableModel;
import ch.nfr.tablemodel.records.DeviceRecord;

import java.beans.PropertyChangeListener;
import java.util.Objects;


/**
 * Represents a device.
 */
public abstract class Device implements TableModel {

    /** The owner id. */
    private final int ownerId;
    /** The device id. */
    private final int id;
    /** The device name. */
    private String deviceName;
    /** The device category. */
    private DeviceCategory deviceCategory;
    /** The consumption. */
    private Consumption consumption;

    /**
     * Creates a new device.
     *
     * @param id the device id
     * @param ownerId the owner id
     * @param deviceName the device name
     * @param deviceCategory the device category
     * @param consumption the consumption
     *
     */
    public Device(int id, int ownerId, String deviceName, DeviceCategory deviceCategory, Consumption consumption) {
        this.id = id;
        this.ownerId = ownerId;
        this.deviceName = Objects.requireNonNull(deviceName);
        this.deviceCategory = Objects.requireNonNull(deviceCategory);
        this.consumption = requireRightConsumption(Objects.requireNonNull(consumption));
    }

    /**
     * Returns the device id.
     *
     * @return the device id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the owner id.
     *
     * @return the owner id
     */
    public int getOwnerId() {
        return ownerId;
    }


    /**
     * Returns the device name.
     *
     * @return the device name
     */
    public String getName() {
        return deviceName;
    }

    /**
     * Returns the device category.
     * @return the device category
     */
    public DeviceCategory getCategory() {
        return deviceCategory;
    }

    /**
     * Returns the yearly consumption in watt seconds.
     *
     * @return the yearly consumption in watt seconds
     */
    public long getYearlyConsumptionInWattSeconds() {
        return consumption.getYearlyConsumptionInWattSeconds();
    }

    /**
     * Returns the consumption.
     *
     * @return the consumption.
     */
    protected Consumption getConsumption() {
        return consumption;
    }

    /**
     * Sets the name of the device.
     *
     * @param name the name of the device
     */
    protected void setName(String name) {
        this.deviceName = name;
    }

    /**
     * Sets the category of the device.
     *
     * @param deviceCategory the category of the device
     */
    protected void setCategory(DeviceCategory deviceCategory) {
        this.deviceCategory = deviceCategory;
    }

    /**
     * Sets the consumption of the device.
     *
     * @param consumption the consumption of the device
     */
    protected void setConsumption (Consumption consumption) {
        this.consumption = requireRightConsumption(consumption);
    }

    /**
     * Check if the consumption is correct.
     * @param consumption the consumption
     * @return the consumption
     */
    protected abstract Consumption requireRightConsumption(Consumption consumption);

    /**
     * Edits the device.
     * @param deviceName the device name
     * @param deviceCategory the device category
     * @param consumption the consumption
     */
    public abstract void editDevice(String deviceName, DeviceCategory deviceCategory, Consumption consumption);

    /**
     * Adds a property change listener.
     * @param listener the property change listener
     */
    public abstract void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Converts the device to a device record.
     * @return the device record representing the device
     */
    public DeviceRecord toDeviceRecord() {
        boolean isWired = this instanceof WiredDevice;
        return new DeviceRecord(isWired, deviceName, deviceCategory, consumption);
    }

    /**
     * Returns the device name.
     * @return the device name
     */
    @Override
    public String toString() {
        return deviceName;
    }
}
