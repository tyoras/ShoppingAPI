package yoan.shopping.list.resource;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.rest.error.Level.INFO;
import static yoan.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static yoan.shopping.list.ItemState.TO_BUY;
import static yoan.shopping.list.resource.ShoppingItemResourceErrorMessage.ALREADY_EXISTING_ITEM;
import static yoan.shopping.list.resource.ShoppingItemResourceErrorMessage.ITEM_NOT_FOUND;
import static yoan.shopping.list.resource.ShoppingItemResourceErrorMessage.MISSING_ITEM_ID_FOR_UPDATE;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import yoan.shopping.infra.rest.Link;
import yoan.shopping.infra.rest.RestRepresentation;
import yoan.shopping.infra.rest.error.WebApiException;
import yoan.shopping.infra.util.error.ApplicationException;
import yoan.shopping.infra.util.error.ErrorMessage;
import yoan.shopping.infra.util.error.RepositoryErrorCode;
import yoan.shopping.list.ShoppingItem;
import yoan.shopping.list.repository.ShoppingItemRepository;
import yoan.shopping.list.representation.ShoppingItemRepresentation;
import yoan.shopping.list.representation.ShoppingItemWriteRepresentation;
import yoan.shopping.test.TestHelper;
import yoan.shopping.user.User;

@RunWith(MockitoJUnitRunner.class)
public class ShoppingItemResourceTest {

	@Mock
	ShoppingItemRepository mockeItemRepo;
	
	private ShoppingItemResource getShoppingItemResource(User connectedUser) {
		ShoppingItemResource testedResource = new ShoppingItemResource(connectedUser, mockeItemRepo);
		return spy(testedResource);
	}
	
	@Test
	public void getRootLinks_should_contains_self_link() {
		//given
		String expectedURL = "http://test";
		UriInfo mockedUriInfo = TestHelper.mockUriInfo(expectedURL);
		ShoppingItemResource testedResource = getShoppingItemResource(TestHelper.generateRandomUser());
		when(testedResource.getUriInfo()).thenReturn(mockedUriInfo);
		
		//when
		List<Link> links = testedResource.getRootLinks();
		
		//then
		assertThat(links).isNotNull();
		assertThat(links).isNotEmpty();
		assertThat(links).contains(Link.self(expectedURL));
	}
	
	@Test
	public void root_should_work() {
		//given
		String expectedURL = "http://test";
		UriInfo mockedUriInfo = TestHelper.mockUriInfo(expectedURL);
		User connectedUser = TestHelper.generateRandomUser();
		ShoppingItemResource testedResource = getShoppingItemResource(connectedUser);
		when(testedResource.getUriInfo()).thenReturn(mockedUriInfo);
		
		//when
		Response response = testedResource.root();
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
		RestRepresentation representation = (RestRepresentation) response.getEntity();
		assertThat(representation).isNotNull();
		assertThat(representation.getLinks()).contains(Link.self(expectedURL));
	}
	
	@Test
	public void create_should_work_with_valid_input_representation() {
		//given
		String listIdStr = UUID.randomUUID().toString();
		UUID expectedID = UUID.randomUUID();
		String expectedName = "name";
		int expectedQuantity = 10;
		String expectedState = TO_BUY.name();
		
		@SuppressWarnings("deprecation")
		ShoppingItemWriteRepresentation representation = new ShoppingItemWriteRepresentation(expectedID, expectedName, expectedQuantity, expectedState);
		ShoppingItemResource testedResource = getShoppingItemResource(TestHelper.generateRandomUser());
		UriInfo mockedUriInfo = TestHelper.mockUriInfo("http://test");
		when(testedResource.getUriInfo()).thenReturn(mockedUriInfo);
		
		//when
		Response response = testedResource.create(listIdStr, representation);
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(CREATED.getStatusCode());
		ShoppingItemRepresentation itemRepresentation = (ShoppingItemRepresentation) response.getEntity();
		assertThat(itemRepresentation).isNotNull();
		assertThat(itemRepresentation.getId()).isEqualTo(expectedID);
		assertThat(itemRepresentation.getName()).isEqualTo(expectedName);
		assertThat(itemRepresentation.getQuantity()).isEqualTo(expectedQuantity);
		assertThat(itemRepresentation.getState()).isEqualTo(expectedState);
	}
	
	@Test
	public void create_should_work_with_input_representation_without_id() {
		//given
		String listIdStr = UUID.randomUUID().toString();
		String expectedName = "name";
		int expectedQuantity = 10;
		String expectedState = TO_BUY.name();
		
		@SuppressWarnings("deprecation")
		ShoppingItemWriteRepresentation representationwithoutId = new ShoppingItemWriteRepresentation(null, expectedName, expectedQuantity, expectedState);
		ShoppingItemResource testedResource = getShoppingItemResource(TestHelper.generateRandomUser());
		UriInfo mockedUriInfo = TestHelper.mockUriInfo("http://test");
		when(testedResource.getUriInfo()).thenReturn(mockedUriInfo);
		
		//when
		Response response = testedResource.create(listIdStr, representationwithoutId);
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(CREATED.getStatusCode());
		ShoppingItemRepresentation itemRepresentation = (ShoppingItemRepresentation) response.getEntity();
		assertThat(itemRepresentation).isNotNull();
		assertThat(itemRepresentation.getId()).isNotEqualTo(ShoppingItem.DEFAULT_ID);
		assertThat(itemRepresentation.getName()).isEqualTo(expectedName);
		assertThat(itemRepresentation.getQuantity()).isEqualTo(expectedQuantity);
		assertThat(itemRepresentation.getState()).isEqualTo(expectedState);
	}
	
	@Test(expected = WebApiException.class)
	public void create_should_return_409_with_already_existing_item() {
		//given
		UUID listId = UUID.randomUUID();
		UUID alreadyExistingShoppingItemId = UUID.randomUUID();
		@SuppressWarnings("deprecation")
		ShoppingItemWriteRepresentation representation = new ShoppingItemWriteRepresentation(alreadyExistingShoppingItemId, "name", 10, TO_BUY.toString());
		ShoppingItemResource testedResource = getShoppingItemResource(TestHelper.generateRandomUser());
		when(mockeItemRepo.getById(listId, alreadyExistingShoppingItemId)).thenReturn(ShoppingItem.Builder.createDefault().withId(alreadyExistingShoppingItemId).build());
		String expectedMessage = ALREADY_EXISTING_ITEM.getDevReadableMessage(alreadyExistingShoppingItemId);
		
		//when
		try {
			testedResource.create(listId.toString(), representation);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, CONFLICT, ERROR, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void create_should_return_400_with_invalid_list_Id() {
		//given
		@SuppressWarnings("deprecation")
		ShoppingItemWriteRepresentation representation = new ShoppingItemWriteRepresentation(UUID.randomUUID(), "name", 10, TO_BUY.toString());
		String invalidListId = "invalid ID";
		ShoppingItemResource testedResource = getShoppingItemResource(TestHelper.generateRandomUser());
		String expectedMessage = "Invalid Param named listId : invalid ID";
		
		//when
		try {
			testedResource.create(invalidListId, representation);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void getById_should_return_400_with_invalid_item_Id() {
		//given
		String listIdStr = UUID.randomUUID().toString();
		String invalidId = "invalid ID";
		ShoppingItemResource testedResource = getShoppingItemResource(TestHelper.generateRandomUser());
		String expectedMessage = "Invalid Param named itemId : invalid ID";
		
		//when
		try {
			testedResource.getById(listIdStr, invalidId);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void getById_should_return_400_with_invalid_list_Id() {
		//given
		String itemIdStr = UUID.randomUUID().toString();
		String invalidListId = "invalid ID";
		ShoppingItemResource testedResource = getShoppingItemResource(TestHelper.generateRandomUser());
		String expectedMessage = "Invalid Param named listId : invalid ID";
		
		//when
		try {
			testedResource.getById(invalidListId, itemIdStr);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void getById_should_return_404_with_unknown_item_Id() {
		//given
		String listIdStr = UUID.randomUUID().toString();
		String unknownId = UUID.randomUUID().toString();
		ShoppingItemResource testedResource = getShoppingItemResource(TestHelper.generateRandomUser());
		ErrorMessage expectedMessage = ShoppingItemResourceErrorMessage.ITEM_NOT_FOUND;
		
		//when
		try {
			testedResource.getById(listIdStr, unknownId);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, NOT_FOUND, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test
	public void getById_should_work_with_existing_item_Id() {
		//given
		UUID listId = UUID.randomUUID();
		UUID existingId = UUID.randomUUID();
		ShoppingItemResource testedResource = getShoppingItemResource(TestHelper.generateRandomUser());
		UriInfo mockedUriInfo = TestHelper.mockUriInfo("http://test");
		when(testedResource.getUriInfo()).thenReturn(mockedUriInfo);
		ShoppingItem existingShoppingItem = ShoppingItem.Builder.createDefault().withId(existingId).build();
		when(mockeItemRepo.getById(listId, existingId)).thenReturn(existingShoppingItem);
		
		//when
		Response response = testedResource.getById(listId.toString(), existingId.toString());
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
		ShoppingItemRepresentation itemRepresentation = (ShoppingItemRepresentation) response.getEntity();
		assertThat(itemRepresentation).isNotNull();
		assertThat(itemRepresentation.getId()).isNotEqualTo(ShoppingItem.DEFAULT_ID);
		assertThat(itemRepresentation.getName()).isEqualTo(existingShoppingItem.getName());
		assertThat(itemRepresentation.getQuantity()).isEqualTo(existingShoppingItem.getQuantity());
		assertThat(itemRepresentation.getState()).isEqualTo(existingShoppingItem.getState().name());
	}
	
	@Test
	public void update_should_work_with_existing_item() {
		//given
		UUID listId = UUID.randomUUID();
		UUID expectedID = UUID.randomUUID();
		String expectedName = "name";
		int expectedQuantity = 10;
		String expectedState = TO_BUY.name();
		
		@SuppressWarnings("deprecation")
		ShoppingItemWriteRepresentation representation = new ShoppingItemWriteRepresentation(expectedID, expectedName, expectedQuantity, expectedState);
		ShoppingItemResource testedResource = getShoppingItemResource(TestHelper.generateRandomUser());
		ShoppingItem existingShoppingItem = ShoppingItem.Builder.createDefault().withId(expectedID).build();
		when(mockeItemRepo.getById(listId, expectedID)).thenReturn(existingShoppingItem);
		UriInfo mockedUriInfo = TestHelper.mockUriInfo("http://test");
		when(testedResource.getUriInfo()).thenReturn(mockedUriInfo);
		
		//when
		Response response = testedResource.update(listId.toString(), representation);
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(NO_CONTENT.getStatusCode());
	}
	
	@Test(expected = WebApiException.class)
	public void update_should_return_400_with_input_representation_without_id() {
		//given
		String listIdStr = UUID.randomUUID().toString();
		@SuppressWarnings("deprecation")
		ShoppingItemWriteRepresentation representationWithoutId = new ShoppingItemWriteRepresentation(null, "name", 10, TO_BUY.toString());
		ShoppingItemResource testedResource = getShoppingItemResource(TestHelper.generateRandomUser());
		String expectedMessage = MISSING_ITEM_ID_FOR_UPDATE.getDevReadableMessage();
		
		//when
		try {
			testedResource.update(listIdStr, representationWithoutId);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, ERROR, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void update_should_return_400_with_invalid_list_Id() {
		//given
		@SuppressWarnings("deprecation")
		ShoppingItemWriteRepresentation representation = new ShoppingItemWriteRepresentation(UUID.randomUUID(), "name", 10, TO_BUY.toString());
		String invalidListId = "invalid ID";
		ShoppingItemResource testedResource = getShoppingItemResource(TestHelper.generateRandomUser());
		String expectedMessage = "Invalid Param named listId : invalid ID";
		
		//when
		try {
			testedResource.update(invalidListId, representation);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = ApplicationException.class)
	public void update_should_return_404_with_unknown_item() {
		//given
		String listIdStr = UUID.randomUUID().toString();
		@SuppressWarnings("deprecation")
		ShoppingItemWriteRepresentation representation = new ShoppingItemWriteRepresentation(UUID.randomUUID(), "name", 10, TO_BUY.toString());
		ShoppingItemResource testedResource = getShoppingItemResource(TestHelper.generateRandomUser());
		String expectedMessage = "Item not found";
		
		//when
		try {
			testedResource.update(listIdStr, representation);
		} catch(ApplicationException ae) {
		//then
			TestHelper.assertApplicationException(ae, INFO, RepositoryErrorCode.NOT_FOUND, expectedMessage);
			throw ae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void deleteById_should_return_400_with_invalid_item_Id() {
		//given
		String listIdStr = UUID.randomUUID().toString();
		String invalidId = "invalid ID";
		ShoppingItemResource testedResource = getShoppingItemResource(TestHelper.generateRandomUser());
		String expectedMessage = "Invalid Param named itemId : invalid ID";
		
		//when
		try {
			testedResource.deleteById(listIdStr, invalidId);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void deleteById_should_return_400_with_invalid_list_Id() {
		//given
		String itemIdStr = UUID.randomUUID().toString();
		String invalidListId = "invalid ID";
		ShoppingItemResource testedResource = getShoppingItemResource(TestHelper.generateRandomUser());
		String expectedMessage = "Invalid Param named listId : invalid ID";
		
		//when
		try {
			testedResource.deleteById(invalidListId, itemIdStr);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void deleteById_should_return_404_with_unknown_item_Id() {
		//given
		String listIdStr = UUID.randomUUID().toString();
		String unknownId = UUID.randomUUID().toString();
		ShoppingItemResource testedResource = getShoppingItemResource(TestHelper.generateRandomUser());
		ErrorMessage expectedMessage = ITEM_NOT_FOUND;
		
		//when
		try {
			testedResource.deleteById(listIdStr, unknownId);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, NOT_FOUND, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test
	public void deleteById_should_work_with_existing_item_Id() {
		//given
		UUID listId = UUID.randomUUID();
		UUID existingId = UUID.randomUUID();
		ShoppingItemResource testedResource = getShoppingItemResource(TestHelper.generateRandomUser());
		ShoppingItem existingShoppingItem = ShoppingItem.Builder.createDefault().withId(existingId).build();
		when(mockeItemRepo.getById(listId, existingId)).thenReturn(existingShoppingItem);
		
		//when
		Response response = testedResource.deleteById(listId.toString(), existingId.toString());
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
	}
}