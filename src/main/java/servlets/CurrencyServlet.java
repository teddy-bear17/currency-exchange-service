package servlets;

import DTO.CurrencyResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servicies.CurrencyService;
import validators.RequestValidator;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyService service = new CurrencyService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        try {
            RequestValidator.validateCurrencyPath(pathInfo);

            String code = pathInfo.substring(1).toUpperCase();

            CurrencyResponseDTO currency = service.getExactCurrency(code);
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), currency);
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