package inc.yowyob.rental_api_reactive.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum UserType {
    @JsonProperty("SUPER_ADMIN")
    SUPER_ADMIN("Super Administrateur", "Accès total au système", false),

    @JsonProperty("ORGANIZATION_OWNER")
    ORGANIZATION_OWNER("Propriétaire d'Organisation", "Propriétaire et administrateur d'une organisation", false),

    @JsonProperty("AGENCY_MANAGER")
    AGENCY_MANAGER("Gestionnaire d'Agence", "Responsable d'une agence au sein d'une organisation", true),

    @JsonProperty("RENTAL_AGENT")
    RENTAL_AGENT("Agent de Location", "Personnel chargé des locations et du service client", true),

    @JsonProperty("CLIENT")
    CLIENT("Client", "Client utilisateur des services de location", false);

    private final String displayName;
    private final String description;
    private final boolean isPersonnel;

    UserType(String displayName, String description, boolean isPersonnel) {
        this.displayName = displayName;
        this.description = description;
        this.isPersonnel = isPersonnel;
    }

    /**
     * Retourne le nom d'affichage du type d'utilisateur
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Retourne la description du type d'utilisateur
     */
    public String getDescription() {
        return description;
    }

    /**
     * Indique si ce type d'utilisateur est du personnel
     */
    public boolean isPersonnel() {
        return isPersonnel;
    }

    /**
     * Retourne tous les types de personnel
     */
    public static UserType[] getPersonnelTypes() {
        return new UserType[]{
            AGENCY_MANAGER,
            RENTAL_AGENT
        };
    }

    /**
     * Retourne tous les types d'administration
     */
    public static UserType[] getAdminTypes() {
        return new UserType[]{
            SUPER_ADMIN,
            ORGANIZATION_OWNER
        };
    }

    /**
     * Vérifie si le type est un type d'administration
     */
    public boolean isAdmin() {
        return this == SUPER_ADMIN || this == ORGANIZATION_OWNER;
    }

    /**
     * Vérifie si le type peut gérer une agence
     */
    public boolean canManageAgency() {
        return this == ORGANIZATION_OWNER || this == AGENCY_MANAGER;
    }

    /**
     * Vérifie si le type peut effectuer des locations
     */
    public boolean canHandleRentals() {
        return this == AGENCY_MANAGER || this == RENTAL_AGENT;
    }

    /**
     * Retourne le niveau hiérarchique (plus haut = plus de permissions)
     */
    public int getHierarchyLevel() {
        return switch (this) {
            case SUPER_ADMIN -> 100;
            case ORGANIZATION_OWNER -> 90;
            case AGENCY_MANAGER -> 70;
            case RENTAL_AGENT -> 50;
            case CLIENT -> 10;
        };
    }
}
