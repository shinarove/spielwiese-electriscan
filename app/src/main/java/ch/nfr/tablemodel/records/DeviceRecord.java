package ch.nfr.tablemodel.records;

import ch.nfr.tablemodel.device.Consumption;
import ch.nfr.tablemodel.device.DeviceCategory;

/**
 * Represents the device record.
 * @param isWired true if the device is wired, false otherwise
 * @param deviceName the device name
 * @param deviceCategory the device category
 * @param consumption the consumption of the device
 */
public record DeviceRecord(boolean isWired, String deviceName, DeviceCategory deviceCategory, Consumption consumption) {
}
