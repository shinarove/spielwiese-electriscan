package ch.nfr.filehandler;

import ch.nfr.tablemodel.Household;
import ch.nfr.tablemodel.RoomType;
import ch.nfr.tablemodel.device.DeviceCategory;
import ch.nfr.tablemodel.device.WiredDevice;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class tests the JsonRecord class.
 */
public class JsonRecordTest {
    String testFolder = "src/test/resources/households/jsonrecord/";

    /**
     * Positive test No. 1
     * Test if the method {@link JsonRecord#load()} loads a valid json and creates a household object
     * The household should be loaded correctly
     * It picks some values from the json file and checks if they are correct
     */
    @Test
    public void testLoadHousehold() {
        String fileName = "Test1.json";
        JsonRecord jsonRecord = new JsonRecord(testFolder + fileName, "Test No. 1", 1);
        jsonRecord.load();
        assertEquals("Test No. 1", jsonRecord.getHouseholdName());
        assertEquals(1, jsonRecord.getId());

        Household household = jsonRecord.getHousehold();
        assertNotNull(household);
        assertEquals("Test No. 1", household.getName());
        assertEquals(1, household.getNumberOfResidents());
        assertEquals(8400, household.getPostalCode());

        assertEquals(1, household.getAllRooms().size());
        assertEquals(0, household.getAllSolarPanels().size());

        assertNotNull(household.getRoom(1));
        assertEquals("Baden", household.getRoom(1).getName());
        assertEquals(RoomType.BATHROOM, household.getRoom(1).getRoomType());

        assertEquals(2, household.getRoom(1).getAllDevices().size());
        assertNotNull(household.getRoom(1).getAllDevices().getFirst());
        assertNotNull(household.getRoom(1).getAllDevices().get(1));
        assertEquals("Rasierer", household.getRoom(1).getAllDevices().getFirst().getName());
        assertInstanceOf(WiredDevice.class, household.getRoom(1).getAllDevices().getFirst());
        assertEquals(9000, ((WiredDevice) household.getRoom(1).getAllDevices().getFirst()).getElectricConsumption().getPowerConsumptionInWattSeconds());
        assertEquals(DeviceCategory.OTHER, household.getRoom(1).getAllDevices().get(1).getCategory());
    }

    /**
     * Positive test No. 2
     * Test if the method {@link JsonRecord#load()} ignores the not necessary properties
     * The household should be loaded correctly and load only the necessary properties
     * It picks some values from the json file and checks if they are correct
     */
    @Test
    public void testLoadHouseholdContainsMoreProperties() {
        String fileName = "Test1SomeMore.json";
        JsonRecord jsonRecord = new JsonRecord(testFolder + fileName, "Test No. 1 Some More", 1);
        jsonRecord.load();
        assertEquals("Test No. 1 Some More", jsonRecord.getHouseholdName());

        Household household = jsonRecord.getHousehold();
        assertNotNull(household);
        assertEquals("Test No. 1 Some More", household.getName());
        assertEquals(1, household.getNumberOfResidents());
        assertEquals(8400, household.getPostalCode()); // Test1SomeMore.json Line 50
        assertNotEquals(6000, household.getPostalCode()); // Test1SomeMore.json Line 4

        assertEquals("Baden", household.getRoom(1).getName());
        assertEquals(RoomType.BATHROOM, household.getRoom(1).getRoomType());
        assertEquals("Rasierer", household.getRoom(1).getAllDevices().getFirst().getName());
        assertEquals(DeviceCategory.OTHER, household.getRoom(1).getAllDevices().get(1).getCategory());
    }

    /**
     * Negative test No. 1
     * Test if the method {@link JsonRecord#load()} throws an exception if the file does not exist
     * The file does not exist
     * The method should throw an exception
     */
    @Test
    public void testLoadHouseholdFileDoesNotExist() {
        String fileName = "Test1DoesNotExist.json";
        JsonRecord jsonRecord = new JsonRecord(testFolder + fileName, "Test No. 1 Does Not Exist", 1);
        assertThrows(RuntimeException.class, jsonRecord::load);
    }

    /**
     * Negative test No. 2
     * Test if the method {@link JsonRecord#load()} throws an exception if the file is not a valid json
     * The file is not a valid json
     * The method should throw an exception
     */
    @Test
    public void testLoadHouseholdFileIsNotJson() {
        String fileName = "Test1NotJson.json";
        JsonRecord jsonRecord = new JsonRecord(testFolder + fileName, "Test No. 1 Not Json", 1);
        assertThrows(RuntimeException.class, jsonRecord::load);
    }

    /**
     * Negative test No. 3
     * Test if the method {@link JsonRecord#load()} throws an exception if the json has not the necessary properties
     * The file is not a valid json
     * The method should throw an exception
     */
    @Test
    public void testLoadHouseholdFileHasMissingProperties() {
        String fileName = "Test1MissingProperties.json";
        JsonRecord jsonRecord = new JsonRecord(testFolder + fileName, "Test No. 1 Missing", 1);
        assertThrows(RuntimeException.class, jsonRecord::load);
    }
}
