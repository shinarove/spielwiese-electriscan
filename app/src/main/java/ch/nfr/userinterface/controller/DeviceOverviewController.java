package ch.nfr.userinterface.controller;

import ch.nfr.tablemodel.Household;
import ch.nfr.tablemodel.Room;
import ch.nfr.tablemodel.TableModel;
import ch.nfr.tablemodel.device.Device;
import ch.nfr.tablemodel.device.DeviceCategory;
import ch.nfr.tablemodel.device.MobileDevice;
import ch.nfr.tablemodel.device.WiredDevice;
import ch.nfr.UnknownPropertyException;
import ch.nfr.calculator.converter.UnitConverter;
import ch.nfr.calculator.units.EnergyUnit;
import ch.nfr.calculator.units.TimeUnit;
import ch.nfr.userinterface.model.ElectriScanModel;
import ch.nfr.userinterface.model.property.ElectriScanProperty;
import ch.nfr.tablemodel.records.DeviceRecord;
import ch.nfr.userinterface.controller.editor.DeviceEditorController;
import ch.nfr.userinterface.controller.editor.RoomEditorController;
import ch.nfr.tablemodel.records.RoomRecord;
import ch.nfr.userinterface.model.DeviceOverviewModel;
import ch.nfr.userinterface.model.property.DeviceOverviewProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;


/**
 * The DeviceOverviewController class is responsible for handling the user interactions in the device overview window.
 * It provides methods for adding, editing, and removing rooms and devices.
 * It also displays the details of the selected item in the tree view.
 */
public class DeviceOverviewController extends OverviewController {
    /** The logger for the DeviceOverviewController class. */
    private static final Logger logger = Logger.getLogger(DeviceOverviewController.class.getName());
    /** The label for the information. */
    @FXML
    private Label informationLabel;
    /** The text flow for the device information. */
    @FXML
    private TextFlow informationText;
    /** The label for the error output. */
    @FXML
    private Label errorOutput;
    /** The tree view for the room and device overview. */
    @FXML
    private TreeView<TableModel> roomAndDeviceOverview;
    /** The root item for the tree view. */
    @FXML
    private TreeItem<TableModel> rootHousehold;
    /** The primary stage of the ElectriScan application. */
    private Stage primaryStage;
    /** The model for the device overview. */
    private DeviceOverviewModel deviceOverviewModel;
    /** The clicked item in the tree view. */
    private TableModel clickedItem;

    /**
     * Initializes the device overview window.
     * This method sets the root {@link #rootHousehold} for the tree view
     * and adds a listener to the tree view to update the {@link #informationText} when an item is clicked.
     */
    public void initialize() {
        rootHousehold = new TreeItem<>();
        rootHousehold.setExpanded(true);
        roomAndDeviceOverview.setRoot(rootHousehold);

        roomAndDeviceOverview.setOnMouseClicked(event -> {
            errorOutput.setText("");
            setClickedItem();
        });
    }

    /**
     * Displays the {@link #informationText} with the details of the selected item in the tree view.
     */
    private void setClickedItem() {
        clickedItem = roomAndDeviceOverview.getSelectionModel().getSelectedItem().getValue();
        if (clickedItemIsHousehold()) {
            informationLabel.setText(getStringInUTF8("Haushalt Informationen:"));
            fillHouseholdInformation(informationText, clickedItem);
        } else if (clickedItemIsRoom()) {
            informationLabel.setText(getStringInUTF8("Raum Informationen:"));
            fillRoomInformation();
        } else if (clickedItemIsDevice()) {
            informationLabel.setText(getStringInUTF8("Geräte Informationen:"));
            fillDeviceInformation();
        }
    }

    /**
     * Fills the information text with the details of the selected room.
     * This method is called when a room is selected in the tree view.
     * It retrieves the room details such as name, room type, area, and number of devices,
     * and displays them in the information text.
     */
    private void fillRoomInformation(){
        informationText.getChildren().clear();
        Room room = (Room) clickedItem;
        String name = room.getName();
        String roomType = room.getRoomType().getGermanName();
        double area = room.getRoomSize();
        int numberOfDevices = room.getNumberOfDevices();

        String firstPart = new String("Name:\t\t\t".getBytes(), StandardCharsets.UTF_8);
        String thirdPart = new String(
                ("\nRaumtyp:\t\t\t" + roomType +
                        "\nFläche:\t\t\t" + area + " m²" +
                        "\nAnzahl Geräte:\t\t" + numberOfDevices)
                        .getBytes(), StandardCharsets.UTF_8);
        String information = firstPart + name + thirdPart;
        Text text = new Text(information);
        informationText.getChildren().add(text);
        logger.info("Room information displayed.");
    }

    /**
     * Fills the information text with the details of the selected device.
     * This method is called when a device is selected in the tree view.
     * It retrieves the device details such as name, category, yearly consumption, power consumption, and usage time,
     * and displays them in the information text.
     * It handles both wired and mobile devices.
     */
    private void fillDeviceInformation(){
        informationText.getChildren().clear();
        String decimalFormat = "%.1f";

        Device device = (Device) this.clickedItem;
        String name = device.getName();
        DeviceCategory category = device.getCategory();
        if (device instanceof WiredDevice wiredDevice) {
            long yearlyConsumptionInWattSeconds = wiredDevice.getElectricConsumption().getYearlyConsumptionInWattSeconds();
            long powerConsumptionInWattSeconds = wiredDevice.getElectricConsumption().getPowerConsumptionInWattSeconds();
            long yearlyUsageInSeconds = wiredDevice.getElectricConsumption().getYearlyUsageInSeconds();
            EnergyUnit energyUnit = wiredDevice.getElectricConsumption().getUsedEnergyUnit();
            TimeUnit timeUnit = wiredDevice.getElectricConsumption().getUsedTimeUnit();
            TimeUnit timeUnitPer = wiredDevice.getElectricConsumption().getUsedTimeUnitPer();

            String convertedYearlyConsumption = String.format(decimalFormat, UnitConverter
                    .convertWattSecondsTo(yearlyConsumptionInWattSeconds, EnergyUnit.KILOWATT_HOUR));
            String convertedPowerConsumption = String.format(decimalFormat, UnitConverter
                    .convertWattSecondsTo(powerConsumptionInWattSeconds, energyUnit));
            String convertedUsage = String.format(decimalFormat, UnitConverter.convertSecondsTo(yearlyUsageInSeconds, timeUnit) / ((double) TimeUnit.YEAR.getFactor() / (double) timeUnitPer.getFactor()));

            String firstPart = new String("Name:\t\t\t\t\t".getBytes(), StandardCharsets.UTF_8);
            String thirdPart = new String(
                    ("\nKategorie:\t\t\t\t" + category.getGermanName() +
                            "\nJährlicher Verbrauch:\t\t" + convertedYearlyConsumption + " Kilowattstunden" +
                            "\nStromverbrauch:\t\t\t" + convertedPowerConsumption + " " + energyUnit.getGermanName() +
                            "\nAngegebene Nutzzeit:\t\t" + convertedUsage + " " + timeUnit.getGermanNamePlural() + " pro " + timeUnitPer.getGermanNameSingle() +
                            "\nNutzzeit pro Jahr:\t\t\t" + ((double) yearlyUsageInSeconds / timeUnit.getFactor()) + " " + timeUnit.getGermanNamePlural())
                            .getBytes(), StandardCharsets.UTF_8);

            String information = firstPart + name + thirdPart;
            Text text = new Text(information);
            informationText.getChildren().add(text);
        } else {
            MobileDevice mobileDevice = (MobileDevice) device;

            long yearlyConsumptionInWattSeconds = mobileDevice.getBatteryConsumption().getYearlyConsumptionInWattSeconds();
            long capacityInWattSeconds = mobileDevice.getBatteryConsumption().getCapacityInWattSeconds();
            long chargingCyclesPerYear = mobileDevice.getBatteryConsumption().getChargingCyclesPerYear();
            EnergyUnit energyUnit = mobileDevice.getBatteryConsumption().getUsedEnergyUnit();

            String convertedYearlyConsumption = String.format(decimalFormat, UnitConverter.convertWattSecondsTo(yearlyConsumptionInWattSeconds, EnergyUnit.KILOWATT_HOUR));
            String convertedCapacity = String.format(decimalFormat, UnitConverter.convertWattSecondsTo(capacityInWattSeconds, energyUnit));

            String firstPart = new String("Name:\t\t\t\t\t".getBytes(), StandardCharsets.UTF_8);
            String thirdPart = new String(
                    ("\nKategorie:\t\t\t\t" + category.getGermanName() +
                            "\nJährlicher Verbrauch:\t\t" + convertedYearlyConsumption + " Kilowattstunden" +
                            "\nLadezyklen pro Jahr:\t\t" + chargingCyclesPerYear +
                            "\nAngegebene Kapazität:\t\t" + convertedCapacity + " " + energyUnit.getGermanName())
                            .getBytes(), StandardCharsets.UTF_8);

            String information = firstPart + name + thirdPart;
            Text text = new Text(information);
            informationText.getChildren().add(text);
        }
    }

    /**
     * Sets the model and primary stage.
     * This method is used to initialize the DeviceOverviewController with the model and primary stage.
     * It also adds property change listeners to the model to handle changes in the {@link ElectriScanModel} and {@link DeviceOverviewModel}.
     *
     * @param deviceOverviewModel the model for the device overview
     * @param primaryStage the primary stage of the ElectriScan application
     */
    public void setModelAndPrimaryStage(DeviceOverviewModel deviceOverviewModel, Stage primaryStage) {
        this.deviceOverviewModel = deviceOverviewModel;
        this.primaryStage = primaryStage;

        this.deviceOverviewModel.addPropertyChangeListenerElectriScan(evt -> {
            switch (ElectriScanProperty.parseProperty(evt.getPropertyName())) {
                case LOAD_HOUSEHOLD -> {
                    Household household = (Household) evt.getNewValue();
                    rootHousehold.getChildren().clear();
                    rootHousehold.setValue(household);

                    buildTreeView(household);
                    informationLabel.setText("");
                    informationText.getChildren().clear();
                }
                case EDIT_HOUSEHOLD -> {
                    if (clickedItemIsHousehold()) {
                        fillHouseholdInformation(informationText, clickedItem);
                    }
                    roomAndDeviceOverview.refresh();
                }
                case LOAD_DIRECTORY -> {}
                case REMOVE_HOUSEHOLD -> {
                    rootHousehold.getChildren().clear();
                    informationLabel.setText("");
                    informationText.getChildren().clear();
                }
                case null -> throw new UnknownPropertyException("Unknown property: " + evt.getPropertyName());
            }
        });


        this.deviceOverviewModel.addPropertyChangeListener(evt -> {
            switch (DeviceOverviewProperty.parseProperty(evt.getPropertyName())) {
                case ADD_ROOM -> {
                    Room room = (Room) evt.getNewValue();
                    TreeItem<TableModel> roomItem = new TreeItem<>(room);
                    rootHousehold.getChildren().add(roomItem);
                }
                case EDIT_ROOM -> fillRoomInformation();
                case REMOVE_ROOM -> {
                    int roomId = ((Room) evt.getOldValue()).getId();
                    rootHousehold.getChildren().removeIf(roomItem -> ((Room) roomItem.getValue()).getId() == roomId);
                }
                case ADD_DEVICE -> {
                    Device device = (Device) evt.getNewValue();
                    TreeItem<TableModel> deviceItem = new TreeItem<>(device);
                    for(TreeItem<TableModel> roomItem : rootHousehold.getChildren()) {
                        if (((Room) roomItem.getValue()).getId() == device.getOwnerId()) {
                            roomItem.getChildren().add(deviceItem);
                        }
                    }
                }
                case EDIT_DEVICE -> fillDeviceInformation();
                case REMOVE_DEVICE -> {
                    int ownerId = ((Device) evt.getOldValue()).getOwnerId();
                    int deviceId = ((Device) evt.getOldValue()).getId();
                    for(TreeItem<TableModel> roomItem : rootHousehold.getChildren()) {
                        if (((Room) roomItem.getValue()).getId() == ownerId) {
                            roomItem.getChildren().removeIf(deviceItem -> ((Device) deviceItem.getValue()).getId() == deviceId);
                        }
                    }
                }
                case null -> throw new UnknownPropertyException("Unknown property: " + evt.getPropertyName());
            }
        });
    }

    /**
     * Builds the tree view for the household.
     * This method is called when a household is loaded.
     * It iterates over all rooms in the household and adds them to the tree view.
     * For each room, it iterates over all devices in the room and adds them to the tree view under the corresponding room.
     *
     * @param household the household to build the tree view for
     */
    private void buildTreeView(Household household) {
        for (Room room : household.getAllRooms()) {
            TreeItem<TableModel> roomItem = new TreeItem<>(room);
            roomItem.setExpanded(true);
            rootHousehold.getChildren().add(roomItem);

            for (Device device : room.getAllDevices()) {
                TreeItem<TableModel> deviceItem = new TreeItem<>(device);
                roomItem.getChildren().add(deviceItem);
            }
        }
        logger.info("Tree view for the household built.");
    }

    /**
     * Handles the action event for adding a room.
     * Calls the methode {@link RoomEditorController#show(Stage)} to open the room editor.
     * If the room record is not null after adding, it adds the room to the model.
     *
     * @param event the action event
     */
    @FXML
    void addRoom(ActionEvent event) {
        RoomRecord tempRoom = RoomEditorController.show(primaryStage);
        if (tempRoom != null) {
            deviceOverviewModel.addRoom(tempRoom);
        }
    }

    /**
     * Handles the action event for editing a room.
     * It first checks if a room is selected in the tree view. If not, it sets an error message.
     * If a room is selected, calls the {@link RoomEditorController#show(Stage, RoomRecord)} method to open the room editor.
     * If the room record is not null after editing, it edits the room in the model.
     *
     * @param event the action event
     */
    @FXML
    void editRoom(ActionEvent event) {
        if (!clickedItemIsRoom()) {
            setErrorOutput("Bitte wählen Sie einen Raum aus!");
        } else {
            Room room = (Room) clickedItem;
            RoomRecord tempRoom = RoomEditorController.show(primaryStage, room.toRoomRecord());
            if (tempRoom != null) {
                deviceOverviewModel.editRoom(room.getId(), tempRoom);
            }
            roomAndDeviceOverview.refresh();
        }
    }

    /**
     * Handles the action event for removing a room.
     * It first checks if a room is selected in the tree view. If not, it sets an error message.
     * If a room is selected, shows a warning message to the user. If the user accept the warning,
     * it deletes the room and all devices in it.
     *
     * @param event the action event
     */
    @FXML
    void removeRoom(ActionEvent event) {
        if (!clickedItemIsRoom()) {
            setErrorOutput("Bitte wählen Sie einen Raum aus!");
        } else {
            primaryStage.getScene().getRoot().setDisable(true);
            String warningMessage = "Möchtest du den Raum wirklich löschen?\n" +
                    "Alle darin enthaltene Geräte werden unwiderruflich gelöscht!";
            boolean changeAccepted = WarningController.showWarning(warningMessage);
            primaryStage.getScene().getRoot().setDisable(false);

            if (changeAccepted) {
                deviceOverviewModel.removeRoom(((Room) clickedItem).getId());
                setClickedItem();
            } else {
                setErrorOutput("Löschen des Raumes abgebrochen!");
            }
        }
    }

    /**
     * Handles the action event for adding a device.
     * It first checks if a room is selected in the tree view. If not, it sets an error message.
     * If a room is selected, calls {@link DeviceEditorController#show(Stage)} to open the device editor.
     * If the device record is not null after adding, it adds the device to the model.
     *
     * @param event the action event
     */
    @FXML
    void addDevice(ActionEvent event) {
        if (!clickedItemIsRoom()) {
            setErrorOutput("Bitte wählen Sie einen Raum aus!");
        } else {
            DeviceRecord tempDevice = DeviceEditorController.show(primaryStage);

            if (tempDevice != null) {
                int roomId = ((Room) clickedItem).getId();
                deviceOverviewModel.addDevice(roomId, tempDevice);
            }
        }
    }

    /**
     * Handles the action event for editing a device.
     * It first checks if a device is selected in the tree view. If not, it sets an error message.
     * If a device is selected, calls {@link DeviceEditorController#show(Stage, DeviceRecord)} to open the device editor.
     * If the device record is not null after editing, it edits the device in the model.
     *
     * @param event the action event
     */
    @FXML
    void editDevice(ActionEvent event) {
        if (!clickedItemIsDevice()) {
            setErrorOutput("Bitte wählen Sie ein Gerät aus!");
        } else {
            Device clickedDevice = (Device) clickedItem;
            DeviceRecord tempDevice = DeviceEditorController.show(primaryStage, clickedDevice.toDeviceRecord());
            if (tempDevice != null) {
                int roomId = clickedDevice.getOwnerId();
                int deviceId = clickedDevice.getId();
                deviceOverviewModel.editDevice(roomId, deviceId, tempDevice);
            }
            roomAndDeviceOverview.refresh();
        }
    }

    /**
     * Handles the action event for removing a device.
     * It first checks if a device is selected in the tree view. If not, it sets an error message.
     * If a device is selected, shows a warning message to the user.
     * If the user accepts the warning, it deletes the device.
     *
     * @param event the action event
     */
    @FXML
    void removeDevice(ActionEvent event) {
        if (!clickedItemIsDevice()) {
            setErrorOutput("Bitte wählen Sie ein Gerät aus!");
        } else {
            primaryStage.getScene().getRoot().setDisable(true);
            String warningMessage = "Möchtest du dieses Gerät wirklich löschen?\n" +
                    "Das Gerät wird unwiderruflich gelöscht!";
            boolean changeAccepted = WarningController.showWarning(warningMessage);
            primaryStage.getScene().getRoot().setDisable(false);

            if (changeAccepted) {
                Device clickedDevice = (Device) clickedItem;
                deviceOverviewModel.removeDevice(clickedDevice.getOwnerId(), clickedDevice.getId());
                setClickedItem();
            } else {
                setErrorOutput("Löschen des Gerätes abgebrochen!");
            }
        }

    }

    /**
     * Handles the action event for quick capturing devices.
     * Calls the {@link DeviceOverviewModel#quickCaptureDevices()} method to capture devices.
     *
     * @param event the action event
     */
    @FXML
    void quickCaptureDevices(ActionEvent event) {
        deviceOverviewModel.quickCaptureDevices();
    }

    /**
     * Sets the error message in the error output label.
     * This method is called when an error occurs, such as when no room or device is selected in the tree view.
     * It converts the error message to UTF-8 and sets it in the error output label.
     *
     * @param errorMessage the error message
     */
    private void setErrorOutput(String errorMessage) {
        errorOutput.setText(getStringInUTF8(errorMessage));
    }

    /**
     * Converts a string to UTF-8.
     * This method is used to convert a string to UTF-8 encoding.
     *
     * @param string the string to convert
     * @return the string in UTF-8 encoding
     */
    private String getStringInUTF8(String string) {
        return new String(string.getBytes(), StandardCharsets.UTF_8);
    }

    /**
     * Checks if the clicked item in the tree view is a household.
     * This method is used to check if the clicked item in the tree view is a household.
     *
     * @return true if the clicked item is a household, false otherwise
     */
    private boolean clickedItemIsHousehold() {
        return clickedItem instanceof Household;
    }

    /**
     * Checks if the clicked item in the tree view is a room.
     * This method is used to check if the clicked item in the tree view is a room.
     *
     * @return true if the clicked item is a room, false otherwise
     */
    private boolean clickedItemIsRoom() {
        return clickedItem instanceof Room;
    }

    /**
     * Checks if the clicked item in the tree view is a device.
     * This method is used to check if the clicked item in the tree view is a device.
     *
     * @return true if the clicked item is a device, false otherwise
     */
    private boolean clickedItemIsDevice() {
        return clickedItem instanceof Device;
    }
}
