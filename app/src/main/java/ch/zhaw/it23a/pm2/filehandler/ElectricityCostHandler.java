package ch.zhaw.it23a.pm2.filehandler;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * This class is responsible for handling the electricity cost.
 * It reads the electricity cost from a properties file and returns the electricity cost for a given postal code.
 */
public class ElectricityCostHandler {
    /** The used logger for this class. */
    private static final Logger logger = Logger.getLogger(ElectricityCostHandler.class.getName());

    /**
     * Get the electricity price for a given postal code.
     * Reads from a properties file and returns the electricity cost for the given postal code.
     *
     * @param postalCode the postal code
     * @param path the path to the properties file
     * @return the electricity cost for the given postal code
     * @throws ElectricityPriceDataException if an error occurs while reading the properties file
     */
    public static double getElectricityPrice(short postalCode, String path) throws ElectricityPriceDataException {
        String postalCodeString = Short.toString(postalCode);
        List<Double> electricityCost = new ArrayList<>();
        Properties properties = new Properties();

        try (FileInputStream fileInputStream = new FileInputStream(path)) {
            properties.load(fileInputStream);

            properties.stringPropertyNames()
                    .stream()
                    .filter(postalCodeString::matches)
                    .forEach(key -> electricityCost.add(Double.parseDouble(properties.getProperty(key))));
        } catch (IOException e) {
            logger.severe("Error while reading the electricity cost properties file: " + e.getMessage());
            throw new ElectricityPriceDataException("Error while reading the electricity cost properties file: " + e.getMessage());
        }
        return electricityCost.getFirst();
    }
}
