package yoan.shopping.client.app.repository.fake;

import java.util.UUID;

import yoan.shopping.client.app.ClientApp;
import yoan.shopping.client.app.repository.ClientAppRepository;

import com.google.common.collect.ImmutableList;

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

	@Override
	protected void processUpdate(ClientApp clientAppToUpdate) { }

	@Override
	protected ImmutableList<ClientApp> processGetByOwner(UUID ownerId) {
		return ImmutableList.of();
	}
}