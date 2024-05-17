package ch.zhaw.it23a.pm2.calculatorAndConverter;

import ch.zhaw.it23a.pm2.calculatorAndConverter.units.EnergyUnit;
import ch.zhaw.it23a.pm2.calculatorAndConverter.units.TimeUnit;

import java.util.Objects;

/**
 * This class provides methods to convert units.
 * It is a utility class and cannot be instantiated.
 */
public class UnitConverter {

    /**
     * Private constructor to prevent instantiation.
     */
    private UnitConverter() {
    }

    /**
     * Converts a time to seconds.
     * @param time the time
     * @param timeUnit the time unit
     * @return the time in seconds
     */
    public static long convertTimeToSeconds(double time, TimeUnit timeUnit) {
        Objects.requireNonNull(timeUnit, "Time unit must not be null.");
        return Math.round(time * timeUnit.getFactor());
    }

    /**
     * Converts seconds to a time.
     * @param seconds the seconds
     * @param timeUnit the time unit
     * @return the time
     */
    public static double convertSecondsTo(long seconds, TimeUnit timeUnit) {
        Objects.requireNonNull(timeUnit, "Time unit must not be null.");
        return (double) seconds / timeUnit.getFactor();
    }

    /**
     * Converts energy to watt seconds.
     * @param energy the energy
     * @param energyUnit the energy unit
     * @return the energy in watt seconds
     */
    public static long convertEnergyToWattSeconds(double energy, EnergyUnit energyUnit) {
        Objects.requireNonNull(energyUnit, "Energy unit must not be null.");
        return Math.round(energy * energyUnit.getFactor());
    }

    /**
     * Converts watt seconds to energy.
     * @param wattSeconds the watt seconds
     * @param energyUnit the energy unit
     * @return the energy
     */
    public static double convertWattSecondsTo(long wattSeconds, EnergyUnit energyUnit) {
        Objects.requireNonNull(energyUnit, "Energy unit must not be null.");
        return (double) wattSeconds / energyUnit.getFactor();
    }
}
