package uk.gov.justice.laa.fee.scheme.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

  @Pointcut("execution(* uk.gov.justice.laa.fee.scheme.controller.*.*(..))")
  public void controllerLayer() {}

  @Pointcut("execution(* uk.gov.justice.laa.fee.scheme.service.*.*(..))")
  public void serviceLayer() {}

  @Before("controllerLayer() || serviceLayer()")
  public void logMethodEntry(JoinPoint joinPoint) {
    log.info("ENTRY -> {}.{}() | arguments: {}", joinPoint.getTarget().getClass().getSimpleName(), joinPoint.getSignature().getName(), joinPoint.getArgs());
  }

  @AfterReturning(value = "controllerLayer() || serviceLayer()", returning = "result")
  public void logMethodExit(JoinPoint joinPoint, Object result) {
    log.info("EXIT -> {}.{}() | result: {}", joinPoint.getTarget().getClass().getSimpleName(), joinPoint.getSignature().getName(), result);
  }

}
