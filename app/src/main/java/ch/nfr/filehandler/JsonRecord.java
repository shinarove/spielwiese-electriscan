package ch.nfr.filehandler;

import ch.nfr.filehandler.converter.HouseholdConverter;
import ch.nfr.tablemodel.Household;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * This class represents a household record. It contains the household name, the file name and the household object.
 * It also contains the id of the household record.
 */
public class JsonRecord {

    /**
     * The logger for the JsonRecord class.
     */
    private static final Logger LOGGER = Logger.getLogger(JsonRecord.class.getName());

    /**
     * The name of the household.
     */
    private final String householdName;

    /**
     * The name of the file.
     */
    private final String fileName;

    /**
     * The id of the household record.
     */
    private int id;

    /**
     * The household object.
     */
    private Household household;

    /**
     * The household converter.
     */
    private final HouseholdConverter householdConverter = new HouseholdConverter();

    /**
     * Constructor for the household record
     *
     * @param fileName      the name of the file
     * @param householdName the name of the household
     * @param id            the id of the household
     */
    public JsonRecord(String fileName, String householdName, int id) {
        this.fileName = fileName;
        this.householdName = householdName;
        this.id = id;
    }

    /**
     * This method loads data from the file and saves it in the household object, and the json object.
     * If the household object is already loaded, it will not load it again.
     */
    public void load() {
        if (household == null) {
            try {
                LOGGER.info("Loading household from file: " + fileName);
                JSONObject jsonHousehold = new JSONObject(new JSONTokener(new FileReader(fileName, StandardCharsets.UTF_8)));
                this.household = householdConverter.readJson(jsonHousehold);
                this.id = jsonHousehold.getInt("id");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            LOGGER.info("Household already loaded");
        }
    }

    /**
     * This method sets the id of the JsonRecord.
     *
     * @param id the id of the JsonRecord
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * This method returns the id of the JsonRecord.
     *
     * @return the id of the JsonRecord
     */
    public int getId() {
        return id;
    }

    /**
     * This method returns the name of the household.
     *
     * @return the name of the household
     */
    public String getHouseholdName() {
        return householdName;
    }

    /**
     * This method returns the name of the file.
     *
     * @return the name of the file
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * This method returns the json object of the household.
     * It uses the householdConverter to convert the household object to a json object.
     * The json object will be updated in the converter.
     *
     * @return the json object of the household
     */
    public JSONObject getJsonObject() {
        return householdConverter.toJson();
    }

    /**
     * This method returns the household object.
     *
     * @return the household object
     */
    public Household getHousehold() {
        return household;
    }
}
