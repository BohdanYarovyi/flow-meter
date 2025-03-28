package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.domain.account.Account;
import com.yarovyi.flowmeter.domain.flow.Flow;
import com.yarovyi.flowmeter.domain.flow.Step;
import com.yarovyi.flowmeter.repository.FlowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    public void create(Flow flow) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(Flow flow) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long createFlowForAccount(Flow flow, Account account) {
        if (Objects.isNull(flow) || Objects.isNull(account))
            throw new NullPointerException("flow or account is null");

        account.getFlows().add(flow);
        flow.setAccount(account);

        return this.flowRepository.save(flow).getId();
    }
}
