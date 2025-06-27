package ma.stagefinder.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception personnalisée levée lorsqu'un utilisateur avec un abonnement GRATUIT
 * tente de postuler à une offre premium (pré-embauche).
 * L'annotation @ResponseStatus(HttpStatus.FORBIDDEN) indique à Spring
 * de retourner automatiquement un code de statut 403 Forbidden.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class PremiumOfferException extends RuntimeException {

    public PremiumOfferException(String message) {
        super(message);
    }
}
