package io.tyoras.shopping.user.repository;

import com.google.common.collect.ImmutableList;
import io.tyoras.shopping.infra.util.error.ApplicationException;
import io.tyoras.shopping.test.TestHelper;
import io.tyoras.shopping.user.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static io.tyoras.shopping.infra.rest.error.Level.INFO;
import static io.tyoras.shopping.infra.util.error.RepositoryErrorCode.NOT_FOUND;
import static io.tyoras.shopping.test.TestHelper.assertApplicationException;
import static io.tyoras.shopping.user.ProfileVisibility.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Answers.CALLS_REAL_METHODS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryTest {

    @Mock(answer = CALLS_REAL_METHODS)
    UserRepository testedRepo;


    @Test
    public void create_should_do_nothing_with_null_user() {
        //given
        User nullUser = null;

        //when
        testedRepo.create(nullUser);

        //then
        verify(testedRepo, never()).processCreate(any());
    }

    @Test
    public void getById_should_return_null_with_null_Id() {
        //given
        UUID nullId = null;

        //when
        User result = testedRepo.getById(nullId);

        //then
        assertThat(result).isNull();
        verify(testedRepo, never()).processGetById(any());
    }

    @Test
    public void update_should_do_nothing_with_null_user() {
        //given
        User nullUser = null;

        //when
        testedRepo.update(nullUser);

        //then
        verify(testedRepo, never()).processUpdate(any());
    }

    @Test(expected = ApplicationException.class)
    public void update_should_fail_with_not_existing_user() {
        //given
        User notExistingUser = TestHelper.generateRandomUser();

        //when
        try {
            testedRepo.update(notExistingUser);
        } catch (ApplicationException ae) {
            //then
            assertApplicationException(ae, INFO, NOT_FOUND, "User not found");
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

    @Test
    public void getByEmail_should_return_null_with_null_email() {
        //given
        String nullEmail = null;

        //when
        User result = testedRepo.getByEmail(nullEmail);

        //then
        assertThat(result).isNull();
        verify(testedRepo, never()).processGetByEmail(any());
    }

    @Test
    public void getByEmail_should_return_null_with_blank_email() {
        //given
        String blankEmail = "  ";

        //when
        User result = testedRepo.getByEmail(blankEmail);

        //then
        assertThat(result).isNull();
        verify(testedRepo, never()).processGetByEmail(any());
    }

    @Test
    public void checkUserExistsByIdOrEmail_should_return_true_with_null_Id_and_existing_email() {
        //given
        UUID nullId = null;
        String existingEmail = "mail@mail.com";
        when(testedRepo.countByIdOrEmail(any(), eq(existingEmail))).thenReturn(Long.valueOf(1));

        //when
        boolean result = testedRepo.checkUserExistsByIdOrEmail(nullId, existingEmail);

        //then
        assertThat(result).isTrue();
        verify(testedRepo).countByIdOrEmail(any(), eq(existingEmail));
    }

    @Test
    public void checkUserExistsByIdOrEmail_should_return_true_with_null_email_and_existing_id() {
        //given
        UUID existingId = UUID.randomUUID();
        String nullEmail = null;
        when(testedRepo.countByIdOrEmail(eq(existingId), any())).thenReturn(Long.valueOf(1));

        //when
        boolean result = testedRepo.checkUserExistsByIdOrEmail(existingId, nullEmail);

        //then
        assertThat(result).isTrue();
        verify(testedRepo).countByIdOrEmail(eq(existingId), any());
    }

    @Test
    public void checkUserExistsByIdOrEmail_should_return_false_with_null_id_and_blank_email() {
        //given
        UUID nullId = null;
        String blankEmail = "  ";

        //when
        boolean result = testedRepo.checkUserExistsByIdOrEmail(nullId, blankEmail);

        //then
        assertThat(result).isFalse();
        verify(testedRepo, never()).countByIdOrEmail(any(), any());
    }

    @Test
    public void searchByName_should_return_empty_list_with_null_search() {
        //given
        String nullSearch = null;

        //when
        ImmutableList<User> result = testedRepo.searchByName(nullSearch);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(testedRepo, never()).processSearchByName(any(), anyInt(), any());
    }

    @Test
    public void searchByName_should_return_empty_list_with_empty_search() {
        //given
        String emptySearch = "     ";

        //when
        ImmutableList<User> result = testedRepo.searchByName(emptySearch);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(testedRepo, never()).processSearchByName(any(), anyInt(), any());
    }

    @Test
    public void searchByName_should_return_empty_list_with_less_than_3_chars_search() {
        //given
        String search = "ab";

        //when
        ImmutableList<User> result = testedRepo.searchByName(search);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(testedRepo, never()).processSearchByName(any(), anyInt(), any());
    }

    @Test
    public void searchByName_should_process_with_valid_search() {
        //given
        String validSearch = "abc";

        //when
        testedRepo.searchByName(validSearch);

        //then
        verify(testedRepo).processSearchByName(eq(PUBLIC), anyInt(), any());
    }
}
