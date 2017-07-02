package io.tyoras.shopping.root.repository.properties;

import static io.tyoras.shopping.root.repository.properties.BuildInfoPropertiesConverter.BUILD_DATE_FIELD;
import static io.tyoras.shopping.root.repository.properties.BuildInfoPropertiesConverter.BUILD_VERSION_FIELD;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import org.junit.Test;

import io.tyoras.shopping.root.BuildInfo;
import io.tyoras.shopping.root.repository.properties.BuildInfoPropertiesConverter;

public class BuildInfoPropertiesConverterTest {
	
	@Test
	public void fromProperties_should_work() {
		//given
		BuildInfo expectedInfo = getValidBuildInfo();
		Properties properties = getValidInfoProperties(expectedInfo);

		//when
		BuildInfo info = BuildInfoPropertiesConverter.fromProperties(properties);
		
		//then
		assertThat(info).isNotNull();
		assertThat(info).isEqualTo(expectedInfo);
	}
	
	@Test
	public void fromProperties_should_return_build_date_if_date_was_not_replaced_in_file() {
		//given
		Properties properties = new Properties();
		properties.setProperty(BUILD_VERSION_FIELD, "valid version");
		properties.setProperty(BUILD_DATE_FIELD, "${maven.build.timestamp}");

		//when
		BuildInfo info = BuildInfoPropertiesConverter.fromProperties(properties);
		
		//then
		assertThat(info).isNotNull();
		assertThat(info.getVersion()).isEqualTo("valid version");
		assertThat(info.getBuildDate()).isNotNull();
	}
	
	@Test
	public void fromProperties_should_return_null_build_date_if_date_is_wrong() {
		//given
		Properties properties = new Properties();
		properties.setProperty(BUILD_VERSION_FIELD, "valid version");
		properties.setProperty(BUILD_DATE_FIELD, "wrong_date");

		//when
		BuildInfo info = BuildInfoPropertiesConverter.fromProperties(properties);
		
		//then
		assertThat(info).isNotNull();
		assertThat(info.getVersion()).isEqualTo("valid version");
		assertThat(info.getBuildDate()).isNull();
	}
	
	private Properties getValidInfoProperties(BuildInfo info) {
		Properties properties = new Properties();
		properties.setProperty(BUILD_VERSION_FIELD, info.getVersion());
		properties.setProperty(BUILD_DATE_FIELD, info.getBuildDate().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_INSTANT));
		return properties;
	}
	
	private BuildInfo getValidBuildInfo() {
		return new BuildInfo("valid version", LocalDateTime.ofInstant(Instant.parse("2015-06-02T19:21:00Z"), ZoneId.systemDefault()));
	}
}
