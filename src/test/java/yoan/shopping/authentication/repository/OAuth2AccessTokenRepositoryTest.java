package yoan.shopping.authentication.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Answers.CALLS_REAL_METHODS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OAuth2AccessTokenRepositoryTest {
	
	@Mock(answer= CALLS_REAL_METHODS)
	OAuth2AccessTokenRepository testedRepo;
	
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
	public void create_should_do_nothing_with_null_token() {
		//given
		String nullToken = null;

		//when
		testedRepo.create(nullToken, null);
		
		//then
		verify(testedRepo, never()).processCreate(any(), any());
	}
	
	@Test
	public void create_should_do_nothing_with_blank_token() {
		//given
		String blankToken = "  ";

		//when
		testedRepo.create(blankToken, UUID.randomUUID());
		
		//then
		verify(testedRepo, never()).processCreate(any(), any());
	}
	
	@Test
	public void create_should_do_nothing_with_null_userId() {
		//given
		UUID nullUserId = null;

		//when
		testedRepo.create("token", nullUserId);
		
		//then
		verify(testedRepo, never()).processCreate(any(), any());
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
