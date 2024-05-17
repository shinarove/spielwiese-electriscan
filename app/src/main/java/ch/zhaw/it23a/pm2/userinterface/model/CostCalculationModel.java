package ch.zhaw.it23a.pm2.userinterface.model;

import ch.zhaw.it23a.pm2.calculatorAndConverter.CostCalculator;
import ch.zhaw.it23a.pm2.calculatorAndConverter.NoRegisteredDeviceException;
import ch.zhaw.it23a.pm2.calculatorAndConverter.SolarPanelCalculator;
import ch.zhaw.it23a.pm2.filehandler.ElectricityCostHandler;
import ch.zhaw.it23a.pm2.filehandler.ElectricityPriceDataException;
import ch.zhaw.it23a.pm2.filehandler.WeatherArchiveException;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.logging.Logger;

import static ch.zhaw.it23a.pm2.userinterface.model.property.CostOverviewProperty.UPDATE_CHARTS;

/**
 * Model for the cost overview view.
 */
public class CostCalculationModel extends SecondaryModel {
    /** The used Logger in this class */
    private static final Logger logger = Logger.getLogger(CostCalculationModel.class.getName());
    /** The PropertyChangeSupport for the model */
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    /** The used CostCalculator to calculate the different cost positions */
    private final CostCalculator costCalculator;

    /**
     * Constructs a new CostOverviewModel.
     */
    public CostCalculationModel() {
        super();
        String weatherArchivePath = "src/main/resources/weather-archive/weather-archive.properties";
        this.costCalculator = new CostCalculator(new SolarPanelCalculator(), weatherArchivePath);
    }

    /**
     * Updates the charts with the current data.
     * Informs the listeners about the update with the new calculation result.
     * <p>
     * If {@link NoRegisteredDeviceException}, {@link WeatherArchiveException} or {@link ElectricityPriceDataException} is thrown,
     * prints an error message to the text output.
     */
    public void updateCharts() {
        try {
            String electricityCostPath = "src/main/resources/electricity-price/electricityPrice.properties";
            CostCalculator.CalculationRecordWrapper result = costCalculator.calculateCost(getHousehold(),
                    ElectricityCostHandler.getElectricityPrice(getHousehold().getPostalCode(), electricityCostPath));
            propertyChangeSupport.firePropertyChange(UPDATE_CHARTS.name(), null, result);
            logger.info("Charts updated successfully.");
        } catch (NoRegisteredDeviceException e) {
            setMessageInTextOutput("Keine Geräte im Haushalt registriert.");
        } catch (WeatherArchiveException e) {
            setMessageInTextOutput("Fehler beim Lesen des Wetterarchivs.");
        } catch (ElectricityPriceDataException e) {
            setMessageInTextOutput("Fehler beim Lesen der Strompreisdaten.");
        }

    }

    /**
     * Adds a PropertyChangeListener to the model.
     *
     * @param listener the PropertyChangeListener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
}
