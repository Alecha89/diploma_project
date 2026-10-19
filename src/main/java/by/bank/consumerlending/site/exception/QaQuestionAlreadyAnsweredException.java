package by.bank.consumerlending.site.exception;

import lombok.Getter;

@Getter
public class QaQuestionAlreadyAnsweredException extends RuntimeException {

    private final String code;

    private final String message;

    public QaQuestionAlreadyAnsweredException(String message) {
        this.code = "CONFLICT";
        this.message = message;
    }
}