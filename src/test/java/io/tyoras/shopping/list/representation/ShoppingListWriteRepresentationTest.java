package io.tyoras.shopping.list.representation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import io.dropwizard.jackson.Jackson;
import io.tyoras.shopping.infra.rest.error.WebApiException;
import io.tyoras.shopping.list.ShoppingList;
import io.tyoras.shopping.test.TestHelper;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static io.tyoras.shopping.infra.rest.error.Level.ERROR;
import static io.tyoras.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static io.tyoras.shopping.infra.util.error.CommonErrorMessage.INVALID;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;

public class ShoppingListWriteRepresentationTest {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    private static final String REPRESENTATION_AS_JSON = fixture("representations/list_write.json");

    @Test(expected = NullPointerException.class)
    public void shoppingListWriteRepresentation_should_fail_without_list() {
        //given
        ShoppingList nullShoppingList = null;

        //when
        new ShoppingListWriteRepresentation(nullShoppingList);
    }

    @Test(expected = NullPointerException.class)
    public void toShoppingList_should_fail_without_representation() {
        //given
        UUID listId = UUID.randomUUID();
        ShoppingListWriteRepresentation nullRepresentation = null;

        //when
        try {
            ShoppingListWriteRepresentation.toShoppingList(nullRepresentation, listId);
        } catch (NullPointerException npe) {
            //then
            assertThat(npe.getMessage()).isEqualTo("Unable to create ShoppingList from null ShoppingListWriteRepresentation");
            throw npe;
        }
    }

    @Test(expected = WebApiException.class)
    public void toShoppingList_should_fail_with_invalid_representation() {
        //given
        UUID listId = UUID.randomUUID();
        @SuppressWarnings("deprecation")
        ShoppingListWriteRepresentation invalidShoppingListWriteRepresentation = new ShoppingListWriteRepresentation(" ", UUID.randomUUID(), Lists.newArrayList());
        String expectedMessage = INVALID.getDevReadableMessage("list") + " : Invalid list name";

        //when
        try {
            ShoppingListWriteRepresentation.toShoppingList(invalidShoppingListWriteRepresentation, listId);
        } catch (WebApiException wae) {
            //then
            TestHelper.assertWebApiException(wae, BAD_REQUEST, ERROR, API_RESPONSE, expectedMessage);
            throw wae;
        }
    }

    @Test
    public void toShoppingList_should_work() {
        //given
        ShoppingList expectedShoppingList = TestHelper.generateRandomShoppingList();
        List<ShoppingItemWriteRepresentation> itemList = ShoppingItemWriteRepresentation.extractItemListRepresentations(expectedShoppingList.getItemList());
        @SuppressWarnings("deprecation")
        ShoppingListWriteRepresentation validShoppingListWriteRepresentation = new ShoppingListWriteRepresentation(expectedShoppingList.getName(), expectedShoppingList.getOwnerId(), itemList);

        //when
        ShoppingList result = ShoppingListWriteRepresentation.toShoppingList(validShoppingListWriteRepresentation, expectedShoppingList.getId());

        //then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedShoppingList);
        assertThat(result.getItemList()).isNotNull();
        assertThat(result.getItemList()).hasSameSizeAs(expectedShoppingList.getItemList());
    }

    @Test
    public void serializesToJSON() throws Exception {
        //given
        ShoppingListWriteRepresentation representation = getRepresentation();

        final String expectedSerialization = MAPPER.writeValueAsString(MAPPER.readValue(REPRESENTATION_AS_JSON, ShoppingListWriteRepresentation.class));

        //when
        String serialized = MAPPER.writeValueAsString(representation);

        //then
        assertThat(serialized).isEqualTo(expectedSerialization);
    }

    @Test
    public void deserializesFromJSON() throws Exception {
        ShoppingListWriteRepresentation expectedDeserialization = getRepresentation();

        //when
        ShoppingListWriteRepresentation deserialized = MAPPER.readValue(REPRESENTATION_AS_JSON, ShoppingListWriteRepresentation.class);

        //then
        assertThat(deserialized).isEqualTo(expectedDeserialization);
    }

    @SuppressWarnings("deprecation")
    private ShoppingListWriteRepresentation getRepresentation() {
        UUID ownerId = UUID.fromString("85d34e20-aefd-470e-9414-efb852004fa7");
        UUID id1 = UUID.fromString("85d34e20-aefd-470e-9414-efb852004fa5");
        ShoppingItemWriteRepresentation item1 = new ShoppingItemWriteRepresentation(id1, "item1", 15, "TO_BUY");
        UUID id2 = UUID.fromString("85d34e20-aefd-470e-9414-efb852004fa8");
        ShoppingItemWriteRepresentation item2 = new ShoppingItemWriteRepresentation(id2, "item2", 18, "BOUGHT");
        return new ShoppingListWriteRepresentation("list", ownerId, Lists.newArrayList(item1, item2));

    }
}
