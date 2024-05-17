package ch.nfr.calculator;

import ch.nfr.calculator.units.MonthUnit;
import ch.nfr.filehandler.WeatherArchiveException;
import ch.nfr.tablemodel.Household;
import ch.nfr.tablemodel.Room;
import ch.nfr.tablemodel.SolarPanel;
import ch.nfr.tablemodel.device.Device;
import ch.nfr.tablemodel.device.DeviceCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This class tests the CostCalculator class.
 */
public class CostCalculatorTest {

    /**
     * The SolarPanelCalculator instance to test.
     */
    SolarPanelCalculator mockedSolarCalculator = mock(SolarPanelCalculator.class);
    /**
     * The Household instance to test.
     */
    Household mockedHousehold;
    /**
     * The list of rooms to test.
     */
    final List<Room> mockedRooms = new ArrayList<>();
    /**
     * The list of devices to test.
     */
    final List<Device> mockedDevices = new ArrayList<>();
    /**
     * The list of solar panels to test.
     */
    final List<SolarPanel> mockedSolarPanels = new ArrayList<>();
    /**
     * The valid electricity cost to test.
     */
    final double validElectricityCost = 32.14;
    /**
     * The path to the weather archive properties file.
     */
    final String weatherArchivePath = "src/main/resources/weather-archive/weather-archive.properties";

    /**
     * Set up the test environment.
     */
    @BeforeEach
    void setUp() {
        mockedHousehold = mock(Household.class);
        doAnswer(invocation -> (short) 8450).when(mockedHousehold).getPostalCode();

        mockedSolarPanels.add(mock(SolarPanel.class));

        mockedRooms.add(mock(Room.class));
        mockedRooms.add(mock(Room.class));

        mockedDevices.add(mock(Device.class));
        mockedDevices.add(mock(Device.class));
        mockedDevices.add(mock(Device.class));
        mockedDevices.add(mock(Device.class));
    }

    /**
     * Set up the mocked devices with valid yearly consumption and categories.
     */
    private void setupValidMockedDevices() {
        for (int i = 0; i < mockedDevices.size(); i++) {
            DeviceCategory category = DeviceCategory.values()[i];
            doAnswer(invocation -> category).when(mockedDevices.get(i)).getCategory();

            doAnswer(invocation -> 10_000_000L).when(mockedDevices.get(i)).getYearlyConsumptionInWattSeconds();
        }
    }

    /**
     * Set up the mocked devices with small yearly consumption and categories.
     */
    private void setupSmallMockedDevices() {
        for (int i = 0; i < mockedDevices.size(); i++) {
            DeviceCategory category = DeviceCategory.values()[i];
            doAnswer(invocation -> category).when(mockedDevices.get(i)).getCategory();

            doAnswer(invocation -> 1_000_000L).when(mockedDevices.get(i)).getYearlyConsumptionInWattSeconds();
        }
    }

    /**
     * Set up the mocked rooms with valid yearly consumption.
     */
    private void setupMockedRooms() {
        int number = 1;
        for (Room mockedRoom : mockedRooms) {
            doAnswer(invocation -> mockedDevices).when(mockedRoom).getAllDevices();
            int finalNumber = number;
            doAnswer(invocation -> "Room " + finalNumber).when(mockedRoom).getName();
            number++;
        }
    }

    /**
     * Set up the mocked solar panels with valid production.
     */
    private void setupMockedSolarPanels() {
        int number = 1;
        for (SolarPanel mockedSolarPanel : mockedSolarPanels) {
            int finalNumber = number;
            doAnswer(invocation -> "Solar panel " + finalNumber).when(mockedSolarPanel).getName();
            number++;
        }
    }

    /**
     * Set up the mocked solar panel calculator with valid production.
     * @throws WeatherArchiveException if an error occurs while getting the sun hours from the weather archive
     */
    private void setupMockedSolarPanelCalculator() throws WeatherArchiveException {
        doAnswer(invocation -> {
            SolarPanelCalculator.TotalSolarCalculationWrapper totalResult;
            double totalYearlyProduction = 0.0;

            List<SolarPanelCalculator.SolarCalculationRecord> totalResults = new ArrayList<>();
            for (SolarPanel solarPanel : mockedSolarPanels) {
                double totalProduction = 0.0;
                List<SolarPanelCalculator.MonthCalculationRecord> monthResults = new ArrayList<>();
                for (MonthUnit month : MonthUnit.values()) {
                    monthResults.add(new SolarPanelCalculator.MonthCalculationRecord(1.0, month));
                    totalProduction += 1.0;
                    totalYearlyProduction += 1.0;
                }
                totalResults.add(new SolarPanelCalculator.SolarCalculationRecord(solarPanel, totalProduction, monthResults));
            }
            totalResult = new SolarPanelCalculator.TotalSolarCalculationWrapper(totalYearlyProduction, totalResults);
            return totalResult;
        }).when(mockedSolarCalculator).calculateSolarPanelProduction(Mockito.anyShort(), Mockito.anyList(), Mockito.anyString());
    }

    /**
     * Positiv test Nr. 1a
     * Test case for household with only devices.
     */
    @Test
    void onlyDevices() throws NoRegisteredDeviceException, WeatherArchiveException {
        CostCalculator costCalculator = new CostCalculator(mockedSolarCalculator, weatherArchivePath);

        setupValidMockedDevices();
        setupMockedRooms();

        doAnswer(invocation -> mockedRooms).when(mockedHousehold).getAllRooms();
        doAnswer(invocation -> new ArrayList<>()).when(mockedHousehold).getAllSolarPanels();

        CostCalculator.CalculationRecordWrapper result = costCalculator.calculateCost(mockedHousehold, validElectricityCost);

        assertEquals(4, result.deviceCalculationRecords().size(),
                "There should be 4 different device calculation records");
        assertEquals(2, result.roomCalculationRecords().size(),
                "There should be 2 different room calculation records");
        assertEquals(0, result.totalSolarCalculationRecord().solarCalculationRecords().size(),
                "There should be 0 solar calculation records");

        double delta = 0.0001;
        for (CostCalculator.DeviceCalculationRecord deviceResult : result.deviceCalculationRecords()) {
            assertTrue(Arrays.stream(DeviceCategory.values()).anyMatch(category -> category.equals(deviceResult.deviceCategory())),
                    "In the result there should be the category " + deviceResult.deviceCategory());

            assertEquals(20_000_000L, deviceResult.powerConsumptionInWattSeconds(),
                    "The power consumption should be 20'000'000 for category " + deviceResult.deviceCategory());
            assertEquals(178.5555, deviceResult.electricityCostInRp(), delta,
                    "The electricity cost should be 178.5555 for category " + deviceResult.deviceCategory());
        }

        for (CostCalculator.RoomCalculationRecord roomResult : result.roomCalculationRecords()) {
            assertTrue(mockedRooms.stream().anyMatch(room -> room.getName().equals(roomResult.room().getName())),
                    "In the result there should be the room " + roomResult.room().getName());

            assertEquals(40_000_000L, roomResult.powerConsumptionInWattSeconds(),
                    "The power consumption should be 40'000'000 for room " + roomResult.room().getName());
            assertEquals(357.1111, roomResult.electricityCostInRp(), delta,
                    "The electricity cost should be 357.111 for room " + roomResult.room().getName());
        }

        assertEquals(80_000_000L, result.totalCostRecord().yearlyCorrectedConsumptionInWattSeconds(),
                "The total power consumption should be 80'000'000");
        assertEquals(714.2222, result.totalCostRecord().yearlyElectricityCostInRp(), delta,
                "The total electricity cost should be 714.2222");
        assertEquals(validElectricityCost, result.totalCostRecord().electricityCostInRpPerkWh(),
                "The electricity cost per kWh should be the same as the input");

        Mockito.verifyNoInteractions(mockedSolarCalculator);
    }

    /**
     * Positiv test Nr. 1b-I
     * Test case for household with solar panels and devices.
     * The sum of the yearly consumption of the devices is greater than the production of the solar panels.
     */
    @Test
    void sumEnergyDevicesGreaterThanSolarPanels() throws NoRegisteredDeviceException, InvalidSolarPanelException, WeatherArchiveException {
        CostCalculator costCalculator = new CostCalculator(mockedSolarCalculator, weatherArchivePath);
        setupMockedSolarPanelCalculator();

        setupValidMockedDevices();
        setupMockedRooms();
        setupMockedSolarPanels();

        doAnswer(invocation -> mockedRooms).when(mockedHousehold).getAllRooms();
        doAnswer(invocation -> mockedSolarPanels).when(mockedHousehold).getAllSolarPanels();

        CostCalculator.CalculationRecordWrapper result = costCalculator.calculateCost(mockedHousehold, validElectricityCost);

        assertEquals(4, result.deviceCalculationRecords().size(),
                "There should be 4 different device calculation records");
        assertEquals(2, result.roomCalculationRecords().size(),
                "There should be 2 different room calculation records");
        assertEquals(1, result.totalSolarCalculationRecord().solarCalculationRecords().size(),
                "There should be 1 solar calculation records");

        double delta = 0.0001;
        for (CostCalculator.DeviceCalculationRecord deviceResult : result.deviceCalculationRecords()) {
            assertTrue(Arrays.stream(DeviceCategory.values()).anyMatch(category -> category.equals(deviceResult.deviceCategory())),
                    "In the result there should be the category " + deviceResult.deviceCategory());

            assertEquals(20_000_000L, deviceResult.powerConsumptionInWattSeconds(),
                    "The power consumption should be 20'000'000 for category " + deviceResult.deviceCategory());
            assertEquals(178.5555, deviceResult.electricityCostInRp(), delta,
                    "The electricity cost should be 178.5555 for category " + deviceResult.deviceCategory());
        }

        for (CostCalculator.RoomCalculationRecord roomResult : result.roomCalculationRecords()) {
            assertTrue(mockedRooms.stream().anyMatch(room -> room.getName().equals(roomResult.room().getName())),
                    "In the result there should be the room " + roomResult.room().getName());

            assertEquals(40_000_000L, roomResult.powerConsumptionInWattSeconds(),
                    "The power consumption should be 40'000'000 for room " + roomResult.room().getName());
            assertEquals(357.1111, roomResult.electricityCostInRp(), delta,
                    "The electricity cost should be 357.111 for room " + roomResult.room().getName());
        }

        for (SolarPanelCalculator.SolarCalculationRecord solarResult : result.totalSolarCalculationRecord().solarCalculationRecords()) {
            assertTrue(mockedSolarPanels.stream().anyMatch(solarPanel -> solarPanel.getName().equals(solarResult.solarPanel().getName())),
                    "In the result there should be the solar panel " + solarResult.solarPanel().getName());

            assertEquals(12L, solarResult.yearlyProductionInKiloWattHour(),
                    "The total production should be 12kWh for solar panel " + solarResult.solarPanel().getName());
            assertEquals(12, solarResult.monthCalculationRecords().size(),
                    "There should be 12 month results for solar panel " + solarResult.solarPanel());

            for (SolarPanelCalculator.MonthCalculationRecord monthResult : solarResult.monthCalculationRecords()) {
                assertEquals(1L, monthResult.productionInKiloWattHour(),
                        "The production should be 1kWh for month " + monthResult.month());
            }
        }

        assertEquals(36_800_000L, result.totalCostRecord().yearlyCorrectedConsumptionInWattSeconds(),
                "The total power consumption should be 36_800_000");
        assertEquals(328.5422, result.totalCostRecord().yearlyElectricityCostInRp(), delta,
                "The total electricity cost should be 328.5422");
        assertEquals(validElectricityCost, result.totalCostRecord().electricityCostInRpPerkWh(),
                "The electricity cost per kWh should be the same as the input");

        Mockito.verify(mockedSolarCalculator).calculateSolarPanelProduction((short) 8450, mockedSolarPanels, weatherArchivePath);
    }

    /**
     * Positiv test Nr. 1b-II
     * Test case for household with solar panels and devices.
     * The sum of the yearly consumption of the devices is smaller than the production of the solar panels.
     */
    @Test
    void sumEnergyDevicesSmallerThanSolarPanels() throws NoRegisteredDeviceException, WeatherArchiveException {
        CostCalculator costCalculator = new CostCalculator(mockedSolarCalculator, weatherArchivePath);
        setupMockedSolarPanelCalculator();

        setupSmallMockedDevices();
        setupMockedRooms();
        setupMockedSolarPanels();

        doAnswer(invocation -> mockedRooms).when(mockedHousehold).getAllRooms();
        doAnswer(invocation -> mockedSolarPanels).when(mockedHousehold).getAllSolarPanels();

        CostCalculator.CalculationRecordWrapper result = costCalculator.calculateCost(mockedHousehold, validElectricityCost);

        double delta = 0.0001;
        assertEquals(0L, result.totalCostRecord().yearlyCorrectedConsumptionInWattSeconds(),
                "The total power consumption should be 0 Ws");
        assertEquals(0.0, result.totalCostRecord().yearlyElectricityCostInRp(), delta,
                "The total electricity cost should be 0.0 Rp");

        Mockito.verify(mockedSolarCalculator).calculateSolarPanelProduction((short) 8450, mockedSolarPanels, weatherArchivePath);

    }

    /**
     * Negativ test Nr. 1a
     * Test case for household with no devices and no solar panels.
     */
    @Test
    void householdEqualsNull() {
        CostCalculator costCalculator = new CostCalculator(mockedSolarCalculator, weatherArchivePath);
        assertThrows(NullPointerException.class, () -> costCalculator.calculateCost(null, validElectricityCost));
    }

    /**
     * Negativ test Nr. 1b
     * Test case for invalid electricity cost. (0 or negative)
     */
    @Test
    void electricityCostSmallerOrEqualThanZero() {
        CostCalculator costCalculator = new CostCalculator(mockedSolarCalculator, weatherArchivePath);

        assertThrows(IllegalArgumentException.class, () -> costCalculator.calculateCost(mockedHousehold, 0));
        assertThrows(IllegalArgumentException.class, () -> costCalculator.calculateCost(mockedHousehold, -1));
    }

    /**
     * Negativ test Nr. 1c
     * Test case for household with no devices.
     */
    @Test
    void noRegisteredDevices() {
        CostCalculator costCalculator = new CostCalculator(mockedSolarCalculator, weatherArchivePath);
        doAnswer(invocation -> new ArrayList<>()).when(mockedHousehold).getAllRooms();
        doAnswer(invocation -> new ArrayList<>()).when(mockedHousehold).getAllSolarPanels();

        assertThrows(NoRegisteredDeviceException.class, () -> costCalculator.calculateCost(mockedHousehold, validElectricityCost));
    }

    /**
     * Negativ test Nr. 1d
     * @throws WeatherArchiveException if an error occurs while getting the sun hours from the weather archive
     */
    @Test
    void noAnswerFormSolarPanelCalculator() throws WeatherArchiveException {
        SolarPanelCalculator mockedSolarCalculator = mock(SolarPanelCalculator.class);
        CostCalculator costCalculator = new CostCalculator(mockedSolarCalculator, weatherArchivePath);

        doThrow(WeatherArchiveException.class).when(mockedSolarCalculator).calculateSolarPanelProduction(Mockito.anyShort(), Mockito.anyList(), Mockito.anyString());
        doAnswer(invocation -> mockedRooms).when(mockedHousehold).getAllRooms();
        doAnswer(invocation -> mockedSolarPanels).when(mockedHousehold).getAllSolarPanels();

        setupValidMockedDevices();
        setupMockedRooms();
        setupMockedSolarPanels();


        assertThrows(WeatherArchiveException.class, () -> costCalculator.calculateCost(mockedHousehold, validElectricityCost));
    }
}
