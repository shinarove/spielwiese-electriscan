package ch.nfr.filehandler.property;

import ch.nfr.calculator.units.EnergyUnit;
import ch.nfr.calculator.units.TimeUnit;
import ch.nfr.tablemodel.device.BatteryConsumption;
import ch.nfr.tablemodel.device.Device;
import ch.nfr.tablemodel.device.DeviceCategory;
import ch.nfr.tablemodel.device.ElectricConsumption;
import ch.nfr.tablemodel.device.MobileDevice;
import ch.nfr.tablemodel.device.WiredDevice;

/**
 * This enum represents the device properties.
 * It contains methods to bind the property types and values from the {@link Device} class.
 */
public enum DeviceProperty {
    DEVICE_ID {
        /**
         * This method checks if the value is a valid type.
         * @param value the value
         * @return true if the value is an Integer, otherwise false
         */
        @Override
        public boolean isValidType(Object value) {
            return value instanceof Integer;
        }

        /**
         * This method returns the value of the property.
         * @see Device#getId()
         * @param device the device
         * @return the value of the property
         */
        @Override
        public Object getValue(Device device) {
            return device.getId();
        }
    },

    IS_WIRED {
        /**
         * This method checks if the value is a valid type.
         * @param value the value
         * @return true if the value is a Boolean, otherwise false
         */
        @Override
        public boolean isValidType(Object value) {
            return value instanceof Boolean;
        }

        /**
         * This method returns the value of the property.
         * @param device the device
         * @return true if the device is a wired device, otherwise false
         */
        @Override
        public Object getValue(Device device) {
            return device instanceof WiredDevice;
        }
    },
    DEVICE_NAME {
        /**
         * This method checks if the value is a valid type.
         * @param value the value
         * @return true if the value is a String, otherwise false
         */
        @Override
        public boolean isValidType(Object value) {
            return value instanceof String;
        }

        /**
         * This method returns the value of the property.
         * @see Device#getName()
         * @param device the device
         * @return the value of the property
         */
        @Override
        public Object getValue(Device device) {
            return device.getName();
        }
    },
    DEVICE_CATEGORY {
        /**
         * This method checks if the value is a valid type.
         * @param value the value
         * @return true if the value is a String, otherwise false
         */
        @Override
        public boolean isValidType(Object value) {
            return value instanceof String;
        }

        /**
         * This method returns the value of the property.
         * @see Device#getCategory()
         * @param device the device
         * @return the value of the property
         */
        @Override
        public Object getValue(Device device) {
            return device.getCategory().name();
        }
    },

    //wenn isWired = true
    POWER_CONSUMPTION {
        /**
         * This method checks if the value is a valid type.
         * @param value the value
         * @return true if the value is a Number, otherwise false
         */
        @Override
        public boolean isValidType(Object value) {
            return value instanceof Number;
        }

        /**
         * This method returns the value of the property.
         * @see WiredDevice#getElectricConsumption()
         * @see ElectricConsumption#getPowerConsumptionInWattSeconds()
         * @param device the device
         * @return the value of the property
         */
        @Override
        public Object getValue(Device device) {
            return device instanceof WiredDevice ? ((WiredDevice) device).getElectricConsumption().getPowerConsumptionInWattSeconds() : 0;
        }
    },
    POWER_CONSUMPTION_UNIT {
        /**
         * This method checks if the value is a valid type.
         * @param value the value
         * @return true if the value is a String, otherwise false
         */
        @Override
        public boolean isValidType(Object value) {
            return value instanceof String;
        }

        /**
         * This method returns the value of the property.
         * @see WiredDevice#getElectricConsumption()
         * @see ElectricConsumption#getUsedEnergyUnit()
         * @param device the device
         * @return the value of the property
         */
        @Override
        public Object getValue(Device device) {
            return device instanceof WiredDevice ? ((WiredDevice) device).getElectricConsumption().getUsedEnergyUnit().name() : getEnergyUnit("").name();
        }
    },
    USAGE {
        /**
         * This method checks if the value is a valid type.
         * @param value the value
         * @return true if the value is a Number, otherwise false
         */
        @Override
        public boolean isValidType(Object value) {
            return value instanceof Number;
        }

        /**
         * This method returns the value of the property.
         * @see WiredDevice#getElectricConsumption()
         * @see ElectricConsumption#getYearlyUsageInSeconds()
         * @param device the device
         * @return the value of the property
         */
        @Override
        public Object getValue(Device device) {
            return device instanceof WiredDevice ? ((WiredDevice) device).getElectricConsumption().getYearlyUsageInSeconds() : 0;
        }
    },
    USAGE_UNIT {
        /**
         * This method checks if the value is a valid type.
         * @param value the value
         * @return true if the value is a String, otherwise false
         */
        @Override
        public boolean isValidType(Object value) {
            return value instanceof String;
        }

        /**
         * This method returns the value of the property.
         * @see WiredDevice#getElectricConsumption()
         * @see ElectricConsumption#getUsedTimeUnit()
         * @param device the device
         * @return the value of the property
         */
        @Override
        public Object getValue(Device device) {
            return device instanceof WiredDevice ? ((WiredDevice) device).getElectricConsumption().getUsedTimeUnit().name() : getTimeUnit("").name();
        }
    },
    USAGE_PER_UNIT {
        /**
         * This method checks if the value is a valid type.
         * @param value the value
         * @return true if the value is a String, otherwise false
         */
        @Override
        public boolean isValidType(Object value) {
            return value instanceof String;
        }

        /**
         * This method returns the value of the property.
         * @see WiredDevice#getElectricConsumption()
         * @see ElectricConsumption#getUsedTimeUnitPer()
         * @param device the device
         * @return the value of the property
         */
        @Override
        public Object getValue(Device device) {
            return device instanceof WiredDevice ? ((WiredDevice) device).getElectricConsumption().getUsedTimeUnitPer().name() : getTimeUnit("").name();
        }
    },
    //wenn isWired = false
    BATTERY_CAPACITY {
        /**
         * This method checks if the value is a valid type.
         * @param value the value
         * @return true if the value is a Number, otherwise false
         */
        @Override
        public boolean isValidType(Object value) {
            return value instanceof Number;
        }

        /**
         * This method returns the value of the property.
         * @see MobileDevice#getBatteryConsumption()
         * @see BatteryConsumption#getCapacityInWattSeconds()
         * @param device the device
         * @return the value of the property
         */
        @Override
        public Object getValue(Device device) {
            return device instanceof WiredDevice ? 0 : ((MobileDevice) device).getBatteryConsumption().getCapacityInWattSeconds();
        }
    },
    BATTERY_CAPACITY_UNIT {
        /**
         * This method checks if the value is a valid type.
         * @param value the value
         * @return true if the value is a String, otherwise false
         */
        @Override
        public boolean isValidType(Object value) {
            return value instanceof String;
        }

        /**
         * This method returns the value of the property.
         * @see MobileDevice#getBatteryConsumption()
         * @see BatteryConsumption#getUsedEnergyUnit()
         * @param device the device
         * @return the value of the property
         */
        @Override
        public Object getValue(Device device) {
            return device instanceof WiredDevice ? getEnergyUnit("").name() : ((MobileDevice) device).getBatteryConsumption().getUsedEnergyUnit().name();
        }
    },
    CHARGING_CYCLE {
        /**
         * This method checks if the value is a valid type.
         * @param value the value
         * @return true if the value is a Number, otherwise false
         */
        @Override
        public boolean isValidType(Object value) {
            return value instanceof Number;
        }

        /**
         * This method returns the value of the property.
         * @see MobileDevice#getBatteryConsumption()
         * @see BatteryConsumption#getChargingCyclesPerYear()
         * @param device the device
         * @return the value of the property
         */
        @Override
        public Object getValue(Device device) {
            return device instanceof WiredDevice ? 0 : ((MobileDevice) device).getBatteryConsumption().getChargingCyclesPerYear();
        }
    },
    CHARGING_CYCLE_UNIT {
        /**
         * This method checks if the value is a valid type.
         * @param value the value
         * @return true if the value is a String, otherwise false
         */
        @Override
        public boolean isValidType(Object value) {
            return value instanceof String;
        }

        /**
         * This method returns the value of the property.
         * @see MobileDevice#getBatteryConsumption()
         * @see BatteryConsumption#getUsedTimeUnit()
         * @param device the device
         * @return the value of the property
         */
        @Override
        public Object getValue(Device device) {
            return device instanceof WiredDevice ? getTimeUnit("").name() : ((MobileDevice) device).getBatteryConsumption().getUsedTimeUnit().name();
        }
    };

    /**
     * This method parses the device property.
     * @param propertyName the property name
     * @return the device property
     */
    public static DeviceProperty parseDeviceProperty(String propertyName) {
        for (DeviceProperty property : DeviceProperty.values()) {
            if (property.name().equals(propertyName)) {
                return property;
            }
        }
        return null;
    }

    /**
     * This method returns the time unit.
     * If the string is not a valid time unit, the method returns the default time unit.
     *
     * @param string the string
     * @return the time unit
     */
    public static TimeUnit getTimeUnit(String string) {
        return TimeUnit.parseTimeUnit(string) == null ? TimeUnit.SECOND : TimeUnit.parseTimeUnit(string);
    }

    /**
     * This method returns the energy unit.
     * If the string is not a valid energy unit, the method returns the default energy unit.
     *
     * @param string the string
     * @return the energy unit
     */
    public static EnergyUnit getEnergyUnit(String string) {
        return EnergyUnit.parseEnergyUnit(string) == null ? EnergyUnit.WATT_SECOND : EnergyUnit.parseEnergyUnit(string);
    }

    /**
     * This method returns the device category.
     * If the string is not a valid device category, the method returns the default device category.
     *
     * @param string the string
     * @return the device category
     */
    public static DeviceCategory getDeviceCategory(String string) {
        return DeviceCategory.parseDeviceCategory(string) == null ? DeviceCategory.OTHER : DeviceCategory.parseDeviceCategory(string);
    }

    /**
     * This method checks if the value is a valid type.
     *
     * @param value the value
     * @return true if the value is a valid type, otherwise false
     */
    public abstract boolean isValidType(Object value);

    /**
     * This method returns the value of the property.
     * It binds the Getters from the {@link Device} class.
     *
     * @param device the device
     * @return the value of the property
     */
    public abstract Object getValue(Device device);
}
