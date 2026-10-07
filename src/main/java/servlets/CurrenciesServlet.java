package servlets;

import DTO.CurrencyResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.AlreadyExistsException;
import exceptions.BadRequestException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servicies.CurrenciesService;
import validators.RequestValidator;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CurrenciesService service = new CurrenciesService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<CurrencyResponseDTO> currencies = service.getCurrencies();
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), currencies);
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), Map.of("message", "Ошибка базы данных"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");

        try {
            RequestValidator.requireAllNotBlank("Отсутствует нужное поле формы", name, code, sign);
            RequestValidator.validateCurrencyCode(code);

            CurrencyResponseDTO postedCurrency = service.postCurrency(name, code.toUpperCase(), sign);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(resp.getWriter(), postedCurrency);
        } catch (BadRequestException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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