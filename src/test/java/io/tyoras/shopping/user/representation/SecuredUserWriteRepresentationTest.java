package io.tyoras.shopping.user.representation;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.jackson.Jackson;

public class SecuredUserWriteRepresentationTest {
	
	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();
	
	private static final String REPRESENTATION_AS_JSON = fixture("representations/secured_user_write.json");
	
	@Test
    public void serializesToJSON() throws Exception {
		//given
		SecuredUserWriteRepresentation representation = getRepresentation();

        final String expectedSerialization = MAPPER.writeValueAsString(MAPPER.readValue(REPRESENTATION_AS_JSON, SecuredUserWriteRepresentation.class));

        //when
        String serialized = MAPPER.writeValueAsString(representation);
        
        //then
        assertThat(serialized).isEqualTo(expectedSerialization);
    }
	
	@Test
    public void deserializesFromJSON() throws Exception {
		SecuredUserWriteRepresentation expectedDeserialization = getRepresentation();
        
        //when
		SecuredUserWriteRepresentation deserialized = MAPPER.readValue(REPRESENTATION_AS_JSON, SecuredUserWriteRepresentation.class);
        
        //then
        assertThat(deserialized).isEqualTo(expectedDeserialization);
    }
	
	@SuppressWarnings("deprecation")
	private SecuredUserWriteRepresentation getRepresentation() {
		return new SecuredUserWriteRepresentation("riyori", "adli@enenad.com", "PUBLIC", "secretPassword");
	}

}
