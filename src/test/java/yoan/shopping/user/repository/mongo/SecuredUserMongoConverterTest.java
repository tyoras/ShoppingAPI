package yoan.shopping.user.repository.mongo;

import static org.fest.assertions.api.Assertions.assertThat;
import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.util.error.CommonErrorCode.APPLICATION_ERROR;
import static yoan.shopping.user.repository.UserRepositoryErrorMessage.UNABLE_TO_CONVERT_UNSECURE_USER;
import static yoan.shopping.user.repository.mongo.SecuredUserMongoConverter.FIELD_PASSWORD;
import static yoan.shopping.user.repository.mongo.SecuredUserMongoConverter.FIELD_SALT;
import static yoan.shopping.user.repository.mongo.SecuredUserMongoConverter.FIELD_SECURITY;
import static yoan.shopping.user.repository.mongo.UserMongoConverter.FIELD_EMAIL;
import static yoan.shopping.user.repository.mongo.UserMongoConverter.FIELD_ID;
import static yoan.shopping.user.repository.mongo.UserMongoConverter.FIELD_NAME;

import java.util.UUID;

import org.bson.Document;
import org.junit.Test;

import yoan.shopping.infra.util.error.ApplicationException;
import yoan.shopping.test.TestHelper;
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
							.append(FIELD_SECURITY , new Document(FIELD_PASSWORD, expectedPassword)
														  .append(FIELD_SALT, expectedSalt));
		
		SecuredUserMongoConverter testedConverter = new SecuredUserMongoConverter();
		
		//when
		SecuredUser result = testedConverter.fromDocument(doc);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(expectId);
		assertThat(result.getName()).isEqualTo(expectedName);
		assertThat(result.getEmail()).isEqualTo(expectedMail);
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
		String expectedId = UUID.randomUUID().toString();
		Document unsecureUserDoc = new Document(FIELD_ID, expectedId)
										.append(FIELD_NAME, "name")
										.append(FIELD_EMAIL, "mail");
		
		SecuredUserMongoConverter testedConverter = new SecuredUserMongoConverter();
		
		//when
		try {
			testedConverter.fromDocument(unsecureUserDoc);
		} catch(ApplicationException ae) {
		//then
			TestHelper.assertApplicationException(ae, ERROR, APPLICATION_ERROR, UNABLE_TO_CONVERT_UNSECURE_USER.getHumanReadableMessage(expectedId));
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
		assertThat(result.get(FIELD_ID)).isEqualTo(securedUser.getId().toString());
		assertThat(result.get(FIELD_NAME)).isEqualTo(securedUser.getName());
		assertThat(result.get(FIELD_EMAIL)).isEqualTo(securedUser.getEmail());
		assertThat(result.get(FIELD_SECURITY, Document.class).getString(FIELD_PASSWORD)).isEqualTo(securedUser.getPassword());
		assertThat(result.get(FIELD_SECURITY, Document.class).get(FIELD_SALT)).isEqualTo(securedUser.getSalt());
	}
}
