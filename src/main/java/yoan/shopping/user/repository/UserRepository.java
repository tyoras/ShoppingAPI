/**
 * 
 */
package yoan.shopping.user.repository;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yoan.shopping.user.User;

/**
 * User repository
 * @author yoan
 */
public abstract class UserRepository {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(UserRepository.class);
	
	/**
	 * Create a new User
	 * @param userToCreate
	 */
	public final void create(User userToCreate) {
		if (userToCreate == null) {
			LOGGER.warn("User creation asked with null user");
			return;
		}
		createImpl(userToCreate);
	}
	
	/**
	 * Get a user by its Id
	 * @param userId
	 */
	public final User getById(UUID userId) {
		if (userId == null) {
			return null;
		}
		return getByIdImpl(userId);
	}
	
	/**
	 * Upsert a User
	 * @param userToUpdate
	 */
	public final void upsert(User userToUpdate) {
		if (userToUpdate == null) {
			LOGGER.warn("User upsert asked with null user");
			return;
		}
		upsertImpl(userToUpdate);
	}
	
	/**
	 * Delete a user by its Id
	 * @param userId
	 */
	public final void deleteById(UUID userId) {
		if (userId == null) {
			LOGGER.warn("User Deletion asked with null Id");
			return;
		}
		deleteByIdImpl(userId);
	}
	
	/**
	 * Create a new User
	 * @param userToCreate
	 */
	protected abstract void createImpl(User userToCreate);
	
	/**
	 * Get a user by its Id
	 * @param userId
	 */
	protected abstract User getByIdImpl(UUID userId);
	
	/**
	 * Update a new User
	 * @param userToUpdate
	 */
	protected abstract void upsertImpl(User userToUpdate);
	
	/**
	 * Delete a user by its Id
	 * @param userId
	 */
	protected abstract void deleteByIdImpl(UUID userId);
}
