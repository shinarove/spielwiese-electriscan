package ch.zhaw.it23a.pm2.userinterface.model.property;

/**
 * Represents the cost overview property.
 */
public enum CostOverviewProperty {

    UPDATE_CHARTS;

    /**
     * Parses the property.
     * @param propertyName the property name
     * @return the property
     */
    public static CostOverviewProperty parseProperty(String propertyName) {
        for (CostOverviewProperty property : CostOverviewProperty.values()) {
            if (property.name().equals(propertyName)) {
                return property;
            }
        }
        return null;
    }

}
