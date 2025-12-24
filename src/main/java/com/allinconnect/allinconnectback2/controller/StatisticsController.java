package com.allinconnect.allinconnectback2.controller;

import com.allinconnect.allinconnectback2.entity.MonthlyStat;
import com.allinconnect.allinconnectback2.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/current")
    public ResponseEntity<MonthlyStat> getCurrentStats() {
        return ResponseEntity.ok(statisticsService.getCurrentMonthStats());
    }

    @GetMapping("/history")
    public ResponseEntity<List<MonthlyStat>> getHistory() {
        return ResponseEntity.ok(statisticsService.getHistory());
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("current", statisticsService.getCurrentMonthStats());
        dashboard.put("history", statisticsService.getHistory());
        return ResponseEntity.ok(dashboard);
    }

    @PostMapping("/freeze")
    public ResponseEntity<MonthlyStat> freezeMonth(@RequestParam int year, @RequestParam int month) {
        return ResponseEntity.ok(statisticsService.freezeMonth(year, month));
    }

    @PostMapping("/freeze-previous")
    public ResponseEntity<MonthlyStat> freezePreviousMonth() {
        LocalDateTime previous = LocalDateTime.now().minusMonths(1);
        return ResponseEntity.ok(statisticsService.freezeMonth(previous.getYear(), previous.getMonthValue()));
    }
}
