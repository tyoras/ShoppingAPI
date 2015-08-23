package yoan.shopping.authentication.repository.inmemory;

import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;
import com.google.inject.Singleton;

import yoan.shopping.authentication.repository.OAuth2AccessTokenRepository;

/**
 * In memory implementation of the oauth2 access token repository
 * @author yoan
 */
@Singleton
public class OAuth2AccessTokenInMemoryRepository extends OAuth2AccessTokenRepository {

	private final Map<String, UUID> userIdByAccessToken = Maps.newHashMap();
	
	@Override
	protected UUID processGetUserIdByAccessToken(String accessToken) {
		return userIdByAccessToken.get(accessToken);
	}

	@Override
	protected void processInsertAccessToken(String accessToken, UUID userId) {
		userIdByAccessToken.put(accessToken, userId);
	}

}
