package ch.nfr.userinterface.model.property;

/**
 * Enum representing the different properties of the ElectriScanModel.
 * These properties are used to identify the type of change that has occurred in the ElectriScanModel.
 * The properties include LOAD_HOUSEHOLD, EDIT_HOUSEHOLD, REMOVE_HOUSEHOLD, and LOAD_DIRECTORY.
 */
public enum ElectriScanProperty {

    LOAD_HOUSEHOLD,
    EDIT_HOUSEHOLD,
    REMOVE_HOUSEHOLD,
    LOAD_DIRECTORY;

    /**
     * Parses the given property name to its corresponding ElectriScanProperty.
     * If the property name does not match any of the ElectriScanProperty values, null is returned.
     *
     * @param propertyName the name of the property to parse.
     * @return the corresponding ElectriScanProperty, or null if no match is found.
     */
    public static ElectriScanProperty parseProperty(String propertyName) {
        for (ElectriScanProperty property : ElectriScanProperty.values()) {
            if (property.name().equals(propertyName)) {
                return property;
            }
        }
        return null;
    }
}
