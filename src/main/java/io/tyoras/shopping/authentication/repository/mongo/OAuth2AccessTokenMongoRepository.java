package io.tyoras.shopping.authentication.repository.mongo;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import io.tyoras.shopping.authentication.repository.OAuth2AccessToken;
import io.tyoras.shopping.authentication.repository.OAuth2AccessTokenRepository;
import io.tyoras.shopping.infra.db.Dbs;
import io.tyoras.shopping.infra.db.mongo.MongoDbConnectionFactory;
import io.tyoras.shopping.infra.db.mongo.MongoIndexEnsurer;
import io.tyoras.shopping.infra.util.helper.MongoRepositoryHelper;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static io.tyoras.shopping.authentication.repository.OAuth2AccessTokenRepositoryErrorMessage.*;
import static io.tyoras.shopping.authentication.repository.mongo.OAuth2AccessTokenMongoConverter.FIELD_CREATED;
import static io.tyoras.shopping.authentication.repository.mongo.OAuth2AccessTokenMongoConverter.FIELD_TOKEN;
import static io.tyoras.shopping.infra.db.mongo.MongoIndexEnsurer.SortOrder.ASCENDING;
import static io.tyoras.shopping.infra.db.mongo.MongoIndexEnsurer.SortOrder.DESCENDING;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * Mongo implementation of the OAuth2 access token repository
 *
 * @author yoan
 */
@Singleton
public class OAuth2AccessTokenMongoRepository extends OAuth2AccessTokenRepository {
    public static final String ACCESS_TOKEN_COLLECTION = "accessToken";
    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2AccessTokenMongoRepository.class);
    private final MongoCollection<OAuth2AccessToken> accessTokenCollection;
    private final OAuth2AccessTokenMongoConverter accessTokenConverter;

    @Inject
    public OAuth2AccessTokenMongoRepository(MongoDbConnectionFactory mongoConnectionFactory) {
        requireNonNull(mongoConnectionFactory);
        accessTokenCollection = mongoConnectionFactory.getCollection(Dbs.SHOPPING, ACCESS_TOKEN_COLLECTION, OAuth2AccessToken.class);
        accessTokenConverter = new OAuth2AccessTokenMongoConverter();
        ensureIndexes();
    }

    private void ensureIndexes() {
        MongoIndexEnsurer indexEnsurer = new MongoIndexEnsurer(accessTokenCollection);
        indexEnsurer.logStartEnsuringIndexes();

        indexEnsurer.ensureUniqueIndex(FIELD_TOKEN, ASCENDING);
        indexEnsurer.ensureTTLIndex(FIELD_CREATED, DESCENDING, ACCESS_TOKEN_TTL_IN_MINUTES, MINUTES);

        indexEnsurer.logEndEnsuringIndexes();
    }

    @Override
    protected UUID processGetUserIdByAccessToken(String accessToken) {
        Bson filter = accessTokenConverter.filterByToken(accessToken);
        OAuth2AccessToken foundAccessToken = null;
        try {
            foundAccessToken = accessTokenCollection.find().filter(filter).first();
        } catch (MongoException e) {
            MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_READ_ACCESS_TOKEN);
        }
        return foundAccessToken == null ? null : foundAccessToken.getuserId();
    }

    @Override
    protected void processCreate(String accessToken, UUID userId) {
        OAuth2AccessToken accessTokenToCreate = OAuth2AccessToken.Builder.createDefault()
                .withRandomId()
                .withToken(accessToken)
                .withUserId(userId)
                .build();
        try {
            accessTokenCollection.insertOne(accessTokenToCreate);
        } catch (MongoException e) {
            MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_CREATION_ACCESS_TOKEN);
        }
    }

    @Override
    protected void processDeleteByAccessToken(String accessToken) {
        Bson filter = accessTokenConverter.filterByToken(accessToken);
        try {
            accessTokenCollection.deleteOne(filter);
        } catch (MongoException e) {
            MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_DELETE_ACCESS_TOKEN);
        }
    }
}
