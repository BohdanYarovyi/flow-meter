package com.yarovyi.flowmeter.controller.rest;

import com.yarovyi.flowmeter.entity.account.Account;
import com.yarovyi.flowmeter.dto.stat.StatInterval;
import com.yarovyi.flowmeter.dto.stat.UniqueMonth;
import com.yarovyi.flowmeter.service.AccountService;
import com.yarovyi.flowmeter.service.FlowService;
import com.yarovyi.flowmeter.service.StatisticsService;
import com.yarovyi.flowmeter.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/flowmeter/statistics")
public class StatisticsController {
    private final AccountService accountService;
    private final FlowService flowService;
    private final StatisticsService statisticsService;


    @GetMapping("/unique-months/{flowId:\\d+}")
    public ResponseEntity<Set<UniqueMonth>> getUniqueMonths(@PathVariable(name = "flowId") Long flowId) {
        return ResponseEntity.ok(this.statisticsService.getSortedUniqueMonths(flowId));
    }


    @GetMapping("/last-week/{flowId:\\d+}")
    public ResponseEntity<StatInterval> getStatisticForLastWeek(@PathVariable(name = "flowId") Long flowId,
                                                                Principal principal) {
        Account account = SecurityUtil.getCurrentAccount(this.accountService, principal);
        this.flowService.checkOwnerShipOrElseThrow(flowId, account.getId());

        StatInterval statisticsForLastWeek = this.statisticsService.getStatisticsForLastWeek(flowId);
        return ResponseEntity.ok(statisticsForLastWeek);
    }


    @GetMapping("/last-month/{flowId:\\d+}")
    public ResponseEntity<StatInterval> getStatisticsForLastMonth(@PathVariable(name = "flowId") Long flowId,
                                                                  Principal principal) {
        Account account = SecurityUtil.getCurrentAccount(this.accountService, principal);
        this.flowService.checkOwnerShipOrElseThrow(flowId, account.getId());

        StatInterval statisticsForLastMonth = this.statisticsService.getStatisticsForLastMonth(flowId);
        return ResponseEntity.ok(statisticsForLastMonth);
    }


    @GetMapping("/last-year/{flowId:\\d+}")
    public ResponseEntity<StatInterval> getStatisticsForLastYear(@PathVariable(name = "flowId") Long flowId,
                                                                 Principal principal) {
        Account account = SecurityUtil.getCurrentAccount(this.accountService, principal);
        this.flowService.checkOwnerShipOrElseThrow(flowId, account.getId());

        StatInterval statisticsForLastYear = this.statisticsService.getStatisticsForLastYear(flowId);
        return ResponseEntity.ok(statisticsForLastYear);
    }


    @GetMapping("/month/{flowId:\\d+}")
    public ResponseEntity<StatInterval> getStatisticsForMonth(@PathVariable(name = "flowId") Long flowId,
                                                              @RequestParam(name = "month") String month,
                                                              @RequestParam(name = "year") int year,
                                                              Principal principal) {
        Account account = SecurityUtil.getCurrentAccount(this.accountService, principal);
        this.flowService.checkOwnerShipOrElseThrow(flowId, account.getId());

        StatInterval statisticsForMonth = this.statisticsService.getStatisticsForMonth(flowId, year, month);
        return ResponseEntity.ok(statisticsForMonth);
    }


}
