package io.tyoras.shopping.user.repository.mongo;

import io.tyoras.shopping.infra.util.error.ApplicationException;
import io.tyoras.shopping.infra.util.helper.DateHelper;
import io.tyoras.shopping.test.TestHelper;
import io.tyoras.shopping.user.ProfileVisibility;
import io.tyoras.shopping.user.SecuredUser;
import io.tyoras.shopping.user.User;
import org.bson.Document;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import static io.tyoras.shopping.infra.rest.error.Level.ERROR;
import static io.tyoras.shopping.infra.util.error.CommonErrorCode.APPLICATION_ERROR;
import static io.tyoras.shopping.user.ProfileVisibility.PUBLIC;
import static io.tyoras.shopping.user.repository.UserRepositoryErrorMessage.UNABLE_TO_CONVERT_UNSECURE_USER;
import static io.tyoras.shopping.user.repository.mongo.SecuredUserMongoConverter.*;
import static io.tyoras.shopping.user.repository.mongo.UserMongoConverter.*;
import static io.tyoras.shopping.user.repository.mongo.UserMongoConverter.FIELD_ID;
import static org.assertj.core.api.Assertions.assertThat;

public class SecuredUserMongoConverterTest {
    @Test
    public void fromDocument_should_return_null_with_null_document() {
        //given
        Document nullDoc = null;
        SecuredUserMongoConverter testedConverter = new SecuredUserMongoConverter();

        //when
        User result = testedConverter.fromDocument(nullDoc);

        //then
        assertThat(result).isNull();
    }

    @Test
    public void fromDocument_should_work_with_valid_doc() {
        //given
        UUID expectId = UUID.randomUUID();
        String expectedName = "name";
        String expectedMail = "mail";
        ProfileVisibility expectedProfileVisibility = PUBLIC;
        LocalDateTime expectedCreationDate = LocalDateTime.now();
        LocalDateTime expectedLastUpdate = LocalDateTime.now();
        String expectedPassword = "password";
        Object expectedSalt = UUID.randomUUID().toString();
        Document doc = new Document(FIELD_ID, expectId)
                .append(FIELD_NAME, expectedName)
                .append(FIELD_EMAIL, expectedMail)
                .append(FIELD_PROFILE_VISIBILITY, expectedProfileVisibility.name())
                .append(FIELD_CREATED, DateHelper.toDate(expectedCreationDate))
                .append(FIELD_LAST_UPDATE, DateHelper.toDate(expectedLastUpdate))
                .append(FIELD_SECURITY, new Document(FIELD_PASSWORD, expectedPassword)
                        .append(FIELD_SALT, expectedSalt));

        SecuredUserMongoConverter testedConverter = new SecuredUserMongoConverter();

        //when
        SecuredUser result = testedConverter.fromDocument(doc);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(expectId);
        assertThat(result.getName()).isEqualTo(expectedName);
        assertThat(result.getEmail()).isEqualTo(expectedMail);
        assertThat(result.getProfileVisibility()).isEqualTo(expectedProfileVisibility);
        assertThat(result.getCreationDate()).isEqualToIgnoringNanos(expectedCreationDate);
        assertThat(result.getLastUpdate()).isEqualToIgnoringNanos(expectedLastUpdate);
        assertThat(result.getPassword()).isEqualTo(expectedPassword);
        assertThat(result.getSalt()).isEqualTo(expectedSalt);
    }

    @Test
    public void toDocument_should_return_empty_doc_with_null_user() {
        //given
        SecuredUser nulluser = null;
        SecuredUserMongoConverter testedConverter = new SecuredUserMongoConverter();

        //when
        Document result = testedConverter.toDocument(nulluser);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(new Document());
    }

    @Test(expected = ApplicationException.class)
    public void fromDocument_should_fail_with_unsecure_user_doc() {
        //given
        UUID expectedId = UUID.randomUUID();
        Document unsecureUserDoc = new Document(FIELD_ID, expectedId)
                .append(FIELD_NAME, "name")
                .append(FIELD_EMAIL, "mail")
                .append(FIELD_PROFILE_VISIBILITY, PUBLIC.name())
                .append(FIELD_LAST_UPDATE, new Date())
                .append(FIELD_CREATED, new Date());

        SecuredUserMongoConverter testedConverter = new SecuredUserMongoConverter();

        //when
        try {
            testedConverter.fromDocument(unsecureUserDoc);
        } catch (ApplicationException ae) {
            //then
            TestHelper.assertApplicationException(ae, ERROR, APPLICATION_ERROR, UNABLE_TO_CONVERT_UNSECURE_USER.getDevReadableMessage(expectedId));
            throw ae;
        }
    }

    @Test
    public void toDocument_should_work_with_valid_user() {
        //given
        User user = TestHelper.generateRandomUser();
        SecuredUser securedUser = SecuredUser.Builder.createFrom(user).build();
        SecuredUserMongoConverter testedConverter = new SecuredUserMongoConverter();

        //when
        Document result = testedConverter.toDocument(securedUser);

        //then
        assertThat(result).isNotNull();
        assertThat(result.get(FIELD_ID)).isEqualTo(securedUser.getId());
        assertThat(result.get(FIELD_NAME)).isEqualTo(securedUser.getName());
        assertThat(result.get(FIELD_EMAIL)).isEqualTo(securedUser.getEmail());
        assertThat(ProfileVisibility.valueOfOrNull(result.getString(FIELD_PROFILE_VISIBILITY))).isEqualTo(user.getProfileVisibility());
        assertThat(DateHelper.toLocalDateTime(result.getDate(FIELD_CREATED))).isEqualToIgnoringNanos(user.getCreationDate());
        assertThat(DateHelper.toLocalDateTime(result.getDate(FIELD_LAST_UPDATE))).isEqualToIgnoringNanos(user.getLastUpdate());
        assertThat(result.get(FIELD_SECURITY, Document.class).getString(FIELD_PASSWORD)).isEqualTo(securedUser.getPassword());
        assertThat(result.get(FIELD_SECURITY, Document.class).get(FIELD_SALT)).isEqualTo(securedUser.getSalt());
    }

    @Test
    public void getChangePasswordUpdate_should_be_correct() {
        //given
        SecuredUser securedUser = TestHelper.generateRandomSecuredUser();
        SecuredUserMongoConverter testedConverter = new SecuredUserMongoConverter();

        //when
        Document result = testedConverter.getChangePasswordUpdate(securedUser);

        //then
        assertThat(result).isNotNull();
        Document updateDoc = result.get("$set", Document.class);
        assertThat(updateDoc).isNotNull();
        assertThat(DateHelper.toLocalDateTime(updateDoc.getDate(FIELD_LAST_UPDATE))).isEqualToIgnoringNanos(securedUser.getLastUpdate());
        assertThat(updateDoc.get(FIELD_SECURITY, Document.class).getString(FIELD_PASSWORD)).isEqualTo(securedUser.getPassword());
        assertThat(updateDoc.get(FIELD_SECURITY, Document.class).get(FIELD_SALT)).isEqualTo(securedUser.getSalt());
    }
}
