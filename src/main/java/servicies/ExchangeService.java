package servicies;

import DAO.CurrencyDAO;
import DAO.ExchangeRateDAO;
import DTO.ExchangeResponseDTO;
import exceptions.NotFoundException;
import models.Exchange;
import models.Currency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;

import java.util.Optional;

public class ExchangeService {
    private final CurrencyDAO currencyDAO = new CurrencyDAO();
    private final ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO();

    public ExchangeResponseDTO calculateExchange(String baseCode, String targetCode, BigDecimal amount) throws SQLException {
        Currency baseCurrency = currencyDAO.getCurrencyByCode(baseCode).orElseThrow(() -> new NotFoundException("Валюта " + baseCode + " не найдена"));
        Currency targetCurrency = currencyDAO.getCurrencyByCode(targetCode).orElseThrow(() -> new NotFoundException("Валюта " + targetCode + " не найдена"));
        BigDecimal rate = findExchangeRate(baseCode, targetCode);
        if (rate == null) {
            throw new NotFoundException("Обменный курс для указанной пары не найден");
        } 
        BigDecimal convertedAmount = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        return ExchangeResponseDTO.from(baseCurrency, targetCurrency, rate, amount, convertedAmount);
    }

    private BigDecimal findExchangeRate(String baseCode, String targetCode) throws SQLException {
        Optional<Exchange> directExchange = exchangeRateDAO.getExactExchangeRate(baseCode, targetCode);
        if (directExchange.isPresent()) {
            return directExchange.get().getRate();
        }

        Optional<Exchange> reverseExchange = exchangeRateDAO.getExactExchangeRate(targetCode, baseCode);
        if (reverseExchange.isPresent()) {
            BigDecimal rate = reverseExchange.get().getRate();
            return BigDecimal.ONE.divide(rate, 6, RoundingMode.HALF_UP);
        }

        Optional<Exchange> usdA = exchangeRateDAO.getExactExchangeRate("USD", baseCode);
        Optional<Exchange> usdB = exchangeRateDAO.getExactExchangeRate("USD", targetCode);
        if (usdA.isPresent() && usdB.isPresent()) {
            return usdB.get().getRate().divide(usdA.get().getRate(), 6, RoundingMode.HALF_UP);
        }
        return null;
    }
}
