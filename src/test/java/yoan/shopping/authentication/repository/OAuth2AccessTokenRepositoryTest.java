package yoan.shopping.authentication.repository;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import yoan.shopping.authentication.repository.fake.OAuth2AccessTokenFakeRepository;

@RunWith(MockitoJUnitRunner.class)
public class OAuth2AccessTokenRepositoryTest {
	
	@Spy
	OAuth2AccessTokenRepository testedRepo = getTestedRepository();
	
	protected OAuth2AccessTokenRepository getTestedRepository() {
		return new OAuth2AccessTokenFakeRepository();
	}
	
	@Test
	public void getUserIdByAccessToken_should_return_null_with_null_token() {
		//given
		String nullToken = null;

		//when
		UUID result = testedRepo.getUserIdByAccessToken(nullToken);
		
		//then
		assertThat(result).isNull();
		verify(testedRepo, never()).processGetUserIdByAccessToken(any());
	}

	@Test
	public void getUserIdByAccessToken_should_return_null_with_blank_token() {
		//given
		String blankToken = "  ";

		//when
		UUID result = testedRepo.getUserIdByAccessToken(blankToken);
		
		//then
		assertThat(result).isNull();
		verify(testedRepo, never()).processGetUserIdByAccessToken(any());
	}
	
	@Test
	public void insertAccessToken_should_do_nothing_with_null_token() {
		//given
		String nullToken = null;

		//when
		testedRepo.insert(nullToken, null);
		
		//then
		verify(testedRepo, never()).processInsert(any(), any());
	}
	
	@Test
	public void insertAccessToken_should_do_nothing_with_blank_token() {
		//given
		String blankToken = "  ";

		//when
		testedRepo.insert(blankToken, UUID.randomUUID());
		
		//then
		verify(testedRepo, never()).processInsert(any(), any());
	}
	
	@Test
	public void insertAccessToken_should_do_nothing_with_null_userId() {
		//given
		UUID nullUserId = null;

		//when
		testedRepo.insert("token", nullUserId);
		
		//then
		verify(testedRepo, never()).processInsert(any(), any());
	}
	
	@Test
	public void deleteByAccessToken_should_do_nothing_with_null_token() {
		//given
		String nullToken = null;

		//when
		testedRepo.deleteByAccessToken(nullToken);
		
		//then
		verify(testedRepo, never()).processDeleteByAccessToken(any());
	}
	
	@Test
	public void deleteByAccessToken_should_do_nothing_with_blank_token() {
		//given
		String blankToken = "  ";

		//when
		testedRepo.deleteByAccessToken(blankToken);
		
		//then
		verify(testedRepo, never()).processDeleteByAccessToken(any());
	}
}
