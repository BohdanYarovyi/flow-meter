package com.yarovyi.flowmeter.mapper;

import com.yarovyi.flowmeter.entity.flow.Step;
import com.yarovyi.flowmeter.dto.flow.StepDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static com.yarovyi.flowmeter.mapper.CaseMapper.CASEs_TO_DTOs;
import static com.yarovyi.flowmeter.mapper.CaseMapper.DTOs_TO_CASEs;

public class StepMapper {

    public static final Function<Step, StepDto> STEP_TO_DTO = (step) -> {
        if (Objects.isNull(step))
            return null;

        return new StepDto(
                step.getId(),
                step.getCreatedAt(),
                step.getUpdatedAt(),
                step.getDay(),
                CASEs_TO_DTOs.apply(step.getCases())
        );
    };

    public static final Function<List<Step>, List<StepDto>> STEPs_TO_DTOs = (steps) -> {
        if (Objects.isNull(steps))
            return new ArrayList<>();

        return steps.stream()
                .map(STEP_TO_DTO)
                .toList();
    };

    public static final Function<StepDto, Step> DTO_TO_STEP = (dto) -> {
        var step = new Step();

        if (Objects.isNull(dto))
           return step;

        step.setId(dto.id());
        step.setCreatedAt(dto.createdAt());
        step.setUpdatedAt(dto.updatedAt());
        step.setDay(dto.day());

        if (!Objects.isNull(dto.cases())) {
            var cases = DTOs_TO_CASEs.apply(dto.cases());
            step.setCases(cases);
        }

        return step;
    };

    public static final Function<List<StepDto>, List<Step>> DTOs_TO_STEPs = (dtos) -> {
        if (Objects.isNull(dtos))
            return new ArrayList<>();

        return dtos.stream()
                .map(DTO_TO_STEP)
                .toList();
    };

}
