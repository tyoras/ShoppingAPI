package io.tyoras.shopping.root.representation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import io.dropwizard.jackson.Jackson;
import io.tyoras.shopping.infra.rest.Link;
import io.tyoras.shopping.root.BuildInfo;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class RootRepresentationTest {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    private static final String REPRESENTATION_AS_JSON = fixture("representations/root.json");

    @Test
    public void serializesToJSON() throws Exception {
        //given
        RootRepresentation representation = getRepresentation();

        final String expectedSerialization = MAPPER.writeValueAsString(MAPPER.readValue(REPRESENTATION_AS_JSON, RootRepresentation.class));

        //when
        String serialized = MAPPER.writeValueAsString(representation);

        //then
        assertThat(serialized).isEqualTo(expectedSerialization);
    }

    @Test
    public void deserializesFromJSON() throws Exception {
        RootRepresentation expectedDeserialization = getRepresentation();

        //when
        RootRepresentation deserialized = MAPPER.readValue(REPRESENTATION_AS_JSON, RootRepresentation.class);

        //then
        assertThat(deserialized).isEqualTo(expectedDeserialization);
    }

    private RootRepresentation getRepresentation() {
        LocalDateTime buildDate = LocalDateTime.of(2016, 12, 29, 23, 29, 42);
        BuildInfo buildInfo = new BuildInfo("0.2.3-SNAPSHOT", buildDate);
        UUID connectedUserid = UUID.fromString("ed2979db-21f5-4834-8449-417105e03e6a");
        List<Link> links = Lists.newArrayList(Link.self("http://shopping-app.io"), new Link("google", "http://www.google.com"));
        return new RootRepresentation(buildInfo, connectedUserid, links);
    }

}
