package com.yarovyi.flowmeter.controller.rest;

import com.yarovyi.flowmeter.entity.account.Account;
import com.yarovyi.flowmeter.entity.flow.Flow;
import com.yarovyi.flowmeter.entity.flow.Step;
import com.yarovyi.flowmeter.dto.flow.FlowDto;
import com.yarovyi.flowmeter.dto.flow.StepDto;
import com.yarovyi.flowmeter.exception.SubentityNotFoundException;
import com.yarovyi.flowmeter.service.AccountService;
import com.yarovyi.flowmeter.service.FlowService;
import com.yarovyi.flowmeter.service.StepService;
import com.yarovyi.flowmeter.util.SecurityUtil;
import com.yarovyi.flowmeter.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import static com.yarovyi.flowmeter.mapper.FlowMapper.*;
import static com.yarovyi.flowmeter.mapper.StepMapper.*;
import static com.yarovyi.flowmeter.mapper.StepMapper.STEP_TO_DTO;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/flows")
public class FlowController {
    private final AccountService accountService;
    private final FlowService flowService;
    private final StepService stepService;


    @GetMapping
    public ResponseEntity<List<FlowDto>> getFlows() {
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
    public ResponseEntity<FlowDto> editFlow(@RequestBody @Validated FlowDto flowDto,
                                            BindingResult bindingResult,
                                            Principal principal) {
        ValidationUtil.checkOrThrow(bindingResult);

        Account account = SecurityUtil.getCurrentAccount(accountService, principal);
        this.flowService.checkOwnerShipOrElseThrow(flowDto.id(), account.getId());

        Flow flow = this.flowService.update(DTO_TO_FLOW.apply(flowDto));

        return ResponseEntity
                .ok(FLOW_TO_DTO.apply(flow));
    }


    @PostMapping("/{flowId:\\d+}/steps")
    public ResponseEntity<StepDto> createStepForFlow(@PathVariable(name = "flowId") Long flowId,
                                                  @RequestBody @Validated StepDto stepDto,
                                                  BindingResult bindingResult,
                                                  Principal principal) {
        ValidationUtil.checkOrThrow(bindingResult);

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
