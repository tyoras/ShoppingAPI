package yoan.shopping.client.app.representation;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.fest.assertions.api.Assertions.assertThat;
import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static yoan.shopping.infra.util.error.CommonErrorMessage.INVALID;

import java.util.UUID;

import org.junit.Test;

import yoan.shopping.client.app.ClientApp;
import yoan.shopping.infra.rest.error.WebApiException;
import yoan.shopping.test.TestHelper;

public class ClientAppWriteRepresentationTest {
	
	@Test(expected = NullPointerException.class)
	public void clientAppWriteRepresentation_should_fail_without_clientApp() {
		//given
		ClientApp nullClientApp = null;
		
		//when
		new ClientAppWriteRepresentation(nullClientApp);
	}
	
	@Test(expected = NullPointerException.class)
	public void toClientApp_should_fail_without_representation() {
		//given
		UUID appId = UUID.randomUUID();
		ClientAppWriteRepresentation nullRepresentation = null;
		
		//when
		try {
			ClientAppWriteRepresentation.toClientApp(nullRepresentation, appId);
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("Unable to create client application from null ClientAppWriteRepresentation");
			throw npe;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void toClientApp_should_fail_without_app_id() {
		//given
		UUID nullAppId = null;
		ClientApp expectedClientApp = TestHelper.generateRandomClientApp();
		@SuppressWarnings("deprecation")
		ClientAppWriteRepresentation validclientAppWriteRepresentation = new ClientAppWriteRepresentation(expectedClientApp.getName(), expectedClientApp.getOwnerId(), expectedClientApp.getRedirectURI().toString());
		String expectedMessage = INVALID.getDevReadableMessage("client application") + " : App Id is mandatory";
		
		//when
		try {
			ClientAppWriteRepresentation.toClientApp(validclientAppWriteRepresentation, nullAppId);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, ERROR, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void toClientApp_should_fail_with_invalid_Representation() {
		//given
		UUID appId = UUID.randomUUID();
		@SuppressWarnings("deprecation")
		ClientAppWriteRepresentation invalidclientAppWriteRepresentation = new ClientAppWriteRepresentation(" ", UUID.randomUUID(), "http://test");
		String expectedMessage = INVALID.getDevReadableMessage("client application") + " : Invalid app name";
		
		//when
		try {
			ClientAppWriteRepresentation.toClientApp(invalidclientAppWriteRepresentation, appId);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, ERROR, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void toClientApp_should_fail_with_invalid_redirect_URI() {
		//given
		UUID appId = UUID.randomUUID();
		@SuppressWarnings("deprecation")
		ClientAppWriteRepresentation invalidclientAppWriteRepresentation = new ClientAppWriteRepresentation(" ", UUID.randomUUID(), " ");
		String expectedMessage = INVALID.getDevReadableMessage("client application");
		
		//when
		try {
			ClientAppWriteRepresentation.toClientApp(invalidclientAppWriteRepresentation, appId);
		} catch(WebApiException wae) {
		//then
			assertThat(wae.getStatus()).isEqualTo(BAD_REQUEST);
			assertThat(wae.getLevel()).isEqualTo(ERROR);
			assertThat(wae.getErrorCode()).isEqualTo(API_RESPONSE);
			assertThat(wae.getMessage()).startsWith(expectedMessage);
			throw wae;
		}
	}
	
	@Test
	public void toClientApp_should_work() {
		//given
		ClientApp expectedClientApp = TestHelper.generateRandomClientApp();
		@SuppressWarnings("deprecation")
		ClientAppWriteRepresentation validclientAppWriteRepresentation = new ClientAppWriteRepresentation(expectedClientApp.getName(), expectedClientApp.getOwnerId(), expectedClientApp.getRedirectURI().toString());
		
		//when
		ClientApp result = ClientAppWriteRepresentation.toClientApp(validclientAppWriteRepresentation, expectedClientApp.getId());
		
		//then
		assertThat(result).isNotNull();
		assertThat(result.getName()).isEqualTo(expectedClientApp.getName());
		assertThat(result.getOwnerId()).isEqualTo(expectedClientApp.getOwnerId());
		assertThat(result.getRedirectURI()).isEqualTo(expectedClientApp.getRedirectURI());
	}
}
