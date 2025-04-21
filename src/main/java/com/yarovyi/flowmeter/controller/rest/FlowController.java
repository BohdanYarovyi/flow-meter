package com.yarovyi.flowmeter.controller.rest;

import com.yarovyi.flowmeter.domain.account.Account;
import com.yarovyi.flowmeter.domain.flow.Flow;
import com.yarovyi.flowmeter.domain.flow.Step;
import com.yarovyi.flowmeter.entity.domainDto.FlowDto;
import com.yarovyi.flowmeter.entity.domainDto.StepDto;
import com.yarovyi.flowmeter.entity.exception.SubentityNotFoundException;
import com.yarovyi.flowmeter.service.AccountService;
import com.yarovyi.flowmeter.service.FlowService;
import com.yarovyi.flowmeter.service.StepService;
import com.yarovyi.flowmeter.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import static com.yarovyi.flowmeter.util.FlowMapper.*;
import static com.yarovyi.flowmeter.util.StepMapper.*;
import static com.yarovyi.flowmeter.util.StepMapper.STEP_TO_DTO;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/flows")
public class FlowController {
    private final AccountService accountService;
    private final FlowService flowService;
    private final StepService stepService;


    @GetMapping
    public ResponseEntity<List<FlowDto>> gatFlows() {
        List<Flow> flows = this.flowService.getAll();
        List<FlowDto> dtos = FLOWs_TO_DTOs.apply(flows);

        return ResponseEntity.ok(dtos);
    }


    @GetMapping("/{flowId:\\d+}")
    public ResponseEntity<FlowDto> getFlowById(@PathVariable(name = "flowId") Long flowId) {
        Flow flow = this.flowService
                .getById(flowId)
                .orElseThrow(() -> new SubentityNotFoundException(Flow.class));

        FlowDto dto = FLOW_TO_DTO.apply(flow);

        return ResponseEntity.ok(dto);
    }


    @PutMapping
    public ResponseEntity<FlowDto> editFlow(@RequestBody FlowDto flowDto,
                                            Principal principal) {
        Account account = SecurityUtil.getCurrentAccount(accountService, principal);
        this.flowService.checkOwnerShipOrElseThrow(flowDto.id(), account.getId());

        Flow flow = this.flowService.update(DTO_TO_FLOW.apply(flowDto));

        return ResponseEntity
                .ok(FLOW_TO_DTO.apply(flow));
    }


    @PostMapping("/{flowId:\\d+}/steps")
    public ResponseEntity<StepDto> createStepForFlow(@PathVariable(name = "flowId") Long flowId,
                                                  @RequestBody StepDto stepDto,
                                                  Principal principal) {
        Account account = SecurityUtil.getCurrentAccount(this.accountService, principal);
        this.flowService.checkOwnerShipOrElseThrow(flowId, account.getId());

        Flow flow = this.flowService
                .getById(flowId)
                .orElseThrow(() -> new SubentityNotFoundException(Flow.class));

        Step step = DTO_TO_STEP.apply(stepDto);
        Step savedStep = this.stepService.createStepForFlow(step, flow);

        URI location = UriComponentsBuilder
                .fromPath("/api/steps/{stepId}")
                .buildAndExpand(Map.of("stepId", savedStep.getId()))
                .toUri();

        return ResponseEntity
                .created(location)
                .body(STEP_TO_DTO.apply(savedStep));
    }


    @DeleteMapping("/{flowId:\\d+}")
    public ResponseEntity<Void> deleteFlowById(@PathVariable(value = "flowId") Long flowId,
                                               Principal principal) {
        Account account = SecurityUtil.getCurrentAccount(this.accountService, principal);
        this.flowService.checkOwnerShipOrElseThrow(flowId, account.getId());

        this.flowService.delete(flowId);

        return ResponseEntity
                .noContent()
                .build();
    }


}
