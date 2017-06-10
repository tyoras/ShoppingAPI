package io.tyoras.shopping.user.repository;

import static io.tyoras.shopping.infra.rest.error.Level.INFO;
import static io.tyoras.shopping.infra.util.error.RepositoryErrorCode.NOT_FOUND;
import static io.tyoras.shopping.user.resource.UserResourceErrorMessage.USER_NOT_FOUND;

import java.time.LocalDateTime;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import io.tyoras.shopping.infra.util.error.ApplicationException;
import io.tyoras.shopping.user.ProfileVisibility;
import io.tyoras.shopping.user.User;

/**
 * User repository
 * @author yoan
 */
public abstract class UserRepository {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(UserRepository.class);
	
	public static final int NAME_SEARCH_MIN_LENGTH = 3;
	
	public static final int NAME_SEARCH_MAX_RESULT = 10;
	
	/**
	 * Create a new User
	 * @param askedUserToCreate
	 */
	public final void create(User askedUserToCreate) {
		if (askedUserToCreate == null) {
			LOGGER.warn("User creation asked with null user");
			return;
		}
		
		User userToCreate = forceCreationDate(askedUserToCreate);
		processCreate(userToCreate);
	}
	
	private User forceCreationDate(User user) {
		LocalDateTime creationDate = LocalDateTime.now();
		return User.Builder.createFrom(user)
			.withCreationDate(creationDate)
			.withLastUpdate(creationDate)
			.build();
	}
	
	/**
	 * Get a user by its Id
	 * @param userId
	 * @return found user or null if not found
	 */
	public final User getById(UUID userId) {
		if (userId == null) {
			return null;
		}
		return processGetById(userId);
	}
	
	/**
	 * Get a user by its email adress
	 * @param email
	 * @return found user or null if not found
	 */
	public final User getByEmail(String email) {
		if (StringUtils.isBlank(email)) {
			return null;
		}
		return processGetByEmail(email);
	}
	
	/**
	 * Update a User
	 * @param askedUserToUpdate
	 */
	public final void update(User askedUserToUpdate) {
		if (askedUserToUpdate == null) {
			LOGGER.warn("User update asked with null user");
			return;
		}
		User existingUser = findUser(askedUserToUpdate.getId());
		
		User userToUpdate = mergeUpdatesInExistingUser(existingUser, askedUserToUpdate);
		processUpdate(userToUpdate);
	}
	
	private User mergeUpdatesInExistingUser(User existingUser, User askedUserToUpdate) {
		return User.Builder.createFrom(existingUser)
				.withLastUpdate(LocalDateTime.now())
				.withName(askedUserToUpdate.getName())
				.withEmail(askedUserToUpdate.getEmail())
				.withProfileVisibility(askedUserToUpdate.getProfileVisibility())
				.build();
	}
	
	/**
	 * Get a user by its Id and fail if it does not exist
	 * @param userId
	 * @return found user
	 * @throws ApplicationException if user not found
	 */
	public final User findUser(UUID userId) {
		User foundUser = getById(userId);
		
		if (foundUser == null) {
			throw new ApplicationException(INFO, NOT_FOUND, USER_NOT_FOUND);
		}
		
		return foundUser;
	}
	
	
	/**
	 * Delete a user by its Id
	 * @param userId
	 */
	public final void deleteById(UUID userId) {
		if (userId == null) {
			LOGGER.warn("User deletion asked with null Id");
			return;
		}
		processDeleteById(userId);
	}
	
	/**
	 * Check if a user exists with given id or email
	 * @param userId
	 * @param email
	 * @return boolean
	 */
	public boolean checkUserExistsByIdOrEmail(UUID userId, String email) {
		if (userId == null && StringUtils.isBlank(email)) {
			LOGGER.warn("Checking user existence with null Id or email");
			return false;
		}
		return countByIdOrEmail(userId, email) > 0;
	}
	
	/**
	 * Search all user with name containing the search
	 * @param search : at least 3 characters
	 * @return ImmutableList, empty if no result
	 */
	public final ImmutableList<User> searchByName(String search) {
		if (StringUtils.isBlank(search) || search.length() < NAME_SEARCH_MIN_LENGTH) {
			LOGGER.warn(String.format("Unable to search by name with this search \"%s\"", search));
			return ImmutableList.of();
		}
		return processSearchByName(ProfileVisibility.PUBLIC, NAME_SEARCH_MAX_RESULT, search);
	}
	
	/**
	 * Create a new user
	 * @param userToCreate
	 */
	protected abstract void processCreate(User userToCreate);
	
	/**
	 * Get a user by its Id
	 * @param userId
	 */
	protected abstract User processGetById(UUID userId);
	
	/**
	 * Get a user by its email adress
	 * @param email
	 */
	protected abstract User processGetByEmail(String email);
	
	/**
	 * Update an existing user
	 * @param userToUpdate
	 */
	protected abstract void processUpdate(User userToUpdate);
	
	/**
	 * Delete a user by its Id
	 * @param userId
	 */
	protected abstract void processDeleteById(UUID userId);
	
	/**
	 * Count users with id or email
	 * @param userId
	 * @param email
	 * @return number of user with id or email
	 */
	protected abstract long countByIdOrEmail(UUID userId, String email);
	
	/**
	 * Search users by name 
	 * @param visibility
	 * @param nbMaxResult : number of result threshold
	 * @param search
	 * @return found users if less than nbMaxResult
	 * @throws ApplicationException if more than nbMaxResult users found
	 */
	protected abstract ImmutableList<User> processSearchByName(ProfileVisibility visibility, int nbMaxResult, String search);
}
