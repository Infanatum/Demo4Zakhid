package com.example.Demo4Zakhid;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, AccessRecord> requestCounts = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            RateLimit rateLimit = handlerMethod.getMethod().getAnnotation(RateLimit.class);

            if (rateLimit != null) {
                String ip = request.getRemoteAddr();
                int maxRequests = rateLimit.maxRequests();
                int duration = rateLimit.durationInMinutes();
                AccessRecord record = requestCounts.computeIfAbsent(ip, k -> new AccessRecord(maxRequests, duration));

                if (!record.grantAccess()) {
                    response.setStatus(HttpServletResponse.SC_BAD_GATEWAY); // 502 Error
                    return false;
                }
            }
        }
        return true;
    }

    private static class AccessRecord {
        private final int maxRequests;
        private final int duration;
        private int requestCount;
        private LocalDateTime windowStart;

        public AccessRecord(int maxRequests, int duration) {
            this.maxRequests = maxRequests;
            this.duration = duration;
            this.requestCount = 0;
            this.windowStart = LocalDateTime.now();
        }

        public synchronized boolean grantAccess() {
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(windowStart.plusMinutes(duration))) {
                windowStart = now;
                requestCount = 0;
            }

            if (requestCount < maxRequests) {
                requestCount++;
                return true;
            } else {
                return false;
            }
        }
    }
}