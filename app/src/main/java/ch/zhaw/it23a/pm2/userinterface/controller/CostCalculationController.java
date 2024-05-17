package ch.zhaw.it23a.pm2.userinterface.controller;

import ch.zhaw.it23a.pm2.UnknownPropertyException;
import ch.zhaw.it23a.pm2.calculatorAndConverter.CostCalculator;
import ch.zhaw.it23a.pm2.calculatorAndConverter.SolarPanelCalculator;
import ch.zhaw.it23a.pm2.calculatorAndConverter.UnitConverter;
import ch.zhaw.it23a.pm2.calculatorAndConverter.units.EnergyUnit;
import ch.zhaw.it23a.pm2.userinterface.model.CostCalculationModel;
import ch.zhaw.it23a.pm2.userinterface.model.property.CostOverviewProperty;
import ch.zhaw.it23a.pm2.userinterface.model.property.ElectriScanProperty;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Arc;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible for controlling the cost overview view in the application.
 * It manages various charts and displays related to cost, such as cost per category, cost per room,
 * production of solar panels, and total cost. It also handles user interactions related to these displays.
 * <p>
 * The class maintains a reference to a {@link CostCalculationModel} which it uses to update the charts and displays.
 * It also listens for property changes on the model to keep the view in sync with the underlying data.
 */
public class CostCalculationController {
    /** The anchor pane for the chart display */
    @FXML
    private AnchorPane chartDisplay;
    /** The label for the error output */
    @FXML
    private Label errorOutput;
    /** The pie chart for the cost per category */
    private final LabeledPieChart costPerCategory = new LabeledPieChart();
    /** The pie chart for the cost per room */
    private final LabeledPieChart costPerRoomChart = new LabeledPieChart();
    /** The category x-axis for the production solar panel chart */
    private final CategoryAxis xLineAxis = new CategoryAxis();
    /** The number y-axis for the production solar panel chart */
    private final NumberAxis yLineAxis = new NumberAxis();
    /** The line chart for the production solar panel */
    private final LineChart<String, Number> productionSolarPanelChart = new LineChart<>(xLineAxis, yLineAxis);
    /** The text flow for the total cost */
    private final TextFlow totalCost = new TextFlow();
    /** The model the controller listens to */
    private CostCalculationModel costCalculationModel;
    /** The primary stage */
    private Stage primaryStage;
    /** The current selected chart type to display */
    private ChartType currentChartType;
    /** The chart types */
    private enum ChartType {
        COST_PER_CATEGORY,
        COST_PER_ROOM,
        PRODUCTION_SOLAR_PANEL,
        TOTAL_COST
    }
    /** The current total cost record */
    CostCalculator.TotalCostRecord currentTotalCostRecord;

    /**
     * Sets the different titels for the charts and anchors them on the {@link #chartDisplay}.
     * Sets the current chart type to {@link ChartType#COST_PER_ROOM}.
     */
    public void initialize() {
        //set titel for the charts
        costPerCategory.setTitle("Kosten pro Kategorie");
        costPerRoomChart.setTitle("Kosten pro Raum");
        productionSolarPanelChart.setTitle("Produktion Solarpanel");

        xLineAxis.setLabel("Monat");
        yLineAxis.setLabel("Produktion in kWh");

        currentChartType = ChartType.COST_PER_ROOM;
        chartDisplay.getChildren().add(costPerRoomChart);
        anchorCostPerRoomChart();
        anchorCostPerCategoryChart();
        anchorProductionSolarPanelChart();
        anchorTotalCost();
    }

    /**
     * Sets the model and the primary stage, sets a property change listener on the model to update the charts.
     * If the received property is null, an {@link UnknownPropertyException} is thrown.
     *
     * @param costCalculationModel the model to set
     * @param primaryStage the primary stage to set
     */
    public void setModelAndPrimaryStage(CostCalculationModel costCalculationModel, Stage primaryStage) {
        this.costCalculationModel = costCalculationModel;
        this.primaryStage = primaryStage;

        this.costCalculationModel.addPropertyChangeListenerElectriScan(evt -> {
            switch (ElectriScanProperty.parseProperty(evt.getPropertyName())) {
                case LOAD_HOUSEHOLD, REMOVE_HOUSEHOLD -> {
                    costPerCategory.getData().clear();
                    costPerRoomChart.getData().clear();
                    productionSolarPanelChart.getData().clear();
                    totalCost.getChildren().clear();
                }
                case EDIT_HOUSEHOLD -> {
                    if (currentChartType == ChartType.TOTAL_COST && currentTotalCostRecord != null) {
                        makeTextForTotalCost(currentTotalCostRecord);
                    }
                }
                case LOAD_DIRECTORY -> {}
                case null -> throw new UnknownPropertyException("Unknown property: " + evt.getPropertyName());
            }
        });

        this.costCalculationModel.addPropertyChangeListener(evt -> {
            switch (CostOverviewProperty.parseProperty(evt.getPropertyName())) {
                case UPDATE_CHARTS -> {
                    //clear all charts and texts
                    costPerCategory.getData().clear();
                    costPerRoomChart.getData().clear();
                    productionSolarPanelChart.getData().clear();
                    totalCost.getChildren().clear();

                    CostCalculator.CalculationRecordWrapper result = (CostCalculator.CalculationRecordWrapper) evt.getNewValue();
                    List<CostCalculator.DeviceCalculationRecord> deviceCalculationRecords = result.deviceCalculationRecords();
                    List<CostCalculator.RoomCalculationRecord> roomCalculationRecords = result.roomCalculationRecords();
                    SolarPanelCalculator.TotalSolarCalculationWrapper solarTotalSolarCalculationWrapper = result.totalSolarCalculationRecord();
                    currentTotalCostRecord = result.totalCostRecord();

                    for (CostCalculator.DeviceCalculationRecord deviceCalculationRecord : deviceCalculationRecords) {
                        costPerCategory.getData().add(new PieChart.Data(encodeTextInUTF8(deviceCalculationRecord.deviceCategory().getGermanName()),
                                deviceCalculationRecord.electricityCostInRp()));
                    }
                    for (CostCalculator.RoomCalculationRecord roomCalculationRecord : roomCalculationRecords) {
                        costPerRoomChart.getData().add(new PieChart.Data(roomCalculationRecord.room().getName(),
                                roomCalculationRecord.electricityCostInRp()));
                    }
                    for (SolarPanelCalculator.SolarCalculationRecord solarCalculationRecord : solarTotalSolarCalculationWrapper.solarCalculationRecords()) {
                        XYChart.Series<String, Number> series = new XYChart.Series<>();
                        series.setName(solarCalculationRecord.solarPanel().getName());
                        solarCalculationRecord.monthCalculationRecords().stream()
                                .sorted(Comparator.comparingInt(record -> record.month().getMonthNumber()))
                                .forEach(productionRecord -> series.getData().add(
                                        new XYChart.Data<>(encodeTextInUTF8(productionRecord.month().getGermanName()), productionRecord.productionInKiloWattHour())));
                        productionSolarPanelChart.getData().add(series);
                    }
                    makeTextForTotalCost(currentTotalCostRecord);
                }
                case null -> throw new UnknownPropertyException("Unknown property: " + evt.getPropertyName());
            }
        });
    }

    /**
     * Makes the text for the total cost.
     */
    private void makeTextForTotalCost(CostCalculator.TotalCostRecord totalCostRecord) {
        double yearlyCorrectedConsumptionInKiloWattHour = UnitConverter.convertWattSecondsTo(totalCostRecord.yearlyCorrectedConsumptionInWattSeconds(), EnergyUnit.KILOWATT_HOUR);
        double yearlyConsumptionInKiloWattHour = UnitConverter.convertWattSecondsTo(totalCostRecord.yearlyConsumptionInWattSeconds(), EnergyUnit.KILOWATT_HOUR);
        double yearlyProductionInKiloWattHour = UnitConverter.convertWattSecondsTo(totalCostRecord.yearlyProductionInWattSeconds(), EnergyUnit.KILOWATT_HOUR);
        double yearlyElectricityCostInCHF = totalCostRecord.yearlyElectricityCostInRp() / 100.0;
        double ElectricityCostPerKiloWattHour = totalCostRecord.electricityCostInRpPerkWh() / 100.0;


        String householdInformation = "Haushalt:\t" + costCalculationModel.getHousehold().getName() + "\n" +
                "Postleitzahl:\t" + costCalculationModel.getHousehold().getPostalCode() + "\n" +
                "Anzahl Bewohner:\t" + costCalculationModel.getHousehold().getNumberOfResidents() + "\n\n";
        String yearlyCorrectedConsumption = "Jährlicher Verbrauch:\t" + yearlyCorrectedConsumptionInKiloWattHour + " kWh (mit Bezug auf Stromerzeugung)\n";
        String yearlyConsumption = "Jährlicher Verbrauch:\t" + yearlyConsumptionInKiloWattHour + " kWh\n";
        String yearlyProduction = "Jährliche Produktion:\t" + yearlyProductionInKiloWattHour + " kWh\n";
        String yearlyElectricityCost = "Jährliche Stromkosten:\t" + yearlyElectricityCostInCHF + " CHF\n";
        String ElectricityCost = "Stromtarif für pro kWh:\t" + ElectricityCostPerKiloWattHour + " CHF\n";

        String text = encodeTextInUTF8(householdInformation + yearlyCorrectedConsumption + yearlyConsumption + yearlyProduction + yearlyElectricityCost + ElectricityCost);
        totalCost.getChildren().add(new Text(text));
    }

    /**
     * Shows the cost per category chart.
     *
     * @param event the event that triggered the action
     */
    @FXML
    void showCostPerRoom(ActionEvent event) {
        if (currentChartType != ChartType.COST_PER_ROOM) {
            chartDisplay.getChildren().clear();
            chartDisplay.getChildren().add(costPerRoomChart);
            currentChartType = ChartType.COST_PER_ROOM;
        }
    }

    /**
     * Shows the cost per room chart.
     *
     * @param event the event that triggered the action
     */
    @FXML
    void showProductionSolarPanel(ActionEvent event) {
        if (currentChartType != ChartType.PRODUCTION_SOLAR_PANEL) {
            chartDisplay.getChildren().clear();
            chartDisplay.getChildren().add(productionSolarPanelChart);
            currentChartType = ChartType.PRODUCTION_SOLAR_PANEL;
        }
    }

    /**
     * Shows the total cost.
     *
     * @param event the event that triggered the action
     */
    @FXML
    void showTotalCost(ActionEvent event) {
        if (currentChartType != ChartType.TOTAL_COST) {
            chartDisplay.getChildren().clear();
            chartDisplay.getChildren().add(totalCost);
            currentChartType = ChartType.TOTAL_COST;
        }
    }

    /**
     * Shows the cost per category chart.
     *
     * @param event the event that triggered the action
     */
    @FXML
    void showCostPerCategory(ActionEvent event) {
        if (currentChartType != ChartType.COST_PER_CATEGORY) {
            chartDisplay.getChildren().clear();
            chartDisplay.getChildren().add(costPerCategory);
            currentChartType = ChartType.COST_PER_CATEGORY;
        }
    }

    /**
     * Updates the charts.
     *
     * @param event the event that triggered the action
     */
    @FXML
    void updateCharts(ActionEvent event) {
        costCalculationModel.updateCharts();
    }

    /**
     * Anchor the {@link #costPerCategory} to the {@link #chartDisplay}
     */
    private void anchorCostPerCategoryChart() {
        AnchorPane.setTopAnchor(costPerCategory, 0.0);
        AnchorPane.setBottomAnchor(costPerCategory, 0.0);
        AnchorPane.setLeftAnchor(costPerCategory, 0.0);
        AnchorPane.setRightAnchor(costPerCategory, 0.0);
    }

    /**
     * Anchor the {@link #costPerRoomChart} to the {@link #chartDisplay}
     */
    private void anchorCostPerRoomChart() {
        AnchorPane.setTopAnchor(costPerRoomChart, 0.0);
        AnchorPane.setBottomAnchor(costPerRoomChart, 0.0);
        AnchorPane.setLeftAnchor(costPerRoomChart, 0.0);
        AnchorPane.setRightAnchor(costPerRoomChart, 0.0);
    }

    /**
     * Anchor the {@link #productionSolarPanelChart} to the {@link #chartDisplay}
     */
    private void anchorProductionSolarPanelChart() {
        AnchorPane.setTopAnchor(productionSolarPanelChart, 0.0);
        AnchorPane.setBottomAnchor(productionSolarPanelChart, 0.0);
        AnchorPane.setLeftAnchor(productionSolarPanelChart, 0.0);
        AnchorPane.setRightAnchor(productionSolarPanelChart, 0.0);
    }

    /**
     * Anchor the {@link #totalCost} to the {@link #chartDisplay}
     */
    private void anchorTotalCost() {
        AnchorPane.setTopAnchor(totalCost, 0.0);
        AnchorPane.setBottomAnchor(totalCost, 0.0);
        AnchorPane.setLeftAnchor(totalCost, 0.0);
        AnchorPane.setRightAnchor(totalCost, 0.0);
    }

    /**
     * Encodes the text in UTF-8
     *
     * @param text the text to encode
     * @return the encoded text
     */
    private String encodeTextInUTF8(String text) {
        return new String(text.getBytes(), StandardCharsets.UTF_8);
    }

    /**
     * A pie chart that additionally displays labels with the numeric value.
     */
    private static class LabeledPieChart extends PieChart {
        /** The labels for the pie chart */
        Map<Data, Text> labels = new HashMap<>();

        /**
         * Creates a new labeled pie chart
         * Listens to changes in the data and then calls {@link #addLabels()} to add the labels.
         */
        public LabeledPieChart() {
            super();
            this.getData().addListener((ListChangeListener.Change<? extends PieChart.Data> c) -> addLabels());
        }

        /**
         * Layouts the chart children
         *
         * @param top the top
         * @param left the left
         * @param contentWidth the width of the content to display
         * @param contentHeight the height of the content to display
         */
        @Override
        protected void layoutChartChildren(double top, double left, double contentWidth, double contentHeight) {
            super.layoutChartChildren(top, left, contentWidth, contentHeight);

            double centerX = contentWidth / 2 + left;
            double centerY = contentHeight / 2 + top;

            layoutLabels(centerX, centerY);
        }

        /**
         * Adds the labels to the chart
         */
        private void addLabels() {
            for (Text label : labels.values()) {
                this.getChartChildren().remove(label);
            }
            labels.clear();
            for (Data vData : getData()) {
                final Text dataText;
                final double yValue =  vData.getPieValue() / 100.0;
                dataText = new Text(String.format("%.2f", yValue) + ".-");
                labels.put(vData, dataText);
                this.getChartChildren().add(dataText);
            }
        }

        /**
         * Layouts the labels
         *
         * @param centerX the center x
         * @param centerY the center y
         */
        private void layoutLabels(double centerX, double centerY) {
            double total = 0.0;
            for (Data d : this.getData()) {
                total += d.getPieValue();
            }
            double scale = (total != 0) ? 360 / total : 0;

            for (Map.Entry<Data, Text> entry : labels.entrySet()) {
                Data vData = entry.getKey();
                Text vText = entry.getValue();

                Region vNode = (Region) vData.getNode();
                Arc arc = (Arc) vNode.getShape();

                double start = arc.getStartAngle();

                double size = (isClockwise()) ? (-scale * Math.abs(vData.getPieValue())) : (scale * Math.abs(vData.getPieValue()));
                final double angle = normalizeAngle(start + (size / 2));
                final double sproutX = calcX(angle, arc.getRadiusX() / 2, centerX);
                final double sproutY = calcY(angle, arc.getRadiusY() / 2, centerY);

                vText.relocate(
                        sproutX - vText.getBoundsInLocal().getWidth(),
                        sproutY - vText.getBoundsInLocal().getHeight());
            }
        }

        /**
         * Normalizes the angle
         *
         * @param angle the angle to normalize
         * @return the normalized angle
         */
        private static double normalizeAngle(double angle) {
            double a = angle % 360;
            if (a <= -180) {
                a += 360;
            }
            if (a > 180) {
                a -= 360;
            }
            return a;
        }

        /**
         * Calculates the x value
         *
         * @param angle the angle
         * @param radius the radius
         * @param centerX the center x
         * @return the x value
         */
        private static double calcX(double angle, double radius, double centerX) {
            return centerX + radius * Math.cos(Math.toRadians(-angle));
        }

        /**
         * Calculates the y value
         *
         * @param angle the angle
         * @param radius the radius
         * @param centerY the center y
         * @return the y value
         */
        private static double calcY(double angle, double radius, double centerY) {
            return centerY + radius * Math.sin(Math.toRadians(-angle));
        }
    }
}
