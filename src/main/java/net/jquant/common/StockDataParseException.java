package net.jquant.common;

public class StockDataParseException extends Exception {
    public StockDataParseException() {
    }

    public StockDataParseException(String message) {
        super(message);
    }

    public StockDataParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public StockDataParseException(Throwable cause) {
        super(cause);
    }

    public StockDataParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
