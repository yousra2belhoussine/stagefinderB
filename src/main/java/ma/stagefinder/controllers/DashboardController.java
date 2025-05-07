package ma.stagefinder.controllers;

import lombok.AllArgsConstructor;
import ma.stagefinder.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@AllArgsConstructor
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/users-registered-per-month")
    public List<Map<String, Object>> getUsersRegisteredPerMonth() {
        return dashboardService.getUsersRegisteredPerMonth();
    }

    @GetMapping("/job-postings-per-month")
    public List<Map<String, Object>> getJobPostingsPerMonth() {
        return dashboardService.getJobPostingsPerMonth();
    }

    @GetMapping("/most-published-categories")
    public List<Map<String, Object>> getMostPublishedCategories() {
        return dashboardService.getMostPublishedCategories();
    }
}