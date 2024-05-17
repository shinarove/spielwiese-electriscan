package ch.nfr.userinterface.controller;

import ch.nfr.tablemodel.Household;
import ch.nfr.tablemodel.TableModel;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.nio.charset.StandardCharsets;

/**
 * This class is super class for overview windows in the ElectriScan application.
 */
public abstract class OverviewController {

    /**
     * Fills the information text with the details of the selected household.
     *
     * @param informationText The text flow to display the information.
     * @param clickedItem The clicked item in the tree view.
     */
    protected void fillHouseholdInformation(TextFlow informationText, TableModel clickedItem) {
        informationText.getChildren().clear();
        Household household = (Household) clickedItem;
        String name = household.getName();
        short postalCode = household.getPostalCode();
        int numberOfResidents = household.getNumberOfResidents();
        int numberOfRooms = household.getNumberOfRooms();

        String fristPart = new String("Name:\t\t\t".getBytes(), StandardCharsets.UTF_8);
        String thirdPart = new String(
                ("\nPostleitzahl:\t\t" + postalCode +
                        "\nAnzahl Bewohner:\t" + numberOfResidents +
                        "\nAnzahl Räume:\t\t" + numberOfRooms)
                        .getBytes(), StandardCharsets.UTF_8);

        String information = fristPart + name + thirdPart;
        Text text = new Text(information);
        informationText.getChildren().add(text);
    }
}
