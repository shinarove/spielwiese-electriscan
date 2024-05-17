package ch.zhaw.it23a.pm2.filehandler.property;

import ch.zhaw.it23a.pm2.tablemodel.Room;
import ch.zhaw.it23a.pm2.tablemodel.RoomType;

/**
 * This enum represents the room property.
 * It contains methods to bind the property types and values from {@link Room} class.
 */
public enum RoomProperty {

    ROOM_ID {
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
         * This method returns the id of the room.
         * @see Room#getId()
         * @param room the room
         * @return the value of the room property
         */
        @Override
        public Object getValue(Room room) {
            return room.getId();
        }
    },
    ROOM_NAME {
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
         * This method returns the name of the room.
         * @see Room#getName()
         * @param room the room
         * @return the value of the room property
         */
        @Override
        public Object getValue(Room room) {
            return room.getName();
        }
    },
    ROOM_TYPE {
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
         * This method returns the room type.
         * @see Room#getRoomType()
         * @param room the room
         * @return the value of the room property
         */
        @Override
        public Object getValue(Room room) {
            return room.getRoomType().name();
        }
    },
    ROOM_SIZE {
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
         * This method returns the size of the room.
         * @see Room#getRoomSize()
         * @param room the room
         * @return the value of the room property
         */
        @Override
        public Object getValue(Room room) {
            return room.getRoomSize();
        }
    };

    /**
     * This method parses the property.
     *
     * @param propertyName the property name
     * @return the room property
     */
    public static RoomProperty parseProperty(String propertyName) {
        for (RoomProperty property : RoomProperty.values()) {
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

    public static RoomType getRoomType(String value) {
        return RoomType.parseRoomType(value) == null ? RoomType.DUMMY : RoomType.parseRoomType(value);
    }

    /**
     * This method returns the value of the room property.
     * It binds the Getters from the {@link Room} class.
     *
     * @param room the room
     * @return the value of the room property
     */
    public abstract Object getValue(Room room);
}
