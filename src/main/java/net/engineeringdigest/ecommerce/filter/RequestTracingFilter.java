package net.engineeringdigest.ecommerce.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestTracingFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(RequestTracingFilter.class);
    private static final String TRACE_ID = "traceId";
    private static final String START_TIME = "startTime";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        try {
            String traceId = UUID.randomUUID().toString();
            MDC.put(TRACE_ID, traceId);
            MDC.put(START_TIME, String.valueOf(System.currentTimeMillis()));
            response.setHeader("X-Trace-Id", traceId);

            logger.info("Incoming request {} {} from {}", 
                request.getMethod(), 
                request.getRequestURI(),
                request.getRemoteAddr());

            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - Long.parseLong(MDC.get(START_TIME));
            logger.info("Request completed in {}ms with status {}", 
                duration, 
                response.getStatus());
            MDC.clear();
        }
    }
}
