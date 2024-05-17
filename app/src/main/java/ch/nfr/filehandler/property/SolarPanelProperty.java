package ch.nfr.filehandler.property;

import ch.nfr.tablemodel.Orientation;
import ch.nfr.tablemodel.SolarPanel;

/**
 * This enum represents the solar panel property.
 * It contains methods to bind the property types and values from {@link SolarPanel} class.
 */
public enum SolarPanelProperty {

    SOLAR_PANEL_ID {
        /**
         * This method checks if the value is a valid type.
         * @param value the value to check
         * @return true if the value is an Integer, false otherwise
         */
        @Override
        public boolean isValidType(Object value) {
            return value instanceof Integer;
        }

        /**
         * This method returns the id of the solar panel.
         * @see SolarPanel#getId()
         * @param solarPanel the solar panel
         * @return the id of the solar panel
         */
        @Override
        public Object getValue(SolarPanel solarPanel) {
            return solarPanel.getId();
        }
    },
    SOLAR_PANEL_NAME {
        /**
         * This method checks if the value is a valid type.
         * @param value the value to check
         * @return true if the value is a String, false otherwise
         */
        @Override
        public boolean isValidType(Object value) {
            return value instanceof String;
        }

        /**
         * This method returns the name of the solar panel.
         * @see SolarPanel#getName()
         * @param solarPanel the solar panel
         * @return the name of the solar panel
         */
        @Override
        public Object getValue(SolarPanel solarPanel) {
            return solarPanel.getName();
        }
    },
    SOLAR_PANEL_AREA {
        /**
         * This method checks if the value is a valid type.
         * @param value the value to check
         * @return true if the value is a Number, false otherwise
         */
        @Override
        public boolean isValidType(Object value) {
            return value instanceof Number;
        }

        /**
         * This method returns the area of the solar panel.
         * @see SolarPanel#getArea()
         * @param solarPanel the solar panel
         * @return the area of the solar panel
         */
        @Override
        public Object getValue(SolarPanel solarPanel) {
            return solarPanel.getArea();
        }
    },
    ORIENTATION {
        /**
         * This method checks if the value is a valid type.
         * @param value the value to check
         * @return true if the value is a String, false otherwise
         */
        @Override
        public boolean isValidType(Object value) {
            return value instanceof String;
        }

        /**
         * This method returns the orientation of the solar panel.
         * @see Orientation#parseOrientation(String)
         * @param solarPanel the solar panel
         * @return the orientation of the solar panel
         */
        @Override
        public Object getValue(SolarPanel solarPanel) {
            return solarPanel.getOrientation().name();
        }
    };

    /**
     * This method parses the property name.
     *
     * @param propertyName the property name
     * @return the solar panel property
     */
    public static SolarPanelProperty parseProperty(String propertyName) {
        for (SolarPanelProperty property : SolarPanelProperty.values()) {
            if (property.name().equals(propertyName)) {
                return property;
            }
        }
        return null;
    }

    /**
     * This method returns the orientation of the solar panel.
     *
     * @param string the orientation of the solar panel
     * @return the orientation of the solar panel
     */
    public static Orientation getOrientation(String string) {
        return Orientation.parseOrientation(string) == null ? Orientation.SOUTH_WEST : Orientation.parseOrientation(string);
    }

    /**
     * This method checks if the value is a valid type.
     *
     * @param value the value to check
     * @return true if the value is a valid type, false otherwise
     */
    public abstract boolean isValidType(Object value);

    /**
     * This method returns the value of the solar panel property.
     * It binds the Getters from the {@link SolarPanel} class.
     *
     * @param solarPanel the solar panel
     * @return the value of the solar panel property
     */
    public abstract Object getValue(SolarPanel solarPanel);
}
