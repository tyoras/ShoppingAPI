package yoan.shopping.infra.util.helper;

import static yoan.shopping.infra.rest.error.Level.ERROR;
import static yoan.shopping.infra.util.error.CommonErrorCode.APPLICATION_ERROR;

import org.slf4j.Logger;

import com.mongodb.MongoException;

import yoan.shopping.infra.util.error.ApplicationException;
import yoan.shopping.infra.util.error.ErrorMessage;

/**
 * utility methods for mongodb repository implementation
 * @author yoan
 */
public class MongoRepositoryHelper {
	
	private MongoRepositoryHelper() { }

	/**
	 * Log the mongo exception as an error with an explicit message
	 * @param logger
	 * @param exception
	 * @param errorMessage
	 * @throws ApplicationException
	 */
	public static void handleMongoError(Logger logger, MongoException exception, ErrorMessage errorMessage) throws ApplicationException {
		String message = errorMessage.getDevReadableMessage(exception.getMessage());
		logger.error(message, exception);
		throw new ApplicationException(ERROR, APPLICATION_ERROR, message, exception);
	}
}
