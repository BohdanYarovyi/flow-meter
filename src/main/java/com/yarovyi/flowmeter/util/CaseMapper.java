package com.yarovyi.flowmeter.util;

import com.yarovyi.flowmeter.domain.flow.Case;
import com.yarovyi.flowmeter.entity.domainDto.CaseDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CaseMapper {

    public static final Function<Case, CaseDto> CASE_TO_DTO = (aCase) -> {
        if (Objects.isNull(aCase))
            return null;

        return new CaseDto(
                aCase.getId(),
                aCase.getCreatedAt(),
                aCase.getUpdatedAt(),
                aCase.getText(),
                aCase.getPercent(),
                aCase.isCounting()
        );
    };

    public static final Function<List<Case>, List<CaseDto>> CASEs_TO_DTOs = (cases) -> {
        if (Objects.isNull(cases))
            return new ArrayList<>();

        return cases.stream()
                .map(CASE_TO_DTO)
                .toList();
    };

    public static final Function<CaseDto, Case> DTO_TO_CASE = (dto) -> {
        var c = new Case();

        if (Objects.isNull(dto))
            return c;

        c.setId(dto.id());
        c.setCreatedAt(dto.createdAt());
        c.setUpdatedAt(dto.updatedAt());
        c.setText(dto.text());
        c.setPercent(dto.percent());
        c.setCounting(dto.counting());

        return c;
    };

    public static final Function<List<CaseDto>, List<Case>> DTOs_TO_CASEs = (dtos) -> {
        if (Objects.isNull(dtos))
            return new ArrayList<>();

        return dtos.stream()
                .map(DTO_TO_CASE)
                .toList();
    };

    public static final BiFunction<Case,Case,Case> COMMIT_CASE_UPDATE = (existCase, editedCase) -> {
        if (Objects.isNull(existCase) || Objects.isNull(editedCase)) {
            throw new NullPointerException("Exist case or edited case is null");
        }

        if (!Objects.equals(existCase.getText(), editedCase.getText())) {
            existCase.setText(editedCase.getText());
        }

        if (!Objects.equals(existCase.getPercent(), editedCase.getPercent())) {
            existCase.setPercent(editedCase.getPercent());
        }

        if (!Objects.equals(existCase.isCounting(), editedCase.isCounting())) {
            existCase.setCounting(editedCase.isCounting());
        }

        return existCase;
    };
}
