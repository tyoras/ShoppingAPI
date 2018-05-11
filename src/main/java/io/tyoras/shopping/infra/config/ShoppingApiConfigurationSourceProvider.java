package io.tyoras.shopping.infra.config;

import io.dropwizard.configuration.FileConfigurationSourceProvider;
import io.tyoras.shopping.infra.util.error.ApplicationException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import static io.tyoras.shopping.infra.logging.Markers.CONFIG;
import static io.tyoras.shopping.infra.rest.error.Level.ERROR;
import static io.tyoras.shopping.infra.util.error.CommonErrorCode.APPLICATION_ERROR;


public class ShoppingApiConfigurationSourceProvider extends FileConfigurationSourceProvider {
    /**
     * Name of the environment variable to find the config file
     */
    private static final String CONFIG_LOCATION_ENV_VARIABLE = "API_SHOPPING_CONFIG_FILE_PATH";

    /**
     * Yml file name located in src/main/resources
     */
    private static final String DEFAULT_CONFIG_LOCATION = "config/defaultConfigAPI.yml";

    private static final Logger LOGGER = LoggerFactory.getLogger(ShoppingApiConfigurationSourceProvider.class);

    @Override
    public InputStream open(String path) throws IOException {
        if (StringUtils.isBlank(path)) {
            return readConfig();
        }

        return super.open(path);
    }

    /**
     * Read the API configuration
     *
     * @return Config if found, else null
     */
    public InputStream readConfig() {
        String configLocation = readConfigLocationFromEnv();
        if (StringUtils.isBlank(configLocation)) {
            configLocation = DEFAULT_CONFIG_LOCATION;
        }
        LOGGER.info(CONFIG.getMarker(), "Loaded config file is " + getConfigLocationName(configLocation));
        return readConfig(configLocation);
    }

    protected String readConfigLocationFromEnv() {
        return System.getenv(CONFIG_LOCATION_ENV_VARIABLE);
    }

    protected String getConfigLocationName(String configLocation) {
        if (DEFAULT_CONFIG_LOCATION.equals(configLocation)) {
            return "Default (src/main/resources/" + DEFAULT_CONFIG_LOCATION + ")";
        }
        return "Env defined (" + CONFIG_LOCATION_ENV_VARIABLE + " : " + configLocation + ")";
    }

    protected InputStream readConfig(String configLocation) {

        try (InputStream configFileStream = getInputStreamOnConfFile(configLocation)) {
            return configFileStream;
        } catch (IOException ioe) {
            String message = "Problem While loading " + getConfigLocationName(configLocation) + " config properties file.";
            LOGGER.error(CONFIG.getMarker(), message, ioe);
            throw new ApplicationException(ERROR, APPLICATION_ERROR, message, ioe);
        }
    }

    private InputStream getInputStreamOnConfFile(String configLocation) {
        if (DEFAULT_CONFIG_LOCATION.equals(configLocation)) {
            return getInputStreamFromDefaultConfig();
        }
        return getInputStreamFromFileSystem(configLocation);
    }

    private InputStream getInputStreamFromDefaultConfig() {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        return currentClassLoader.getResourceAsStream(DEFAULT_CONFIG_LOCATION);
    }

    private InputStream getInputStreamFromFileSystem(String configLocation) {
        Path configPath = getConfigPath(configLocation);
        ensureConfigFileExists(configPath);
        InputStream configFileStream;
        try {
            configFileStream = Files.newInputStream(configPath);
        } catch (IOException ioe) {
            String message = "Problem while openning stream on path found in \"" + CONFIG_LOCATION_ENV_VARIABLE + "\" env variable : " + configLocation;
            LOGGER.error(CONFIG.getMarker(), message, ioe);
            throw new ApplicationException(ERROR, APPLICATION_ERROR, message, ioe);
        }
        return configFileStream;
    }

    private Path getConfigPath(String configLocation) {
        Path configPath;
        try {
            configPath = FileSystems.getDefault().getPath(configLocation);
        } catch (InvalidPathException e) {
            String message = "Invalid path found in \"" + CONFIG_LOCATION_ENV_VARIABLE + "\" env variable : " + configLocation;
            LOGGER.error(CONFIG.getMarker(), message, e);
            throw new ApplicationException(ERROR, APPLICATION_ERROR, message, e);
        }
        return configPath;
    }

    private void ensureConfigFileExists(Path configPath) {
        if (Files.notExists(configPath)) {
            String message = "Unable to find a readable file at path found in \"" + CONFIG_LOCATION_ENV_VARIABLE + "\" env variable : " + configPath;
            LOGGER.error(CONFIG.getMarker(), message);
            throw new ApplicationException(ERROR, APPLICATION_ERROR, message);
        }
    }

}
