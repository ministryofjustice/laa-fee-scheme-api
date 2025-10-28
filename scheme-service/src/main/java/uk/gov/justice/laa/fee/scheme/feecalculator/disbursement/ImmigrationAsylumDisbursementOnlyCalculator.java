package uk.gov.justice.laa.fee.scheme.feecalculator.disbursement;

import static uk.gov.justice.laa.fee.scheme.enums.LimitType.DISBURSEMENT;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_IMM_ASYLM_DISB_ONLY;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.checkLimitAndCapIfExceeded;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDouble;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.LimitContextNew;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

/**
 * Calculate the Immigration and asylum disbursement only fee for a given fee entity and fee calculation request.
 */
@Slf4j
@Component
public class ImmigrationAsylumDisbursementOnlyCalculator {

  /**
   * Calculated fee for Immigration and asylum disbursement only fee based on the provided fee entity and fee calculation request.
   */
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    BigDecimal requestedNetDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal requestedNetDisbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());
    String immigrationPriorAuthorityNumber = feeCalculationRequest.getImmigrationPriorAuthorityNumber();

    List<ValidationMessagesInner> validationMessages = new ArrayList<>();
    LimitContextNew disbursementLimitContext = new LimitContextNew(DISBURSEMENT, feeEntity.getDisbursementLimit(),
        immigrationPriorAuthorityNumber, WARN_IMM_ASYLM_DISB_ONLY);
    BigDecimal netDisbursementAmount = checkLimitAndCapIfExceeded(requestedNetDisbursementAmount,
        disbursementLimitContext, validationMessages);

    BigDecimal totalAmount = netDisbursementAmount.add(requestedNetDisbursementVatAmount);

    log.info("Build fee calculation response");
    return new FeeCalculationResponse().toBuilder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeScheme().getSchemeCode())
        .claimId(feeCalculationRequest.getClaimId())
        .validationMessages(validationMessages)
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(totalAmount))
            .disbursementAmount(toDouble(netDisbursementAmount))
            .requestedNetDisbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
            .disbursementVatAmount(feeCalculationRequest.getDisbursementVatAmount())
            .build())
        .build();
  }
}
