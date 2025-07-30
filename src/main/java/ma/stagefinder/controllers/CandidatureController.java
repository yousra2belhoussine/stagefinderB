package ma.stagefinder.controllers;

import lombok.AllArgsConstructor;
import ma.stagefinder.dtos.CandidatureDTO;
import ma.stagefinder.dtos.UpdateStatusRequest;
import ma.stagefinder.entities.enums.StatutCandidature;
import ma.stagefinder.repositories.CandidatureRepository;
import ma.stagefinder.services.CandidatureService;
import ma.stagefinder.services.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/candidatures")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class CandidatureController {

  private CandidatureRepository candidatureRepository;
  private CandidatureService candidatureService;
  private FileStorageService fileStorageService;

  @PreAuthorize("hasRole('ADMINISTRATEUR')")
  @GetMapping("/count")
  public long countCandidatures() {
    return candidatureRepository.count();
  }

  @PostMapping
  public ResponseEntity<CandidatureDTO> createCandidature(
          @RequestParam("userId") Long userId,
          @RequestParam("offreId") Long offreId,
          @RequestParam("statutCandidature") String statutCandidature,
          @RequestParam(value = "file", required = false) List<MultipartFile> files,
          @RequestParam(value = "type", required = false) List<String> types) throws IOException {
    CandidatureDTO candidatureDTO = new CandidatureDTO();
    candidatureDTO.setUserId(userId);
    candidatureDTO.setOffreId(offreId);
    candidatureDTO.setStatutCandidature(StatutCandidature.valueOf(statutCandidature));

    // Process files
    if (files != null && types != null) {
      for (int i = 0; i < files.size(); i++) {
        MultipartFile file = files.get(i);
        String type = types.get(i);
        String filename = fileStorageService.storeFile(file, type);
        if (type.equals("cv") || type.equals("cvChoisi")) {
          candidatureDTO.setCvChoisi(file);
          candidatureDTO.setCvFileName(filename); // Store CV file name
        } else if (type.equals("lettre") || type.equals("lettreMotivation")) {
          candidatureDTO.setLettreMotivation(file);
          candidatureDTO.setLettreMotivationFileName(filename); // Store cover letter file name
        }
      }
    }

    CandidatureDTO savedCandidature = candidatureService.createCandidature(candidatureDTO);
    return ResponseEntity.ok(savedCandidature);
  }

  @GetMapping("/offre/{offreId}")
  public ResponseEntity<Page<CandidatureDTO>> getCandidaturesByOffre(
          @PathVariable Long offreId,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size) {
    Page<CandidatureDTO> candidatures = candidatureService.getCandidaturesByOffre(offreId, page, size);
    return ResponseEntity.ok(candidatures);
  }

  @PreAuthorize("hasRole('ADMINISTRATEUR') or hasRole('USER')")
  @GetMapping("/file/{fileName}")
  public ResponseEntity<Resource> serveFile(@PathVariable String fileName, @RequestParam String type) throws IOException {
    Path filePath = fileStorageService.getFilePath(fileName, type);
    Resource resource = new UrlResource(filePath.toUri());
    if (resource.exists() && resource.isReadable()) {
      String contentType = type.equals("image") ? "image/" + fileName.substring(fileName.lastIndexOf(".") + 1) : "application/pdf";
      return ResponseEntity.ok()
              .contentType(MediaType.parseMediaType(contentType))
              .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
              .body(resource);
    } else {
      return ResponseEntity.status(404).body(null);
    }
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<CandidatureDTO> updateCandidatureStatus(
          @PathVariable Long id,
          @RequestBody UpdateStatusRequest request) {
    CandidatureDTO updatedCandidature = candidatureService.updateCandidatureStatus(id, request.getStatutCandidature());
    return ResponseEntity.ok(updatedCandidature);
  }

  @GetMapping("/my-applied-offers/{userId}")
  public ResponseEntity<List<CandidatureDTO>> getMyAppliedOffers(@PathVariable Long userId) {
    List<CandidatureDTO> appliedOffers = candidatureService.getAppliedOffersByUser(userId);
    return ResponseEntity.ok(appliedOffers);
  }
}