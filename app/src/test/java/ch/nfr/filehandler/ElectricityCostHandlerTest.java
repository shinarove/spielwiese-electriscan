package ch.nfr.filehandler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This class tests the ElectricityCostHandler class.
 */
public class ElectricityCostHandlerTest {

    /**
     * Test the getElectricityPrice method.
     *
     * @throws ElectricityPriceDataException if the electricity price data could not be read.
     */
    @Test
    void testGetElectricityPrice() throws ElectricityPriceDataException {
        String testingPath = "src/test/resources/electricityPrice.properties";

        short postalCode = 8000; // Replace with the postal code you want to test
        double expectedPrice = 31.61; // Replace with the expected price
        double actualPrice = ElectricityCostHandler.getElectricityPrice(postalCode, testingPath);
        assertEquals(expectedPrice, actualPrice, "The electricity price for postal code " + postalCode + " should be " + expectedPrice);

        postalCode = 7999;
        expectedPrice = 29.85;
        actualPrice = ElectricityCostHandler.getElectricityPrice(postalCode, testingPath);
        assertEquals(expectedPrice, actualPrice, "The electricity price for postal code " + postalCode + " should be " + expectedPrice);
    }
}
