package ch.zhaw.it23a.pm2.tablemodel;

import ch.zhaw.it23a.pm2.tablemodel.device.Device;
import ch.zhaw.it23a.pm2.tablemodel.records.RoomRecord;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.zhaw.it23a.pm2.tablemodel.ChangeProperty.*;

/**
 * Represents a room with devices.
 */
public class Room implements TableModel {

    /**
     * The property change support.
     */
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    /**
     * The id of the room.
     */
    private final int id;
    /**
     * The name of the room.
     */
    private String roomName;
    /**
     * The type of the room.
     */
    private RoomType roomType;
    /**
     * The size of the room.
     */
    private double roomSize;
    /**
     * The devices in the room.
     */
    private Map<Integer, Device> devices;

    /**
     * Constructor for the room.
     * @param id The id of the room.
     * @param roomName The name of the room.
     * @param roomType The type of the room.
     * @param roomSize The size of the room.
     */
    public Room(int id, String roomName, RoomType roomType, double roomSize) {
        this.id = id;
        this.roomName = roomName;
        this.roomType = roomType;
        this.roomSize = roomSize;
        this.devices = new HashMap<>();
    }

    /**
     * Constructor for the room.
     * @param id The id of the room.
     * @param roomName The name of the room.
     * @param roomType The type of the room.
     * @param roomSize The size of the room.
     * @param devices The devices in the room.
     */
    public Room(int id, String roomName, RoomType roomType, double roomSize, Map<Integer, Device> devices) {
        this.id = id;
        this.roomName = roomName;
        this.roomType = roomType;
        this.roomSize = roomSize;
        this.devices = devices;
    }

    /**
     * Get the id of the room.
     * @return The id of the room.
     */
    public int getId() {
        return id;
    }

    /**
     * Get the name of the room.
     * @return The name of the room.
     */
    public String getName() {
        return roomName;
    }

    /**
     * Get the type of the room.
     * @return The type of the room.
     */
    public RoomType getRoomType() {
        return roomType;
    }

    /**
     * Get the size of the room.
     * @return The size of the room.
     */
    public double getRoomSize() {
        return roomSize;
    }

    /**
     * Get the number of devices in the room.
     * @return The number of devices in the room.
     */
    public int getNumberOfDevices() {
        return devices.size();
    }

    /**
     * Edits the room.
     *
     * @param roomName the name of the room
     * @param roomType the type of the room
     * @param roomSize the size of the room
     */
    public void editRoom(String roomName, RoomType roomType, double roomSize) {
        this.roomName = roomName;
        this.roomType = roomType;
        this.roomSize = roomSize;
        propertyChangeSupport.firePropertyChange(EDIT_ROOM.name(), null, this);
    }

    /**
     * Adds a device to the room.
     *
     * @param device the device to add
     */
    public void addDevice(Device device) {
        if (id == device.getOwnerId()) {
            device.addPropertyChangeListener(evt -> {
                this.propertyChangeSupport.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
            });
            devices.put(device.getId(), device);
            propertyChangeSupport.firePropertyChange(ADD_DEVICE.name(), null, device);
        }
    }

    /**
     * Removes a device with their id from the room.
     *
     * @param deviceId The id of the device to remove
     */
    public void removeDevice(int deviceId) {
        devices.remove(deviceId);
        propertyChangeSupport.firePropertyChange(REMOVE_DEVICE.name(), deviceId, 0);
    }

    /**
     * Get a device by its id.
     *
     * @param deviceId The id of the device.
     * @return The device with the given id.
     */
    public Device getDevice(int deviceId) {
        if (!devices.containsKey(deviceId)) {
            throw new IllegalArgumentException("Device with id " + deviceId + " does not exist in room " + id);
        }
        return devices.get(deviceId);
    }

    /**
     * Adds a property change listener.
     * @param listener The property change listener.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Gets the next free device id.
     * @return the next free device id
     */
    public int getFreeDeviceId() {
        int deviceId = 1;
        while (devices.containsKey(deviceId)) {
            deviceId++;
        }
        return deviceId;
    }

    /**
     * Get all devices in the room.
     * @return A list of all devices in the room.
     */
    public List<Device> getAllDevices() {
        return new ArrayList<>(devices.values());
    }

    /**
     * Converts the room to a room record.
     * @return The room record representing the room.
     */
    public RoomRecord toRoomRecord() {
        return new RoomRecord(roomType, roomName, roomSize);
    }

    /**
     * Converts the room to a string, the value of the string is equal to the room name.
     * @return The room as a string
     */
    public String toString() {
        return roomName;
    }
}
