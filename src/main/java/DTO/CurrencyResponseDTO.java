package DTO;

import models.Currency;

public record CurrencyResponseDTO(
        int id,
        String name,
        String code,
        String sign
) {
    public static CurrencyResponseDTO from(Currency currency) {
        return new CurrencyResponseDTO(
                currency.getId(),
                currency.getFullName(),
                currency.getCode(),
                currency.getSign()
        );
    }
}