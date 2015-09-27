package yoan.shopping.authentication.repository.fake;

import java.util.UUID;

import yoan.shopping.authentication.repository.OAuth2AccessTokenRepository;

/**
 * Fake implementation of OAuth2 access token repository
 * Test purpose only
 * @author yoan
 */
public class OAuth2AccessTokenFakeRepository extends OAuth2AccessTokenRepository {
	@Override
	protected UUID processGetUserIdByAccessToken(String accessToken) {
		return null;
	}

	@Override
	protected void processInsert(String accessToken, UUID userId) { }

	@Override
	protected void processDeleteByAccessToken(String accessToken) { }
}