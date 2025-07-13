package inc.yowyob.rental_api_reactive.application.service;

import inc.yowyob.rental_api_reactive.infrastructure.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.util.regex.Pattern;

/**
 * Service réactif pour la gestion des mots de passe
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordReactiveService {

    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;
    private final SecureRandom secureRandom = new SecureRandom();

    // Pattern pour validation du mot de passe
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );

    /**
     * Valide un mot de passe selon les critères de sécurité
     */
    public Mono<Boolean> validatePassword(String password) {
        return Mono.fromCallable(() -> {
                if (password == null || password.length() < appProperties.getSecurity().getPasswordMinLength()) {
                    return false;
                }

                // Vérification des critères de complexité
                return PASSWORD_PATTERN.matcher(password).matches();
            })
            .doOnNext(isValid -> {
                if (!isValid) {
                    log.debug("Password validation failed");
                }
            });
    }

    /**
     * Encode un mot de passe
     */
    public Mono<String> encodePassword(String rawPassword) {
        return validatePassword(rawPassword)
            .flatMap(isValid -> {
                if (!isValid) {
                    return Mono.error(new IllegalArgumentException(
                        "Password must be at least " + appProperties.getSecurity().getPasswordMinLength() +
                            " characters long and contain at least one uppercase letter, one lowercase letter, " +
                            "one digit, and one special character"));
                }

                return Mono.fromCallable(() -> passwordEncoder.encode(rawPassword));
            });
    }

    /**
     * Vérifie si un mot de passe correspond au hash
     */
    public Mono<Boolean> matches(String rawPassword, String encodedPassword) {
        return Mono.fromCallable(() -> passwordEncoder.matches(rawPassword, encodedPassword));
    }

    /**
     * Génère un mot de passe temporaire sécurisé
     */
    public Mono<String> generateTemporaryPassword() {
        return Mono.fromCallable(() -> {
            String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@$!%*?&";
            StringBuilder password = new StringBuilder();

            // S'assurer qu'on a au moins un caractère de chaque type requis
            password.append(getRandomChar("ABCDEFGHIJKLMNOPQRSTUVWXYZ")); // Majuscule
            password.append(getRandomChar("abcdefghijklmnopqrstuvwxyz")); // Minuscule
            password.append(getRandomChar("0123456789")); // Chiffre
            password.append(getRandomChar("@$!%*?&")); // Caractère spécial

            // Compléter avec des caractères aléatoires
            for (int i = 4; i < 12; i++) {
                password.append(chars.charAt(secureRandom.nextInt(chars.length())));
            }

            // Mélanger les caractères
            return shuffleString(password.toString());
        });
    }

    /**
     * Génère un token de réinitialisation
     */
    public Mono<String> generateResetToken() {
        return Mono.fromCallable(() -> {
            byte[] token = new byte[32];
            secureRandom.nextBytes(token);
            return bytesToHex(token);
        });
    }

    /**
     * Vérifie la force d'un mot de passe
     */
    public Mono<PasswordStrength> checkPasswordStrength(String password) {
        return Mono.fromCallable(() -> {
            if (password == null || password.isEmpty()) {
                return PasswordStrength.VERY_WEAK;
            }

            int score = 0;

            // Longueur
            if (password.length() >= 8) score++;
            if (password.length() >= 12) score++;

            // Complexité
            if (password.matches(".*[a-z].*")) score++;
            if (password.matches(".*[A-Z].*")) score++;
            if (password.matches(".*\\d.*")) score++;
            if (password.matches(".*[@$!%*?&].*")) score++;

            // Pas de répétitions
            if (!password.matches(".*(.)\\1{2,}.*")) score++;

            return switch (score) {
                case 0, 1, 2 -> PasswordStrength.VERY_WEAK;
                case 3, 4 -> PasswordStrength.WEAK;
                case 5 -> PasswordStrength.MEDIUM;
                case 6 -> PasswordStrength.STRONG;
                default -> PasswordStrength.VERY_STRONG;
            };
        });
    }

    private char getRandomChar(String chars) {
        return chars.charAt(secureRandom.nextInt(chars.length()));
    }

    private String shuffleString(String input) {
        char[] chars = input.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = secureRandom.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    public enum PasswordStrength {
        VERY_WEAK,
        WEAK,
        MEDIUM,
        STRONG,
        VERY_STRONG
    }
}
