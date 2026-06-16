package be.alkaram.mosquee.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    // Max requêtes par fenêtre de temps par IP
    private static final int MAX_REQUESTS_STRIPE = 10; // 10 paiements / minute
    private static final int MAX_REQUESTS_CONTACT = 5; // 5 messages / minute
    private static final int MAX_REQUESTS_AUTH = 5; // 5 tentatives login / minute
    private static final long WINDOW_MS = 60_000; // 1 minute

    private final Map<String, RequestCount> counts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String path = req.getRequestURI();
        String ip = getClientIp(req);

        int limit = -1;
        if (path.startsWith("/api/stripe/"))
            limit = MAX_REQUESTS_STRIPE;
        else if (path.equals("/api/contact"))
            limit = MAX_REQUESTS_CONTACT;
        else if (path.equals("/api/auth/login"))
            limit = MAX_REQUESTS_AUTH;

        if (limit > 0) {
            String key = ip + ":" + path;
            RequestCount rc = counts.computeIfAbsent(key, k -> new RequestCount());

            if (!rc.increment(limit)) {
                res.setStatus(429);
                res.setContentType("application/json");
                res.getWriter().write("{\"erreur\":\"Trop de requêtes. Réessayez dans une minute.\"}");
                return;
            }
        }

        chain.doFilter(req, res);
    }

    private String getClientIp(HttpServletRequest req) {
        String forwarded = req.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty())
            return forwarded.split(",")[0].trim();
        return req.getRemoteAddr();
    }

    static class RequestCount {
        AtomicInteger count = new AtomicInteger(0);
        long windowStart = System.currentTimeMillis();

        synchronized boolean increment(int limit) {
            long now = System.currentTimeMillis();
            if (now - windowStart > WINDOW_MS) {
                count.set(0);
                windowStart = now;
            }
            return count.incrementAndGet() <= limit;
        }
    }
}