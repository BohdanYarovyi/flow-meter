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
import java.util.Objects;
import java.util.Optional;

import static com.yarovyi.flowmeter.mapper.FlowMapper.COMMIT_FLOW_UPDATE;

@RequiredArgsConstructor
@Service
public class FlowServiceImpl implements FlowService {
    private final FlowRepository flowRepository;


    @Transactional(readOnly = true)
    @Override
    public List<Flow> getAll() {
        return this.flowRepository.findAll();
    }


    @Transactional(readOnly = true)
    @Override
    public List<Flow> getAllByAccountId(Long accountId) {
        if (Objects.isNull(accountId))
            throw new NullPointerException("accountId is required");

        return this.flowRepository.findAllByAccountIdWithEagerFetch(accountId);
    }


    @Transactional(readOnly = true)
    @Override
    public Optional<Flow> getById(Long id) {
        if (Objects.isNull(id))
            throw new NullPointerException("id is required");

        return this.flowRepository.findById(id);
    }


    @Transactional(readOnly = true)
    @Override
    public boolean checkOwnership(Long flowId, Long accountId) {
        return this.flowRepository.existsByIdAndAccount_Id(flowId, accountId);
    }


    @Transactional(readOnly = true)
    @Override
    public void checkOwnerShipOrElseThrow(Long flowId, Long accountId) {
        if (!checkOwnership(flowId, accountId))
            throw new SubentityNotFoundException("Account has not such flow", Flow.class);
    }


    @Transactional
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


    @Transactional
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


    @Transactional
    @Override
    public Flow createFlowForAccount(Flow flow, Account account) {
        if (Objects.isNull(flow) || Objects.isNull(account))
            throw new NullPointerException("flow or account is null");

        account.getFlows().add(flow);
        flow.setAccount(account);

        return this.flowRepository.save(flow);
    }

}
