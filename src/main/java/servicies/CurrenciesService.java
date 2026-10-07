package servicies;

import DAO.CurrencyDAO;
import DTO.CurrencyResponseDTO;
import exceptions.AlreadyExistsException;
import models.Currency;

import java.sql.SQLException;
import java.util.List;

public class CurrenciesService {
    private final CurrencyDAO currencyDAO = new CurrencyDAO();

    public List<CurrencyResponseDTO> getCurrencies() throws SQLException {
        return currencyDAO.getAllCurrencies().stream().map(CurrencyResponseDTO::from).toList();
    }

    public CurrencyResponseDTO postCurrency(String name, String code, String sign) throws SQLException, AlreadyExistsException {
        Currency currency = new Currency();
        currency.setFullName(name);
        currency.setCode(code);
        currency.setSign(sign);

        return CurrencyResponseDTO.from(currencyDAO.postCurrency(currency));
    }




}
