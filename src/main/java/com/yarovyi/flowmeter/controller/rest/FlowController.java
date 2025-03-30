package com.yarovyi.flowmeter.controller.rest;

import com.yarovyi.flowmeter.domain.account.Account;
import com.yarovyi.flowmeter.domain.flow.Flow;
import com.yarovyi.flowmeter.domain.flow.Step;
import com.yarovyi.flowmeter.entity.dto.FlowDto;
import com.yarovyi.flowmeter.entity.dto.StepDto;
import com.yarovyi.flowmeter.entity.exception.AccountAuthenticationException;
import com.yarovyi.flowmeter.entity.exception.ForbiddenRequestException;
import com.yarovyi.flowmeter.entity.exception.SubentityNotFoundException;
import com.yarovyi.flowmeter.service.AccountService;
import com.yarovyi.flowmeter.service.FlowService;
import com.yarovyi.flowmeter.service.StepService;
import com.yarovyi.flowmeter.util.StepMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.yarovyi.flowmeter.util.FlowMapper.FLOW_TO_DTO;
import static com.yarovyi.flowmeter.util.FlowMapper.FLOWs_TO_DTOs;
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
        Flow flow = this.flowService.getById(flowId).orElseThrow(
                () -> new SubentityNotFoundException("Flow with id:%s not found".formatted(flowId), Flow.class)
        );

        FlowDto dto = FLOW_TO_DTO.apply(flow);

        return ResponseEntity.ok(dto);
    }


    @PostMapping("/{flowId:\\d+}/steps")
    public ResponseEntity<StepDto> createStepForFlow(@PathVariable(name = "flowId") Long flowId,
                                                  @RequestBody StepDto stepDto,
                                                  Principal principal) {
        Flow flow = this.flowService.getById(flowId).orElseThrow(() -> new SubentityNotFoundException(Flow.class));
        Account currentAccount = this.accountService.getAccountByLogin(principal.getName())
                .orElseThrow(() -> new AccountAuthenticationException("Account is not logged in"));

        if (!Objects.equals(currentAccount.getId(), flow.getAccount().getId()))
            throw new ForbiddenRequestException("Creating step forbidden", "Account cannot create step for other flows");

        Step step = StepMapper.DTO_TO_STEP.apply(stepDto);
        Step savedStep = this.stepService.createStepForFlow(step, flow);

        URI location = UriComponentsBuilder
                .fromPath("/api/steps/{stepId}")
                .buildAndExpand(Map.of("stepId", savedStep.getId()))
                .toUri();

        return ResponseEntity.created(location).body(STEP_TO_DTO.apply(savedStep));
    }


}
