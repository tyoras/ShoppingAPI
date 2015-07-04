/**
 * 
 */
package yoan.shopping.user.repository.mongo;

import org.bson.Document;

import yoan.shopping.user.SecuredUser;
import yoan.shopping.user.User;

/**
 * 
 * @author yoan
 */
public class SecuredUserMongoConverter extends UserMongoConverter {
	
	public static final String FIELD_PASSWORD = "security.password";
    public static final String FIELD_SALT = "security.salt";
	
	public SecuredUser fromDocument(Document doc) {
		User user = super.fromDocument(doc);
		if (user == null) {
			return null;
		}
		
        String password = (String) doc.get(FIELD_PASSWORD);
        Object salt = doc.get(FIELD_SALT);
        
        return SecuredUser.Builder.createFrom(user)
		        				   .withPassword(password)
		        				   .withSalt(salt)
		        				   .build();
	}

	public Document toDocument(SecuredUser user) {
		Document doc = super.toDocument(user);
		if (doc.isEmpty()) {
			return doc;
		}
		
		return doc.append(FIELD_PASSWORD, user.getPassword())
				  .append(FIELD_SALT, user.getSalt());
	}
}
