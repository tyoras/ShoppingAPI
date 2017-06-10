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
