package gui;

enum OrderStatus {
    PENDING("en_attente"),
    PREPARING("en_traitement"),
    FINISHED("Terminée"),
    CANCELLED("Annulée");

    private String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    // Helper method to get enum from string value (case-insensitive)
    public static OrderStatus fromString(String text) {
        if (text != null) {
            for (OrderStatus status : OrderStatus.values()) {
                if (text.equalsIgnoreCase(status.displayName)) {
                    return status;
                }
            }
        }
        // Return PENDING or throw an exception if status string is invalid
        return PENDING; // Default to PENDING if string doesn't match
    }
}
