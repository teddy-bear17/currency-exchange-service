package servlets;

import DTO.ExchangeRateResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servicies.ExchangeRateService;
import validators.RequestValidator;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRateService service = new ExchangeRateService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        try {
            RequestValidator.validateCurrencyPairPath(pathInfo);

            String currencyCodes = pathInfo.substring(1).toUpperCase();
            String baseCode = currencyCodes.substring(0, 3);
            String targetCode = currencyCodes.substring(3, 6);

            ExchangeRateResponseDTO exchange = service.getExactExchangeRate(baseCode, targetCode);
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), exchange);
        } catch (BadRequestException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), Map.of("message", e.getMessage()));
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(resp.getWriter(), Map.of("message", e.getMessage()));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), Map.of("message", "Ошибка базы данных"));
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        String rateStr = extractRateFromRequestBody(req);

        try {
            RequestValidator.validateCurrencyPairPath(pathInfo);

            BigDecimal rate = RequestValidator.parseAndValidatePositiveDecimal(
                    rateStr,
                    "Неверный формат курса (rate должен быть числом)",
                    "Обменный курс должен быть положительным числом"
            );

            String currencyCodes = pathInfo.substring(1).toUpperCase();
            String baseCode = currencyCodes.substring(0, 3);
            String targetCode = currencyCodes.substring(3, 6);

            ExchangeRateResponseDTO updatedExchange = service.updateExchangeRate(baseCode, targetCode, rate);
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), updatedExchange);

        } catch (BadRequestException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), Map.of("message", e.getMessage()));
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(resp.getWriter(), Map.of("message", e.getMessage()));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), Map.of("message", "Ошибка базы данных"));
        }
    }

    private String extractRateFromRequestBody(HttpServletRequest req) throws IOException {
        String param = req.getParameter("rate");
        if (param != null) return param;

        try (var reader = req.getReader()) {
            String body = reader.lines().reduce("", (acc, line) -> acc + line);
            for (String pair : body.split("&")) {
                String[] parts = pair.split("=");
                if (parts.length == 2 && parts[0].equals("rate")) {
                    return java.net.URLDecoder.decode(parts[1], java.nio.charset.StandardCharsets.UTF_8);
                }
            }
        }
        return null;
    }
}
