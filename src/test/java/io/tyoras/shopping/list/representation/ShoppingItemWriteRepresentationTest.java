package io.tyoras.shopping.list.representation;

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
import io.tyoras.shopping.infra.rest.error.WebApiException;
import io.tyoras.shopping.list.ShoppingItem;
import io.tyoras.shopping.test.TestHelper;

public class ShoppingItemWriteRepresentationTest {
	
	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();
	
	private static final String REPRESENTATION_AS_JSON = fixture("representations/item_write.json");
	
	@Test(expected = NullPointerException.class)
	public void itemWriteRepresentation_should_fail_without_item() {
		//given
		ShoppingItem nullShoppingItem = null;
		
		//when
		new ShoppingItemWriteRepresentation(nullShoppingItem);
	}
	
	
	@Test(expected = NullPointerException.class)
	public void toShoppingItem_should_fail_without_representation() {
		//given
		ShoppingItemWriteRepresentation nullRepresentation = null;
		
		//when
		try {
			ShoppingItemWriteRepresentation.toShoppingItem(nullRepresentation);
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("Unable to create ShoppingItem from null ShoppingItemWriteRepresentation");
			throw npe;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void toShoppingItem_should_fail_with_invalid_Representation() {
		//given
		@SuppressWarnings("deprecation")
		ShoppingItemWriteRepresentation invalidShoppingItemWriteRepresentation = new ShoppingItemWriteRepresentation(UUID.randomUUID(), " ", 0, " ");
		String expectedMessage = INVALID.getDevReadableMessage("item") + " : Invalid item name";
		
		//when
		try {
			ShoppingItemWriteRepresentation.toShoppingItem(invalidShoppingItemWriteRepresentation);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, ERROR, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void toShoppingItem_should_fail_with_unknow_item_state() {
		//given
		@SuppressWarnings("deprecation")
		ShoppingItemWriteRepresentation invalidShoppingItemWriteRepresentation = new ShoppingItemWriteRepresentation(UUID.randomUUID(), "name", 0, "unknown");
		String expectedMessage = INVALID.getDevReadableMessage("item") + " : Invalid item state";
		
		//when
		try {
			ShoppingItemWriteRepresentation.toShoppingItem(invalidShoppingItemWriteRepresentation);
		} catch(WebApiException wae) {
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
		ShoppingItemWriteRepresentation validShoppingItemWriteRepresentation = new ShoppingItemWriteRepresentation(null, expectedShoppingItem.getName(), expectedShoppingItem.getQuantity(),  expectedShoppingItem.getState().name());
		
		//when
		ShoppingItem result = ShoppingItemWriteRepresentation.toShoppingItem(validShoppingItemWriteRepresentation, expectedShoppingItem.getId());
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(expectedShoppingItem);
	}
	
	@Test
    public void serializesToJSON() throws Exception {
		//given
		ShoppingItemWriteRepresentation representation = getRepresentation();

        final String expectedSerialization = MAPPER.writeValueAsString(MAPPER.readValue(REPRESENTATION_AS_JSON, ShoppingItemWriteRepresentation.class));

        //when
        String serialized = MAPPER.writeValueAsString(representation);
        
        //then
        assertThat(serialized).isEqualTo(expectedSerialization);
    }
	
	@Test
    public void deserializesFromJSON() throws Exception {
		ShoppingItemWriteRepresentation expectedDeserialization = getRepresentation();
        
        //when
		ShoppingItemWriteRepresentation deserialized = MAPPER.readValue(REPRESENTATION_AS_JSON, ShoppingItemWriteRepresentation.class);
        
        //then
        assertThat(deserialized).isEqualTo(expectedDeserialization);
    }
	
	@SuppressWarnings("deprecation")
	private ShoppingItemWriteRepresentation getRepresentation() {
		UUID id = UUID.fromString("85d34e20-aefd-470e-9414-efb852004fa5");
		return new ShoppingItemWriteRepresentation(id, "item", 15, "TO_BUY");
	}
}
