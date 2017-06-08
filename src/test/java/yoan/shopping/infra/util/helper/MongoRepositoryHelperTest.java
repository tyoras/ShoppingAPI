package yoan.shopping.infra.util.helper;

import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.util.error.CommonErrorCode.APPLICATION_ERROR;
import static yoan.shopping.user.repository.UserRepositoryErrorMessage.PROBLEM_CREATION_USER;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;

import com.mongodb.MongoException;

import yoan.shopping.infra.util.error.ApplicationException;
import yoan.shopping.test.TestHelper;

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
		} catch(ApplicationException ae) {
		//then
			TestHelper.assertApplicationException(ae, ERROR, APPLICATION_ERROR, expectedmessage);
			throw ae;
		}
	}
}
