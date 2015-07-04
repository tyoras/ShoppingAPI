package yoan.shopping.user.repository.mongo;

import static org.fest.assertions.api.Assertions.assertThat;
import static yoan.shopping.user.repository.mongo.UserMongoConverter.*;
import static yoan.shopping.user.repository.mongo.SecuredUserMongoConverter.*;

import java.util.UUID;

import org.bson.Document;
import org.junit.Test;

import yoan.shopping.user.SecuredUser;
import yoan.shopping.user.User;

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
		String expectedPassword = "password";
		Object expectedSalt = UUID.randomUUID().toString();
		Document doc = new Document(FIELD_ID, expectId.toString())
							.append(FIELD_NAME, expectedName)
							.append(FIELD_EMAIL, expectedMail)
							.append(FIELD_PASSWORD, expectedPassword)
							.append(FIELD_SALT, expectedSalt);
		
		SecuredUserMongoConverter testedConverter = new SecuredUserMongoConverter();
		
		//when
		User result = testedConverter.fromDocument(doc);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(expectId);
		assertThat(result.getName()).isEqualTo(expectedName);
		assertThat(result.getEmail()).isEqualTo(expectedMail);
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
	
	@Test
	public void toDocument_should_work_with_valid_user() {
		//given
		User user = User.Builder.createDefault().withRandomId().build();
		SecuredUser securedUser = SecuredUser.Builder.createFrom(user).build();
		SecuredUserMongoConverter testedConverter = new SecuredUserMongoConverter();
		
		//when
		Document result = testedConverter.toDocument(securedUser);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result.get(FIELD_ID)).isEqualTo(user.getId().toString());
		assertThat(result.get(FIELD_NAME)).isEqualTo(user.getName());
		assertThat(result.get(FIELD_EMAIL)).isEqualTo(user.getEmail());
		assertThat(result.get(FIELD_PASSWORD)).isEqualTo(securedUser.getPassword());
		assertThat(result.get(FIELD_SALT)).isEqualTo(securedUser.getSalt());
	}
}
