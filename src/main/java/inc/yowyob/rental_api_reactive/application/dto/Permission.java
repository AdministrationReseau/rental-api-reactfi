package inc.yowyob.rental_api_reactive.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum Permission {
    // === PERMISSIONS VÉHICULES ===
    @JsonProperty("vehicle_read")
    VEHICLE_READ("vehicle_read", "Lire les informations des véhicules", "VEHICLE"),
    @JsonProperty("vehicle_write")
    VEHICLE_WRITE("vehicle_write", "Créer de nouveaux véhicules", "VEHICLE"),
    @JsonProperty("vehicle_update")
    VEHICLE_UPDATE("vehicle_update", "Modifier les véhicules existants", "VEHICLE"),
    @JsonProperty("vehicle_delete")
    VEHICLE_DELETE("vehicle_delete", "Supprimer des véhicules", "VEHICLE"),
    @JsonProperty("vehicle_manage_images")
    VEHICLE_MANAGE_IMAGES("vehicle_manage_images", "Gérer les images des véhicules", "VEHICLE"),
    @JsonProperty("vehicle_change_status")
    VEHICLE_CHANGE_STATUS("vehicle_change_status", "Changer le statut des véhicules", "VEHICLE"),

    // === PERMISSIONS CHAUFFEURS ===
    @JsonProperty("driver_read")
    DRIVER_READ("driver_read", "Lire les informations des chauffeurs", "DRIVER"),
    @JsonProperty("driver_write")
    DRIVER_WRITE("driver_write", "Créer de nouveaux chauffeurs", "DRIVER"),
    @JsonProperty("driver_update")
    DRIVER_UPDATE("driver_update", "Modifier les chauffeurs existants", "DRIVER"),
    @JsonProperty("driver_delete")
    DRIVER_DELETE("driver_delete", "Supprimer des chauffeurs", "DRIVER"),
    @JsonProperty("driver_manage_documents")
    DRIVER_MANAGE_DOCUMENTS("driver_manage_documents", "Gérer les documents des chauffeurs", "DRIVER"),
    @JsonProperty("driver_manage_schedule")
    DRIVER_MANAGE_SCHEDULE("driver_manage_schedule", "Gérer les plannings des chauffeurs", "DRIVER"),

    // === PERMISSIONS LOCATIONS ===
    @JsonProperty("rental_read")
    RENTAL_READ("rental_read", "Lire les informations des locations", "RENTAL"),
    @JsonProperty("rental_write")
    RENTAL_WRITE("rental_write", "Créer de nouvelles locations", "RENTAL"),
    @JsonProperty("rental_update")
    RENTAL_UPDATE("rental_update", "Modifier les locations existantes", "RENTAL"),
    @JsonProperty("rental_delete")
    RENTAL_DELETE("rental_delete", "Supprimer des locations", "RENTAL"),
    @JsonProperty("rental_approve")
    RENTAL_APPROVE("rental_approve", "Approuver les demandes de location", "RENTAL"),
    @JsonProperty("rental_cancel")
    RENTAL_CANCEL("rental_cancel", "Annuler des locations", "RENTAL"),
    @JsonProperty("rental_extend")
    RENTAL_EXTEND("rental_extend", "Prolonger des locations", "RENTAL"),

    // === PERMISSIONS UTILISATEURS ===
    @JsonProperty("user_read")
    USER_READ("user_read", "Lire les informations des utilisateurs", "USER"),
    @JsonProperty("user_write")
    USER_WRITE("user_write", "Créer de nouveaux utilisateurs", "USER"),
    @JsonProperty("user_update")
    USER_UPDATE("user_update", "Modifier les utilisateurs existants", "USER"),
    @JsonProperty("user_delete")
    USER_DELETE("user_delete", "Supprimer des utilisateurs", "USER"),
    @JsonProperty("user_manage_roles")
    USER_MANAGE_ROLES("user_manage_roles", "Gérer les rôles des utilisateurs", "USER"),
    @JsonProperty("user_reset_password")
    USER_RESET_PASSWORD("user_reset_password", "Réinitialiser les mots de passe", "USER"),

    // === PERMISSIONS AGENCES ===
    @JsonProperty("agency_read")
    AGENCY_READ("agency_read", "Lire les informations des agences", "AGENCY"),
    @JsonProperty("agency_write")
    AGENCY_WRITE("agency_write", "Créer de nouvelles agences", "AGENCY"),
    @JsonProperty("agency_update")
    AGENCY_UPDATE("agency_update", "Modifier les agences existantes", "AGENCY"),
    @JsonProperty("agency_delete")
    AGENCY_DELETE("agency_delete", "Supprimer des agences", "AGENCY"),
    @JsonProperty("agency_manage_staff")
    AGENCY_MANAGE_STAFF("agency_manage_staff", "Gérer le personnel des agences", "AGENCY"),

    // === PERMISSIONS ORGANISATIONS ===
    @JsonProperty("organization_read")
    ORGANIZATION_READ("organization_read", "Lire les informations de l'organisation", "ORGANIZATION"),
    @JsonProperty("organization_write")
    ORGANIZATION_WRITE("organization_write", "Créer de nouvelles organisations", "ORGANIZATION"),
    @JsonProperty("organization_update")
    ORGANIZATION_UPDATE("organization_update", "Modifier les informations de l'organisation", "ORGANIZATION"),
    @JsonProperty("organization_delete")
    ORGANIZATION_DELETE("organization_delete", "Supprimer des organisations", "ORGANIZATION"),
    @JsonProperty("organization_manage_settings")
    ORGANIZATION_MANAGE_SETTINGS("organization_manage_settings", "Gérer les paramètres de l'organisation", "ORGANIZATION"),
    @JsonProperty("organization_manage_subscription")
    ORGANIZATION_MANAGE_SUBSCRIPTION("organization_manage_subscription", "Gérer l'abonnement de l'organisation", "ORGANIZATION"),

    // === PERMISSIONS RÔLES ===
    @JsonProperty("role_read")
    ROLE_READ("role_read", "Lire les informations des rôles", "ROLE"),
    @JsonProperty("role_write")
    ROLE_WRITE("role_write", "Créer de nouveaux rôles", "ROLE"),
    @JsonProperty("role_update")
    ROLE_UPDATE("role_update", "Modifier les rôles existants", "ROLE"),
    @JsonProperty("role_delete")
    ROLE_DELETE("role_delete", "Supprimer des rôles", "ROLE"),
    @JsonProperty("role_assign_permissions")
    ROLE_ASSIGN_PERMISSIONS("role_assign_permissions", "Assigner des permissions aux rôles", "ROLE"),

    // === PERMISSIONS PAIEMENTS ===
    @JsonProperty("payment_read")
    PAYMENT_READ("payment_read", "Lire les informations des paiements", "PAYMENT"),
    @JsonProperty("payment_process")
    PAYMENT_PROCESS("payment_process", "Traiter les paiements", "PAYMENT"),
    @JsonProperty("payment_refund")
    PAYMENT_REFUND("payment_refund", "Effectuer des remboursements", "PAYMENT"),
    @JsonProperty("payment_view_details")
    PAYMENT_VIEW_DETAILS("payment_view_details", "Voir les détails des transactions", "PAYMENT"),

    // === PERMISSIONS RAPPORTS ===
    @JsonProperty("report_read")
    REPORT_READ("report_read", "Lire les rapports", "REPORT"),
    @JsonProperty("report_generate")
    REPORT_GENERATE("report_generate", "Générer des rapports", "REPORT"),
    @JsonProperty("report_export")
    REPORT_EXPORT("report_export", "Exporter des rapports", "REPORT"),
    @JsonProperty("report_advanced")
    REPORT_ADVANCED("report_advanced", "Accéder aux rapports avancés", "REPORT"),

    // === PERMISSIONS PARAMÈTRES ===
    @JsonProperty("settings_read")
    SETTINGS_READ("settings_read", "Lire les paramètres", "SETTINGS"),
    @JsonProperty("settings_write")
    SETTINGS_WRITE("settings_write", "Modifier les paramètres", "SETTINGS"),
    @JsonProperty("settings_manage_notifications")
    SETTINGS_MANAGE_NOTIFICATIONS("settings_manage_notifications", "Gérer les notifications", "SETTINGS"),

    // === PERMISSIONS SYSTÈME ===
    @JsonProperty("system_admin")
    SYSTEM_ADMIN("system_admin", "Administration complète du système", "SYSTEM"),
    @JsonProperty("system_backup")
    SYSTEM_BACKUP("system_backup", "Effectuer des sauvegardes", "SYSTEM"),
    @JsonProperty("system_logs")
    SYSTEM_LOGS("system_logs", "Accéder aux logs système", "SYSTEM"),
    @JsonProperty("system_monitoring")
    SYSTEM_MONITORING("system_monitoring", "Accéder au monitoring", "SYSTEM");

    private final String code;
    private final String description;
    private final String resource;

    Permission(String code, String description, String resource) {
        this.code = code;
        this.description = description;
        this.resource = resource;
    }

    /**
     * Vérifie si la permission concerne une ressource donnée
     */
    public boolean isForResource(String resourceName) {
        return this.resource.equalsIgnoreCase(resourceName);
    }

    /**
     * Obtient toutes les permissions pour une ressource
     */
    public static Permission[] getPermissionsForResource(String resourceName) {
        return java.util.Arrays.stream(Permission.values())
            .filter(permission -> permission.isForResource(resourceName))
            .toArray(Permission[]::new);
    }

    /**
     * Obtient toutes les ressources disponibles
     */
    public static String[] getAllResources() {
        return java.util.Arrays.stream(Permission.values())
            .map(Permission::getResource)
            .distinct()
            .toArray(String[]::new);
    }

    /**
     * Trouve une permission par son code
     */
    public static Permission fromCode(String code) {
        for (Permission permission : Permission.values()) {
            if (permission.getCode().equals(code)) {
                return permission;
            }
        }
        return null;
    }
}
