/**
 *
 */
package io.tyoras.shopping.user.repository.mongo;

import io.tyoras.shopping.infra.db.mongo.MongoDocumentConverter;
import io.tyoras.shopping.infra.util.helper.DateHelper;
import io.tyoras.shopping.user.ProfileVisibility;
import io.tyoras.shopping.user.User;
import org.bson.Document;
import org.bson.codecs.Codec;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * MongoDb codec to convert user to BSON
 *
 * @author yoan
 */
public class UserMongoConverter extends MongoDocumentConverter<User> {
    public static final String FIELD_NAME = "name";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_PROFILE_VISIBILITY = "profileVisibility";
    public static final String FIELD_CREATED = "created";
    public static final String FIELD_LAST_UPDATE = "lastUpdate";

    public UserMongoConverter() {
        super();
    }

    public UserMongoConverter(Codec<Document> codec) {
        super(codec);
    }

    public static Document getUserUpdate(User userToUpdate) {
        Document updateDoc = new Document(FIELD_LAST_UPDATE, DateHelper.toDate(userToUpdate.getLastUpdate()))
                .append(FIELD_EMAIL, userToUpdate.getEmail())
                .append(FIELD_NAME, userToUpdate.getName())
                .append(FIELD_PROFILE_VISIBILITY, userToUpdate.getProfileVisibility().name());
        return new Document("$set", updateDoc);
    }

    @Override
    public User fromDocument(Document doc) {
        if (doc == null) {
            return null;
        }

        UUID id = doc.get(FIELD_ID, UUID.class);
        String name = doc.getString(FIELD_NAME);
        String email = doc.getString(FIELD_EMAIL);
        String profileVisibilityCode = doc.getString(FIELD_PROFILE_VISIBILITY);
        ProfileVisibility profileVisibility = ProfileVisibility.valueOfOrNull(profileVisibilityCode);
        Date created = doc.getDate(FIELD_CREATED);
        LocalDateTime creationDate = DateHelper.toLocalDateTime(created);
        Date lastUpdated = doc.getDate(FIELD_LAST_UPDATE);
        LocalDateTime lastUpdate = DateHelper.toLocalDateTime(lastUpdated);

        return User.Builder.createDefault()
                .withId(id)
                .withCreationDate(creationDate)
                .withLastUpdate(lastUpdate)
                .withName(name)
                .withEmail(email)
                .withProfileVisibility(profileVisibility)
                .build();
    }

    @Override
    public Document toDocument(User user) {
        if (user == null) {
            return new Document();
        }

        return new Document(FIELD_ID, user.getId())
                .append(FIELD_NAME, user.getName())
                .append(FIELD_EMAIL, user.getEmail())
                .append(FIELD_PROFILE_VISIBILITY, user.getProfileVisibility().name())
                .append(FIELD_CREATED, DateHelper.toDate(user.getCreationDate()))
                .append(FIELD_LAST_UPDATE, DateHelper.toDate(user.getLastUpdate()));
    }

    @Override
    public Class<User> getEncoderClass() {
        return User.class;
    }

    @Override
    public User generateIdIfAbsentFromDocument(User user) {
        return documentHasId(user) ? user : User.Builder.createFrom(user).withRandomId().build();
    }
}