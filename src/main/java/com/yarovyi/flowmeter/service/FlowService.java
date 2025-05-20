package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.entity.account.Account;
import com.yarovyi.flowmeter.entity.flow.Flow;

import java.util.List;
import java.util.Optional;

public interface FlowService {
    List<Flow> getAll();
    List<Flow> getAllByAccountId(Long accountId);
    Optional<Flow> getById(Long id);
    Flow create(Flow flow);
    Flow update(Flow flow);
    void delete(Long id);

    Flow createFlowForAccount(Flow flow, Account account);

    boolean checkOwnership(Long flowId, Long accountId);
    void checkOwnerShipOrElseThrow(Long flowId, Long accountId);
}
