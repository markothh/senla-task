package exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter("/*")
public class ExceptionFilter implements Filter {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (Throwable t) {
            response.setContentType("application/json");
            response.getWriter().write(
                    mapper.writeValueAsString(new ErrorResponse("ERROR", t.getMessage()))
            );
            response.getWriter().flush();
        }
    }
}