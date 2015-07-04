/**
 * 
 */
package yoan.shopping.user.repository.mongo;

import static yoan.shopping.infra.logging.Markers.SECURITY;
import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.util.error.CommonErrorCode.APPLICATION_ERROR;
import static yoan.shopping.user.repository.UserRepositoryErrorMessage.UNABLE_TO_CONVERT_UNSECURE_USER;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yoan.shopping.infra.util.error.ApplicationException;
import yoan.shopping.user.SecuredUser;
import yoan.shopping.user.User;

/**
 * 
 * @author yoan
 */
public class SecuredUserMongoConverter extends UserMongoConverter {
	public static final String FIELD_SECURITY = "security";
	public static final String FIELD_PASSWORD = "password";
    public static final String FIELD_SALT = "salt";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SecuredUserMongoConverter.class);
	
	public SecuredUser fromDocument(Document doc) {
		User user = super.fromDocument(doc);
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
			LOGGER.error(SECURITY.getMarker(), UNABLE_TO_CONVERT_UNSECURE_USER.getHumanReadableMessage(userToConvert.toString()));
			throw new ApplicationException(ERROR, APPLICATION_ERROR, UNABLE_TO_CONVERT_UNSECURE_USER.getHumanReadableMessage(userToConvert.getId().toString()));
		}
	}

	public Document toDocument(SecuredUser user) {
		Document doc = super.toDocument(user);
		if (doc.isEmpty()) {
			return doc;
		}
		
		Document securityObject = new Document(FIELD_PASSWORD, user.getPassword()).append(FIELD_SALT, user.getSalt());
		return doc.append(FIELD_SECURITY, securityObject);
	}
}
