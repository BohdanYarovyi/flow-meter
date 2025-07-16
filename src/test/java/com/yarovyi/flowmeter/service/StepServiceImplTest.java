package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.entity.flow.Flow;
import com.yarovyi.flowmeter.entity.flow.Step;
import com.yarovyi.flowmeter.exception.ForbiddenRequestException;
import com.yarovyi.flowmeter.exception.SubentityNotFoundException;
import com.yarovyi.flowmeter.repository.StepRepository;
import com.yarovyi.flowmeter.testUtility.entityGenerator.FlowGenerator;
import com.yarovyi.flowmeter.testUtility.entityGenerator.StepGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;

@ExtendWith(MockitoExtension.class)
class StepServiceImplTest {
    @Mock
    private StepRepository stepRepository;
    @InjectMocks
    private StepServiceImpl stepService;


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    getStepById(Long stepId)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void getStepById_whenStepExists_thenReturnOptional() {
        // given
        Long stepId = 10L;
        Step step = StepGenerator.oneStep();
        step.setId(stepId);

        // mockito
        Mockito.when(stepRepository.getStepWithEagerFetchById(stepId))
                .thenReturn(Optional.of(step));

        // when
        Optional<Step> result = stepService.getStepById(stepId);

        // then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(stepId, result.get().getId());
    }

    @Test
    void getStepById_whenStepDoesNotExists_thenReturnEmptyOptional() {
        // given
        Long stepId = 10L;

        // mockito
        Mockito.when(stepRepository.getStepWithEagerFetchById(stepId))
                .thenReturn(Optional.empty());

        // when
        Optional<Step> result = stepService.getStepById(stepId);

        // then
        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    @Test
    void getStepById_whenParameterStepIdIsNull_thenThrowException() {
        // given
        Long stepId = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> stepService.getStepById(stepId));

    }


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    createStepForFlow(Step step, Flow flow)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void createStepForFlow_whenThereIsNoStepWithSuchDate_thenCreateAndReturnStep() {
        // given
        Flow flow = FlowGenerator.oneFlow();
        Step expected = StepGenerator.oneStep();
        Step forSave = new Step(expected);
        // removing step with the same date
        flow.getSteps().removeIf(s -> Objects.equals(s.getDay(), expected.getDay()));
        expected.setFlow(flow);
        forSave.setId(null);

        // mockito
        Mockito.when(stepRepository.existsStepByDayAndFlow_Id(expected.getDay(), flow.getId()))
                .thenReturn(false);
        Mockito.when(stepRepository.save(any(Step.class)))
                .thenAnswer(invocation -> {
                    Step step = invocation.getArgument(0);
                    step.setId(expected.getId());

                    return step;
                });

        // when
        Step result = stepService.createStepForFlow(forSave, flow);

        // then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(flow.getId(), result.getFlow().getId());
        assertTrue(result.getFlow().getSteps().stream().anyMatch(s -> Objects.equals(s.getId(), expected.getId())));
        assertEquals(expected.getDay(), result.getDay());
        Mockito.verify(stepRepository, atLeastOnce()).save(any(Step.class));
    }

    @Test
    void createStepForFlow_whenThereIsExistsStepWithSuchDate_thenThrowException() {
        // given
        Step step = StepGenerator.oneStep();
        Flow flow = FlowGenerator.oneFlow();
        step.setId(null);

        // mockito
        Mockito.when(stepRepository.existsStepByDayAndFlow_Id(step.getDay(), flow.getId()))
                .thenReturn(true);

        // when
        assertThrows(ForbiddenRequestException.class, () -> stepService.createStepForFlow(step, flow));
    }

    @Test
    void createStepForFlow_whenStepIdIsNotNull_thenThrowException() {
        // given
        Step step = StepGenerator.oneStep();
        Flow flow = FlowGenerator.oneFlow();

        // when
        assertThrows(IllegalArgumentException.class, () -> stepService.createStepForFlow(step, flow));
    }

    @Test
    void createStepForFlow_whenParameterStepIsNull_thenThrowException() {
        // given
        Step step = null;
        Flow flow = FlowGenerator.oneFlow();

        // when
        assertThrows(IllegalArgumentException.class, () -> stepService.createStepForFlow(step, flow));
    }

    @Test
    void createStepForFlow_whenParameterFlowIsNull_thenThrowException() {
        // given
        Step step = StepGenerator.oneStep();
        Flow flow = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> stepService.createStepForFlow(step, flow));
    }


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    deleteStepById(Long stepId)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void deleteStepById_whenStepExists_thenDeleteStep() {
        // given
        Step step = StepGenerator.oneStep();
        Long stepId = step.getId();

        // mockito
        Mockito.when(stepRepository.findById(stepId))
                .thenReturn(Optional.of(step));
        Mockito.when(stepRepository.save(any(Step.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        stepService.deleteStepById(stepId);

        // captor
        ArgumentCaptor<Step> captor = ArgumentCaptor.forClass(Step.class);
        Mockito.verify(stepRepository).save(captor.capture());
        Step beforeSave = captor.getValue();

        // then
        assertNotNull(beforeSave);
        assertTrue(beforeSave.isDeleted());
    }

    @Test
    void deleteStepById_whenStepDoesNotExist_thenThrowException() {
        // given
        Long stepId = 10L;

        // mockito
        Mockito.when(stepRepository.findById(stepId))
                .thenReturn(Optional.empty());

        // when
        assertThrows(SubentityNotFoundException.class, () -> stepService.deleteStepById(stepId));
    }

    @Test
    void deleteStepById_whenParameterStepIdIsNull_thenThrowException() {
        // given
        Long stepId = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> stepService.deleteStepById(stepId));
    }


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    getTargetPercentageByStepId(Long stepId)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void getTargetPercentageByStepId_whenStepExists_thenReturnTargetPercentage() {
        // given
        Flow flow = FlowGenerator.oneFlow();
        Step step = StepGenerator.oneStep();
        flow.setTargetPercentage(60);
        flow.getSteps().clear();
        flow.getSteps().add(step);
        step.setFlow(flow);

        // mockito
        Mockito.when(stepRepository.getStepWithEagerFetchById(step.getId()))
                .thenReturn(Optional.of(step));

        // when
        Long result = stepService.getTargetPercentageByStepId(step.getId());

        // then
        assertNotNull(result);
        assertEquals(flow.getTargetPercentage(), result);
    }

    @Test
    void getTargetPercentageByStepId_whenStepDoesntNotExist_thenThrowException() {
        // given
        Long stepId = 10L;

        // mockito
        Mockito.when(stepRepository.getStepWithEagerFetchById(stepId))
                .thenReturn(Optional.empty());

        // when
        assertThrows(SubentityNotFoundException.class, () -> stepService.getTargetPercentageByStepId(stepId));
    }

    @Test
    void getTargetPercentageByStepId_whenParameterStepIdIsNull_thenThrowException() {
        // given
        Long stepId = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> stepService.getTargetPercentageByStepId(stepId));
    }


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    checkOwnership(Long stepId, Long accountId)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void checkOwnership_whenAccountIsOwnerOfStep_thenReturnTrue() {
        // given
        Long stepId = 10L;
        Long accountId = 15L;

        // mockito
        Mockito.when(stepRepository.existsByIdAndFlow_Account_Id(stepId, accountId))
                .thenReturn(true);

        // when
        boolean result = stepService.checkOwnership(stepId, accountId);

        // then
        assertTrue(result);
    }

    @Test
    void checkOwnership_whenAccountIsNotOwnerOfStep_thenReturnFalse() {
        // given
        Long stepId = 10L;
        Long accountId = 15L;

        // mockito
        Mockito.when(stepRepository.existsByIdAndFlow_Account_Id(stepId, accountId))
                .thenReturn(false);

        // when
        boolean result = stepService.checkOwnership(stepId, accountId);

        // then
        assertFalse(result);
    }

    @Test
    void checkOwnership_whenParameterStepIdIsNull_thenThrowException() {
        // given
        Long stepId = null;
        Long accountId = 15L;

        // when
        assertThrows(IllegalArgumentException.class, () -> stepService.checkOwnership(stepId, accountId));
    }

    @Test
    void checkOwnership_whenParameterAccountIdIsNull_thenThrowException() {
        // given
        Long stepId = 10L;
        Long accountId = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> stepService.checkOwnership(stepId, accountId));
    }

/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    checkOwnershipOrElseThrow(Long stepId, Long accountId)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void checkOwnershipOrElseThrow_whenAccountIsOwnerOfStep_thenDoNothing() {
        // given
        Long stepId = 10L;
        Long accountId = 15L;

        // mockito
        Mockito.when(stepRepository.existsByIdAndFlow_Account_Id(stepId, accountId))
                .thenReturn(true);

        // when
        assertDoesNotThrow(() -> stepService.checkOwnershipOrElseThrow(stepId, accountId));
    }

    @Test
    void checkOwnershipOrElseThrow_whenAccountIsNotOwnerOfStep_thenThrowException() {
        // given
        Long stepId = 10L;
        Long accountId = 15L;

        // mockito
        Mockito.when(stepRepository.existsByIdAndFlow_Account_Id(stepId, accountId))
                .thenReturn(false);

        // when
        assertThrows(SubentityNotFoundException.class, () -> stepService.checkOwnershipOrElseThrow(stepId, accountId));
    }

    @Test
    void checkOwnershipOrElseThrow_whenParameterStepIdIsNull_thenThrowException() {
        // given
        Long stepId = null;
        Long accountId = 15L;

        // when
        assertThrows(IllegalArgumentException.class, () -> stepService.checkOwnershipOrElseThrow(stepId, accountId));
    }

    @Test
    void checkOwnershipOrElseThrow_whenParameterAccountIdIsNull_thenThrowException() {
        // given
        Long stepId = 10L;
        Long accountId = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> stepService.checkOwnershipOrElseThrow(stepId, accountId));
    }

}