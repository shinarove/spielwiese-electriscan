package ch.zhaw.it23a.pm2.tablemodel.records;

/**
 * Represents the household record.
 * @param householdName the household name
 * @param postalCode the postal code
 * @param numberOfResidents the number of residents
 */
public record HouseholdRecord(String householdName, short postalCode, int numberOfResidents) {
}
