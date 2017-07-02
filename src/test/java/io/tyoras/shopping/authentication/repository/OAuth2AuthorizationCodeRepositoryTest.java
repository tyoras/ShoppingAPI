package io.tyoras.shopping.authentication.repository;

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

import io.tyoras.shopping.authentication.repository.OAuth2AuthorizationCodeRepository;

@RunWith(MockitoJUnitRunner.class)
public class OAuth2AuthorizationCodeRepositoryTest {
	
	@Mock(answer= CALLS_REAL_METHODS)
	OAuth2AuthorizationCodeRepository testedRepo;
	
	@Test
	public void getUserIdByAuthorizationCode_should_return_null_with_null_code() {
		//given
		String nullCode = null;

		//when
		UUID result = testedRepo.getUserIdByAuthorizationCode(nullCode);
		
		//then
		assertThat(result).isNull();
		verify(testedRepo, never()).processGetUserIdByAuthorizationCode(any());
	}

	@Test
	public void getUserIdByAuthorizationCode_should_return_null_with_blank_code() {
		//given
		String blankCode = "  ";

		//when
		UUID result = testedRepo.getUserIdByAuthorizationCode(blankCode);
		
		//then
		assertThat(result).isNull();
		verify(testedRepo, never()).processGetUserIdByAuthorizationCode(any());
	}
	
	@Test
	public void create_should_do_nothing_with_null_code() {
		//given
		String nullCode = null;

		//when
		testedRepo.create(nullCode, null);
		
		//then
		verify(testedRepo, never()).processCreate(any(), any());
	}
	
	@Test
	public void create_should_do_nothing_with_blank_code() {
		//given
		String blankCode = "  ";

		//when
		testedRepo.create(blankCode, UUID.randomUUID());
		
		//then
		verify(testedRepo, never()).processCreate(any(), any());
	}
	
	@Test
	public void create_should_do_nothing_with_null_userId() {
		//given
		UUID nullUserId = null;

		//when
		testedRepo.create("code", nullUserId);
		
		//then
		verify(testedRepo, never()).processCreate(any(), any());
	}
	
	@Test
	public void deleteByCode_should_do_nothing_with_null_code() {
		//given
		String nullCode = null;

		//when
		testedRepo.deleteByCode(nullCode);
		
		//then
		verify(testedRepo, never()).processDeleteByCode(any());
	}
	
	@Test
	public void deleteByCode_should_do_nothing_with_blank_code() {
		//given
		String blankCode = "  ";

		//when
		testedRepo.deleteByCode(blankCode);
		
		//then
		verify(testedRepo, never()).processDeleteByCode(any());
	}
}
