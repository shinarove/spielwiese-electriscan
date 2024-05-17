package ch.nfr.filehandler;

import ch.nfr.tablemodel.Household;

import java.io.FileNotFoundException;
import java.util.Map;

/**
 * This interface represents the file handler.
 * It contains methods to get files in a folder, switch to a household, create a household and delete a household.
 */
public interface FileHandler {

    /**
     * This method returns all Household files in the folder.
     *
     * @return a map with the id of the household and the name of the household
     */
    Map<Integer, String> getFilesInFolder();

    /**
     * This method switches to a household.
     *
     * @param householdId the id of the household.
     * @throws FileNotFoundException if the file is not found.
     */
    Household switchHousehold(int householdId) throws FileNotFoundException;

    /**
     * This method creates a household.
     */
    void createHousehold();

    /**
     * This method deletes a household.
     *
     * @param id the id of the household.
     */
    void deleteHousehold(int id);

    /**
     * This method shuts down the AutoSaveTask.
     */
    void tearDown();

    /**
     * This method exports a household.
     *
     * @param absolutePath the absolute path of the file.
     */
    void exportHousehold(String absolutePath);

    /**
     * This method imports a household.
     *
     * @param absolutePath the absolute path of the file.
     */
    void importHousehold(String absolutePath);
}
