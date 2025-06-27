package ma.stagefinder.mapper;

import ma.stagefinder.dtos.UserDTO;
import ma.stagefinder.entities.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class EntityMapperTest {

    private final EntityMapper entityMapper = Mappers.getMapper(EntityMapper.class);

    @Test
    void shouldCorrectlyMapUserToUserDTO() {
        // Arrange
        User userEntity = new User();
        userEntity.setId(1L);
        userEntity.setNom("Mohammed Malki");
        userEntity.setEmail("malki.test@example.com");

        // Act
        UserDTO resultUserDTO = entityMapper.toUserDTO(userEntity);

        // Assert
        assertNotNull(resultUserDTO);
        assertEquals(userEntity.getId(), resultUserDTO.getId());
        assertEquals(userEntity.getNom(), resultUserDTO.getNom());
        assertEquals(userEntity.getEmail(), resultUserDTO.getEmail());
    }

    // ===============================================
    // ====          TEST JDID L HNA            ====
    // ===============================================

    @Test // Kan'zidoha dima l'ay méthode dial test
    void shouldCorrectlyMapUserDTOToUser() {
        // ==========================================================
        // Étape 1: Arrange (التجهيز)
        // Had l'merra, kan'bdaou b DTO.
        // ==========================================================
        UserDTO userDTO = new UserDTO();
        userDTO.setId(2L);
        userDTO.setNom("Fatima Alami");
        userDTO.setEmail("fatima.test@example.com");

        // ==========================================================
        // Étape 2: Act (الفعل)
        // On exécute la méthode qu'on veut tester: toUser()
        // ==========================================================
        User resultUserEntity = entityMapper.toUser(userDTO);

        // ==========================================================
        // Étape 3: Assert (التأكيد)
        // On vérifie que l'entité résultante est correcte.
        // ==========================================================
        assertNotNull(resultUserEntity, "L'entité résultante ne doit pas être null");

        assertEquals(userDTO.getId(), resultUserEntity.getId(), "L'ID doit être le même");
        assertEquals(userDTO.getNom(), resultUserEntity.getNom(), "Le nom doit être le même");
        assertEquals(userDTO.getEmail(), resultUserEntity.getEmail(), "L'email doit être le même");
    }
}