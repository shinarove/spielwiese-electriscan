package ch.nfr.filehandler.converter;

import ch.nfr.filehandler.property.HouseholdProperty;
import ch.nfr.filehandler.property.RoomProperty;
import ch.nfr.filehandler.property.SolarPanelProperty;
import ch.nfr.tablemodel.Household;
import ch.nfr.tablemodel.Room;
import ch.nfr.tablemodel.SolarPanel;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * This class converts a household object to a json object and vice versa.
 */
public class HouseholdConverter implements JsonConverter {

    /**
     * The json object of the household.
     */
    private JSONObject jsonObject;

    /**
     * Reads the household properties from the json object and creates a household object.
     * @param jsonObject The json object to read the table model properties from.
     * @return The household object created from the json object.
     */
    @Override
    public Household readJson(JSONObject jsonObject) {
        if (!hasHouseholdProperties(jsonObject))
            throw new RuntimeException("Household properties are missing in the json file");
        if (!areHouseholdPropertiesValidTypes(jsonObject))
            throw new RuntimeException("Household properties have invalid types in the json file");
        return createHousehold(jsonObject);
    }

    /**
     * Converts a household object to a json object.
     * @return The json object created from the household object.
     */
    @Override
    public JSONObject toJson() {
        return this.jsonObject;
    }

    /**
     * Writes the household properties to the json object.
     * @param propertyName The name of the property to write.
     * @param oldValue The old value of the property.
     * @param newValue The new value of the property.
     */
    @Override
    public void writeJson(String propertyName, Object oldValue, Object newValue) {
        switch (propertyName) {
            case "EDIT_HOUSEHOLD": {
                for (HouseholdProperty property : HouseholdProperty.values()) {
                    jsonObject.put(property.name(), property.getValue((Household) newValue));
                }
                break;
            }
            case "ADD_ROOM": {
                Room room = (Room) newValue;
                jsonObject.getJSONArray("rooms").put(new RoomConverter().addPropertyChangeListener(evt -> this.writeJson(evt.getPropertyName(), evt.getOldValue(),evt.getNewValue())).toJson(room));
                break;
            }
            case "REMOVE_ROOM": {
                int roomId = (int) oldValue;
                int index = -1;
                for (int i = 0; i < jsonObject.getJSONArray("rooms").length(); i++) {
                    if (jsonObject.getJSONArray("rooms").getJSONObject(i).getInt(RoomProperty.ROOM_ID.name()) == roomId) {
                        index = i;
                    }
                }
                if (index != -1) {
                    jsonObject.getJSONArray("rooms").remove(index);
                }
                break;
            }
            case "ADD_SOLAR_PANEL": {
                SolarPanel solarPanel = (SolarPanel) newValue;
                jsonObject.getJSONArray("solarPanels").put(new SolarPanelConverter().addPropertyChangeListener(evt -> this.writeJson(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue())).toJson(solarPanel));
                break;
            }
            case "REMOVE_SOLAR_PANEL": {
                int solarPanelId = (int) oldValue;
                int index = -1;
                for (int i = 0; i < jsonObject.getJSONArray("solarPanels").length(); i++) {
                    if (jsonObject.getJSONArray("solarPanels").getJSONObject(i).getInt(SolarPanelProperty.SOLAR_PANEL_ID.name()) == solarPanelId) {
                        index = i;
                    }
                }
                if (index != -1) {
                    jsonObject.getJSONArray("solarPanels").remove(index);
                }
                break;
            }
            case "ROOM_CHANGES": {
                JSONObject jsonObject = (JSONObject) newValue;
                int roomId = jsonObject.getInt(RoomProperty.ROOM_ID.name());
                int index = -1;
                for (int i = 0; i < this.jsonObject.getJSONArray("rooms").length(); i++) {
                    if (this.jsonObject.getJSONArray("rooms").getJSONObject(i).getInt(RoomProperty.ROOM_ID.name()) == roomId) {
                        index = i;
                    }
                }
                if (index != -1) {
                    this.jsonObject.getJSONArray("rooms").put(index, jsonObject);
                }
                break;
            }
            case "SOLAR_PANEL_CHANGES": {
                JSONObject jsonObject = (JSONObject) newValue;
                int solarPanelId = jsonObject.getInt(SolarPanelProperty.SOLAR_PANEL_ID.name());
                int index = -1;
                for (int i = 0; i < this.jsonObject.getJSONArray("solarPanels").length(); i++) {
                    if (this.jsonObject.getJSONArray("solarPanels").getJSONObject(i).getInt(SolarPanelProperty.SOLAR_PANEL_ID.name()) == solarPanelId) {
                        index = i;
                    }
                }
                if (index != -1) {
                    this.jsonObject.getJSONArray("solarPanels").put(index, jsonObject);
                }
                break;
            }
        }
    }

    /**
     * Checks if the json object has all household properties.
     * @param jsonObject The json object to check.
     * @return True if the json object has all household properties, false otherwise.
     */
    private boolean hasHouseholdProperties(JSONObject jsonObject) {
        for (HouseholdProperty property : HouseholdProperty.values()) {
            if (!jsonObject.has(property.name())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the household properties in the json object have valid types.
     * @param jsonObject The json object to check.
     * @return True if the household properties in the json object have valid types, false otherwise.
     */
    private boolean areHouseholdPropertiesValidTypes(JSONObject jsonObject) {
        for (HouseholdProperty property : HouseholdProperty.values()) {
            Object value = jsonObject.get(property.name());
            if (!property.isValidType(value)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Creates a household object from the json object.
     * @param jsonObject The json object to create the household object from.
     * @return The household object created from the json object.
     */
    private Household createHousehold(JSONObject jsonObject) {
        String householdName = jsonObject.getString(HouseholdProperty.HOUSEHOLD_NAME.name());
        int postalCode = jsonObject.getInt(HouseholdProperty.POSTAL_CODE.name());
        int numberOfResidents = jsonObject.getInt(HouseholdProperty.NUMBER_OF_RESIDENTS.name());
        HashMap<Integer, Room> rooms = createRooms(jsonObject);
        HashMap<Integer, SolarPanel> solarPanels = createSolarPanels(jsonObject);
        this.jsonObject = jsonObject;
        Household household = new Household(householdName, (short) postalCode, numberOfResidents, rooms, solarPanels);
        household.addPropertyChangeListener(evt -> this.writeJson(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue()));
        return household;
    }

    /**
     * Creates room objects from the json object.
     * @param jsonObject The json object to create the room objects from.
     * @return The room objects created from the json object.
     */
    private HashMap<Integer, Room> createRooms(JSONObject jsonObject) {
        HashMap<Integer, Room> roomsMap = new HashMap<>();
        if (jsonObject.has("rooms")) {
            if (jsonObject.get("rooms") instanceof JSONArray) {
                JSONArray rooms = jsonObject.getJSONArray("rooms");
                for (int i = 0; i < rooms.length(); i++) {
                    if (rooms.get(i) instanceof JSONObject) {
                        RoomConverter roomConverter = new RoomConverter().addPropertyChangeListener(evt -> this.writeJson(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue()));
                        Room room = roomConverter.readJson(rooms.getJSONObject(i));
                        roomsMap.put(room.getId(), room);
                    }
                }
            } else {
                jsonObject.remove("rooms");
                jsonObject.put("rooms", new JSONArray());
            }
        } else {
            jsonObject.put("rooms", new JSONArray());
        }
        return roomsMap;
    }


    /**
     * Creates solar panel objects from the json object.
     * @param jsonObject The json object to create the solar panel objects from.
     * @return The solar panel objects created from the json object.
     */
    private HashMap<Integer, SolarPanel> createSolarPanels(JSONObject jsonObject) {
        HashMap<Integer, SolarPanel> solarPanelsMap = new HashMap<>();
        if (jsonObject.has("solarPanels")) {
            if (jsonObject.get("solarPanels") instanceof JSONArray) {
                JSONArray solarPanels = jsonObject.getJSONArray("solarPanels");
                for (int i = 0; i < solarPanels.length(); i++) {
                    if (solarPanels.get(i) instanceof JSONObject) {
                        SolarPanelConverter solarPanelConverter = new SolarPanelConverter();
                        solarPanelConverter.addPropertyChangeListener(evt -> this.writeJson(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue()));
                        SolarPanel solarPanel = solarPanelConverter.readJson(solarPanels.getJSONObject(i));
                        solarPanelsMap.put(solarPanel.getId(), solarPanel);
                    }
                }
            } else {
                jsonObject.remove("solarPanels");
                jsonObject.put("solarPanels", new JSONArray());
            }
        } else {
            jsonObject.put("solarPanels", new JSONArray());
        }
        return solarPanelsMap;
    }
}
