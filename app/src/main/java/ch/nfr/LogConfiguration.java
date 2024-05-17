package ch.nfr;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LogConfiguration {
    private static final String DEFAULT_LOG_FILE_NAME = "src/log.properties";
    private static final Logger logger = Logger.getLogger(LogConfiguration.class.getCanonicalName());

    static {
        String logConfigFile = System.getProperty("java.util.logging.config.file", DEFAULT_LOG_FILE_NAME);
        Path logConfigPath = Path.of(logConfigFile);
        try {
            InputStream configFileStream;
            if (Files.isReadable(logConfigPath)) {
                configFileStream = Files.newInputStream(logConfigPath);
                LogManager.getLogManager().readConfiguration(configFileStream);
                logger.log(Level.FINE, "Log configuration read from {0}", logConfigFile);
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error loading log configuration", e);
        }
    }
}
