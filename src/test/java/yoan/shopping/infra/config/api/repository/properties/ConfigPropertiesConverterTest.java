package yoan.shopping.infra.config.api.repository.properties;

import static org.fest.assertions.api.Assertions.assertThat;
import static yoan.shopping.infra.config.api.repository.properties.ConfigPropertiesConverter.API_HOST_FIELD;
import static yoan.shopping.infra.config.api.repository.properties.ConfigPropertiesConverter.API_PORT_FIELD;
import static yoan.shopping.infra.config.api.repository.properties.ConfigPropertiesConverter.MONGO_HOST_FIELD;
import static yoan.shopping.infra.config.api.repository.properties.ConfigPropertiesConverter.MONGO_PASS_FIELD;
import static yoan.shopping.infra.config.api.repository.properties.ConfigPropertiesConverter.MONGO_PORT_FIELD;
import static yoan.shopping.infra.config.api.repository.properties.ConfigPropertiesConverter.MONGO_USER_FIELD;
import static yoan.shopping.infra.config.api.repository.properties.ConfigPropertiesConverter.SWAGGER_BASE_PATH_FIELD;

import java.util.Properties;

import org.junit.Test;

import yoan.shopping.infra.config.api.Config;


public class ConfigPropertiesConverterTest {

	@Test
	public void fromProperties_should_work() {
		//given
		Config expectedConfig = getValidConfig();
		Properties properties = getValidConfigProperties(expectedConfig);

		//when
		Config config = ConfigPropertiesConverter.fromProperties(properties);
		
		//then
		assertThat(config).isNotNull();
		assertThat(config).isEqualTo(expectedConfig);
	}
	
	private Properties getValidConfigProperties(Config config) {
		Properties properties = new Properties();
		properties.setProperty(API_HOST_FIELD, config.getApiHost());
		properties.setProperty(API_PORT_FIELD, config.getApiPort().toString());
		properties.setProperty(MONGO_HOST_FIELD, config.getMongoHost());
		properties.setProperty(MONGO_PORT_FIELD, config.getMongoPort().toString());
		properties.setProperty(MONGO_USER_FIELD, config.getMongoUser());
		properties.setProperty(MONGO_PASS_FIELD, config.getMongoPass());
		properties.setProperty(SWAGGER_BASE_PATH_FIELD, config.getSwaggerBasePath());
		return properties;
	}
	
	private Config getValidConfig() {
		return Config.Builder.createDefault()
				.withMongoUser("user")
				.withMongoPass("pass")
				.build();
	}
}
