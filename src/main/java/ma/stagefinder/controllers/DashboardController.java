package ma.stagefinder.controllers;

import lombok.RequiredArgsConstructor; // ✅ Ziyada jdida
import ma.stagefinder.services.DashboardService;
import org.springframework.http.ResponseEntity; // ✅ Ziyada jdida
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor // ✅ B'blasset @Autowired w @AllArgsConstructor
public class DashboardController {

  private final DashboardService dashboardService;

  @GetMapping("/users-registered-per-month")
  public ResponseEntity<List<Map<String, Object>>> getUsersRegisteredPerMonth() {
    return ResponseEntity.ok(dashboardService.getUsersRegisteredPerMonth());
  }

  @GetMapping("/job-postings-per-month")
  public ResponseEntity<List<Map<String, Object>>> getJobPostingsPerMonth() {
    return ResponseEntity.ok(dashboardService.getJobPostingsPerMonth());
  }

  @GetMapping("/most-published-categories")
  public ResponseEntity<List<Map<String, Object>>> getMostPublishedCategories() {
    return ResponseEntity.ok(dashboardService.getMostPublishedCategories());
  }

  // ✅ ==========================================================
  // ==     ENDPOINT JDID POUR LES STATISTIQUES D'ABONNEMENT   ==
  // ==========================================================
  @GetMapping("/statistiques/abonnements")
  public ResponseEntity<Map<String, Long>> getAbonnementStats() {
    return ResponseEntity.ok(dashboardService.getAbonnementStatistiques());
  }
}
