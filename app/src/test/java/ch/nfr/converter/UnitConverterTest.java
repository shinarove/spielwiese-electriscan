package ch.nfr.converter;

import ch.nfr.calculator.converter.UnitConverter;
import org.junit.jupiter.api.Test;

import static ch.nfr.calculator.units.EnergyUnit.*;
import static ch.nfr.calculator.units.TimeUnit.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This class tests the UnitConverter class.
 */
class UnitConverterTest {

    /**
     * Tests the convertEnergyToWattSeconds method of the UnitConverter class.
     */
    @Test
    void convertTimeToSeconds() {
        assertEquals(1L,
                UnitConverter.convertTimeToSeconds(1.0, SECOND), "1 second converts to 1 second");
        assertEquals(60L,
                UnitConverter.convertTimeToSeconds(1.0, MINUTE), "1 minute converts to 60 seconds");
        assertEquals(3600L,
                UnitConverter.convertTimeToSeconds(1.0, HOUR), "1 hour converts to 3600 seconds");
        assertEquals(86_400L,
                UnitConverter.convertTimeToSeconds(1.0, DAY), "1 day converts to 86'400 seconds");
        assertEquals(604_800L,
                UnitConverter.convertTimeToSeconds(1.0, WEEK), "1 week converts to 604'800 seconds");
        assertEquals(2_628_288L,
                UnitConverter.convertTimeToSeconds(1.0, MONTH), "1 month converts to 2'592'000 seconds");
        assertEquals(31_536_000L,
                UnitConverter.convertTimeToSeconds(1.0, YEAR), "1 year converts to 31'536'000 seconds");

        assertEquals(0L,
                UnitConverter.convertTimeToSeconds(0.0, SECOND), "0 seconds converts to 0 seconds");

        assertEquals(58L,
                UnitConverter.convertTimeToSeconds(58.233, SECOND), "58.233 seconds convert to 58 seconds");
        assertEquals(3494L,
                UnitConverter.convertTimeToSeconds(58.233, MINUTE), "58.233 minutes convert to 3'494 seconds");
        assertEquals(209_639L,
                UnitConverter.convertTimeToSeconds(58.233, HOUR), "58.233 hours convert to 209'639 seconds");
        assertEquals(5_031_331L,
                UnitConverter.convertTimeToSeconds(58.233, DAY), "58.233 days convert to 5'031'331 seconds");
        assertEquals(35_219_318L,
                UnitConverter.convertTimeToSeconds(58.233, WEEK), "58.233 weeks convert to 35'19'318 seconds");
        assertEquals(153_053_095L,
                UnitConverter.convertTimeToSeconds(58.233, MONTH), "58.233 months convert to 150'939'936 seconds");
        assertEquals(1_836_435_888L,
                UnitConverter.convertTimeToSeconds(58.233, YEAR), "58.233 years convert to 1'837'693'721 seconds");
    }


    /**
     * Tests the convertEnergyToWattSeconds method of the UnitConverter class.
     */
    @Test
    void convertEnergyToWattSeconds() {
        assertEquals(1L,
                UnitConverter.convertEnergyToWattSeconds(1.0, WATT_SECOND),
                "1 watt second converts to 1 watt seconds");
        assertEquals(3600L,
                UnitConverter.convertEnergyToWattSeconds(1.0, WATT_HOUR),
                "1 watt hour converts to 3600 watt seconds");
        assertEquals(3_600_000L,
                UnitConverter.convertEnergyToWattSeconds(1.0, KILOWATT_HOUR),
                "1 kilowatt hour converts to 3'600'000 watt seconds");

        assertEquals(0L,
                UnitConverter.convertEnergyToWattSeconds(0.0, WATT_SECOND),
                "0 milliwatt seconds converts to 0 milliwatt seconds");

        assertEquals(58L,
                UnitConverter.convertEnergyToWattSeconds(58.233, WATT_SECOND),
                "58.233 watt seconds convert to 58 watt seconds");
        assertEquals(209_639L,
                UnitConverter.convertEnergyToWattSeconds(58.233, WATT_HOUR),
                "58.233 watt hours convert to 209'639 watt seconds");
        assertEquals(209_638_800L,
                UnitConverter.convertEnergyToWattSeconds(58.233, KILOWATT_HOUR),
                "58.233 kilowatt hours convert to 209'638'800 watt seconds");
    }
}