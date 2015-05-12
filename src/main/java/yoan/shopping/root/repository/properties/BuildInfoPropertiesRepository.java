/**
 * 
 */
package yoan.shopping.root.repository.properties;

import static yoan.shopping.infra.logging.Markers.CONFIG;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import yoan.shopping.root.BuildInfo;
import yoan.shopping.root.repository.BuildInfoRepository;

/**
 * Properties file based implementation of the build infos repository
 * @author yoan
 */
@Singleton
public class BuildInfoPropertiesRepository implements BuildInfoRepository {
	/** Properties file name located in src/main/resources */
	private static final String BUILD_INFO_PROPERTIES_FILE_NAME = "/version.properties";
	/** The loaded build infos */
	private static BuildInfo BUILD_INFOS = null;
	static {
		BUILD_INFOS = tryToloadPropertiesFile();
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BuildInfoPropertiesRepository.class);
	
	@Override
	public BuildInfo getCurrentBuildInfos() {
		if (BUILD_INFOS == null) {
			BUILD_INFOS = tryToloadPropertiesFile();
		} 
		return BUILD_INFOS;
	}

	/**
	 * Try to load properties file 
	 * @return build info if read ok else return null
	 */
	private static BuildInfo tryToloadPropertiesFile() {
		Properties properties = new Properties();
		InputStream inputStream = BuildInfoPropertiesRepository.class.getClassLoader().getResourceAsStream(BUILD_INFO_PROPERTIES_FILE_NAME);
		
		if (inputStream != null) {
			try {
				properties.load(inputStream);
			} catch (IOException e) {
				LOGGER.warn(CONFIG.getMarker(), "Problem while loading : " + BUILD_INFO_PROPERTIES_FILE_NAME, e);
				return null;
			}
		} else {
			LOGGER.warn(CONFIG.getMarker(), "property file '" + BUILD_INFO_PROPERTIES_FILE_NAME + "' not found in the classpath");
			return null;
		}
		
		return BuildInfoPropertiesConverter.fromProperties(properties);
	}
	
	
}
