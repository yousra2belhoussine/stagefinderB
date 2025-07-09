package ma.stagefinder.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

import java.time.Duration;

@Configuration
public class RedisConfig {

    /**
     * Configure la manière dont les objets seront stockés dans Redis.
     * On utilise le format JSON avec support pour les types Java 8 date/time.
     */
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        // Créer un ObjectMapper personnalisé avec le support des types Java 8 date/time
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Créer le sérialiseur JSON avec notre ObjectMapper configuré
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        return RedisCacheConfiguration.defaultCacheConfig()
                // Durée de vie par défaut pour les entrées du cache
                .entryTtl(Duration.ofMinutes(60))
                // Utiliser notre sérialiseur personnalisé
                .serializeValuesWith(SerializationPair.fromSerializer(serializer));
    }
}