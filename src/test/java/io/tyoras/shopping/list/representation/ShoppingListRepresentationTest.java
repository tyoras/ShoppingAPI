package io.tyoras.shopping.list.representation;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static io.tyoras.shopping.infra.rest.error.Level.ERROR;
import static io.tyoras.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static io.tyoras.shopping.infra.util.error.CommonErrorMessage.INVALID;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.UriInfo;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import io.dropwizard.jackson.Jackson;
import io.tyoras.shopping.infra.rest.Link;
import io.tyoras.shopping.infra.rest.error.WebApiException;
import io.tyoras.shopping.list.ShoppingList;
import io.tyoras.shopping.list.representation.ShoppingItemRepresentation;
import io.tyoras.shopping.list.representation.ShoppingListRepresentation;
import io.tyoras.shopping.test.TestHelper;

public class ShoppingListRepresentationTest {
	
	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();
	
	private static final String REPRESENTATION_AS_JSON = fixture("representations/list_read.json");
	
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
	
	@Test
    public void serializesToJSON() throws Exception {
		//given
		ShoppingListRepresentation representation = getRepresentation();

        final String expectedSerialization = MAPPER.writeValueAsString(MAPPER.readValue(REPRESENTATION_AS_JSON, ShoppingListRepresentation.class));

        //when
        String serialized = MAPPER.writeValueAsString(representation);
        
        //then
        assertThat(serialized).isEqualTo(expectedSerialization);
    }
	
	@Test
    public void deserializesFromJSON() throws Exception {
		ShoppingListRepresentation expectedDeserialization = getRepresentation();
        
        //when
		ShoppingListRepresentation deserialized = MAPPER.readValue(REPRESENTATION_AS_JSON, ShoppingListRepresentation.class);
        
        //then
        assertThat(deserialized).isEqualTo(expectedDeserialization);
    }
	
	@SuppressWarnings("deprecation")
	private ShoppingListRepresentation getRepresentation() {
		UUID id = UUID.fromString("85d34e20-aefd-470e-9414-efb852004fa9");
		UUID ownerId = UUID.fromString("85d34e20-aefd-470e-9414-efb852004fa7");
		LocalDateTime creationDate = LocalDateTime.of(2016, 12, 29, 23, 29, 42);
		LocalDateTime updateDate = LocalDateTime.of(2016, 12, 29, 23, 30, 42);
		UUID id1 = UUID.fromString("85d34e20-aefd-470e-9414-efb852004fa5");
		LocalDateTime creationDate1 = LocalDateTime.of(2016, 12, 29, 23, 29, 42);
		LocalDateTime updateDate1 = LocalDateTime.of(2016, 12, 29, 23, 30, 42);
		ShoppingItemRepresentation item1 = new ShoppingItemRepresentation(id1, "item1", 15, "TO_BUY", creationDate1, updateDate1);
		UUID id2 = UUID.fromString("85d34e20-aefd-470e-9414-efb852004fa8");
		LocalDateTime creationDate2 = LocalDateTime.of(2016, 12, 29, 23, 29, 43);
		LocalDateTime updateDate2 = LocalDateTime.of(2016, 12, 29, 23, 30, 44);
		ShoppingItemRepresentation item2 = new ShoppingItemRepresentation(id2, "item2", 18, "BOUGHT", creationDate2, updateDate2);
		List<Link> links = Lists.newArrayList(Link.self("http://shopping-app.io"), new Link("google", "http://www.google.com"));
		return new ShoppingListRepresentation(id, "list", ownerId, creationDate, updateDate, Lists.newArrayList(item1, item2), links);
		
	}
}
