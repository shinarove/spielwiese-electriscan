package ch.zhaw.it23a.pm2.tablemodel.device;

import ch.zhaw.it23a.pm2.calculatorAndConverter.units.EnergyUnit;
import ch.zhaw.it23a.pm2.calculatorAndConverter.units.TimeUnit;

/**
 * Represents the battery consumption of a mobile device.
 */

public class BatteryConsumption implements Consumption {
    /** The number of charging cycles per year. */
    private final long chargingCyclesPerYear;
    /** The capacity in milliwatt seconds. */
    private final long capacityInWattSeconds;
    /** The yearly consumption in milliwatt seconds. */
    private final long yearlyConsumptionInWattSeconds;

    /** The time unit. */
    private final TimeUnit usedTimeUnit;
    /** The energy unit. */
    private final EnergyUnit usedEnergyUnit;

    /**
     * Creates a new battery consumption.
     * @param chargingCyclesPerYear the number of charging cycles per year
     * @param capacityInWattSeconds the capacity in milliwatt seconds
     * @param usedTimeUnit the time unit
     * @param usedEnergyUnit the energy unit
     */
    public BatteryConsumption(long chargingCyclesPerYear, long capacityInWattSeconds, TimeUnit usedTimeUnit, EnergyUnit usedEnergyUnit) {
        this.chargingCyclesPerYear = chargingCyclesPerYear;
        this.capacityInWattSeconds = capacityInWattSeconds;
        this.yearlyConsumptionInWattSeconds = (chargingCyclesPerYear * capacityInWattSeconds) / TimeUnit.HOUR.getFactor();

        this.usedTimeUnit = usedTimeUnit;
        this.usedEnergyUnit = usedEnergyUnit;
    }

    /**
     * returns the number of charging cycles per year
     *
     * @return the number of charging cycles per year
     */
    public long getChargingCyclesPerYear() {
        return chargingCyclesPerYear;
    }

    /**
     * returns the capacity in milliwatt seconds
     *
     * @return the capacity in milliwatt seconds
     */
    public long getCapacityInWattSeconds() {
        return capacityInWattSeconds;
    }

    /**
     * returns the yearly consumption in milliwatt seconds
     *
     * @return the yearly consumption in milliwatt seconds
     */
    public long getYearlyConsumptionInWattSeconds() {
        return yearlyConsumptionInWattSeconds;
    }

    /**
     * returns the used time unit
     *
     * @return the used time unit
     */
    public TimeUnit getUsedTimeUnit() {
        return usedTimeUnit;
    }

    /**
     * returns the used energy unit
     *
     * @return the used energy unit
     */
    public EnergyUnit getUsedEnergyUnit() {
        return usedEnergyUnit;
    }
}
