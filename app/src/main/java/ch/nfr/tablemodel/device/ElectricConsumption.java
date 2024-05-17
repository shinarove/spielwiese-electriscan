package ch.nfr.tablemodel.device;

import ch.nfr.calculator.units.EnergyUnit;
import ch.nfr.calculator.units.TimeUnit;

/**
 * Represents the electric consumption of a device.
 */
public class ElectricConsumption implements Consumption {

    /** The electric power in milliwatt. */
    private final long powerConsumptionInWattSeconds;
    /** The yearly usage in seconds. */
    private final long yearlyUsageInSeconds;
    /** The yearly consumption in milliwatt seconds. */
    private final long yearlyConsumptionInWattSeconds;
    /** The time unit. */
    private final TimeUnit usedTimeUnit;
    /** The time unit per. */
    private final TimeUnit usedTimeUnitPer;
    /** The energy unit. */
    private final EnergyUnit usedEnergyUnit;

    /**
     * Creates a new electric consumption.
     * @param powerConsumptionInWattSeconds the electric power in milliwatt
     * @param yearlyUsageInSeconds the yearly usage in seconds
     * @param usedTimeUnit the time unit
     * @param usedTimeUnit2 the time unit per
     * @param usedEnergyUnit the energy unit
     */
    public ElectricConsumption(long powerConsumptionInWattSeconds, long yearlyUsageInSeconds, TimeUnit usedTimeUnit, TimeUnit usedTimeUnit2, EnergyUnit usedEnergyUnit) {
        this.powerConsumptionInWattSeconds = powerConsumptionInWattSeconds;
        this.yearlyUsageInSeconds = yearlyUsageInSeconds;
        this.yearlyConsumptionInWattSeconds = (powerConsumptionInWattSeconds * yearlyUsageInSeconds) / TimeUnit.HOUR.getFactor();

        this.usedTimeUnit = usedTimeUnit;
        this.usedTimeUnitPer = usedTimeUnit2;
        this.usedEnergyUnit = usedEnergyUnit;
    }

    /**
     * Returns the electric power in milliwatt.
     *
     * @return the electric power in milliwatt
     */
    public long getPowerConsumptionInWattSeconds() {
        return powerConsumptionInWattSeconds;
    }

    /**
     * Returns the yearly usage in seconds.
     *
     * @return the yearly usage in seconds
     */
    public long getYearlyUsageInSeconds() {
        return yearlyUsageInSeconds;
    }

    /**
     * Returns the yearly consumption in milliwatt seconds.
     *
     * @return the yearly consumption in milliwatt seconds
     */
    public long getYearlyConsumptionInWattSeconds() {
        return yearlyConsumptionInWattSeconds;
    }

    /**
     * Returns the used time unit.
     *
     * @return the used time unit
     */
    public TimeUnit getUsedTimeUnit() {
        return usedTimeUnit;
    }

    /**
     * Returns the used time unit per.
     *
     * @return the used time unit per
     */
    public TimeUnit getUsedTimeUnitPer() {
        return usedTimeUnitPer;
    }

    /**
     * Returns the used energy unit.
     *
     * @return the used energy unit
     */
    public EnergyUnit getUsedEnergyUnit() {
        return usedEnergyUnit;
    }
}
