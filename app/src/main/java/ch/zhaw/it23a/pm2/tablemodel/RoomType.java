package ch.zhaw.it23a.pm2.tablemodel;

import java.nio.charset.StandardCharsets;

/**
 * This enum represents the room type.
 */
public enum RoomType {
    LIVING_ROOM("Wohnzimmer"),
    KITCHEN("Küche"),
    BATHROOM("Badezimmer"),
    BEDROOM("Schlafzimmer"),
    DINING_ROOM("Esszimmer"),
    OFFICE("Büro"),
    LAUNDRY_ROOM("Waschküche"),
    GARAGE("Garage"),
    BASEMENT("Keller"),
    ATTIC("Dachboden"),
    GUEST_ROOM("Gästezimmer"),
    GYM("Fitnessraum"),
    FOYER("Foyer"),
    ENTRYWAY("Eingangsbereich"),
    DUMMY("Dummy");

    /**
     * The german name of the room type.
     */
    private final String germanName;

    /**
     * Creates a new room type.
     *
     * @param germanName the german name of the room type
     */
    RoomType(String germanName) {
        this.germanName = germanName;
    }

    /**
     * Returns the german name of the room type.
     * @return the german name
     */
    public String getGermanName() {
        return germanName;
    }

    /**
     * Parses the room type.
     * @param roomType the room type as a string
     * @return the room type enum

     */
    public static RoomType parseRoomType(String roomType) {
        for (RoomType type : RoomType.values()) {
            String germanName = new String(type.germanName.getBytes(), StandardCharsets.UTF_8);
            if (type.name().equals(roomType) || germanName.equals(roomType)) {
                return type;
            }
        }
        return null;
    }
}
