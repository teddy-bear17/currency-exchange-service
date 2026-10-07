package servicies;

import DAO.CurrencyDAO;
import DTO.CurrencyResponseDTO;
import exceptions.NotFoundException;
import java.sql.SQLException;



public class CurrencyService {
    private final CurrencyDAO currencyDAO = new CurrencyDAO();
    public CurrencyResponseDTO getExactCurrency(String code) throws SQLException {
        return currencyDAO.getCurrencyByCode(code).map(CurrencyResponseDTO::from).orElseThrow(() -> new NotFoundException("Валюта с таким кодом не найдена"));
    }
}
