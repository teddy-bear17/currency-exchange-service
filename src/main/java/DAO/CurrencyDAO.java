package DAO;

import exceptions.AlreadyExistsException;
import models.Currency;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDAO {
    private final String getAllCurrenciesSql = "SELECT * FROM currencies";
    private final String getCurrencyByCodeSql = "SELECT * FROM currencies WHERE code = ?";
    private final String insertCurrencySql = "INSERT INTO currencies (code, full_name, sign) VALUES (?, ?, ?)";

    public List<Currency> getAllCurrencies() throws SQLException {
        List<Currency> currencies = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(getAllCurrenciesSql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Currency currency = new Currency();
                currency.setId(rs.getInt("id"));
                currency.setCode(rs.getString("code"));
                currency.setFullName(rs.getString("full_name"));
                currency.setSign(rs.getString("sign"));
                currencies.add(currency);
            }
        }
        return currencies;
    }

    public Optional<Currency> getCurrencyByCode(String code) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(getCurrencyByCodeSql)) {

            stmt.setString(1, code);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Currency currency = new Currency();
                    currency.setId(rs.getInt("id"));
                    currency.setCode(rs.getString("code"));
                    currency.setFullName(rs.getString("full_name"));
                    currency.setSign(rs.getString("sign"));
                    return Optional.of(currency);
                }
            }
        }
        return Optional.empty();
    }

    public Currency postCurrency(Currency currency) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertCurrencySql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, currency.getCode());
            stmt.setString(2, currency.getFullName());
            stmt.setString(3, currency.getSign());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Не удалось добавить валюту.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    currency.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Не удалось добавить валюту. Не получен ID");
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 19 || e.getMessage() != null && e.getMessage().contains("UNIQUE constraint failed")) {
                throw new AlreadyExistsException("Валюта с такими кодами уже существует");
            }
            throw e;
        }
        return currency;
    }
}
