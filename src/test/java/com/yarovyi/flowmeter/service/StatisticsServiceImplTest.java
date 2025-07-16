package com.yarovyi.flowmeter.service;

import com.yarovyi.flowmeter.dto.stat.StatInterval;
import com.yarovyi.flowmeter.dto.stat.StatParams;
import com.yarovyi.flowmeter.dto.stat.StatPoint;
import com.yarovyi.flowmeter.dto.stat.UniqueMonth;
import com.yarovyi.flowmeter.entity.flow.Flow;
import com.yarovyi.flowmeter.entity.flow.Step;
import com.yarovyi.flowmeter.exception.InvalidParametersException;
import com.yarovyi.flowmeter.exception.SubentityNotFoundException;
import com.yarovyi.flowmeter.repository.FlowRepository;
import com.yarovyi.flowmeter.repository.StatisticsRepository;
import com.yarovyi.flowmeter.strategy.statistics.StatisticsFactory;
import com.yarovyi.flowmeter.strategy.statistics.StatisticsScope;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceImplTest {
    @Mock
    private FlowRepository flowRepository;
    @Mock
    private StatisticsRepository statisticsRepository;
    @Mock
    private StatisticsFactory statisticsFactory;
    @InjectMocks
    private StatisticsServiceImpl service;


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    getSortedUniqueMonths(Long flowId)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void getSortedUniqueMonths_whenFlowExists_thenReturnSetOfUniqueMonth() {
        // given
        Long flowId = 10L;
        Set<Step> steps = createSetOfSteps();
        Set<UniqueMonth> expected = createSetOfUniqueSteps();
        Flow flow = createFlowWithSteps(flowId, steps);

        // mockito
        Mockito.when(flowRepository.findById(flowId))
                .thenReturn(Optional.of(flow));

        // when
        Set<UniqueMonth> result = service.getSortedUniqueMonths(flowId);

        // then
        assertNotNull(result);
        assertEquals(expected.size(), result.size());
        assertIterableEquals(expected, result);
        Mockito.verify(flowRepository, times(1)).findById(flowId);
    }

    Flow createFlowWithSteps(Long id, Set<Step> steps) {
        Flow flow = new Flow();
        flow.setId(id);
        flow.setSteps(steps);

        return flow;
    }

    Set<Step> createSetOfSteps() {
        Set<Step> steps = new HashSet<>();
        steps.add(createStep(LocalDate.of(2020,7,10)));
        steps.add(createStep(LocalDate.of(2021,7,10)));
        steps.add(createStep(LocalDate.of(2021,8,10)));
        steps.add(createStep(LocalDate.of(2021,9,10)));
        steps.add(createStep(LocalDate.of(2021,10,10)));
        // duplicate won't be added
        steps.add(createStep(LocalDate.of(2021,8,11)));

        return steps;
    }

    Set<UniqueMonth> createSetOfUniqueSteps() {
        Set<UniqueMonth> steps = new TreeSet<>();
        steps.add(new UniqueMonth(2020, "JULY"));
        steps.add(new UniqueMonth(2021, "JULY"));
        steps.add(new UniqueMonth(2021, "AUGUST"));
        steps.add(new UniqueMonth(2021, "SEPTEMBER"));
        steps.add(new UniqueMonth(2021, "OCTOBER"));

        return steps;
    }

    Step createStep(LocalDate date) {
        Step step = new Step();
        step.setDay(date);

        return step;
    }

    @Test
    void getSortedUniqueMonths_whenFlowIsNotExist_thenReturnEmptySet() {
        // given
        Long flowId = 10L;
        Set<Step> steps = Collections.emptySet();
        Flow flow = createFlowWithSteps(flowId, steps);

        // mockito
        Mockito.when(flowRepository.findById(flowId))
                .thenReturn(Optional.of(flow));

        // when
        Set<UniqueMonth> result = service.getSortedUniqueMonths(flowId);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getSortedUniqueMonths_whenFlowNotExists_thenThrowException() {
        // given
        Long flowId = 10L;

        // mockito
        Mockito.when(flowRepository.findById(flowId))
                .thenReturn(Optional.empty());

        // when
        assertThrows(SubentityNotFoundException.class, () -> service.getSortedUniqueMonths(flowId));
    }

    @Test
    void getSortedUniqueMonths_whenParameterFlowIdIsNull_thenThrowException() {
        // given
        Long flowId = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> service.getSortedUniqueMonths(flowId));
    }


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    getStatisticsForMonth(Long flowId, int year, String month)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void getStatisticsForMonth_whenAllIsValid_thenReturnStatisticsForMonth() {
        // given
        Long flowId = 10L;
        int year = 2025;
        String month = "SEPTEMBER";
        StatInterval expected = getStatIntervalForMonth(year, Month.valueOf(month));

        // mockito
        Mockito.when(statisticsFactory.prepareStatistics(any(StatParams.class), eq(statisticsRepository)))
                .thenReturn(expected);

        // when
        StatInterval result = service.getStatisticsForMonth(flowId, year, month);

        // captor
        ArgumentCaptor<StatParams> captor = ArgumentCaptor.forClass(StatParams.class);
        Mockito.verify(statisticsFactory).prepareStatistics(captor.capture(), any(StatisticsRepository.class));
        StatParams capturedValue = captor.getValue();

        // then
        assertNotNull(capturedValue);
        assertEquals(StatisticsScope.MONTH, capturedValue.scope());
        assertEquals(flowId, capturedValue.flowId());
        assertEquals(year, capturedValue.year());
        assertEquals(month, capturedValue.month());

        assertNotNull(result);
        assertEquals(expected.flowTitle(), result.flowTitle());
        assertEquals(expected.year(), result.year());
        assertEquals(expected.interval(), result.interval());
        assertIterableEquals(expected.points(), result.points());
        Mockito.verify(statisticsFactory, times(1))
                .prepareStatistics(any(StatParams.class), any(StatisticsRepository.class));
    }

    StatInterval getStatIntervalForMonth(int year, Month month) {
        LocalDate from = LocalDate.of(year, month, 1);
        LocalDate to = from.plusMonths(1);
        List<StatPoint> points = getStatPoints(from, to);

        return new StatInterval(month.name(), "Flow title", year, points);
    }

    private List<StatPoint> getStatPoints(LocalDate from, LocalDate to) {
        assert from.isBefore(to);
        List<StatPoint> points = new ArrayList<>();
        long daysBetween = ChronoUnit.DAYS.between(from, to);

        for (int i = 0; i < daysBetween; i++) {
            points.add(StatPoint.empty(from.plusDays(i)));
        }

        return points;
    }

    @Test
    void getStatisticsForMonth_whenParameterMonthIsInvalid_thenThrowException() {
        // given
        Long flowId = 10L;
        int year = 2025;
        String month = "SSSEPTEMBER";

        // when
        assertThrows(InvalidParametersException.class, () -> service.getStatisticsForMonth(flowId, year, month));
    }

    @Test
    void getStatisticsForMonth_whenParameterFlowIdIsNull_thenThrowException() {
        // given
        Long flowId = null;
        int year = 2025;
        String month = "SEPTEMBER";

        // when
        assertThrows(IllegalArgumentException.class, () -> service.getStatisticsForMonth(flowId, year, month));
    }


    @Test
    void getStatisticsForMonth_whenParameterMonthIsNull_thenThrowException() {
        // given
        Long flowId = 10L;
        int year = 2025;
        String month = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> service.getStatisticsForMonth(flowId, year, month));
    }


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    getStatisticsForLastWeek(Long flowId)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void getStatisticsForLastWeek_whenCalled_thanReturnStatisticForLastWeek() {
        // given
        Long flowId = 10L;
        StatInterval expected = getStatIntervalForLastWeek();

        // mockito
        Mockito.when(statisticsFactory.prepareStatistics(any(StatParams.class), eq(statisticsRepository)))
                .thenReturn(expected);

        // when
        StatInterval result = service.getStatisticsForLastWeek(flowId);

        // captor
        ArgumentCaptor<StatParams> captor = ArgumentCaptor.forClass(StatParams.class);
        Mockito.verify(statisticsFactory).prepareStatistics(captor.capture(), any(StatisticsRepository.class));
        StatParams capturedValue = captor.getValue();

        // then
        assertNotNull(capturedValue);
        assertEquals(StatisticsScope.LAST_WEEK, capturedValue.scope());
        assertEquals(flowId, capturedValue.flowId());
        assertNull(capturedValue.year());
        assertNull(capturedValue.month());

        assertNotNull(result);
        assertEquals(expected, result);
        Mockito.verify(statisticsFactory, times(1))
                .prepareStatistics(any(StatParams.class), any(StatisticsRepository.class));
    }

    private StatInterval getStatIntervalForLastWeek() {
        LocalDate now = LocalDate.now();
        LocalDate weekAgo = now.minusWeeks(1);

        List<StatPoint> points = getStatPoints(weekAgo, now);
        return new StatInterval(StatisticsScope.LAST_WEEK.name(), "Flow title", now.getYear(), points);
    }

    @Test
    void getStatisticsForLastWeek_whenParameterFlowIdIsNull_thanThrowException() {
        // given
        Long flowId = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> service.getStatisticsForLastWeek(flowId));
    }


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    getStatisticsForLastMonth(Long flowId)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void getStatisticsForLastMonth_whenCalled_thanReturnStatisticsForLastMonth() {
        // given
        Long flowId = 10L;
        StatInterval expected = getStatIntervalForLastMonth();

        // mockito
        Mockito.when(statisticsFactory.prepareStatistics(any(StatParams.class), eq(statisticsRepository)))
                .thenReturn(expected);

        // when
        StatInterval result = service.getStatisticsForLastMonth(flowId);

        // captor
        ArgumentCaptor<StatParams> captor = ArgumentCaptor.forClass(StatParams.class);
        Mockito.verify(statisticsFactory).prepareStatistics(captor.capture(), any(StatisticsRepository.class));
        StatParams capturedValue = captor.getValue();

        // then
        assertNotNull(capturedValue);
        assertEquals(StatisticsScope.LAST_MONTH, capturedValue.scope());
        assertEquals(flowId, capturedValue.flowId());
        assertNull(capturedValue.year());
        assertNull(capturedValue.month());

        assertNotNull(result);
        assertEquals(expected, result);
        Mockito.verify(statisticsFactory, times(1))
                .prepareStatistics(any(StatParams.class), any(StatisticsRepository.class));
    }

    private StatInterval getStatIntervalForLastMonth() {
        LocalDate now = LocalDate.now();
        LocalDate monthAgo = now.minusMonths(1);
        List<StatPoint> points = getStatPoints(monthAgo, now);

        return new StatInterval(StatisticsScope.LAST_MONTH.name(), "Flow title", now.getYear(), points);
    }

    @Test
    void getStatisticsForLastMonth_whenParameterFlowIdIsNull_thanThrowException() {
        // given
        Long flowId = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> service.getStatisticsForLastMonth(flowId));
    }


/*
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    getStatisticsForLastYear(Long flowId)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/
    @Test
    void getStatisticsForLastYear_whenCalled_thanReturnStatisticsForLastYear() {
        // given
        Long flowId = 10L;
        StatInterval expected = getStatIntervalForLastYear();

        // mockito
        Mockito.when(statisticsFactory.prepareStatistics(any(StatParams.class), eq(statisticsRepository)))
                .thenReturn(expected);

        // when
        StatInterval result = service.getStatisticsForLastYear(flowId);

        // captor
        ArgumentCaptor<StatParams> captor = ArgumentCaptor.forClass(StatParams.class);
        Mockito.verify(statisticsFactory).prepareStatistics(captor.capture(), any(StatisticsRepository.class));
        StatParams capturedValue = captor.getValue();

        // then
        assertNotNull(capturedValue);
        assertEquals(StatisticsScope.LAST_YEAR, capturedValue.scope());
        assertEquals(flowId, capturedValue.flowId());
        assertNull(capturedValue.year());
        assertNull(capturedValue.month());

        assertNotNull(result);
        assertEquals(expected, result);
        Mockito.verify(statisticsFactory, times(1))
                .prepareStatistics(any(StatParams.class), any(StatisticsRepository.class));
    }

    private StatInterval getStatIntervalForLastYear() {
        LocalDate now = LocalDate.now();
        LocalDate yearAgo = now.minusYears(1);

        List<StatPoint> points = getStatPointsByMonth(yearAgo, now);

        return new StatInterval(StatisticsScope.LAST_YEAR.name(), "Flow title", now.getYear(), points);
    }

    private List<StatPoint> getStatPointsByMonth(LocalDate from, LocalDate to) {
        assert from.isBefore(to);
        List<StatPoint> points = new ArrayList<>();
        long monthsBetween = ChronoUnit.MONTHS.between(from, to);

        for (int i = 0; i < monthsBetween; i++) {
            points.add(StatPoint.empty(from.plusMonths(i)));
        }

        return points;
    }

    @Test
    void getStatisticsForLastYear_whenParameterFlowIdIsNull_thanThrowException() {
        // given
        Long flowId = null;

        // when
        assertThrows(IllegalArgumentException.class, () -> service.getStatisticsForLastYear(flowId));
    }

}