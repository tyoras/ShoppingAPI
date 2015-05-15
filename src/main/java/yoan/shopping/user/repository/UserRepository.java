/**
 * 
 */
package yoan.shopping.user.repository;

import java.util.UUID;

import yoan.shopping.user.User;

/**
 * User repository
 * @author yoan
 */
public abstract class UserRepository {
	
	protected final User connectedUser;
	
	protected UserRepository(User connectedUser) {
		this.connectedUser = connectedUser;
	}
	
	/**
	 * Create a new User
	 * @param userToCreate
	 */
	public abstract void create(User userToCreate);
	
	/**
	 * Get a user by its Id
	 * @param userId
	 */
	public abstract User getById(UUID userId);
	
	/**
	 * Update a new User
	 * @param userToUpdate
	 */
	public abstract void update(User userToUpdate);
	
	/**
	 * Delete a user by its Id
	 * @param userId
	 */
	public abstract void deleteById(UUID userId);
}
