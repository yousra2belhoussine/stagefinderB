package ma.stagefinder.controllers;

import lombok.AllArgsConstructor;
import ma.stagefinder.repositories.CandidatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/candidatures")
@AllArgsConstructor
public class CandidatureController {

    private CandidatureRepository candidatureRepository;

    @GetMapping("/count")
    public long countCandidatures() {
        return candidatureRepository.count();
    }
}
