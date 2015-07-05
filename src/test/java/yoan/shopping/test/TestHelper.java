/**
 * 
 */
package yoan.shopping.test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.UUID;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import yoan.shopping.infra.rest.error.ErrorRepresentation;
import yoan.shopping.infra.rest.error.Level;
import yoan.shopping.infra.rest.error.WebApiException;
import yoan.shopping.infra.util.error.ApplicationException;
import yoan.shopping.infra.util.error.ErrorCode;
import yoan.shopping.infra.util.error.ErrorMessage;
import yoan.shopping.user.SecuredUser;
import yoan.shopping.user.User;

/**
 * Unit test helper
 * @author yoan
 */
public class TestHelper {
	private TestHelper() { }
	
	public static void assertApplicationException(ApplicationException ae, Level expectedLevel, ErrorCode expectedErrorCode, String expectedMessage) {
		assertThat(ae.getLevel()).isEqualTo(expectedLevel);
		assertThat(ae.getErrorCode()).isEqualTo(expectedErrorCode);
		assertThat(ae.getMessage()).as("message").isEqualTo(expectedMessage);
	}
	
	public static void assertApplicationException(ApplicationException ae, Level expectedLevel, ErrorCode expectedErrorCode, ErrorMessage expectedMessage) {
		assertApplicationException(ae, expectedLevel, expectedErrorCode, expectedMessage.getDevReadableMessage());
	}
	
	public static void assertWebApiException(WebApiException wae, Status expectedStatus, Level expectedLevel, ErrorCode expectedErrorCode, String expectedMessage) {
		assertThat(wae.getStatus()).isEqualTo(expectedStatus);
		assertApplicationException(wae, expectedLevel, expectedErrorCode, expectedMessage);
	}
	
	public static void assertErrorResponse(Response errorResponse, Status expectedStatus, Level expectedLevel, String expectedErrorCode, String expectedMessage) {
		assertThat(errorResponse).isNotNull();
		assertThat(errorResponse.getStatus()).isEqualTo(expectedStatus.getStatusCode());
		ErrorRepresentation payload = (ErrorRepresentation) errorResponse.getEntity();
		assertThat(payload).isNotNull();
		assertThat(payload.getLevel()).isEqualTo(expectedLevel);
		assertThat(payload.getCode()).isEqualTo(expectedErrorCode);
		assertThat(payload.getMessage()).isEqualTo(expectedMessage);
	}
	
	/**
	 * Create UriInfo mock for Resource and link creation purpose
	 * @param expectedURL : expected URL
	 * @return
	 */
	public static UriInfo mockUriInfo(String expectedURL) {
		UriBuilder uriBuilder = mock(UriBuilder.class);
		when(uriBuilder.path((String) anyVararg())).thenReturn(uriBuilder);
		when(uriBuilder.path(any(Class.class))).thenReturn(uriBuilder);
		when(uriBuilder.path(any(Class.class), anyString())).thenReturn(uriBuilder);
		when(uriBuilder.build()).thenReturn(URI.create(expectedURL));
		when(uriBuilder.build(any())).thenReturn(URI.create(expectedURL));
		UriInfo mockedUriInfo = mock(UriInfo.class);
		when(mockedUriInfo.getAbsolutePath()).thenReturn(URI.create(expectedURL));
		when(mockedUriInfo.getBaseUriBuilder()).thenReturn(uriBuilder);
		when(mockedUriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
		
		return mockedUriInfo;
	}
	
	public static User generateRandomUser() {
		return User.Builder.createDefault()
						   .withRandomId()
						   .withName("name " + UUID.randomUUID())
						   .build();
	}
	
	public static SecuredUser generateRandomSecuredUser() {
		User user = User.Builder.createDefault().withRandomId().build();
		return SecuredUser.Builder.createFrom(user)
								  .withPassword(UUID.randomUUID().toString())
								  .withSalt(UUID.randomUUID().toString())
								  .build();
	}
}
