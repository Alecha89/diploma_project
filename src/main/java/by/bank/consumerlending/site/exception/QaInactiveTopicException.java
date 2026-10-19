package by.bank.consumerlending.site.exception;

import lombok.Getter;

@Getter
public class QaInactiveTopicException extends RuntimeException {

    private final String code;

    private final String message;

    public QaInactiveTopicException(String message) {
        this.code = "BAD_REQUEST";
        this.message = message;
    }
}