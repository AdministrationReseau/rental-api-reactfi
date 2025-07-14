package inc.yowyob.rental_api_reactive.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Énumération des types de rôles dans le système
 */
@Getter
public enum RoleType {
    /**
     * Super Administrateur - Accès complet à toute la plateforme
     */
    @JsonProperty("SUPER_ADMIN")
    SUPER_ADMIN("SUPER_ADMIN", "Super Administrateur", "Administrateur système avec accès complet à toute la plateforme", 100, true, "#dc2626", "crown"),

    /**
     * Propriétaire d'organisation - Accès complet à son organisation
     */
    @JsonProperty("ORGANIZATION_OWNER")
    ORGANIZATION_OWNER("ORGANIZATION_OWNER", "Propriétaire d'Organisation", "Propriétaire de l'organisation avec droits complets sur son organisation", 90, false, "#7c3aed", "building"),

    /**
     * Administrateur d'organisation - Gestion administrative de l'organisation
     */
    @JsonProperty("ORGANIZATION_ADMIN")
    ORGANIZATION_ADMIN("ORGANIZATION_ADMIN", "Administrateur d'Organisation", "Administrateur de l'organisation avec droits étendus", 80, false, "#059669", "settings"),

    /**
     * Manager d'agence - Gestion complète de son agence
     */
    @JsonProperty("AGENCY_MANAGER")
    AGENCY_MANAGER("AGENCY_MANAGER", "Manager d'Agence", "Gestionnaire d'agence avec droits complets sur son agence", 70, false, "#2563eb", "home"),

    /**
     * Agent de location - Gestion des locations et service client
     */
    @JsonProperty("RENTAL_AGENT")
    RENTAL_AGENT("RENTAL_AGENT", "Agent de Location", "Agent de location responsable des réservations et du service client", 50, false, "#ea580c", "user-check"),

    /**
     * Chauffeur - Accès aux missions assignées
     */
    @JsonProperty("DRIVER")
    DRIVER("DRIVER", "Chauffeur", "Chauffeur avec accès aux missions et planning", 30, false, "#0891b2", "car"),

    /**
     * Comptable - Gestion financière et paiements
     */
    @JsonProperty("ACCOUNTANT")
    ACCOUNTANT("ACCOUNTANT", "Comptable", "Comptable responsable de la gestion financière", 60, false, "#16a34a", "calculator"),

    /**
     * Gestionnaire de paiements - Traitement des paiements
     */
    @JsonProperty("PAYMENT_MANAGER")
    PAYMENT_MANAGER("PAYMENT_MANAGER", "Gestionnaire de Paiements", "Gestionnaire responsable du traitement des paiements", 55, false, "#ca8a04", "credit-card"),

    /**
     * Mécanicien - Maintenance des véhicules
     */
    @JsonProperty("MECHANIC")
    MECHANIC("MECHANIC", "Mécanicien", "Mécanicien responsable de la maintenance des véhicules", 40, false, "#dc2626", "wrench"),

    /**
     * Client - Utilisateur final qui loue des véhicules
     */
    @JsonProperty("CLIENT")
    CLIENT("CLIENT", "Client", "Utilisateur client avec accès aux fonctionnalités de location", 10, true, "#059669", "user"),

    /**
     * Rôle personnalisé - Créé par les organisations
     */
    @JsonProperty("CUSTOM")
    CUSTOM("CUSTOM", "Rôle Personnalisé", "Rôle personnalisé créé par l'organisation", 0, false, "#6b7280", "shield");

    private final String code;
    private final String displayName;
    private final String description;
    private final int priority;
    private final boolean isSystemRole;
    private final String defaultColor;
    private final String defaultIcon;

    RoleType(String code, String displayName, String description, int priority, boolean isSystemRole, String defaultColor, String defaultIcon) {
        this.code = code;
        this.displayName = displayName;
        this.description = description;
        this.priority = priority;
        this.isSystemRole = isSystemRole;
        this.defaultColor = defaultColor;
        this.defaultIcon = defaultIcon;
    }

    /**
     * Vérifie si ce type de rôle est un rôle système
     */
    public boolean isSystemRole() {
        return this.isSystemRole;
    }

    /**
     * Vérifie si ce type de rôle peut créer des rôles personnalisés
     */
    public boolean canCreateCustomRoles() {
        return this == SUPER_ADMIN || this == ORGANIZATION_OWNER || this == ORGANIZATION_ADMIN;
    }

    /**
     * Vérifie si ce type de rôle peut gérer les utilisateurs
     */
    public boolean canManageUsers() {
        return this == SUPER_ADMIN || this == ORGANIZATION_OWNER || this == ORGANIZATION_ADMIN || this == AGENCY_MANAGER;
    }

    /**
     * Vérifie si ce type de rôle peut gérer les agences
     */
    public boolean canManageAgencies() {
        return this == SUPER_ADMIN || this == ORGANIZATION_OWNER || this == ORGANIZATION_ADMIN;
    }

    /**
     * Vérifie si ce type de rôle peut gérer les véhicules
     */
    public boolean canManageVehicles() {
        return this == SUPER_ADMIN || this == ORGANIZATION_OWNER || this == ORGANIZATION_ADMIN || this == AGENCY_MANAGER;
    }

    /**
     * Vérifie si ce type de rôle peut traiter les locations
     */
    public boolean canProcessRentals() {
        return this == SUPER_ADMIN || this == ORGANIZATION_OWNER || this == ORGANIZATION_ADMIN ||
            this == AGENCY_MANAGER || this == RENTAL_AGENT;
    }

    /**
     * Vérifie si ce type de rôle peut gérer les paiements
     */
    public boolean canManagePayments() {
        return this == SUPER_ADMIN || this == ORGANIZATION_OWNER || this == ORGANIZATION_ADMIN ||
            this == AGENCY_MANAGER || this == ACCOUNTANT || this == PAYMENT_MANAGER;
    }

    /**
     * Vérifie si ce type de rôle peut accéder aux rapports
     */
    public boolean canAccessReports() {
        return this == SUPER_ADMIN || this == ORGANIZATION_OWNER || this == ORGANIZATION_ADMIN ||
            this == AGENCY_MANAGER || this == ACCOUNTANT;
    }

    /**
     * Vérifie si ce type de rôle nécessite une agence assignée
     */
    public boolean requiresAgency() {
        return this == AGENCY_MANAGER || this == RENTAL_AGENT || this == DRIVER || this == MECHANIC;
    }

    /**
     * Vérifie si ce type de rôle est considéré comme du personnel
     */
    public boolean isPersonnel() {
        return this == AGENCY_MANAGER || this == RENTAL_AGENT || this == DRIVER ||
            this == MECHANIC || this == ACCOUNTANT || this == PAYMENT_MANAGER;
    }

    /**
     * Vérifie si ce type de rôle peut être assigné à des utilisateurs externes
     */
    public boolean canBeAssignedExternally() {
        return this != SUPER_ADMIN && this != CUSTOM;
    }

    /**
     * Obtient la hiérarchie des rôles supérieurs
     */
    public RoleType[] getSuperiorRoles() {
        return switch (this) {
            case SUPER_ADMIN -> new RoleType[]{};
            case ORGANIZATION_OWNER -> new RoleType[]{SUPER_ADMIN};
            case ORGANIZATION_ADMIN -> new RoleType[]{SUPER_ADMIN, ORGANIZATION_OWNER};
            case AGENCY_MANAGER -> new RoleType[]{SUPER_ADMIN, ORGANIZATION_OWNER, ORGANIZATION_ADMIN};
            case ACCOUNTANT, PAYMENT_MANAGER -> new RoleType[]{SUPER_ADMIN, ORGANIZATION_OWNER, ORGANIZATION_ADMIN};
            case RENTAL_AGENT -> new RoleType[]{SUPER_ADMIN, ORGANIZATION_OWNER, ORGANIZATION_ADMIN, AGENCY_MANAGER};
            case MECHANIC -> new RoleType[]{SUPER_ADMIN, ORGANIZATION_OWNER, ORGANIZATION_ADMIN, AGENCY_MANAGER};
            case DRIVER -> new RoleType[]{SUPER_ADMIN, ORGANIZATION_OWNER, ORGANIZATION_ADMIN, AGENCY_MANAGER, RENTAL_AGENT};
            case CLIENT -> new RoleType[]{SUPER_ADMIN, ORGANIZATION_OWNER, ORGANIZATION_ADMIN, AGENCY_MANAGER, RENTAL_AGENT};
            case CUSTOM -> new RoleType[]{SUPER_ADMIN, ORGANIZATION_OWNER, ORGANIZATION_ADMIN};
        };
    }

    /**
     * Vérifie si ce rôle est supérieur à un autre rôle
     */
    public boolean isSuperiorTo(RoleType otherRole) {
        return this.priority > otherRole.priority;
    }

    /**
     * Vérifie si ce rôle est inférieur à un autre rôle
     */
    public boolean isInferiorTo(RoleType otherRole) {
        return this.priority < otherRole.priority;
    }

    /**
     * Vérifie si ce rôle a le même niveau qu'un autre rôle
     */
    public boolean isSameLevelAs(RoleType otherRole) {
        return this.priority == otherRole.priority;
    }

    /**
     * Trouve un type de rôle par son code
     */
    public static RoleType fromCode(String code) {
        for (RoleType roleType : RoleType.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    /**
     * Obtient tous les types de rôles système
     */
    public static RoleType[] getSystemRoles() {
        return java.util.Arrays.stream(RoleType.values())
            .filter(RoleType::isSystemRole)
            .toArray(RoleType[]::new);
    }

    /**
     * Obtient tous les types de rôles organisationnels (non-système)
     */
    public static RoleType[] getOrganizationalRoles() {
        return java.util.Arrays.stream(RoleType.values())
            .filter(roleType -> !roleType.isSystemRole() && roleType != CUSTOM)
            .toArray(RoleType[]::new);
    }

    /**
     * Obtient tous les types de rôles de personnel
     */
    public static RoleType[] getPersonnelRoles() {
        return java.util.Arrays.stream(RoleType.values())
            .filter(RoleType::isPersonnel)
            .toArray(RoleType[]::new);
    }

    /**
     * Obtient la description complète du rôle
     */
    public String getFullDescription() {
        return String.format("%s - %s (Priorité: %d)", displayName, description, priority);
    }

    /**
     * Vérifie si ce rôle peut modifier un autre rôle
     */
    public boolean canModifyRole(RoleType targetRole) {
        // Les rôles système ne peuvent pas être modifiés sauf par SUPER_ADMIN
        if (targetRole.isSystemRole() && this != SUPER_ADMIN) {
            return false;
        }

        // Un rôle peut modifier les rôles de priorité inférieure
        return this.isSuperiorTo(targetRole);
    }

    /**
     * Vérifie si ce rôle peut assigner un autre rôle
     */
    public boolean canAssignRole(RoleType targetRole) {
        // SUPER_ADMIN peut assigner tous les rôles
        if (this == SUPER_ADMIN) {
            return true;
        }

        // Les autres rôles ne peuvent pas assigner SUPER_ADMIN
        if (targetRole == SUPER_ADMIN) {
            return false;
        }

        // Un rôle peut assigner des rôles de priorité inférieure ou égale (sauf pour lui-même)
        return this.priority >= targetRole.priority && this != targetRole;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
