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


    public static OrderStatus fromString(String text) {
        if (text != null) {
            for (OrderStatus status : OrderStatus.values()) {
                if (text.equalsIgnoreCase(status.displayName)) {
                    return status;
                }
            }
        }

        return PENDING;
    }
}
