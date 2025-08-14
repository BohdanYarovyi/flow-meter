package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.entity.account.Account;
import com.yarovyi.flowmeter.entity.flow.Flow;
import com.yarovyi.flowmeter.exception.EntityBadRequestException;
import com.yarovyi.flowmeter.exception.SubentityNotFoundException;
import com.yarovyi.flowmeter.repository.FlowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.yarovyi.flowmeter.mapper.FlowMapper.COMMIT_FLOW_UPDATE;

@RequiredArgsConstructor
@Service
public class FlowServiceImpl implements FlowService {
    private final FlowRepository flowRepository;


    @Transactional(readOnly = true)
    @Override
    public List<Flow> getAll() {
        return flowRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Flow> getAllByAccountId(Long accountId) {
        if (accountId == null) {
            throw new IllegalArgumentException("Parameter 'accountId' is null");
        }

        List<Flow> flows = flowRepository.findFlowsByAccount_Id(accountId);
        flows = flowRepository.fetchStepsToFlows(flows);
        flows = flowRepository.fetchCasesToFlowsWithSteps(flows);

        return flows;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Flow> getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Parameter 'id' is null");
        }

        return flowRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean checkOwnership(Long flowId, Long accountId) {
        if (flowId == null || accountId == null) {
            throw new IllegalArgumentException("Parameters 'flowId' or 'accountId' is null");
        }

        return flowRepository.existsByIdAndAccount_Id(flowId, accountId);
    }

    @Transactional(readOnly = true)
    @Override
    public void checkOwnerShipOrElseThrow(Long flowId, Long accountId) {
        if (!checkOwnership(flowId, accountId))
            throw new SubentityNotFoundException("Account has not such flow", Flow.class);
    }

    @Transactional
    @Override
    public Flow update(Flow updated) {
        if (updated == null) {
            throw new IllegalArgumentException("Parameter 'updated' is null");
        }

        if (updated.getId() == null) {
            throw new EntityBadRequestException("Flow.id is null");
        }

        Flow existsFlow = flowRepository
                .findById(updated.getId())
                .orElseThrow(() -> new SubentityNotFoundException(Flow.class));
        existsFlow = COMMIT_FLOW_UPDATE.apply(existsFlow, updated);

        return flowRepository.save(existsFlow);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Parameter 'id' is null");
        }

        Flow flow = flowRepository
                .findById(id)
                .orElseThrow(() -> new SubentityNotFoundException(Flow.class));

        // soft-delete
        flow.delete();
        flowRepository.save(flow);
    }

    @Transactional
    @Override
    public Flow createFlowForAccount(Flow flow, Account account) {
        if (flow == null || account == null) {
            throw new IllegalArgumentException("Parameters 'flow' or 'account' are null");
        }

        account.getFlows().add(flow);
        flow.setAccount(account);

        return flowRepository.save(flow);
    }

}
