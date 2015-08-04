package yoan.shopping.list.repository;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static yoan.shopping.infra.rest.error.Level.INFO;
import static yoan.shopping.infra.util.error.RepositoryErrorCode.NOT_FOUND;
import static yoan.shopping.test.TestHelper.assertApplicationException;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import yoan.shopping.infra.util.error.ApplicationException;
import yoan.shopping.infra.util.error.CommonErrorMessage;
import yoan.shopping.list.ShoppingList;
import yoan.shopping.list.repository.fake.ShoppingListFakeRepository;
import yoan.shopping.test.TestHelper;

import com.google.common.collect.ImmutableList;

@RunWith(MockitoJUnitRunner.class)
public class ShoppingListRepositoryTest {

	@Spy
	ShoppingListRepository testedRepo = new ShoppingListFakeRepository();
	
	@Test
	public void create_should_do_nothing_with_null_list() {
		//given
		ShoppingList nullShoppingList = null;

		//when
		testedRepo.create(nullShoppingList);
		
		//then
		verify(testedRepo, never()).processCreate(any());
	}
	
	@Test
	public void getById_should_return_null_with_null_Id() {
		//given
		UUID nullId = null;

		//when
		ShoppingList result = testedRepo.getById(nullId);
		
		//then
		assertThat(result).isNull();
		verify(testedRepo, never()).processGetById(any());
	}
	
	@Test
	public void getByOwner_should_return_empty_list_with_null_Id() {
		//given
		UUID nullId = null;

		//when
		ImmutableList<ShoppingList> result = testedRepo.getByOwner(nullId);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEmpty();
		verify(testedRepo, never()).processGetById(any());
	}
	
	@Test
	public void update_should_do_nothing_with_null_list() {
		//given
		ShoppingList nullShoppingList = null;

		//when
		testedRepo.update(nullShoppingList);
		
		//then
		verify(testedRepo, never()).processUpdate(any());
	}
	
	@Test(expected = ApplicationException.class)
	public void update_should_fail_with_not_existing_list() {
		//given
		ShoppingList notExistingShoppingList = ShoppingList.Builder.createDefault().withRandomId().build();
		String expectedErrorMessage = CommonErrorMessage.NOT_FOUND.getDevReadableMessage("List");

		//when
		try {
			testedRepo.update(notExistingShoppingList);
		} catch (ApplicationException ae) {
		//then
			assertApplicationException(ae, INFO, NOT_FOUND, expectedErrorMessage);
			throw ae;
		} finally {
			verify(testedRepo, never()).processUpdate(any());
		}
	}
	
	@Test
	public void deleteById_should_do_nothing_with_null_Id() {
		//given
		UUID nullId = null;

		//when
		testedRepo.deleteById(nullId);
		
		//then
		verify(testedRepo, never()).processDeleteById(any());
	}
	
	@Test(expected = ApplicationException.class)
	public void findList_should_fail_with_not_existing_list() {
		//given
		UUID notExistingShoppingListId = UUID.randomUUID();
		String expectedErrorMessage = CommonErrorMessage.NOT_FOUND.getDevReadableMessage("List");

		//when
		try {
			testedRepo.findList(notExistingShoppingListId);
		} catch (ApplicationException ae) {
		//then
			assertApplicationException(ae, INFO, NOT_FOUND, expectedErrorMessage);
			throw ae;
		}
	}
	
	@Test
	public void findList_should_work_with_existing_list() {
		//given
		ShoppingList existingList = TestHelper.generateRandomShoppingList();
		doReturn(existingList).when(testedRepo).getById(existingList.getId());

		//when
		ShoppingList result = testedRepo.findList(existingList.getId());
		
		//then
		assertThat(result).isEqualTo(existingList);
	}
}