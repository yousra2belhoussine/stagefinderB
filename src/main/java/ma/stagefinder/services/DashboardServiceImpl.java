package ma.stagefinder.services;

import lombok.AllArgsConstructor;
import ma.stagefinder.repositories.OffreRepository;
import ma.stagefinder.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OffreRepository offreRepository;

    public List<Map<String, Object>> getUsersRegisteredPerMonth() {
        return userRepository.findUsersRegisteredPerMonth();
    }

    public List<Map<String, Object>> getJobPostingsPerMonth() {
        return offreRepository.findJobPostingsPerMonth();
    }

    public List<Map<String, Object>> getMostPublishedCategories() {
        return offreRepository.findMostPublishedCategories();
    }
}