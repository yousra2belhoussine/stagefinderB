package ma.stagefinder.services;

import ma.stagefinder.dtos.FavorisDTO;
import ma.stagefinder.entities.Favoris;
import ma.stagefinder.exceptions.FavorisNotFoundException;
import ma.stagefinder.mapper.EntityMapper;
import ma.stagefinder.repositories.FavorisRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavorisServiceImpl implements FavorisService {

    private final FavorisRepository favorisRepository;

    public FavorisServiceImpl(FavorisRepository favorisRepository) {
        this.favorisRepository = favorisRepository;
    }


    @Override
    public FavorisDTO addFavoris(FavorisDTO favorisDTO) {
        // Mapper le DTO en entité Favoris
        Favoris favoris = EntityMapper.INSTANCE.toFavoris(favorisDTO);

        // Sauvegarder dans la base de données
        Favoris savedFavoris = favorisRepository.save(favoris);

        // Mapper l'entité sauvegardée en DTO et retourner
        return EntityMapper.INSTANCE.toFavorisDTO(savedFavoris);
    }

    @Override
    public void deleteFavoris(Long id) {
        Favoris favoris = favorisRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Favoris avec id " + id + " introuvable"));
        favorisRepository.delete(favoris);
    }

    @Override
    public List<FavorisDTO> getFavorisByUserId(Long userId) {
        List<Favoris> favorisList = favorisRepository.findByUserId(userId);
        return favorisList.stream()
                .map(EntityMapper.INSTANCE::toFavorisDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FavorisDTO> getFavorisByOffreId(Long offreId) {
        List<Favoris> favorisList = favorisRepository.findByOffreId(offreId);
        return favorisList.stream()
                .map(EntityMapper.INSTANCE::toFavorisDTO)
                .collect(Collectors.toList());
    }




}