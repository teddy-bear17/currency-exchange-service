package DTO;

import models.Currency;

import java.math.BigDecimal;

public record ExchangeResponseDTO(
        CurrencyResponseDTO baseCurrency,
        CurrencyResponseDTO targetCurrency,
        BigDecimal rate,
        BigDecimal amount,
        BigDecimal convertedAmount
) {
    public static ExchangeResponseDTO from(
            Currency baseCurrency,
            Currency targetCurrency,
            BigDecimal rate,
            BigDecimal amount,
            BigDecimal convertedAmount
    ) {
        return new ExchangeResponseDTO(
                CurrencyResponseDTO.from(baseCurrency),
                CurrencyResponseDTO.from(targetCurrency),
                rate,
                amount,
                convertedAmount
        );
    }
}