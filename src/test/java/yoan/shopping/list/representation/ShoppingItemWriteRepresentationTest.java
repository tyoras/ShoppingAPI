package yoan.shopping.list.representation;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static yoan.shopping.infra.util.error.CommonErrorMessage.INVALID;

import java.util.UUID;

import org.junit.Test;

import yoan.shopping.infra.rest.error.WebApiException;
import yoan.shopping.list.ShoppingItem;
import yoan.shopping.test.TestHelper;

public class ShoppingItemWriteRepresentationTest {
	
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
}
