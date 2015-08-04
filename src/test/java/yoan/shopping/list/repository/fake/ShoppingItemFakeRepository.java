package yoan.shopping.list.repository.fake;

import java.util.UUID;

import yoan.shopping.list.ShoppingItem;
import yoan.shopping.list.repository.ShoppingItemRepository;

/**
 * Fake implementation of the shopping item repository
 * Test purpose only
 * @author yoan
 */
public class ShoppingItemFakeRepository extends ShoppingItemRepository {

	@Override
	protected void processCreate(UUID listId, ShoppingItem itemToCreate) { }

	@Override
	protected ShoppingItem processGetById(UUID listId, UUID itemId) { return null; }

	@Override
	protected void processUpdate(UUID listId, ShoppingItem itemToUpdate) { }

	@Override
	protected void processDeleteById(UUID listId, UUID itemId) { }
}