package DAO;

import exceptions.AlreadyExistsException;
import models.Currency;
import models.Exchange;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDAO {
    private final String getAllExchangeRatesSql = "SELECT e.id, e.rate, " +
            "c1.id as base_id, c1.code as base_code, c1.full_name as base_full_name, c1.sign as base_sign, " +
            "c2.id as target_id, c2.code as target_code, c2.full_name as target_full_name, c2.sign as target_sign " +
            "FROM exchange_rates e " +
            "JOIN currencies c1 ON e.base_currency_id = c1.id " +
            "JOIN currencies c2 ON e.target_currency_id = c2.id";
    private final String getExactExchangeRateSql = "SELECT e.id, e.rate, " +
            "c1.id as base_id, c1.code as base_code, c1.full_name as base_full_name, c1.sign as base_sign, " +
            "c2.id as target_id, c2.code as target_code, c2.full_name as target_full_name, c2.sign as target_sign " +
            "FROM exchange_rates e " +
            "JOIN currencies c1 ON e.base_currency_id = c1.id " +
            "JOIN currencies c2 ON e.target_currency_id = c2.id " +
            "WHERE c1.code = ? AND c2.code = ?";

    private final String insertExchangeRateSql = "INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) VALUES (?, ?, ?)";
    private  final String updateExchangeRateSql = "UPDATE exchange_rates SET rate = ? WHERE base_currency_id = ? AND target_currency_id = ?";

    public List<Exchange> getAllExchangeRates() throws SQLException {
        List<Exchange> exchangeRates = new ArrayList<>();
        try(Connection connection = DBConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(getAllExchangeRatesSql);
            ResultSet rs = stmt.executeQuery()) {
            Exchange exchange;
            Currency currency;
            while (rs.next()) {
                exchange = new Exchange();
                exchange.setId(rs.getInt("id"));
                exchange.setRate(rs.getBigDecimal("rate"));

                currency = new Currency();
                currency.setId(rs.getInt("base_id"));
                currency.setCode(rs.getString("base_code"));
                currency.setFullName(rs.getString("base_full_name"));
                currency.setSign(rs.getString("base_sign"));

                exchange.setBaseCurrency(currency);

                currency = new Currency();
                currency.setId(rs.getInt("target_id"));
                currency.setCode(rs.getString("target_code"));
                currency.setFullName(rs.getString("target_full_name"));
                currency.setSign(rs.getString("target_sign"));

                exchange.setTargetCurrency(currency);
                exchangeRates.add(exchange);
            }
        }
        return exchangeRates;
    }

    public Optional<Exchange> getExactExchangeRate(String baseCode, String targetCode) throws SQLException {
        try(Connection connection = DBConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(getExactExchangeRateSql)) {
            stmt.setString(1, baseCode);
            stmt.setString(2, targetCode);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Exchange exchange = new Exchange();
                    exchange.setId(rs.getInt("id"));
                    exchange.setRate(rs.getBigDecimal("rate"));

                    Currency baseCurrency = new Currency();
                    baseCurrency.setId(rs.getInt("base_id"));
                    baseCurrency.setCode(rs.getString("base_code"));
                    baseCurrency.setFullName(rs.getString("base_full_name"));
                    baseCurrency.setSign(rs.getString("base_sign"));

                    exchange.setBaseCurrency(baseCurrency);

                    Currency targetCurrency = new Currency();
                    targetCurrency.setId(rs.getInt("target_id"));
                    targetCurrency.setCode(rs.getString("target_code"));
                    targetCurrency.setFullName(rs.getString("target_full_name"));
                    targetCurrency.setSign(rs.getString("target_sign"));

                    exchange.setTargetCurrency(targetCurrency);

                    return Optional.of(exchange);
                }
            }
        }
        return Optional.empty();
    }

    public Exchange insertExchangeRate(Exchange exchange) throws SQLException {
        try(Connection connection = DBConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(insertExchangeRateSql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, exchange.getBaseCurrency().getId());
            stmt.setInt(2, exchange.getTargetCurrency().getId());
            stmt.setBigDecimal(3, exchange.getRate());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    exchange.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Не удалось добавить обменный курс. Не получен ID");
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 19 || (e.getMessage() != null && e.getMessage().contains("UNIQUE constraint failed"))) {
                throw new AlreadyExistsException("Валютная пара с такими кодами уже существует");
            }
            throw e;
        }
        return exchange;
    }

    public void updateExchangeRate(Exchange exchange) throws SQLException {
        try(Connection connection = DBConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(updateExchangeRateSql)) {
            stmt.setBigDecimal(1, exchange.getRate());
            stmt.setInt(2, exchange.getBaseCurrency().getId());
            stmt.setInt(3, exchange.getTargetCurrency().getId());

            stmt.executeUpdate();
        }
    }
}
