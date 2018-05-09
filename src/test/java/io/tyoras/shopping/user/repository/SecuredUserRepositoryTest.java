package io.tyoras.shopping.user.repository;

import io.tyoras.shopping.infra.util.error.ApplicationException;
import io.tyoras.shopping.user.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static io.tyoras.shopping.infra.rest.error.Level.ERROR;
import static io.tyoras.shopping.infra.rest.error.Level.INFO;
import static io.tyoras.shopping.infra.util.error.RepositoryErrorCode.NOT_FOUND;
import static io.tyoras.shopping.test.TestHelper.assertApplicationException;
import static io.tyoras.shopping.user.repository.UserRepositoryErrorCode.UNSECURE_PASSWORD;
import static io.tyoras.shopping.user.repository.UserRepositoryErrorMessage.PROBLEM_PASSWORD_VALIDITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Answers.CALLS_REAL_METHODS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SecuredUserRepositoryTest {

    @Mock(answer = CALLS_REAL_METHODS)
    SecuredUserRepository testedRepo;

    @Test
    public void create_should_do_nothing_with_null_user() {
        //given
        User nullUser = null;

        //when
        testedRepo.create(nullUser, "password");

        //then
        verify(testedRepo, never()).processCreate(any());
    }

    @Test(expected = ApplicationException.class)
    public void create_should_fail_with_null_password() {
        //given
        User user = User.Builder.createDefault().withRandomId().build();
        String nullPassword = null;

        //when
        try {
            testedRepo.create(user, nullPassword);
        } catch (ApplicationException ae) {
            //then
            verify(testedRepo, never()).processCreate(any());
            assertApplicationException(ae, ERROR, UNSECURE_PASSWORD, PROBLEM_PASSWORD_VALIDITY);
            throw ae;
        }
    }

    @Test(expected = ApplicationException.class)
    public void create_should_fail_with_blank_password() {
        //given
        User user = User.Builder.createDefault().withRandomId().build();
        String blankPassword = "  ";

        //when
        try {
            testedRepo.create(user, blankPassword);
        } catch (ApplicationException ae) {
            //then
            verify(testedRepo, never()).processCreate(any());
            assertApplicationException(ae, ERROR, UNSECURE_PASSWORD, PROBLEM_PASSWORD_VALIDITY);
            throw ae;
        }
    }

    @Test
    public void generateSalt_should_never_return_null() {
        //when
        Object salt = testedRepo.generateSalt();

        //then
        assertThat(salt).isNotNull();
    }

    @Test
    public void generateSalt_should_never_return_same_value() {
        //when
        Object salt1 = testedRepo.generateSalt();
        Object salt2 = testedRepo.generateSalt();

        //then
        assertThat(salt1).isNotEqualTo(salt2);
    }

    @Test
    public void hashPassword_should_always_return_the_same_hash_if_using_the_same_salt() {
        //given
        Object salt = "salt";
        String password = "password";

        //when
        String hash1 = testedRepo.hashPassword(password, salt);
        String hash2 = testedRepo.hashPassword(password, salt);
        String hash3 = testedRepo.hashPassword(password, salt);

        assertThat(hash1).isNotNull();
        assertThat(hash1).isEqualTo(hash2);
        assertThat(hash1).isEqualTo(hash3);
    }

    @Test
    public void hashPassword_should_not_return_the_same_hash_if_using_different_salt() {
        //given
        String password = "password";

        //when
        String hash1 = testedRepo.hashPassword(password, UUID.randomUUID().toString());
        String hash2 = testedRepo.hashPassword(password, UUID.randomUUID().toString());
        String hash3 = testedRepo.hashPassword(password, UUID.randomUUID().toString());

        assertThat(hash1).isNotEqualTo(hash2);
        assertThat(hash1).isNotEqualTo(hash3);
        assertThat(hash2).isNotEqualTo(hash3);
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
        String blankEmail = null;

        //when
        User result = testedRepo.getByEmail(blankEmail);

        //then
        assertThat(result).isNull();
        verify(testedRepo, never()).processGetByEmail(any());
    }

    @Test
    public void changePassword_should_do_nothing_with_null_user_Id() {
        //given
        UUID nullUserId = null;

        //when
        testedRepo.changePassword(nullUserId, "password");

        //then
        verify(testedRepo, never()).processChangePassword(any());
    }

    @Test(expected = ApplicationException.class)
    public void changePassword_should_fail_with_not_existing_user() {
        //given
        UUID notExistingUserId = UUID.randomUUID();

        //when
        try {
            testedRepo.changePassword(notExistingUserId, "password");
        } catch (ApplicationException ae) {
            //then
            assertApplicationException(ae, INFO, NOT_FOUND, "User not found");
            throw ae;
        } finally {
            verify(testedRepo, never()).processChangePassword(any());
        }
    }

    @Test(expected = ApplicationException.class)
    public void changePassword_should_fail_with_blank_password() {
        //given
        UUID userId = UUID.randomUUID();
        String blankPassword = "  ";

        //when
        try {
            testedRepo.changePassword(userId, blankPassword);
        } catch (ApplicationException ae) {
            //then
            verify(testedRepo, never()).processChangePassword(any());
            assertApplicationException(ae, ERROR, UNSECURE_PASSWORD, PROBLEM_PASSWORD_VALIDITY);
            throw ae;
        }
    }
}
