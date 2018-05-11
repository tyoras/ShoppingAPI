package io.tyoras.shopping.client.app.representation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import io.dropwizard.jackson.Jackson;
import io.tyoras.shopping.client.app.ClientApp;
import io.tyoras.shopping.infra.rest.Link;
import io.tyoras.shopping.infra.rest.error.WebApiException;
import io.tyoras.shopping.test.TestHelper;
import org.junit.Test;

import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.UUID;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static io.tyoras.shopping.infra.rest.error.Level.ERROR;
import static io.tyoras.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static io.tyoras.shopping.infra.util.error.CommonErrorMessage.INVALID;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ClientAppRepresentationTest {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    private static final String REPRESENTATION_AS_JSON = fixture("representations/client_app_read.json");

    @Test(expected = NullPointerException.class)
    public void clientAppRepresentation_should_fail_without_clientApp() {
        //given
        ClientApp nullClientApp = null;

        //when
        new ClientAppRepresentation(nullClientApp, mock(UriInfo.class));
    }

    @Test(expected = NullPointerException.class)
    public void clientAppRepresentation_should_fail_without_UriInfo() {
        //given
        UriInfo nullUriInfo = null;

        //when
        new ClientAppRepresentation(TestHelper.generateRandomClientApp(), nullUriInfo);
    }

    @Test
    public void clientAppRepresentation_should_contains_clientApp_self_link() {
        //given
        ClientApp clientApp = TestHelper.generateRandomClientApp();
        String expectedURL = "http://test";
        UriInfo mockedUriInfo = TestHelper.mockUriInfo(expectedURL);

        //when
        ClientAppRepresentation result = new ClientAppRepresentation(clientApp, mockedUriInfo);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(clientApp.getId());
        assertThat(result.getLinks()).isNotNull();
        assertThat(result.getLinks()).isNotEmpty();
        assertThat(result.getLinks()).contains(Link.self(expectedURL));
    }

    @Test(expected = NullPointerException.class)
    public void toClientApp_should_fail_without_representation() {
        //given
        ClientAppRepresentation nullRepresentation = null;

        //when
        try {
            ClientAppRepresentation.toClientApp(nullRepresentation);
        } catch (NullPointerException npe) {
            //then
            assertThat(npe.getMessage()).isEqualTo("Unable to create client application from null ClientAppRepresentation");
            throw npe;
        }
    }

    @Test(expected = WebApiException.class)
    public void toClientApp_should_fail_with_invalid_Representation() {
        //given
        @SuppressWarnings("deprecation")
        ClientAppRepresentation invalidClientAppRepresentation = new ClientAppRepresentation(UUID.randomUUID(), " ", UUID.randomUUID(), "http://test", " ", Lists.newArrayList());
        String expectedMessage = INVALID.getDevReadableMessage("client application") + " : Invalid app name";

        //when
        try {
            ClientAppRepresentation.toClientApp(invalidClientAppRepresentation);
        } catch (WebApiException wae) {
            //then
            TestHelper.assertWebApiException(wae, BAD_REQUEST, ERROR, API_RESPONSE, expectedMessage);
            throw wae;
        }
    }

    @Test(expected = WebApiException.class)
    public void toClientApp_should_fail_with_invalid_redirect_URI() {
        //given
        @SuppressWarnings("deprecation")
        ClientAppRepresentation invalidClientAppRepresentation = new ClientAppRepresentation(UUID.randomUUID(), " ", UUID.randomUUID(), " ", " ", Lists.newArrayList());
        String expectedMessage = INVALID.getDevReadableMessage("client application");

        //when
        try {
            ClientAppRepresentation.toClientApp(invalidClientAppRepresentation);
        } catch (WebApiException wae) {
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
        ClientAppRepresentation validClientAppRepresentation = new ClientAppRepresentation(expectedClientApp.getId(), expectedClientApp.getName(), expectedClientApp.getOwnerId(), expectedClientApp.getRedirectURI().toString(), expectedClientApp.getSecret(), Lists.newArrayList());

        //when
        ClientApp result = ClientAppRepresentation.toClientApp(validClientAppRepresentation);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(expectedClientApp.getName());
        assertThat(result.getOwnerId()).isEqualTo(expectedClientApp.getOwnerId());
        assertThat(result.getRedirectURI()).isEqualTo(expectedClientApp.getRedirectURI());
    }

    @Test
    public void serializesToJSON() throws Exception {
        //given
        ClientAppRepresentation representation = getRepresentation();

        final String expectedSerialization = MAPPER.writeValueAsString(MAPPER.readValue(REPRESENTATION_AS_JSON, ClientAppRepresentation.class));

        //when
        String serialized = MAPPER.writeValueAsString(representation);

        //then
        assertThat(serialized).isEqualTo(expectedSerialization);
    }

    @Test
    public void deserializesFromJSON() throws Exception {
        ClientAppRepresentation expectedDeserialization = getRepresentation();

        //when
        ClientAppRepresentation deserialized = MAPPER.readValue(REPRESENTATION_AS_JSON, ClientAppRepresentation.class);

        //then
        assertThat(deserialized).isEqualTo(expectedDeserialization);
    }

    @SuppressWarnings("deprecation")
    private ClientAppRepresentation getRepresentation() {
        UUID id = UUID.fromString("85d34e20-aefd-470e-9414-efb852004fa3");
        UUID ownerId = UUID.fromString("e1d8c0ec-f27f-4368-8fbc-4a7f49cb106d");
        List<Link> links = Lists.newArrayList(Link.self("http://shopping-app.io"), new Link("google", "http://www.google.com"));
        return new ClientAppRepresentation(id, "riyori", ownerId, "http://localhost:8080", "secret", links);
    }
}
