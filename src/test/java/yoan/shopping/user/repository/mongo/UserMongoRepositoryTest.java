package yoan.shopping.user.repository.mongo;

import static org.assertj.core.api.Assertions.assertThat;
import static yoan.shopping.infra.db.Dbs.SHOPPING;
import static yoan.shopping.infra.rest.error.Level.INFO;
import static yoan.shopping.test.TestHelper.assertApplicationException;
import static yoan.shopping.user.repository.UserRepositoryErrorCode.TOO_MUCH_RESULT;
import static yoan.shopping.user.repository.UserRepositoryErrorMessage.PROBLEM_CREATION_USER;
import static yoan.shopping.user.repository.mongo.UserMongoRepository.USER_COLLECTION;

import java.util.UUID;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;

import com.google.common.collect.ImmutableList;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import yoan.shopping.infra.util.error.ApplicationException;
import yoan.shopping.test.TestHelper;
import yoan.shopping.test.fongo.FongoBackedTest;
import yoan.shopping.user.User;
import yoan.shopping.user.repository.UserRepository;

public class UserMongoRepositoryTest extends FongoBackedTest {
	
	private final UserMongoConverter converter = new UserMongoConverter();
	private final MongoCollection<Document> userCollection = getFongoDbConnectionFactory().getCollection(SHOPPING, USER_COLLECTION);
	
	@InjectMocks
	UserMongoRepository testedRepo;
	
	@Test
	public void create_should_work() {
		//given
		User expectedUser = TestHelper.generateRandomUser();

		//when
		testedRepo.create(expectedUser);
		
		//then
		Bson filter = Filters.eq("_id", expectedUser.getId());
		Document result = userCollection.find().filter(filter).first();
		User user = converter.fromDocument(result);
		assertThat(user).isEqualTo(expectedUser);
		assertThat(user.getCreationDate()).isEqualTo(user.getLastUpdate());
	}
	
	@Test(expected = ApplicationException.class)
	public void create_should_fail_with_message_with_already_existing_user() {
		//given
		User alreadyExistingUser = TestHelper.generateRandomUser();
		testedRepo.create(alreadyExistingUser);
		
		//when
		try {
			testedRepo.create(alreadyExistingUser);
		} catch (ApplicationException ae) {
		//then
			assertThat(ae.getMessage()).contains(PROBLEM_CREATION_USER.getDevReadableMessage(""));
			throw ae;
		} finally {
			//checking if the already existing user still exists
			Bson filter = Filters.eq("_id", alreadyExistingUser.getId());
			Document result = userCollection.find().filter(filter).first();
			User user = converter.fromDocument(result);
			assertThat(user).isEqualTo(alreadyExistingUser);
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
		testedRepo.create(expectedUser);

		//when
		User result = testedRepo.getById(expectedUser.getId());
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(expectedUser);
	}
	
	@Test
	public void getByEmail_should_return_null_with_not_existing_user_email() {
		//given
		String notExistingUserEmail = "not_existing@mail.com";

		//when
		User result = testedRepo.getByEmail(notExistingUserEmail);
		
		//then
		assertThat(result).isNull();
	}
	
	@Test
	public void getByEmail_should_work_with_existing_user_email() {
		//given
		User expectedUser = TestHelper.generateRandomUser();
		testedRepo.create(expectedUser);

		//when
		User result = testedRepo.getByEmail(expectedUser.getEmail());
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(expectedUser);
	}
	
	@Test
	public void update_should_work_with_existing_user() throws InterruptedException {
		//given
		User originalUser = TestHelper.generateRandomUser();
		testedRepo.create(originalUser);
		originalUser = testedRepo.getById(originalUser.getId());
		String modifiedName = "new " + originalUser.getName();
		User modifiedUser = User.Builder.createFrom(originalUser).withName(modifiedName).build();
		Thread.sleep(1);
		
		//when
		testedRepo.update(modifiedUser);
		
		//then
		User result = testedRepo.getById(originalUser.getId());
		assertThat(result).isNotNull();
		assertThat(result.getName()).isEqualTo(modifiedName);
		assertThat(result).isEqualTo(modifiedUser);
		//creation date should not change
		assertThat(result.getCreationDate()).isEqualTo(originalUser.getCreationDate());
		//last update date should have changed
		assertThat(result.getLastUpdate().isAfter(originalUser.getLastUpdate())).isTrue();
	}
	
	@Test
	public void deleteById_should_not_fail_with_not_existing_user_id() {
		//given
		UUID notExistingUserId = UUID.randomUUID();

		//when
		testedRepo.deleteById(notExistingUserId);
		
		//then
		//should not have failed
	}
	
	@Test
	public void deleteById_should_work_with_existing_user_id() {
		//given
		User existingUser = TestHelper.generateRandomUser();
		testedRepo.create(existingUser);

		//when
		testedRepo.deleteById(existingUser.getId());
		
		//then
		User result = testedRepo.getById(existingUser.getId());
		assertThat(result).isNull();
	}
	
	@Test
	@Ignore //does not work because there is a bug with fongo parsing uuid when using count
	public void countByIdOrEmail_should_work_with_existing_user_id_and_blank_email() {
		//given
		String blankEmail = "re  ";
		User existingUser = TestHelper.generateRandomUser();
		testedRepo.create(existingUser);

		//when
		long result = testedRepo.countByIdOrEmail(existingUser.getId(), blankEmail);
		
		//then
		assertThat(result).isEqualTo(1);
	}
	
	@Test
	public void searchByName_should_work_with_existing_users() {
		//given
		User user1 = User.Builder.createFrom(TestHelper.generateRandomUser()).withName("abcd").build();
		testedRepo.create(user1);
		User user2 = User.Builder.createFrom(TestHelper.generateRandomUser()).withName("abdc").build();
		testedRepo.create(user2);
		User user3 = User.Builder.createFrom(TestHelper.generateRandomUser()).withName("dabc").build();
		testedRepo.create(user3);
		User user4 = User.Builder.createFrom(TestHelper.generateRandomUser()).withName("ab cd").build();
		testedRepo.create(user4);
		User user5 = User.Builder.createFrom(TestHelper.generateRandomUser()).withName(" abc").build();
		testedRepo.create(user5);
		
		//when
		ImmutableList<User> result = testedRepo.searchByName("abc");
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).containsOnly(user1, user3, user5);
	}
	
	@Test
	public void searchByName_should_return_empty_list_if_no_match() {
		//given
		User user1 = User.Builder.createFrom(TestHelper.generateRandomUser()).withName("abcd").build();
		testedRepo.create(user1);
		User user2 = User.Builder.createFrom(TestHelper.generateRandomUser()).withName("abdc").build();
		testedRepo.create(user2);
		User user3 = User.Builder.createFrom(TestHelper.generateRandomUser()).withName("dabc").build();
		testedRepo.create(user3);
		User user4 = User.Builder.createFrom(TestHelper.generateRandomUser()).withName("ab cd").build();
		testedRepo.create(user4);
		User user5 = User.Builder.createFrom(TestHelper.generateRandomUser()).withName(" abc").build();
		testedRepo.create(user5);
		
		//when
		ImmutableList<User> result = testedRepo.searchByName("cde");
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEmpty();
	}
	
	@Test(expected = ApplicationException.class)
	public void searchByName_should_fail_if_too_much_result() {
		//given
		String search = "abc";
		int nbUserThatMatchSearch = UserRepository.NAME_SEARCH_MAX_RESULT + 1;
		for (int i = 0; i < nbUserThatMatchSearch; i++) {
			User user = User.Builder.createFrom(TestHelper.generateRandomUser()).withName(search + i).build();
			testedRepo.create(user);
		}
		
		//when
		try {
			testedRepo.searchByName(search);
		} catch (ApplicationException ae) {
		//then
			assertApplicationException(ae, INFO, TOO_MUCH_RESULT, String.format("Too much users (%s) for search : %s", nbUserThatMatchSearch, search));
			throw ae;
		}
	}
}
