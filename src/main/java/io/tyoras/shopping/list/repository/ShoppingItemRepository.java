package io.tyoras.shopping.list.repository;

import io.tyoras.shopping.infra.util.error.ApplicationException;
import io.tyoras.shopping.infra.util.error.CommonErrorMessage;
import io.tyoras.shopping.infra.util.error.RepositoryErrorCode;
import io.tyoras.shopping.list.ShoppingItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.UUID;

import static io.tyoras.shopping.infra.rest.error.Level.INFO;

/**
 * Repository focused on shopping items
 *
 * @author yoan
 */
public abstract class ShoppingItemRepository {

    public static final Logger LOGGER = LoggerFactory.getLogger(ShoppingItemRepository.class);

    /**
     * Create a new shopping item
     *
     * @param listId               : id of the list which the item is belonging
     * @param shoppingListToCreate
     */
    public final void create(UUID listId, ShoppingItem shoppingListToCreate) {
        if (listId == null) {
            LOGGER.warn("Shopping item creation asked with null list ID");
            return;
        }
        if (shoppingListToCreate == null) {
            LOGGER.warn("Shopping item creation asked with null item");
            return;
        }

        ShoppingItem itemToCreate = forceCreationDate(shoppingListToCreate);
        processCreate(listId, itemToCreate);
    }

    private ShoppingItem forceCreationDate(ShoppingItem item) {
        LocalDateTime creationDate = LocalDateTime.now();
        return ShoppingItem.Builder.createFrom(item)
                .withCreationDate(creationDate)
                .withLastUpdate(creationDate)
                .build();
    }

    /**
     * Get a shopping item by its Id
     *
     * @param listId : id of the list which the item is belonging
     * @param itemId
     * @return found item or null if not found
     */
    public final ShoppingItem getById(UUID listId, UUID itemId) {
        if (listId == null) {
            LOGGER.warn("Shopping item asked with null list ID");
            return null;
        }
        if (itemId == null) {
            LOGGER.warn("Shopping item asked with null id");
            return null;
        }
        return processGetById(listId, itemId);
    }

    /**
     * Update a shopping item
     *
     * @param listId            : id of the list which the item is belonging
     * @param askedListToUpdate
     */
    public final void update(UUID listId, ShoppingItem askedListToUpdate) {
        if (listId == null) {
            LOGGER.warn("Shopping item update asked with null list ID");
            return;
        }
        if (askedListToUpdate == null) {
            LOGGER.warn("Shopping item update asked with null item");
            return;
        }
        ShoppingItem existingShoppingItem = findItem(listId, askedListToUpdate.getId());

        ShoppingItem itemToUpdate = mergeUpdatesInExistingShoppingItem(existingShoppingItem, askedListToUpdate);
        processUpdate(listId, itemToUpdate);
    }

    ShoppingItem mergeUpdatesInExistingShoppingItem(ShoppingItem existingShoppingItem, ShoppingItem askedShoppingItemToUpdate) {
        return ShoppingItem.Builder.createFrom(existingShoppingItem)
                .withLastUpdate(LocalDateTime.now())
                .withQuantity(askedShoppingItemToUpdate.getQuantity())
                .withName(askedShoppingItemToUpdate.getName())
                .withState(askedShoppingItemToUpdate.getState())
                .build();
    }

    /**
     * Get a item by its Id and fail if it does not exist
     *
     * @param listId : id of the list which the item is belonging
     * @param itemId
     * @return found item
     * @throws ApplicationException if item not found
     */
    public final ShoppingItem findItem(UUID listId, UUID itemId) {
        ShoppingItem foundList = getById(listId, itemId);

        if (foundList == null) {
            throw new ApplicationException(INFO, RepositoryErrorCode.NOT_FOUND, CommonErrorMessage.NOT_FOUND.getDevReadableMessage("Item"));
        }

        return foundList;
    }


    /**
     * Delete a user by its Id
     *
     * @param listId : id of the list which the item is belonging
     * @param itemId
     */
    public final void deleteById(UUID listId, UUID itemId) {
        if (listId == null) {
            LOGGER.warn("Shopping item deletion asked with null list ID");
            return;
        }
        if (itemId == null) {
            LOGGER.warn("Shopping item deletion asked with null Id");
            return;
        }
        processDeleteById(listId, itemId);
    }

    /**
     * Create a new item
     *
     * @param listId       : id of the list which the item is belonging
     * @param itemToCreate
     */
    protected abstract void processCreate(UUID listId, ShoppingItem itemToCreate);

    /**
     * Get a item by its Id
     *
     * @param listId : id of the list which the item is belonging
     * @param itemId
     * @return found item
     */
    protected abstract ShoppingItem processGetById(UUID listId, UUID itemId);

    /**
     * Update a item
     *
     * @param listId       : id of the list which the item is belonging
     * @param itemToUpdate
     */
    protected abstract void processUpdate(UUID listId, ShoppingItem itemToUpdate);

    /**
     * Delete a item by its Id
     *
     * @param listId : id of the list which the item is belonging
     * @param userId
     */
    protected abstract void processDeleteById(UUID listId, UUID itemId);
}
