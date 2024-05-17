package ch.zhaw.it23a.pm2.tablemodel.records;

import ch.zhaw.it23a.pm2.tablemodel.device.Consumption;
import ch.zhaw.it23a.pm2.tablemodel.device.DeviceCategory;

/**
 * Represents the device record.
 * @param isWired true if the device is wired, false otherwise
 * @param deviceName the device name
 * @param deviceCategory the device category
 * @param consumption the consumption of the device
 */
public record DeviceRecord(boolean isWired, String deviceName, DeviceCategory deviceCategory, Consumption consumption) {
}
