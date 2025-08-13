package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.entity.flow.Case;
import com.yarovyi.flowmeter.entity.flow.Step;
import com.yarovyi.flowmeter.exception.EntityBadRequestException;
import com.yarovyi.flowmeter.exception.SubentityNotFoundException;
import com.yarovyi.flowmeter.repository.CaseRepository;
import com.yarovyi.flowmeter.testUtility.entityGenerator.CaseGenerator;
import com.yarovyi.flowmeter.testUtility.entityGenerator.StepGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CaseServiceImplTest {
    @Mock
    private CaseRepository caseRepository;
    @InjectMocks
    private CaseServiceImpl service;


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    createCaseForStep(Step step, Case case1)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void createCaseForStep_whenIsOk_thenCreateCaseForStepAndReturn() {
        // given
        Case inputCase = CaseGenerator.oneCase();
        Step inputStep = StepGenerator.oneStep();

        // mockito
        Mockito.when(caseRepository.save(any(Case.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Case result = service.createCaseForStep(new Step(inputStep), new Case(inputCase));

        // captor
        ArgumentCaptor<Case> captor = ArgumentCaptor.forClass(Case.class);
        Mockito.verify(caseRepository).save(captor.capture());
        Case beforeSave = captor.getValue();

        // then
        assertNotNull(result);
        assertNotNull(result.getStep());
        assertEquals(inputStep.getId(), result.getStep().getId());
        assertEquals(inputCase.getId(), result.getId());
        assertEquals(beforeSave.getId(), result.getId());
        assertEquals(beforeSave.getStep().getId(), result.getStep().getId());
    }

    @Test
    void createCaseForStep_whenParameterStepIsNull_thenThrowException() {
        // given
        Step step = null;
        Case case1 = new Case();

        // when
        assertThrows(IllegalArgumentException.class, () -> service.createCaseForStep(step, case1));
    }

    @Test
    void createCaseForStep_whenParameterCaseIsNull_thenThrowException() {
        // given
        Step step = new Step();
        Case case1 = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> service.createCaseForStep(step, case1));
    }

/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    edit(Case editedCase)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void edit_whenIsOk_thenEditAndReturnCase() {
        // given
        Long id = 10L;
        Case before = CaseGenerator.oneCase();
        Case edited = CaseGenerator.oneCase();
        before.setId(id);
        edited.setId(id);

        // mockito
        Mockito.when(caseRepository.findById(id))
                .thenReturn(Optional.of(before));
        Mockito.when(caseRepository.save(any(Case.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Case result = service.edit(edited);

        // captor
        ArgumentCaptor<Case> captor = ArgumentCaptor.forClass(Case.class);
        Mockito.verify(caseRepository).save(captor.capture());
        Case beforeSaving = captor.getValue();

        // then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(before.getId(), result.getId());
        assertEquals(edited, result);
        assertEquals(beforeSaving, result);
    }

    @Test
    void edit_whenCaseIsNotExistsById_thenThrowException() {
        // given
        Long id = 10L;
        Case changed = new Case();
        changed.setId(id);

        // mockito
        Mockito.when(caseRepository.findById(id))
                .thenReturn(Optional.empty());

        // when
        assertThrows(SubentityNotFoundException.class, () -> service.edit(changed));
    }

    @Test
    void edit_whenParameterEditedCaseIsNull_thenThrowException() {
        // given
        Case changed = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> service.edit(changed));
    }

    @Test
    void edit_whenParameterEditedCaseDontHaveId_thenThrowException() {
        // given
        Case changed = CaseGenerator.oneCase();
        changed.setId(null);

        // when
        assertThrows(EntityBadRequestException.class, () -> service.edit(changed));
    }


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    deleteCaseById(Long caseId)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void deleteCaseById_whenIsOk_thenDeleteCase() {
        // given
        Long caseId = 10L;
        Case case1 = CaseGenerator.oneCase();
        case1.setId(caseId);
        case1.setDeleted(false);

        // mockito
        Mockito.when(caseRepository.findById(caseId))
                .thenReturn(Optional.of(case1));
        Mockito.when(caseRepository.save(any(Case.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        service.deleteCaseById(caseId);

        // captor
        ArgumentCaptor<Case> captor = ArgumentCaptor.forClass(Case.class);
        Mockito.verify(caseRepository).save(captor.capture());
        Case beforeSave = captor.getValue();

        // then
        assertNotNull(beforeSave);
        assertEquals(case1.getId(), beforeSave.getId());
        assertTrue(beforeSave.isDeleted());
    }

    @Test
    void deleteCaseById_whenCaseNotExist_thenThrowException() {
        // given
        Long caseId = 10L;

        // mockito
        Mockito.when(caseRepository.findById(caseId))
                .thenReturn(Optional.empty());

        // when
        assertThrows(SubentityNotFoundException.class, () -> service.deleteCaseById(caseId));
    }

    @Test
    void deleteCaseById_whenParameterCaseIdIsNull_thenThrowException() {
        // given
        Long caseId = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> service.deleteCaseById(caseId));
    }


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    checkOwnership(Long caseId, Long accountId)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void checkOwnership_whenAccountHasSuchCase_thenReturnTrue() {
        // given
        Long caseId = 10L;
        Long accountId = 5L;

        // mockito
        Mockito.when(caseRepository.existsByIdAndAccountId(caseId, accountId))
                .thenReturn(true);

        // when
        boolean ownership = service.checkOwnership(caseId, accountId);

        // then
        assertTrue(ownership);
    }

    @Test
    void checkOwnership_whenAccountDontHaveSuchCase_thenReturnFalse() {
        // given
        Long caseId = 10L;
        Long accountId = 5L;

        // mockito
        Mockito.when(caseRepository.existsByIdAndAccountId(caseId, accountId))
                .thenReturn(false);

        // when
        boolean ownership = service.checkOwnership(caseId, accountId);

        // then
        assertFalse(ownership);
    }

    @Test
    void checkOwnership_whenParameterCaseIdIsNull_thenThrowException() {
        // given
        Long caseId = null;
        Long accountId = 5L;

        // when
        assertThrows(IllegalArgumentException.class, () -> service.checkOwnership(caseId, accountId));
    }

    @Test
    void checkOwnership_whenParameterAccountIdIsNull_thenThrowException() {
        // given
        Long caseId = 10L;
        Long accountId = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> service.checkOwnership(caseId, accountId));
    }


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    checkOwnershipOrElseThrow(Long caseId, Long accountId)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void checkOwnershipOrElseThrow_whenAccountHasSuchCase_thenDoNothing() {
        // given
        Long caseId = 10L;
        Long accountId = 5L;

        // mockito
        Mockito.when(caseRepository.existsByIdAndAccountId(caseId, accountId))
                .thenReturn(true);

        // when
        assertDoesNotThrow(() -> service.checkOwnershipOrElseThrow(caseId, accountId));
    }

    @Test
    void checkOwnershipOrElseThrow_whenAccountDontHaveSuchCase_thenThrowException() {
        // given
        Long caseId = 10L;
        Long accountId = 5L;

        // mockito
        Mockito.when(caseRepository.existsByIdAndAccountId(caseId, accountId))
                .thenReturn(false);

        // when
        assertThrows(SubentityNotFoundException.class, () -> service.checkOwnershipOrElseThrow(caseId, accountId));
    }

    @Test
    void checkOwnershipOrElseThrow_whenParameterCaseIdIsNull_thenThrowException() {
        // given
        Long caseId = null;
        Long accountId = 5L;

        // when
        assertThrows(IllegalArgumentException.class, () -> service.checkOwnershipOrElseThrow(caseId, accountId));
    }

    @Test
    void checkOwnershipOrElseThrow_whenParameterAccountIdIsNull_thenThrowException() {
        // given
        Long caseId = 10L;
        Long accountId = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> service.checkOwnershipOrElseThrow(caseId, accountId));
    }

}