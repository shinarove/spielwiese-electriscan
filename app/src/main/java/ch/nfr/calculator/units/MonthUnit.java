package ch.nfr.calculator.units;

/**
 * Enum for the months of the year.
 */
public enum MonthUnit {
    JANUARY(1,"Januar"),
    FEBRUARY(2,"Februar"),
    MARCH(3,"März"),
    APRIL(4,"April"),
    MAY(5,"Mai"),
    JUNE(6,"Juni"),
    JULY(7,"Juli"),
    AUGUST(8,"August"),
    SEPTEMBER(9,"September"),
    OCTOBER(10,"Oktober"),
    NOVEMBER(11,"November"),
    DECEMBER(12, "Dezember");

    /** The number of the month. */
    private final int monthNumber;
    /** The German name of the month. */
    private final String germanName;

    /**
     * Constructor for the month unit.
     * @param monthNumber to set
     * @param germanName to set
     */
    MonthUnit(int monthNumber, String germanName) {
        this.monthNumber = monthNumber;
        this.germanName = germanName;
    }

    /**
     * Get the number of the month.
     * @return the number of the month
     */
    public int getMonthNumber() {
        return monthNumber;
    }

    /**
     * Get the German name of the month.
     * @return the German name of the month
     */
    public String getGermanName() {
        return germanName;
    }

    /**
     * Get the month unit for a given month number.
     * If the month number does not match any month unit, it returns null.
     *
     * @param monthNumber the month number
     * @return the month unit or null
     */
    public static MonthUnit getMonthUnit(int monthNumber) {
        for (MonthUnit monthUnit : MonthUnit.values()) {
            if (monthUnit.getMonthNumber() == monthNumber) {
                return monthUnit;
            }
        }
        return null;
    }

    /**
     * Parses the month unit from a string it accepts the {@link #name()} of the enum or the German name.
     * If the string does not match any month unit, it returns null.
     *
     * @param monthUnitString the string to parse
     * @return the month unit or null
     */
    public static MonthUnit parseMonthUnit(String monthUnitString) {
        for (MonthUnit monthUnit : MonthUnit.values()) {
            if (monthUnit.name().equals(monthUnitString) || monthUnit.getGermanName().equals(monthUnitString)) {
                return monthUnit;
            }
        }
        return null;
    }
}
