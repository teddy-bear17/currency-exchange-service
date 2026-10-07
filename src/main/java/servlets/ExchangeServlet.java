package servlets;

import DTO.ExchangeResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servicies.ExchangeService;
import validators.RequestValidator;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private  final ExchangeService service = new ExchangeService();
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCode = req.getParameter("from");
        String targetCode = req.getParameter("to");
        String amountStr = req.getParameter("amount");


        try {
            RequestValidator.requireAllNotBlank("Отсутствует нужный параметр", baseCode, targetCode, amountStr);
            RequestValidator.validateCurrencyCode(baseCode);
            RequestValidator.validateCurrencyCode(targetCode);
            BigDecimal amount = RequestValidator.parseAndValidatePositiveDecimal(amountStr,
                    "Неверный формат суммы для перевода (amount должно быть числом)",
                    "Сумма для перевода должна быть положительным числом");

            ExchangeResponseDTO exchangeResponseDTO = service.calculateExchange(baseCode.toUpperCase(), targetCode.toUpperCase(), amount);
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), exchangeResponseDTO);
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
}
