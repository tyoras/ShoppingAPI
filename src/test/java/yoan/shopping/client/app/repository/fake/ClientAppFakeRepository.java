package yoan.shopping.client.app.repository.fake;

import java.util.UUID;

import yoan.shopping.client.app.ClientApp;
import yoan.shopping.client.app.repository.ClientAppRepository;

/**
 * Fake implementation of Client app repository
 * Test purpose only
 * @author yoan
 */
public class ClientAppFakeRepository extends ClientAppRepository {
	@Override
	protected void processCreate(ClientApp appToCreate) { }

	@Override
	protected ClientApp processGetById(UUID clientId) {
		return null;
	}

	@Override
	protected void processChangeSecret(ClientApp clientAppToUpdate) { }

	@Override
	protected void processDeleteById(UUID clientId) { }
}