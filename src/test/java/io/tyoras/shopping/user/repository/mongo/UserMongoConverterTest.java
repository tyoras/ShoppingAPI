package io.tyoras.shopping.user.repository.mongo;

import io.tyoras.shopping.infra.util.helper.DateHelper;
import io.tyoras.shopping.test.TestHelper;
import io.tyoras.shopping.user.ProfileVisibility;
import io.tyoras.shopping.user.User;
import org.bson.Document;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static io.tyoras.shopping.user.ProfileVisibility.PUBLIC;
import static io.tyoras.shopping.user.repository.mongo.UserMongoConverter.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author yoan
 */
public class UserMongoConverterTest {

    @Test
    public void fromDocument_should_return_null_with_null_document() {
        //given
        Document nullDoc = null;
        UserMongoConverter testedConverter = new UserMongoConverter();

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

        Document doc = new Document(FIELD_ID, expectId)
                .append(FIELD_NAME, expectedName)
                .append(FIELD_EMAIL, expectedMail)
                .append(FIELD_PROFILE_VISIBILITY, expectedProfileVisibility.name())
                .append(FIELD_CREATED, DateHelper.toDate(expectedCreationDate))
                .append(FIELD_LAST_UPDATE, DateHelper.toDate(expectedLastUpdate));
        UserMongoConverter testedConverter = new UserMongoConverter();

        //when
        User result = testedConverter.fromDocument(doc);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(expectId);
        assertThat(result.getName()).isEqualTo(expectedName);
        assertThat(result.getEmail()).isEqualTo(expectedMail);
        assertThat(result.getProfileVisibility()).isEqualTo(expectedProfileVisibility);
        assertThat(result.getCreationDate()).isEqualToIgnoringNanos(expectedCreationDate);
        assertThat(result.getLastUpdate()).isEqualToIgnoringNanos(expectedLastUpdate);
    }

    @Test
    public void toDocument_should_return_empty_doc_with_null_user() {
        //given
        User nulluser = null;
        UserMongoConverter testedConverter = new UserMongoConverter();

        //when
        Document result = testedConverter.toDocument(nulluser);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(new Document());
    }

    @Test
    public void toDocument_should_work_with_valid_user() {
        //given
        User user = TestHelper.generateRandomUser();
        UserMongoConverter testedConverter = new UserMongoConverter();

        //when
        Document result = testedConverter.toDocument(user);

        //then
        assertThat(result).isNotNull();
        assertThat(result.get(FIELD_ID)).isEqualTo(user.getId());
        assertThat(result.getString(FIELD_NAME)).isEqualTo(user.getName());
        assertThat(result.getString(FIELD_EMAIL)).isEqualTo(user.getEmail());
        assertThat(ProfileVisibility.valueOfOrNull(result.getString(FIELD_PROFILE_VISIBILITY))).isEqualTo(user.getProfileVisibility());
        assertThat(DateHelper.toLocalDateTime(result.getDate(FIELD_CREATED))).isEqualToIgnoringNanos(user.getCreationDate());
        assertThat(DateHelper.toLocalDateTime(result.getDate(FIELD_LAST_UPDATE))).isEqualToIgnoringNanos(user.getLastUpdate());
    }
}
