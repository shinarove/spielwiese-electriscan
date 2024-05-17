package ch.nfr.tablemodel;

/**
 * Represents the change property.
 *
 */
public enum ChangeProperty {
    EDIT_HOUSEHOLD,
    ADD_ROOM,
    EDIT_ROOM,
    REMOVE_ROOM,
    ADD_SOLAR_PANEL,
    EDIT_SOLAR_PANEL,
    REMOVE_SOLAR_PANEL,
    ADD_DEVICE,
    EDIT_DEVICE,
    REMOVE_DEVICE;

    /**
     * Parses the property.
     * @param propertyName the property name
     * @return the property
     */
    public static ChangeProperty parseProperty(String propertyName) {
        for (ChangeProperty property : ChangeProperty.values()) {
            if (property.name().equals(propertyName)) {
                return property;
            }
        }
        return null;
    }
}
