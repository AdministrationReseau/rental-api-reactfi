package inc.yowyob.rental_api_reactive.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DriverStatus {
    @JsonProperty("AVAILABLE")
    AVAILABLE,

    @JsonProperty("ON_DUTY")
    ON_DUTY,

    @JsonProperty("OFF_DUTY")
    OFF_DUTY,

    @JsonProperty("ON_LEAVE")
    ON_LEAVE;

    /** Vérifie si le chauffeur peut être assigné à une mission
     */

    public boolean isAssignable() {
        return this == AVAILABLE;
    }

    /**
     * Vérifie si le chauffeur est en service (actif)
     */
    public boolean isOnDuty() {
        return this == ON_DUTY || this == AVAILABLE;
    }

    /**
     * Vérifie si le chauffeur est disponible pour le travail
     */
    public boolean isWorkReady() {
        return this == AVAILABLE || this == ON_DUTY;
    }

    /**
     * Obtient les statuts qui permettent la transition vers le statut cible
     */
    public static boolean canTransitionTo(DriverStatus from, DriverStatus to) {
        if (from == to) return true;
        
        switch (to) {
            case AVAILABLE:
                return from == ON_DUTY; // Passer de en service à disponible
            case ON_DUTY:
                return from == AVAILABLE || from == OFF_DUTY; // Prendre le service
            case OFF_DUTY:
                return from == ON_DUTY || from == AVAILABLE || from == ON_LEAVE; // Terminer le service
            case ON_LEAVE:
                return from == OFF_DUTY; // Partir en congé uniquement si hors service
            default:
                return false;
        }
    }

    /**
     * Retourne la description du statut
     */
    public String getDescription() {
        switch (this) {
            case AVAILABLE:
                return "Chauffeur disponible pour une mission";
            case ON_DUTY:
                return "Chauffeur en service";
            case OFF_DUTY:
                return "Chauffeur hors service";
            case ON_LEAVE:
                return "Chauffeur en congé";
            default:
                return this.name();
        }
    }
}