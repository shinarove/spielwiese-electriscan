package ch.nfr.calculator;

import ch.nfr.filehandler.WeatherArchiveException;
import ch.nfr.tablemodel.Orientation;
import ch.nfr.tablemodel.SolarPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This class tests the SolarPanelCalculator class.
 */
public class SolarPanelCalculatorTest {

    /**
     * The SolarPanelCalculator instance to test.
     */
    private SolarPanelCalculator calculator;
    /**
     * The list of solar panels to test.
     */
    private List<SolarPanel> solarPanels;
    /**
     * The path to the weather archive properties file.
     */
    private final String path = "src/test/resources/weather-archive.properties";

    /**
     * Set up the test environment.
     */
    @BeforeEach
    void setUp() {
        calculator = new SolarPanelCalculator();
        solarPanels = new ArrayList<>();

        // Mock SolarPanel
        SolarPanel panel = mock(SolarPanel.class);
        when(panel.getArea()).thenReturn(100.0);
        when(panel.getOrientation()).thenReturn(Orientation.SOUTH);
        solarPanels.add(panel);
    }

    /**
     * Positive test No. 1
     * Test the calculateSolarPanelProduction method with a list of solar panels.
     * The method should return a list with one element.
     */
    @Test
    void testCalculateSolarPanelProduction() throws RuntimeException, WeatherArchiveException {
        SolarPanelCalculator.TotalSolarCalculationWrapper result = calculator.calculateSolarPanelProduction((short) 1000, solarPanels, path);
        List<SolarPanelCalculator.SolarCalculationRecord> solarPanelResults = result.solarCalculationRecords();

        // Verify the size of the solarPanelResults
        assertEquals(1, solarPanelResults.size());

        // Verify the total production of the solar panel
        assertEquals(3449.25, solarPanelResults.getFirst().yearlyProductionInKiloWattHour(), 0.001);
    }

    /**
     * Positive test No. 2
     * Test the calculateSolarPanelProduction method with different orientations.
     * The method should return a list with two elements.
     */
    @Test
    void testCalculateSolarPanelProductionWithDifferentOrientations() throws RuntimeException, WeatherArchiveException {
        SolarPanel panelNorth = mock(SolarPanel.class);
        when(panelNorth.getArea()).thenReturn(100.0);
        when(panelNorth.getOrientation()).thenReturn(Orientation.NORTH);
        solarPanels.add(panelNorth);

        SolarPanelCalculator.TotalSolarCalculationWrapper result = calculator.calculateSolarPanelProduction((short) 1000, solarPanels, path);
        List<SolarPanelCalculator.SolarCalculationRecord> results = result.solarCalculationRecords();
        assertEquals(2, results.size());
        // Add your assertions here to verify the production for different orientations
        // Get the first and second element of the list and check the total production
        assertEquals(3449.25, results.getFirst().yearlyProductionInKiloWattHour(), 0.001);
        assertEquals(517.387, results.getLast().yearlyProductionInKiloWattHour(), 0.001);
        assertEquals(3966.637, result.totalYearlyProduction(), 0.001);
    }

    /**
     * Negative test No. 1a
     * Test the calculateSolarPanelProduction method with a solar panel with an area of 0.
     * The method should throw an {@link InvalidSolarPanelException}.
     */
    @Test
    void testCalculateSolarPanelProductionWithEmptyArea() throws InvalidSolarPanelException {
        solarPanels.clear();
        assertEquals(new ArrayList<>(), solarPanels);
        SolarPanel noAreaPanel = mock(SolarPanel.class);
        when(noAreaPanel.getArea()).thenReturn(0.0);
        when(noAreaPanel.getOrientation()).thenReturn(Orientation.SOUTH);
        solarPanels.add(noAreaPanel);
        assertThrows(InvalidSolarPanelException.class, () -> calculator.calculateSolarPanelProduction((short) 8000, solarPanels, path));
    }

    /**
     * Negative test No. 1b
     * Test the calculateSolarPanelProduction method with null solar panels.
     * The method should throw an {@link InvalidSolarPanelException}.
     */
    @Test
    void testCalculateSolarPanelProductionWithNull() {
        assertThrows(NullPointerException.class, () -> calculator.calculateSolarPanelProduction((short) 8000, null, path));
    }

    /**
     * Negative test No. 1c
     * Test the calculateSolarPanelProduction method with an empty list of solar panels.
     * The method should throw an {@link IllegalArgumentException}.
     */
    @Test
    void testCalculateSolarPanelProductionWithEmptyList() {
        solarPanels.clear();
        assertEquals(new ArrayList<>(), solarPanels);
        assertThrows(IllegalArgumentException.class, () -> calculator.calculateSolarPanelProduction((short) 8000, solarPanels, path));
    }

    /**
     * Negative test No. 2
     * Test the calculateSolarPanelProduction method with an invalid path.
     * The method should throw a {@link WeatherArchiveException}.
     */
    @Test
    void testCalculateSolarPanelProductionWithInvalidPath() {
        assertThrows(WeatherArchiveException.class, () -> calculator.calculateSolarPanelProduction((short) 8000, solarPanels, "src/test/resources/weather-archive-invalid.properties"));
    }

    /**
     * Negative test No. 3
     * Test the calculateSolarPanelProduction method with an empty sun hours list.
     * The method should throw an {@link WeatherArchiveException}.
     */
    @Test
    void testCalculateSolarPanelProductionWithEmptySunHours() {
        assertThrows(WeatherArchiveException.class, () -> calculator.calculateSolarPanelProduction((short) 99999, solarPanels, path));
    }

}

