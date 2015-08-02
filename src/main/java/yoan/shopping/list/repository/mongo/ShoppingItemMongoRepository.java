package yoan.shopping.list.repository.mongo;

import static java.util.Objects.requireNonNull;
import static yoan.shopping.infra.db.mongo.MongoDocumentConverter.FIELD_ID;
import static yoan.shopping.infra.rest.error.Level.INFO;
import static yoan.shopping.list.repository.ShoppingItemRepositoryErrorMessage.PROBLEM_CREATION_ITEM;
import static yoan.shopping.list.repository.ShoppingItemRepositoryErrorMessage.PROBLEM_CREATION_ITEM_ALREADY_EXISTS;
import static yoan.shopping.list.repository.ShoppingItemRepositoryErrorMessage.PROBLEM_DELETE_ITEM;
import static yoan.shopping.list.repository.ShoppingItemRepositoryErrorMessage.PROBLEM_UPDATE_ITEM;
import static yoan.shopping.list.repository.mongo.ShoppingListMongoConverter.FIELD_ITEM_LIST;
import static yoan.shopping.list.repository.mongo.ShoppingListMongoConverter.FIELD_LAST_UPDATE;
import static yoan.shopping.list.repository.mongo.ShoppingListMongoRepository.LIST_COLLECTION;

import java.time.LocalDateTime;
import java.util.UUID;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import yoan.shopping.infra.db.Dbs;
import yoan.shopping.infra.db.mongo.MongoDbConnectionFactory;
import yoan.shopping.infra.util.error.ApplicationException;
import yoan.shopping.infra.util.error.RepositoryErrorCode;
import yoan.shopping.infra.util.helper.DateHelper;
import yoan.shopping.infra.util.helper.MongoRepositoryHelper;
import yoan.shopping.list.ShoppingItem;
import yoan.shopping.list.ShoppingList;
import yoan.shopping.list.repository.ShoppingItemRepository;
import yoan.shopping.list.repository.ShoppingListRepository;

/**
 * Mongo implementation of the shopping item repository
 * @author yoan
 */
@Singleton
public class ShoppingItemMongoRepository extends ShoppingItemRepository {
	
	private final MongoCollection<ShoppingList> listCollection;
	private final ShoppingItemMongoConverter itemConverter;
	private final ShoppingListRepository listRepository;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ShoppingItemMongoRepository.class);
	private static final String FIELD_ITEM_ID_IN_LIST = FIELD_ITEM_LIST + '.' + FIELD_ID;
	
	@Inject
	public ShoppingItemMongoRepository(MongoDbConnectionFactory mongoConnectionFactory, ShoppingListRepository listRepository) {
		requireNonNull(mongoConnectionFactory);
		listCollection = mongoConnectionFactory.getCollection(Dbs.SHOPPING, LIST_COLLECTION, ShoppingList.class);
		this.listRepository = requireNonNull(listRepository);
		itemConverter = new ShoppingItemMongoConverter();
	}
	
	@Override
	protected void processCreate(UUID listId, ShoppingItem itemToCreate) {
		//ensure list exists before updating it
		listRepository.findList(listId);
		ensureItemNotExists(listId, itemToCreate);
		
		Bson filter = Filters.eq(FIELD_ID, listId);
		Document addItem = new Document("$addToSet", new Document(FIELD_ITEM_LIST, itemConverter.toDocument(itemToCreate)));
		addItem.append("$set", new Document(FIELD_LAST_UPDATE, DateHelper.toDate(itemToCreate.getCreationDate())));
		try {
			listCollection.updateOne(filter, addItem);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_CREATION_ITEM);
		}
	}

	private void ensureItemNotExists(UUID listId, ShoppingItem itemToCreate) {
		ShoppingItem item = getById(listId, itemToCreate.getId());
		if (item != null) {
			throw new ApplicationException(INFO, RepositoryErrorCode.ALREADY_EXISTING, PROBLEM_CREATION_ITEM_ALREADY_EXISTS.getDevReadableMessage(itemToCreate.getId()));
		}
	}

	@Override
	protected ShoppingItem processGetById(UUID listId, UUID itemId) {
		ShoppingList list = listRepository.findList(listId);
		for (ShoppingItem item : list.getItemList()) {
			if (item.getId().equals(itemId)) {
				return item;
			}
		}
		return null;
	}

	@Override
	protected void processUpdate(UUID listId, ShoppingItem itemToUpdate) {
		Bson filter = Filters.and(Filters.eq(FIELD_ITEM_ID_IN_LIST, itemToUpdate.getId()), Filters.eq(FIELD_ITEM_ID_IN_LIST, itemToUpdate.getId()));
		Bson updatedItem = itemConverter.toDocument(itemToUpdate);
		Document set = new Document(FIELD_ITEM_LIST + ".$", updatedItem).append(ShoppingListMongoConverter.FIELD_LAST_UPDATE, DateHelper.toDate(itemToUpdate.getLastUpdate()));
		Document update = new Document("$set", set);
		try {
			listCollection.updateOne(filter, update);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_UPDATE_ITEM);
		}
	}

	@Override
	protected void processDeleteById(UUID listId, UUID itemId) {
		Bson filter = Filters.eq(FIELD_ID, listId);
		Document pullItem = new Document("$pull", new Document(FIELD_ITEM_LIST, new Document(FIELD_ID, itemId)));
		pullItem.append("$set", new Document(ShoppingListMongoConverter.FIELD_LAST_UPDATE, DateHelper.toDate(LocalDateTime.now())));
		try {
			listCollection.updateOne(filter, pullItem);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_DELETE_ITEM);
		}
	}
}
