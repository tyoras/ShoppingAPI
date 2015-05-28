package yoan.shopping.infra.config.filter;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static yoan.shopping.infra.config.filter.AuthenticationFilter.USER_ID_HEADER;
import static yoan.shopping.infra.util.error.CommonErrorMessage.INVALID;
import static yoan.shopping.infra.util.error.CommonErrorMessage.MISSING;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import yoan.shopping.test.FakeEnumeration;
import yoan.shopping.user.User;
import yoan.shopping.user.repository.UserRepository;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationFilterTest {
	
	private static final UUID EXISTING_USER_ID = UUID.randomUUID();
	private static final User EXISTING_USER = User.Builder.createDefault().withId(EXISTING_USER_ID).build();
	@Mock
	UserRepository userRepo;
	@Mock
	HttpServletRequest request;
	@Mock
	HttpServletResponse response;
	@Mock
	FilterChain filterChain;
	
	@InjectMocks
	AuthenticationFilter testedFilter;
	
	@Before
	public void setUpMocks() {
		when(userRepo.getById(EXISTING_USER_ID)).thenReturn(EXISTING_USER);
		when(request.getHeaderNames()).thenReturn(new FakeEnumeration<String>());
		
	}
	
	@Test
	public void doFilter_should_add_user_info_in_request_attribute_with_a_valid_user_id() throws IOException, ServletException {
		//given
		when(request.getHeader(USER_ID_HEADER)).thenReturn(EXISTING_USER_ID.toString());
		
		//when
		testedFilter.doFilter(request, response, filterChain);
		
		//then
		verify(request).setAttribute(any(), eq(EXISTING_USER));
	}
	
	@Test
	public void doFilter_should_add_default_user_info_in_request_attribute_with_an_unexisting_user_id() throws IOException, ServletException {
		//given
		String unexistingUserId = UUID.randomUUID().toString();
		String expectedMsg = "Trying to connect with unknown user : " + unexistingUserId;
		when(request.getHeader(USER_ID_HEADER)).thenReturn(unexistingUserId);
		
		//when
		testedFilter.doFilter(request, response, filterChain);
		
		//then
		verify(request).setAttribute(any(), eq(User.DEFAULT));
		verify(response).sendError(UNAUTHORIZED.getStatusCode(), expectedMsg);
	}
	
	@Test
	public void doFilter_should_add_default_user_info_in_request_attribute_with_an_invalid_user_id() throws IOException, ServletException {
		//given
		String invalidUserId = "invalid UUID";
		String expectedMsg = INVALID.getHumanReadableMessage("header " + USER_ID_HEADER + " : " + invalidUserId);
		when(request.getHeader(USER_ID_HEADER)).thenReturn(invalidUserId);
		
		
		//when
		testedFilter.doFilter(request, response, filterChain);
		
		//thenx
		verify(request).setAttribute(any(), eq(User.DEFAULT));
		verify(response).sendError(UNAUTHORIZED.getStatusCode(), expectedMsg);
	}
	
	@Test
	public void doFilter_should_add_default_user_info_in_request_attribute_with_a_blank_user_id_header() throws IOException, ServletException {
		//given
		String blankUserId = "  ";
		String expectedMsg = MISSING.getHumanReadableMessage("header " + USER_ID_HEADER);
		when(request.getHeader(USER_ID_HEADER)).thenReturn(blankUserId);
		
		//when
		testedFilter.doFilter(request, response, filterChain);
		
		//thenx
		verify(request).setAttribute(any(), eq(User.DEFAULT));
		verify(response).sendError(UNAUTHORIZED.getStatusCode(), expectedMsg);
	}
	
	@Test
	public void doFilter_should_add_default_user_info_in_request_attribute_without_user_id_header() throws IOException, ServletException {
		//given
		String blankUserId = "  ";
		String expectedMsg = MISSING.getHumanReadableMessage("header " + USER_ID_HEADER);
		when(request.getHeader(USER_ID_HEADER)).thenReturn(blankUserId);
		
		//when
		testedFilter.doFilter(request, response, filterChain);
		
		//thenx
		verify(request).setAttribute(any(), eq(User.DEFAULT));
		verify(response).sendError(UNAUTHORIZED.getStatusCode(), expectedMsg);
	}
}
