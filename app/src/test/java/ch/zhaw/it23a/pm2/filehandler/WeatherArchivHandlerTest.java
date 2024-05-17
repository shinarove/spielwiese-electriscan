package ch.zhaw.it23a.pm2.filehandler;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class tests the WeatherArchivHandler class.
 */
public class WeatherArchivHandlerTest {


    /**
     * Test the readSunHoursFile method.
     * @throws WeatherArchiveException if the weather archive file could not be read.
     */
    @Test
    public void testReadSunHoursFile() throws WeatherArchiveException {
        List<WeatherArchivHandler.SunHoursPerMonthRecord> sunHours;
        sunHours = WeatherArchivHandler.readSunHoursFile((short) 1000, "src/test/resources/weather-archive.properties");
        assertEquals(12, sunHours.size());
    }

    /**
     * Test the readSunHoursFile method with all postal codes.
     * @throws WeatherArchiveException if the weather archive file could not be read.
     */
    @Test
    public void testAllPostalCodes() throws WeatherArchiveException {
        for (int i = 1000; i < 9658; i++) {
            String weatherArchive = "src/main/resources/weather-archive/weather-archive.properties";
            List<WeatherArchivHandler.SunHoursPerMonthRecord> sunHours;
            sunHours = WeatherArchivHandler.readSunHoursFile((short) i, weatherArchive);
            if (sunHours.size() != 12) {
                System.out.println("Postal code " + i + " has " + sunHours.size() + " months");
                fail();
            }
        }
    }
}
