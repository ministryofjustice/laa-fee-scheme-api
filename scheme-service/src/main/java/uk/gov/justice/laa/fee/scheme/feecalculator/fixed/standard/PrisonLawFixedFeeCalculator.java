package uk.gov.justice.laa.fee.scheme.feecalculator.fixed.standard;

import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_PRISON_HAS_ESCAPED;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_PRISON_MAY_HAVE_ESCAPED;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.buildValidationWarning;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.limit.LimitUtil.isEscapedCase;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;
import uk.gov.justice.laa.fee.scheme.service.VatRatesService;

/**
 * Calculate the prison law fee for a given fee entity and fee data.
 */
@Slf4j
@Component
public class PrisonLawFixedFeeCalculator extends StandardFixedFeeCalculator {

  private static final Set<String> FEE_CODES_ESCAPE_USING_ESCAPE_THRESHOLD = Set.of("PRIA", "PRIB2", "PRIC2", "PRID2", "PRIE2");
  private static final Set<String> FEE_CODES_ESCAPE_USING_FEE_LIMIT = Set.of("PRIB1", "PRIC1", "PRID1", "PRIE1");

  public PrisonLawFixedFeeCalculator(VatRatesService vatRatesService) {
    super(vatRatesService, true);
  }

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(CategoryType.PRISON_LAW);
  }

  @Override
  protected boolean handleEscapeCase(FeeCalculationRequest request,
                                     FeeEntity feeEntity,
                                     List<ValidationMessagesInner> messages, BigDecimal totalAmount) {

    BigDecimal profitCosts = toBigDecimal(request.getNetProfitCosts());
    BigDecimal waitingCosts = toBigDecimal(request.getNetWaitingCosts());
    BigDecimal total = profitCosts.add(waitingCosts);
    String feeCode = request.getFeeCode();

    if (FEE_CODES_ESCAPE_USING_ESCAPE_THRESHOLD.contains(feeCode)) {
      return escapeCaseValidation(feeEntity, messages, total);
    }
    if (FEE_CODES_ESCAPE_USING_FEE_LIMIT.contains(feeCode)) {
      feeLimitValidation(feeEntity, messages, total);
      return false;
    }
    return false;
  }

  /**
   * Calculate if the case may have escaped using fee limit,
   * escape flag will always be false.
   */
  private void feeLimitValidation(FeeEntity feeEntity, List<ValidationMessagesInner> validationMessages,
                                  BigDecimal totalAmount) {

    BigDecimal feeLimit = feeEntity.getTotalLimit();
    if (isEscapedCase(totalAmount, feeLimit)) {
      validationMessages.add(buildValidationWarning(WARN_PRISON_MAY_HAVE_ESCAPED,
          "Case has exceeded fee limit"));
    } else {
      log.warn("Case has not exceeded fee limit");
    }
  }

  /**
   * Calculate if the fee has escaped using EscapeThresholdLimit,
   * escape flag will be true when it has.
   */
  private boolean escapeCaseValidation(FeeEntity feeEntity, List<ValidationMessagesInner> validationMessages,
                                       BigDecimal totalAmount) {

    if (isEscapedCase(totalAmount, feeEntity)) {
      validationMessages.add(buildValidationWarning(WARN_PRISON_HAS_ESCAPED,
          "Case has escaped"));
      return true;
    }
    log.warn("Case has not escaped");
    return false;
  }

}