package ch.nfr.filehandler;


import ch.nfr.calculator.units.EnergyUnit;
import ch.nfr.calculator.units.TimeUnit;
import ch.nfr.tablemodel.RoomType;
import ch.nfr.tablemodel.device.DeviceCategory;
import ch.nfr.tablemodel.device.ElectricConsumption;
import ch.nfr.tablemodel.records.DeviceRecord;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class tests the DeviceProposalHandler class.

 */
public class DeviceProposalHandlerTest {

    /**
     * Tests the readDeviceProposalFile method of the DeviceProposalHandler class.
     */
    @Test
    void readDeviceProposalFile() {
        String propertiesFile = "src/test/resources/testDeviceProposal.properties";
        List<DeviceRecord> deviceRecords;
        deviceRecords = DeviceProposalHandler.readDeviceProposalFile(propertiesFile, RoomType.LIVING_ROOM);
        assertEquals(2, deviceRecords.size(), "Expected 2 devices in the living room");

        DeviceRecord device;
        if (deviceRecords.getFirst().deviceName().equals("Fernseher")) {
            device = deviceRecords.getFirst();
        } else {
            device = deviceRecords.getLast();
        }

        assertEquals("Fernseher", device.deviceName(),
                "Expected name of the first device to be 'Fernseher'");
        assertEquals(DeviceCategory.ENTERTAINMENT, device.deviceCategory(),
                "Expected category of the first device to be 'ENTERTAINMENT'");
        assertInstanceOf(ElectricConsumption.class, device.consumption(),
                "Expected consumption of the first device to be an instance of ElectricConsumption");
        ElectricConsumption firstConsumption = (ElectricConsumption) device.consumption();
        assertEquals(360_000, firstConsumption.getPowerConsumptionInWattSeconds(), 0.001,
                "Expected PowerConsumption of the first device to be 360'000 WattSeconds");
        assertEquals(EnergyUnit.WATT_HOUR, firstConsumption.getUsedEnergyUnit(),
                "Expected EnergyUnit of the first device to be WATT_HOUR");
        assertEquals(2_277_600, firstConsumption.getYearlyUsageInSeconds(),
                "Expected yearly usage of the first device to be 2'277'600 seconds");
        assertEquals(TimeUnit.MINUTE, firstConsumption.getUsedTimeUnit(),
                "Expected TimeUnit of the first device to be MINUTE");
        assertEquals(TimeUnit.DAY, firstConsumption.getUsedTimeUnitPer(),
                "Expected per TimeUnit of the first device to be DAY");
    }

    /**
     * Tests the readDeviceProposalFile method of the DeviceProposalHandler class with the default device proposal file.
     */
    @Test
    void testDeviceProposalFile() {
        String propertiesFile = "src/main/resources/default-devices/deviceProposal.properties";
        for (RoomType roomType : RoomType.values()) {
            if (roomType != RoomType.DUMMY) {
                List<DeviceRecord> deviceRecords = DeviceProposalHandler.readDeviceProposalFile(propertiesFile, roomType);
                assertNotNull(deviceRecords, "Expected a list of device records");
                assertFalse(deviceRecords.isEmpty(), "Expected the list of device records not to be empty in " + roomType);
            }
        }
    }
}
