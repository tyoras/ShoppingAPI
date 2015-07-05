package yoan.shopping.user.repository.mongo;

import static org.fest.assertions.api.Assertions.assertThat;
import static yoan.shopping.infra.db.Dbs.SHOPPING;
import static yoan.shopping.user.repository.UserRepositoryErrorMessage.PROBLEM_CREATION_USER;
import static yoan.shopping.user.repository.mongo.UserMongoRepository.USER_COLLECTION;

import java.util.UUID;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Test;
import org.mockito.InjectMocks;

import yoan.shopping.infra.util.error.ApplicationException;
import yoan.shopping.test.FongoBackedTest;
import yoan.shopping.test.TestHelper;
import yoan.shopping.user.SecuredUser;
import yoan.shopping.user.User;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

public class SecuredUserMongoRepositoryTest extends FongoBackedTest {
	
	private final SecuredUserMongoConverter converter = new SecuredUserMongoConverter();
	private final MongoCollection<Document> userCollection = getFongoDbConnectionFactory().getCollection(SHOPPING, USER_COLLECTION);
	
	@InjectMocks
	SecuredUserMongoRepository testedRepo;
	
	@Test
	public void create_should_work() {
		//given
		SecuredUser expectedUser = TestHelper.generateRandomSecuredUser();
		String expectedPassword = expectedUser.getPassword();

		//when
		testedRepo.create(expectedUser, expectedPassword);
		
		//then
		Bson filter = Filters.eq("_id", expectedUser.getId().toString());
		Document result = userCollection.find().filter(filter).first();
		SecuredUser securedUser = converter.fromDocument(result);
		User user = User.Builder.createFrom(securedUser).build();
		
		assertThat(user).isEqualTo(User.Builder.createFrom(expectedUser).build());
		//with the generated salt we should be able to generate the same hash
		String hash = testedRepo.hashPassword(expectedPassword, securedUser.getSalt());
		assertThat(securedUser.getPassword()).isEqualTo(hash);
	}
	
	@Test(expected = ApplicationException.class)
	public void create_should_fail_with_already_existing_user_and_same_password() {
		//given
		User alreadyExistingUser = TestHelper.generateRandomUser();
		testedRepo.create(alreadyExistingUser, "password");
		
		//when
		try {
			testedRepo.create(alreadyExistingUser, "password");
		} catch (ApplicationException ae) {
		//then
			assertThat(ae.getMessage()).contains(PROBLEM_CREATION_USER.getDevReadableMessage(""));
			throw ae;
		} finally {
			//checking if the already existing user still exists
			Bson filter = Filters.eq("_id", alreadyExistingUser.getId().toString());
			Document result = userCollection.find().filter(filter).first();
			User user = new UserMongoConverter().fromDocument(result);
			assertThat(user).isEqualTo(alreadyExistingUser);
		}
	}
	
	@Test(expected = ApplicationException.class)
	public void create_should_fail_with_already_existing_user_and_different_password() {
		//given
		User alreadyExistingUser = TestHelper.generateRandomUser();
		testedRepo.create(alreadyExistingUser, "password1");
		SecuredUser alreadyExistingSecuredUser = testedRepo.getById(alreadyExistingUser.getId());
		
		//when
		try {
			testedRepo.create(alreadyExistingUser, "password2");
		} catch (ApplicationException ae) {
		//then
			assertThat(ae.getMessage()).contains(PROBLEM_CREATION_USER.getDevReadableMessage(""));
			throw ae;
		} finally {
			//checking if the already existing user still exists
			Bson filter = Filters.eq("_id", alreadyExistingUser.getId().toString());
			Document result = userCollection.find().filter(filter).first();
			SecuredUser securedUser = converter.fromDocument(result);
			assertThat(securedUser).isEqualTo(alreadyExistingSecuredUser);
		}
	}
	
	@Test
	public void getById_should_return_null_with_not_existing_user_id() {
		//given
		UUID notExistingUserId = UUID.randomUUID();

		//when
		User result = testedRepo.getById(notExistingUserId);
		
		//then
		assertThat(result).isNull();
	}
	
	@Test
	public void getById_should_work_with_existing_user_id() {
		//given
		User expectedUser = TestHelper.generateRandomUser();
		String originalPassword = "password";
		testedRepo.create(expectedUser, originalPassword);
		

		//when
		SecuredUser result = testedRepo.getById(expectedUser.getId());
		
		//then
		assertThat(result).isNotNull();
		assertThat(User.Builder.createFrom(result).build()).isEqualTo(expectedUser);
		//with the generated salt we should be able to generate the same hash
		String hash = testedRepo.hashPassword(originalPassword, result.getSalt());
		assertThat(result.getPassword()).isEqualTo(hash);
	}
	
}
