/**
 *
 */
package io.tyoras.shopping.user.repository.mongo;

import io.tyoras.shopping.infra.db.mongo.MongoDocumentConverter;
import io.tyoras.shopping.infra.util.error.ApplicationException;
import io.tyoras.shopping.infra.util.helper.DateHelper;
import io.tyoras.shopping.user.SecuredUser;
import io.tyoras.shopping.user.User;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.tyoras.shopping.infra.logging.Markers.SECURITY;
import static io.tyoras.shopping.infra.rest.error.Level.ERROR;
import static io.tyoras.shopping.infra.util.error.CommonErrorCode.APPLICATION_ERROR;
import static io.tyoras.shopping.user.repository.UserRepositoryErrorMessage.UNABLE_TO_CONVERT_UNSECURE_USER;
import static io.tyoras.shopping.user.repository.mongo.UserMongoConverter.FIELD_LAST_UPDATE;

/**
 * MongoDb codec to convert secured user to BSON
 *
 * @author yoan
 */
public class SecuredUserMongoConverter extends MongoDocumentConverter<SecuredUser> {
    public static final String FIELD_SECURITY = "security";
    public static final String FIELD_PASSWORD = "password";
    public static final String FIELD_SALT = "salt";
    private static final Logger LOGGER = LoggerFactory.getLogger(SecuredUserMongoConverter.class);
    private UserMongoConverter userConverter;

    public SecuredUserMongoConverter() {
        super();
        userConverter = new UserMongoConverter();
    }

    public SecuredUserMongoConverter(Codec<Document> codec) {
        super(codec);
        userConverter = new UserMongoConverter(codec);
    }

    @Override
    public SecuredUser fromDocument(Document doc) {
        User user = userConverter.fromDocument(doc);
        if (user == null) {
            return null;
        }

        Document securityObject = doc.get(FIELD_SECURITY, Document.class);
        ensureSecurityObjectIsPresent(securityObject, user);
        String password = securityObject.getString(FIELD_PASSWORD);
        Object salt = securityObject.get(FIELD_SALT);

        return SecuredUser.Builder.createFrom(user)
                .withPassword(password)
                .withSalt(salt)
                .build();
    }

    private void ensureSecurityObjectIsPresent(Document securityObject, User userToConvert) {
        if (securityObject == null) {
            LOGGER.error(SECURITY.getMarker(), UNABLE_TO_CONVERT_UNSECURE_USER.getDevReadableMessage(userToConvert.toString()));
            throw new ApplicationException(ERROR, APPLICATION_ERROR, UNABLE_TO_CONVERT_UNSECURE_USER.getDevReadableMessage(userToConvert.getId().toString()));
        }
    }

    @Override
    public Document toDocument(SecuredUser user) {
        Document doc = userConverter.toDocument(user);
        if (doc.isEmpty()) {
            return doc;
        }

        Document securityObject = getSecurityObjectFromSecuredUser(user);
        return doc.append(FIELD_SECURITY, securityObject);
    }

    private Document getSecurityObjectFromSecuredUser(SecuredUser user) {
        return new Document(FIELD_PASSWORD, user.getPassword()).append(FIELD_SALT, user.getSalt());
    }

    public Document getChangePasswordUpdate(SecuredUser userToUpdate) {
        Document securityObject = getSecurityObjectFromSecuredUser(userToUpdate);
        Document updateDoc = new Document(FIELD_LAST_UPDATE, DateHelper.toDate(userToUpdate.getLastUpdate()))
                .append(FIELD_SECURITY, securityObject);
        return new Document("$set", updateDoc);
    }

    @Override
    public Class<SecuredUser> getEncoderClass() {
        return SecuredUser.class;
    }

    @Override
    public SecuredUser generateIdIfAbsentFromDocument(SecuredUser securedUser) {
        User user = User.Builder.createFrom(securedUser).withRandomId().build();
        return !documentHasId(securedUser) ? SecuredUser.Builder.createFrom(user)
                .withPassword(securedUser.getPassword())
                .withSalt(securedUser.getSalt())
                .build()
                : securedUser;
    }
}
