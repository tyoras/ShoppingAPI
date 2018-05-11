package io.tyoras.shopping.authentication.repository.mongo;

import com.mongodb.client.MongoCollection;
import io.tyoras.shopping.authentication.repository.OAuth2AuthorizationCode;
import io.tyoras.shopping.infra.util.error.ApplicationException;
import io.tyoras.shopping.test.fongo.FongoBackedTest;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.UUID;

import static io.tyoras.shopping.authentication.repository.OAuth2AuthorizationCodeRepositoryErrorMessage.PROBLEM_CREATION_AUTH_CODE;
import static io.tyoras.shopping.authentication.repository.mongo.OAuth2AuthorizationCodeMongoRepository.AUTHZ_CODE_COLLECTION;
import static io.tyoras.shopping.infra.db.Dbs.SHOPPING;
import static org.assertj.core.api.Assertions.assertThat;

public class OAuth2AuthorizationCodeMongoRepositoryTest extends FongoBackedTest {

    private final OAuth2AuthorizationCodeMongoConverter converter = new OAuth2AuthorizationCodeMongoConverter();
    private final MongoCollection<Document> authCodeCollection = getFongoDbConnectionFactory().getCollection(SHOPPING, AUTHZ_CODE_COLLECTION);

    @InjectMocks
    OAuth2AuthorizationCodeMongoRepository testedRepo;

    @Test
    public void create_should_work() {
        //given
        String authCode = "code";
        UUID userId = UUID.randomUUID();

        //when
        testedRepo.create(authCode, userId);

        //then
        Bson filter = converter.filterByCode(authCode);
        Document result = authCodeCollection.find().filter(filter).first();
        OAuth2AuthorizationCode found = converter.fromDocument(result);

        String foundCode = found.getCode();
        UUID foundUserId = found.getuserId();
        assertThat(foundCode).isEqualTo(authCode);
        assertThat(foundUserId).isEqualTo(userId);
    }

    @Ignore //Fongo does not use the indexes
    @Test(expected = ApplicationException.class)
    public void create_should_fail_with_already_existing_auth_code() {
        //given
        String authCode = "code";
        UUID userId = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        testedRepo.create(authCode, userId);
        Bson filter = converter.filterByCode(authCode);
        Document foundDoc = authCodeCollection.find().filter(filter).first();
        OAuth2AuthorizationCode existing = converter.fromDocument(foundDoc);

        //when
        try {
            testedRepo.create(authCode, userId2);
        } catch (ApplicationException ae) {
            //then
            assertThat(ae.getMessage()).contains(PROBLEM_CREATION_AUTH_CODE.getDevReadableMessage(""));
            throw ae;
        } finally {
            //existing should still exists with the initial userId
            Document result = authCodeCollection.find().filter(filter).first();
            OAuth2AuthorizationCode found = converter.fromDocument(result);
            assertThat(found).isEqualTo(existing);
        }
    }

    @Test
    public void getUserIdByAuthorizationCode_should_return_null_with_not_existing_auth_code() {
        //given
        String notExistingCode = "not existing code";

        //when
        UUID result = testedRepo.getUserIdByAuthorizationCode(notExistingCode);

        //then
        assertThat(result).isNull();
    }

    @Test
    public void getUserIdByAuthorizationCode_should_work_with_existing_auth_code() {
        //given
        String existingAuthCode = "code";
        UUID expectedUserId = UUID.randomUUID();
        testedRepo.create(existingAuthCode, expectedUserId);

        //when
        UUID result = testedRepo.getUserIdByAuthorizationCode(existingAuthCode);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedUserId);
    }

    @Test
    public void deleteByCode_should_not_fail_with_not_existing_auth_code() {
        //given
        String notExistingCode = "not existing code";

        //when
        testedRepo.deleteByCode(notExistingCode);

        //then
        //should not have failed
    }

    @Test
    public void deleteByCode_should_work_with_existing_auth_code() {
        //given
        String existingAuthCode = "code";
        testedRepo.create(existingAuthCode, UUID.randomUUID());

        //when
        testedRepo.deleteByCode(existingAuthCode);

        //then
        UUID result = testedRepo.getUserIdByAuthorizationCode(existingAuthCode);
        assertThat(result).isNull();
    }
}
