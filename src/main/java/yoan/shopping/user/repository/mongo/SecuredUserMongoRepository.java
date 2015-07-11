/**
 * 
 */
package yoan.shopping.user.repository.mongo;

import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.util.error.CommonErrorCode.APPLICATION_ERROR;
import static yoan.shopping.user.repository.UserRepositoryErrorMessage.PROBLEM_CREATION_USER;
import static yoan.shopping.user.repository.UserRepositoryErrorMessage.PROBLEM_UPDATE_USER_PASSWORD;
import static yoan.shopping.user.repository.mongo.UserMongoRepository.USER_COLLECTION;

import java.util.UUID;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yoan.shopping.infra.db.Dbs;
import yoan.shopping.infra.db.mongo.MongoDbConnectionFactory;
import yoan.shopping.infra.util.error.ApplicationException;
import yoan.shopping.user.SecuredUser;
import yoan.shopping.user.repository.SecuredUserRepository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

/**
 * Mongo implementation of the user with security information repository 
 * @author yoan
 */
@Singleton
public class SecuredUserMongoRepository extends SecuredUserRepository {

	private final SecuredUserMongoConverter userConverter;
	private final MongoCollection<Document> userCollection;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SecuredUserMongoRepository.class);
	
	@Inject
	public SecuredUserMongoRepository(MongoDbConnectionFactory mongoConnectionFactory) {
		userCollection = mongoConnectionFactory.getCollection(Dbs.SHOPPING , USER_COLLECTION);
		userConverter = new SecuredUserMongoConverter();
	}
	
	@Override
	protected void processCreate(SecuredUser user) {
		Document doc = userConverter.toDocument(user);
		try {
			userCollection.insertOne(doc);
		} catch(MongoException e) {
			String message = PROBLEM_CREATION_USER.getDevReadableMessage(e.getMessage());
			LOGGER.error(message, e);
			throw new ApplicationException(ERROR, APPLICATION_ERROR, message, e);
		}
	}

	@Override
	protected SecuredUser processGetById(UUID userId) {
		Bson filter = Filters.eq("_id", userId.toString());
		Document result = userCollection.find().filter(filter).first();
		
		return userConverter.fromDocument(result);
	}

	@Override
	protected void processChangePassword(SecuredUser userToUpdate) {
		Bson filter = Filters.eq("_id", userToUpdate.getId().toString());
		Bson update = userConverter.getChangePasswordUpdate(userToUpdate);
		try {
			userCollection.updateOne(filter, update);
		} catch(MongoException e) {
			String message = PROBLEM_UPDATE_USER_PASSWORD.getDevReadableMessage(e.getMessage());
			LOGGER.error(message, e);
			throw new ApplicationException(ERROR, APPLICATION_ERROR, message, e);
		}
	}
}