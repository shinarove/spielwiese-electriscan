package ch.zhaw.it23a.pm2.filehandler.converter;

import ch.zhaw.it23a.pm2.tablemodel.TableModel;
import org.json.JSONObject;

/**
 * This interface converts a json object to a table model and vice versa.
 */
public interface JsonConverter {
    /**
     * Reads the table model properties from the json object and creates a table model object.
     * @param jsonObject The json object to read the table model properties from.
     * @return The table model object created from the json object.
     */
    TableModel readJson(JSONObject jsonObject);

    /**
     * Converts a table model object to a json object.
     * @return The json object created from the table model object.
     */
    JSONObject toJson();

    /**
     * Writes the table model properties to the json object.
     * @param propertyName The name of the property to write.
     * @param oldValue The old value of the property.
     * @param newValue The new value of the property.
     */
    void writeJson(String propertyName,Object oldValue, Object newValue);

}
