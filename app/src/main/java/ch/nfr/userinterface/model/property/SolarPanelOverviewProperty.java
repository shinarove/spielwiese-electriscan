package ch.nfr.userinterface.model.property;

/**
 * Enum representing the different properties of the SolarPanelOverviewModel.
 * These properties are used to identify the type of change that has occurred in the SolarPanelOverviewModel.
 * The properties include ADD_SOLAR_PANEL, EDIT_SOLAR_PANEL, and REMOVE_SOLAR_PANEL.
 */
public enum SolarPanelOverviewProperty {

    ADD_SOLAR_PANEL,
    EDIT_SOLAR_PANEL,
    REMOVE_SOLAR_PANEL;

    /**
     * Parses the given property name to its corresponding SolarPanelOverviewProperty.
     * If the property name does not match any of the SolarPanelOverviewProperty values, null is returned.
     *
     * @param propertyName the name of the property to parse.
     * @return the corresponding SolarPanelOverviewProperty, or null if no match is found.
     */
    public static SolarPanelOverviewProperty parseProperty(String propertyName) {
        for (SolarPanelOverviewProperty property : SolarPanelOverviewProperty.values()) {
            if (property.name().equals(propertyName)) {
                return property;
            }
        }
        return null;
    }
}
