package com.yarovyi.flowmeter.util;

import com.yarovyi.flowmeter.domain.flow.Flow;
import com.yarovyi.flowmeter.entity.domainDto.FlowDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.yarovyi.flowmeter.util.StepMapper.*;

public class FlowMapper {

    public static final Function<Flow, FlowDto> FLOW_TO_DTO = (flow) -> {
        if (Objects.isNull(flow))
            return null;

        return new FlowDto(
                flow.getId(),
                flow.getCreatedAt(),
                flow.getUpdatedAt(),
                flow.getTitle(),
                flow.getDescription(),
                flow.getTargetPercentage(),
                STEPs_TO_DTOs.apply(flow.getSteps())
        );
    };

    public static final Function<List<Flow>, List<FlowDto>> FLOWs_TO_DTOs = (flows) -> {
        if (Objects.isNull(flows))
            return new ArrayList<>();

        return flows.stream()
                .map(FLOW_TO_DTO)
                .toList();
    };

    public static final Function<FlowDto, Flow> DTO_TO_FLOW = (dto) -> {
        var flow = new Flow();

        if (Objects.isNull(dto))
            return flow;

        flow.setId(dto.id());
        flow.setCreatedAt(dto.createdAt());
        flow.setUpdatedAt(dto.updatedAt());
        flow.setTitle(dto.title());
        flow.setDescription(dto.description());
        flow.setTargetPercentage(dto.targetPercentage());

        if (!Objects.isNull(dto.steps())) {
            var steps = DTOs_TO_STEPs.apply(dto.steps());
            flow.setSteps(steps);
        }

        return flow;
    };

    public static final Function<List<FlowDto>, List<Flow>> DTOs_TO_FLOWs = (dtos) -> {
        if (Objects.isNull(dtos))
            return new ArrayList<>();

        return dtos.stream()
                .map(DTO_TO_FLOW)
                .toList();
    };

    public static final BiFunction<Flow, Flow, Flow> COMMIT_FLOW_UPDATE = (exist, edited) -> {
        if (Objects.isNull(exist) || Objects.isNull(edited)) {
            throw new NullPointerException("Edited flow or exist flow is null");
        }

        if (!Objects.equals(exist.getTitle(), edited.getTitle())) {
            exist.setTitle(edited.getTitle());
        }

        if (!Objects.equals(exist.getDescription(), edited.getDescription())) {
            exist.setDescription(edited.getDescription());
        }

        if (!Objects.equals(exist.getTargetPercentage(), edited.getTargetPercentage())) {
            exist.setTargetPercentage(edited.getTargetPercentage());
        }

        return exist;
    };

}
