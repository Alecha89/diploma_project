package by.bank.consumerlending.site.exception;

import lombok.Getter;

@Getter
public class QaTooManyQuestionsException extends RuntimeException {

    private final String code;

    private final String message;

    public QaTooManyQuestionsException(String message) {
        this.code = "BAD_REQUEST";
        this.message = message;
    }
}
