package ch.nfr.calculator;

import ch.nfr.calculator.units.MonthUnit;
import ch.nfr.filehandler.WeatherArchivHandler;
import ch.nfr.filehandler.WeatherArchiveException;
import ch.nfr.tablemodel.Orientation;
import ch.nfr.tablemodel.SolarPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * This class calculates the production of a solar panel for a given postal code and a list of solar panels.
 */
public class SolarPanelCalculator {
    /** The used Logger for this class */
    private static final Logger logger = Logger.getLogger(SolarPanelCalculator.class.getName());
    /** The production of a solar panel in kWh/m^2 per day */
    private static final double SOLAR_PANEL_PRODUCTION = 2.7; // kWh/m^2
    /** The production factors for the north orientations */
    private static final double NORTH_PRODUCTION_FACTOR = 0.15; // Approx 85% loss
    /** The production factors for the east or west orientations */
    private static final double EAST_WEST_PRODUCTION_FACTOR = 0.80; // Approx 20% loss
    /** The production factors for the south orientations */
    private static final double SOUTH_PRODUCTION_FACTOR = 1.0; // Approx 0% loss
    /** The production loss factor median for a year */
    private static final double PRODUCTION_LOSS = 0.175; // Approx 35% efficiency from sun angle and 50 from inverter

    /**
     * Calculate the solar panel production for a given postal code and a list of solar panels
     * Checks if the given list of solar panels is not empty or null.
     * If the one of the area of the solar panels is less than or equal to 0.0 an {@link InvalidSolarPanelException} is thrown.
     *
     * @param postalCode  to identify the location and with that the sun hours.
     * @param solarPanels the list of solar panels to calculate the production for.
     * @return a {@link TotalSolarCalculationWrapper} containing the total yearly production and the production for each solar panel.
     * @throws WeatherArchiveException if an error occurs while getting the sun hours from the weather archive
     */
    public TotalSolarCalculationWrapper calculateSolarPanelProduction(short postalCode, List<SolarPanel> solarPanels, String weatherArchivePath) throws WeatherArchiveException {
        Objects.requireNonNull(solarPanels);
        if (solarPanels.isEmpty()) {
            logger.severe("The list of solar panels must not be empty.");
            throw new IllegalArgumentException("The list of solar panels must not be empty.");
        }
        if (!(solarPanels.stream().allMatch(solarPanel -> solarPanel.getArea() > 0.0))) {
            logger.severe("The area of all solar panels must be greater than 0.0.");
            throw new InvalidSolarPanelException("The area of the solar panel must be greater than 0.0.");
        }

        List<SolarCalculationRecord> solarCalculationRecords = new ArrayList<>();
        List<WeatherArchivHandler.SunHoursPerMonthRecord> monthlySunHours = WeatherArchivHandler.readSunHoursFile(postalCode, weatherArchivePath);

        // Calculate the production for each solar panel
        double totalYearlyProduction = 0;
        for (SolarPanel solarPanel : solarPanels) {
            List<MonthCalculationRecord> monthCalculationRecords = new ArrayList<>();
            double yearlyProductionInKiloWattHour = 0;
            for (WeatherArchivHandler.SunHoursPerMonthRecord sunHours : monthlySunHours) {
                double productionFactor = getProductionFactor(solarPanel.getOrientation());
                double productionPerMonth = solarPanel.getArea() * SOLAR_PANEL_PRODUCTION
                        * productionFactor * (sunHours.sunHours() / 24) * PRODUCTION_LOSS;
                monthCalculationRecords.add(new MonthCalculationRecord(productionPerMonth,
                        sunHours.month()));
                yearlyProductionInKiloWattHour += productionPerMonth;
                logger.info("Production for " + solarPanel.getName() + " in " + sunHours.month() + ": " + productionPerMonth + " kWh");
            }
            totalYearlyProduction += yearlyProductionInKiloWattHour;
            solarCalculationRecords.add(new SolarCalculationRecord(solarPanel, yearlyProductionInKiloWattHour, monthCalculationRecords));
            logger.info("Total production for " + solarPanel.getName() + ": " + yearlyProductionInKiloWattHour + " kWh");
        }
        logger.info("Total yearly production: " + totalYearlyProduction + " kWh");
        return new TotalSolarCalculationWrapper(totalYearlyProduction, solarCalculationRecords);
    }

    /**
     * Get the production factor for a given orientation
     *
     * @param orientation the orientation of the solar panel
     * @return the production factor for the given orientation
     */
    private double getProductionFactor(Orientation orientation) {
        return switch (orientation) {
            case NORTH -> NORTH_PRODUCTION_FACTOR;
            case NORTH_EAST, NORTH_WEST -> (EAST_WEST_PRODUCTION_FACTOR + NORTH_PRODUCTION_FACTOR) / 2;
            case EAST, WEST -> EAST_WEST_PRODUCTION_FACTOR;
            case SOUTH_EAST, SOUTH_WEST -> (EAST_WEST_PRODUCTION_FACTOR + SOUTH_PRODUCTION_FACTOR) / 2;
            case SOUTH -> SOUTH_PRODUCTION_FACTOR;
        };
    }

    /**
     * This record is to store the total yearly production of all the solar panels.
     * And a list of the production for each solar panel.
     * @param totalYearlyProduction the total production in kWh
     * @param solarCalculationRecords the production for each solar panel
     */
    public record TotalSolarCalculationWrapper(double totalYearlyProduction, List<SolarCalculationRecord> solarCalculationRecords) {
    }

    /**
     * This record is to store the total yearly electricity production of one solar panel.
     * And a list of the production for each month.
     * @param solarPanel the solar panel
     * @param yearlyProductionInKiloWattHour the total production in kWh
     * @param monthCalculationRecords the production for each month
     */
    public record SolarCalculationRecord(SolarPanel solarPanel, double yearlyProductionInKiloWattHour,
                                         List<MonthCalculationRecord> monthCalculationRecords) {
    }

    /**
     * This record is to store the produced electricity during one month.
     * @param productionInKiloWattHour the production in kilowatt-hour
     * @param month the month
     */
    public record MonthCalculationRecord(double productionInKiloWattHour, MonthUnit month) {
    }
}
