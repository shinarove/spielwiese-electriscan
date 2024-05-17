package ch.zhaw.it23a.pm2.calculatorAndConverter;

import ch.zhaw.it23a.pm2.calculatorAndConverter.units.EnergyUnit;
import ch.zhaw.it23a.pm2.filehandler.WeatherArchiveException;
import ch.zhaw.it23a.pm2.tablemodel.Household;
import ch.zhaw.it23a.pm2.tablemodel.Room;
import ch.zhaw.it23a.pm2.tablemodel.device.Device;
import ch.zhaw.it23a.pm2.tablemodel.device.DeviceCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * The CostCalculator class is responsible for calculating the yearly electricity cost of a household.
 */
public class CostCalculator {
    /** The used Logger in this class. */
    private final Logger logger = Logger.getLogger(CostCalculator.class.getName());
    /** The SolarPanelCalculator instance used to calculate the solar panel production. */
    private final SolarPanelCalculator solarPanelCalculator;
    /** The electricity cost in Rp per kWh. */
    private double electricityCostInRpPerkWh;
    /** The household to calculate the cost for. */
    private Household household;
    /** The used weather archive path */
    private final String weatherArchivePath;

    /**
     * Creates a new CostCalculator with the given SolarPanelCalculator.
     * @param solarPanelCalculator the SolarPanelCalculator instance
     * @param weatherArchivePath the path to the weather archive
     */
    public CostCalculator(SolarPanelCalculator solarPanelCalculator, String weatherArchivePath) {
        this.solarPanelCalculator = Objects.requireNonNull(solarPanelCalculator);
        this.weatherArchivePath = Objects.requireNonNull(weatherArchivePath);
    }

    /**
     * Calculates the yearly electricity cost for the given household and electricity cost in Rp per kWh.
     * It uses an {@link ExecutorService} with fixed thread pool size of 4, to perform the calculations concurrently.
     *
     * <p>The method performs the following steps:</p>
     * <ol>
     *   <li>Calculates the cost for each device category in the household by calling the {@link #calculateDeviceCategoryCosts(ExecutorService)} method.</li>
     *   <li>Calculates the cost for each room in the household by calling the {@link #calculateRoomCosts(ExecutorService)} method.</li>
     *   <li>If the household has any solar panels, calculates the solar panel production using the {@link SolarPanelCalculator}.</li>
     *   <li>Calculates the total power produced by the solar panels.</li>
     *   <li>Calculates the total cost by calling the {@link #calculateTotalCost(List, long)} method,
     *   passing in the list of {@link RoomCalculationRecord} and the total power produced by the solar panels.</li>
     * </ol>
     *
     * @param household the household to calculate the cost for, if null throws an {@link NullPointerException}
     * @param electricityCostInRpPerkWh the electricity cost in Rp per kWh, if less than or equal to 0 throws an {@link IllegalArgumentException}
     * @return a {@link CalculationRecordWrapper} containing the {@link DeviceCalculationRecord}, {@link RoomCalculationRecord},
     * {@link SolarPanelCalculator.TotalSolarCalculationWrapper}, and the {@link TotalCostRecord}
     * @throws NoRegisteredDeviceException if no devices are registered in the household
     */
    public CalculationRecordWrapper calculateCost(Household household, double electricityCostInRpPerkWh) throws NoRegisteredDeviceException, WeatherArchiveException{
        this.household = Objects.requireNonNull(household);
        if (electricityCostInRpPerkWh <= 0) {
            throw new IllegalArgumentException("Electricity cost must be greater than 0");
        }

        this.electricityCostInRpPerkWh = electricityCostInRpPerkWh;

        ExecutorService executorService = Executors.newFixedThreadPool(4);

        List<DeviceCalculationRecord> resultDevices = calculateDeviceCategoryCosts(executorService);
        List<RoomCalculationRecord> resultRooms = calculateRoomCosts(executorService);
        executorService.shutdown();

        SolarPanelCalculator.TotalSolarCalculationWrapper solarCalculationRecord = null;

        if (resultDevices.isEmpty() || resultRooms.isEmpty()) {
            throw new NoRegisteredDeviceException("No registered devices in household");
        }

        if (!household.getAllSolarPanels().isEmpty()) {
            logger.info("Calculating solar panel production");
            solarCalculationRecord = solarPanelCalculator
                    .calculateSolarPanelProduction(household.getPostalCode(), household.getAllSolarPanels(), weatherArchivePath);
        }

        double totalYearlyProduction;
        if (solarCalculationRecord == null) {
            totalYearlyProduction = 0.0;
            solarCalculationRecord = new SolarPanelCalculator.TotalSolarCalculationWrapper(0.0, new ArrayList<>());
        } else {
            totalYearlyProduction = solarCalculationRecord.totalYearlyProduction();
        }
        long producedPowerInWattSeconds = UnitConverter.convertEnergyToWattSeconds(totalYearlyProduction, EnergyUnit.KILOWATT_HOUR);
        TotalCostRecord totalCostRecord = calculateTotalCost(resultRooms, producedPowerInWattSeconds);

        return new CalculationRecordWrapper(resultDevices, resultRooms, solarCalculationRecord, totalCostRecord);
    }

    /**
     * Calculates the yearly electricity cost for each {@link DeviceCategory} in the household.
     *
     * @param executorService the ExecutorService to use for concurrent execution
     * @return a list of {@link DeviceCalculationRecord} for each {@link DeviceCategory} in the household
     */
    private List<DeviceCalculationRecord> calculateDeviceCategoryCosts(ExecutorService executorService) {
        CountDownLatch latch = new CountDownLatch(DeviceCategory.values().length);
        List<DeviceCalculationRecord> deviceCalculationRecords = new ArrayList<>();

        for (DeviceCategory category : DeviceCategory.values()) {
            executorService.execute(() -> {
                long totalPowerConsumptionInWattSeconds = 0;
                double totalElectricityCostInRp = 0.0;
                for (Room room : household.getAllRooms()){
                    for (Device device : room.getAllDevices()){
                        if (device.getCategory().equals(category)){
                            totalPowerConsumptionInWattSeconds += device.getYearlyConsumptionInWattSeconds();
                            totalElectricityCostInRp += (device.getYearlyConsumptionInWattSeconds() * electricityCostInRpPerkWh) / (1000 * 3600);
                        }
                    }
                }
                if (totalPowerConsumptionInWattSeconds > 0 || totalElectricityCostInRp > 0) {
                    logger.info("Adding device category result for " + category);
                    synchronized (deviceCalculationRecords) {
                        deviceCalculationRecords.add(new DeviceCalculationRecord(category,
                                totalPowerConsumptionInWattSeconds,
                                totalElectricityCostInRp));
                    }
                }
                latch.countDown();
            });
        }
        try {
            latch.await(); // Wait for all tasks to complete
        } catch (InterruptedException e) {
            logger.warning("Interrupted while waiting for tasks to complete");
        }
        return deviceCalculationRecords;
    }

    /**
     * Calculates the yearly electricity cost for each room in the household.
     *
     * @param executorService the ExecutorService to use for concurrent execution
     * @return a list of {@link RoomCalculationRecord} for each room in the household
     */
    private List<RoomCalculationRecord> calculateRoomCosts(ExecutorService executorService) {
        CountDownLatch latch = new CountDownLatch(household.getAllRooms().size());
        List<RoomCalculationRecord> roomCalculationRecords = new ArrayList<>();

        for (Room room : household.getAllRooms()) {
            executorService.execute(() -> {
                long powerConsumptionInWattSeconds = 0;
                double electricityCostInRp = 0.0;
                for (Device device : room.getAllDevices()) {
                    powerConsumptionInWattSeconds += device.getYearlyConsumptionInWattSeconds();
                    electricityCostInRp += (device.getYearlyConsumptionInWattSeconds() * electricityCostInRpPerkWh) / (1000 * 3600);
                }
                if (powerConsumptionInWattSeconds > 0 || electricityCostInRp > 0) {
                    logger.info("Adding room result for " + room.getName());
                    synchronized (roomCalculationRecords) {
                        roomCalculationRecords.add(new RoomCalculationRecord(room,
                                powerConsumptionInWattSeconds,
                                electricityCostInRp));
                    }
                }
                latch.countDown();
            });
        }

        try {
            latch.await(); // Wait for all tasks to complete
        } catch (InterruptedException e) {
            logger.warning("Interrupted while waiting for tasks to complete");
        }
        return roomCalculationRecords;
    }

    /**
     * Calculates the total power consumption and electricity cost for the household.
     *
     * @param roomResults the list of {@link RoomCalculationRecord} for each room in the household
     * @param producedPowerInWattSeconds the total power produced by the solar panels
     * @return a {@link TotalCostRecord} containing the total power consumption and electricity cost
     */
    private TotalCostRecord calculateTotalCost(List<RoomCalculationRecord> roomResults, long producedPowerInWattSeconds) {
        long yearlyConsumptionInWattSeconds = 0;
        long yearlyCorrectedConsumptionInWattSeconds = -producedPowerInWattSeconds;

        for (RoomCalculationRecord roomCalculationRecord : roomResults) {
            yearlyCorrectedConsumptionInWattSeconds += roomCalculationRecord.powerConsumptionInWattSeconds();
            yearlyConsumptionInWattSeconds += roomCalculationRecord.powerConsumptionInWattSeconds();
        }

        if (yearlyCorrectedConsumptionInWattSeconds < 0) {
            yearlyCorrectedConsumptionInWattSeconds = 0;
        }
        double totalElectricityCostInRp = (yearlyCorrectedConsumptionInWattSeconds * electricityCostInRpPerkWh) / (1000 * 3600);
        return new TotalCostRecord(yearlyCorrectedConsumptionInWattSeconds, yearlyConsumptionInWattSeconds, producedPowerInWattSeconds, totalElectricityCostInRp, electricityCostInRpPerkWh);
    }

    /**
     * The CalculationRecordWrapper class is a wrapper class for the calculation results.
     * @param deviceCalculationRecords the list of {@link DeviceCalculationRecord}
     * @param roomCalculationRecords the list of {@link RoomCalculationRecord}
     * @param totalSolarCalculationRecord the {@link SolarPanelCalculator.TotalSolarCalculationWrapper}
     * @param totalCostRecord the {@link TotalCostRecord}
     */
    public record CalculationRecordWrapper(List<DeviceCalculationRecord> deviceCalculationRecords,
                                           List<RoomCalculationRecord> roomCalculationRecords,
                                           SolarPanelCalculator.TotalSolarCalculationWrapper totalSolarCalculationRecord,
                                           TotalCostRecord totalCostRecord) {
    }

    /**
     * This record class represents the calculation results for a device category.
     *
     * @param deviceCategory the device category
     * @param powerConsumptionInWattSeconds the power consumption in WattSeconds
     * @param electricityCostInRp the electricity cost in Rp
     */
    public record DeviceCalculationRecord(DeviceCategory deviceCategory,
                                          long powerConsumptionInWattSeconds,
                                          double electricityCostInRp) {
    }

    /**
     * This record class represents the calculation results for a room.
     *
     * @param room the room
     * @param powerConsumptionInWattSeconds the power consumption in WattSeconds
     * @param electricityCostInRp the electricity cost in Rp
     */
    public record RoomCalculationRecord(Room room, long powerConsumptionInWattSeconds,
                                        double electricityCostInRp) {
    }

    /**
     * This record class represents the total power consumption and electricity cost for the household.
     *
     * @param yearlyCorrectedConsumptionInWattSeconds corrected yearly consumption in WattSeconds
     * @param yearlyConsumptionInWattSeconds yearly consumption in WattSeconds
     * @param yearlyProductionInWattSeconds yearly production in WattSeconds
     * @param yearlyElectricityCostInRp yearly electricity cost in Rp
     * @param electricityCostInRpPerkWh electricity cost in Rp per kWh
     */
    public record TotalCostRecord(long yearlyCorrectedConsumptionInWattSeconds,
                                  long yearlyConsumptionInWattSeconds,
                                  long yearlyProductionInWattSeconds,
                                  double yearlyElectricityCostInRp,
                                  double electricityCostInRpPerkWh) {
    }
}
