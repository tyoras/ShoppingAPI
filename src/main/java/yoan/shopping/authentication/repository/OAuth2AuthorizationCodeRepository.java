package yoan.shopping.authentication.repository;

import static yoan.shopping.authentication.repository.OAuth2AuthorizationCodeRepositoryErrorMessage.*;
import static yoan.shopping.infra.logging.Markers.AUTHENTICATION;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

/**
 * OAuth2 authorization code repository
 * @author yoan
 */
public abstract class OAuth2AuthorizationCodeRepository {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2AuthorizationCodeRepository.class);
	private static final Marker AUTH_MARKER = AUTHENTICATION.getMarker();
	
	public UUID getUserIdByAuthorizationCode(String authzCode) {
		if (StringUtils.isBlank(authzCode)) {
			LOGGER.error(AUTH_MARKER, PROBLEM_INVALID_AUTH_CODE.getDevReadableMessage(authzCode, "searching user ID"));
			return null;
		}
		return processGetUserIdByAuthorizationCode(authzCode);
	};
	
	public void insert(String authzCode, UUID userId) {
		if (StringUtils.isBlank(authzCode)) {
			LOGGER.error(AUTH_MARKER, PROBLEM_INVALID_AUTH_CODE.getDevReadableMessage(authzCode, "inserting code for user : " + userId));
			return;
		}
		
		if (userId == null) {
			LOGGER.error(AUTH_MARKER, PROBLEM_INSERT_USER_ID_NULL.getDevReadableMessage(authzCode));
			return;
		}
		
		processInsert(authzCode, userId);
	}
	
	public void deleteByCode(String authzCode) {
		if (StringUtils.isBlank(authzCode)) {
			LOGGER.error(AUTH_MARKER, PROBLEM_INVALID_AUTH_CODE.getDevReadableMessage(authzCode, "deleting auth code"));
			return;
		}
		
		processDeleteByCode(authzCode);
	}
	
	protected abstract UUID processGetUserIdByAuthorizationCode(String authzCode);
	
	protected abstract void processInsert(String authzCode, UUID userId);
	
	protected abstract void processDeleteByCode(String authzCode);
}
