package yoan.shopping.user.repository.mongo;

import static yoan.shopping.infra.db.mongo.MongoDocumentConverter.FIELD_ID;
import static yoan.shopping.infra.db.mongo.MongoIndexEnsurer.SortOrder.ASCENDING;
import static yoan.shopping.infra.rest.error.Level.INFO;
import static yoan.shopping.user.repository.UserRepositoryErrorCode.TOO_MUCH_RESULT;
import static yoan.shopping.user.repository.UserRepositoryErrorMessage.PROBLEM_CREATION_USER;
import static yoan.shopping.user.repository.UserRepositoryErrorMessage.PROBLEM_DELETE_USER;
import static yoan.shopping.user.repository.UserRepositoryErrorMessage.PROBLEM_READ_USER;
import static yoan.shopping.user.repository.UserRepositoryErrorMessage.PROBLEM_SEARCH_USER;
import static yoan.shopping.user.repository.UserRepositoryErrorMessage.PROBLEM_UPDATE_USER;
import static yoan.shopping.user.repository.UserRepositoryErrorMessage.TOO_MUCH_RESULT_FOR_SEARCH;
import static yoan.shopping.user.repository.mongo.UserMongoConverter.FIELD_EMAIL;
import static yoan.shopping.user.repository.mongo.UserMongoConverter.FIELD_NAME;
import static yoan.shopping.user.repository.mongo.UserMongoConverter.FIELD_PROFILE_VISIBILITY;

import java.util.List;
import java.util.UUID;

import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import yoan.shopping.infra.db.Dbs;
import yoan.shopping.infra.db.mongo.MongoDbConnectionFactory;
import yoan.shopping.infra.db.mongo.MongoIndexEnsurer;
import yoan.shopping.infra.util.error.ApplicationException;
import yoan.shopping.infra.util.helper.MongoRepositoryHelper;
import yoan.shopping.user.ProfileVisibility;
import yoan.shopping.user.User;
import yoan.shopping.user.repository.UserRepository;

/**
 * Mongo implementation of the user repository
 * @author yoan
 */
@Singleton
public class UserMongoRepository extends UserRepository {
	public static final String USER_COLLECTION = "users";
	
	private final MongoCollection<User> userCollection;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserMongoRepository.class);
	
	@Inject
	public UserMongoRepository(MongoDbConnectionFactory mongoConnectionFactory) {
		userCollection = mongoConnectionFactory.getCollection(Dbs.SHOPPING, USER_COLLECTION, User.class);
		ensureIndexes();
	}
	
	private void ensureIndexes() {
		MongoIndexEnsurer indexEnsurer = new MongoIndexEnsurer(userCollection);
		indexEnsurer.logStartEnsuringIndexes();
		
		indexEnsurer.ensureUniqueIndex(FIELD_EMAIL, ASCENDING);
		indexEnsurer.ensureMultiKeyIndex(ImmutableMap.of(FIELD_PROFILE_VISIBILITY, ASCENDING, FIELD_NAME, ASCENDING));
		
		indexEnsurer.logEndEnsuringIndexes();
	}
	
	@Override
	protected void processCreate(User user) {
		try {
			userCollection.insertOne(user);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_CREATION_USER);
		}
	}

	@Override
	protected User processGetById(UUID userId) {
		Bson filter = Filters.eq(FIELD_ID, userId);
		User foundUser = null;
		try {
			foundUser = userCollection.find().filter(filter).first();
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_READ_USER);
		}
		return foundUser;
	}
	
	@Override
	protected void processUpdate(User user) {
		Bson filter = Filters.eq(FIELD_ID, user.getId());
		Bson update = UserMongoConverter.getUserUpdate(user);
		try {
			userCollection.updateOne(filter, update);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_UPDATE_USER);
		}
	}
	
	@Override
	protected void processDeleteById(UUID userId) {
		Bson filter = Filters.eq(FIELD_ID, userId);
		try {
			userCollection.deleteOne(filter);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_DELETE_USER);
		}
	}

	@Override
	protected User processGetByEmail(String email) {
		Bson filter = Filters.eq(FIELD_EMAIL, email);
		User foundUser = null;
		try {
			foundUser = userCollection.find().filter(filter).first();
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_READ_USER);
		}
		return foundUser;
	}

	@Override
	protected long countByIdOrEmail(UUID userId, String email) {
		Bson filter = Filters.or(Filters.eq(FIELD_ID, userId), Filters.eq(FIELD_EMAIL, email));
		long count = 0;
		try {
			
			count = userCollection.count(filter);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_READ_USER);
		}
		return count;
	}

	@Override
	protected ImmutableList<User> processSearchByName(ProfileVisibility visibility, int nbMaxResult, String search) {
		Bson visibilityFilter = Filters.eq(FIELD_PROFILE_VISIBILITY, visibility.name());
		Bson nameSearchFilter = Filters.regex(FIELD_NAME, search);
		Bson filter = Filters.and(visibilityFilter, nameSearchFilter);
		List<User> users = Lists.newArrayList();
		try {
			long count = userCollection.count(filter);
			if (count > nbMaxResult) {
				throw new ApplicationException(INFO, TOO_MUCH_RESULT, TOO_MUCH_RESULT_FOR_SEARCH.getDevReadableMessage(count, search));
			}
			
			users = userCollection.find().filter(filter).into(users);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_SEARCH_USER);
		}
		return ImmutableList.copyOf(users);
	}
	
	
}
