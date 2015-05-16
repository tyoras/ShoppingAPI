/**
 * 
 */
package yoan.shopping.infra.config.api.repository.properties;

import static yoan.shopping.infra.logging.Markers.CONFIG;
import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.util.error.CommonErrorCode.APPLICATION_ERROR;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import yoan.shopping.infra.config.api.Config;
import yoan.shopping.infra.config.api.repository.ConfigRepository;
import yoan.shopping.infra.util.error.ApplicationException;

/**
 * Properties file based implementation of the API config repository
 * @author yoan
 */
@Singleton
public class ConfigPropertiesRepository extends ConfigRepository {
	/** Properties file name located in src/main/resources */
	private static final String DEFAULT_CONFIG_PROPERTIES_FILE_NAME = "/config/defaultConfigAPI.properties";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigPropertiesRepository.class);
	
	@Override
	protected Config readConfig(String configLocation) {
		Properties properties = new Properties();
		
		try (InputStream configFileStream = getInputStreamOnConfFile(configLocation)){
			properties.load(configFileStream);
		} catch(IOException ioe) {
			String message = "Problem While loading " + getConfigLocationName(configLocation) + " config properties file.";
			LOGGER.error(CONFIG.getMarker(), message, ioe);
			throw new ApplicationException(ERROR, APPLICATION_ERROR, message, ioe);
		}
		
		return ConfigPropertiesConverter.fromProperties(properties);
	}

	private InputStream getInputStreamOnConfFile(String configLocation) {
		InputStream configFileStream;
		if (StringUtils.isBlank(configLocation)) {
			configFileStream = getInputStreamFromDefaultConfig();
		} else {
			configFileStream = getInputStreamFromFileSystem(configLocation);
		}
		LOGGER.info(CONFIG.getMarker(), "Loaded config file is " + getConfigLocationName(configLocation));
		return configFileStream;
	}
	
	private InputStream getInputStreamFromDefaultConfig() {
		ClassLoader currentClassLoader = ConfigPropertiesRepository.class.getClassLoader();
		return currentClassLoader.getResourceAsStream(DEFAULT_CONFIG_PROPERTIES_FILE_NAME);
	}
	
	private InputStream getInputStreamFromFileSystem(String configLocation) {
		Path configPath = getConfigPath(configLocation);
		ensureConfigFileExists(configPath);
		InputStream configFileStream;
		try {
			configFileStream = Files.newInputStream(configPath);
		} catch(IOException ioe) {
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
		} catch(InvalidPathException e) {
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
	
	private String getConfigLocationName(String configLocation) {
		if (StringUtils.isBlank(configLocation)) {
			return "Default (src/mai/resources/" + DEFAULT_CONFIG_PROPERTIES_FILE_NAME + ")";
		}
		return "Env defined (" + CONFIG_LOCATION_ENV_VARIABLE + " : " + configLocation +")";
	}
}
