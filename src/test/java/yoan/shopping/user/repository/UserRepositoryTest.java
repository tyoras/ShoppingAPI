package yoan.shopping.user.repository;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static yoan.shopping.infra.rest.error.Level.INFO;
import static yoan.shopping.infra.util.error.RepositoryErrorCode.NOT_FOUND;
import static yoan.shopping.test.TestHelper.assertApplicationException;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import yoan.shopping.infra.util.error.ApplicationException;
import yoan.shopping.test.TestHelper;
import yoan.shopping.user.User;
import yoan.shopping.user.repository.UserRepository;
import yoan.shopping.user.repository.fake.UserFakeRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryTest {
	
	@Spy
	UserRepository testedRepo = new UserFakeRepository();
	
	@Test
	public void create_should_do_nothing_with_null_user() {
		//given
		User nullUser = null;

		//when
		testedRepo.create(nullUser);
		
		//then
		verify(testedRepo, never()).processCreate(any());
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
	
	@Test
	public void update_should_do_nothing_with_null_user() {
		//given
		User nullUser = null;

		//when
		testedRepo.update(nullUser);
		
		//then
		verify(testedRepo, never()).processUpdate(any());
	}
	
	@Test(expected = ApplicationException.class)
	public void update_should_fail_with_not_existing_user() {
		//given
		User notExistingUser = TestHelper.generateRandomUser();

		//when
		try {
			testedRepo.update(notExistingUser);
		} catch (ApplicationException ae) {
		//then
			assertApplicationException(ae, INFO, NOT_FOUND, "User not found");
			throw ae;
		} finally {
			verify(testedRepo, never()).processUpdate(any());
		}
	}
	
	@Test
	public void deleteById_should_do_nothing_with_null_Id() {
		//given
		UUID nullId = null;

		//when
		testedRepo.deleteById(nullId);
		
		//then
		verify(testedRepo, never()).processDeleteById(any());
	}
}
