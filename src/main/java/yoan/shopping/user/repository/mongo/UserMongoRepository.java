/**
 * 
 */
package yoan.shopping.user.repository.mongo;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static yoan.shopping.infra.config.guice.ShoppingModule.*;
import yoan.shopping.infra.db.Dbs;
import yoan.shopping.infra.db.mongo.MongoDbConnectionFactory;
import yoan.shopping.infra.util.ApplicationException;
import yoan.shopping.user.User;
import yoan.shopping.user.repository.UserRepository;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;

/**
 *
 * @author yoan
 */
public class UserMongoRepository extends UserRepository {
	public static final String USER_COLLECTION = "users";
	
	private final UserMongoConverter userConverter;
	private final MongoCollection<Document> userCollection = MongoDbConnectionFactory.getCollection(Dbs.SHOPPING ,USER_COLLECTION);
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserMongoRepository.class);
	
	@Inject
	public UserMongoRepository(@Named(CONNECTED_USER) User connectedUser) {
		super(connectedUser);
		userConverter = new UserMongoConverter();
	}
	
	@Override
	public void create(User user) {
		Document doc = userConverter.toDocument(user);
		try {
			userCollection.insertOne(doc);
		} catch(MongoException e) {
			LOGGER.error("Error while creating user : " + e.getMessage(), e);
			throw new ApplicationException("Error while creating user : " + e.getMessage(), e);
		}
	}

}
