package ma.stagefinder.exceptions;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String message;
    private LocalDateTime timestamp;
    private int status;

    public ErrorResponse(String message, int status) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.status = status;
    }

    // getters and setters
}
