package yoan.shopping.client.app.repository;

import static yoan.shopping.client.app.repository.ClientAppRepositoryErrorCode.UNSECURE_SECRET;
import static yoan.shopping.client.app.repository.ClientAppRepositoryErrorMessage.PROBLEM_SECRET_VALIDITY;
import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.rest.error.Level.INFO;
import static yoan.shopping.infra.util.error.CommonErrorMessage.NOT_FOUND;

import java.time.LocalDateTime;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yoan.shopping.client.app.ClientApp;
import yoan.shopping.infra.config.guice.ShiroSecurityModule;
import yoan.shopping.infra.util.error.ApplicationException;
import yoan.shopping.infra.util.error.RepositoryErrorCode;

/**
 * Client application repository
 * @author yoan
 */
public abstract class ClientAppRepository {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(ClientAppRepository.class);
	
	/**
	 * Create a new User
	 * @param userToCreate
	 */
	public void create(ClientApp appToCreate, String secret) {
		if (appToCreate == null) {
			LOGGER.warn("Client app creation asked with null app");
			return;
		}
		ensureSecretValidity(secret);
		
		ClientApp appWithCreationDate = forceCreationDate(appToCreate);
		ClientApp clientAppToCreate = forceClientSecret(appWithCreationDate, secret);
		processCreate(clientAppToCreate);
	}
	
	private ClientApp forceCreationDate(ClientApp app) {
		LocalDateTime creationDate = LocalDateTime.now();
		return ClientApp.Builder.createFrom(app)
			.withCreationDate(creationDate)
			.withLastUpdate(creationDate)
			.build();
	}
	
	private ClientApp forceClientSecret(ClientApp app, String secret) {
		Object salt = generateSalt();
		String hashedSecret = hashSecret(secret, salt);
		return ClientApp.Builder.createFrom(app)
					   		  	.withSecret(hashedSecret)
					   		  	.withSalt(salt)
					   		  	.build();
	}
	
	protected Object generateSalt() {
		return UUID.randomUUID().toString();
	}
	
	protected void ensureSecretValidity(String secret) {
		if (!checkSecretValidity(secret)) {
			String message = PROBLEM_SECRET_VALIDITY.getDevReadableMessage();
			LOGGER.error(message);
			throw new ApplicationException(ERROR, UNSECURE_SECRET, message);
		}
	}
	
	protected boolean checkSecretValidity(String secret) {
		return StringUtils.isNotBlank(secret);
	}
	
	public String hashSecret(String secret, Object salt) {
		return new Sha256Hash(secret, salt, ShiroSecurityModule.NB_HASH_ITERATION).toBase64();
	}
	
	/**
	 * Get a client app by its Id
	 * @param clientId
	 * @return found app
	 */
	public final ClientApp getById(UUID clientId) {
		if (clientId == null) {
			return null;
		}
		return processGetById(clientId);
	}
	
	/**
	 * Get a user by its Id and fail if it does not exist
	 * @param userId
	 * @return found app
	 * @throws ApplicationException if user not found
	 */
	public final ClientApp findClientAppById(UUID clientId) {
		ClientApp foundApp = getById(clientId);
		
		if (foundApp == null) {
			throw new ApplicationException(INFO, RepositoryErrorCode.NOT_FOUND, NOT_FOUND.getDevReadableMessage("Client app"));
		}
		
		return foundApp;
	}
	
	/**
	 * Change client app secret
	 * @param clientId
	 * @param newSecret
	 */
	public final void changeSecret(UUID clientId, String newSecret) {
		if (clientId == null) {
			LOGGER.warn("Secret update asked with null client app");
			return;
		}
		ensureSecretValidity(newSecret);
		
		ClientApp existingClientApp = findClientAppById(clientId);
		existingClientApp = forceLastUpdateDate(existingClientApp);
		ClientApp clientAppToUpdate = forceClientSecret(existingClientApp, newSecret);
		processChangeSecret(clientAppToUpdate);
	}
	
	private ClientApp forceLastUpdateDate(ClientApp app) {
		return ClientApp.Builder.createFrom(app)
			.withLastUpdate(LocalDateTime.now())
			.build();
	}
	
	/**
	 * Delete a client app by its Id
	 * @param listId
	 */
	public final void deleteById(UUID listId) {
		if (listId == null) {
			LOGGER.warn("Client app deletion asked with null Id");
			return;
		}
		processDeleteById(listId);
	}
	
	/**
	 * Create a new ClientApp
	 * @param appToCreate
	 */
	protected abstract void processCreate(ClientApp appToCreate);
	
	/**
	 * Get a client app by its Id
	 * @param userId
	 */
	protected abstract ClientApp processGetById(UUID clientId);
	
	/**
	 * Update secret
	 * @param userToUpdate
	 */
	protected abstract void processChangeSecret(ClientApp clientAppToUpdate);
	
	/**
	 * Delete a client app by its Id
	 * @param userId
	 */
	protected abstract void processDeleteById(UUID clientId);
}
