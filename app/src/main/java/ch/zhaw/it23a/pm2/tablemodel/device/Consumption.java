package ch.zhaw.it23a.pm2.tablemodel.device;

/**
 * Represents the consumption of a device.
 */
public interface Consumption {

    /**
     * Returns the yearly consumption in watt seconds.
     * @return the yearly consumption in watt seconds
     */
    long getYearlyConsumptionInWattSeconds();

}
