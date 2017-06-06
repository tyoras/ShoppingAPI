package yoan.shopping.infra.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.util.error.CommonErrorCode.APPLICATION_ERROR;
import static yoan.shopping.test.TestHelper.assertApplicationException;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import yoan.shopping.infra.util.error.ApplicationException;

@RunWith(MockitoJUnitRunner.class)
public class ShoppingApiConfigurationSourceProviderTest {
	
	@Spy
	private ShoppingApiConfigurationSourceProvider tested;
	
	@Test
	public void open_should_use_path_if_provided() throws IOException {
		//given
		String configPath = "src/test/resources/config/defaultConfigAPI.yml";
		
		//when
		InputStream result = tested.open(configPath);
		
		//then
		assertThat(result).isNotNull();
		verify(tested, never()).readConfig();
	}
	
	@Test(expected = ApplicationException.class)
	public void open_should_fail_with_invalid_env_config_location() throws IOException {
		//given
		String wrongConfigPath = "...invalid...";
		doReturn(wrongConfigPath).when(tested).readConfigLocationFromEnv();
		String expectedMessage = "Unable to find a readable file at path found in \"API_SHOPPING_CONFIG_FILE_PATH\" env variable : ...invalid...";
		
		//when
		try {
			tested.open(null);
		} catch (ApplicationException ae) {
		//then
			assertApplicationException(ae, ERROR, APPLICATION_ERROR, expectedMessage);
			throw ae;
		}
	}
	
	@Test
	public void open_should_work_with_valid_env_config_location() throws IOException {
		//given
		String configPath = "src/test/resources/config/defaultConfigAPI.yml";
		doReturn(configPath).when(tested).readConfigLocationFromEnv();
		
		//when
		InputStream result = tested.open(null);
		
		//then
		assertThat(result).isNotNull();
		verify(tested).readConfig();
	}
	
	@Test
	public void open_should_work_without_env_config_location_defined() throws IOException {
		//given
		doReturn(null).when(tested).readConfigLocationFromEnv();
		
		//when
		InputStream result = tested.open(null);
		
		//then
		assertThat(result).isNotNull();
		verify(tested).readConfig();
	}
}

