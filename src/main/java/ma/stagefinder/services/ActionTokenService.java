package ma.stagefinder.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.stagefinder.entities.ActionToken;
import ma.stagefinder.entities.User;
import ma.stagefinder.entities.enums.ActionTokenType;
import ma.stagefinder.repositories.ActionTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActionTokenService {

    private final ActionTokenRepository actionTokenRepository;

    /**
     * Generate email verification token for user
     */
    @Transactional
    public String generateEmailVerificationToken(User user) {
        // Delete any existing verification tokens for this user
        actionTokenRepository.deleteByUserAndType(user, ActionTokenType.EMAIL_VERIFICATION);

        // Generate new token
        String tokenString = UUID.randomUUID().toString();

        ActionToken token = ActionToken.builder()
                .token(tokenString)
                .type(ActionTokenType.EMAIL_VERIFICATION)
                .user(user)
                .expiresAt(LocalDateTime.now().plusHours(24)) // Expires in 24 hours
                .build();

        actionTokenRepository.save(token);
        log.info("Email verification token generated for user: {}", user.getEmail());

        return tokenString;
    }

    /**
     * Verify email verification token
     */
    @Transactional
    public boolean verifyEmailToken(String tokenString) {
        Optional<ActionToken> tokenOpt = actionTokenRepository
                .findByTokenAndType(tokenString, ActionTokenType.EMAIL_VERIFICATION);

        if (tokenOpt.isEmpty()) {
            log.warn("Invalid email verification token: {}", tokenString);
            return false;
        }

        ActionToken token = tokenOpt.get();

        // Check if token is expired
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Expired email verification token: {}", tokenString);
            actionTokenRepository.delete(token); // Clean up expired token
            return false;
        }

        // Token is valid - delete it after verification
        actionTokenRepository.delete(token);
        log.info("Email verification token verified successfully for user: {}",
                token.getUser().getEmail());

        return true;
    }

    /**
     * Get user by valid email verification token
     */
    public Optional<User> getUserByEmailVerificationToken(String tokenString) {
        Optional<ActionToken> tokenOpt = actionTokenRepository
                .findByTokenAndType(tokenString, ActionTokenType.EMAIL_VERIFICATION);

        if (tokenOpt.isEmpty()) {
            return Optional.empty();
        }

        ActionToken token = tokenOpt.get();

        // Check if token is expired
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            actionTokenRepository.delete(token); // Clean up expired token
            return Optional.empty();
        }

        return Optional.of(token.getUser());
    }

    /**
     * Clean up expired tokens (runs every hour)
     */
    @Scheduled(fixedRate = 3600000) // Every hour
    @Transactional
    public void cleanupExpiredTokens() {
        actionTokenRepository.deleteExpiredTokens(LocalDateTime.now());
        log.info("Cleaned up expired tokens");
    }
}