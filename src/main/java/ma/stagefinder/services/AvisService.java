package ma.stagefinder.services;

import ma.stagefinder.dtos.AvisDTO;

import java.util.List;

public interface AvisService {
  AvisDTO createAvis(AvisDTO avisDTO);
  AvisDTO getAvisById(Long id);
  List<AvisDTO> getAllAvis();
  List<AvisDTO> getAvisByAuteur(Long auteurId);
  List<AvisDTO> getAvisByDestinataire(Long destinataireId);
  List<AvisDTO> getAvisByOffre(Long offreId);
  AvisDTO updateAvis(Long id, AvisDTO avisDTO);
  void deleteAvis(Long id);
}
