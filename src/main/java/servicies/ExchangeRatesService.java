package servicies;

import DAO.CurrencyDAO;
import DAO.ExchangeRateDAO;
import DTO.ExchangeRateResponseDTO;
import exceptions.AlreadyExistsException;
import exceptions.NotFoundException;
import models.Exchange;

import java.math.BigDecimal;
import java.sql.SQLException;
import models.Currency;
import java.util.List;

public class ExchangeRatesService {
    private final CurrencyDAO currencyDAO = new CurrencyDAO();
    private final ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO();

    public List<ExchangeRateResponseDTO> getExchangeRates() throws SQLException {
        return exchangeRateDAO.getAllExchangeRates().stream().map(ExchangeRateResponseDTO::from).toList();
    }

    public ExchangeRateResponseDTO postExchangeRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) throws SQLException, AlreadyExistsException {

        Currency baseCurrency = currencyDAO.getCurrencyByCode(baseCurrencyCode).orElseThrow(() -> new NotFoundException("Базовая валюта " + baseCurrencyCode + " не найдена"));
        Currency targetCurrency = currencyDAO.getCurrencyByCode(targetCurrencyCode).orElseThrow(() -> new NotFoundException("Целевая валюта " + targetCurrencyCode + " не найдена"));


        Exchange exchange = new Exchange();
        exchange.setBaseCurrency(baseCurrency);
        exchange.setTargetCurrency(targetCurrency);
        exchange.setRate(rate);
        return ExchangeRateResponseDTO.from(exchangeRateDAO.insertExchangeRate(exchange));
    }
}
