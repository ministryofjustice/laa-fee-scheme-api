package uk.gov.justice.laa.fee.scheme.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * To capture logs when logging level is DEBUG.
 */

@Aspect
@Component
@Slf4j
public class LoggingAspect {

  @Pointcut("execution(* uk.gov.justice.laa.fee.scheme.controller.*.*(..))")
  public void controllerLayer() {}

  @Pointcut("execution(* uk.gov.justice.laa.fee.scheme.service.*.*(..))")
  public void serviceLayer() {}

  @Pointcut("execution(* uk.gov.justice.laa.fee.scheme.feecalculator.fixed.*(..))")
  public void fixedFeeCalculationLayer() {}

  @Pointcut("execution(* uk.gov.justice.laa.fee.scheme.feecalculator.hourly.*(..))")
  public void hourlyFeeCalculationLayer() {}

  @Before("controllerLayer() || serviceLayer() || fixedFeeCalculationLayer() || hourlyFeeCalculationLayer()")
  public void logMethodEntry(JoinPoint joinPoint) {
    log.debug("ENTRY -> {}.{}() | arguments: {}", joinPoint.getTarget().getClass().getSimpleName(),
        joinPoint.getSignature().getName(), joinPoint.getArgs());
  }

  @AfterReturning(value = "controllerLayer() || serviceLayer() || fixedFeeCalculationLayer() "
      + "|| hourlyFeeCalculationLayer()", returning = "result")
  public void logMethodExit(JoinPoint joinPoint, Object result) {
    log.debug("EXIT -> {}.{}() | result: {}", joinPoint.getTarget().getClass().getSimpleName(),
        joinPoint.getSignature().getName(), result);
  }

}


