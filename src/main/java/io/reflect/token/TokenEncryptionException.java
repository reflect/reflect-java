package io.reflect.token;

/**
 * Thrown to indicate that an unexpected error occurred when encrypting token
 * data.
 */
public class TokenEncryptionException extends Exception {
    private static final long serialVersionUID = -2158785019540289829L;

    /**
     * Constructs a new exception with the given underlying cause, the reason
     * for the encryption error.
     */
    public TokenEncryptionException(Throwable cause) {
        super(cause);
    }
}
