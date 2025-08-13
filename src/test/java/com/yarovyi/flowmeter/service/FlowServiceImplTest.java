package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.entity.account.Account;
import com.yarovyi.flowmeter.entity.flow.Flow;
import com.yarovyi.flowmeter.exception.EntityBadRequestException;
import com.yarovyi.flowmeter.exception.SubentityNotFoundException;
import com.yarovyi.flowmeter.repository.FlowRepository;
import com.yarovyi.flowmeter.testUtility.entityGenerator.AccountGenerator;
import com.yarovyi.flowmeter.testUtility.entityGenerator.FlowGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class FlowServiceImplTest {
    @Mock
    private FlowRepository flowRepository;
    @InjectMocks
    private FlowServiceImpl flowService;


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    getAll()
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void getAll_whenFlowsExist_thenReturnAll() {
        // given
        List<Flow> allFlows = FlowGenerator.flows();

        // mockito
        Mockito.when(flowRepository.findAll())
                .thenReturn(allFlows);

        // when
        List<Flow> result = flowService.getAll();

        // then
        assertNotNull(result);
        assertIterableEquals(allFlows, result);
    }

    @Test
    void getAll_whenNoOneFlow_thenReturnEmptyList() {
        // given
        List<Flow> allFlows = List.of();

        // mockito
        Mockito.when(flowRepository.findAll())
                .thenReturn(allFlows);

        // when
        List<Flow> result = flowService.getAll();

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    getAllByAccountId(Long accountId)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void getAllByAccountId_whenAccountHasFlows_thenReturnListOfFlow() {
        // given
        Account account = AccountGenerator.oneAccount();
        account.getFlows().forEach(f -> f.setAccount(account));

        // mockito
        Mockito.when(flowRepository.findAllByAccountIdWithEagerFetch(account.getId()))
                .thenReturn(account.getFlows());

        // when
        List<Flow> result = flowService.getAllByAccountId(account.getId());

        // then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        for (Flow flow : result) {
            assertEquals(account.getId(), flow.getAccount().getId());
        }
    }

    @Test
    void getAllByAccountId_whenAccountDoesntHaveAnyFlow_thenReturnEmptyList() {
        // given
        Long accountId = 10L;
        List<Flow> flows = List.of();

        // mockito
        Mockito.when(flowRepository.findAllByAccountIdWithEagerFetch(accountId))
                .thenReturn(flows);

        // when
        List<Flow> result = flowService.getAllByAccountId(accountId);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllByAccountId_whenParameterAccountIdIsNull_thenThrowException() {
        // given
        Long accountId = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> flowService.getAllByAccountId(accountId));
    }


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    getById(Long id)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void getById_whenFlowExists_thenReturnFlow() {
        // given
        Flow flow = FlowGenerator.oneFlow();

        // mockito
        Mockito.when(flowRepository.findById(flow.getId()))
                .thenReturn(Optional.of(flow));

        // when
        Optional<Flow> result = flowService.getById(flow.getId());

        // then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(flow, result.get());
    }

    @Test
    void getById_whenFlowDoesntExist_thenReturnEmptyOptional() {
        // given
        Long id = 10L;

        // mockito
        Mockito.when(flowRepository.findById(id))
                .thenReturn(Optional.empty());

        // when
        Optional<Flow> result = flowService.getById(id);

        // then
        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    @Test
    void getById_whenParameterIdIsNull_thenThrowException() {
        // given
        Long id = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> flowService.getById(id));
    }


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    checkOwnership(Long flowId, Long accountId)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void checkOwnership_whenAccountIsFlowOwner_thenReturnTrue() {
        // given
        Long flowId = 10L;
        Long accountId = 5L;

        // mockito
        Mockito.when(flowRepository.existsByIdAndAccount_Id(flowId, accountId))
                .thenReturn(true);

        // when
        boolean result = flowService.checkOwnership(flowId, accountId);

        // then
        assertTrue(result);
    }

    @Test
    void checkOwnership_whenAccountIsNotFlowOwner_thenReturnFalse() {
        // given
        Long flowId = 10L;
        Long accountId = 5L;

        // mockito
        Mockito.when(flowRepository.existsByIdAndAccount_Id(flowId, accountId))
                .thenReturn(false);

        // when
        boolean result = flowService.checkOwnership(flowId, accountId);

        // then
        assertFalse(result);
    }

    @Test
    void checkOwnership_whenParameterFlowIdIsNull_thenThrowException() {
        // given
        Long flowId = null;
        Long accountId = 5L;

        // when
        assertThrows(IllegalArgumentException.class, () -> flowService.checkOwnership(flowId, accountId));
    }

    @Test
    void checkOwnership_whenParameterAccountIdIsNull_thenThrowException() {
        // given
        Long flowId = 10L;
        Long accountId = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> flowService.checkOwnership(flowId, accountId));
    }


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    checkOwnerShipOrElseThrow(Long flowId, Long accountId)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void checkOwnerShipOrElseThrow_whenAccountIsFlowOwner_thenDoNothing() {
        // given
        Long flowId = 10L;
        Long accountId = 5L;

        // mockito
        Mockito.when(flowRepository.existsByIdAndAccount_Id(flowId, accountId))
                .thenReturn(true);

        // when
        assertDoesNotThrow(() -> flowService.checkOwnerShipOrElseThrow(flowId, accountId));
    }

    @Test
    void checkOwnerShipOrElseThrow_whenAccountIsNotFlowOwner_thenThrowException() {
        // given
        Long flowId = 10L;
        Long accountId = 5L;

        // mockito
        Mockito.when(flowRepository.existsByIdAndAccount_Id(flowId, accountId))
                .thenReturn(false);

        // when
        assertThrows(SubentityNotFoundException.class, () -> flowService.checkOwnerShipOrElseThrow(flowId, accountId));
    }

    @Test
    void checkOwnerShipOrElseThrow_whenParameterFlowIdIsNull_thenThrowException() {
        // given
        Long flowId = null;
        Long accountId = 5L;

        // when
        assertThrows(IllegalArgumentException.class, () -> flowService.checkOwnerShipOrElseThrow(flowId, accountId));
    }

    @Test
    void checkOwnerShipOrElseThrow_whenParameterAccountIdIsNull_thenThrowException() {
        // given
        Long flowId = 10L;
        Long accountId = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> flowService.checkOwnerShipOrElseThrow(flowId, accountId));
    }


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    update(Flow updated)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void update_whenIsOk_thenUpdateAndReturnFlow() {
        // given
        Long flowId = 10L;
        Flow existed = FlowGenerator.oneFlow();
        Flow updated = FlowGenerator.oneFlow();
        existed.setId(flowId);
        updated.setId(flowId);

        // mockito
        Mockito.when(flowRepository.findById(flowId))
                .thenReturn(Optional.of(existed));
        Mockito.when(flowRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Flow result = flowService.update(updated);

        // captor
        ArgumentCaptor<Flow> captor = ArgumentCaptor.forClass(Flow.class);
        Mockito.verify(flowRepository).save(captor.capture());
        Flow beforeSave = captor.getValue();

        // then
        assertNotNull(result);
        assertEquals(updated, result);
        assertEquals(flowId, result.getId());
        assertEquals(beforeSave, result);
    }

    @Test
    void update_whenFlowForUpdateNotExists_thenThrowException() {
        // given
        Long flowId = 10L;
        Flow updated = FlowGenerator.oneFlow();
        updated.setId(flowId);

        // mockito
        Mockito.when(flowRepository.findById(flowId))
                .thenReturn(Optional.empty());

        // when
        assertThrows(SubentityNotFoundException.class, () -> flowService.update(updated));
    }

    @Test
    void update_whenParameterUpdatedIsNull_thenThrowException() {
        // given
        Flow updated = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> flowService.update(updated));
    }

    @Test
    void update_whenFlowIdIsNull_thenThrowException() {
        // given
        Long id = null;
        Flow updated = FlowGenerator.oneFlow();
        updated.setId(id);

        // when
        assertThrows(EntityBadRequestException.class, () -> flowService.update(updated));
    }


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    delete(Long id)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void delete_whenFlowExists_thenDeleteFlow() {
        // given
        Flow forDelete = FlowGenerator.oneFlow();
        forDelete.setDeleted(false);

        // mockito
        Mockito.when(flowRepository.findById(forDelete.getId()))
                .thenReturn(Optional.of(forDelete));

        // when
        flowService.delete(forDelete.getId());

        // captor
        ArgumentCaptor<Flow> captor = ArgumentCaptor.forClass(Flow.class);
        Mockito.verify(flowRepository).save(captor.capture());
        Flow beforeSave = captor.getValue();

        // then
        Mockito.verify(flowRepository, atLeastOnce()).save(any());
        assertTrue(beforeSave.isDeleted());
    }

    @Test
    void delete_whenFlowDoesntExist_thenThrowException() {
        // given
        Long flowId = 10L;

        // mockito
        Mockito.when(flowRepository.findById(flowId))
                .thenReturn(Optional.empty());

        // when
        assertThrows(SubentityNotFoundException.class, () -> flowService.delete(flowId));
    }

    @Test
    void delete_whenParameterIdIsNull_thenThrowException() {
        // given
        Long flowId = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> flowService.delete(flowId));
    }


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    createFlowForAccount(Flow flow, Account account)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void createFlowForAccount_whenItIsOk_thenReturnCreatedFlow() {
        // given
        Flow flow = FlowGenerator.oneFlow();
        Account account = AccountGenerator.oneAccount();

        // mockito
        Mockito.when(flowRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Flow result = flowService.createFlowForAccount(flow, account);

        // captor
        ArgumentCaptor<Flow> captor = ArgumentCaptor.forClass(Flow.class);
        Mockito.verify(flowRepository).save(captor.capture());
        Flow beforeSave = captor.getValue();

        // then
        assertNotNull(result);
        assertEquals(flow.getId(), result.getId());
        assertEquals(result.getId(), beforeSave.getId());
        assertNotNull(result.getAccount());
        assertEquals(account.getId(), result.getAccount().getId());
    }

    @Test
    void createFlowForAccount_whenParameterFlowIsNull_thenThrowException() {
        // given
        Flow flow = null;
        Account account = new Account();

        // when
        assertThrows(IllegalArgumentException.class, () -> flowService.createFlowForAccount(flow, account));
    }

    @Test
    void createFlowForAccount_whenParameterAccountIsNull_thenThrowException() {
        // given
        Flow flow = new Flow();
        Account account = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> flowService.createFlowForAccount(flow, account));
    }

}