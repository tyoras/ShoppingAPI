package yoan.shopping.list.resource;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static yoan.shopping.infra.rest.error.Level.INFO;
import static yoan.shopping.infra.util.error.CommonErrorCode.API_RESPONSE;
import static yoan.shopping.list.resource.ShoppingListResourceErrorMessage.LISTS_NOT_FOUND;
import static yoan.shopping.list.resource.ShoppingListResourceErrorMessage.LIST_NOT_FOUND;

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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import yoan.shopping.infra.rest.Link;
import yoan.shopping.infra.rest.RestRepresentation;
import yoan.shopping.infra.rest.error.WebApiException;
import yoan.shopping.infra.util.error.ApplicationException;
import yoan.shopping.infra.util.error.ErrorMessage;
import yoan.shopping.infra.util.error.RepositoryErrorCode;
import yoan.shopping.list.ShoppingList;
import yoan.shopping.list.repository.ShoppingListRepository;
import yoan.shopping.list.representation.ShoppingItemRepresentation;
import yoan.shopping.list.representation.ShoppingListRepresentation;
import yoan.shopping.list.representation.ShoppingListWriteRepresentation;
import yoan.shopping.test.TestHelper;
import yoan.shopping.user.User;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ShoppingListResourceTest {

	@Mock
	ShoppingListRepository mockeListRepo;
	
	@Spy
	@InjectMocks
	private ShoppingListResource testedResource;
	
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
		String expectedName = "name";
		UUID expectedOwnerId = UUID.randomUUID();
		@SuppressWarnings("deprecation")
		ShoppingListWriteRepresentation representation = new ShoppingListWriteRepresentation(expectedName, expectedOwnerId, Lists.newArrayList());
		UriInfo mockedUriInfo = TestHelper.mockUriInfo("http://test");
		doReturn(mockedUriInfo).when(testedResource).getUriInfo();
		
		//when
		Response response = testedResource.create(TestHelper.generateRandomUser(), representation);
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(CREATED.getStatusCode());
		ShoppingListRepresentation listRepresentation = (ShoppingListRepresentation) response.getEntity();
		assertThat(listRepresentation).isNotNull();
		assertThat(listRepresentation.getId()).isNotEqualTo(ShoppingList.DEFAULT_ID);
		assertThat(listRepresentation.getName()).isEqualTo(expectedName);
		assertThat(listRepresentation.getOwnerId()).isEqualTo(expectedOwnerId);
	}
	
	@Test(expected = WebApiException.class)
	public void getById_should_return_400_with_invalid_Id() {
		//given
		String invalidId = "invalid ID";
		String expectedMessage = "Invalid Param named listId : invalid ID";
		
		//when
		try {
			testedResource.getById(TestHelper.generateRandomUser(), invalidId);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void getById_should_return_404_with_unknown_list_Id() {
		//given
		String unknownId = UUID.randomUUID().toString();
		ErrorMessage expectedMessage = ShoppingListResourceErrorMessage.LIST_NOT_FOUND;
		
		//when
		try {
			testedResource.getById(TestHelper.generateRandomUser(), unknownId);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, NOT_FOUND, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test
	public void getById_should_work_with_existing_list_Id() {
		//given
		UUID existingId = UUID.randomUUID();
		UriInfo mockedUriInfo = TestHelper.mockUriInfo("http://test");
		doReturn(mockedUriInfo).when(testedResource).getUriInfo();
		ShoppingList existingShoppingList = ShoppingList.Builder.createDefault().withId(existingId).build();
		when(mockeListRepo.getById(existingId)).thenReturn(existingShoppingList);
		
		//when
		Response response = testedResource.getById(TestHelper.generateRandomUser(), existingId.toString());
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
		ShoppingListRepresentation listRepresentation = (ShoppingListRepresentation) response.getEntity();
		assertThat(listRepresentation).isNotNull();
		assertThat(listRepresentation.getId()).isNotEqualTo(ShoppingList.DEFAULT_ID);
		assertThat(listRepresentation.getName()).isEqualTo(existingShoppingList.getName());
		assertThat(listRepresentation.getOwnerId()).isEqualTo(existingShoppingList.getOwnerId());
		assertThat(ShoppingItemRepresentation.toShoppingItemList(listRepresentation.getItemList())).isEqualTo(existingShoppingList.getItemList());
	}
	
	@Test
	public void update_should_work_with_existing_list() {
		//given
		UUID expectedID = UUID.randomUUID();
		String expectedName = "name";
		UUID expectedOwnerId = UUID.randomUUID();
		@SuppressWarnings("deprecation")
		ShoppingListWriteRepresentation representation = new ShoppingListWriteRepresentation(expectedName, expectedOwnerId, Lists.newArrayList());
		ShoppingList existingShoppingList = ShoppingList.Builder.createDefault().withId(expectedID).build();
		when(mockeListRepo.getById(expectedID)).thenReturn(existingShoppingList);
		UriInfo mockedUriInfo = TestHelper.mockUriInfo("http://test");
		doReturn(mockedUriInfo).when(testedResource).getUriInfo();
		
		//when
		Response response = testedResource.update(TestHelper.generateRandomUser(), expectedID.toString(), representation);
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(NO_CONTENT.getStatusCode());
	}
	
	@Test(expected = WebApiException.class)
	public void update_should_return_400_with_input_representation_with_invalid_id() {
		//given
		String invalidListId = "invalid";
		@SuppressWarnings("deprecation")
		ShoppingListWriteRepresentation representation = new ShoppingListWriteRepresentation("name", UUID.randomUUID(), Lists.newArrayList());
		String expectedMessage = "Invalid Param named listId : invalid";
		
		//when
		try {
			testedResource.update(TestHelper.generateRandomUser(), invalidListId, representation);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = ApplicationException.class)
	public void update_should_return_404_with_unknown_list() {
		//given
		String unknownListId = UUID.randomUUID().toString();
		@SuppressWarnings("deprecation")
		ShoppingListWriteRepresentation representation = new ShoppingListWriteRepresentation("name", UUID.randomUUID(), Lists.newArrayList());
		String expectedMessage = "List not found";
		
		//when
		try {
			testedResource.update(TestHelper.generateRandomUser(), unknownListId, representation);
		} catch(ApplicationException ae) {
		//then
			TestHelper.assertApplicationException(ae, INFO, RepositoryErrorCode.NOT_FOUND, expectedMessage);
			throw ae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void deleteById_should_return_400_with_invalid_Id() {
		//given
		String invalidId = "invalid ID";
		String expectedMessage = "Invalid Param named listId : invalid ID";
		
		//when
		try {
			testedResource.deleteById(TestHelper.generateRandomUser(), invalidId);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void deleteById_should_return_404_with_unknown_list_Id() {
		//given
		String unknownId = UUID.randomUUID().toString();
		ErrorMessage expectedMessage = LIST_NOT_FOUND;
		
		//when
		try {
			testedResource.deleteById(TestHelper.generateRandomUser(), unknownId);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, NOT_FOUND, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test
	public void deleteById_should_work_with_existing_list_Id() {
		//given
		UUID existingId = UUID.randomUUID();
		ShoppingList existingShoppingList = ShoppingList.Builder.createDefault().withId(existingId).build();
		when(mockeListRepo.getById(existingId)).thenReturn(existingShoppingList);
		
		//when
		Response response = testedResource.deleteById(TestHelper.generateRandomUser(), existingId.toString());
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
	}
	
	@Test(expected = WebApiException.class)
	public void getByOwnerId_should_return_400_with_invalid_Id() {
		//given
		String invalidId = "invalid ID";
		String expectedMessage = "Invalid Param named ownerId : invalid ID";
		
		//when
		try {
			testedResource.getByOwnerId(TestHelper.generateRandomUser(), invalidId);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void getByOwnerId_should_return_404_with_unknown_owner_Id() {
		//given
		UUID unknownId = UUID.randomUUID();
		when(mockeListRepo.getByOwner(unknownId)).thenReturn(ImmutableList.of());
		String expectedMessage = LISTS_NOT_FOUND.getDevReadableMessage(unknownId.toString());
		
		//when
		try {
			testedResource.getByOwnerId(TestHelper.generateRandomUser(), unknownId.toString());
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, NOT_FOUND, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test
	public void getByOwnerId_should_work_with_existing_list_Id() {
		//given
		UUID existingOwnerId = UUID.randomUUID();
		UriInfo mockedUriInfo = TestHelper.mockUriInfo("http://test");
		doReturn(mockedUriInfo).when(testedResource).getUriInfo();
		ShoppingList existingShoppingList = ShoppingList.Builder.createDefault().withRandomId().withOwnerId(existingOwnerId).build();
		ShoppingList existingShoppingList2 = ShoppingList.Builder.createDefault().withRandomId().withOwnerId(existingOwnerId).build();
		when(mockeListRepo.getByOwner(existingOwnerId)).thenReturn(ImmutableList.of(existingShoppingList, existingShoppingList2));
		
		//when
		Response response = testedResource.getByOwnerId(TestHelper.generateRandomUser(), existingOwnerId.toString());
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
		List<?> listsRepresentation = (List<?>) response.getEntity();
		assertThat(listsRepresentation).isNotNull();
		assertThat(listsRepresentation).hasSize(2);
		
		ShoppingListRepresentation representation = (ShoppingListRepresentation) listsRepresentation.get(0);
		assertThat(representation.getId()).isNotEqualTo(ShoppingList.DEFAULT_ID);
		assertThat(representation.getName()).isEqualTo(existingShoppingList.getName());
		assertThat(representation.getOwnerId()).isEqualTo(existingShoppingList.getOwnerId());
		assertThat(ShoppingItemRepresentation.toShoppingItemList(representation.getItemList())).isEqualTo(existingShoppingList.getItemList());
		
		ShoppingListRepresentation representation2 = (ShoppingListRepresentation) listsRepresentation.get(1);
		assertThat(representation2.getId()).isNotEqualTo(ShoppingList.DEFAULT_ID);
		assertThat(representation2.getName()).isEqualTo(existingShoppingList2.getName());
		assertThat(representation2.getOwnerId()).isEqualTo(existingShoppingList2.getOwnerId());
		assertThat(ShoppingItemRepresentation.toShoppingItemList(representation2.getItemList())).isEqualTo(existingShoppingList2.getItemList());
	}
}