package ma.stagefinder.config; // Assurez-vous que le nom du package est correct

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

import java.time.Duration;

@Configuration
@EnableCaching // On met l'annotation ici maintenant
public class RedisConfig  {

    /**
     * Configure la manière dont les objets seront stockés dans Redis.
     * On utilise le format JSON, ce qui est plus flexible et standard que la sérialisation Java.
     * On définit aussi une durée de vie par défaut pour les entrées du cache.
     */
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(60)) // Le cache expirera après 60 minutes par défaut
                .disableCachingNullValues()
                .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    /**
     * Personnalise le gestionnaire de cache pour des caches spécifiques.
     * Ici, on dit que le cache nommé "categories" a une durée de vie de 1 heure.
     */
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
                .withCacheConfiguration("categories", // Le nom que vous avez mis dans @Cacheable("categories")
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1)));
        // Vous pouvez ajouter d'autres .withCacheConfiguration("autreNom", ...) ici
    }
}