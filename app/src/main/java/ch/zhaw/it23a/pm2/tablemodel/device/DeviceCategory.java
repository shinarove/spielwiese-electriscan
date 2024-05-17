package ch.zhaw.it23a.pm2.tablemodel.device;

import java.nio.charset.StandardCharsets;

/**
 * The device category.
 */
public enum DeviceCategory {

    ENTERTAINMENT("Unterhaltung"),
    COMMUNICATION("Kommunikation"),
    KITCHEN("Küche"),
    CLEANING("Reinigung"),
    OFFICE("Büro"),
    FITNESS("Fitness"),
    PHOTOGRAPHY("Fotografie"),
    SECURITY("Sicherheit"),
    LIGHTING("Beleuchtung"),
    OTHER("Sonstiges");

    /**
     * The german name of the device category.
     */
    private final String germanName;

    /**
     * Creates a new device category.
     *
     * @param germanName the german name of the device category
     */
    DeviceCategory(String germanName) {
        this.germanName = germanName;
    }

    /**
     * Returns the german name of the device category.
     *
     * @return the german name
     */
    public String getGermanName() {
        return germanName;
    }

    /**
     * Parses the device category.
     *
     * @param deviceCategory the device category
     * @return the device category
     */
    public static DeviceCategory parseDeviceCategory(String deviceCategory) {
        for (DeviceCategory category : DeviceCategory.values()) {
            String germanName = new String(category.germanName.getBytes(), StandardCharsets.UTF_8);
            if (category.name().equals(deviceCategory) || germanName.equals(deviceCategory)) {
                return category;
            }
        }
        return null;
    }
}
