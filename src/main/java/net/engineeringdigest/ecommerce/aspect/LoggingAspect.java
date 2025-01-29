package net.engineeringdigest.ecommerce.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public LoggingAspect(@Qualifier("jsonKafkaTemplate") KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public Object logApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            
            Map<String, Object> logData = new HashMap<>();
            logData.put("method", methodName);
            logData.put("executionTime", executionTime);
            logData.put("status", "SUCCESS");
            
            logger.info("Method: {} completed in {}ms", methodName, executionTime);
            kafkaTemplate.send("audit-logs", logData);
            
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            
            Map<String, Object> logData = new HashMap<>();
            logData.put("method", methodName);
            logData.put("executionTime", executionTime);
            logData.put("status", "ERROR");
            logData.put("error", e.getMessage());
            
            logger.error("Method: {} failed after {}ms. Error: {}", methodName, executionTime, e.getMessage());
            kafkaTemplate.send("audit-logs", logData);
            
            throw e;
        }
    }
}
