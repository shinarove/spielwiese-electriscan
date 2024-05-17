package ch.zhaw.it23a.pm2.tablemodel.records;

import ch.zhaw.it23a.pm2.tablemodel.Orientation;

/**
 * Represents the solar record.
 * @param solarPanelName the solar panel name
 * @param area the area of the solar panel
 * @param orientation the orientation of the solar panel
 */
public record SolarRecord(String solarPanelName, double area, Orientation orientation) {
}
