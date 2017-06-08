package yoan.shopping.list.representation;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static yoan.shopping.infra.util.error.CommonErrorMessage.INVALID;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.UriInfo;

import org.junit.Test;

import com.google.common.collect.Lists;

import yoan.shopping.infra.rest.Link;
import yoan.shopping.infra.rest.error.WebApiException;
import yoan.shopping.list.ShoppingList;
import yoan.shopping.test.TestHelper;

public class ShoppingListRepresentationTest {
	
	@Test(expected = NullPointerException.class)
	public void shoppingListRepresentation_should_fail_without_list() {
		//given
		ShoppingList nullShoppingList = null;
		
		//when
		new ShoppingListRepresentation(nullShoppingList, mock(UriInfo.class));
	}
	
	@Test(expected = NullPointerException.class)
	public void shoppingListRepresentation_should_fail_without_UriInfo() {
		//given
		UriInfo nullUriInfo = null;
		
		//when
		new ShoppingListRepresentation(TestHelper.generateRandomShoppingList(), nullUriInfo);
	}
	
	@Test
	public void shoppingListRepresentation_should_contains_list_self_link() {
		//given
		ShoppingList list = TestHelper.generateRandomShoppingList();
		String expectedURL = "http://test";
		UriInfo mockedUriInfo = TestHelper.mockUriInfo(expectedURL);
		
		//when
		ShoppingListRepresentation result = new ShoppingListRepresentation(list, mockedUriInfo);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(list.getId());
		assertThat(result.getLinks()).isNotNull();
		assertThat(result.getLinks()).isNotEmpty();
		assertThat(result.getLinks()).contains(Link.self(expectedURL));
	}
	
	@Test(expected = NullPointerException.class)
	public void toShoppingList_should_fail_without_representation() {
		//given
		ShoppingListRepresentation nullRepresentation = null;
		
		//when
		try {
			ShoppingListRepresentation.toShoppingList(nullRepresentation);
		} catch(NullPointerException npe) {
		//then
			assertThat(npe.getMessage()).isEqualTo("Unable to create ShoppingList from null ShoppingListRepresentation");
			throw npe;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void toShoppingList_should_fail_with_invalid_representation() {
		//given
		@SuppressWarnings("deprecation")
		ShoppingListRepresentation invalidShoppingListRepresentation = new ShoppingListRepresentation(UUID.randomUUID(), " ", UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now(), Lists.newArrayList(), Lists.newArrayList());
		String expectedMessage = INVALID.getDevReadableMessage("list") + " : Invalid list name";
		
		//when
		try {
			ShoppingListRepresentation.toShoppingList(invalidShoppingListRepresentation);
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
		List<ShoppingItemRepresentation> itemList = ShoppingItemRepresentation.extractItemListRepresentations(expectedShoppingList.getItemList());
		@SuppressWarnings("deprecation")
		ShoppingListRepresentation validShoppingListRepresentation = new ShoppingListRepresentation(expectedShoppingList.getId(), expectedShoppingList.getName(), expectedShoppingList.getOwnerId(), expectedShoppingList.getCreationDate(), expectedShoppingList.getLastUpdate(), itemList, Lists.newArrayList());
		
		//when
		ShoppingList result = ShoppingListRepresentation.toShoppingList(validShoppingListRepresentation);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(expectedShoppingList);
		assertThat(result.getItemList()).isEqualTo(expectedShoppingList.getItemList());
	}
}
