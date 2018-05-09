package io.tyoras.shopping.authentication.repository.mongo;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import io.tyoras.shopping.authentication.repository.OAuth2AuthorizationCode;
import io.tyoras.shopping.authentication.repository.OAuth2AuthorizationCodeRepository;
import io.tyoras.shopping.infra.db.Dbs;
import io.tyoras.shopping.infra.db.mongo.MongoDbConnectionFactory;
import io.tyoras.shopping.infra.db.mongo.MongoIndexEnsurer;
import io.tyoras.shopping.infra.util.helper.MongoRepositoryHelper;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static io.tyoras.shopping.authentication.repository.OAuth2AuthorizationCodeRepositoryErrorMessage.*;
import static io.tyoras.shopping.authentication.repository.mongo.OAuth2AuthorizationCodeMongoConverter.FIELD_CODE;
import static io.tyoras.shopping.authentication.repository.mongo.OAuth2AuthorizationCodeMongoConverter.FIELD_CREATED;
import static io.tyoras.shopping.infra.db.mongo.MongoIndexEnsurer.SortOrder.ASCENDING;
import static io.tyoras.shopping.infra.db.mongo.MongoIndexEnsurer.SortOrder.DESCENDING;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * Mongo implementation of the OAuth2 authorization code repository
 *
 * @author yoan
 */
@Singleton
public class OAuth2AuthorizationCodeMongoRepository extends OAuth2AuthorizationCodeRepository {

    public static final String AUTHZ_CODE_COLLECTION = "authzCode";
    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2AuthorizationCodeMongoRepository.class);
    private final MongoCollection<OAuth2AuthorizationCode> authCodeCollection;
    private final OAuth2AuthorizationCodeMongoConverter authCodeConverter;

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
        OAuth2AuthorizationCode foundAuthCode = null;
        try {
            foundAuthCode = authCodeCollection.find().filter(filter).first();
        } catch (MongoException e) {
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
        } catch (MongoException e) {
            MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_CREATION_AUTH_CODE);
        }
    }

    @Override
    protected void processDeleteByCode(String authzCode) {
        Bson filter = authCodeConverter.filterByCode(authzCode);
        try {
            authCodeCollection.deleteOne(filter);
        } catch (MongoException e) {
            MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_DELETE_AUTH_CODE);
        }
    }
}
