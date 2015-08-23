package yoan.shopping.authentication.repository.inmemory;

import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;
import com.google.inject.Singleton;

import yoan.shopping.authentication.repository.OAuth2AuthorizationCodeRepository;

/**
 * In memory implementation of the oauth2 access token repository
 * @author yoan
 */
@Singleton
public class OAuth2AuthorizationCodeInMemoryRepository extends OAuth2AuthorizationCodeRepository {
	
	private final Map<String, UUID> userIdByAuthorizationCode = Maps.newHashMap();
	
	@Override
	protected UUID processGetUserIdByAuthorizationCode(String authzCode) {
		return userIdByAuthorizationCode.get(authzCode);
	}

	@Override
	protected void processInsert(String authzCode, UUID userId) {
		userIdByAuthorizationCode.put(authzCode, userId);
	}

	@Override
	protected void processDeleteByCode(String authzCode) {
		userIdByAuthorizationCode.remove(authzCode);
	}

}
