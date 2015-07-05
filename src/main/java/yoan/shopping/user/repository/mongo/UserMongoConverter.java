/**
 * 
 */
package yoan.shopping.user.repository.mongo;

import java.util.UUID;

import org.bson.Document;

import yoan.shopping.infra.db.mongo.MongoDocumentConverter;
import yoan.shopping.user.User;

/**
 * @author yoan
 */
public class UserMongoConverter implements MongoDocumentConverter<User> {
	public static final String FIELD_ID = "_id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_EMAIL = "email";
	
	@Override
	public User fromDocument(Document doc) {
		if (doc == null) {
			return null;
		}
		
		String idStr = doc.getString(FIELD_ID);
        UUID id = UUID.fromString(idStr);
        String name = doc.getString(FIELD_NAME);
        String email = doc.getString(FIELD_EMAIL);
        
        return User.Builder.createDefault()
        				   .withId(id)
        				   .withName(name)
        				   .withEmail(email)
        				   .build();
	}

	@Override
	public Document toDocument(User user) {
		if (user == null) {
			return new Document();
		}
		
		return new Document(FIELD_ID, user.getId().toString())
				.append(FIELD_NAME, user.getName())
				.append(FIELD_EMAIL, user.getEmail());
	}
}