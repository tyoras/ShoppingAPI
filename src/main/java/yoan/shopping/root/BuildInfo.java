/**
 * 
 */
package yoan.shopping.root;

import static com.google.common.base.Preconditions.checkArgument;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;

/**
 * Informations about build
 * @author yoan
 */
public class BuildInfo {
	/** Current build version */
	private final String version;
	/** Maven build date */
	private final LocalDateTime buildDate;
	
	public BuildInfo(String version, LocalDateTime buildDate) {
		checkArgument(StringUtils.isNotBlank(version));
		this.version = version;
		this.buildDate = buildDate;
	}

	public String getVersion() {
		return version;
	}

	public LocalDateTime getBuildDate() {
		return buildDate;
	}
}
