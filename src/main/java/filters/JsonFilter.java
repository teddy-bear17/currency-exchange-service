package filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(urlPatterns = {
        "/currencies",
        "/currency/*",
        "/exchangeRates",
        "/exchangeRate/*",
        "/exchange"
})
public class JsonFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        request.setCharacterEncoding("UTF-8");

        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        chain.doFilter(request, response);
    }
}