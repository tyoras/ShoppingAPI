/**
 * 
 */
package io.tyoras.shopping.list.repository;

import static io.tyoras.shopping.infra.rest.error.Level.INFO;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import io.tyoras.shopping.infra.util.error.ApplicationException;
import io.tyoras.shopping.infra.util.error.CommonErrorMessage;
import io.tyoras.shopping.infra.util.error.RepositoryErrorCode;
import io.tyoras.shopping.list.ShoppingList;

/**
 * Repository focused on shopping lists
 * @author yoan
 */
public abstract class ShoppingListRepository {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(ShoppingListRepository.class);
	
	/**
	 * Create a new shopping list
	 * @param shoppingListToCreate
	 */
	public final void create(ShoppingList shoppingListToCreate) {
		if (shoppingListToCreate == null) {
			LOGGER.warn("Shopping list creation asked with null list");
			return;
		}
		
		ShoppingList listToCreate = forceCreationDate(shoppingListToCreate);
		processCreate(listToCreate);
	}
	
	private ShoppingList forceCreationDate(ShoppingList list) {
		LocalDateTime creationDate = LocalDateTime.now();
		return ShoppingList.Builder.createFrom(list)
			.withCreationDate(creationDate)
			.withLastUpdate(creationDate)
			.build();
	}
	
	/**
	 * Get a shopping list by its Id
	 * @param listId
	 * @return found list or null if not found
	 */
	public final ShoppingList getById(UUID listId) {
		if (listId == null) {
			LOGGER.warn("Shopping list asked with null id");
			return null;
		}
		return processGetById(listId);
	}
	
	/**
	 * Get all shopping list from an user
	 * @param ownerId
	 * @return  found lists or null if not found
	 */
	public final ImmutableList<ShoppingList> getByOwner(UUID ownerId) {
		if (ownerId == null) {
			LOGGER.warn("User's shopping lists asked with null id");
			return ImmutableList.of();
		}
		return processGetByOwner(ownerId);
	}
	
	/**
	 * Update a shopping list
	 * @param askedListToUpdate
	 */
	public final void update(ShoppingList askedListToUpdate) {
		if (askedListToUpdate == null) {
			LOGGER.warn("Shopping list update asked with null list");
			return;
		}
		ShoppingList existingShoppingList = findList(askedListToUpdate.getId());
		
		ShoppingList ListToUpdate = mergeUpdatesInExistingShoppingList(existingShoppingList, askedListToUpdate);
		processUpdate(ListToUpdate);
	}
	
	private ShoppingList mergeUpdatesInExistingShoppingList(ShoppingList existingShoppingList, ShoppingList askedShoppingListToUpdate) {
		return ShoppingList.Builder.createFrom(existingShoppingList)
				.withLastUpdate(LocalDateTime.now())
				.withItemList(askedShoppingListToUpdate.getItemList())
				.withName(askedShoppingListToUpdate.getName())
				.build();
	}
	
	/**
	 * Get a list by its Id and fail if it does not exist
	 * @param listId
	 * @return found list
	 * @throws ApplicationException if list not found
	 */
	public final ShoppingList findList(UUID listId) {
		ShoppingList foundList = getById(listId);
		
		if (foundList == null) {
			throw new ApplicationException(INFO, RepositoryErrorCode.NOT_FOUND, CommonErrorMessage.NOT_FOUND.getDevReadableMessage("List"));
		}
		
		return foundList;
	}
	
	
	/**
	 * Delete a user by its Id
	 * @param listId
	 */
	public final void deleteById(UUID listId) {
		if (listId == null) {
			LOGGER.warn("Shopping list deletion asked with null Id");
			return;
		}
		processDeleteById(listId);
	}
	
	/**
	 * Create a new list
	 * @param listToCreate
	 */
	protected abstract void processCreate(ShoppingList listToCreate);
	
	/**
	 * Get a list by its Id
	 * @param listId
	 * @return found list
	 */
	protected abstract ShoppingList processGetById(UUID listId);
	
	/**
	 * Update a list
	 * @param listToUpdate
	 */
	protected abstract void processUpdate(ShoppingList listToUpdate);
	
	/**
	 * Delete a list by its Id
	 * @param userId
	 */
	protected abstract void processDeleteById(UUID listId);
	
	/**
	 * Get all shopping list from an user
	 * @param ownerId
	 * @return found lists
	 */
	protected abstract ImmutableList<ShoppingList> processGetByOwner(UUID ownerId);
}