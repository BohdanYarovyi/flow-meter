package com.yarovyi.flowmeter.controller.rest;

import com.yarovyi.flowmeter.domain.flow.Flow;
import com.yarovyi.flowmeter.entity.dto.FlowDto;
import com.yarovyi.flowmeter.entity.exception.SubentityNotFoundException;
import com.yarovyi.flowmeter.service.FlowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.yarovyi.flowmeter.util.FlowMapper.FLOW_TO_DTO;
import static com.yarovyi.flowmeter.util.FlowMapper.FLOWs_TO_DTOs;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/flows")
public class FlowController {
    private final FlowService flowService;


    @GetMapping
    public ResponseEntity<List<FlowDto>> gatFlows() {
        List<Flow> flows = this.flowService.getAll();
        List<FlowDto> dtos = FLOWs_TO_DTOs.apply(flows);

        return ResponseEntity.ok(dtos);
    }


    @GetMapping("/{flowId:\\d+}")
    public ResponseEntity<FlowDto> getFlowById(@PathVariable(name = "flowId") Long flowId) {
        Flow flow = this.flowService.getById(flowId).orElseThrow(
                () -> new SubentityNotFoundException("Flow with id:%s not found".formatted(flowId), Flow.class)
        );

        FlowDto dto = FLOW_TO_DTO.apply(flow);

        return ResponseEntity.ok(dto);
    }


}
