package uk.gov.justice.laa.fee.scheme.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class SelectiveLoggingAspect {

  @Around("@annotation(loggable)")
  public Object logMethod(ProceedingJoinPoint joinPoint, Loggable loggable) throws Throwable {
    long start = System.currentTimeMillis();

    // Log only selected fields
    Object[] args = joinPoint.getArgs();
    String[] fieldsToLog = loggable.fields();

    String argsLog = Arrays.stream(args)
        .map(arg -> filterFields(arg, fieldsToLog))
        .collect(Collectors.joining(", "));

    log.info("➡️ Entering {} with filtered args: {}",
        joinPoint.getSignature().toShortString(), argsLog);

    try {
      Object result = joinPoint.proceed();
      long duration = System.currentTimeMillis() - start;
      log.info("✅ Completed {} in {} ms with result: {}",
          joinPoint.getSignature().toShortString(), duration, result);
      return result;
    } catch (Throwable ex) {
      long duration = System.currentTimeMillis() - start;
      log.error("❌ Exception in {} after {} ms, cause: {}",
          joinPoint.getSignature().toShortString(), duration, ex.getMessage(), ex);
      throw ex;
    }
  }

  @Around("@annotation(logEntry)")
  public Object logEntryExit(ProceedingJoinPoint joinPoint, Loggable.LogEntry logEntry) throws Throwable {
    Object[] args = joinPoint.getArgs();
    String[] fieldsToLog = logEntry.fields();

    String argsLog = Arrays.stream(args)
        .map(arg -> filterFields(arg, fieldsToLog))
        .collect(Collectors.joining(", "));

    log.info("➡️ Entering {} with args: {}", joinPoint.getSignature().toShortString(), argsLog);
    return joinPoint.proceed();
  }

  @AfterReturning(pointcut = "@annotation(logExit)", returning = "result")
  public void logExit(JoinPoint joinPoint, Loggable.LogExit logExit, Object result) {
    if (logExit.logResult()) {
      log.info("✅ Exiting {} with result: {}", joinPoint.getSignature().toShortString(), result);
    } else {
      log.info("✅ Exiting {}", joinPoint.getSignature().toShortString());
    }
  }


  @AfterReturning("@annotation(logCustomResponseOnExit)")
  public Object logCustomResponseOnExit(JoinPoint joinPoint, Loggable.LogCustomResponseOnExit logCustomResponseOnExit) throws Throwable {
    Object[] args = joinPoint.getArgs();
    String[] fieldsToLog = logCustomResponseOnExit.fields();

    String argsLog = Arrays.stream(args)
        .map(arg -> filterFields(arg, fieldsToLog))
        .collect(Collectors.joining(", "));

    log.info("➡️ Exiting {} with outcome: {}", joinPoint.getSignature().toShortString(), argsLog);
    return joinPoint.getThis();
  }

  private String filterFields(Object obj, String[] fieldsToLog) {
    if (obj == null || fieldsToLog.length == 0) return obj.toString();

    return Arrays.stream(fieldsToLog)
        .map(fieldName -> {
          try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return fieldName + "=" + field.get(obj);
          } catch (Exception e) {
            return fieldName + "=<not-found>";
          }
        })
        .collect(Collectors.joining(", "));
  }
}
