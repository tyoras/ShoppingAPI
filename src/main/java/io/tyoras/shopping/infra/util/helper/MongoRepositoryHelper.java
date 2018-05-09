package io.tyoras.shopping.infra.util.helper;

import com.mongodb.MongoException;
import io.tyoras.shopping.infra.util.error.ApplicationException;
import io.tyoras.shopping.infra.util.error.ErrorMessage;
import org.slf4j.Logger;

import static io.tyoras.shopping.infra.rest.error.Level.ERROR;
import static io.tyoras.shopping.infra.util.error.CommonErrorCode.APPLICATION_ERROR;

/**
 * utility methods for mongodb repository implementation
 *
 * @author yoan
 */
public class MongoRepositoryHelper {

    private MongoRepositoryHelper() {
    }

    /**
     * Log the mongo exception as an error with an explicit message
     *
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
