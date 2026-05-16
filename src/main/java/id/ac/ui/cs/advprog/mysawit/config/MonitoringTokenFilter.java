package id.ac.ui.cs.advprog.mysawit.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class MonitoringTokenFilter extends OncePerRequestFilter {

    private static final String PROMETHEUS_PATH = "/actuator/prometheus";
    private static final String BEARER_PREFIX = "Bearer ";

    private final String monitoringToken;

    public MonitoringTokenFilter(@Value("${monitoring.token:}") String monitoringToken) {
        this.monitoringToken = monitoringToken == null ? "" : monitoringToken.trim();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        if (!isPrometheusEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!StringUtils.hasText(monitoringToken)) {
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Monitoring token is not configured");
            return;
        }

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!isValidAuthorization(authorization)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid monitoring token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPrometheusEndpoint(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (StringUtils.hasText(contextPath) && requestUri != null && requestUri.startsWith(contextPath)) {
            requestUri = requestUri.substring(contextPath.length());
        }
        return PROMETHEUS_PATH.equals(requestUri);
    }

    private boolean isValidAuthorization(String authorization) {
        if (!StringUtils.hasText(authorization) || !authorization.startsWith(BEARER_PREFIX)) {
            return false;
        }

        String providedToken = authorization.substring(BEARER_PREFIX.length()).trim();
        byte[] expected = monitoringToken.getBytes(StandardCharsets.UTF_8);
        byte[] provided = providedToken.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(expected, provided);
    }
}
