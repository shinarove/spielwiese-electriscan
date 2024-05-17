package ch.nfr.userinterface.model.property;

/**
 * Enum representing the different properties of the DeviceOverviewModel.
 * These properties are used to identify the type of change that has occurred in the DeviceOverviewModel.
 * The properties include ADD_ROOM, EDIT_ROOM, REMOVE_ROOM, ADD_DEVICE, EDIT_DEVICE, and REMOVE_DEVICE.
 */
public enum DeviceOverviewProperty {

    ADD_ROOM,
    EDIT_ROOM,
    REMOVE_ROOM,
    ADD_DEVICE,
    EDIT_DEVICE,
    REMOVE_DEVICE;

    /**
     * Parses the given property name to its corresponding DeviceOverviewProperty.
     * If the property name does not match any of the DeviceOverviewProperty values, null is returned.
     *
     * @param propertyName the name of the property to parse.
     * @return the corresponding DeviceOverviewProperty, or null if no match is found.
     */
    public static DeviceOverviewProperty parseProperty(String propertyName) {
        for (DeviceOverviewProperty property : DeviceOverviewProperty.values()) {
            if (property.name().equals(propertyName)) {
                return property;
            }
        }
        return null;
    }
}
