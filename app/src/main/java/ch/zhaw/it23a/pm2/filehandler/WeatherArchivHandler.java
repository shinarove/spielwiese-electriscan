package ch.zhaw.it23a.pm2.filehandler;

import ch.zhaw.it23a.pm2.calculatorAndConverter.units.MonthUnit;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * This class is responsible for reading the sun hours file and returning the sun hours for the given postal code.
 * The class is a singleton and can't be instantiated. It provides a static method to read the sun hours file.
 */
public class WeatherArchivHandler {
    /** The used Logger in this class */
    private static final Logger logger = Logger.getLogger(WeatherArchivHandler.class.getName());

    /**
     * The private constructor to prevent the instantiation of this class.
     */
    private WeatherArchivHandler() {
    }

    /**
     * Reads the weather archive file and returns the sun hours for each month for the given postal code.
     *
     * @param weatherArchivePath the path to the weather archive file
     * @param postalCode the postal code for which the sun hours should be read
     * @return a list of {@link SunHoursPerMonthRecord} containing the sun hours for each month
     * @throws WeatherArchiveException if an error occurs while reading the sun hours file or no sun hours are found for the given postal code
     */
    public static List<SunHoursPerMonthRecord> readSunHoursFile(short postalCode, String weatherArchivePath) throws WeatherArchiveException {
        Objects.requireNonNull(weatherArchivePath, "Properties file must not be null");
        List<SunHoursPerMonthRecord> sunHours = new ArrayList<>();
        String postalCodeString = Short.toString(postalCode);

        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(weatherArchivePath)) {
            properties.load(fileInputStream);

            properties.stringPropertyNames().stream()
                    .filter(key -> {
                        String[] keyParts = key.split("\\."); // Split the key into parts
                        String postalCodeRegex = keyParts[0];// Get the postal code regex from the first part of the key
                        return postalCodeString.matches(postalCodeRegex); // Check if the postal code matches the regex
                    })
                    .forEach(key -> {
                        int sunHour = Integer.parseInt(properties.getProperty(key)); // Split the value to get the sun hours
                        MonthUnit month = MonthUnit.parseMonthUnit(key.split("\\.")[1]);
                        sunHours.add(new SunHoursPerMonthRecord(month, sunHour)); // Parse and add sun hours to the list
                    });

        } catch (IOException e) {
            logger.severe("There was an error while reading the weather archive." + e.getMessage());
            throw new WeatherArchiveException("There was an error while reading the weather archive.", e);
        }

        if (sunHours.isEmpty()) {
            logger.severe("No weather archive data found for the given postal code.");
            throw new WeatherArchiveException("No weather archive data found for the given postal code.");
        } else {
            logger.info("Successfully read weather archive for postal code " + postalCode);
            return sunHours;
        }
    }

    /**
     * A record to store the sun hours for a month.
     *
     * @param month as a {@link MonthUnit}
     * @param sunHours the sun hours for the month
     */
    public record SunHoursPerMonthRecord(MonthUnit month, int sunHours) {
    }

}
