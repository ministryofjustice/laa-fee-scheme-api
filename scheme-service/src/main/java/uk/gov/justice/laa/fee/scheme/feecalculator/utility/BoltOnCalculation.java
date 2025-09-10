package uk.gov.justice.laa.fee.scheme.feecalculator.utility;

import java.math.BigDecimal;

public record BoltOnCalculation(String name, Integer requested, BigDecimal amount) {}