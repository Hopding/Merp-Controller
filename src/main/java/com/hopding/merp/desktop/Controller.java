package com.hopding.merp.desktop;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import java.net.ConnectException;

import static com.hopding.merp.desktop.ArrowIconImages.*;

/**
 * This class is the Controller for the FXML GUI. It provides the methods that are to be called
 * when the various GUI elements are interacted with by the user.
 */
public class Controller {
    /**
     * Represents connection to the Raspberry Pi
     */
    private RPiConnection rpic;

    /**
     * Contains the KeyCode for the key that was pressed immediately preceding
     * the most recent key press. Used to prevent multiple firing events from
     * a key being held down causing multiple signals to be sent.
     */
    private KeyCode prevKeyCode = null;

    /**
     * Determines magnitude of PWM signal sent to the Raspberry Pi
     */
    private int speed = 45;

    /**
     * Field where user enters the IP of the Raspberry Pi
     */
    @FXML
    private TextField ipAddrField;

    /**
     * Buttons used to connect and disconnect from Raspberry Pi
     */
    @FXML
    private Button connectBtn, disconnectBtn;

    /**
     * ImageViews that serve as buttons for sending messages to the Raspberry Pi
     */
    @FXML
    private ImageView upBtn, downBtn, rightBtn, leftBtn, upRightBtn, upLeftBtn, downRightBtn, downLeftBtn;

    /**
     * Slider that adjusts the current speed
     */
    @FXML
    private Slider speedSlider;

    /**
     * TextField that both sets and displays the current speed
     */
    @FXML
    private TextField speedBox;

///////////////////////////////// Start Of initalize() //////////////////////////////////////////////////
    /**
     * <b>WARNING: THIS SHOULD ONLY BE CALLED BY THE SYSTEM/FXML</b><br>
     * This method is called when the GUI is being initialized from the FXML. It defines
     * the logic to be executed when the various buttons are pressed by the user.
     */
    @FXML
    public void initialize() {
        rpic = RPiConnection.getRPiConnection();
        setUIToDisconnectedState();

        // When connectBtn is pressed:
        connectBtn.setOnAction(event -> {

            // Try to connect to the Raspberry Pi in a new thread:
            new Thread(() -> {
                try {
                    System.out.println("Attempting connection to " + ipAddrField.getText() + " ...");
                    rpic.attemptConnection(ipAddrField.getText(), 12345);
                    System.out.println("Connection successful!");

                    // The connection was successful. Alert the user with a popup
                    // and adjust the GUI to its connected state.
                    Platform.runLater(() -> { // Get back into the main UI thread
                        Alert alert = new Alert(
                                Alert.AlertType.INFORMATION,
                                "Successfully connected to " + ipAddrField.getText(),
                                ButtonType.OK);
                        alert.setTitle("Connection Success");
                        alert.showAndWait();
                        setUIToConnectedState();
                    });
                }

                catch(ConnectException e) {
                    System.out.println("Failed to connect to " + ipAddrField.getText());

                    // The connection was not successful. Alert the user with a popup
                    // and adjust the GUI to its disconnected state.
                    Platform.runLater(() -> { // Get back into the main UI thread
                        Alert alert = new Alert(
                                Alert.AlertType.ERROR,
                                "Failed to connect to " + ipAddrField.getText(),
                                ButtonType.OK);
                        alert.setTitle("Connection Failure");
                        alert.showAndWait();
                        setUIToDisconnectedState();
                    });
                }
            }).start();
        });

        // When disconnectBtn is pressed:
        disconnectBtn.setOnAction(event -> {
            rpic.disconnect();
            Alert alert = new Alert(
                    Alert.AlertType.INFORMATION,
                    "Successfully disconnected from " + ipAddrField.getText(),
                    ButtonType.OK);
            alert.setTitle("Disconnect Success");
            alert.showAndWait();
            setUIToDisconnectedState();
        });

     //////////////////////// When A Directional Control Button Is Pressed: /////////////////////////////
        upBtn.setOnMousePressed(event -> {
            rpic.sendPWM(90 + speed, 90 + speed);
            setIconPressed(upBtn);
        });

        downBtn.setOnMousePressed(event -> {
            rpic.sendPWM(90 - speed, 90 - speed);
            setIconPressed(downBtn);
        });

        rightBtn.setOnMousePressed(event -> {
            rpic.sendPWM(90 + speed, 90 - speed);
            setIconPressed(rightBtn);
        });

        leftBtn.setOnMousePressed(event -> {
            rpic.sendPWM(90 - speed, 90 + speed);
            setIconPressed(leftBtn);
        });

        upRightBtn.setOnMousePressed(event -> {
            rpic.sendPWM(180, 94);
            setIconPressed(upRightBtn);
        });

        upLeftBtn.setOnMousePressed(event -> {
            rpic.sendPWM(94, 180);
            setIconPressed(upLeftBtn);
        });

        downRightBtn.setOnMousePressed(event -> {
            rpic.sendPWM(0, 86);
            setIconPressed(downRightBtn);
        });

        downLeftBtn.setOnMousePressed(event -> {
            rpic.sendPWM(86, 0);
            setIconPressed(downLeftBtn);
        });
     ////////////////////////////////////////////////////////////////////////////////////////////////////
    }
////////////////////////////////// End Of initalize() ///////////////////////////////////////////////////

///////////////////////////////// Start Of Private Methods //////////////////////////////////////////////
    /**
     * <b>WARNING: THIS SHOULD ONLY BE CALLED BY THE SYSTEM/FXML</b><br>
     * FXML is set up to call this method when a the mouse is released while hovering
     * over any one of the directional control buttons (which are ImageViews). It sets
     * that button's icon to the released state image, and sends the stop message
     * to the Raspberry Pi.
     *
     * @param event passed in by the system. Is the MouseEvent that triggered this method call.
     */
    @SuppressWarnings("unchecked")
    @FXML
    private void onButtonReleased(MouseEvent event) {
        setIconReleased((ImageView)event.getSource());
        rpic.sendStopPWM();
    }

    /**
     * <b>WARNING: THIS SHOULD ONLY BE CALLED BY THE SYSTEM/FXML</b><br>
     * FXML is set up to call this method when a key is pressed and the
     * MERP Controller app has the focus. If the pressed key is an arrow
     * key, then the corresponding signal will be sent to the Raspberry Pi.
     * <br>
     * This method is setup to only send one signal throughout the duration
     * of a key press - even if they key is held for a period of time.
     *
     * @param event passed in by the system. Is the KeyEvent that triggered this method call.
     */
    @FXML
    private void onKeyPressed(KeyEvent event) {
        if(event.getCode() != prevKeyCode) {
            switch (event.getCode()) {
                case UP: rpic.sendPWM(90 + speed,90 + speed);
                    break;
                case DOWN: rpic.sendPWM(90 - speed, 90 - speed);
                    break;
                case RIGHT: rpic.sendPWM(90 + speed, 90 - speed);
                    break;
                case LEFT: rpic.sendPWM(90 - speed, 90 + speed);
                    break;
            }
        }
        prevKeyCode = event.getCode();
    }

    /**
     * <b>WARNING: THIS SHOULD ONLY BE CALLED BY THE SYSTEM/FXML</b><br>
     * FXML is set up to call this method when a key is released and the
     * MERP Controller app has the focus. If the released key is an arrow
     * key, then the stop signal will be sent to the Raspberry Pi.
     *
     * @param event passed in by the system. Is the KeyEvent that triggered this method call.
     */
    @FXML
    private void onKeyReleased(KeyEvent event) {
        prevKeyCode = null;
        switch(event.getCode()) {
            case UP:
            case DOWN:
            case RIGHT:
            case LEFT: rpic.sendStopPWM();
                break;
        }
    }

    /**
     * <b>WARNING: THIS SHOULD ONLY BE CALLED BY THE SYSTEM/FXML</b><br>
     * FXML is set up to call this method when the position of the speedSlider
     * Slider is changed. The speed and value displayed in the speedBox TextField
     * are updated.
     */
    @FXML
    private void onSliderSpeedChanged() {
        speed = (int)speedSlider.getValue();
        speedBox.setText(String.valueOf(speed));

        // Removes focus from text box so arrow-key control will work
        speedSlider.getParent().requestFocus();
    }

    /**
     * <b>WARNING: THIS SHOULD ONLY BE CALLED BY THE SYSTEM/FXML</b><br>
     * FXML is set up to call this method when a new value is entered
     * into the speedBox TextField. The speed will be updated, and the
     * speedSlider Slider updated.
     */
    @FXML
    private void onSpeedEntered() {
        try {
            speed = (int) Double.parseDouble(speedBox.getText());
        } catch(Exception e) {
            // Swallow any exceptions that might occur while parsing
            // If there is a problem, we just don't change the speed value
        }

        // Check to be sure speed is within required range
        if(speed > 90)
            speed = 90;
        else if(speed < 0)
            speed = 0;

        // If speed's value was capped, update to reflect that
        // Or if a decimal value was entered, update to reflect truncated int val
        speedBox.setText(String.valueOf(speed));

        // Update speedSlider
        speedSlider.setValue(speed);

        // Removes focus from text box so arrow-key control will work
        speedBox.getParent().requestFocus();
    }

    /**
     * Used to disable the directional control buttons. Their opacity is decreased, causing
     * them to grey out to indicate their disabled state.
     */
    private void disableControlButtons() {
        upBtn.setDisable(true);
        upBtn.setOpacity(0.5);
        downBtn.setDisable(true);
        downBtn.setOpacity(0.5);
        rightBtn.setDisable(true);
        rightBtn.setOpacity(0.5);
        leftBtn.setDisable(true);
        leftBtn.setOpacity(0.5);
        upRightBtn.setDisable(true);
        upRightBtn.setOpacity(0.5);
        downRightBtn.setDisable(true);
        downRightBtn.setOpacity(0.5);
        upLeftBtn.setDisable(true);
        upLeftBtn.setOpacity(0.5);
        downLeftBtn.setDisable(true);
        downLeftBtn.setOpacity(0.5);
    }

    /**
     * Used to enable the directional control buttons. Their opacity is maximized, causing
     * them to appear normal to indicate their enabled state.
     */
    private void enableControlButtons() {
        upBtn.setDisable(false);
        upBtn.setOpacity(1.0);
        downBtn.setDisable(false);
        downBtn.setOpacity(1.0);
        rightBtn.setDisable(false);
        rightBtn.setOpacity(1.0);
        leftBtn.setDisable(false);
        leftBtn.setOpacity(1.0);
        upRightBtn.setDisable(false);
        upRightBtn.setOpacity(1.0);
        downRightBtn.setDisable(false);
        downRightBtn.setOpacity(1.0);
        upLeftBtn.setDisable(false);
        upLeftBtn.setOpacity(1.0);
        downLeftBtn.setDisable(false);
        downLeftBtn.setOpacity(1.0);
    }

    /**
     * Used to change the icon of a directional control button to indicate
     * it is in the pressed state.
     *
     * @param imageView the directional control button that is currently pressed
     */
    private void setIconPressed(ImageView imageView) {
        if(imageView == upBtn)
            upBtn.setImage(upArrowPressed);
        else if(imageView == downBtn)
            downBtn.setImage(downArrowPressed);
        else if(imageView == rightBtn)
            rightBtn.setImage(rightArrowPressed);
        else if(imageView == leftBtn)
            leftBtn.setImage(leftArrowPressed);
        else if(imageView == upRightBtn)
            upRightBtn.setImage(upRightArrowPressed);
        else if(imageView == upLeftBtn)
            upLeftBtn.setImage(upLeftArrowPressed);
        else if(imageView == downRightBtn)
            downRightBtn.setImage(downRightArrowPressed);
        else if(imageView == downLeftBtn)
            downLeftBtn.setImage(downLeftArrowPressed);
    }

    /**
     * Used to change the icon of a directional control button to indicate
     * it is in the released state.
     *
     * @param imageView the directional control button that is currently released
     */
    private void setIconReleased(ImageView imageView) {
        if(imageView == upBtn)
            upBtn.setImage(upArrowReleased);
        else if(imageView == downBtn)
            downBtn.setImage(downArrowReleased);
        else if(imageView == rightBtn)
            rightBtn.setImage(rightArrowReleased);
        else if(imageView == leftBtn)
            leftBtn.setImage(leftArrowReleased);
        else if(imageView == upRightBtn)
            upRightBtn.setImage(upRightArrowReleased);
        else if(imageView == upLeftBtn)
            upLeftBtn.setImage(upLeftArrowReleased);
        else if(imageView == downRightBtn)
            downRightBtn.setImage(downRightArrowReleased);
        else if(imageView == downLeftBtn)
            downLeftBtn.setImage(downLeftArrowReleased);
    }

    /**
     * Sets the UI to its connected state.
     */
    private void setUIToConnectedState() {
        ipAddrField.setDisable(true);
        connectBtn.setDisable(true);
        disconnectBtn.setDisable(false);
        enableControlButtons();
    }

    /**
     * Sets the UI to its disconnected state.
     */
    private void setUIToDisconnectedState() {
        ipAddrField.setDisable(false);
        connectBtn.setDisable(false);
        disconnectBtn.setDisable(true);
        disableControlButtons();
    }
////////////////////////////////// End Of Private Methods ///////////////////////////////////////////////
}