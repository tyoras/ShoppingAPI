package io.tyoras.shopping.list.resource;

import static io.tyoras.shopping.infra.rest.error.Level.INFO;
import static io.tyoras.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static io.tyoras.shopping.list.ItemState.TO_BUY;
import static io.tyoras.shopping.list.resource.ShoppingItemResourceErrorMessage.ITEM_NOT_FOUND;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import io.tyoras.shopping.infra.rest.Link;
import io.tyoras.shopping.infra.rest.RestRepresentation;
import io.tyoras.shopping.infra.rest.error.WebApiException;
import io.tyoras.shopping.infra.util.error.ApplicationException;
import io.tyoras.shopping.infra.util.error.ErrorMessage;
import io.tyoras.shopping.infra.util.error.RepositoryErrorCode;
import io.tyoras.shopping.list.ShoppingItem;
import io.tyoras.shopping.list.repository.ShoppingItemRepository;
import io.tyoras.shopping.list.representation.ShoppingItemRepresentation;
import io.tyoras.shopping.list.representation.ShoppingItemWriteRepresentation;
import io.tyoras.shopping.list.resource.ShoppingItemResource;
import io.tyoras.shopping.list.resource.ShoppingItemResourceErrorMessage;
import io.tyoras.shopping.test.TestHelper;
import io.tyoras.shopping.user.User;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ShoppingItemResourceTest {

	@Mock
	ShoppingItemRepository mockeItemRepo;
	
	@Spy
	@InjectMocks
	private ShoppingItemResource testedResource;
	
	@Test
	public void getRootLinks_should_contains_self_link() {
		//given
		String expectedURL = "http://test";
		UriInfo mockedUriInfo = TestHelper.mockUriInfo(expectedURL);
		doReturn(mockedUriInfo).when(testedResource).getUriInfo();
		
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
		doReturn(mockedUriInfo).when(testedResource).getUriInfo();
		
		//when
		Response response = testedResource.root(connectedUser);
		
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
		UriInfo mockedUriInfo = TestHelper.mockUriInfo("http://test");
		doReturn(mockedUriInfo).when(testedResource).getUriInfo();
		
		//when
		Response response = testedResource.create(TestHelper.generateRandomUser(), listIdStr, representation);
		
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
	
	@Test
	public void create_should_work_with_input_representation_without_id() {
		//given
		String listIdStr = UUID.randomUUID().toString();
		String expectedName = "name";
		int expectedQuantity = 10;
		String expectedState = TO_BUY.name();
		
		@SuppressWarnings("deprecation")
		ShoppingItemWriteRepresentation representationwithoutId = new ShoppingItemWriteRepresentation(null, expectedName, expectedQuantity, expectedState);
		UriInfo mockedUriInfo = TestHelper.mockUriInfo("http://test");
		doReturn(mockedUriInfo).when(testedResource).getUriInfo();
		
		//when
		Response response = testedResource.create(TestHelper.generateRandomUser(), listIdStr, representationwithoutId);
		
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
	public void create_should_return_400_with_invalid_list_Id() {
		//given
		@SuppressWarnings("deprecation")
		ShoppingItemWriteRepresentation representation = new ShoppingItemWriteRepresentation(UUID.randomUUID(), "name", 10, TO_BUY.toString());
		String invalidListId = "invalid ID";
		String expectedMessage = "Invalid Param named listId : invalid ID";
		
		//when
		try {
			testedResource.create(TestHelper.generateRandomUser(), invalidListId, representation);
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
		String expectedMessage = "Invalid Param named itemId : invalid ID";
		
		//when
		try {
			testedResource.getById(TestHelper.generateRandomUser(), listIdStr, invalidId);
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
		String expectedMessage = "Invalid Param named listId : invalid ID";
		
		//when
		try {
			testedResource.getById(TestHelper.generateRandomUser(), invalidListId, itemIdStr);
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
		ErrorMessage expectedMessage = ShoppingItemResourceErrorMessage.ITEM_NOT_FOUND;
		
		//when
		try {
			testedResource.getById(TestHelper.generateRandomUser(), listIdStr, unknownId);
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
		UriInfo mockedUriInfo = TestHelper.mockUriInfo("http://test");
		doReturn(mockedUriInfo).when(testedResource).getUriInfo();
		ShoppingItem existingShoppingItem = ShoppingItem.Builder.createDefault().withId(existingId).build();
		when(mockeItemRepo.getById(listId, existingId)).thenReturn(existingShoppingItem);
		
		//when
		Response response = testedResource.getById(TestHelper.generateRandomUser(), listId.toString(), existingId.toString());
		
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
		ShoppingItem existingShoppingItem = ShoppingItem.Builder.createDefault().withId(expectedID).build();
		when(mockeItemRepo.getById(listId, expectedID)).thenReturn(existingShoppingItem);
		UriInfo mockedUriInfo = TestHelper.mockUriInfo("http://test");
		doReturn(mockedUriInfo).when(testedResource).getUriInfo();
		
		//when
		Response response = testedResource.update(TestHelper.generateRandomUser(), listId.toString(), expectedID.toString(), representation);
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(NO_CONTENT.getStatusCode());
	}
	
	@Test(expected = WebApiException.class)
	public void update_should_return_400_with_input_representation_with_invalid_id() {
		//given
		String listIdStr = UUID.randomUUID().toString();
		String invalidItemId = "invalid";
		@SuppressWarnings("deprecation")
		ShoppingItemWriteRepresentation representationWithoutId = new ShoppingItemWriteRepresentation(null, "name", 10, TO_BUY.toString());
		String expectedMessage = "Invalid Param named itemId : invalid";
		
		//when
		try {
			testedResource.update(TestHelper.generateRandomUser(), listIdStr, invalidItemId, representationWithoutId);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void update_should_return_400_with_invalid_list_Id() {
		//given
		@SuppressWarnings("deprecation")
		ShoppingItemWriteRepresentation representation = new ShoppingItemWriteRepresentation(UUID.randomUUID(), "name", 10, TO_BUY.toString());
		String invalidListId = "invalid ID";
		String expectedMessage = "Invalid Param named listId : invalid ID";
		
		//when
		try {
			testedResource.update(TestHelper.generateRandomUser(), invalidListId, representation.getId().toString(), representation);
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
		String expectedMessage = "Item not found";
		
		//when
		try {
			testedResource.update(TestHelper.generateRandomUser(), listIdStr, representation.getId().toString(), representation);
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
		String expectedMessage = "Invalid Param named itemId : invalid ID";
		
		//when
		try {
			testedResource.deleteById(TestHelper.generateRandomUser(), listIdStr, invalidId);
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
		String expectedMessage = "Invalid Param named listId : invalid ID";
		
		//when
		try {
			testedResource.deleteById(TestHelper.generateRandomUser(), invalidListId, itemIdStr);
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
		ErrorMessage expectedMessage = ITEM_NOT_FOUND;
		
		//when
		try {
			testedResource.deleteById(TestHelper.generateRandomUser(), listIdStr, unknownId);
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
		ShoppingItem existingShoppingItem = ShoppingItem.Builder.createDefault().withId(existingId).build();
		when(mockeItemRepo.getById(listId, existingId)).thenReturn(existingShoppingItem);
		
		//when
		Response response = testedResource.deleteById(TestHelper.generateRandomUser(), listId.toString(), existingId.toString());
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(NO_CONTENT.getStatusCode());
	}
}