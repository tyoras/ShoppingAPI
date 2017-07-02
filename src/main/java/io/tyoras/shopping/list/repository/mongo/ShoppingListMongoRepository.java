/**
 * 
 */
package io.tyoras.shopping.list.repository.mongo;

import static io.tyoras.shopping.infra.db.mongo.MongoDocumentConverter.FIELD_ID;
import static io.tyoras.shopping.list.repository.ShoppingListRepositoryErrorMessage.PROBLEM_CREATION_LIST;
import static io.tyoras.shopping.list.repository.ShoppingListRepositoryErrorMessage.PROBLEM_DELETE_LIST;
import static io.tyoras.shopping.list.repository.ShoppingListRepositoryErrorMessage.PROBLEM_READ_LIST;
import static io.tyoras.shopping.list.repository.ShoppingListRepositoryErrorMessage.PROBLEM_READ_USER_LISTS;
import static io.tyoras.shopping.list.repository.ShoppingListRepositoryErrorMessage.PROBLEM_UPDATE_LIST;
import static io.tyoras.shopping.list.repository.mongo.ShoppingListMongoConverter.FIELD_OWNER_ID;

import java.util.List;
import java.util.UUID;

import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import io.tyoras.shopping.infra.db.Dbs;
import io.tyoras.shopping.infra.db.mongo.MongoDbConnectionFactory;
import io.tyoras.shopping.infra.util.helper.MongoRepositoryHelper;
import io.tyoras.shopping.list.ShoppingList;
import io.tyoras.shopping.list.repository.ShoppingListRepository;

/**
 * Mongo implementation of the shopping list repository
 * @author yoan
 */
@Singleton
public class ShoppingListMongoRepository extends ShoppingListRepository {
	public static final String LIST_COLLECTION = "list";
	
	private final MongoCollection<ShoppingList> listCollection;
	private final ShoppingListMongoConverter listConverter;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ShoppingListMongoRepository.class);
	
	@Inject
	public ShoppingListMongoRepository(MongoDbConnectionFactory mongoConnectionFactory) {
		listCollection = mongoConnectionFactory.getCollection(Dbs.SHOPPING, LIST_COLLECTION, ShoppingList.class);
		listConverter = new ShoppingListMongoConverter();
	}
	
	@Override
	protected void processCreate(ShoppingList listToCreate) {
		try {
			listCollection.insertOne(listToCreate);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_CREATION_LIST);
		}
	}

	@Override
	protected ShoppingList processGetById(UUID listId) {
		Bson filter = Filters.eq(FIELD_ID, listId);
		ShoppingList foundList = null;
		try {
			foundList = listCollection.find().filter(filter).first();
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_READ_LIST);
		}
		return foundList;
	}

	@Override
	protected void processUpdate(ShoppingList listToUpdate) {
		Bson filter = Filters.eq(FIELD_ID, listToUpdate.getId());
		Bson update = listConverter.getListUpdate(listToUpdate);
		try {
			listCollection.updateOne(filter, update);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_UPDATE_LIST);
		}
	}

	@Override
	protected void processDeleteById(UUID listId) {
		Bson filter = Filters.eq(FIELD_ID, listId);
		try {
			listCollection.deleteOne(filter);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_DELETE_LIST);
		}
	}

	@Override
	protected ImmutableList<ShoppingList> processGetByOwner(UUID ownerId) {
		Bson filter = Filters.eq(FIELD_OWNER_ID, ownerId);
		List<ShoppingList> lists = Lists.newArrayList();
		try {
			lists = listCollection.find().filter(filter).into(lists);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_READ_USER_LISTS);
		}
		return ImmutableList.<ShoppingList>copyOf(lists);
	}

}
