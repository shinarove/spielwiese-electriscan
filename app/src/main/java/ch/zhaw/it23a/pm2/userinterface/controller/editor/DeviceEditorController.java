package ch.zhaw.it23a.pm2.userinterface.controller.editor;

import ch.zhaw.it23a.pm2.tablemodel.device.BatteryConsumption;
import ch.zhaw.it23a.pm2.tablemodel.device.Consumption;
import ch.zhaw.it23a.pm2.tablemodel.device.DeviceCategory;
import ch.zhaw.it23a.pm2.tablemodel.device.ElectricConsumption;
import ch.zhaw.it23a.pm2.tablemodel.records.DeviceRecord;
import ch.zhaw.it23a.pm2.calculatorAndConverter.units.EnergyUnit;
import ch.zhaw.it23a.pm2.calculatorAndConverter.units.TimeUnit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.logging.Logger;

import static ch.zhaw.it23a.pm2.calculatorAndConverter.UnitConverter.*;

/**
 * This class is responsible for managing the device editor window.
 * It is used through two static methods to either create a new device record or edit an existing one.
 * The calling class can easily use the static methods to show the device editor window.
 */
public class DeviceEditorController {
    /** The used logger of this class */
    private static final Logger logger = Logger.getLogger(DeviceEditorController.class.getName());
    /** The text field for the device name. */
    @FXML
    private TextField deviceName;
    /** The choice box for the device category. */
    @FXML
    private ChoiceBox<String> deviceCategory;
    /** The text field for the usage time. */
    @FXML
    private TextField usage;
    /** The text field for the power consumption. */
    @FXML
    private TextField powerConsumption;
    /** The text field for the charging cycles. */
    @FXML
    private TextField chargingCycles;
    /** The text field for the battery capacity. */
    @FXML
    private TextField batteryCapacity;
    /** The choice box for the device category. */
    @FXML
    private ChoiceBox<String> usageUnit;
    /** The choice box for the usage time unit. */
    @FXML
    private ChoiceBox<String> usagePerUnit;
    /** The choice box for the power consumption unit. */
    @FXML
    private ChoiceBox<String> powerConsumptionUnit;
    /** The choice box for the charging cycles unit. */
    @FXML
    private ChoiceBox<String> chargingCyclesUnit;
    /** The choice box for the battery capacity unit. */
    @FXML
    private ChoiceBox<String> batteryCapacityUnit;
    /** The radio button for wired devices. */
    @FXML
    private RadioButton cableRadioButton;
    /** The radio button for battery-powered devices. */
    @FXML
    private RadioButton batteryRadioButton;
    /** The label for the error output. */
    @FXML
    private Label errorOutput;
    /** The label for the battery capacity. */
    @FXML
    private Label batteryCapacityLabel;
    /** The label for the charging cycles. */
    @FXML
    private Label chargingCycleLabel;
    /** The label for the times per unit. */
    @FXML
    private Label timesPerLabel;
    /** The label for the usage time. */
    @FXML
    private Label usageTimeLabel;
    /** The label for the usage per unit. */
    @FXML
    private Label perLabel;
    /** The label for the power consumption. */
    @FXML
    private Label powerConsumptionLabel;
    /** The temporary device record that may be returned. */
    private DeviceRecord tempDevice;
    /** The stage of the device editor window. */
    private Stage stage;

    /**
     * Initializes the device editor window.
     * It sets up the choice boxes for energy units, time units, and device categories.
     * It also sets the default values for these choice boxes and the radio buttons for the device type.
     * Furthermore, it disables the fields for battery-powered devices as the default device type is wired.
     */
    public void initialize() {
        for (EnergyUnit energyUnit : EnergyUnit.values()) {
            powerConsumptionUnit.getItems().add(energyUnit.getGermanName());
            batteryCapacityUnit.getItems().add(energyUnit.getGermanName());
            if (energyUnit == EnergyUnit.WATT_HOUR) {
                powerConsumptionUnit.setValue(energyUnit.getGermanName());
                batteryCapacityUnit.setValue(energyUnit.getGermanName());
            }
        }
        for (TimeUnit timeUnit : TimeUnit.values()) {
            usageUnit.getItems().add(timeUnit.getGermanNamePlural());
            usagePerUnit.getItems().add(timeUnit.getGermanNameSingle());
            chargingCyclesUnit.getItems().add(timeUnit.getGermanNameSingle());
            if (timeUnit == TimeUnit.DAY) {
                usageUnit.setValue(timeUnit.getGermanNamePlural());
                chargingCyclesUnit.setValue(timeUnit.getGermanNameSingle());
            } else if (timeUnit == TimeUnit.YEAR) {
                usagePerUnit.setValue(timeUnit.getGermanNameSingle());
            }
        }
        for (DeviceCategory category : DeviceCategory.values()) {
            String germanName = new String(category.getGermanName().getBytes(), StandardCharsets.UTF_8);
            deviceCategory.getItems().add(germanName);
            if (category == DeviceCategory.OTHER) {
                deviceCategory.setValue(germanName);
            }
        }
        cableRadioButton.setSelected(true);
        enableCableFields();
        disableBatteryFields();
    }

    /**
     * Displays the device editor window as a modal dialog.
     *
     * @param primaryStage The primary stage of the application.
     * @return the device record that was created in the device editor window, or null if an error occurs
     */
    public static DeviceRecord show(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(RoomEditorController.class.getResource("DeviceEditorWindow.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();

            DeviceEditorController controller = loader.getController();
            controller.stage = stage;
            controller.stage.initOwner(primaryStage);
            controller.stage.initModality(javafx.stage.Modality.WINDOW_MODAL);

            stage.setScene(scene);
            stage.setTitle(new String("Gerät hinzufügen".getBytes(), StandardCharsets.UTF_8));
            stage.setResizable(false);
            stage.showAndWait();

            return controller.tempDevice;
        } catch (IOException e) {
            logger.severe("Error loading the Device editor window: " + e.getMessage());
        }
        return null;
    }

    /**
     * Displays the device editor window as a modal dialog.
     * Sets the initial values of the device record to the text fields and choice boxes.
     *
     * @param primaryStage the primary stage of the application
     * @param deviceRecord the device record for the initialized values
     * @return the device record that may have been edited in the device editor window, or null if an error occurs
     */
    public static DeviceRecord show(Stage primaryStage, DeviceRecord deviceRecord) {
        try {
            FXMLLoader loader = new FXMLLoader(RoomEditorController.class.getResource("DeviceEditorWindow.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();

            DeviceEditorController controller = loader.getController();
            controller.stage = stage;
            controller.stage.initOwner(primaryStage);
            controller.stage.initModality(javafx.stage.Modality.WINDOW_MODAL);

            controller.deviceName.setText(deviceRecord.deviceName());
            controller.deviceCategory.setValue(new String(deviceRecord.deviceCategory().getGermanName().getBytes(), StandardCharsets.UTF_8));
            if (deviceRecord.isWired()) {
                if (!(deviceRecord.consumption() instanceof ElectricConsumption)) {
                    throw new IllegalArgumentException("Wrong consumption type");
                }
                controller.cableRadioButton.setSelected(true);
                controller.cableRadioButton.setDisable(true);
                controller.batteryRadioButton.setDisable(true);
                controller.disableBatteryFields();
                controller.enableCableFields();

                controller.setTextFields(true, deviceRecord.consumption());
            } else {
                if (!(deviceRecord.consumption() instanceof BatteryConsumption)) {
                    throw new IllegalArgumentException("Wrong consumption type");
                }
                controller.batteryRadioButton.setSelected(true);
                controller.cableRadioButton.setDisable(true);
                controller.batteryRadioButton.setDisable(true);
                controller.disableCableFields();
                controller.enableBatteryFields();

                controller.setTextFields(false, deviceRecord.consumption());
            }

            stage.setScene(scene);
            stage.setTitle(new String("Gerät hinzufügen".getBytes(), StandardCharsets.UTF_8));
            stage.setResizable(false);
            stage.showAndWait();

            return controller.tempDevice;
        } catch (IOException e) {
            logger.severe("Error loading the Device editor window: " + e.getMessage());
        }
        return null;
    }

    /**
     * Sets the text fields and choice boxes with the given consumption.
     *
     * @param isWired true if the device is a wired device
     * @param consumption the consumption of the device
     */
    private void setTextFields(boolean isWired, Consumption consumption){
        if (isWired){
            ElectricConsumption electricConsumption = (ElectricConsumption) consumption;
            TimeUnit usedTimeUnit = electricConsumption.getUsedTimeUnit();
            TimeUnit usedTimeUnitPer = electricConsumption.getUsedTimeUnitPer();
            EnergyUnit usedEnergyUnit = electricConsumption.getUsedEnergyUnit();
            usageUnit.setValue(usedTimeUnit.getGermanNamePlural());
            usagePerUnit.setValue(usedTimeUnitPer.getGermanNameSingle());
            powerConsumptionUnit.setValue(usedEnergyUnit.getGermanName());

            long usageValue = electricConsumption.getYearlyUsageInSeconds();
            long powerValue = electricConsumption.getPowerConsumptionInWattSeconds();

            double usageValueConverted = convertSecondsTo(usageValue, usedTimeUnit);
            double usedUsageValue = usageValueConverted / ((double) TimeUnit.YEAR.getFactor() / (double) usedTimeUnitPer.getFactor());
            double usedPowerValue = convertWattSecondsTo(powerValue, usedEnergyUnit);

            usage.setText(String.format("%.1f", usedUsageValue));
            powerConsumption.setText(String.valueOf(usedPowerValue));
        } else {
            BatteryConsumption batteryConsumption = (BatteryConsumption) consumption;
            TimeUnit usedTimeUnit = batteryConsumption.getUsedTimeUnit();
            EnergyUnit usedEnergyUnit = batteryConsumption.getUsedEnergyUnit();
            chargingCyclesUnit.setValue(usedTimeUnit.getGermanNamePlural());
            batteryCapacityUnit.setValue(usedEnergyUnit.getGermanName());

            long yearlyChargingCycles = batteryConsumption.getChargingCyclesPerYear();
            long batteryCapacityValue = batteryConsumption.getCapacityInWattSeconds();

            long usedChargingCyclesValue = Math.round(yearlyChargingCycles * ((double) usedTimeUnit.getFactor() / TimeUnit.YEAR.getFactor()));
            double usedBatteryCapacityValue = convertWattSecondsTo(batteryCapacityValue, usedEnergyUnit);

            chargingCycles.setText(String.valueOf(usedChargingCyclesValue));
            batteryCapacity.setText(String.format("%.1f", usedBatteryCapacityValue));
        }
    }

    /**
     * Calls {@link #disableBatteryFields()} and {@link #enableCableFields()}.
     *
     * @param actionEvent the event that triggered the selection
     */
    @FXML
    public void checkedWired(ActionEvent actionEvent) {
        disableBatteryFields();
        enableCableFields();
    }

    /**
     * Calls {@link #enableBatteryFields()} and {@link #disableCableFields()}.
     *
     * @param actionEvent the event that triggered the selection
     */
    @FXML
    public void checkedBatteryPowered(ActionEvent actionEvent) {
        enableBatteryFields();
        disableCableFields();
    }

    /**
     * Disables the text fields and choice boxes which are used for a battery powered device.
     */
    private void disableBatteryFields() {
        chargingCycles.setDisable(true);
        batteryCapacity.setDisable(true);
        chargingCyclesUnit.setDisable(true);
        batteryCapacityUnit.setDisable(true);
        batteryCapacityLabel.setDisable(true);
        chargingCycleLabel.setDisable(true);
        timesPerLabel.setDisable(true);
    }

    /**
     * Disables the text fields and choice boxes which are used for a cabled device.
     */
    private void disableCableFields() {
        usageTimeLabel.setDisable(true);
        usage.setDisable(true);
        usageUnit.setDisable(true);
        perLabel.setDisable(true);
        usagePerUnit.setDisable(true);
        powerConsumptionLabel.setDisable(true);
        powerConsumption.setDisable(true);
        powerConsumptionUnit.setDisable(true);
    }

    /**
     * Enables the text fields and choice boxes which are used for a battery powered device.
     */
    private void enableBatteryFields() {
        chargingCycles.setDisable(false);
        batteryCapacity.setDisable(false);
        chargingCyclesUnit.setDisable(false);
        batteryCapacityUnit.setDisable(false);
        batteryCapacityLabel.setDisable(false);
        chargingCycleLabel.setDisable(false);
        timesPerLabel.setDisable(false);
    }

    /**
     * Enables the text fields and choice boxes which are used for a cabled device.
     */
    private void enableCableFields() {
        usageTimeLabel.setDisable(false);
        usage.setDisable(false);
        usageUnit.setDisable(false);
        perLabel.setDisable(false);
        usagePerUnit.setDisable(false);
        powerConsumptionLabel.setDisable(false);
        powerConsumption.setDisable(false);
        powerConsumptionUnit.setDisable(false);
    }

    /**
     * Closes the window without saving the device record.
     *
     * @param actionEvent the event that triggered the cancel
     */
    @FXML
    public void cancel(ActionEvent actionEvent) {
        stage.close();
    }

    /**
     * Saves the device record if all fields are filled out correctly.
     *
     * @param actionEvent the event that triggered the save
     */
    @FXML
    public void save(ActionEvent actionEvent) {
        if (areFieldsEmpty()) {
            setErrorOutput("Bitte füllen Sie alle Felder aus.");
        } else if (!numbersMatchFormat()) {
            setErrorOutput("Bitte geben Sie Zahlen oder Daten im korrekten Format ein.");
        } else if (cableRadioButton.isSelected()) {
            if (!usageIsSmallerEqualThanUsagePer()) {
                setErrorOutput("Nutzungszeit muss kleiner als Nutzungszeit pro" + usagePerUnit.getValue() + "sein");
            } else {
                DeviceCategory category = DeviceCategory.parseDeviceCategory(deviceCategory.getValue());

                double usageValue = Double.parseDouble(usage.getText());
                double powerConsumptionValue = Double.parseDouble(powerConsumption.getText());
                TimeUnit usageTimeUnit = TimeUnit.parseTimeUnit(usageUnit.getValue());
                TimeUnit usageTimePerUnit = TimeUnit.parseTimeUnit(usagePerUnit.getValue());
                EnergyUnit powerConsumptionUnit = EnergyUnit.parseEnergyUnit(this.powerConsumptionUnit.getValue());

                assert usageTimePerUnit != null;
                double yearlyUsageValue = usageValue * (double) (TimeUnit.YEAR.getFactor() / usageTimePerUnit.getFactor());

                long yearlyUsageInSeconds = convertTimeToSeconds(yearlyUsageValue, usageTimeUnit);
                long powerConsumptionInWattSeconds = convertEnergyToWattSeconds(powerConsumptionValue, powerConsumptionUnit);

                ElectricConsumption electricConsumption =
                        new ElectricConsumption(powerConsumptionInWattSeconds,
                                yearlyUsageInSeconds,
                                usageTimeUnit,
                                usageTimePerUnit,
                                powerConsumptionUnit);

                tempDevice = new DeviceRecord(true, deviceName.getText(), category, electricConsumption);
                stage.close();
            }
        } else {
            DeviceCategory category = DeviceCategory.parseDeviceCategory(deviceCategory.getValue());

            double chargingCyclesValue = Double.parseDouble(chargingCycles.getText());
            double batteryCapacityValue = Double.parseDouble(batteryCapacity.getText());
            TimeUnit chargingCyclesTimeUnit = TimeUnit.parseTimeUnit(chargingCyclesUnit.getValue());
            EnergyUnit batteryCapacityUnit = EnergyUnit.parseEnergyUnit(this.batteryCapacityUnit.getValue());

            assert chargingCyclesTimeUnit != null;
            long yearlyChargingCycles = (long) (chargingCyclesValue * (TimeUnit.YEAR.getFactor() / chargingCyclesTimeUnit.getFactor()));
            long batteryCapacityInWattSeconds = convertEnergyToWattSeconds(batteryCapacityValue, batteryCapacityUnit);

            BatteryConsumption batteryConsumption =
                    new BatteryConsumption(yearlyChargingCycles,
                            batteryCapacityInWattSeconds,
                            chargingCyclesTimeUnit,
                            batteryCapacityUnit);

            tempDevice = new DeviceRecord(false, deviceName.getText(), category, batteryConsumption);
            stage.close();
        }
    }

    /**
     * Checks if the text fields and choice boxes are empty.
     *
     * @return true if the fields are empty
     */
    private boolean areFieldsEmpty(){
        boolean areStandardFieldsEmpty = deviceName.getText().isEmpty() || deviceCategory.getValue().isEmpty();

        if (cableRadioButton.isSelected()) {
            return areStandardFieldsEmpty || usage.getText().isEmpty() || powerConsumption.getText().isEmpty() ||
                    usageUnit.getValue().isEmpty() || usagePerUnit.getValue().isEmpty() || powerConsumptionUnit.getValue().isEmpty();
        } else {
            return areStandardFieldsEmpty || chargingCycles.getText().isEmpty() || batteryCapacity.getText().isEmpty() ||
                    chargingCyclesUnit.getValue().isEmpty() || batteryCapacityUnit.getValue().isEmpty();
        }
    }

    /**
     * Checks if the {@link #usageUnit} is smaller or equal than the {@link #usagePerUnit},
     * it also checks if the given value in {@link #usage} is smaller or equal than the factor from {@link #usagePerUnit}.
     * Which means 365 days per year is valid but 366 days per year is not.
     * <p>
     * This method throws an {@link NullPointerException} if the {@link #usage}, {@link #usageUnit} or {@link #usagePerUnit} is not set.
     *
     * @return true if the usage is smaller than the usage per unit
     */
    private boolean usageIsSmallerEqualThanUsagePer(){
        double usageValue = Double.parseDouble(Objects.requireNonNull(usage.getText()));
        TimeUnit usageTimeUnit = Objects.requireNonNull(TimeUnit.parseTimeUnit(usageUnit.getValue()));
        TimeUnit usageTimePerUnit = Objects.requireNonNull(TimeUnit.parseTimeUnit(usagePerUnit.getValue()));
        double timeUnitFactor = (double) usageTimePerUnit.getFactor() / usageTimeUnit.getFactor();
        boolean isValueSmaller = usageValue <= timeUnitFactor;


        return usageTimeUnit.getFactor() <= usageTimePerUnit.getFactor() && isValueSmaller;
    }

    /**
     * Checks if the {@link #chargingCycles} matches an integer number and if
     * the {@link #usage}, {@link #powerConsumption} and {@link #chargingCycles} matches a decimal number.
     *
     * @return true if the text fields match the format
     */
    private boolean numbersMatchFormat(){
        String decimalRegex = "\\d+(?:\\.\\d)?";
        String integerRegex = "[1-9][0-9]*";
        if (cableRadioButton.isSelected()) {
            return usage.getText().matches(decimalRegex) && powerConsumption.getText().matches(decimalRegex);
        } else {
            return chargingCycles.getText().matches(integerRegex) && batteryCapacity.getText().matches(decimalRegex);
        }
    }

    /**
     * Sets the error output of the given message on {@link #errorOutput} label.
     */
    private void setErrorOutput(String message) {
        errorOutput.setText(new String(message.getBytes(), StandardCharsets.UTF_8));
    }
}
