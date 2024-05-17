package ch.zhaw.it23a.pm2.userinterface.model;

import static ch.zhaw.it23a.pm2.userinterface.model.property.DeviceOverviewProperty.*;

import ch.zhaw.it23a.pm2.filehandler.DeviceProposalHandler;
import ch.zhaw.it23a.pm2.userinterface.model.property.DeviceOverviewProperty;
import ch.zhaw.it23a.pm2.tablemodel.*;
import ch.zhaw.it23a.pm2.tablemodel.device.Device;
import ch.zhaw.it23a.pm2.tablemodel.device.MobileDevice;
import ch.zhaw.it23a.pm2.tablemodel.device.WiredDevice;
import ch.zhaw.it23a.pm2.tablemodel.records.DeviceRecord;
import ch.zhaw.it23a.pm2.tablemodel.records.RoomRecord;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.logging.Logger;

/**
 * The DeviceOverviewModel class is responsible for managing the devices and rooms in a household.
 * It provides methods to add, edit, and remove devices and rooms.
 * It also provides methods to add PropertyChangeListeners to the DeviceOverviewModel and ElectriScanModel.
 * The class uses a PropertyChangeSupport to notify listeners of changes.
 * It also uses an instance of ElectriScanModel.TextOutput to set information text.
 */
public class DeviceOverviewModel extends SecondaryModel {

    /** The used Logger in this class. */
    private static final Logger logger = Logger.getLogger(DeviceOverviewModel.class.getName());

    /** The used PropertyChangeSupport in this class. */
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Adds a new room to the household.
     * Listeners to {@link DeviceOverviewModel#propertyChangeSupport} are informed with the following information:
     * <ul>
     *     <li>Propertyname:   {@link DeviceOverviewProperty#ADD_ROOM} </li>
     *     <li>Old value:      null </li>
     *     <li>New value:      the added {@link Room} instance </li>
     * </ul>
     * Sets an information text in the {@link ElectriScanModel.TextOutput}
     *
     * @param tempRoom a {@link RoomRecord} object holding the data for the room
     */
    public void addRoom(RoomRecord tempRoom) {
        int roomId = getHousehold().getFreeRoomId();
        Room newRoom = new Room(roomId, tempRoom.roomName(), tempRoom.roomType(), tempRoom.area());
        getHousehold().addRoom(newRoom);

        propertyChangeSupport.firePropertyChange(ADD_ROOM.name(), null, newRoom);
        setMessageInTextOutput("Raum: " + tempRoom.roomName() + utf8(" wurde hinzugefügt."));
    }

    /**
     * Edits the room with the given id in the household.
     * Updates the room's name, type, and area with the values from the provided {@link RoomRecord}.
     * Informs listeners with the property name {@link DeviceOverviewProperty#EDIT_ROOM},
     * old value as null and the new value as the edited {@link Room}.
     * Sets an information text in the {@link ElectriScanModel.TextOutput}.
     *
     * @param clickedRoomId the id of the room to edit
     * @param tempRoom a {@link RoomRecord} object holding the new data for the room
     */
    public void editRoom(int clickedRoomId, RoomRecord tempRoom) {
        Room editedRoom = getHousehold().getRoom(clickedRoomId);

        editedRoom.editRoom(tempRoom.roomName(), tempRoom.roomType(), tempRoom.area());
        propertyChangeSupport.firePropertyChange(EDIT_ROOM.name(), null, editedRoom);
        setMessageInTextOutput("Raum: " + tempRoom.roomName() + " wurde bearbeitet.");
    }

    /**
     * Removes the room with the given id from the household.
     * Informs listeners with the property name {@link DeviceOverviewProperty#REMOVE_ROOM},
     * old value as the removed {@link Room} and the new value as null.
     * Sets an information text in the {@link ElectriScanModel.TextOutput}.
     *
     * @param roomId the id of the room to remove
     */
    public void removeRoom(int roomId) {
        Room removedRoom = getHousehold().getRoom(roomId);
        getHousehold().removeRoom(roomId);

        propertyChangeSupport.firePropertyChange(REMOVE_ROOM.name(), removedRoom, null);
        setMessageInTextOutput("Raum: " + removedRoom.getName() + utf8(" wurde gelöscht."));
    }

    /**
     * Adds a new device to the room with the given id in the household.
     * If the device is a wired device, a new WiredDevice is created, otherwise a new MobileDevice is created.
     * Informs listeners with the property name {@link DeviceOverviewProperty#ADD_DEVICE},
     * old value as null and the new value as the new {@link Device}.
     * Sets an information text in the {@link ElectriScanModel.TextOutput}.
     *
     * @param clickedRoomId the id of the room to add the device to
     * @param tempDevice a DeviceRecord object holding the data for the device
     */
    public void addDevice(int clickedRoomId, DeviceRecord tempDevice) {
        int deviceId = getHousehold().getRoom(clickedRoomId).getFreeDeviceId();
        if (tempDevice.isWired()) {
            WiredDevice newDevice = new WiredDevice(deviceId, clickedRoomId, tempDevice.deviceName(), tempDevice.deviceCategory(), tempDevice.consumption());
            getHousehold().getRoom(clickedRoomId).addDevice(newDevice);

            propertyChangeSupport.firePropertyChange(ADD_DEVICE.name(), null, newDevice);
            setMessageInTextOutput(utf8("Gerät: ") + tempDevice.deviceName() + utf8(" wurde hinzugefügt."));
        } else {
            MobileDevice newDevice = new MobileDevice(deviceId, clickedRoomId, tempDevice.deviceName(), tempDevice.deviceCategory(), tempDevice.consumption());
            getHousehold().getRoom(clickedRoomId).addDevice(newDevice);

            propertyChangeSupport.firePropertyChange(ADD_DEVICE.name(), null, newDevice);
            setMessageInTextOutput(utf8("Gerät: ") + tempDevice.deviceName() + " wurde hinzugefügt.");
        }
    }

    /**
     * Edits the device with the given id in the room with the given room id in the household.
     * Updates the device's name, category, and consumption with the values from the provided DeviceRecord.
     * Informs listeners with the property name {@link DeviceOverviewProperty#EDIT_DEVICE},
     * old value as null and the new value as the edited {@link Device}.
     * Sets an information text in the {@link ElectriScanModel.TextOutput}.
     *
     * @param roomId the id of the room that owns the device
     * @param deviceId the id of the device to edit
     * @param tempDevice a DeviceRecord object holding the new data for the device
     */
    public void editDevice(int roomId, int deviceId, DeviceRecord tempDevice) {
        Device editedDevice = getHousehold().getRoom(roomId).getDevice(deviceId);
        editedDevice.editDevice(tempDevice.deviceName(), tempDevice.deviceCategory(), tempDevice.consumption());

        propertyChangeSupport.firePropertyChange(EDIT_DEVICE.name(), null, editedDevice);
        setMessageInTextOutput(utf8("Gerät: ") +
                tempDevice.deviceName() + " wurde bearbeitet.");
    }

    /**
     * Removes the device with the given id from the room with the given owner id in the household.
     * Informs listeners with the property name {@link DeviceOverviewProperty#REMOVE_DEVICE},
     * old value as the removed {@link Device} and the new value as null.
     * Sets an information text in the {@link ElectriScanModel.TextOutput}.
     *
     * @param roomId the id of the room that owns the device
     * @param deviceId the id of the device to remove
     */
    public void removeDevice(int roomId, int deviceId) {
        Device removedDevice = getHousehold().getRoom(roomId).getDevice(deviceId);
        getHousehold().getRoom(roomId).removeDevice(deviceId);

        propertyChangeSupport.firePropertyChange(REMOVE_DEVICE.name(), removedDevice, null);
        setMessageInTextOutput("Gerät: " + removedDevice.getName() +
                utf8(" wurde gelöscht."));
    }

    /**
     * Adds a {@link PropertyChangeListener} to the {@link PropertyChangeSupport}.
     *
     * @param listener the listener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * First gets for every {@link RoomType} the corresponding {@link DeviceRecord}s from the {@link DeviceProposalHandler}.
     * After that, checks every room in the household and adds all the corresponding {@link DeviceRecord}s to the room.
     */
    public void quickCaptureDevices() {
        String deviceProposalPath = "src/main/resources/default-devices/deviceProposal.properties";

        Set<RoomType> usedRoomTypes = new HashSet<>();
        Map<RoomType, List<DeviceRecord>> deviceRecordsForRoomType = new HashMap<>();
        for (Room room : getHousehold().getAllRooms()) {
            RoomType roomType = room.getRoomType();
            if (roomType != RoomType.DUMMY && usedRoomTypes.add(roomType)) {
                List<DeviceRecord> deviceRecords = DeviceProposalHandler.readDeviceProposalFile(deviceProposalPath, roomType);
                deviceRecordsForRoomType.put(roomType, deviceRecords);
                logger.info("DeviceRecords for RoomType " + roomType + " loaded.");
            }
        }

        for (Room room : getHousehold().getAllRooms()) {
            if (room.getRoomType() != RoomType.DUMMY) {
                List<DeviceRecord> deviceRecords = deviceRecordsForRoomType.get(room.getRoomType());
                if (deviceRecords != null) {
                    deviceRecords.forEach(deviceRecord -> addDevice(room.getId(), deviceRecord));
                }
            }
        }
    }
}
