/**
 * 
 */
package yoan.shopping.user.repository.fake;

import java.util.UUID;

import yoan.shopping.user.SecuredUser;
import yoan.shopping.user.repository.SecuredUserRepository;

/**
 * Fake implementation of User repository focused on security information
 * Test purpose only
 * @author yoan
 */
public class SecuredUserFakeRepository extends SecuredUserRepository {
	@Override
	protected void createImpl(SecuredUser userToCreate) { }

	@Override
	protected SecuredUser processGetById(UUID userId) {
		return null;
	}
}
