package yoan.shopping.user.repository;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.util.error.CommonErrorCode.APPLICATION_ERROR;
import static yoan.shopping.test.TestHelper.assertApplicationException;
import static yoan.shopping.user.repository.UserRepositoryErrorMessage.PROBLEM_PASSWORD_VALIDITY;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import yoan.shopping.infra.util.error.ApplicationException;
import yoan.shopping.user.User;
import yoan.shopping.user.repository.fake.SecuredUserFakeRepository;

@RunWith(MockitoJUnitRunner.class)
public class SecuredUserRepositoryTest {
	
	@Spy
	SecuredUserRepository testedRepo = getTestedRepository();
	
	protected SecuredUserRepository getTestedRepository() {
		return new SecuredUserFakeRepository();
	}
	
	@Test
	public void create_should_do_nothing_with_null_user() {
		//given
		User nullUser = null;

		//when
		testedRepo.create(nullUser, "password");
		
		//then
		verify(testedRepo, never()).createImpl(any());
	}
	
	@Test(expected = ApplicationException.class)
	public void create_should_fail_with_null_password() {
		//given
		User user = User.Builder.createDefault().withRandomId().build();
		String nullPassword = null;
		
		//when
		try {
			testedRepo.create(user, nullPassword);
		} catch(ApplicationException ae) {
			//then
			verify(testedRepo, never()).createImpl(any());
			assertApplicationException(ae, ERROR, APPLICATION_ERROR, PROBLEM_PASSWORD_VALIDITY);
			throw ae;
		}
	}
	
	@Test(expected = ApplicationException.class)
	public void create_should_fail_with_blank_password() {
		//given
		User user = User.Builder.createDefault().withRandomId().build();
		String blankPassword = "  ";
		
		//when
		try {
			testedRepo.create(user, blankPassword);
		} catch(ApplicationException ae) {
			//then
			verify(testedRepo, never()).createImpl(any());
			assertApplicationException(ae, ERROR, APPLICATION_ERROR, PROBLEM_PASSWORD_VALIDITY);
			throw ae;
		}
	}
	
	@Test
	public void generateSalt_should_never_return_null() {
		//when
		Object salt = testedRepo.generateSalt();
		
		//then
		assertThat(salt).isNotNull();
	}
	
	@Test
	public void generateSalt_should_never_return_same_value() {
		//when
		Object salt1 = testedRepo.generateSalt();
		Object salt2 = testedRepo.generateSalt();
		
		//then
		assertThat(salt1).isNotEqualTo(salt2);
	}
	
	@Test
	public void hashPassword_should_always_return_the_same_hash_if_using_the_same_salt() {
		//given
		Object salt = "salt";
		String password = "password";
		
		//when
		String hash1 = testedRepo.hashPassword(password, salt);
		String hash2 = testedRepo.hashPassword(password, salt);
		String hash3 = testedRepo.hashPassword(password, salt);
		
		assertThat(hash1).isNotNull();
		assertThat(hash1).isEqualTo(hash2);
		assertThat(hash1).isEqualTo(hash3);
	}
	
	@Test
	public void hashPassword_should_not_return_the_same_hash_if_using_different_salt() {
		//given
		String password = "password";
		
		//when
		String hash1 = testedRepo.hashPassword(password, UUID.randomUUID().toString());
		String hash2 = testedRepo.hashPassword(password, UUID.randomUUID().toString());
		String hash3 = testedRepo.hashPassword(password, UUID.randomUUID().toString());
		
		assertThat(hash1).isNotEqualTo(hash2);
		assertThat(hash1).isNotEqualTo(hash3);
		assertThat(hash2).isNotEqualTo(hash3);
	}
	
	@Test
	public void getById_should_return_null_with_null_Id() {
		//given
		UUID nullId = null;

		//when
		User result = testedRepo.getById(nullId);
		
		//then
		assertThat(result).isNull();
		verify(testedRepo, never()).processGetById(any());
	}
}
