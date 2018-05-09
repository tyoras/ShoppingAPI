package io.tyoras.shopping.infra.util.helper;

import com.mongodb.MongoException;
import io.tyoras.shopping.infra.util.error.ApplicationException;
import io.tyoras.shopping.test.TestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;

import static io.tyoras.shopping.infra.rest.error.Level.ERROR;
import static io.tyoras.shopping.infra.util.error.CommonErrorCode.APPLICATION_ERROR;
import static io.tyoras.shopping.user.repository.UserRepositoryErrorMessage.PROBLEM_CREATION_USER;

@RunWith(MockitoJUnitRunner.class)
public class MongoRepositoryHelperTest {

    @Mock
    private Logger logger;

    @Test(expected = ApplicationException.class)
    public void handleMongoError_should_throw_error() {
        //given
        String expectionMessage = "message";
        MongoException exception = new MongoException(expectionMessage);
        String expectedmessage = PROBLEM_CREATION_USER.getDevReadableMessage(expectionMessage);

        //when
        try {
            MongoRepositoryHelper.handleMongoError(logger, exception, PROBLEM_CREATION_USER);
        } catch (ApplicationException ae) {
            //then
            TestHelper.assertApplicationException(ae, ERROR, APPLICATION_ERROR, expectedmessage);
            throw ae;
        }
    }
}
