package ch.nfr.tablemodel.records;

import ch.nfr.tablemodel.RoomType;

/**
 * Represents the room record.
 * @param roomType the room type
 * @param roomName the room name
 * @param area the area of the room
 */
public record RoomRecord(RoomType roomType, String roomName, double area) {

    /**
     * Returns the room record as a String.
     * @return the room record as a String
     */
    public String toString() {
        return roomName + " (" + roomType + ")" + " - " + area + " m²";
    }
}
