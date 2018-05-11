package io.tyoras.shopping.list.representation;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import io.tyoras.shopping.infra.rest.error.WebApiException;
import io.tyoras.shopping.list.ShoppingItem;
import io.tyoras.shopping.test.TestHelper;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static io.tyoras.shopping.infra.rest.error.Level.ERROR;
import static io.tyoras.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static io.tyoras.shopping.infra.util.error.CommonErrorMessage.INVALID;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;

public class ShoppingItemRepresentationTest {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    private static final String REPRESENTATION_AS_JSON = fixture("representations/item_read.json");

    @Test(expected = NullPointerException.class)
    public void itemRepresentation_should_fail_without_item() {
        //given
        ShoppingItem nullShoppingItem = null;

        //when
        new ShoppingItemRepresentation(nullShoppingItem);
    }


    @Test(expected = NullPointerException.class)
    public void toShoppingItem_should_fail_without_representation() {
        //given
        ShoppingItemRepresentation nullRepresentation = null;

        //when
        try {
            ShoppingItemRepresentation.toShoppingItem(nullRepresentation);
        } catch (NullPointerException npe) {
            //then
            assertThat(npe.getMessage()).isEqualTo("Unable to create ShoppingItem from null ShoppingItemRepresentation");
            throw npe;
        }
    }

    @Test(expected = WebApiException.class)
    public void toShoppingItem_should_fail_with_invalid_Representation() {
        //given
        @SuppressWarnings("deprecation")
        ShoppingItemRepresentation invalidShoppingItemRepresentation = new ShoppingItemRepresentation(UUID.randomUUID(), " ", 0, " ", LocalDateTime.now(), LocalDateTime.now());
        String expectedMessage = INVALID.getDevReadableMessage("item") + " : Invalid item name";

        //when
        try {
            ShoppingItemRepresentation.toShoppingItem(invalidShoppingItemRepresentation);
        } catch (WebApiException wae) {
            //then
            TestHelper.assertWebApiException(wae, BAD_REQUEST, ERROR, API_RESPONSE, expectedMessage);
            throw wae;
        }
    }

    @Test(expected = WebApiException.class)
    public void toShoppingItem_should_fail_with_unknow_item_state() {
        //given
        @SuppressWarnings("deprecation")
        ShoppingItemRepresentation invalidShoppingItemRepresentation = new ShoppingItemRepresentation(UUID.randomUUID(), "name", 0, "unknown", LocalDateTime.now(), LocalDateTime.now());
        String expectedMessage = INVALID.getDevReadableMessage("item") + " : Invalid item state";

        //when
        try {
            ShoppingItemRepresentation.toShoppingItem(invalidShoppingItemRepresentation);
        } catch (WebApiException wae) {
            //then
            TestHelper.assertWebApiException(wae, BAD_REQUEST, ERROR, API_RESPONSE, expectedMessage);
            throw wae;
        }
    }

    @Test
    public void toShoppingItem_should_work() {
        //given
        ShoppingItem expectedShoppingItem = TestHelper.generateRandomShoppingItem();
        @SuppressWarnings("deprecation")
        ShoppingItemRepresentation validShoppingItemRepresentation = new ShoppingItemRepresentation(expectedShoppingItem.getId(), expectedShoppingItem.getName(), expectedShoppingItem.getQuantity(), expectedShoppingItem.getState().name(), expectedShoppingItem.getCreationDate(), expectedShoppingItem.getLastUpdate());

        //when
        ShoppingItem result = ShoppingItemRepresentation.toShoppingItem(validShoppingItemRepresentation);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedShoppingItem);
    }

    @Test
    public void serializesToJSON() throws Exception {
        //given
        ShoppingItemRepresentation representation = getRepresentation();

        final String expectedSerialization = MAPPER.writeValueAsString(MAPPER.readValue(REPRESENTATION_AS_JSON, ShoppingItemRepresentation.class));

        //when
        String serialized = MAPPER.writeValueAsString(representation);

        //then
        assertThat(serialized).isEqualTo(expectedSerialization);
    }

    @Test
    public void deserializesFromJSON() throws Exception {
        ShoppingItemRepresentation expectedDeserialization = getRepresentation();

        //when
        ShoppingItemRepresentation deserialized = MAPPER.readValue(REPRESENTATION_AS_JSON, ShoppingItemRepresentation.class);

        //then
        assertThat(deserialized).isEqualTo(expectedDeserialization);
    }

    @SuppressWarnings("deprecation")
    private ShoppingItemRepresentation getRepresentation() {
        UUID id = UUID.fromString("85d34e20-aefd-470e-9414-efb852004fa5");
        LocalDateTime creationDate = LocalDateTime.of(2016, 12, 29, 23, 29, 42);
        LocalDateTime updateDate = LocalDateTime.of(2016, 12, 29, 23, 30, 42);
        return new ShoppingItemRepresentation(id, "item", 15, "TO_BUY", creationDate, updateDate);
    }
}
