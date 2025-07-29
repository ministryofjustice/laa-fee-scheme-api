package uk.gov.justice.laa.fee.scheme.feecalculator.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.fee.scheme.feecalculator.model.FeeCalculationRequestDto;
import uk.gov.justice.laa.fee.scheme.feecalculator.model.FeeCalculationResponseDto;
import uk.gov.justice.laa.fee.scheme.feecalculator.service.FeeService;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class FeeCalculationController {

    private final FeeService feeService;

    @PostMapping("/fee-calculation")
    public ResponseEntity<FeeCalculationResponseDto> getFeeCalculation(@RequestBody FeeCalculationRequestDto request) {

        FeeCalculationResponseDto feeCalculationResponseDto = feeService.getFeeCalculation(request);

        return ResponseEntity.ok(feeCalculationResponseDto);
    }
}