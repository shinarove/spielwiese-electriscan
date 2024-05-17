package ch.nfr.filehandler;

import ch.nfr.tablemodel.Household;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
/**
 * This class tests the JsonHandler class
 */
public class JsonHandlerTest {
    /**
     * The Config object
     */
    private static Config config;


    /**
     * Test 1.1.1
     * Nr. 1
     * Test if the method getAllHouseholds returns all households in the folder
     * The Map should contain all valid households
     * The Map should be alphabetically sorted by the file name
     */
    @Test
    public void testValidHouseholds() {
        config = mock(Config.class);
        when(config.getJsonFolder()).thenReturn(new File("src/test/resources/households/valid"));
        JsonHandler jsonHandler = new JsonHandler();
        jsonHandler.setConfig(config);
        Map<Integer, String> households = jsonHandler.getFilesInFolder();
        assertEquals(3, households.size());
        assertEquals("Test No. 3", households.get(1));
        assertEquals("Test No. 1", households.get(2));
        assertEquals("Test No. 2", households.get(3));
        jsonHandler.tearDown();
    }

    /**
     * Test 1.1.1
     * Nr. 2
     * Test if the method getAllHouseholds returns all households in the folder
     * The folder contains invalid households
     * The Map should contain all valid households
     * The Map should be alphabetically sorted by the file name
     */
    @Test
    public void testHasInvalidHouseholds() {
        config = mock(Config.class);
        when(config.getJsonFolder()).thenReturn(new File("src/test/resources/households/invalid"));
        JsonHandler jsonHandler = new JsonHandler();
        jsonHandler.setConfig(config);
        Map<Integer, String> households = jsonHandler.getFilesInFolder();
        assertEquals(3, households.size());
        assertEquals("Test No. 3", households.get(1));
        assertEquals("Test No. 1", households.get(2));
        assertEquals("Test No. 2", households.get(3));
        jsonHandler.tearDown();
    }

    /**
     * Test 1.1.1
     * Nr. 3
     * Test if the method getAllHouseholds returns all households in the folder
     * The folder contains different files
     * The Map should contain all valid households
     * The Map should be alphabetically sorted by the file name
     */
    @Test
    public void testContainsDifferentFiles() {
        config = mock(Config.class);
        when(config.getJsonFolder()).thenReturn(new File("src/test/resources/households/different"));
        JsonHandler jsonHandler = new JsonHandler();
        jsonHandler.setConfig(config);
        Map<Integer, String> households = jsonHandler.getFilesInFolder();
        assertEquals(3, households.size());
        assertEquals("Test No. 3", households.get(1));
        assertEquals("Test No. 1", households.get(2));
        assertEquals("Test No. 2", households.get(3));
        jsonHandler.tearDown();
    }

    /**
     * Test 1.1.2
     * Nr. 1
     * Test if the method switchHousehold loads the correct household
     * The folder contains valid households
     * The Id is valid
     */
    @Test
    public void testSwitchToValidHouseholds() {
        config = mock(Config.class);
        when(config.getJsonFolder()).thenReturn(new File("src/test/resources/households/valid"));
        JsonHandler jsonHandler = new JsonHandler();
        jsonHandler.setConfig(config);
        jsonHandler.getFilesInFolder();
        Household loadedHousehold = jsonHandler.switchHousehold(1);
        assertEquals("Test No. 3", loadedHousehold.getName());
        assertEquals(1001, loadedHousehold.getPostalCode());
        assertEquals(5, loadedHousehold.getNumberOfResidents());

        loadedHousehold = jsonHandler.switchHousehold(3);
        assertEquals("Test No. 2", loadedHousehold.getName());
        assertEquals(8484, loadedHousehold.getPostalCode());
        assertEquals(2, loadedHousehold.getNumberOfResidents());
        jsonHandler.tearDown();
    }


    /**
     * Test 1.1.2
     * Nr. 2
     * Test if the method switchHousehold throws an Exception if the parameter householdId is invalid.
     * The folder contains valid households
     */
    @Test
    public void testSwitchToInvalidHouseholds() {
        config = mock(Config.class);
        when(config.getJsonFolder()).thenReturn(new File("src/test/resources/households/valid"));
        JsonHandler jsonHandler = new JsonHandler();
        jsonHandler.setConfig(config);
        jsonHandler.getFilesInFolder();
        assertThrows(NullPointerException.class, () -> jsonHandler.switchHousehold(0));
        assertThrows(NullPointerException.class, () -> jsonHandler.switchHousehold(4));
        assertThrows(NullPointerException.class, () -> jsonHandler.switchHousehold(-1));
        jsonHandler.tearDown();
    }

    /**
     * Test 1.1.3
     * Nr. 1
     * Test if the method createHousehold creates a new household
     * The folder is empty
     * After creating, the folder should contain one file
     */
    @Test
    public void testCreatingHousehold() {
        config = mock(Config.class);
        when(config.getJsonFolder()).thenReturn(new File("src/test/resources/households/create"));
        JsonHandler jsonHandler = new JsonHandler();
        jsonHandler.setConfig(config);
        try {
            for (File file : Files.walk(Path.of("src/test/resources/households/create")).filter(Files::isRegularFile).map(Path::toFile).toList()) {
                file.delete();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<Integer, String> mapAtBeginning = jsonHandler.getFilesInFolder();
        assertEquals(0, mapAtBeginning.size());
        jsonHandler.createHousehold();
        Map<Integer, String> map = jsonHandler.getFilesInFolder();
        assertEquals(1, map.size());
        jsonHandler.tearDown();
    }

    /**
     * Test 1.1.4
     * Nr. 1
     * Test if the method deleteHousehold deletes a valid household
     * The folder contains valid households
     * The Id is valid
     */
    @Test
    public void testDeletingValidHousehold() {
        config = mock(Config.class);
        when(config.getJsonFolder()).thenReturn(new File("src/test/resources/households/delete"));
        JsonHandler jsonHandler = new JsonHandler();
        jsonHandler.setConfig(config);
        try {
            for (File file : Files.walk(Path.of("src/test/resources/households/delete")).filter(Files::isRegularFile).map(Path::toFile).toList()) {
                file.delete();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        jsonHandler.createHousehold();
        assertEquals(1, jsonHandler.getFilesInFolder().size());
        jsonHandler.createHousehold();
        assertEquals(2, jsonHandler.getFilesInFolder().size());
        jsonHandler.deleteHousehold(1);
        Map<Integer, String> delMap = jsonHandler.getFilesInFolder();
        assertEquals(1, delMap.size());
        jsonHandler.deleteHousehold(1);
        assertEquals(0, jsonHandler.getFilesInFolder().size());
        jsonHandler.tearDown();
    }

    /**
     * Test 1.1.4
     * Nr. 2
     * Test if the method deleteHousehold throws an Exception if the parameter householdId is invalid.
     * The folder contains valid households
     */
    @Test
    public void testDeletingInvalidHouseholds() {
        config = mock(Config.class);
        when(config.getJsonFolder()).thenReturn(new File("src/test/resources/households/deleteInvalid"));
        JsonHandler jsonHandler = new JsonHandler();
        jsonHandler.setConfig(config);
        assertEquals(2, jsonHandler.getFilesInFolder().size());
        assertThrows(IllegalArgumentException.class, () -> jsonHandler.deleteHousehold(0));
        assertThrows(IllegalArgumentException.class, () -> jsonHandler.deleteHousehold(3));
        assertThrows(IllegalArgumentException.class, () -> jsonHandler.deleteHousehold(-5));
        assertEquals(2, jsonHandler.getFilesInFolder().size());
        jsonHandler.tearDown();
    }
}
