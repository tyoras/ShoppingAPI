package yoan.shopping.list.representation;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static yoan.shopping.infra.util.error.CommonErrorMessage.INVALID;

import java.util.List;
import java.util.UUID;

import org.junit.Test;

import com.google.common.collect.Lists;

import yoan.shopping.infra.rest.error.WebApiException;
import yoan.shopping.list.ShoppingList;
import yoan.shopping.test.TestHelper;

public class ShoppingListWriteRepresentationTest {
	
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
		} catch(NullPointerException npe) {
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
		} catch(WebApiException wae) {
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
}
