package DTO;

import models.Exchange;

import java.math.BigDecimal;

public record ExchangeRateResponseDTO(
        int id,
        CurrencyResponseDTO baseCurrency,
        CurrencyResponseDTO targetCurrency,
        BigDecimal rate
) {
    public static ExchangeRateResponseDTO from(Exchange exchange) {
        return new ExchangeRateResponseDTO(
                exchange.getId(),
                CurrencyResponseDTO.from(exchange.getBaseCurrency()),
                CurrencyResponseDTO.from(exchange.getTargetCurrency()),
                exchange.getRate()
        );
    }
}