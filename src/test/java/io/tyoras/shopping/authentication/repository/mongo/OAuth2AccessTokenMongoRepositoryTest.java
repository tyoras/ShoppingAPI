package io.tyoras.shopping.authentication.repository.mongo;

import com.mongodb.client.MongoCollection;
import io.tyoras.shopping.authentication.repository.OAuth2AccessToken;
import io.tyoras.shopping.infra.util.error.ApplicationException;
import io.tyoras.shopping.test.fongo.FongoBackedTest;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.UUID;

import static io.tyoras.shopping.authentication.repository.OAuth2AccessTokenRepositoryErrorMessage.PROBLEM_CREATION_ACCESS_TOKEN;
import static io.tyoras.shopping.authentication.repository.mongo.OAuth2AccessTokenMongoRepository.ACCESS_TOKEN_COLLECTION;
import static io.tyoras.shopping.infra.db.Dbs.SHOPPING;
import static org.assertj.core.api.Assertions.assertThat;

public class OAuth2AccessTokenMongoRepositoryTest extends FongoBackedTest {

    private final OAuth2AccessTokenMongoConverter converter = new OAuth2AccessTokenMongoConverter();
    private final MongoCollection<Document> accessTokenCollection = getFongoDbConnectionFactory().getCollection(SHOPPING, ACCESS_TOKEN_COLLECTION);

    @InjectMocks
    OAuth2AccessTokenMongoRepository testedRepo;

    @Test
    public void create_should_work() {
        //given
        String accessToken = "token";
        UUID userId = UUID.randomUUID();

        //when
        testedRepo.create(accessToken, userId);

        //then
        Bson filter = converter.filterByToken(accessToken);
        Document result = accessTokenCollection.find().filter(filter).first();
        OAuth2AccessToken found = converter.fromDocument(result);

        String foundToken = found.getToken();
        UUID foundUserId = found.getuserId();
        assertThat(foundToken).isEqualTo(accessToken);
        assertThat(foundUserId).isEqualTo(userId);
    }

    @Ignore //Fongo does not use the indexes
    @Test(expected = ApplicationException.class)
    public void create_should_fail_with_already_existing_access_token() {
        //given
        String accessToken = "token";
        UUID userId = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        testedRepo.create(accessToken, userId);
        Bson filter = converter.filterByToken(accessToken);
        Document foundDoc = accessTokenCollection.find().filter(filter).first();
        OAuth2AccessToken existing = converter.fromDocument(foundDoc);

        //when
        try {
            testedRepo.create(accessToken, userId2);
        } catch (ApplicationException ae) {
            //then
            assertThat(ae.getMessage()).contains(PROBLEM_CREATION_ACCESS_TOKEN.getDevReadableMessage(""));
            throw ae;
        } finally {
            //existing should still exists with the initial userId
            Document result = accessTokenCollection.find().filter(filter).first();
            OAuth2AccessToken found = converter.fromDocument(result);
            assertThat(found).isEqualTo(existing);
        }
    }

    @Test
    public void getUserIdByAccessToken_should_return_null_with_not_existing_access_token() {
        //given
        String notExistingCode = "not existing token";

        //when
        UUID result = testedRepo.getUserIdByAccessToken(notExistingCode);

        //then
        assertThat(result).isNull();
    }

    @Test
    public void getUserIdByAccessToken_should_work_with_existing_access_token() {
        //given
        String existingAuthCode = "token";
        UUID expectedUserId = UUID.randomUUID();
        testedRepo.create(existingAuthCode, expectedUserId);

        //when
        UUID result = testedRepo.getUserIdByAccessToken(existingAuthCode);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedUserId);
    }

    @Test
    public void deleteByToken_should_not_fail_with_not_existing_access_token() {
        //given
        String notExistingCode = "not existing token";

        //when
        testedRepo.deleteByAccessToken(notExistingCode);

        //then
        //should not have failed
    }

    @Test
    public void deleteByToken_should_work_with_existing_access_token() {
        //given
        String existingAuthCode = "token";
        testedRepo.create(existingAuthCode, UUID.randomUUID());

        //when
        testedRepo.deleteByAccessToken(existingAuthCode);

        //then
        UUID result = testedRepo.getUserIdByAccessToken(existingAuthCode);
        assertThat(result).isNull();
    }
}