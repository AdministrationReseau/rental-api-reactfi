package inc.yowyob.rental_api_reactive.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Énumération des types d'utilisateurs dans le système
 */
@Getter
public enum UserType {
    /**
     * Super Administrateur - Gestionnaire de la plateforme
     */
    @JsonProperty("SUPER_ADMIN")
    SUPER_ADMIN("SUPER_ADMIN", "Super Administrateur", "Gestionnaire de la plateforme avec tous les privilèges", true, 100),

    /**
     * Propriétaire d'organisation - Créateur et gestionnaire d'organisation
     */
    @JsonProperty("ORGANIZATION_OWNER")
    ORGANIZATION_OWNER("ORGANIZATION_OWNER", "Propriétaire d'Organisation", "Propriétaire et gestionnaire principal de l'organisation", false, 90),

    /**
     * Administrateur d'organisation - Administrateur de l'organisation
     */
    @JsonProperty("ORGANIZATION_ADMIN")
    ORGANIZATION_ADMIN("ORGANIZATION_ADMIN", "Administrateur d'Organisation", "Administrateur avec droits étendus dans l'organisation", false, 80),

    /**
     * Manager d'agence - Gestionnaire d'agence
     */
    @JsonProperty("AGENCY_MANAGER")
    AGENCY_MANAGER("AGENCY_MANAGER", "Manager d'Agence", "Gestionnaire responsable d'une agence", false, 70),

    /**
     * Comptable - Gestionnaire financier
     */
    @JsonProperty("ACCOUNTANT")
    ACCOUNTANT("ACCOUNTANT", "Comptable", "Responsable de la gestion financière et comptable", false, 60),

    /**
     * Gestionnaire de paiements - Responsable des paiements
     */
    @JsonProperty("PAYMENT_MANAGER")
    PAYMENT_MANAGER("PAYMENT_MANAGER", "Gestionnaire de Paiements", "Responsable du traitement des paiements", false, 55),

    /**
     * Agent de location - Personnel de location
     */
    @JsonProperty("RENTAL_AGENT")
    RENTAL_AGENT("RENTAL_AGENT", "Agent de Location", "Agent responsable des locations et du service client", false, 50),

    /**
     * Mécanicien - Responsable de la maintenance
     */
    @JsonProperty("MECHANIC")
    MECHANIC("MECHANIC", "Mécanicien", "Responsable de la maintenance et réparation des véhicules", false, 40),

    /**
     * Chauffeur - Conducteur de véhicules
     */
    @JsonProperty("DRIVER")
    DRIVER("DRIVER", "Chauffeur", "Chauffeur professionnel assigné aux missions", false, 30),

    /**
     * Client - Utilisateur final
     */
    @JsonProperty("CLIENT")
    CLIENT("CLIENT", "Client", "Utilisateur final qui loue des véhicules", false, 10);

    private final String code;
    private final String displayName;
    private final String description;
    private final boolean isSystemAdmin;
    private final int hierarchyLevel;

    UserType(String code, String displayName, String description, boolean isSystemAdmin, int hierarchyLevel) {
        this.code = code;
        this.displayName = displayName;
        this.description = description;
        this.isSystemAdmin = isSystemAdmin;
        this.hierarchyLevel = hierarchyLevel;
    }

    /**
     * Obtient le niveau hiérarchique de ce type d'utilisateur
     * Plus le niveau est élevé, plus l'utilisateur a d'autorité
     */
    public int getHierarchyLevel() {
        return this.hierarchyLevel;
    }

    /**
     * Vérifie si ce type d'utilisateur est un administrateur système
     */
    public boolean isSystemAdmin() {
        return this.isSystemAdmin;
    }

    /**
     * Vérifie si ce type d'utilisateur est du personnel
     */
    public boolean isPersonnel() {
        return this == AGENCY_MANAGER || this == RENTAL_AGENT || this == DRIVER ||
            this == MECHANIC || this == ACCOUNTANT || this == PAYMENT_MANAGER;
    }

    /**
     * Vérifie si ce type d'utilisateur est un gestionnaire
     */
    public boolean isManager() {
        return this == ORGANIZATION_OWNER || this == ORGANIZATION_ADMIN || this == AGENCY_MANAGER;
    }

    /**
     * Vérifie si ce type d'utilisateur peut gérer d'autres utilisateurs
     */
    public boolean canManageUsers() {
        return this == SUPER_ADMIN || this == ORGANIZATION_OWNER ||
            this == ORGANIZATION_ADMIN || this == AGENCY_MANAGER;
    }

    /**
     * Vérifie si ce type d'utilisateur nécessite une agence assignée
     */
    public boolean requiresAgency() {
        return this == AGENCY_MANAGER || this == RENTAL_AGENT || this == DRIVER || this == MECHANIC;
    }

    /**
     * Vérifie si ce type d'utilisateur peut créer des organisations
     */
    public boolean canCreateOrganizations() {
        return this == SUPER_ADMIN || this == ORGANIZATION_OWNER;
    }

    /**
     * Vérifie si ce type d'utilisateur peut accéder aux rapports financiers
     */
    public boolean canAccessFinancialReports() {
        return this == SUPER_ADMIN || this == ORGANIZATION_OWNER ||
            this == ORGANIZATION_ADMIN || this == ACCOUNTANT;
    }

    /**
     * Vérifie si ce type d'utilisateur peut traiter les paiements
     */
    public boolean canProcessPayments() {
        return this == SUPER_ADMIN || this == ORGANIZATION_OWNER || this == ORGANIZATION_ADMIN ||
            this == AGENCY_MANAGER || this == RENTAL_AGENT || this == ACCOUNTANT || this == PAYMENT_MANAGER;
    }

    /**
     * Vérifie si ce type d'utilisateur est supérieur hiérarchiquement à un autre
     */
    public boolean isSuperiorTo(UserType otherType) {
        return this.hierarchyLevel > otherType.hierarchyLevel;
    }

    /**
     * Vérifie si ce type d'utilisateur est inférieur hiérarchiquement à un autre
     */
    public boolean isInferiorTo(UserType otherType) {
        return this.hierarchyLevel < otherType.hierarchyLevel;
    }

    /**
     * Vérifie si ce type d'utilisateur est au même niveau hiérarchique qu'un autre
     */
    public boolean isSameLevelAs(UserType otherType) {
        return this.hierarchyLevel == otherType.hierarchyLevel;
    }

    /**
     * Vérifie si ce type d'utilisateur peut gérer un autre type d'utilisateur
     */
    public boolean canManage(UserType otherType) {
        // SUPER_ADMIN peut gérer tout le monde
        if (this == SUPER_ADMIN) {
            return true;
        }

        // Personne ne peut gérer SUPER_ADMIN sauf SUPER_ADMIN lui-même
        if (otherType == SUPER_ADMIN) {
            return false;
        }

        // Sinon, on peut gérer les types de niveau inférieur
        return this.isSuperiorTo(otherType);
    }

    /**
     * Obtient le RoleType correspondant à ce UserType
     */
    public RoleType getCorrespondingRoleType() {
        return switch (this) {
            case SUPER_ADMIN -> RoleType.SUPER_ADMIN;
            case ORGANIZATION_OWNER -> RoleType.ORGANIZATION_OWNER;
            case ORGANIZATION_ADMIN -> RoleType.ORGANIZATION_ADMIN;
            case AGENCY_MANAGER -> RoleType.AGENCY_MANAGER;
            case RENTAL_AGENT -> RoleType.RENTAL_AGENT;
            case DRIVER -> RoleType.DRIVER;
            case ACCOUNTANT -> RoleType.ACCOUNTANT;
            case PAYMENT_MANAGER -> RoleType.PAYMENT_MANAGER;
            case MECHANIC -> RoleType.MECHANIC;
            case CLIENT -> RoleType.CLIENT;
        };
    }

    /**
     * Trouve un type d'utilisateur par son code
     */
    public static UserType fromCode(String code) {
        for (UserType userType : UserType.values()) {
            if (userType.getCode().equals(code)) {
                return userType;
            }
        }
        return null;
    }

    /**
     * Trouve un type d'utilisateur par son niveau hiérarchique
     */
    public static UserType fromHierarchyLevel(int level) {
        for (UserType userType : UserType.values()) {
            if (userType.getHierarchyLevel() == level) {
                return userType;
            }
        }
        return null;
    }

    /**
     * Obtient tous les types d'utilisateurs personnel
     */
    public static UserType[] getPersonnelTypes() {
        return java.util.Arrays.stream(UserType.values())
            .filter(UserType::isPersonnel)
            .toArray(UserType[]::new);
    }

    /**
     * Obtient tous les types d'utilisateurs gestionnaires
     */
    public static UserType[] getManagerTypes() {
        return java.util.Arrays.stream(UserType.values())
            .filter(UserType::isManager)
            .toArray(UserType[]::new);
    }

    /**
     * Obtient tous les types d'utilisateurs qui nécessitent une agence
     */
    public static UserType[] getAgencyRequiredTypes() {
        return java.util.Arrays.stream(UserType.values())
            .filter(UserType::requiresAgency)
            .toArray(UserType[]::new);
    }

    /**
     * Obtient tous les types d'utilisateurs triés par niveau hiérarchique (décroissant)
     */
    public static UserType[] getByHierarchyDescending() {
        return java.util.Arrays.stream(UserType.values())
            .sorted((a, b) -> Integer.compare(b.hierarchyLevel, a.hierarchyLevel))
            .toArray(UserType[]::new);
    }

    /**
     * Obtient tous les types d'utilisateurs triés par niveau hiérarchique (croissant)
     */
    public static UserType[] getByHierarchyAscending() {
        return java.util.Arrays.stream(UserType.values())
            .sorted((a, b) -> Integer.compare(a.hierarchyLevel, b.hierarchyLevel))
            .toArray(UserType[]::new);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
