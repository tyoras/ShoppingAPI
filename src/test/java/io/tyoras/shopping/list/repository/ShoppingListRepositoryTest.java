package io.tyoras.shopping.list.repository;

import com.google.common.collect.ImmutableList;
import io.tyoras.shopping.infra.util.error.ApplicationException;
import io.tyoras.shopping.infra.util.error.CommonErrorMessage;
import io.tyoras.shopping.list.ShoppingList;
import io.tyoras.shopping.test.TestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static io.tyoras.shopping.infra.rest.error.Level.INFO;
import static io.tyoras.shopping.infra.util.error.RepositoryErrorCode.NOT_FOUND;
import static io.tyoras.shopping.test.TestHelper.assertApplicationException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Answers.CALLS_REAL_METHODS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ShoppingListRepositoryTest {

    @Mock(answer = CALLS_REAL_METHODS)
    ShoppingListRepository testedRepo;

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