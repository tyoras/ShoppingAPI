package yoan.shopping.user.repository.fake;

import java.util.UUID;

import yoan.shopping.user.User;
import yoan.shopping.user.repository.UserRepository;

/**
 * Fake implementation of User repository
 * Test purpose only
 * @author yoan
 */
public class UserFakeRepository extends UserRepository {

	@Override
	protected void processCreate(User userToCreate) { }

	@Override
	protected User processGetById(UUID userId) {
		return null;
	}

	@Override
	protected void processUpdate(User userToUpdate) { }

	@Override
	protected void processDeleteById(UUID userId) { }

}
