package ee.ut.math.tvt.salessystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Base class for sales system exceptions
 */
public class SalesSystemException extends RuntimeException {

    private static final Logger log = LogManager.getLogger(SalesSystemException.class);

    public SalesSystemException(String message) {
        super(message);
        log.debug(message);
    }
}
