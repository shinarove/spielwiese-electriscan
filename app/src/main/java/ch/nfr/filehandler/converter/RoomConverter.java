package ch.nfr.filehandler.converter;

import ch.nfr.filehandler.property.DeviceProperty;
import ch.nfr.filehandler.property.RoomProperty;
import ch.nfr.tablemodel.Room;
import ch.nfr.tablemodel.RoomType;
import ch.nfr.tablemodel.device.Device;
import org.json.JSONArray;
import org.json.JSONObject;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;

/**
 * This class converts a room object to a json object and vice versa.
 */
public class RoomConverter implements JsonConverter {
    /**
     * The json object of the room.
     */
    private JSONObject jsonObject;
    /**
     * The property change support of the room.
     */
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Reads the room properties from the json object and creates a room object.
     * @param jsonObject The json object to read the table model properties from.
     * @return The room object created from the json object.
     */
    @Override
    public Room readJson(JSONObject jsonObject) {
        if (!hasRoomProperties(jsonObject))
            throw new RuntimeException("Room properties are missing in the json file");
        if (!areRoomPropertiesValidTypes(jsonObject))
            throw new RuntimeException("Room properties have invalid types in the json file");
        return createRoom(jsonObject);
    }

    /**
     * Converts a room object to a json object.
     * @return The json object created from the room object.
     */
    @Override
    public JSONObject toJson() {
        return this.jsonObject;
    }

    /**
     * Writes the room properties to the json object.
     * @param propertyName The name of the property to write.
     * @param oldValue The old value of the property.
     * @param newValue The new value of the property.
     */
    @Override
    public void writeJson(String propertyName, Object oldValue, Object newValue) {
        switch (propertyName) {
            case "EDIT_ROOM": {
                for (RoomProperty property : RoomProperty.values()) {
                    jsonObject.put(property.name(), property.getValue((Room) newValue));
                }
                break;
            }
            case "ADD_DEVICE": {
                Device device = (Device) newValue;
                jsonObject.getJSONArray("devices").put(new DeviceConverter().addPropertyChangeListener(evt -> this.writeJson(evt.getPropertyName(), evt.getOldValue(),evt.getNewValue())).toJson(device, jsonObject.getInt(RoomProperty.ROOM_ID.name())));
                break;
            }
            case "REMOVE_DEVICE": {
                int deviceId = (int) oldValue;
                int index = -1;
                for (int i = 0; i < jsonObject.getJSONArray("devices").length(); i++) {
                    if (jsonObject.getJSONArray("devices").getJSONObject(i).getInt(DeviceProperty.DEVICE_ID.name()) == deviceId) {
                        index = i;
                    }
                }
                if (index != -1) {
                    jsonObject.getJSONArray("devices").remove(index);
                }
                break;
            }
            case "DEVICE_CHANGES": {
                JSONObject deviceJson = (JSONObject) newValue;
                int deviceId = deviceJson.getInt(DeviceProperty.DEVICE_ID.name());
                int index = -1;
                for (int i = 0; i < jsonObject.getJSONArray("devices").length(); i++) {
                    if (jsonObject.getJSONArray("devices").getJSONObject(i).getInt(DeviceProperty.DEVICE_ID.name()) == deviceId) {
                        index = i;
                    }
                }
                if (index != -1) {
                    jsonObject.getJSONArray("devices").put(index, deviceJson);
                }
                break;
            }
        }
        propertyChangeSupport.firePropertyChange("ROOM_CHANGES", null, jsonObject);
    }

    /**
     * Checks if the json object has all room properties.
     * @param jsonObject The json object to check.
     * @return True if the json object has all room properties, false otherwise.
     */
    private boolean hasRoomProperties(JSONObject jsonObject) {
        for (RoomProperty property : RoomProperty.values()) {
            if (!jsonObject.has(property.name())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the room properties in the json object have valid types.
     * @param jsonObject The json object to check.
     * @return True if the room properties in the json object have valid types, false otherwise.
     */
    private boolean areRoomPropertiesValidTypes(JSONObject jsonObject) {
        for (RoomProperty property : RoomProperty.values()) {
            Object value = jsonObject.get(property.name());
            if (!property.isValidType(value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a room object from the json object.
     * @param jsonObject The json object to create the room object from.
     * @return The room object created from the json object.
     */
    private Room createRoom(JSONObject jsonObject) {
        int roomId = jsonObject.getInt(RoomProperty.ROOM_ID.name());
        String roomName = jsonObject.getString(RoomProperty.ROOM_NAME.name());
        RoomType roomType = RoomProperty.getRoomType(jsonObject.getString(RoomProperty.ROOM_TYPE.name()));
        double roomSize = jsonObject.getDouble(RoomProperty.ROOM_SIZE.name());
        HashMap<Integer, Device> devices = createDevices(jsonObject, roomId);
        this.jsonObject = jsonObject;
        Room room = new Room(roomId, roomName, roomType, roomSize, devices);
        room.addPropertyChangeListener(evt -> this.writeJson(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue()));
        return room;
    }

    /**
     * Creates device objects from the json object.
     * @param jsonObject The json object to create the device objects from.
     * @param ownerId The owner id of the devices.
     * @return The device objects created from the json object.
     */
    public HashMap<Integer, Device> createDevices(JSONObject jsonObject, int ownerId) {
        HashMap<Integer, Device> devicesMap = new HashMap<>();
        if (jsonObject.has("devices")) {
            if (jsonObject.get("devices") instanceof JSONArray) {
                JSONArray devices = jsonObject.getJSONArray("devices");
                for (int i = 0; i < devices.length(); i++) {
                    if (devices.get(i) instanceof JSONObject) {
                        DeviceConverter deviceConverter = new DeviceConverter();
                        deviceConverter.setOwnerID(ownerId);
                        deviceConverter.addPropertyChangeListener(evt -> this.writeJson(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue()));
                        Device device = deviceConverter.readJson(devices.getJSONObject(i));
                        devicesMap.put(device.getId(), device);
                    }
                }
            } else {
                jsonObject.remove("devices");
                jsonObject.put("devices", new JSONArray());
            }
        } else {
            jsonObject.put("devices", new JSONArray());
        }
        return devicesMap;
    }

    /**
     * Converts a room object to a json object.
     * @param room The room object to convert to a json object.
     * @return The json object created from the room object.
     */
    public JSONObject toJson(Room room) {
        jsonObject = new JSONObject();
        for (RoomProperty property : RoomProperty.values()) {
            jsonObject.put(property.name(), property.getValue(room));
        }
        jsonObject.put("devices", new JSONArray());
        room.addPropertyChangeListener(evt -> writeJson(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue()));
        return jsonObject;
    }

    /**
     * Adds a property change listener to the room.
     * @param listener The property change listener to add.
     * @return The room converter with the added property change listener.
     */
    public RoomConverter addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
        return this;
    }
}
