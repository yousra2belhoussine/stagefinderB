package ma.stagefinder.services;

import ma.stagefinder.dtos.FavorisDTO;

import java.util.List;

public interface FavorisService {


    FavorisDTO addFavoris(FavorisDTO favorisDTO);

    void deleteFavoris(Long id);

    List<FavorisDTO> getFavorisByUserId(Long userId);

    List<FavorisDTO> getFavorisByOffreId(Long offreId);


}
