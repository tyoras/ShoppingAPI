package io.tyoras.shopping.user.representation;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static io.tyoras.shopping.infra.rest.error.Level.ERROR;
import static io.tyoras.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static io.tyoras.shopping.infra.util.error.CommonErrorMessage.INVALID;
import static io.tyoras.shopping.user.ProfileVisibility.PUBLIC;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.UriInfo;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import io.dropwizard.jackson.Jackson;
import io.tyoras.shopping.infra.rest.Link;
import io.tyoras.shopping.infra.rest.error.WebApiException;
import io.tyoras.shopping.test.TestHelper;
import io.tyoras.shopping.user.User;
import io.tyoras.shopping.user.representation.UserRepresentation;

public class UserRepresentationTest {
	
	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();
	
	private static final String REPRESENTATION_AS_JSON = fixture("representations/user_read.json");
	
	@Test(expected = NullPointerException.class)
	public void userRepresentation_should_fail_without_user() {
		//given
		User nullUser = null;
		
		//when
		new UserRepresentation(nullUser, mock(UriInfo.class));
	}
	
	@Test(expected = NullPointerException.class)
	public void userRepresentation_should_fail_without_UriInfo() {
		//given
		UriInfo nullUriInfo = null;
		
		//when
		new UserRepresentation(TestHelper.generateRandomUser(), nullUriInfo);
	}
	
	@Test
	public void userRepresentation_should_contains_user_self_link() {
		//given
		User user = TestHelper.generateRandomUser();
		String expectedURL = "http://test";
		UriInfo mockedUriInfo = TestHelper.mockUriInfo(expectedURL);
		
		//when
		UserRepresentation result = new UserRepresentation(user, mockedUriInfo);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(user.getId());
		assertThat(result.getLinks()).isNotNull();
		assertThat(result.getLinks()).isNotEmpty();
		assertThat(result.getLinks()).contains(Link.self(expectedURL));
	}
	
	@Test(expected = NullPointerException.class)
	public void toUser_should_fail_without_representation() {
		//given
		UserRepresentation nullRepresentation = null;
		
		//when
		try {
			UserRepresentation.toUser(nullRepresentation);
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("Unable to create User from null UserRepresentation");
			throw npe;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void toUser_should_fail_with_invalid_Representation() {
		//given
		@SuppressWarnings("deprecation")
		UserRepresentation invalidUserRepresentation = new UserRepresentation(UUID.randomUUID(), " ", " ", PUBLIC.name(), Lists.newArrayList());
		String expectedMessage = INVALID.getDevReadableMessage("user") + " : Invalid user name";
		
		//when
		try {
			UserRepresentation.toUser(invalidUserRepresentation);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, ERROR, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test
	public void toUser_should_work() {
		//given
		User expectedUser = TestHelper.generateRandomUser();
		@SuppressWarnings("deprecation")
		UserRepresentation validUserRepresentation = new UserRepresentation(expectedUser.getId(), expectedUser.getName(), expectedUser.getEmail(), expectedUser.getProfileVisibility().name(), Lists.newArrayList());
		
		//when
		User result = UserRepresentation.toUser(validUserRepresentation);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(expectedUser);
	}
	
	@Test
    public void serializesToJSON() throws Exception {
		//given
		UserRepresentation representation = getRepresentation();

        final String expectedSerialization = MAPPER.writeValueAsString(MAPPER.readValue(REPRESENTATION_AS_JSON, UserRepresentation.class));

        //when
        String serialized = MAPPER.writeValueAsString(representation);
        
        //then
        assertThat(serialized).isEqualTo(expectedSerialization);
    }
	
	@Test
    public void deserializesFromJSON() throws Exception {
		UserRepresentation expectedDeserialization = getRepresentation();
        
        //when
		UserRepresentation deserialized = MAPPER.readValue(REPRESENTATION_AS_JSON, UserRepresentation.class);
        
        //then
        assertThat(deserialized).isEqualTo(expectedDeserialization);
    }
	
	@SuppressWarnings("deprecation")
	private UserRepresentation getRepresentation() {
		UUID id = UUID.fromString("85d34e20-aefd-470e-9414-efb852004fa4");
		List<Link> links = Lists.newArrayList(Link.self("http://shopping-app.io"), new Link("google", "http://www.google.com"));
		return new UserRepresentation(id, "riyori", "adli@enenad.com", "PUBLIC", links);
	}
}
