package ch.nfr.filehandler.property;

import ch.nfr.tablemodel.Household;

/**
 * This enum represents the household property.
 * It contains methods to bind the property types and values from {@link Household} class.
 */
public enum HouseholdProperty {

    HOUSEHOLD_NAME {
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
         * This method returns the name of the household.
         * @see Household#getName()
         * @param household the household
         * @return the name of the household
         */
        @Override
        public Object getValue(Household household) {
            return household.getName();
        }
    },
    NUMBER_OF_RESIDENTS {
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
         * This method returns the number of residents within the household.
         * @see Household#getNumberOfResidents()
         * @param household the household
         * @return the number of residents within the household
         */
        @Override
        public Object getValue(Household household) {
            return household.getNumberOfResidents();
        }
    },
    POSTAL_CODE {
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
         * This method returns the postal code of the household.
         * @see Household#getPostalCode()
         * @param household the household
         * @return the postal code of the household
         */
        @Override
        public Object getValue(Household household) {
            return household.getPostalCode();
        }
    };

    /**
     * This method parses the property.
     *
     * @param propertyName the property name
     * @return the property
     */
    public static HouseholdProperty parseProperty(String propertyName) {
        for (HouseholdProperty property : HouseholdProperty.values()) {
            if (property.name().equals(propertyName)) {
                return property;
            }
        }
        return null;
    }

    /**
     * This method checks if the value is a valid type.
     *
     * @param value the value to check
     * @return true if the value is a valid type, false otherwise
     */
    public abstract boolean isValidType(Object value);

    /**
     * This method returns the value of the property.
     * It binds the Getters from the {@link Household} class.
     *
     * @param household the household
     * @return the value of the property
     */
    public abstract Object getValue(Household household);
}
