package yoan.shopping.authentication.repository;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

/**
 * OAuth2 access token repository
 * @author yoan
 */
public abstract class OAuth2AccessTokenRepository {
	
	public UUID getUserIdByAccessToken(String accessToken) {
		if (StringUtils.isBlank(accessToken)) {
			//TODO log problem
			return null;
		}
		return processGetUserIdByAccessToken(accessToken);
	};
	
	public void insertAccessToken(String accessToken, UUID userId) {
		if (StringUtils.isBlank(accessToken)) {
			//TODO log problem
			return;
		}
		
		if (userId == null) {
			//TODO log problem
			return;
		}
		
		processInsertAccessToken(accessToken, userId);
	}
	
	protected abstract UUID processGetUserIdByAccessToken(String accessToken);
	
	protected abstract void processInsertAccessToken(String accessToken, UUID userId);
}
