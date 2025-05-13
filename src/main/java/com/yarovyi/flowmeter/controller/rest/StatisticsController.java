package com.yarovyi.flowmeter.controller.rest;

import com.yarovyi.flowmeter.entity.dto.UniqueMonth;
import com.yarovyi.flowmeter.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/flowmeter/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;


    @GetMapping("/unique-months/{flowId:\\d+}")
    public ResponseEntity<Set<UniqueMonth>> getUniqueMonths(@PathVariable(name = "flowId") Long flowId) {
        Set<UniqueMonth> uniqueMonths = this.statisticsService.getSortedUniqueMonths(flowId);

        return ResponseEntity.ok(uniqueMonths); // todo: needed to sort here
    }


}
