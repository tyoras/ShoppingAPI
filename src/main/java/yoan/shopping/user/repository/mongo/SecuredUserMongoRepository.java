/**
 * 
 */
package yoan.shopping.user.repository.mongo;

import static yoan.shopping.user.repository.UserRepositoryErrorMessage.PROBLEM_CREATION_USER;
import static yoan.shopping.user.repository.UserRepositoryErrorMessage.PROBLEM_READ_USER;
import static yoan.shopping.user.repository.UserRepositoryErrorMessage.PROBLEM_UPDATE_USER_PASSWORD;
import static yoan.shopping.user.repository.mongo.UserMongoConverter.*;
import static yoan.shopping.user.repository.mongo.UserMongoRepository.USER_COLLECTION;

import java.util.UUID;

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
import yoan.shopping.infra.util.helper.MongoRepositoryHelper;
import yoan.shopping.user.SecuredUser;
import yoan.shopping.user.repository.SecuredUserRepository;

/**
 * Mongo implementation of the user with security information repository 
 * @author yoan
 */
@Singleton
public class SecuredUserMongoRepository extends SecuredUserRepository {

	private final SecuredUserMongoConverter userConverter;
	private final MongoCollection<SecuredUser> userCollection;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SecuredUserMongoRepository.class);
	
	@Inject
	public SecuredUserMongoRepository(MongoDbConnectionFactory mongoConnectionFactory) {
		userCollection = mongoConnectionFactory.getCollection(Dbs.SHOPPING , USER_COLLECTION, SecuredUser.class);
		userConverter = new SecuredUserMongoConverter();
	}
	
	@Override
	protected void processCreate(SecuredUser user) {
		try {
			userCollection.insertOne(user);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_CREATION_USER);
		}
	}

	@Override
	protected SecuredUser processGetById(UUID userId) {
		Bson filter = Filters.eq(FIELD_ID, userId);
		SecuredUser foundUser = null;
		try {
			foundUser = userCollection.find().filter(filter).first();
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_READ_USER);
		}
		
		return foundUser;
	}

	@Override
	protected void processChangePassword(SecuredUser userToUpdate) {
		Bson filter = Filters.eq("_id", userToUpdate.getId());
		Bson update = userConverter.getChangePasswordUpdate(userToUpdate);
		try {
			userCollection.updateOne(filter, update);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_UPDATE_USER_PASSWORD);
		}
	}

	@Override
	protected SecuredUser processGetByEmail(String userEmail) {
		Bson filter = Filters.eq(FIELD_EMAIL, userEmail);
		SecuredUser foundUser = null;
		try {
			foundUser = userCollection.find().filter(filter).first();
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_READ_USER);
		}
		
		return foundUser;
	}
}