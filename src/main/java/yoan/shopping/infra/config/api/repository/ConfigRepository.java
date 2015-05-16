/**
 * 
 */
package yoan.shopping.infra.config.api.repository;

import yoan.shopping.infra.config.api.Config;

/**
 * Application configuation repository
 * @author yoan
 */
public abstract class ConfigRepository {
	/** Name of the environment variable to find the config file */
	protected static final String CONFIG_LOCATION_ENV_VARIABLE = "API_SHOPPING_CONFIG_FILE_PATH";
	
	/**
	 * Read the API configuration
	 * @return Config if found, else null
	 */
	public Config readConfig() {
		String configLocation = System.getenv(CONFIG_LOCATION_ENV_VARIABLE);
		return readConfig(configLocation);
	}
	
	protected abstract Config readConfig(String configLocation);
}
