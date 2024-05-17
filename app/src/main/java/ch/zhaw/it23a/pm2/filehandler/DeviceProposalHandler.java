package ch.zhaw.it23a.pm2.filehandler;

import ch.zhaw.it23a.pm2.calculatorAndConverter.UnitConverter;
import ch.zhaw.it23a.pm2.calculatorAndConverter.units.EnergyUnit;
import ch.zhaw.it23a.pm2.calculatorAndConverter.units.TimeUnit;
import ch.zhaw.it23a.pm2.tablemodel.device.BatteryConsumption;
import ch.zhaw.it23a.pm2.tablemodel.device.DeviceCategory;
import ch.zhaw.it23a.pm2.tablemodel.device.ElectricConsumption;
import ch.zhaw.it23a.pm2.tablemodel.RoomType;
import ch.zhaw.it23a.pm2.tablemodel.records.DeviceRecord;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import static ch.zhaw.it23a.pm2.filehandler.property.DeviceProperty.*;

/**
 * The DeviceProposalHandler class is responsible for reading the device proposal file and creating a list of DeviceRecords.
 */
public class DeviceProposalHandler {
    /** The used Logger in this class. */
    private static final Logger logger = Logger.getLogger(DeviceProposalHandler.class.getName());
    /** The path to the properties file. */
    private final String propertiesPath;

    /**
     * Creates a new DeviceProposalHandler with the given properties file.
     *
     * @param propertiesFile the path to the properties file
     */
    private DeviceProposalHandler(String propertiesFile) {
        this.propertiesPath = Objects.requireNonNull(propertiesFile);
    }

    /**
     * Reads the device proposal file and returns a list of {@link DeviceRecord}s for the given {@link RoomType}.
     * @param propertiesFile the path to the properties file
     * @param roomType the room type to read from
     * @return a list of {@link DeviceRecord}s
     */
    public static List<DeviceRecord> readDeviceProposalFile(String propertiesFile, RoomType roomType) {
        List<DeviceRecord> deviceRecords = new ArrayList<>();
        DeviceProposalHandler deviceProposalHandler = new DeviceProposalHandler(propertiesFile);
        Set<String> processedDevices = new HashSet<>();

        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(deviceProposalHandler.propertiesPath)) {
            properties.load(fileInputStream);

            properties.stringPropertyNames()
                    .stream()
                    .filter(key -> {
                        String[] keyParts = key.split("\\.");
                        return RoomType.parseRoomType(keyParts[0]) == roomType;
                    })
                    .forEach(key -> {
                        String[] keyParts = key.split("\\.");
                        String subKey = keyParts[0] + "." + keyParts[1] + ".";
                        if (processedDevices.add(subKey)) {
                            logger.info("Found new device: " + keyParts[1] + " in " + roomType.name());
                            deviceRecords.add(deviceProposalHandler.createDeviceRecord(properties, subKey));
                        }
                    });
        } catch (IOException e) {
            logger.severe("Could not read the properties file: " + e.getMessage());
        }
        return deviceRecords;
    }

    /**
     * Creates a new DeviceRecord from the given properties and subKey.
     * @param properties the properties to read from
     * @param subKey the subKey to read from
     * @return a new DeviceRecord
     */
    private DeviceRecord createDeviceRecord(Properties properties, String subKey) {
        boolean isWired = Boolean.parseBoolean(properties.getProperty(subKey + IS_WIRED.name()));
        String name = properties.getProperty(subKey + DEVICE_NAME.name());
        DeviceCategory deviceCategory = DeviceCategory.parseDeviceCategory(properties.getProperty(subKey + DEVICE_CATEGORY.name()));

        if (isWired) {
            double powerConsumption = Double.parseDouble(properties.getProperty(subKey + POWER_CONSUMPTION.name()));
            double usage = Double.parseDouble(properties.getProperty(subKey + USAGE.name()));

            EnergyUnit powerConsumptionUnit = EnergyUnit.parseEnergyUnit(properties.getProperty(subKey + POWER_CONSUMPTION_UNIT.name()));
            TimeUnit usageUnit = TimeUnit.parseTimeUnit(properties.getProperty(subKey + USAGE_UNIT.name()));
            TimeUnit usagePerUnit = TimeUnit.parseTimeUnit(properties.getProperty(subKey + USAGE_PER_UNIT.name()));

            assert usagePerUnit != null;
            double yearlyUsage = usage * (double) (TimeUnit.YEAR.getFactor() / usagePerUnit.getFactor());

            long powerConsumptionInWattSeconds = UnitConverter.convertEnergyToWattSeconds(powerConsumption, powerConsumptionUnit);
            long yearlyUsageInSeconds = UnitConverter.convertTimeToSeconds(yearlyUsage, usageUnit);

            return new DeviceRecord(true, name, deviceCategory,
                    new ElectricConsumption(powerConsumptionInWattSeconds, yearlyUsageInSeconds, usageUnit, usagePerUnit, powerConsumptionUnit));
        } else {
            double batteryCapacity = Double.parseDouble(properties.getProperty(subKey + BATTERY_CAPACITY.name()));
            double chargingCycle = Double.parseDouble(properties.getProperty(subKey + CHARGING_CYCLE.name()));

            EnergyUnit batteryCapacityUnit = EnergyUnit.parseEnergyUnit(properties.getProperty(subKey + BATTERY_CAPACITY_UNIT.name()));
            TimeUnit chargingCycleUnit = TimeUnit.parseTimeUnit(properties.getProperty(subKey + CHARGING_CYCLE_UNIT.name()));

            long batteryCapacityInWattSeconds = UnitConverter.convertEnergyToWattSeconds(batteryCapacity, batteryCapacityUnit);
            assert chargingCycleUnit != null;
            long yearlyChargingCycles = (long) (chargingCycle * (TimeUnit.YEAR.getFactor() / chargingCycleUnit.getFactor()));

            return new DeviceRecord(false, name, deviceCategory,
                    new BatteryConsumption(yearlyChargingCycles, batteryCapacityInWattSeconds, chargingCycleUnit, batteryCapacityUnit));
        }
    }
}
