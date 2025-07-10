package ma.stagefinder.services;

import java.util.List;
import java.util.Map;

public interface DashboardService {

  List<Map<String, Object>> getUsersRegisteredPerMonth() ;

  List<Map<String, Object>> getJobPostingsPerMonth() ;

  List<Map<String, Object>> getMostPublishedCategories() ;
}
