package by.bank.consumerlending.site.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class AppExceptionHandler {

    @ExceptionHandler(QaQuestionAlreadyAnsweredException.class)
    public ResponseEntity<ExceptionResponse> handleConflictException(QaQuestionAlreadyAnsweredException ex) {
        log.warn("Конфликт процессов бизнес-логики [{}]: [{}]", ex.getCode(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ExceptionResponse(ex.getCode(), ex.getMessage(), null));
    }

    @ExceptionHandler(QaInactiveTopicException.class)
    public ResponseEntity<ExceptionResponse> handleInactiveTopicException(QaInactiveTopicException ex) {
        log.warn("Ошибка создания вопроса в разделе Q&A [{}]: [{}]", ex.getCode(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(ex.getCode(), ex.getMessage(), null));
    }

    @ExceptionHandler(QaTooManyQuestionsException.class)
    public ResponseEntity<ExceptionResponse> handleNooManyQuestionsException(QaTooManyQuestionsException ex) {
        log.warn("Ошибка получения вопросов текущего пользователя в разделе Q&A [{}]: [{}]", ex.getCode(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(ex.getCode(), ex.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, List<String>> details = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            String message = error.getDefaultMessage() != null ? error.getDefaultMessage() : "Невалидное значение";
            details.computeIfAbsent(error.getField(), _ -> new ArrayList<>()).add(message);
        }
        log.warn("Ошибка валидации запроса [{}]: [{}]", ex.getStatusCode(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse("VALIDATION_ERROR", "Ошибка валидации данных", details));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String fieldName = ex.getName();
        String requiredType = (ex.getRequiredType() != null)
                ? ex.getRequiredType().getSimpleName()
                : "unknown";
        String providedValue = String.valueOf(ex.getValue());
        StringUtils.truncate(providedValue);
        String message = String.format("Параметр '%s' должен быть типа %s. Получено: '%s'",
                fieldName, requiredType, providedValue);

        Map<String, List<String>> details = Map.of(
                fieldName, List.of("Некорректный формат значения")
        );
        log.warn("Ошибка валидации типов [{}]: [{}]", ex.getErrorCode(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse("VALIDATION_ERROR", message, details));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, List<String>> details = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.groupingBy(
                        violation -> {
                            String path = violation.getPropertyPath().toString();
                            return path.substring(path.lastIndexOf('.') + 1);
                        },
                        Collectors.mapping(ConstraintViolation::getMessage, Collectors.toList())
                ));
        log.warn("Ошибка параметров запроса [{}]", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse("VALIDATION_ERROR", "Ошибка валидации параметров запроса", details));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ExceptionResponse> handleMethodValidationException(HandlerMethodValidationException ex) {
        String validationDetails = ex.getParameterValidationResults().stream()
                .map(result -> {
                    String paramName = result.getMethodParameter().getParameterName();
                    String errors = result.getResolvableErrors().stream()
                            .map(MessageSourceResolvable::getDefaultMessage)
                            .filter(Objects::nonNull)
                            .collect(Collectors.joining(", "));
                    return String.format("%s: %s", paramName, errors);
                })
                .collect(Collectors.joining("; "));
        log.warn("Ошибка валидации параметров запроса [{}]: [{}]", ex.getStatusCode(), validationDetails);

        Map<String, List<String>> details = new HashMap<>();

        for (ParameterValidationResult result : ex.getParameterValidationResults()) {
            String paramName = result.getMethodParameter().getParameterName();
            String key = (paramName != null) ? paramName : "parameter";

            List<String> messages = result.getResolvableErrors().stream()
                    .map(MessageSourceResolvable::getDefaultMessage)
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .toList();

            if (!messages.isEmpty()) {
                details.put(key, messages);
            }
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse("VALIDATION_ERROR", "Ошибка валидации параметров запроса", details));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        String genericMessage = "Ошибка чтения тела запроса или неверный формат данных";
        String errorDetail = "Некорректный синтаксис JSON или несоответствие типов";

        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife) {
            // Путь к полю извлекается через цепочку ссылок Reference
            String path = ife.getPath().stream()
                    .map(JacksonException.Reference::getPropertyName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("."));

            String target = ife.getTargetType() != null ? ife.getTargetType().getSimpleName() : "unknown";
            errorDetail = String.format("Поле '%s' ожидает тип %s, но получено: '%s'",
                    path.isEmpty() ? "root" : path, target, ife.getValue());
        } else if (cause instanceof DatabindException de) {
            errorDetail = "Ошибка обработки данных: " + de.getOriginalMessage();
        }
        log.warn("Ошибка входных данных в JSON: {}", errorDetail);

        Map<String, List<String>> details = Map.of("request_body", List.of(errorDetail));
        ExceptionResponse response = new ExceptionResponse(
                "MALFORMED_JSON",
                genericMessage,
                details
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleAuthorizationDenied(AuthorizationDeniedException ex) {
        log.warn("Попытка доступа пользователя без необходимых прав [{}]", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ExceptionResponse(
                        "ACCESS_DENIED",
                        "У вас недостаточно прав для выполнения этой операции",
                        null
                ));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleBadCredentials(BadCredentialsException ex) {
        log.warn("Неудачная попытка входа [{}]", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ExceptionResponse(
                        "UNAUTHORIZED",
                        "Ошибка авторизации: неверные данные или истекшая сессия",
                        null
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        String message = ex.getMessage();
        String targetField = "global";
        if (message != null) {
            String lowerMessage = message.toLowerCase();
            if (lowerMessage.contains("файл") || lowerMessage.contains("file")) {
                targetField = "file";
            } else if (lowerMessage.contains("id") || lowerMessage.contains("идентификатор")) {
                targetField = "id";
            }
        }
        Map<String, List<String>> details = Map.of(
                targetField, List.of(message != null ? message : "Некорректное значение аргумента")
        );

        log.warn("Нарушение бизнес-логики: [{}]", message);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse("BAD_REQUEST", "Некорректные параметры запроса", details));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleAll(Exception ex) {
        log.error("Непредвиденная ошибка сервера: ", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse("INTERNAL_SERVER_ERROR", "Произошла непредвиденная ошибка", null));
    }
}
