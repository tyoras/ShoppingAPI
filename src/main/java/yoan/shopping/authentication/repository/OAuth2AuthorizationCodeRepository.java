package yoan.shopping.authentication.repository;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

/**
 * OAuth2 authorization code repository
 * @author yoan
 */
public abstract class OAuth2AuthorizationCodeRepository {
	
	public UUID getUserIdByAuthorizationCode(String authzCode) {
		if (StringUtils.isBlank(authzCode)) {
			//TODO log problem
			return null;
		}
		return processGetUserIdByAuthorizationCode(authzCode);
	};
	
	public void insert(String authzCode, UUID userId) {
		if (StringUtils.isBlank(authzCode)) {
			//TODO log problem
			return;
		}
		
		if (userId == null) {
			//TODO log problem
			return;
		}
		
		processInsert(authzCode, userId);
	}
	
	public void deleteByCode(String authzCode) {
		if (StringUtils.isBlank(authzCode)) {
			//TODO log problem
			return;
		}
		
		processDeleteByCode(authzCode);
	}
	
	protected abstract UUID processGetUserIdByAuthorizationCode(String authzCode);
	
	protected abstract void processInsert(String authzCode, UUID userId);
	
	protected abstract void processDeleteByCode(String authzCode);
}
