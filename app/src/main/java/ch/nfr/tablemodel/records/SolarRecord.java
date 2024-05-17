package ch.nfr.tablemodel.records;

import ch.nfr.tablemodel.Orientation;

/**
 * Represents the solar record.
 * @param solarPanelName the solar panel name
 * @param area the area of the solar panel
 * @param orientation the orientation of the solar panel
 */
public record SolarRecord(String solarPanelName, double area, Orientation orientation) {
}
