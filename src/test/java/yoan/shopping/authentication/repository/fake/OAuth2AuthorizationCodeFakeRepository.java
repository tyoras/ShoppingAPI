package yoan.shopping.authentication.repository.fake;

import java.util.UUID;

import yoan.shopping.authentication.repository.OAuth2AuthorizationCodeRepository;

/**
 * Fake implementation of OAuth2 authorization code repository
 * Test purpose only
 * @author yoan
 */
public class OAuth2AuthorizationCodeFakeRepository extends OAuth2AuthorizationCodeRepository {
	@Override
	protected UUID processGetUserIdByAuthorizationCode(String authzCode) {
		return null;
	}

	@Override
	protected void processInsert(String authzCode, UUID userId) { }

	@Override
	protected void processDeleteByCode(String authzCode) { }
}