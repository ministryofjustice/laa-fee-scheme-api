package uk.gov.justice.laa.fee.scheme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.entity.FeeCategoryMappingEntity;
import uk.gov.justice.laa.fee.scheme.enums.AreaOfLawType;
import uk.gov.justice.laa.fee.scheme.exception.AreaOfLawNotFoundException;
import uk.gov.justice.laa.fee.scheme.model.FeeCodeDetailsV1;
import uk.gov.justice.laa.fee.scheme.model.FeeCodesResponseV1;
import uk.gov.justice.laa.fee.scheme.repository.FeeCategoryMappingRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for retrieving fee codes based on area of law.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FeeCodesService {

    private static final Set<String> ASSOC_CIVIL_FEE_CODES = Set.of("ASMS", "ASPL", "ASAS");
    private static final List<String> ASSOC_CIVIL_CATEGORY_CODES = List.of("APPEALS", "INVEST", "PRISON");

    private final FeeCategoryMappingRepository feeCategoryMappingRepository;

    /**
     * Get fee codes based on area of law.
     *
     * @param areaOfLaw the area of law
     * @return fee codes response
     */
    public FeeCodesResponseV1 getFeeCodesV1(String areaOfLaw) {

        log.info("Get fee codes for area of law: {}", areaOfLaw);

        List<FeeCategoryMappingEntity> mappings =
                feeCategoryMappingRepository
                        .findByCategoryOfLawTypeAreaOfLawTypeCode(parseAreaOfLaw(areaOfLaw));

        List<FeeCodeDetailsV1> feeCodes = buildFeeCodesV1(mappings);

        return FeeCodesResponseV1.builder()
                .feeCodes(feeCodes)
                .build();
    }

    /**
     * Safe parse of area of law to emum.
     */
    private AreaOfLawType parseAreaOfLaw(String areaOfLaw) {

        if (areaOfLaw == null || areaOfLaw.isBlank()) {
            throw new AreaOfLawNotFoundException(areaOfLaw);
        }

        return Arrays.stream(AreaOfLawType.values())
                .filter(e -> e.name().equalsIgnoreCase(areaOfLaw.trim()))
                .findFirst()
                .orElseThrow(() ->
                        new AreaOfLawNotFoundException(areaOfLaw)
                );
    }

    /**
     * Groups mappings by fee code and maps to fee codes list.
     */
    private List<FeeCodeDetailsV1> buildFeeCodesV1(List<FeeCategoryMappingEntity> mappings) {

        Map<String, List<FeeCategoryMappingEntity>> grouped =
                mappings.stream()
                        .collect(Collectors.groupingBy(m -> m.getFeeCode().getFeeCode()));

        return grouped.values().stream()
                .map(this::mapToFeeCodeDetails)
                .toList();
    }

    /**
     * Map grouped entity list to fee code details model.
     */
    private FeeCodeDetailsV1 mapToFeeCodeDetails(List<FeeCategoryMappingEntity> mappings) {
        FeeCategoryMappingEntity feeCategoryMappingEntity = mappings.getFirst();

        String feeCode = feeCategoryMappingEntity.getFeeCode().getFeeCode();

        List<String> categoryOfLawCodes = ASSOC_CIVIL_FEE_CODES.contains(feeCode) ? ASSOC_CIVIL_CATEGORY_CODES
                : Collections.singletonList(feeCategoryMappingEntity.getCategoryOfLawType().getCode());

        return FeeCodeDetailsV1.builder()
                .feeCode(feeCode)
                .areaOfLaw(
                        feeCategoryMappingEntity.getCategoryOfLawType()
                                .getAreaOfLawType()
                                .getDescription()
                )
                .categoryOfLawCodes(categoryOfLawCodes)
                .feeCodeDescription(feeCategoryMappingEntity.getFeeCode().getFeeDescription())
                .feeType(feeCategoryMappingEntity.getFeeCode().getFeeType().name())
                .build();
    }
}
