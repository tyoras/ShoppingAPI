/**
 * 
 */
package yoan.shopping.infra.config.api.repository.properties;

import static yoan.shopping.infra.logging.Markers.CONFIG;
import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.util.error.CommonErrorCode.APPLICATION_ERROR;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yoan.shopping.infra.config.api.Config;
import yoan.shopping.infra.util.error.ApplicationException;

/**
 * Converter from properties format to Config
 * @author yoan
 */
public class ConfigPropertiesConverter {
	private static final String API_HOST_FIELD = "api.host";
	private static final String API_PORT_FIELD = "api.port";
	
	private static final String MONGO_HOST_FIELD = "mongo.host";
	private static final String MONGO_PORT_FIELD = "mongo.port";
	private static final String MONGO_USER_FIELD = "mongo.user";
	private static final String MONGO_PASS_FIELD = "mongo.pass";
	
	private static final String SWAGGER_BASE_PATH_FIELD = "swagger.basePath";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigPropertiesConverter.class);
	
	public static Config fromProperties(Properties properties) {
		String apiHost = getMandatoryProperty(properties, API_HOST_FIELD);
		Integer apiPort = getMandatoryIntegerProperty(properties, API_PORT_FIELD);
		
		String mongoHost = getMandatoryProperty(properties, MONGO_HOST_FIELD);
		Integer mongoPort = getMandatoryIntegerProperty(properties, MONGO_PORT_FIELD);
		String mongoUser = getOptionalProperty(properties, MONGO_USER_FIELD);
		String mongoPass = getOptionalProperty(properties, MONGO_PASS_FIELD);
		
		String swaggerBasePath = getMandatoryProperty(properties, SWAGGER_BASE_PATH_FIELD);
		
		return Config.Builder.createDefault()
							.withApiHost(apiHost).withApiPort(apiPort)
							.withMongoHost(mongoHost).withMongoPort(mongoPort)
							.withMongoUser(mongoUser).withMongoPass(mongoPass)
							.withSwaggerBasePath(swaggerBasePath)
							.build();
	}
	
	private static String getMandatoryProperty(Properties properties, String fieldName) {
		String property = properties.getProperty(fieldName);
		if (StringUtils.isBlank(property)) {
			String message = "Missing mandatory property : " + fieldName; 
			LOGGER.error(CONFIG.getMarker(), message);
			throw new ApplicationException(ERROR, APPLICATION_ERROR, message);
		}
		return property;
	}
	
	private static Integer getMandatoryIntegerProperty(Properties properties, String fieldName) {
		String propertyStr = getMandatoryProperty(properties, fieldName);
		Integer property = null;
		try {
			property = Integer.parseInt(propertyStr);
		} catch(NumberFormatException e) {
			String message = "Invalid integer format for mandatory property : " + fieldName; 
			LOGGER.error(CONFIG.getMarker(), message, e);
			throw new ApplicationException(ERROR, APPLICATION_ERROR, message, e);
		}
		return property;
	}
	
	private static String getOptionalProperty(Properties properties, String fieldName) {
		String property = properties.getProperty(fieldName);
		if (StringUtils.isBlank(property)) {
			String message = "Missing optionnal property : " + fieldName + " => skipping it"; 
			LOGGER.info(CONFIG.getMarker(), message);
			return null;
		}
		return property;
	}
	
//	private static Integer getOptionnalIntegerProperty(Properties properties, String fieldName) {
//		String propertyStr = getOptionalProperty(properties, fieldName);
//		if (propertyStr == null)
//			return null;
//		
//		Integer property = null;
//		try {
//			property = Integer.parseInt(propertyStr);
//		} catch(NumberFormatException e) {
//			LOGGER.warn(CONFIG.getMarker(), "Invalid integer format for optionnal property : " + fieldName + " => skipping it", e);
//		}
//		return property;
//	}
}
