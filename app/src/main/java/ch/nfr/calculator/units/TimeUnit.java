package ch.nfr.calculator.units;

/**
 *  Represents the time units. Holds the time in seconds for each unit.
 * SECONDS: 1 second
 * MINUTES: 60 seconds
 * HOURS: 3600 seconds
 * DAYS: 86,400 seconds
 * WEEKS: 604,800 seconds
 * MONTHS: 2,592,000 seconds (months are calculated with 30 days)
 * YEARS: 31,536,000 seconds (years are calculated with 365 days)
 */
public enum TimeUnit {
    SECOND(1L, "Sekunden", "Sekunde"),
    MINUTE(60L, "Minuten", "Minute"),
    HOUR(3600L, "Stunden", "Stunde"),
    DAY(86_400L, "Tage", "Tag"),
    WEEK(604_800L, "Wochen", "Woche"),
    //a month is calculated as 30.42 days
    MONTH(2_628_288L, "Monate", "Monat"),
    //a year is calculated as 365 days
    YEAR(31_536_000L, "Jahre", "Jahr");

    /**
     * The factor to convert the time unit to seconds.
     */
    private final long factor;

    /**
     * The german name of the time unit in plural and singular.
     */
    private final String germanNamePlural;
    /**
     * The german name of the time unit in singular.
     */
    private final String germanNameSingle;

    /**
     * Creates a new time unit.
     * @param factor the factor to convert the time unit to seconds
     * @param germanNamePlural the german name of the time unit in plural
     * @param germanNameSingle the german name of the time unit in singular
     */
    TimeUnit(long factor, String germanNamePlural, String germanNameSingle) {
        this.germanNamePlural = germanNamePlural;
        this.germanNameSingle = germanNameSingle;
        this.factor = factor;
    }

    /**
     * Returns the factor to convert the time unit to seconds.
     * @return the factor
     */
    public long getFactor(){
        return factor;
    }

    /**
     * Returns the german name of the time unit in plural.
     * @return the german name in plural
     */
    public String getGermanNamePlural() {
        return germanNamePlural;
    }

    /**
     * Returns the german name of the time unit in singular.
     * @return the german name in singular
     */
    public String getGermanNameSingle() {
        return germanNameSingle;
    }

    /**
     * Parses a time unit from a string.
     * @param timeUnit the time unit as string
     * @return the time unit or null if the time unit is not found
     */
    public static TimeUnit parseTimeUnit(String timeUnit) {
        for (TimeUnit unit : TimeUnit.values()) {
            if (unit.name().equals(timeUnit) || unit.getGermanNamePlural().equals(timeUnit) || unit.getGermanNameSingle().equals(timeUnit)) {
                return unit;
            }
        }
        return null;
    }
}
