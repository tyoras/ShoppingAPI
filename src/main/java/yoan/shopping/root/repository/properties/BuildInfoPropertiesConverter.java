/**
 * 
 */
package yoan.shopping.root.repository.properties;

import static yoan.shopping.infra.logging.Markers.CONFIG;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yoan.shopping.root.BuildInfo;

/**
 * Converter from properties format to BuildInfo
 * @author yoan
 */
public class BuildInfoPropertiesConverter {
	private static final String BUILD_VERSION_FIELD = "version";
	private static final String BUILD_DATE_FIELD = "build.date";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BuildInfoPropertiesConverter.class);
	
	public static BuildInfo fromProperties(Properties properties) {
		String version = properties.getProperty(BUILD_VERSION_FIELD);
		String buildDateStr = properties.getProperty(BUILD_DATE_FIELD);
		LocalDateTime buildDate = null;
		try {
			buildDate = LocalDateTime.parse(buildDateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		} catch(DateTimeParseException dtpe) {
			if (!"${maven.build.timestamp}".equals(buildDateStr)) {
				LOGGER.warn(CONFIG.getMarker(), "Unable to parse date found in properties file", dtpe);
			} else {
				LOGGER.debug(CONFIG.getMarker(), "Unable to parse date found in properties file : ${maven.build.timestamp}" );
				buildDate = LocalDateTime.now();
			}
		}
		
		return new BuildInfo(version, buildDate);
	}
}
