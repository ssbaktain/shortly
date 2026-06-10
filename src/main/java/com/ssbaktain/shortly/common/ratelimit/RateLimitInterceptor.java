package com.ssbaktain.shortly.common.ratelimit;

import com.ssbaktain.shortly.common.exception.RateLimitExceededException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final int CAPACITY = 5;
    private static final int REFILL_TOKENS = 3;
    private static final Duration REFILL_PERIOD = Duration.ofMinutes(1);

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        if (!"POST".equals(request.getMethod())) {
            return true;
        }

        String key = resolveKey(request);
        Bucket bucket = buckets.computeIfAbsent(key, k -> newBucket());

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            return true;
        }

        long retryAfterSeconds = probe.getNanosToWaitForRefill() / 1_000_000_000;
        throw new RateLimitExceededException(retryAfterSeconds);
    }

    @SuppressWarnings("unchecked")
    private String resolveKey(HttpServletRequest request) {
        Map<String, String> pathVariables = (Map<String, String>)
                request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String shortKey = pathVariables != null ? pathVariables.get("shortKey") : "unknown";
        return request.getRemoteAddr() + ":" + shortKey;
    }

    private Bucket newBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(CAPACITY)
                .refillGreedy(REFILL_TOKENS, REFILL_PERIOD)
                .build();
        return Bucket.builder().addLimit(limit).build();
    }
}
