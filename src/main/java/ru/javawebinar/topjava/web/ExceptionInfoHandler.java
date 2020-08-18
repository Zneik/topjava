package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.javawebinar.topjava.util.ValidationUtil;
import ru.javawebinar.topjava.util.exception.ErrorInfo;
import ru.javawebinar.topjava.util.exception.ErrorType;
import ru.javawebinar.topjava.util.exception.IllegalRequestDataException;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static ru.javawebinar.topjava.util.exception.ErrorType.*;

@RestControllerAdvice(annotations = RestController.class)
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
public class ExceptionInfoHandler {
    private static final Logger log = LoggerFactory.getLogger(ExceptionInfoHandler.class);
    private static final Map<String, String> SQL_CONSTRAINS = Map.of(
            "users_unique_email_idx", "user.email.duplicate",
            "meals_unique_user_datetime_idx", "meal.dateTime.duplicate"
    );

    private final MessageSource messageSource;

    public ExceptionInfoHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    //  http://stackoverflow.com/a/22358422/548473
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(NotFoundException.class)
    public ErrorInfo handleError(HttpServletRequest req, NotFoundException e) {
        return logAndGetErrorInfo(req, e, false, DATA_NOT_FOUND);
    }

    @ResponseStatus(value = HttpStatus.CONFLICT)  // 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorInfo conflict(HttpServletRequest req, DataIntegrityViolationException e) {
        String message = ValidationUtil.getRootCause(e).getMessage();
        if (message != null) {
            for (Map.Entry<String, String> item : SQL_CONSTRAINS.entrySet()) {
                if (message.toLowerCase().contains(item.getKey())) {
                    return logAndGetErrorInfo(req,
                            e,
                            true,
                            VALIDATION_ERROR,
                            new String[]{messageSource.getMessage(item.getValue(), null, LocaleContextHolder.getLocale())});
                }
            }
        }
        return logAndGetErrorInfo(req, e, true, DATA_ERROR);
    }

    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)  // 422
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    public ErrorInfo bindValidationError(HttpServletRequest request, Exception exception) {
        BindingResult bindingResult;
        if (exception instanceof BindException) {
            bindingResult = ((BindException) exception).getBindingResult();
        } else {
            bindingResult = ((MethodArgumentNotValidException) exception).getBindingResult();
        }
        String[] details = bindingResult.getFieldErrors().stream()
                .map(fieldError -> String.format("[%s] - %s", fieldError.getField(), fieldError.getDefaultMessage()))
                .toArray(String[]::new);
        return logAndGetErrorInfo(request, exception, true, VALIDATION_ERROR, details);
    }

    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)  // 422
    @ExceptionHandler({IllegalRequestDataException.class, MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ErrorInfo illegalRequestDataError(HttpServletRequest req, Exception e) {
        return logAndGetErrorInfo(req, e, false, VALIDATION_ERROR);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorInfo handleError(HttpServletRequest req, Exception e) {
        return logAndGetErrorInfo(req, e, true, APP_ERROR);
    }

    //    https://stackoverflow.com/questions/538870/should-private-helper-methods-be-static-if-they-can-be-static
    private static ErrorInfo logAndGetErrorInfo(HttpServletRequest req, Exception e, boolean logException, ErrorType errorType) {
        return logAndGetErrorInfo(req, e, logException, errorType, null);
    }

    private static ErrorInfo logAndGetErrorInfo(HttpServletRequest req,
                                                Exception e,
                                                boolean logException,
                                                ErrorType errorType,
                                                String[] details) {
        Throwable rootCause = ValidationUtil.getRootCause(e);
        if (logException) {
            log.error(errorType + " at request " + req.getRequestURL(), rootCause);
        } else {
            log.warn("{} at request  {}: {}", errorType, req.getRequestURL(), rootCause.toString());
        }
        return new ErrorInfo(req.getRequestURL(), errorType, details == null ?
                new String[]{getLocaleMessage(rootCause)} :
                details);
    }

    private static String getLocaleMessage(Throwable throwable) {
        return throwable.getLocalizedMessage() != null ?
                throwable.getLocalizedMessage() :
                throwable.getClass().getName();
    }
}