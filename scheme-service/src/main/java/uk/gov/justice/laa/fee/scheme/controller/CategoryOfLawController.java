package uk.gov.justice.laa.fee.scheme.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.fee.scheme.api.CategoryOfLawApi;
import uk.gov.justice.laa.fee.scheme.model.CategoryOfLaw;
import uk.gov.justice.laa.fee.scheme.model.CategoryOfLawResponseDto;

import uk.gov.justice.laa.fee.scheme.service.FeeService;

@RestController
@AllArgsConstructor
public class CategoryOfLawController implements CategoryOfLawApi {

    private final FeeService feeService;

    @Override
    public ResponseEntity<CategoryOfLaw> getCategoryOfLaw(@PathVariable String feeCode) {
          CategoryOfLaw categoryOfLawDto = feeService.getCategoryCode(feeCode);

       return ResponseEntity.ok(categoryOfLawDto);
    }

    //    @GetMapping("/{feeCode}")
//    public ResponseEntity<CategoryOfLawResponseDto> getCategoryLawCode(@PathVariable String feeCode) {
//
//        CategoryOfLawResponseDto categoryOfLawDto = feeService.getCategoryCode(feeCode);
//
//        return ResponseEntity.ok(categoryOfLawDto);
//    }
}
