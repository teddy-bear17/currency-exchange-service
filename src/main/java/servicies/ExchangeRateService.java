package servicies;

import DAO.ExchangeRateDAO;
import DTO.ExchangeRateResponseDTO;
import exceptions.NotFoundException;
import models.Exchange;

import java.math.BigDecimal;
import java.sql.SQLException;

public class ExchangeRateService {
    private final ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO();

    public ExchangeRateResponseDTO getExactExchangeRate(String baseCode, String targetCode) throws SQLException {
        return exchangeRateDAO.getExactExchangeRate(baseCode, targetCode).map(ExchangeRateResponseDTO::from).orElseThrow(() -> new NotFoundException("Обменый курс с кодами валют " + baseCode + " " + targetCode + " не найден"));
    }

    public ExchangeRateResponseDTO updateExchangeRate(String baseCode, String targetCode, BigDecimal rate) throws SQLException {
        Exchange exchange = exchangeRateDAO.getExactExchangeRate(baseCode, targetCode)
                .orElseThrow(() -> new NotFoundException("Валютная пара отсутствует в базе данных"));

        exchange.setRate(rate);
        exchangeRateDAO.updateExchangeRate(exchange);
        return ExchangeRateResponseDTO.from(exchange);
    }
}
