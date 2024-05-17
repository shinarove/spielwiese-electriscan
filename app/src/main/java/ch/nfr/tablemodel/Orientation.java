package ch.nfr.tablemodel;

import java.nio.charset.StandardCharsets;

/**
 * The orientation of a solar panel.
 */
public enum Orientation {


    NORTH("Norden"),
    EAST("Osten"),
    SOUTH("Süden"),
    WEST("Westen"),
    NORTH_EAST("Nordosten"),
    NORTH_WEST("Nordwesten"),
    SOUTH_EAST("Südosten"),
    SOUTH_WEST("Südwesten");
    /**
     * The german name of the orientation.
     */
    private final String germanName;

    /**
     * Creates a new orientation.
     *
     * @param germanName the german name of the orientation
     */
    Orientation(String germanName) {
        this.germanName = germanName;
    }

    /**
     * Returns the german name of the orientation.
     *
     * @return the german name
     */
    public String getGermanName() {
        return germanName;
    }

    /**
     * Parses the orientation.
     * @param orientationString the orientation string
     * @return the orientation
     */
    public static Orientation parseOrientation(String orientationString) {
        for (Orientation orientation : Orientation.values()) {
            String germanName = new String(orientation.germanName.getBytes(), StandardCharsets.UTF_8);
            if ((orientation.name()).equals(orientationString) || germanName.equals(orientationString)) {
                return orientation;
            }
        }
        return null;
    }
}
