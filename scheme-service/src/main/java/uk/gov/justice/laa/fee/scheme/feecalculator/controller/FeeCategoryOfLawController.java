package uk.gov.justice.laa.fee.scheme.feecalculator.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.fee.scheme.feecalculator.model.CategoryOfLawResponseDto;

import uk.gov.justice.laa.fee.scheme.feecalculator.service.FeeService;

@RestController
@AllArgsConstructor
@RequestMapping("/api/fee")
public class FeeCategoryOfLawController {

    private final FeeService feeService;

    @GetMapping("/{feeCode}")
    public ResponseEntity<CategoryOfLawResponseDto> getCategoryLawCode(@PathVariable String feeCode) {

        CategoryOfLawResponseDto categoryOfLawDto = feeService.getCategoryCode(feeCode);

        return ResponseEntity.ok(categoryOfLawDto);
    }
}
