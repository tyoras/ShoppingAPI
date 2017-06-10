package io.tyoras.shopping.list.representation;

import static io.tyoras.shopping.infra.rest.error.Level.ERROR;
import static io.tyoras.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static io.tyoras.shopping.infra.util.error.CommonErrorMessage.INVALID;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.Test;

import io.tyoras.shopping.infra.rest.error.WebApiException;
import io.tyoras.shopping.list.ShoppingItem;
import io.tyoras.shopping.list.representation.ShoppingItemRepresentation;
import io.tyoras.shopping.test.TestHelper;

public class ShoppingItemRepresentationTest {
	
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
		} catch(NullPointerException npe) {
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
		ShoppingItemRepresentation invalidShoppingItemRepresentation = new ShoppingItemRepresentation(UUID.randomUUID(), "name", 0, "unknown", LocalDateTime.now(), LocalDateTime.now());
		String expectedMessage = INVALID.getDevReadableMessage("item") + " : Invalid item state";
		
		//when
		try {
			ShoppingItemRepresentation.toShoppingItem(invalidShoppingItemRepresentation);
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
		ShoppingItemRepresentation validShoppingItemRepresentation = new ShoppingItemRepresentation(expectedShoppingItem.getId(), expectedShoppingItem.getName(), expectedShoppingItem.getQuantity(),  expectedShoppingItem.getState().name(), expectedShoppingItem.getCreationDate(), expectedShoppingItem.getLastUpdate());
		
		//when
		ShoppingItem result = ShoppingItemRepresentation.toShoppingItem(validShoppingItemRepresentation);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(expectedShoppingItem);
	}
}
