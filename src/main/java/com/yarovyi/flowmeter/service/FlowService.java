package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.domain.account.Account;
import com.yarovyi.flowmeter.domain.flow.Flow;

import java.util.List;
import java.util.Optional;

public interface FlowService {
    List<Flow> getAll();
    List<Flow> getAllByAccountId(Long accountId);
    Optional<Flow> getById(Long id);
    void create(Flow flow);
    void update(Flow flow);
    void delete(Long id);

    Flow createFlowForAccount(Flow flow, Account account);
}
