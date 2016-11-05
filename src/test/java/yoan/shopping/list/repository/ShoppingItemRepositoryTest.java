package yoan.shopping.list.repository;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Answers.CALLS_REAL_METHODS;
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
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import yoan.shopping.infra.util.error.ApplicationException;
import yoan.shopping.infra.util.error.CommonErrorMessage;
import yoan.shopping.list.ShoppingItem;
import yoan.shopping.test.TestHelper;

@RunWith(MockitoJUnitRunner.class)
public class ShoppingItemRepositoryTest {
	
	@Mock(answer= CALLS_REAL_METHODS)
	ShoppingItemRepository testedRepo;
	
	@Test
	public void create_should_do_nothing_with_null_item() {
		//given
		UUID listId = UUID.randomUUID();
		ShoppingItem nullShoppingItem = null;

		//when
		testedRepo.create(listId, nullShoppingItem);
		
		//then
		verify(testedRepo, never()).processCreate(any(), any());
	}
	
	@Test
	public void create_should_do_nothing_with_null_listId() {
		//given
		UUID nullListId = null;
		ShoppingItem shoppingItem = TestHelper.generateRandomShoppingItem();

		//when
		testedRepo.create(nullListId, shoppingItem);
		
		//then
		verify(testedRepo, never()).processCreate(any(), any());
	}
	
	@Test
	public void getById_should_return_null_with_null_Id() {
		//given
		UUID listId = UUID.randomUUID();
		UUID nullId = null;
		
		//when
		ShoppingItem result = testedRepo.getById(listId, nullId);
		
		//then
		assertThat(result).isNull();
		verify(testedRepo, never()).processGetById(any(), any());
	}
	
	@Test
	public void getById_should_return_null_with_null_listId() {
		//given
		UUID nullListId = null;
		UUID itemId = UUID.randomUUID();
		
		//when
		ShoppingItem result = testedRepo.getById(nullListId, itemId);
		
		//then
		assertThat(result).isNull();
		verify(testedRepo, never()).processGetById(any(), any());
	}
	
	@Test
	public void update_should_do_nothing_with_null_item() {
		//given
		UUID listId = UUID.randomUUID();
		ShoppingItem nullShoppingItem = null;

		//when
		testedRepo.update(listId, nullShoppingItem);
		
		//then
		verify(testedRepo, never()).processUpdate(any(), any());
	}
	
	@Test
	public void update_should_do_nothing_with_null_listId() {
		//given
		UUID nullListId = null;
		ShoppingItem shoppingItem = TestHelper.generateRandomShoppingItem();

		//when
		testedRepo.update(nullListId, shoppingItem);
		
		//then
		verify(testedRepo, never()).processUpdate(any(), any());
	}
	
	@Test(expected = ApplicationException.class)
	public void update_should_fail_with_not_existing_item() {
		//given
		UUID listId = UUID.randomUUID();
		ShoppingItem notExistingShoppingItem = TestHelper.generateRandomShoppingItem();
		String expectedErrorMessage = CommonErrorMessage.NOT_FOUND.getDevReadableMessage("Item");

		//when
		try {
			testedRepo.update(listId, notExistingShoppingItem);
		} catch (ApplicationException ae) {
		//then
			assertApplicationException(ae, INFO, NOT_FOUND, expectedErrorMessage);
			throw ae;
		} finally {
			verify(testedRepo, never()).processUpdate(any(), any());
		}
	}
	
	@Test(expected = ApplicationException.class)
	public void update_should_fail_with_not_existing_list() {
		//given
		UUID notExistingListId = UUID.randomUUID();
		ShoppingItem shoppingItem = TestHelper.generateRandomShoppingItem();
		String expectedErrorMessage = CommonErrorMessage.NOT_FOUND.getDevReadableMessage("Item");

		//when
		try {
			testedRepo.update(notExistingListId, shoppingItem);
		} catch (ApplicationException ae) {
		//then
			assertApplicationException(ae, INFO, NOT_FOUND, expectedErrorMessage);
			throw ae;
		} finally {
			verify(testedRepo, never()).processUpdate(any(), any());
		}
	}
	
	@Test
	public void deleteById_should_do_nothing_with_null_Id() {
		//given
		UUID listId = UUID.randomUUID();
		UUID nullId = null;

		//when
		testedRepo.deleteById(listId, nullId);
		
		//then
		verify(testedRepo, never()).processDeleteById(any(), any());
	}
	
	@Test
	public void deleteById_should_do_nothing_with_null_listId() {
		//given
		UUID nullListId = null;
		UUID itemId = UUID.randomUUID();
		//when
		testedRepo.deleteById(nullListId, itemId);
		
		//then
		verify(testedRepo, never()).processDeleteById(any(), any());
	}
	
	@Test(expected = ApplicationException.class)
	public void findItem_should_fail_with_not_existing_list() {
		//given
		UUID listId = UUID.randomUUID();
		UUID notExistingShoppingItemId = UUID.randomUUID();
		String expectedErrorMessage = CommonErrorMessage.NOT_FOUND.getDevReadableMessage("Item");

		//when
		try {
			testedRepo.findItem(listId, notExistingShoppingItemId);
		} catch (ApplicationException ae) {
		//then
			assertApplicationException(ae, INFO, NOT_FOUND, expectedErrorMessage);
			throw ae;
		}
	}
	
	@Test
	public void findList_should_work_with_existing_list() {
		//given
		UUID listId = UUID.randomUUID();
		ShoppingItem existingItem = TestHelper.generateRandomShoppingItem();
		doReturn(existingItem).when(testedRepo).getById(listId, existingItem.getId());

		//when
		ShoppingItem result = testedRepo.findItem(listId, existingItem.getId());
		
		//then
		assertThat(result).isEqualTo(existingItem);
	}
}
