package yoan.shopping.user.repository;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

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
		verify(testedRepo, never()).createImpl(any());
	}
	
	@Test
	public void getById_should_return_null_with_null_Id() {
		//given
		UUID nullId = null;

		//when
		User result = testedRepo.getById(nullId);
		
		//then
		assertThat(result).isNull();
		verify(testedRepo, never()).getByIdImpl(any());
	}
	
	@Test
	public void upsert_should_do_nothing_with_null_user() {
		//given
		User nullUser = null;

		//when
		testedRepo.upsert(nullUser);
		
		//then
		verify(testedRepo, never()).upsertImpl(any());
	}
	
	@Test
	public void deleteById_should_do_nothing_with_null_Id() {
		//given
		UUID nullId = null;

		//when
		testedRepo.deleteById(nullId);
		
		//then
		verify(testedRepo, never()).deleteByIdImpl(any());
	}
}
