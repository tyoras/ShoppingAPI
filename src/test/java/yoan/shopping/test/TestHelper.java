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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
import yoan.shopping.list.ShoppingItem;
import yoan.shopping.list.ShoppingList;
import yoan.shopping.user.SecuredUser;
import yoan.shopping.user.User;

import com.google.common.collect.ImmutableList;

/**
 * Unit test helper
 * @author yoan
 */
public class TestHelper {
	private TestHelper() { }
	
	private static Random rand = new Random();
	private static final ImmutableList<String> SYLLABS = ImmutableList.<String>of("yo", "an", "ad", "ri", "en", "e", "mi", "li", "en");
	
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
	
	public static void assertWebApiException(WebApiException wae, Status expectedStatus, Level expectedLevel, ErrorCode expectedErrorCode, ErrorMessage expectedMessage) {
		assertWebApiException(wae, expectedStatus, expectedLevel, expectedErrorCode, expectedMessage.getDevReadableMessage());
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
		when(uriBuilder.build(anyVararg())).thenReturn(URI.create(expectedURL));
		UriInfo mockedUriInfo = mock(UriInfo.class);
		when(mockedUriInfo.getAbsolutePath()).thenReturn(URI.create(expectedURL));
		when(mockedUriInfo.getBaseUriBuilder()).thenReturn(uriBuilder);
		when(mockedUriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
		
		return mockedUriInfo;
	}
	
	public static User generateRandomUser() {
		return User.Builder.createDefault()
						   .withRandomId()
						   .withName(generateRandomName())
						   .build();
	}
	
	public static SecuredUser generateRandomSecuredUser() {
		User user = User.Builder.createDefault().withRandomId().build();
		return SecuredUser.Builder.createFrom(user)
								  .withPassword(UUID.randomUUID().toString())
								  .withSalt(UUID.randomUUID().toString())
								  .build();
	}
	
	public static ShoppingItem generateRandomShoppingItem() {
		return ShoppingItem.Builder.createDefault()
								   .withRandomId()
								   .withName(generateRandomName())
								   .withQuantity(generateRandomInt(0, 10))
								   .build();
	}
	
	public static ShoppingList generateRandomShoppingList() {
		int nbItem = generateRandomInt(1, 3);
		List<ShoppingItem> itemList = new ArrayList<>();
		for (int i = 0; i < nbItem; i++) {
			itemList.add(generateRandomShoppingItem());
		}
		return ShoppingList.Builder.createDefault()
								   .withRandomId()
								   .withName(generateRandomName())
								   .withItemList(itemList)
								   .withOwnerId(UUID.randomUUID())
								   .build();
	}
	
	public static String generateRandomName() {
		return generateString(generateRandomInt(1, 3));
	}
	
	public static int generateRandomInt(int min, int max) {
	    return rand.nextInt((max - min) + 1) + min;
	}
	
	public static String generateString(int length) {
	    StringBuilder name = new StringBuilder();
	    for (int i = 0; i < length; i++) {
	    	name.append(SYLLABS.get(rand.nextInt(SYLLABS.size())));
	    }
	    return name.toString();
	}
}
