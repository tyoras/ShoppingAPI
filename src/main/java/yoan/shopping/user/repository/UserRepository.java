/**
 * 
 */
package yoan.shopping.user.repository;

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
	 * Creates a new User
	 * @param userToCreate
	 */
	public abstract void create(User userToCreate);
}
