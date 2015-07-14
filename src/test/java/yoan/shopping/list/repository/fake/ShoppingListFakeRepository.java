package yoan.shopping.list.repository.fake;

import java.util.UUID;

import com.google.common.collect.ImmutableList;

import yoan.shopping.list.ShoppingList;
import yoan.shopping.list.repository.ShoppingListRepository;

/**
 * Fake implementation of the shopping list repository
 * Test purpose only
 * @author yoan
 */
public class ShoppingListFakeRepository extends ShoppingListRepository {

	@Override
	protected void processCreate(ShoppingList listToCreate) { }

	@Override
	protected ShoppingList processGetById(UUID listId) { return null; }

	@Override
	protected void processUpdate(ShoppingList listToUpdate) { }

	@Override
	protected void processDeleteById(UUID listId) { }

	@Override
	protected ImmutableList<ShoppingList> processGetByOwner(UUID ownerId) { return ImmutableList.<ShoppingList>of(); }

}
