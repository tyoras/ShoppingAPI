package yoan.shopping.root.repository.properties;

import static org.fest.assertions.api.Assertions.assertThat;
import static yoan.shopping.root.repository.properties.BuildInfoPropertiesRepository.BUILD_INFO_DEFAULT_PROPERTIES_FILE_NAME;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.Test;

import yoan.shopping.root.BuildInfo;

public class BuildInfoPropertiesRepositoryTest {
	
	@Test
	public void readConfig_should_work_when_config_file_is_found() {
		//given
		BuildInfoPropertiesRepository testedRepo = new BuildInfoPropertiesRepository(BUILD_INFO_DEFAULT_PROPERTIES_FILE_NAME);
		String expectedVersion = "TEST";
		LocalDateTime expectedDate = LocalDateTime.ofInstant(Instant.parse("2015-06-02T19:21:00Z"), ZoneId.systemDefault());
		BuildInfo expectedInfo = new BuildInfo(expectedVersion, expectedDate);
		
		//when
		BuildInfo info = testedRepo.getCurrentBuildInfos();
		
		//then
		assertThat(info).isNotNull();
		assertThat(info).isEqualTo(expectedInfo);
	}
	
	@Test
	public void readConfig_should_return_DEFAULT_when_build_file_is_not_found() {
		//given
		BuildInfoPropertiesRepository testedRepo = new BuildInfoPropertiesRepository("nothere.properties");
		
		//when
		BuildInfo info = testedRepo.getCurrentBuildInfos();
		
		//then
		assertThat(info).isNotNull();
		assertThat(info).isEqualTo(BuildInfo.DEFAULT);
	}
}
