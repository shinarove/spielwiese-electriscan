package ch.nfr.tablemodel;

import ch.nfr.tablemodel.records.HouseholdRecord;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import static ch.nfr.tablemodel.ChangeProperty.*;


/**
 * Represents a household with rooms and solar panels.
 */
public class Household implements TableModel {
    /** Logger */
    private static final Logger logger = Logger.getLogger(Household.class.getName());
    /** The property change support. */
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    /** The name of the household. */
    private String householdName;
    /** The number of residents in the household. */
    private int numberOfResidents;
    /** The postal code of the household. */
    private short postalCode;
    /** The rooms in the household. */
    private Map<Integer, Room> rooms;
    /** The solar panels in the household. */
    private Map<Integer, SolarPanel> solarPanels;

    /**
     * Default constructor.
     */
    public Household () {
        this("Default Haushalt", (short) 1000, 1, new HashMap<>(), new HashMap<>());
    }

    /**
     * Constructor with parameters.
     * @param householdName the name of the household
     * @param postalCode the postal code of the household
     * @param numberOfResidents the number of residents in the household
     * @param rooms the rooms in the household
     * @param solarPanels the solar panels in the household
     */
    public Household (String householdName, short postalCode, int numberOfResidents, Map<Integer,Room> rooms, Map<Integer,SolarPanel> solarPanels) {
        this.householdName = Objects.requireNonNull(householdName);
        this.postalCode = postalCode;
        this.numberOfResidents = numberOfResidents;
        this.rooms = rooms;
        this.solarPanels = solarPanels;
    }

    /**
     * Returns the name of the household.
     * @return the name of the household
     */
    public String getName() {
        return householdName;
    }

    /**
     * Returns the number of residents in the household.
     * @return the number of residents in the household
     */
    public int getNumberOfResidents() {
        return numberOfResidents;
    }

    /**
     * Returns the postal code of the household.
     * @return the postal code of the household
     */
    public short getPostalCode() {
        return postalCode;
    }

    /**
     * Returns the number of rooms in the household.
     * @return the number of rooms in the household
     */
    public int getNumberOfRooms() {
        return rooms.size();
    }

    /**
     * Edits the household.
     * @param householdName the name of the household
     * @param postalCode the postal code of the household
     * @param numberOfResidents the number of residents in the household
     */
    public void editHousehold(String householdName, short postalCode, int numberOfResidents) {
        this.householdName = householdName;
        this.postalCode = postalCode;
        this.numberOfResidents = numberOfResidents;
        propertyChangeSupport.firePropertyChange(EDIT_HOUSEHOLD.name(), null, this);
    }

    /**
     * Adds a room to the household.
     * @param room the room to add
     */
    public void addRoom(Room room) {
        room.addPropertyChangeListener(evt -> {
            this.propertyChangeSupport.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
        });
        rooms.put(room.getId(), room);
        propertyChangeSupport.firePropertyChange(ADD_ROOM.name(), null, room);
    }

    /**
     * Removes a room from the household.
     * @param roomId the room to remove
     */
    public void removeRoom(int roomId) {
        rooms.remove(roomId);
        propertyChangeSupport.firePropertyChange(REMOVE_ROOM.name(), roomId, null);
    }

    /**
     * Returns a room from the household.
     * @param roomId the room to return
     * @return the room
     */
    public Room getRoom(int roomId) {
        if (!rooms.containsKey(roomId)) {
            throw new IllegalArgumentException("Room with id " + roomId + " does not exist in household " + householdName);
        }
        return rooms.get(roomId);
    }

    /**
     * Adds a solar panel to the household.
     * @param solarPanel the solar panel to add
     */
    public void addSolarPanel(SolarPanel solarPanel) {
        solarPanel.addPropertyChangeListener(evt -> {
            this.propertyChangeSupport.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
        });
        solarPanels.put(solarPanel.getId(), solarPanel);
        propertyChangeSupport.firePropertyChange(ADD_SOLAR_PANEL.name(), null, solarPanel);
    }

    /**
     * Removes a solar panel from the household.
     * @param solarPanelId the solar panel to remove
     */
    public void removeSolarPanel(int solarPanelId) {
        solarPanels.remove(solarPanelId);
        propertyChangeSupport.firePropertyChange(REMOVE_SOLAR_PANEL.name(), solarPanelId, null);
    }

    /**
     * Returns a solar panel from the household.
     * @param solarPanelId the solar panel to return
     * @return the solar panel
     */
    public SolarPanel getSolarPanel(int solarPanelId) {
        if (!solarPanels.containsKey(solarPanelId)) {
            throw new IllegalArgumentException("Solar panel with id " + solarPanelId + " does not exist in household " + householdName);
        }
        return solarPanels.get(solarPanelId);
    }

    /**
     * Adds a property change listener.
     * @param listener the listener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Gets the next free room id.
     * @return the next free room id
     */
    public int getFreeRoomId() {
        int roomId = 1;
        while (rooms.containsKey(roomId)) {
            roomId++;
        }
        return roomId;
    }

    /**
     * Gets the next free solar panel id.
     * @return the next free solar panel id
     */
    public int getFreeSolarPanelId() {
        int solarPanelId = 1;
        while (solarPanels.containsKey(solarPanelId)) {
            solarPanelId++;
        }
        return solarPanelId;
    }

    /**
     * Returns all rooms in the household.
     * @return a list of all rooms in the household
     */
    public List<Room> getAllRooms() {
        return List.copyOf(rooms.values());
    }

    /**
     * Returns all solar panels in the household.
     * @return a list of all solar panels in the household
     */
    public List<SolarPanel> getAllSolarPanels() {
        return List.copyOf(solarPanels.values());
    }

    /**
     * Converts the household to a household record.
     * @return the household record representing the household
     */
    public HouseholdRecord toHouseholdRecord() {
        return new HouseholdRecord(householdName, postalCode, numberOfResidents);
    }

    /**
     * Converts household to a string, the value is equal to the household name
     * @return the household as a string
     */
    @Override
    public String toString() {
        return householdName;
    }
}
