package yoan.shopping.user.repository;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
	
	@Test
	public void getByEmail_should_return_null_with_null_email() {
		//given
		String nullEmail = null;

		//when
		User result = testedRepo.getByEmail(nullEmail);
		
		//then
		assertThat(result).isNull();
		verify(testedRepo, never()).processGetByEmail(any());
	}
	
	@Test
	public void getByEmail_should_return_null_with_blank_email() {
		//given
		String blankEmail = "  ";

		//when
		User result = testedRepo.getByEmail(blankEmail);
		
		//then
		assertThat(result).isNull();
		verify(testedRepo, never()).processGetByEmail(any());
	}
	
	@Test
	public void checkUserExistsByIdOrEmail_should_return_true_with_null_Id_and_existing_email() {
		//given
		UUID nullId = null;
		String existingEmail = "mail@mail.com";
		when(testedRepo.countByIdOrEmail(any(), eq(existingEmail))).thenReturn(Long.valueOf(1));

		//when
		boolean result = testedRepo.checkUserExistsByIdOrEmail(nullId, existingEmail);
		
		//then
		assertThat(result).isTrue();
		verify(testedRepo).countByIdOrEmail(any(), eq(existingEmail));
	}
	
	@Test
	public void checkUserExistsByIdOrEmail_should_return_true_with_null_email_and_existing_id() {
		//given
		UUID existingId = UUID.randomUUID();
		String nullEmail = null;
		when(testedRepo.countByIdOrEmail(eq(existingId), any())).thenReturn(Long.valueOf(1));

		//when
		boolean result = testedRepo.checkUserExistsByIdOrEmail(existingId, nullEmail);
		
		//then
		assertThat(result).isTrue();
		verify(testedRepo).countByIdOrEmail(eq(existingId), any());
	}
	
	@Test
	public void checkUserExistsByIdOrEmail_should_return_false_with_null_id_and_blank_email() {
		//given
		UUID nullId = null;
		String blankEmail = "  ";

		//when
		boolean result = testedRepo.checkUserExistsByIdOrEmail(nullId, blankEmail);
		
		//then
		assertThat(result).isFalse();
		verify(testedRepo, never()).countByIdOrEmail(any(), any());
	}
}
