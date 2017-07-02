package io.tyoras.shopping.client.app.representation;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static io.tyoras.shopping.infra.rest.error.Level.ERROR;
import static io.tyoras.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static io.tyoras.shopping.infra.util.error.CommonErrorMessage.INVALID;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.jackson.Jackson;
import io.tyoras.shopping.client.app.ClientApp;
import io.tyoras.shopping.infra.rest.error.WebApiException;
import io.tyoras.shopping.test.TestHelper;

public class ClientAppWriteRepresentationTest {
	
	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();
	
	private static final String REPRESENTATION_AS_JSON = fixture("representations/client_app_write.json");
	
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
	
	@Test
    public void serializesToJSON() throws Exception {
		//given
		ClientAppWriteRepresentation representation = getRepresentation();

        final String expectedSerialization = MAPPER.writeValueAsString(MAPPER.readValue(REPRESENTATION_AS_JSON, ClientAppWriteRepresentation.class));

        //when
        String serialized = MAPPER.writeValueAsString(representation);
        
        //then
        assertThat(serialized).isEqualTo(expectedSerialization);
    }

	@Test
    public void deserializesFromJSON() throws Exception {
		ClientAppWriteRepresentation expectedDeserialization = getRepresentation();
        
        //when
		ClientAppWriteRepresentation deserialized = MAPPER.readValue(REPRESENTATION_AS_JSON, ClientAppWriteRepresentation.class);
        
        //then
        assertThat(deserialized).isEqualTo(expectedDeserialization);
    }
	
	@SuppressWarnings("deprecation")
	private ClientAppWriteRepresentation getRepresentation() {
		UUID ownerId = UUID.fromString("e1d8c0ec-f27f-4368-8fbc-4a7f49cb106d");
		return new ClientAppWriteRepresentation("riyori", ownerId, "http://localhost:8080");
	}
}
