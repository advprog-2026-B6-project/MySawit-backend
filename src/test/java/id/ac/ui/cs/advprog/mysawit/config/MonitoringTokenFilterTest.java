package id.ac.ui.cs.advprog.mysawit.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MonitoringTokenFilterTest {

    @Test
    void doFilter_nonPrometheusEndpoint_continuesChain() throws ServletException, IOException {
        MonitoringTokenFilter filter = new MonitoringTokenFilter("secret-token");
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/actuator/health");
        when(request.getContextPath()).thenReturn("");

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).sendError(HttpServletResponse.SC_UNAUTHORIZED,
                "Invalid monitoring token");
    }

    @Test
    void doFilter_prometheusEndpointWithValidToken_continuesChain()
            throws ServletException, IOException {
        MonitoringTokenFilter filter = new MonitoringTokenFilter("secret-token");
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/actuator/prometheus");
        when(request.getContextPath()).thenReturn("");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer secret-token");

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).sendError(HttpServletResponse.SC_UNAUTHORIZED,
                "Invalid monitoring token");
    }

    @Test
    void doFilter_prometheusEndpointWithContextPathAndValidToken_continuesChain()
            throws ServletException, IOException {
        MonitoringTokenFilter filter = new MonitoringTokenFilter("secret-token");
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/api/actuator/prometheus");
        when(request.getContextPath()).thenReturn("/api");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer secret-token");

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    void doFilter_prometheusEndpointWithoutToken_returnsUnauthorized()
            throws ServletException, IOException {
        MonitoringTokenFilter filter = new MonitoringTokenFilter("secret-token");
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/actuator/prometheus");
        when(request.getContextPath()).thenReturn("");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        filter.doFilterInternal(request, response, chain);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED,
                "Invalid monitoring token");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    void doFilter_prometheusEndpointWithWrongToken_returnsUnauthorized()
            throws ServletException, IOException {
        MonitoringTokenFilter filter = new MonitoringTokenFilter("secret-token");
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/actuator/prometheus");
        when(request.getContextPath()).thenReturn("");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer wrong-token");

        filter.doFilterInternal(request, response, chain);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED,
                "Invalid monitoring token");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    void doFilter_prometheusEndpointWithoutConfiguredToken_returnsServiceUnavailable()
            throws ServletException, IOException {
        MonitoringTokenFilter filter = new MonitoringTokenFilter("");
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/actuator/prometheus");
        when(request.getContextPath()).thenReturn("");

        filter.doFilterInternal(request, response, chain);

        verify(response).sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                "Monitoring token is not configured");
        verify(chain, never()).doFilter(request, response);
    }
}
