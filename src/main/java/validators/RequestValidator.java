package validators;

import exceptions.BadRequestException;
import java.math.BigDecimal;

public class RequestValidator {

    public static void requireAllNotBlank(String errorMessage, String... values) {
        for (String value : values) {
            if (value == null || value.isBlank()) {
                throw new BadRequestException(errorMessage);
            }
        }
    }

    public static void validateCurrencyCode(String code) {
        if (code == null || !code.matches("^[A-Za-z]{3}$")) {
            throw new BadRequestException("Код валюты должен состоять ровно из 3 букв");
        }
    }

    public static void validateCurrencyPath(String pathInfo) {
        if (pathInfo == null || pathInfo.equals("/")) {
            throw new BadRequestException("Код валюты отсутствует в адресе");
        }
        if (!pathInfo.matches("^/[A-Za-z]{3}$")) {
            throw new BadRequestException("Некорректный код валюты (ожидается 3 буквы)");
        }
    }

    public static void validateCurrencyPairPath(String pathInfo) {
        if (pathInfo == null || pathInfo.equals("/")) {
            throw new BadRequestException("Коды валют пары отсутствуют в адресе");
        }
        if (!pathInfo.matches("^/[A-Za-z]{6}$")) {
            throw new BadRequestException("Некорректный формат валютной пары (ожидается 6 букв)");
        }
    }

    public static BigDecimal parseAndValidatePositiveDecimal(String valueStr, String parseErrorMsg, String negativeErrorMsg) {
        if (valueStr == null || valueStr.isBlank()) {
            throw new BadRequestException("Отсутствует нужное поле формы");
        }

        BigDecimal value;
        try {
            value = new BigDecimal(valueStr);
        } catch (NumberFormatException e) {
            throw new BadRequestException(parseErrorMsg);
        }

        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException(negativeErrorMsg);
        }

        return value;
    }
}