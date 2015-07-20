/**
 * 
 */
package yoan.shopping.user.repository.mongo;

import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.util.error.CommonErrorCode.APPLICATION_ERROR;
import static yoan.shopping.user.repository.UserRepositoryErrorMessage.PROBLEM_CREATION_USER;
import static yoan.shopping.user.repository.UserRepositoryErrorMessage.PROBLEM_UPDATE_USER;

import java.util.UUID;

import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yoan.shopping.infra.db.Dbs;
import yoan.shopping.infra.db.mongo.MongoDbConnectionFactory;
import yoan.shopping.infra.util.error.ApplicationException;
import yoan.shopping.user.User;
import yoan.shopping.user.repository.UserRepository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

/**
 * Mongo implementation of the user repository
 * @author yoan
 */
@Singleton
public class UserMongoRepository extends UserRepository {
	public static final String USER_COLLECTION = "users";
	
	private final UserMongoConverter userConverter;
	private final MongoCollection<User> userCollection;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserMongoRepository.class);
	
	@Inject
	public UserMongoRepository(MongoDbConnectionFactory mongoConnectionFactory) {
		userCollection = mongoConnectionFactory.getCollection(Dbs.SHOPPING, USER_COLLECTION, User.class);
		userConverter = new UserMongoConverter();
	}
	
	@Override
	protected void processCreate(User user) {
		try {
			userCollection.insertOne(user);
		} catch(MongoException e) {
			String message = PROBLEM_CREATION_USER.getDevReadableMessage(e.getMessage());
			LOGGER.error(message, e);
			throw new ApplicationException(ERROR, APPLICATION_ERROR, message, e);
		}
	}

	@Override
	protected User processGetById(UUID userId) {
		Bson filter = Filters.eq("_id", userId);
		return userCollection.find().filter(filter).first();
	}
	
	@Override
	protected void processUpdate(User user) {
		Bson filter = Filters.eq("_id", user.getId());
		Bson update = userConverter.getUserUpdate(user);
		try {
			userCollection.updateOne(filter, update);
		} catch(MongoException e) {
			String message = PROBLEM_UPDATE_USER.getDevReadableMessage(e.getMessage());
			LOGGER.error(message, e);
			throw new ApplicationException(ERROR, APPLICATION_ERROR, message, e);
		}
	}
	
	@Override
	protected void processDeleteById(UUID userId) {
		Bson filter = Filters.eq("_id", userId);
		userCollection.deleteOne(filter);
	}
}
