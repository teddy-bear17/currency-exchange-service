package servlets;

import DTO.ExchangeRateResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.AlreadyExistsException;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servicies.ExchangeRatesService;
import validators.RequestValidator;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ExchangeRatesService service = new ExchangeRatesService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<ExchangeRateResponseDTO> exchangeRates = service.getExchangeRates();
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), exchangeRates);

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), Map.of("message", "Ошибка базы данных"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rateStr = req.getParameter("rate");

        try {
            RequestValidator.requireAllNotBlank("Отсутствует нужное поле формы", baseCurrencyCode, targetCurrencyCode, rateStr);
            RequestValidator.validateCurrencyCode(baseCurrencyCode);
            RequestValidator.validateCurrencyCode(targetCurrencyCode);
            BigDecimal rate = RequestValidator.parseAndValidatePositiveDecimal(
                    rateStr,
                    "Неверный формат курса (rate должен быть числом)",
                    "Обменный курс должен быть положительным числом"
            );

            ExchangeRateResponseDTO exchange = service.postExchangeRate(
                    baseCurrencyCode.toUpperCase(),
                    targetCurrencyCode.toUpperCase(),
                    rate
            );
            resp.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(resp.getWriter(), exchange);
        } catch (BadRequestException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), Map.of("message", e.getMessage()));
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(resp.getWriter(), Map.of("message", e.getMessage()));
        } catch (AlreadyExistsException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            objectMapper.writeValue(resp.getWriter(), Map.of("message", e.getMessage()));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), Map.of("message", "Ошибка базы данных"));
        }
    }
}