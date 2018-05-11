/**
 *
 */
package io.tyoras.shopping.root.repository.properties;

import io.tyoras.shopping.root.BuildInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Properties;

import static io.tyoras.shopping.infra.logging.Markers.CONFIG;

/**
 * Converter from properties format to BuildInfo
 *
 * @author yoan
 */
public class BuildInfoPropertiesConverter {
    protected static final String BUILD_VERSION_FIELD = "version";
    protected static final String BUILD_DATE_FIELD = "build.date";

    private static final Logger LOGGER = LoggerFactory.getLogger(BuildInfoPropertiesConverter.class);

    public static BuildInfo fromProperties(Properties properties) {
        String version = properties.getProperty(BUILD_VERSION_FIELD);
        String buildDateStr = properties.getProperty(BUILD_DATE_FIELD);
        LocalDateTime buildDate = null;
        try {
            Instant instant = Instant.parse(buildDateStr);
            buildDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        } catch (DateTimeParseException dtpe) {
            if (!"${maven.build.timestamp}".equals(buildDateStr)) {
                LOGGER.warn(CONFIG.getMarker(), "Unable to parse date found in properties file", dtpe);
            } else {
                LOGGER.debug(CONFIG.getMarker(), "Unable to parse date found in properties file : ${maven.build.timestamp}");
                buildDate = LocalDateTime.now();
            }
        }

        return new BuildInfo(version, buildDate);
    }
}
