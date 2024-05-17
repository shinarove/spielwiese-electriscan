package ch.zhaw.it23a.pm2.calculatorAndConverter.units;

/**
 * Represents the energy units. Holds the energy in watt seconds for each unit.
 * WATT_SECOND: 1 watt second
 * WATT_HOUR: 3,600 watt seconds
 * KILOWATT_HOUR: 3,600,000 watt seconds
 */
public enum EnergyUnit {

    WATT_SECOND(1L, "Wattsekunden"),
    WATT_HOUR(3600L, "Wattstunden"),
    KILOWATT_HOUR(3_600_000L, "Kilowattstunden");

    /**
     * The factor to convert the energy unit to watt seconds.
     */
    private final long factor;

    /**
     * The german name of the
     * energy unit.
     */
    private final String germanName;

    /**
     * Creates a new energy unit.
     * @param factor the factor to convert the energy unit to watt seconds
     * @param germanName the german name of the
     * energy unit
     */
    EnergyUnit(long factor, String germanName) {
        this.factor = factor;
        this.germanName = germanName;
    }

    /**
     * Returns the factor to convert the energy unit to watt seconds.
     * @return the factor
     */
    public long getFactor() {
        return factor;
    }

    /**
     * Returns the german name of the energy unit.
     * @return the german name
     */
    public String getGermanName(){
        return germanName;
    }

    /**
     * Parses the energy unit.
     * @param energyUnit the energy unit
     * @return the energy unit
     */
    public static EnergyUnit parseEnergyUnit(String energyUnit) {
        for (EnergyUnit unit : EnergyUnit.values()) {
            if (unit.name().equals(energyUnit) || unit.getGermanName().equals(energyUnit)) {
                return unit;
            }
        }
        return null;
    }
}