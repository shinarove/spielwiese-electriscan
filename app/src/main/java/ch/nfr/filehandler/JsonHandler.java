package ch.nfr.filehandler;

import ch.nfr.filehandler.property.HouseholdProperty;
import ch.nfr.tablemodel.Household;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/***
 * This class is responsible for handling the JSON files.
 * JSONHandler implements the FileHandler interface.
 * It saves the data from the JSONObject every 10 seconds to the file.
 */
public class JsonHandler implements FileHandler {

    /**
     * The logger for the JsonHandler class.
     */
    private static final Logger LOGGER = Logger.getLogger(JsonHandler.class.getName());

    /**
     * The configuration object.
     */
    private Config config = new Config();

    /**
     * The JSONObject that contains the data.
     */
    private JSONObject jsonObject = new JSONObject();

    /**
     * The id of the loaded household.
     */
    private int loadedHouseholdId = 0;

    /**
     * The PropertyChangeSupport object.
     */
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * The Timer object for the autosave task.
     */
    private final Timer autosaveTimer;

    /**
     * The Map that contains the households.
     */
    private final Map<Integer, JsonRecord> households = new HashMap<>();

    /**
     * The boolean that indicates if the JSONHandler object is initialized.
     */
    private boolean isInitialized = false;

    /**
     * The List that contains the files that are marked for deletion.
     */
    private final List<String> deletedFiles = new ArrayList<>();


    /**
     * The constructor of the JSONHandler class.
     * It creates a new Timer object and schedules a new AutosaveTask.
     * The AutosaveTask saves the data to the file every 10 seconds.
     * It also adds a PropertyChangeListener to the JSONHandler object.
     * The PropertyChangeListener listens for changes in the jsonFilePath property.
     * If the jsonFilePath property changes, the old JSONObject is saved to the old file.
     */
    public JsonHandler() {
        this.autosaveTimer = new Timer();
        this.autosaveTimer.schedule(new AutosaveTask(), 0, 10000);

        this.addPropertyChangeListener(evt -> {
            if ("loadedHouseholdId".equals(evt.getPropertyName())) {
                // Delete the files that are marked for deletion
                deleteFiles();
                // Save the current JSONObject to the old file
                saveDataToFile((int) evt.getOldValue());
                try {
                    // Load the new JSONObject from the new file
                    loadJsonObject((int) evt.getNewValue());
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * This method is called when the JSONHandler object is destroyed.
     * It cancels the Timer object.
     */
    public void tearDown() {
        saveDataToFile(loadedHouseholdId);
        autosaveTimer.cancel();
    }

    /**
     * This method exports the household to the given file path.
     * It copies the file to the given file path.
     *
     * @param absolutePath The absolute path of the file.
     */
    @Override
    public void exportHousehold(String absolutePath) {
        try {
            if (absolutePath.equals(config.getJsonFolder().getPath())) {
                LOGGER.warning("The file cannot be exported to the same folder.");
            } else {
                saveDataToFile(loadedHouseholdId);
                Path source = Path.of(households.get(loadedHouseholdId).getFileName());
                Path newFolder = Path.of(absolutePath);
                Files.copy(source, newFolder.resolve(source.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            LOGGER.severe("Error while exporting the household: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * This method imports the household from the given file path.
     * It reads the file with the given file path and creates a new JSONObject.
     * It creates a new JsonRecord object and puts it in the households Map.
     *
     * @param absolutePath The absolute path of the file.
     * @throws RuntimeException if the file is not loadable or an error occurred while importing the household.
     */
    @Override
    public void importHousehold(String absolutePath) {
        Path source = Path.of(absolutePath);
        Path newFile = Path.of(config.getJsonFolder().getPath()).resolve(source.getFileName());
        try {
            Files.copy(source, newFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOGGER.severe("Error while importing the household: " + e.getMessage());
            throw new RuntimeException(e);
        }
        try {
            JSONObject jsonObject = new JSONObject(new JSONTokener(new FileReader(newFile.toString())));
            if (jsonObject.has("delete")) jsonObject.remove("delete");
            jsonObject.put("delete", true);
            FileWriter fileWriter = new FileWriter(newFile.toString(), StandardCharsets.UTF_8);
            fileWriter.write(jsonObject.toString());
            fileWriter.close();
            JsonRecord jsonRecord = new JsonRecord(newFile.toString(), jsonObject.getString(HouseholdProperty.HOUSEHOLD_NAME.name()), households.size() + 1);
            jsonRecord.load(); //try to load
            jsonObject.put("delete", false);
            fileWriter = new FileWriter(newFile.toString());
            fileWriter.write(jsonObject.toString());
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("The file could load.");
    }

    /**
     * This method loads the data from the file.
     * It reads the file with the given file path and creates a new JSONObject.
     * It differs from the Method {@link #loadHouseholds()} because it loads the data from the file.
     */
    @Override
    public Map<Integer, String> getFilesInFolder() {
        LOGGER.info("getFilesInFolder");
        setLoadedHouseholdId(loadedHouseholdId);
        loadHouseholds();
        isInitialized = true;
        return households.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getHouseholdName()));
    }


    /**
     * This method loads the households from the JSON files.
     * It reads the file with the given file path and creates a new JSONObject.
     * It goes through all the files in the json folder and searches for the name and id of the household.
     * Furthermore, it puts the name and id in a Map.
     * It differs from the Method {@link #sortHouseholds()} because it loads the households from the JSON files and searches the folder.
     */
    private void loadHouseholds() {
        //scan all the file in the file path
        List<String> jsonFiles = getJsonFiles();
        Integer id = 1;

        for (String file : jsonFiles) {
            try {
                try {
                    new JSONObject(new JSONTokener(new FileReader(file)));
                } catch (FileNotFoundException | JSONException e) {
                    LOGGER.warning("Error while loading the JSON file: " + file);
                    continue;
                }
                JSONObject jsonObject = new JSONObject(new JSONTokener(new FileReader(file)));
                if (jsonObject.has("delete")) {
                    if (jsonObject.get("delete").equals(true)) {
                        if (!isInitialized) deletedFiles.add(file);
                        continue;
                    }
                }
                if (hasJsonHouseholdProperties(jsonObject)) {
                    String name = jsonObject.getString(HouseholdProperty.HOUSEHOLD_NAME.name());
                    this.households.put(id, new JsonRecord(file, name, id));
                    FileWriter fileWriter = new FileWriter(file);
                    if (jsonObject.has("id")) jsonObject.remove("id");
                    fileWriter.write(jsonObject.put("id", id).toString());
                    fileWriter.close();
                    id++;
                }
            } catch (IOException e) {
                LOGGER.severe("Error while writing the id in the JSON file: " + file);
            }
        }
    }


    /**
     * This method goes through all the files in the file path and searches for the name and id of the household.
     * It puts the file path in a List and returns it.
     * The folder with the JSON files is defined in the {@link Config} class.
     */
    private List<String> getJsonFiles() {
        LOGGER.info("getJsonFiles: " + config.getJsonFolder().getPath());
        try {
            return Files.walk(Path.of(config.getJsonFolder().getPath()))
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .filter(f -> f.endsWith(".json"))
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            LOGGER.severe("Error while getting all JSON files from the folder: " + config.getJsonFolder().getPath());
            return Collections.emptyList();
        }
    }

    /**
     * This inner class is responsible for saving the data to the file.
     * It extends the TimerTask class and overrides the run method.
     * This class is scheduled by the Timer object in the constructor of the {@link JsonHandler} class.
     */
    private class AutosaveTask extends TimerTask {
        @Override
        public void run() {
            saveDataToFile(loadedHouseholdId);
        }
    }

    /**
     * This method saves the data to the file.
     * It writes the JSONObject to the file.
     *
     * @param householdId The id of the household should be saved.
     */
    private void saveDataToFile(int householdId) {
        if (householdId != 0) {
            try {
                FileWriter fileWriter = new FileWriter(households.get(householdId).getFileName(), StandardCharsets.UTF_8);
                fileWriter.write(jsonObject.toString());
                fileWriter.close();
            } catch (IOException e) {
                LOGGER.severe("An error occurred while saving the data to the file: " + households.get(householdId).getFileName());

            }
        } else {
            LOGGER.info("No file path is set");
            System.out.println("No file path is set");
        }
    }

    /**
     * This method switches the file to the file with the given name.
     * It sets the jsonFilePath property to the new file path.
     *
     * @param householdId The id of the household should be switched to.
     */
    @Override
    public Household switchHousehold(int householdId) {
        sortHouseholds();
        households.get(householdId).load();
        setLoadedHouseholdId(householdId);
        return households.get(householdId).getHousehold();
    }

    /**
     * This method creates a new household.
     * It creates a new file with the current date and time as the file name.
     * It writes a dummy household to the file.
     * Then it creates a new JsonRecord object and puts it in the households Map.
     */
    @Override
    public void createHousehold() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        StringBuilder fileName = new StringBuilder(now.format(formatter));
        String fileExtension = ".json";
        String filePath = config.getJsonFolder().getPath() + File.separator + fileName + fileExtension;
        int counter = 1;
        int recordId = households.size() + 1;
        while (Files.exists(Path.of(filePath))) {
            fileName.append(counter);
            filePath = config.getJsonFolder().getPath() + File.separator + fileName + fileExtension;
        }

        File file = new File(filePath);
        try {
            if (file.createNewFile()) {
                LOGGER.info("File created: " + file.getName());
                Household dummyHousehold = new Household();
                FileWriter fileWriter = writeDummyHousehold(file, dummyHousehold);
                fileWriter.close();
                JsonRecord jsonRecord = new JsonRecord(filePath, dummyHousehold.getName(), recordId);
                households.put(recordId, jsonRecord);
                sortHouseholds();
                setLoadedHouseholdId(0);
            } else {
                LOGGER.severe("File already exists." + file.getName());
                throw new FileAlreadyExistsException("File already exists." + file.getName());
            }
        } catch (IOException e) {
            LOGGER.warning("An error occurred while creating the file: " + file.getName());
            throw new RuntimeException("An error occurred while creating the file: " + file.getName());
        }
    }

    /**
     * This method writes a dummy household to the file.
     * It creates a new JSONObject and puts the properties of the household in it.
     * Then it writes the JSONObject to the file.
     *
     * @param file           The file where the data should be written to.
     * @param dummyHousehold The dummy household should be written to the file.
     */
    private FileWriter writeDummyHousehold(File file, Household dummyHousehold) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(HouseholdProperty.HOUSEHOLD_NAME.name(), dummyHousehold.getName());
        jsonObject.put(HouseholdProperty.POSTAL_CODE.name(), dummyHousehold.getPostalCode());
        jsonObject.put(HouseholdProperty.NUMBER_OF_RESIDENTS.name(), dummyHousehold.getNumberOfResidents());
        jsonObject.put("rooms", new JSONArray());
        jsonObject.put("solarPanels", new JSONArray());
        jsonObject.put("id", households.size() + 1);
        fileWriter.write(jsonObject.toString());
        return fileWriter;
    }

    /**
     * This method flags the file for deletion.
     * It sets a new property in the JSONObject to mark the file for deletion.
     * Then it will be removed from the list of households.
     *
     * @param householdId The id of the household should be deleted.
     */
    @Override
    public void deleteHousehold(int householdId) {
        if (householdId > 0 && householdId <= households.size()) {
            switchHousehold(householdId);
            setLoadedHouseholdId(householdId);
            if (jsonObject.has("delete")) jsonObject.remove("delete");
            jsonObject.put("delete", true);
            setLoadedHouseholdId(0);
            households.remove(householdId);
            sortHouseholds();
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * This method sorts the households by the file name alphabetically.
     * It reads the file with the given file path and creates a new JSONObject.
     * It differs from the Method {@link #loadHouseholds()} because it sorts the Map wit JsonRecords.
     */
    private void sortHouseholds() {
        List<JsonRecord> sortedHouseholds = households.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.comparing(JsonRecord::getFileName)))
                .map(Map.Entry::getValue)
                .toList();
        households.clear();
        for (int i = 0; i < sortedHouseholds.size(); i++) {
            try {
                households.put(i + 1, sortedHouseholds.get(i));
                JSONObject jsonObject = new JSONObject(new JSONTokener(new FileReader(households.get(i + 1).getFileName())));
                if (jsonObject.has("delete")) {
                    if (jsonObject.get("delete").equals(true)) continue;
                }
                FileWriter fileWriter = new FileWriter(households.get(i + 1).getFileName());
                jsonObject.put("id", i + 1);
                fileWriter.write(jsonObject.toString());
                fileWriter.close();
                households.get(i + 1).setId(i + 1);
            } catch (IOException e) {
                LOGGER.severe("Error while writing the id in the JSON file: " +
                        config.getJsonFolder().getPath() + File.separator + households.get(i + 1).getFileName());
            }
        }
    }

    /**
     * This method loads the JSONObject from the file.
     * It reads the file with the given file path and creates a new JSONObject.
     *
     * @param householdId The id of the household should be loaded.
     */
    private void loadJsonObject(int householdId) throws FileNotFoundException {
        if (householdId != 0) {
            jsonObject = households.get(householdId).getJsonObject();
        } else {
            LOGGER.info("No file path is set");
            jsonObject = new JSONObject();
        }
    }

    /**
     * This method deletes the files that are marked for deletion.
     */
    private void deleteFiles() {
        deletedFiles.forEach(file -> {
            try {
                Files.delete(Path.of(file));
            } catch (IOException e) {
                LOGGER.severe("Error while deleting the file: " + file + e.getMessage());
            }
        });
    }

    /**
     * This method checks if the JSONObject has all the properties of the household.
     *
     * @param jsonObject The JSONObject that should be checked.
     * @return True if the JSONObject has all the properties, false if not.
     */
    private boolean hasJsonHouseholdProperties(JSONObject jsonObject) {
        for (HouseholdProperty property : HouseholdProperty.values()) {
            if (!jsonObject.has(property.name())) {
                LOGGER.severe("Error while loading the JSON file: Property: " + property.name() + " is missing");
                return false;
            }
        }
        return true;
    }

    /**
     * This sets the configuration object.
     *
     * @param config The configuration object.
     */
    public void setConfig(Config config) {
        this.config = config;
    }

    /**
     * This method sets the id of the loaded household.
     * It fires a PropertyChangeEvent.
     *
     * @param loadedHouseholdId The id of the loaded household.
     */
    private void setLoadedHouseholdId(int loadedHouseholdId) {
        int oldValue = this.loadedHouseholdId;
        this.loadedHouseholdId = loadedHouseholdId;
        this.propertyChangeSupport.firePropertyChange("loadedHouseholdId", oldValue, loadedHouseholdId);
    }

    /**
     * This method adds a PropertyChangeListener to the JSONHandler object.
     *
     * @param listener The PropertyChangeListener that should be added.
     */
    private void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

}