package ch.nfr.filehandler;

import java.io.File;
import java.util.Objects;

/**
 * This class represents the configuration.
 * It contains the folder where the JSON files are stored.
 */
public class Config {

    /**
     * This method returns the folder where the JSON files are stored.
     *
     * @return the folder where the JSON files are stored
     */
    public File getJsonFolder() {
        return checkExistingFolderOrThrow("../json");
    }

    /**
     * This method checks if the folder exists and is a folder.
     * If the folder does not exist or is not a folder, an IllegalArgumentException is thrown.
     *
     * @param directoryPath the path of the folder
     * @return the folder
     */
    private File checkExistingFolderOrThrow(String directoryPath) {
        Objects.requireNonNull(directoryPath, "Directory must not be null");
        File folder = new File(directoryPath);
        if (!folder.exists()) {
            throw new IllegalArgumentException("Folder does not exist: " + directoryPath);
        }
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("Not a folder: " + directoryPath);
        }
        return folder;
    }
}
