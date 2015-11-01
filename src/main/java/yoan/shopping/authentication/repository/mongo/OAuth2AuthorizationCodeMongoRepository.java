package yoan.shopping.authentication.repository.mongo;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MINUTES;
import static yoan.shopping.authentication.repository.OAuth2AuthorizationCodeRepositoryErrorMessage.PROBLEM_CREATION_AUTH_CODE;
import static yoan.shopping.authentication.repository.OAuth2AuthorizationCodeRepositoryErrorMessage.PROBLEM_DELETE_AUTH_CODE;
import static yoan.shopping.authentication.repository.OAuth2AuthorizationCodeRepositoryErrorMessage.PROBLEM_READ_AUTH_CODE;
import static yoan.shopping.authentication.repository.mongo.OAuth2AuthorizationCodeMongoConverter.FIELD_CODE;
import static yoan.shopping.authentication.repository.mongo.OAuth2AuthorizationCodeMongoConverter.FIELD_CREATED;
import static yoan.shopping.infra.db.mongo.MongoIndexEnsurer.SortOrder.ASCENDING;
import static yoan.shopping.infra.db.mongo.MongoIndexEnsurer.SortOrder.DESCENDING;

import java.util.UUID;

import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;

import yoan.shopping.authentication.repository.OAuth2AuthorizationCode;
import yoan.shopping.authentication.repository.OAuth2AuthorizationCodeRepository;
import yoan.shopping.infra.db.Dbs;
import yoan.shopping.infra.db.mongo.MongoDbConnectionFactory;
import yoan.shopping.infra.db.mongo.MongoIndexEnsurer;
import yoan.shopping.infra.util.helper.MongoRepositoryHelper;

/**
 * Mongo implementation of the OAuth2 authorization code repository
 * @author yoan
 */
@Singleton
public class OAuth2AuthorizationCodeMongoRepository extends OAuth2AuthorizationCodeRepository {
	
	public static final String AUTHZ_CODE_COLLECTION = "authzCode";
	
	private final MongoCollection<OAuth2AuthorizationCode> authCodeCollection;
	private final OAuth2AuthorizationCodeMongoConverter authCodeConverter;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2AuthorizationCodeMongoRepository.class);
	
	@Inject
	public OAuth2AuthorizationCodeMongoRepository(MongoDbConnectionFactory mongoConnectionFactory) {
		requireNonNull(mongoConnectionFactory);
		authCodeCollection = mongoConnectionFactory.getCollection(Dbs.SHOPPING, AUTHZ_CODE_COLLECTION, OAuth2AuthorizationCode.class);
		authCodeConverter = new OAuth2AuthorizationCodeMongoConverter();
		ensureIndexes();
	}
	
	private void ensureIndexes() {
		MongoIndexEnsurer indexEnsurer = new MongoIndexEnsurer(authCodeCollection);
		indexEnsurer.logStartEnsuringIndexes();
		
		indexEnsurer.ensureUniqueIndex(FIELD_CODE, ASCENDING);
		indexEnsurer.ensureTTLIndex(FIELD_CREATED, DESCENDING, AUTH_CODE_TTL_IN_MINUTES, MINUTES);
		
		indexEnsurer.logEndEnsuringIndexes();
	}
	
	@Override
	protected UUID processGetUserIdByAuthorizationCode(String authzCode) {
		Bson filter = authCodeConverter.filterByCode(authzCode);
		OAuth2AuthorizationCode foundAuthCode= null;
		try {
			foundAuthCode = authCodeCollection.find().filter(filter).first();
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_READ_AUTH_CODE);
		}
		return foundAuthCode == null ? null : foundAuthCode.getuserId();
	}

	@Override
	protected void processCreate(String authzCode, UUID userId) {
		OAuth2AuthorizationCode authCodeToCreate = OAuth2AuthorizationCode.Builder.createDefault()
			.withRandomId()
			.withCode(authzCode)
			.withUserId(userId)
			.build();
		try {
			authCodeCollection.insertOne(authCodeToCreate);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_CREATION_AUTH_CODE);
		}
	}

	@Override
	protected void processDeleteByCode(String authzCode) {
		Bson filter = authCodeConverter.filterByCode(authzCode);
		try {
			authCodeCollection.deleteOne(filter);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_DELETE_AUTH_CODE);
		}
	}
}
