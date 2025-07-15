package inc.yowyob.rental_api_reactive.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Énumération des types d'organisations
 */
@Getter
public enum OrganizationType {
    /**
     * Entreprise de location de véhicules
     */
    @JsonProperty("CAR_RENTAL")
    CAR_RENTAL("CAR_RENTAL", "Location de véhicules", "Entreprise spécialisée dans la location de véhicules", "#3b82f6"),

    /**
     * Service de transport avec chauffeur
     */
    @JsonProperty("TRANSPORT_SERVICE")
    TRANSPORT_SERVICE("TRANSPORT_SERVICE", "Service de transport", "Service de transport avec chauffeur", "#10b981"),

    /**
     * Autopartage
     */
    @JsonProperty("CAR_SHARING")
    CAR_SHARING("CAR_SHARING", "Autopartage", "Service d'autopartage et de véhicules partagés", "#f59e0b"),

    /**
     * Flotte d'entreprise
     */
    @JsonProperty("FLEET_MANAGEMENT")
    FLEET_MANAGEMENT("FLEET_MANAGEMENT", "Gestion de flotte", "Gestion de flotte d'entreprise", "#8b5cf6"),

    /**
     * Location longue durée
     */
    @JsonProperty("LONG_TERM_RENTAL")
    LONG_TERM_RENTAL("LONG_TERM_RENTAL", "Location longue durée", "Location de véhicules longue durée", "#06b6d4"),

    /**
     * Service de livraison
     */
    @JsonProperty("DELIVERY_SERVICE")
    DELIVERY_SERVICE("DELIVERY_SERVICE", "Service de livraison", "Service de livraison et transport de marchandises", "#ef4444"),

    /**
     * Agence de voyage
     */
    @JsonProperty("TRAVEL_AGENCY")
    TRAVEL_AGENCY("TRAVEL_AGENCY", "Agence de voyage", "Agence de voyage avec services de transport", "#ec4899"),

    /**
     * Location d'équipements
     */
    @JsonProperty("EQUIPMENT_RENTAL")
    EQUIPMENT_RENTAL("EQUIPMENT_RENTAL", "Location d'équipements", "Location d'équipements et véhicules spécialisés", "#84cc16"),

    /**
     * Autre type d'organisation
     */
    @JsonProperty("OTHER")
    OTHER("OTHER", "Autre", "Autre type d'organisation", "#6b7280");

    private final String code;
    private final String displayName;
    private final String description;
    private final String color;

    OrganizationType(String code, String displayName, String description, String color) {
        this.code = code;
        this.displayName = displayName;
        this.description = description;
        this.color = color;
    }

    /**
     * Obtient le type d'organisation par son code
     */
    public static OrganizationType fromCode(String code) {
        for (OrganizationType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Type d'organisation invalide: " + code);
    }

    /**
     * Vérifie si le type est lié au transport
     */
    public boolean isTransportRelated() {
        return this == TRANSPORT_SERVICE ||
            this == CAR_SHARING ||
            this == DELIVERY_SERVICE ||
            this == TRAVEL_AGENCY;
    }

    /**
     * Vérifie si le type est lié à la location
     */
    public boolean isRentalRelated() {
        return this == CAR_RENTAL ||
            this == LONG_TERM_RENTAL ||
            this == EQUIPMENT_RENTAL ||
            this == CAR_SHARING;
    }

    /**
     * Vérifie si le type nécessite une gestion de flotte
     */
    public boolean requiresFleetManagement() {
        return this == FLEET_MANAGEMENT ||
            this == TRANSPORT_SERVICE ||
            this == DELIVERY_SERVICE;
    }

    /**
     * Obtient les limites par défaut selon le type d'organisation
     */
    public OrganizationLimits getDefaultLimits() {
        switch (this) {
            case CAR_RENTAL:
                return new OrganizationLimits(5, 50, 20, 15);
            case TRANSPORT_SERVICE:
                return new OrganizationLimits(3, 30, 50, 25);
            case CAR_SHARING:
                return new OrganizationLimits(10, 100, 10, 20);
            case FLEET_MANAGEMENT:
                return new OrganizationLimits(1, 200, 100, 50);
            case LONG_TERM_RENTAL:
                return new OrganizationLimits(3, 80, 15, 12);
            case DELIVERY_SERVICE:
                return new OrganizationLimits(5, 50, 100, 30);
            case TRAVEL_AGENCY:
                return new OrganizationLimits(2, 20, 30, 10);
            case EQUIPMENT_RENTAL:
                return new OrganizationLimits(3, 40, 10, 8);
            default:
                return new OrganizationLimits(1, 10, 5, 5);
        }
    }

    /**
     * Classe interne pour les limites d'organisation
     */
    public static class OrganizationLimits {
        private final int maxAgencies;
        private final int maxVehicles;
        private final int maxDrivers;
        private final int maxUsers;

        public OrganizationLimits(int maxAgencies, int maxVehicles, int maxDrivers, int maxUsers) {
            this.maxAgencies = maxAgencies;
            this.maxVehicles = maxVehicles;
            this.maxDrivers = maxDrivers;
            this.maxUsers = maxUsers;
        }

        public int getMaxAgencies() { return maxAgencies; }
        public int getMaxVehicles() { return maxVehicles; }
        public int getMaxDrivers() { return maxDrivers; }
        public int getMaxUsers() { return maxUsers; }
    }
}
