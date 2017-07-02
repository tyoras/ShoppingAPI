package io.tyoras.shopping.user;

import static com.google.common.base.Preconditions.checkArgument;
import static io.tyoras.shopping.infra.util.error.CommonErrorMessage.UNABLE_TO_FIND;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

public enum ProfileVisibility {
	PUBLIC("Profile can be searched or viewed by all users"),
	PRIVATE("Profile can be searched or viewed only by friends");
	
	/** Visibility decription */
	private final String description;
	
	private ProfileVisibility(String description) {
		checkArgument(isNotBlank(description), "A visibility description should not be empty");
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
	public static ProfileVisibility valueOfOrNull(String codeStr) {
		if (StringUtils.isBlank(codeStr)) {
			return handleInvalidCode(codeStr);
		}
		try {
			return ProfileVisibility.valueOf(codeStr);
		} catch(IllegalArgumentException iae) {
			return handleInvalidCode(codeStr);
		}
	}
	
	private static ProfileVisibility handleInvalidCode(String code) {
		LoggerFactory.getLogger(ProfileVisibility.class).info(UNABLE_TO_FIND.getDevReadableMessage("ProfileVisibility", code));
		return null;
	}
}
