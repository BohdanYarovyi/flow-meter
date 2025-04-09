package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.domain.account.Account;
import com.yarovyi.flowmeter.domain.flow.Flow;
import com.yarovyi.flowmeter.domain.flow.Step;
import com.yarovyi.flowmeter.entity.exception.EntityBadRequestException;
import com.yarovyi.flowmeter.entity.exception.SubentityNotFoundException;
import com.yarovyi.flowmeter.repository.FlowRepository;
import com.yarovyi.flowmeter.util.FlowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.yarovyi.flowmeter.util.FlowMapper.COMMIT_FLOW_UPDATE;

@RequiredArgsConstructor
@Service
public class FlowServiceImpl implements FlowService {
    private final FlowRepository flowRepository;


    @Override
    public List<Flow> getAll() {
        return this.flowRepository.findAll();
    }


    @Override
    public List<Flow> getAllByAccountId(Long accountId) {
        if (Objects.isNull(accountId))
            throw new NullPointerException("accountId is required");

        var flowsByAccountId = this.flowRepository.findAllByAccount_Id(accountId);
        flowsByAccountId.forEach(flow -> flow.getSteps().sort(Comparator.comparing(Step::getDay)));

        return flowsByAccountId;
    }


    @Override
    public Optional<Flow> getById(Long id) {
        if (Objects.isNull(id))
            throw new NullPointerException("id is required");

        return this.flowRepository.findById(id);
    }


    @Override
    public boolean checkOwnership(Long flowId, Long accountId) {
        return this.flowRepository.existsByIdAndAccount_Id(flowId, accountId);
    }


    @Override
    public void checkOwnerShipOrElseThrow(Long flowId, Long accountId) {
        if (!checkOwnership(flowId, accountId))
            throw new SubentityNotFoundException("Account has not such flow", Flow.class);
    }


    @Override
    public Flow create(Flow flow) {
        throw new UnsupportedOperationException();
    }


    @Override
    public Flow update(Flow flow) {
        if (Objects.isNull(flow))
            throw new NullPointerException("flow is null");

        if (Objects.isNull(flow.getId()))
            throw new EntityBadRequestException("To edit flow flowID is required, but it is null");

        Flow existsFlow = this.flowRepository
                .findById(flow.getId())
                .orElseThrow(() -> new SubentityNotFoundException(Flow.class));
        existsFlow = COMMIT_FLOW_UPDATE.apply(existsFlow, flow);

        return this.flowRepository.save(existsFlow);
    }


    @Override
    public void delete(Long id) {
        if (Objects.isNull(id))
            throw new NullPointerException("Flow id is null");

        Flow flow = this.flowRepository
                .findById(id)
                .orElseThrow(() -> new SubentityNotFoundException(Flow.class));

        // soft-delete
        flow.delete();
        this.flowRepository.save(flow);
    }


    @Override
    public Flow createFlowForAccount(Flow flow, Account account) {
        if (Objects.isNull(flow) || Objects.isNull(account))
            throw new NullPointerException("flow or account is null");

        account.getFlows().add(flow);
        flow.setAccount(account);

        return this.flowRepository.save(flow);
    }


}
