package com.internship.tool.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.tool.entity.AuditLog;
import com.internship.tool.entity.Compliance;
import com.internship.tool.repository.AuditLogRepository;
import com.internship.tool.repository.ComplianceRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogRepository auditRepo;
    private final ComplianceRepository complianceRepo;
    private final ObjectMapper objectMapper;

    @Around("execution(* com.internship.tool.service.*.*(..))")
    public Object logAudit(ProceedingJoinPoint joinPoint) throws Throwable {

        String methodName = joinPoint.getSignature().getName();

        // ❌ Skip GET methods
        if (methodName.toLowerCase().contains("get")) {
            return joinPoint.proceed();
        }

        System.out.println("🔥 AOP TRIGGERED: " + methodName);

        Object[] args = joinPoint.getArgs();

        Object oldValue = null;

        // ✅ GET OLD VALUE (for update/delete)
        if ((methodName.toLowerCase().contains("update") || methodName.toLowerCase().contains("delete"))
                && args.length > 0) {

            Long id = (Long) args[0];
            Optional<Compliance> existing = complianceRepo.findById(id);

            if (existing.isPresent()) {
                oldValue = existing.get();
            }
        }

        // 👉 EXECUTE ORIGINAL METHOD
        Object result = joinPoint.proceed();

        try {
            AuditLog log = new AuditLog();

            log.setEntityType("Compliance");
            log.setAction(methodName);
            log.setChangedBy("SYSTEM");
            log.setChangedAt(LocalDateTime.now());

            // ✅ ENTITY ID
            if (result instanceof Compliance comp) {
                log.setEntityId(comp.getId());
            } else if (methodName.toLowerCase().contains("delete") && args.length > 0) {
                log.setEntityId((Long) args[0]);
            }

            // ✅ SET OLD VALUE JSON
            if (oldValue != null) {
                log.setOldValue(objectMapper.writeValueAsString(oldValue));
            }

            // ✅ SET NEW VALUE JSON
            if (result != null) {
                log.setNewValue(objectMapper.writeValueAsString(result));
            }

            auditRepo.save(log);

            System.out.println("✅ Audit Saved: " + methodName);

        } catch (Exception e) {
            System.out.println("❌ Audit failed: " + e.getMessage());
        }

        return result;
    }
}