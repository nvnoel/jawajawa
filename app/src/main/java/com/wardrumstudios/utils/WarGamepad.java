package com.wardrumstudios.utils;

import android.content.res.Configuration;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewParent;
import com.bda.controller.Controller;
import com.bda.controller.ControllerListener;
import com.bda.controller.StateEvent;
import java.nio.ByteBuffer;

public class WarGamepad extends WarBilling implements ControllerListener {
    private static final int COMMAND_DOWN = 2;
    private static final int COMMAND_FIRE = 16;
    private static final int COMMAND_LEFT = 8;
    private static final int COMMAND_RIGHT = 4;
    private static final int COMMAND_STATUS = 64;
    private static final int COMMAND_STOP = 32;
    private static final int COMMAND_UP = 1;
    static int MAX_GAME_PADS = 4;
    private static final int OSGT_60beat = 2;
    private static final int OSGT_AmazonGamepad = 12;
    private static final int OSGT_AmazonRemote = 11;
    private static final int OSGT_AndroidTV = 13;
    private static final int OSGT_Gamestop = 3;
    private static final int OSGT_Generic = 5;
    private static final int OSGT_IOSExtended = 9;
    private static final int OSGT_IOSSimple = 10;
    private static final int OSGT_Moga = 4;
    private static final int OSGT_MogaPro = 7;
    private static final int OSGT_Nyko = 6;
    private static final int OSGT_PS3 = 8;
    private static final int OSGT_Xbox360 = 0;
    private static final int OSGT_XperiaPlay = 1;
    private static final int OSX360_A = 1;
    private static final int OSX360_AXIS_L2 = 4;
    private static final int OSX360_AXIS_R2 = 5;
    private static final int OSX360_AXIS_X1 = 0;
    private static final int OSX360_AXIS_X2 = 2;
    private static final int OSX360_AXIS_Y1 = 1;
    private static final int OSX360_AXIS_Y2 = 3;
    private static final int OSX360_B = 2;
    private static final int OSX360_BACK = 32;
    private static final int OSX360_DPADDOWN = 512;
    private static final int OSX360_DPADLEFT = 1024;
    private static final int OSX360_DPADRIGHT = 2048;
    private static final int OSX360_DPADUP = 256;
    private static final int OSX360_L1 = 64;
    private static final int OSX360_L3 = 4096;
    private static final int OSX360_R1 = 128;
    private static final int OSX360_R3 = 8192;
    private static final int OSX360_START = 16;
    private static final int OSX360_X = 4;
    private static final int OSX360_Y = 8;
    private static final int OSXP_BACK = 16384;
    private static final int OSXP_GP_MENU = 32768;
    private static final int OSXP_MENU = 4096;
    private static final int OSXP_SEARCH = 8192;
    private static final String TAG = "WarGamepad";
    public GamePad[] GamePads;
    protected boolean IsAndroidTV = false;
    Controller mogaController = null;

    public native boolean processTouchpadAsPointer(ViewParent viewParent, boolean z);

    public class GamePad {
        boolean DpadIsAxis;
        public int GamepadButtonMask;
        public int GamepadDpadHack;
        public boolean GamepadTouchReversed;
        int NykoCheckHacks;
        int deviceId;
        boolean is360;
        private boolean isXperia;
        public long lastConnect;
        public long lastDisconnect;
        private UsbDeviceConnection mGamepadConnection;
        private UsbDevice mGamepadDevice;
        private UsbEndpoint mGamepadEndpointIntr;
        private Thread mGamepadThread;
        private InputDevice mLastGamepadInputDevice;
        boolean mightBeNyko;
        float mobiX;
        float mobiY;
        Controller mogaController;
        public int numGamepadTouchSamples;
        public boolean reportPS3as360;
        boolean active = false;
        public int GamepadType = -1;
        public float[] GamepadAxes = new float[6];
        public int[] GamepadTouches = new int[16];

        public GamePad() {
            this.GamepadDpadHack = 0;
            this.GamepadButtonMask = 0;
            this.mightBeNyko = false;
            this.NykoCheckHacks = 0;
            this.is360 = true;
            this.DpadIsAxis = false;
            this.reportPS3as360 = true;
            this.mogaController = null;
            this.mobiX = 0.0f;
            this.mobiY = 0.0f;
            this.GamepadDpadHack = 0;
            this.GamepadButtonMask = 0;
            this.mightBeNyko = false;
            this.NykoCheckHacks = 0;
            this.is360 = true;
            this.DpadIsAxis = false;
            this.reportPS3as360 = true;
            this.mogaController = null;
            this.mobiX = 0.0f;
            this.mobiY = 0.0f;
        }
    }

    int GetDeviceIndex(int deviceId, int source) {
        if ((source & 16) != 0) {
            for (int i = 0; i < MAX_GAME_PADS; i++) {
                if (this.GamePads[i].active && this.GamePads[i].deviceId == deviceId) {
                    return i;
                }
            }
        }
        return -1;
    }

    int GetDeviceIndexByName(String gamepadString) {
        return -1;
    }

    public void SetReportPS3As360(boolean reportPS3as360) {
        for (int i = 0; i < MAX_GAME_PADS; i++) {
            this.GamePads[i].reportPS3as360 = reportPS3as360;
        }
    }

    int GetFreeIndex(int deviceId, int source) {
        if ((source & 16) == 0) {
            return -1;
        }
        for (int i = 0; i < MAX_GAME_PADS; i++) {
            if (this.GamePads[i].active && this.GamePads[i].deviceId == deviceId) {
                return i;
            }
        }
        for (int i2 = 0; i2 < MAX_GAME_PADS; i2++) {
            if (!this.GamePads[i2].active) {
                this.GamePads[i2].deviceId = deviceId;
                this.GamePads[i2].active = true;
                return i2;
            }
        }
        return -1;
    }

    public int InitMogaController(int index) {
        try {
            this.GamePads[index].mogaController = Controller.getInstance(this);
            this.GamePads[index].mogaController.init();
            this.GamePads[index].mogaController.setListener(this, new Handler());
            this.GamePads[index].active = true;
            System.out.println("*****Set Moga as index 0");
            return index;
        } catch (IllegalArgumentException e) {
            this.GamePads[index].mogaController = null;
            return -1;
        }
    }

    @Override // com.wardrumstudios.utils.WarBilling, com.wardrumstudios.utils.WarBase, com.nvidia.devtech.NvEventQueueActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.mogaController = Controller.getInstance(this);
            this.mogaController.init();
            this.mogaController.setListener(this, new Handler());
        } catch (IllegalArgumentException e) {
            this.mogaController = null;
        }
        this.GamePads = new GamePad[MAX_GAME_PADS];
        for (int i = 0; i < MAX_GAME_PADS; i++) {
            this.GamePads[i] = new GamePad();
        }
        if (Build.PRODUCT.contains("R800")) {
            Log.e(TAG, "Xperia Play detected.");
            this.GamePads[0].isXperia = true;
            CheckNavigation(getResources().getConfiguration());
            return;
        }
        Log.e(TAG, "Product " + Build.PRODUCT);
        Log.e(TAG, "Device " + Build.DEVICE);
    }

    @Override // com.wardrumstudios.utils.WarBilling, com.wardrumstudios.utils.WarBase, com.nvidia.devtech.NvEventQueueActivity, android.app.Activity
    public void onDestroy() {
        if (this.mogaController != null) {
            this.mogaController.exit();
        }
        for (int i = 0; i < MAX_GAME_PADS; i++) {
            if (this.GamePads[i].mogaController != null) {
                this.GamePads[i].mogaController.exit();
            }
        }
        super.onDestroy();
    }

    @Override // com.wardrumstudios.utils.WarBase
    public void SetGamepad(String gamepadString) {
        int index = GetDeviceIndexByName(gamepadString);
        if (index != -1) {
            if (gamepadString.equals("GS controller")) {
                this.GamePads[index].GamepadType = 3;
            } else if (gamepadString.equals("")) {
                this.GamePads[index].GamepadType = -1;
            }
        }
    }

    @Override // com.wardrumstudios.utils.WarBase
    public void USBDeviceAttached(UsbDevice device, String name) {
        Log.e(TAG, "Device Attached : " + device);
        if (device == null) {
            Log.e(TAG, "Given null device?");
        } else if (device.getInterfaceCount() != 1) {
            Log.e(TAG, "could not find interface");
        } else {
            UsbInterface intf = device.getInterface(0);
            if (intf.getEndpointCount() != 1) {
                Log.e(TAG, "could not find endpoint");
                return;
            }
            UsbEndpoint ep = intf.getEndpoint(0);
            if (ep.getType() != 3) {
                Log.e(TAG, "endpoint is not interrupt type");
                return;
            }
            final int index = GetFreeIndex(device.getDeviceId(), 16);
            if (index != -1) {
                this.GamePads[index].is360 = true;
                UsbDeviceConnection connection = this.mUsbManager.openDevice(device);
                if (connection != null && connection.claimInterface(intf, true)) {
                    Log.e(TAG, "Success, I have a USB gamepad " + device.toString());
                    this.GamePads[index].GamepadType = 0;
                    if (device.toString().contains("PLAYSTATION")) {
                        this.GamePads[index].is360 = false;
                        if (!this.GamePads[index].reportPS3as360) {
                            this.GamePads[index].GamepadType = 8;
                        }
                    }
                    this.GamePads[index].mGamepadDevice = device;
                    this.GamePads[index].mGamepadEndpointIntr = ep;
                    this.GamePads[index].mGamepadConnection = connection;
                    this.GamePads[index].mGamepadThread = new Thread(new Runnable() { // from class: com.wardrumstudios.utils.WarGamepad.1
                        @Override // java.lang.Runnable
                        public void run() {
                            WarGamepad.this.USBDeviceRun(index);
                        }
                    });
                    this.GamePads[index].mGamepadThread.start();
                    this.GamePads[index].lastConnect = SystemClock.uptimeMillis();
                } else {
                    Log.e(TAG, "Failed to open USB gamepad");
                    this.GamePads[index].GamepadType = -1;
                    this.GamePads[index].mGamepadDevice = null;
                    this.GamePads[index].mGamepadConnection = null;
                    this.GamePads[index].active = false;
                }
                super.USBDeviceAttached(device, name);
            }
        }
    }

    @Override // com.wardrumstudios.utils.WarBase
    public void USBDeviceDetached(UsbDevice device, String name) {
        int index = GetDeviceIndex(device.getDeviceId(), 16);
        if (index == -1) {
            Log.e(TAG, "Disconnected gamepad, device not connected");
            return;
        }
        if (this.GamePads[index].GamepadType != -1) {
            Log.e(TAG, "Disconnected gamepad, stopping usb thread");
            this.GamePads[index].GamepadType = -1;
            this.GamePads[index].mGamepadDevice = null;
            this.GamePads[index].GamepadButtonMask = 0;
            if (this.GamePads[index].mGamepadThread != null) {
                this.GamePads[index].mGamepadThread.stop();
            }
            this.GamePads[index].lastDisconnect = SystemClock.uptimeMillis();
        }
        this.GamePads[index].active = false;
        super.USBDeviceDetached(device, name);
    }

    public void USBDeviceRun(int index) {
        ByteBuffer buffer = ByteBuffer.allocate(1);
        UsbRequest request = new UsbRequest();
        request.initialize(this.GamePads[index].mGamepadConnection, this.GamePads[index].mGamepadEndpointIntr);
        byte status = -1;
        while (true) {
            request.queue(buffer, 1);
            sendCommand(index, 64);
            if (this.GamePads[index].mGamepadConnection.requestWait() == request) {
                byte newStatus = buffer.get(0);
                Log.e(TAG, "****got status " + ((int) newStatus));
                if (newStatus != status) {
                    Log.e(TAG, "got status " + ((int) newStatus));
                    status = newStatus;
                    if ((status & 16) != 0) {
                        sendCommand(index, 32);
                    }
                }
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                }
            } else {
                Log.e(TAG, "requestWait failed, exiting");
                return;
            }
        }
    }

    private void sendCommand(int index, int control) {
        synchronized (this) {
            if (control != 64) {
                Log.e(TAG, "sendMove " + control);
            }
            if (this.GamePads[index].mGamepadConnection != null) {
                byte[] message = {(byte) control};
                this.GamePads[index].mGamepadConnection.controlTransfer(33, OSGT_IOSExtended, OSX360_DPADDOWN, 0, message, message.length, 0);
            }
        }
    }

    @Override // android.app.Activity
    public boolean onGenericMotionEvent(MotionEvent event) {
        int index = GetDeviceIndex(event.getDeviceId(), event.getSource());
        if (index == -1) {
            int source = event.getDevice() != null ? event.getDevice().getSources() : 0;
            index = GetFreeIndex(event.getDeviceId(), source);
            if (index == -1) {
                if ((event.getSource() & 16) != 0) {
                    System.out.println("********************ERROR********* cannot assign controller MotionEvent " + event);
                }
                return super.onGenericMotionEvent(event);
            }
        }
        if (this.GamePads[index].isXperia) {
            return false;
        }
        if ((event.getSource() & 16) != 0 && event.getAction() == 2) {
            if (this.GamePads[index].mLastGamepadInputDevice == null || this.GamePads[index].mLastGamepadInputDevice.getId() != event.getDeviceId()) {
                this.GamePads[index].mLastGamepadInputDevice = event.getDevice();
                if (this.GamePads[index].mLastGamepadInputDevice == null) {
                    return false;
                }
                this.GamePads[index].GamepadType = -1;
                this.GamePads[index].mightBeNyko = false;
                if (!this.GamePads[index].mLastGamepadInputDevice.getName().contains("NYKO")) {
                    this.GamePads[index].mLastGamepadInputDevice.getName().contains("Broadcom Bluetooth HID");
                    InputDevice.MotionRange gas = this.GamePads[index].mLastGamepadInputDevice.getMotionRange(22);
                    InputDevice.MotionRange brake = this.GamePads[index].mLastGamepadInputDevice.getMotionRange(23);
                    if (gas != null && brake != null) {
                        this.GamePads[index].mightBeNyko = true;
                        this.GamePads[index].NykoCheckHacks = 0;
                    }
                } else {
                    this.GamePads[index].GamepadType = 6;
                }
            }
            if (this.GamePads[index].GamepadType == -1 || this.GamePads[index].GamepadType == 11 || this.GamePads[index].GamepadType == 3) {
                this.GamePads[index].mLastGamepadInputDevice = event.getDevice();
                String name = this.GamePads[index].mLastGamepadInputDevice.getName();
                if (name.contains("Thunder") || name.contains("Amazon Fire Game Controller")) {
                    this.GamePads[index].GamepadType = OSGT_AmazonGamepad;
                    this.GamePads[index].GamepadButtonMask = 0;
                    this.GamePads[index].lastConnect = SystemClock.uptimeMillis();
                    System.out.println("Setting GamepadType to Amazon Controller");
                }
            }
            if (this.GamePads[index].GamepadType == -1 || this.GamePads[index].GamepadType == 3) {
                if (SystemClock.uptimeMillis() - this.GamePads[index].lastDisconnect >= 250) {
                    try {
                        this.GamePads[index].mLastGamepadInputDevice = event.getDevice();
                        Log.e(TAG, "FIXME! Received joystick event without a valid joystick. " + this.GamePads[index].mLastGamepadInputDevice);
                        this.GamePads[index].GamepadType = 0;
                        if (this.IsAndroidTV) {
                            this.GamePads[index].GamepadType = OSGT_AndroidTV;
                        }
                        if (this.GamePads[index].mLastGamepadInputDevice != null) {
                            Log.e(TAG, "mLastGamepadInputDevice.getName() " + this.GamePads[index].mLastGamepadInputDevice.getName());
                            if (this.GamePads[index].mLastGamepadInputDevice.getName().contains("PLAYSTATION")) {
                                this.GamePads[index].is360 = false;
                                if (!this.GamePads[index].reportPS3as360) {
                                    this.GamePads[index].GamepadType = 8;
                                }
                            } else {
                                this.GamePads[index].is360 = true;
                            }
                        }
                        this.GamePads[index].GamepadButtonMask = 0;
                        this.GamePads[index].lastConnect = SystemClock.uptimeMillis();
                    } catch (Exception e) {
                    }
                } else {
                    return false;
                }
            }
            int historySize = event.getHistorySize();
            for (int i = 0; i < historySize; i++) {
                processJoystickInput(event, i);
            }
            processJoystickInput(event, -1);
            return true;
        }
        return super.onGenericMotionEvent(event);
    }

    private void ClearBadJoystickAxis(GamePad myGamepad) {
        if (myGamepad.GamepadAxes[0] == -1.0f && myGamepad.GamepadAxes[1] == -1.0f && myGamepad.GamepadAxes[2] == -1.0f && myGamepad.GamepadAxes[3] == -1.0f) {
            System.out.println("Clearing Bad Joystick Axis");
            myGamepad.GamepadAxes[0] = 0.0f;
            myGamepad.GamepadAxes[1] = 0.0f;
            myGamepad.GamepadAxes[2] = 0.0f;
            myGamepad.GamepadAxes[3] = 0.0f;
            myGamepad.GamepadType = -1;
        }
    }

    private void processJoystickInput(MotionEvent event, int historyPos) {
        int index = GetDeviceIndex(event.getDeviceId(), event.getSource());
        if (index == -1) {
            int source = event.getDevice() != null ? event.getDevice().getSources() : 0;
            index = GetFreeIndex(event.getDeviceId(), source);
            if (index == -1) {
                if ((event.getSource() & 16) != 0) {
                    System.out.println("********************ERROR********* cannot assign controller");
                    return;
                }
                return;
            }
        }
        GamePad myGamePad = this.GamePads[index];
        myGamePad.GamepadAxes[0] = getCenteredAxis(event, myGamePad.mLastGamepadInputDevice, 0, historyPos);
        myGamePad.GamepadAxes[1] = getCenteredAxis(event, myGamePad.mLastGamepadInputDevice, 1, historyPos);
        myGamePad.GamepadAxes[2] = getCenteredAxis(event, myGamePad.mLastGamepadInputDevice, 11, historyPos);
        myGamePad.GamepadAxes[3] = getCenteredAxis(event, myGamePad.mLastGamepadInputDevice, 14, historyPos);
        if (IsValidAxis(event, myGamePad.mLastGamepadInputDevice, 17) || IsValidAxis(event, myGamePad.mLastGamepadInputDevice, 23)) {
            float valL2 = getCenteredAxis(event, myGamePad.mLastGamepadInputDevice, 17, historyPos);
            if (valL2 == 0.0f) {
                float[] fArr = myGamePad.GamepadAxes;
                valL2 = getCenteredAxis(event, myGamePad.mLastGamepadInputDevice, 23, historyPos);
                fArr[4] = valL2;
            }
            myGamePad.GamepadAxes[4] = valL2;
        }
        if (IsValidAxis(event, myGamePad.mLastGamepadInputDevice, 18) || IsValidAxis(event, myGamePad.mLastGamepadInputDevice, 22)) {
            float valR2 = getCenteredAxis(event, myGamePad.mLastGamepadInputDevice, 18, historyPos);
            if (valR2 == 0.0f) {
                float[] fArr2 = myGamePad.GamepadAxes;
                valR2 = getCenteredAxis(event, myGamePad.mLastGamepadInputDevice, 22, historyPos);
                fArr2[5] = valR2;
            }
            myGamePad.GamepadAxes[5] = valR2;
        }
        if (myGamePad.GamepadType != 6 && SystemClock.uptimeMillis() - myGamePad.lastConnect > 1000) {
            float DPAD_X = getCenteredAxis(event, myGamePad.mLastGamepadInputDevice, 15, historyPos);
            float DPAD_Y = getCenteredAxis(event, myGamePad.mLastGamepadInputDevice, 16, historyPos);
            if (myGamePad.mightBeNyko) {
                if (myGamePad.GamepadAxes[0] > 0.75f && DPAD_X > 0.75f) {
                    myGamePad.NykoCheckHacks |= 1;
                }
                if (myGamePad.GamepadAxes[0] < -0.75f && DPAD_X < -0.75f) {
                    myGamePad.NykoCheckHacks |= 2;
                }
                if (myGamePad.GamepadAxes[1] > 0.75f && DPAD_Y > 0.75f) {
                    myGamePad.NykoCheckHacks |= 4;
                }
                if (myGamePad.GamepadAxes[1] < -0.7f && DPAD_Y < 0.75f) {
                    myGamePad.NykoCheckHacks |= 8;
                }
                if (myGamePad.GamepadAxes[0] > 0.75f && DPAD_X == 0.0f) {
                    myGamePad.mightBeNyko = false;
                }
                if (myGamePad.GamepadAxes[0] < -0.75f && DPAD_X == 0.0f) {
                    myGamePad.mightBeNyko = false;
                }
                if (myGamePad.GamepadAxes[1] > 0.75f && DPAD_Y == 0.0f) {
                    myGamePad.mightBeNyko = false;
                }
                if (myGamePad.GamepadAxes[1] < -0.75f && DPAD_Y == 0.0f) {
                    myGamePad.mightBeNyko = false;
                }
                if (myGamePad.NykoCheckHacks == 15) {
                    myGamePad.GamepadType = 6;
                    System.out.println("detecting NYKO controller");
                }
            }
            if (myGamePad.GamepadDpadHack == 1) {
                if (DPAD_X > 0.5f || DPAD_X < -0.5f || DPAD_Y > 0.5f || DPAD_Y < -0.5f) {
                    myGamePad.GamepadDpadHack = 0;
                } else {
                    return;
                }
            }
            if (DPAD_X > 0.5f) {
                myGamePad.GamepadButtonMask |= OSX360_DPADRIGHT;
            } else {
                myGamePad.GamepadButtonMask &= -2049;
            }
            if (DPAD_X < -0.5f) {
                myGamePad.GamepadButtonMask |= OSX360_DPADLEFT;
            } else {
                myGamePad.GamepadButtonMask &= -1025;
            }
            if (DPAD_Y > 0.5f) {
                myGamePad.GamepadButtonMask |= OSX360_DPADDOWN;
            } else {
                myGamePad.GamepadButtonMask &= -513;
            }
            if (DPAD_Y >= -0.5f) {
                myGamePad.GamepadButtonMask &= -257;
            } else {
                myGamePad.GamepadButtonMask |= OSX360_DPADUP;
            }
        }
    }

    private static boolean IsValidAxis(MotionEvent event, InputDevice device, int axis) {
        InputDevice.MotionRange range = device.getMotionRange(axis, event.getSource());
        return range != null;
    }

    private static float getCenteredAxis(MotionEvent event, InputDevice device, int axis, int historyPos) {
        InputDevice.MotionRange range = device.getMotionRange(axis, event.getSource());
        if (range != null) {
            float flat = range.getFlat();
            float value = historyPos < 0 ? event.getAxisValue(axis) : event.getHistoricalAxisValue(axis, historyPos);
            if (Math.abs(value) > flat) {
                return value;
            }
        }
        return 0.0f;
    }

    @Override // com.nvidia.devtech.NvEventQueueActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent keyEvent) {
        int index = GetDeviceIndex(keyEvent.getDeviceId(), keyEvent.getSource());
        if (index == -1) {
            int source = keyEvent.getDevice() != null ? keyEvent.getDevice().getSources() : 0;
            index = GetFreeIndex(keyEvent.getDeviceId(), source);
            if (index == -1) {
                if ((keyEvent.getSource() & 16) != 0) {
                    System.out.println("********************ERROR********* cannot assign controller keyEvent " + keyEvent);
                }
                return super.onKeyUp(keyCode, keyEvent);
            }
        }
        GamePad myGamePad = this.GamePads[index];
        int buttonMask = 0;
        switch (keyCode) {
            case 96:
                if (!myGamePad.is360) {
                    if (1 == 0) {
                        buttonMask = 1;
                        break;
                    } else {
                        buttonMask = 4;
                        break;
                    }
                } else {
                    buttonMask = 1;
                    break;
                }
            case 97:
                if (!myGamePad.is360) {
                    if (1 == 0) {
                        buttonMask = 2;
                        break;
                    } else {
                        buttonMask = 8;
                        break;
                    }
                } else {
                    buttonMask = 2;
                    break;
                }
            case 99:
                if (!myGamePad.is360) {
                    if (1 == 0) {
                        buttonMask = 4;
                        break;
                    } else {
                        buttonMask = 1;
                        break;
                    }
                } else {
                    buttonMask = 4;
                    break;
                }
            case 100:
                if (!myGamePad.is360) {
                    if (1 == 0) {
                        buttonMask = 8;
                        break;
                    } else {
                        buttonMask = 2;
                        break;
                    }
                } else {
                    buttonMask = 8;
                    break;
                }
            case 102:
                buttonMask = 64;
                break;
            case 103:
                buttonMask = OSX360_R1;
                break;
            case 104:
                myGamePad.GamepadAxes[4] = 0.0f;
                break;
            case 105:
                myGamePad.GamepadAxes[5] = 0.0f;
                break;
            case 106:
                buttonMask = 4096;
                break;
            case 107:
                buttonMask = 8192;
                break;
            case 108:
                buttonMask = 16;
                break;
            case 109:
                buttonMask = 32;
                break;
        }
        if (buttonMask == 0 && !myGamePad.isXperia) {
            switch (keyCode) {
                case 19:
                    buttonMask = OSX360_DPADUP;
                    break;
                case 20:
                    buttonMask = OSX360_DPADDOWN;
                    break;
                case 21:
                    buttonMask = OSX360_DPADLEFT;
                    break;
                case 22:
                    buttonMask = OSX360_DPADRIGHT;
                    break;
                case 23:
                    buttonMask = 1;
                    break;
            }
            if (buttonMask != 0) {
                myGamePad.GamepadDpadHack = 1;
            }
        }
        if (buttonMask == 0 && myGamePad.isXperia && myGamePad.GamepadType != -1) {
            switch (keyCode) {
                case 4:
                    buttonMask = 2;
                    if (keyEvent.getScanCode() == 158) {
                        buttonMask = 2 | OSXP_BACK;
                        break;
                    }
                    break;
                case 19:
                    buttonMask = OSX360_DPADUP;
                    break;
                case 20:
                    buttonMask = OSX360_DPADDOWN;
                    break;
                case 21:
                    buttonMask = OSX360_DPADLEFT;
                    break;
                case 22:
                    buttonMask = OSX360_DPADRIGHT;
                    break;
                case 23:
                    buttonMask = 1;
                    break;
                case 82:
                    buttonMask = 4096;
                    if (keyEvent.getScanCode() == 226) {
                        buttonMask = 4096 | OSXP_GP_MENU;
                        break;
                    }
                    break;
                case 84:
                    buttonMask = 8192;
                    break;
            }
        }
        if (myGamePad.GamepadType == 3) {
            if (keyCode == 111) {
                buttonMask |= 32;
                keyCode = 109;
            } else if (keyCode == 66) {
                buttonMask |= 16;
                keyCode = 108;
            }
        }
        if (buttonMask != 0) {
            myGamePad.GamepadButtonMask &= buttonMask ^ (-1);
        }
        return super.onKeyUp(keyCode, keyEvent);
    }

    @Override // com.nvidia.devtech.NvEventQueueActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        int index = GetDeviceIndex(keyEvent.getDeviceId(), keyEvent.getSource());
        if (index == -1) {
            if (keyEvent.getDevice() != null) {
                keyEvent.getDevice().getSources();
            }
            index = GetFreeIndex(keyEvent.getDeviceId(), keyEvent.getDevice().getSources());
            if (index == -1) {
                if ((keyEvent.getSource() & 16) != 0) {
                    System.out.println("********************ERROR********* cannot assign controller keyEvent " + keyEvent);
                }
                return super.onKeyDown(keyCode, keyEvent);
            }
        }
        GamePad myGamePad = this.GamePads[index];
        int buttonMask = 0;
        try {
            if (!myGamePad.isXperia && keyEvent.getDeviceId() > 0 && myGamePad.GamepadType != 11) {
                boolean mightBeGamestop = (keyCode >= 7 && keyCode <= 16) || (keyCode >= 20 && keyCode <= 22) || ((keyCode >= 37 && keyCode <= 40) || keyCode == 47 || keyCode == 29 || keyCode == 32 || keyCode == 51);
                if (keyEvent.getDevice() != null && (keyEvent.getDevice().getSources() & 16) != 0) {
                    if (myGamePad.GamepadType == -1 || myGamePad.GamepadType == 3) {
                        myGamePad.GamepadType = 5;
                    }
                } else if (mightBeGamestop) {
                    myGamePad.GamepadType = 3;
                } else if (keyEvent.getDevice() != null && (keyEvent.getDevice().getName().equals("GS controller") || keyEvent.getDevice().getName().equals("Broadcom Bluetooth HID"))) {
                    myGamePad.GamepadType = 3;
                }
            }
        } catch (Exception e) {
        } catch (NoSuchMethodError e2) {
        }
        if (keyEvent.getDevice() != null && (myGamePad.GamepadType == -1 || myGamePad.GamepadType == OSGT_AmazonGamepad || myGamePad.GamepadType == 11 || myGamePad.GamepadType == 3)) {
            myGamePad.mLastGamepadInputDevice = keyEvent.getDevice();
            if (myGamePad.mLastGamepadInputDevice != null) {
                String name = myGamePad.mLastGamepadInputDevice.getName();
                if (myGamePad.GamepadType != OSGT_AmazonGamepad && (name.contains("Thunder") || name.contains("Amazon Fire Game Controller"))) {
                    myGamePad.GamepadType = OSGT_AmazonGamepad;
                    myGamePad.lastConnect = SystemClock.uptimeMillis();
                    System.out.println("Setting GamepadType to Amazon Controller onKeyDown");
                } else if (myGamePad.GamepadType != 11 && myGamePad.mLastGamepadInputDevice.getName().equals("Amazon")) {
                    myGamePad.GamepadType = 11;
                    myGamePad.lastConnect = SystemClock.uptimeMillis();
                    System.out.println("Setting GamepadType to Amazon Remote");
                }
            }
        }
        switch (keyCode) {
            case 96:
                if (!myGamePad.is360) {
                    if (1 == 0) {
                        buttonMask = 1;
                        break;
                    } else {
                        buttonMask = 4;
                        break;
                    }
                } else {
                    buttonMask = 1;
                    break;
                }
            case 97:
                if (!myGamePad.is360) {
                    if (1 == 0) {
                        buttonMask = 2;
                        break;
                    } else {
                        buttonMask = 8;
                        break;
                    }
                } else {
                    buttonMask = 2;
                    break;
                }
            case 99:
                if (!myGamePad.is360) {
                    if (1 == 0) {
                        buttonMask = 4;
                        break;
                    } else {
                        buttonMask = 1;
                        break;
                    }
                } else {
                    buttonMask = 4;
                    break;
                }
            case 100:
                if (!myGamePad.is360) {
                    if (1 == 0) {
                        buttonMask = 8;
                        break;
                    } else {
                        buttonMask = 2;
                        break;
                    }
                } else {
                    buttonMask = 8;
                    break;
                }
            case 102:
                buttonMask = 64;
                break;
            case 103:
                buttonMask = OSX360_R1;
                break;
            case 104:
                myGamePad.GamepadAxes[4] = 1.0f;
                break;
            case 105:
                myGamePad.GamepadAxes[5] = 1.0f;
                break;
            case 106:
                buttonMask = 4096;
                break;
            case 107:
                buttonMask = 8192;
                break;
            case 108:
                buttonMask = 16;
                break;
            case 109:
                buttonMask = 32;
                break;
        }
        if (buttonMask == 0 && !myGamePad.isXperia) {
            switch (keyCode) {
                case 19:
                    buttonMask = OSX360_DPADUP;
                    break;
                case 20:
                    buttonMask = OSX360_DPADDOWN;
                    break;
                case 21:
                    buttonMask = OSX360_DPADLEFT;
                    break;
                case 22:
                    buttonMask = OSX360_DPADRIGHT;
                    break;
                case 23:
                    buttonMask = 1;
                    break;
            }
            if (buttonMask != 0) {
                myGamePad.GamepadDpadHack = 1;
            }
        }
        if (buttonMask == 0 && myGamePad.isXperia && myGamePad.GamepadType != -1) {
            switch (keyCode) {
                case 4:
                    buttonMask = 2;
                    if (keyEvent.getScanCode() == 158) {
                        buttonMask = 2 | OSXP_BACK;
                        break;
                    }
                    break;
                case 19:
                    buttonMask = OSX360_DPADUP;
                    break;
                case 20:
                    buttonMask = OSX360_DPADDOWN;
                    break;
                case 21:
                    buttonMask = OSX360_DPADLEFT;
                    break;
                case 22:
                    buttonMask = OSX360_DPADRIGHT;
                    break;
                case 23:
                    buttonMask = 1;
                    break;
                case 82:
                    buttonMask = 4096;
                    if (keyEvent.getScanCode() == 226) {
                        buttonMask = 4096 | OSXP_GP_MENU;
                        break;
                    }
                    break;
                case 84:
                    buttonMask = 8192;
                    break;
            }
        }
        if (myGamePad.GamepadType == 3) {
            if (keyCode == 111) {
                buttonMask |= 32;
                keyCode = 109;
            } else if (keyCode == 66) {
                buttonMask |= 16;
                keyCode = 108;
            }
        }
        if (buttonMask != 0 && SystemClock.uptimeMillis() - myGamePad.lastConnect > 1000) {
            myGamePad.GamepadButtonMask |= buttonMask;
        }
        return super.onKeyDown(keyCode, keyEvent);
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        if (this.GamePads[0].isXperia) {
            CheckNavigation(newConfig);
        }
        super.onConfigurationChanged(newConfig);
    }

    public void CheckNavigation(Configuration withConfig) {
        if (withConfig.navigationHidden == 1 && this.GamePads[0].GamepadType == -1) {
            Log.e(TAG, "Attached xPeria play gamepad.");
            this.GamePads[0].GamepadType = 1;
            this.GamePads[0].is360 = true;
            this.GamePads[0].GamepadButtonMask = 0;
            this.GamePads[0].lastConnect = SystemClock.uptimeMillis();
        } else if (withConfig.navigationHidden == 2 && this.GamePads[0].GamepadType != -1) {
            Log.e(TAG, "Detaching xPeria play gamepad.");
            this.GamePads[0].GamepadType = -1;
            this.GamePads[0].GamepadButtonMask = 0;
            this.GamePads[0].lastDisconnect = SystemClock.uptimeMillis();
        }
    }

    private void setProcessTouchpadAsPointer(boolean processAsPointer) {
        ViewParent viewRoot;
        try {
            if (Build.VERSION.SDK_INT <= OSGT_AndroidTV) {
                try {
                    View root = getWindow().getDecorView().getRootView();
                    if (root != null && (viewRoot = root.getParent()) != null) {
                        if (processTouchpadAsPointer(viewRoot, processAsPointer)) {
                            System.out.println("Processing touchpad as pointer succeeded");
                        } else {
                            System.out.println("Processing touchpad as pointer failed");
                        }
                    }
                } catch (NoClassDefFoundError e) {
                }
            }
        } catch (Exception e2) {
            System.out.println("Unable to set processTouchpadAsPointer: " + e2.toString());
        }
    }

    @Override // com.nvidia.devtech.NvEventQueueActivity
    public void GamepadReportSurfaceCreated(SurfaceHolder holder) {
        System.out.println("Processing touchpad as pointer...");
        setProcessTouchpadAsPointer(true);
    }

    @Override // com.nvidia.devtech.NvEventQueueActivity, android.app.Activity
    public boolean onTouchEvent(MotionEvent event) {
        int gIndex = GetDeviceIndex(event.getDeviceId(), event.getSource());
        if (gIndex == -1) {
            int source = event.getDevice() != null ? event.getDevice().getSources() : 0;
            gIndex = GetFreeIndex(event.getDeviceId(), source);
            if (gIndex == -1) {
                if ((event.getSource() & 16) != 0) {
                    System.out.println("********************ERROR********* cannot assign controller - onTouchEvent " + event);
                }
                return super.onTouchEvent(event);
            }
        }
        ClearBadJoystickAxis(this.GamePads[gIndex]);
        if (Build.VERSION.SDK_INT >= OSGT_IOSExtended && event.getSource() == 1048584) {
            int count = 0;
            int x1 = 0;
            int y1 = 0;
            int x2 = 0;
            int y2 = 0;
            int numEvents = event.getPointerCount();
            for (int i = 0; i < numEvents; i++) {
                event.getPointerId(i);
                if (count == 0) {
                    x1 = (int) event.getX(i);
                    y1 = (int) event.getY(i);
                    count++;
                } else if (count == 1) {
                    x2 = (int) event.getX(i);
                    y2 = (int) event.getY(i);
                    count++;
                }
            }
            TouchpadEvent(event.getAction(), count, x1, y1, x2, y2);
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void TouchpadEvent(int touchAction, int count, int x1, int y1, int x2, int y2) {
        if (touchAction == 0 || touchAction == 1) {
            this.GamePads[0].numGamepadTouchSamples = 0;
            if (touchAction == 1) {
                UpdateTrack(0, 0, 0, true);
                UpdateTrack(1, 0, 0, true);
                return;
            }
        } else {
            this.GamePads[0].numGamepadTouchSamples++;
        }
        boolean fullChange = false;
        if (touchAction == 6) {
            this.GamePads[0].GamepadTouchReversed = true;
            fullChange = true;
        } else if (touchAction == 5 || touchAction != 2) {
            this.GamePads[0].GamepadTouchReversed = false;
            fullChange = true;
        }
        if (this.GamePads[0].GamepadTouchReversed) {
            UpdateTrack(1, x1, y1, fullChange);
            UpdateTrack(0, x2, y2, fullChange);
            return;
        }
        UpdateTrack(0, x1, y1, fullChange);
        UpdateTrack(1, x2, y2, fullChange);
    }

    public void UpdateTrack(int trackNo, int x, int y, boolean fullChange) {
        if (fullChange) {
            for (int i = 0; i < 4; i++) {
                int index = (trackNo * 8) + (i * 2);
                this.GamePads[0].GamepadTouches[index] = x;
                this.GamePads[0].GamepadTouches[index + 1] = y;
            }
            return;
        }
        int index2 = (trackNo * 8) + ((this.GamePads[0].numGamepadTouchSamples % 4) * 2);
        this.GamePads[0].GamepadTouches[index2] = x;
        this.GamePads[0].GamepadTouches[index2 + 1] = y;
    }

    public int GetGamepadType(int index) {
        return this.GamePads[index].GamepadType;
    }

    public int GetGamepadButtons(int index) {
        return this.GamePads[index].GamepadButtonMask;
    }

    public float GetGamepadAxis(int index, int axisId) {
        return this.GamePads[index].GamepadAxes[axisId];
    }

    public int GetGamepadTrack(int index, int trackId, int coord) {
        if (this.GamePads[index].numGamepadTouchSamples < 4) {
            return 0;
        }
        int average = 0;
        for (int i = 0; i < 4; i++) {
            average += this.GamePads[index].GamepadTouches[(trackId * 8) + (i * 2) + coord];
        }
        return average / 4;
    }

    int GetMogaControllerType(int index) {
        int mogaType = this.mogaController.getState(4);
        if (mogaType == 0) {
            return 4;
        }
        if (mogaType == 1) {
            return 7;
        }
        System.out.println("Moga controller type = " + mogaType);
        return 7;
    }

    @Override // com.bda.controller.ControllerListener
    public void onKeyEvent(com.bda.controller.KeyEvent event) {
        int buttonMask = 0;
        int index = GetDeviceIndex(0, 16);
        if (index != -1) {
            GamePad myGamePad = this.GamePads[index];
            if (myGamePad.GamepadType != 4 && myGamePad.GamepadType != 7) {
                myGamePad.GamepadType = GetMogaControllerType(index);
            }
            if (event.getAction() == 0) {
                switch (event.getKeyCode()) {
                    case 19:
                        if (myGamePad.GamepadType == 7) {
                            buttonMask = 0 | OSX360_DPADUP;
                            break;
                        }
                        break;
                    case 20:
                        if (myGamePad.GamepadType == 7) {
                            buttonMask = 0 | OSX360_DPADDOWN;
                            break;
                        }
                        break;
                    case 21:
                        if (myGamePad.GamepadType == 7) {
                            buttonMask = 0 | OSX360_DPADLEFT;
                            break;
                        }
                        break;
                    case 22:
                        if (myGamePad.GamepadType == 7) {
                            buttonMask = 0 | OSX360_DPADRIGHT;
                            break;
                        }
                        break;
                    case 96:
                        buttonMask = 0 | 1;
                        break;
                    case 97:
                        buttonMask = 0 | 2;
                        break;
                    case 99:
                        buttonMask = 0 | 4;
                        break;
                    case 100:
                        buttonMask = 0 | 8;
                        break;
                    case 102:
                        buttonMask = 0 | 64;
                        break;
                    case 103:
                        buttonMask = 0 | OSX360_R1;
                        break;
                    case 104:
                        myGamePad.GamepadAxes[4] = 1.0f;
                        break;
                    case 105:
                        myGamePad.GamepadAxes[5] = 1.0f;
                        break;
                    case 106:
                        buttonMask = 0 | 4096;
                        break;
                    case 107:
                        buttonMask = 0 | 8192;
                        break;
                    case 108:
                        buttonMask = 0 | 16;
                        break;
                    case 109:
                        buttonMask = 0 | 32;
                        break;
                    default:
                        System.out.println("onKeyEvent " + event.getKeyCode());
                        break;
                }
                System.out.println("onKeyEvent " + event.getKeyCode());
                if (buttonMask != 0) {
                    myGamePad.GamepadButtonMask |= buttonMask;
                    return;
                }
                return;
            }
            switch (event.getKeyCode()) {
                case 19:
                    if (myGamePad.GamepadType == 7) {
                        buttonMask = 0 | OSX360_DPADUP;
                        break;
                    }
                    break;
                case 20:
                    if (myGamePad.GamepadType == 7) {
                        buttonMask = 0 | OSX360_DPADDOWN;
                        break;
                    }
                    break;
                case 21:
                    if (myGamePad.GamepadType == 7) {
                        buttonMask = 0 | OSX360_DPADLEFT;
                        break;
                    }
                    break;
                case 22:
                    if (myGamePad.GamepadType == 7) {
                        buttonMask = 0 | OSX360_DPADRIGHT;
                        break;
                    }
                    break;
                case 96:
                    buttonMask = 0 | 1;
                    break;
                case 97:
                    buttonMask = 0 | 2;
                    break;
                case 99:
                    buttonMask = 0 | 4;
                    break;
                case 100:
                    buttonMask = 0 | 8;
                    break;
                case 102:
                    buttonMask = 0 | 64;
                    break;
                case 103:
                    buttonMask = 0 | OSX360_R1;
                    break;
                case 104:
                    myGamePad.GamepadAxes[4] = 0.0f;
                    break;
                case 105:
                    myGamePad.GamepadAxes[5] = 0.0f;
                    break;
                case 106:
                    buttonMask = 0 | 4096;
                    break;
                case 107:
                    buttonMask = 0 | 8192;
                    break;
                case 108:
                    buttonMask = 0 | 16;
                    break;
                case 109:
                    buttonMask = 0 | 32;
                    break;
                default:
                    System.out.println("onKeyEvent " + event);
                    break;
            }
            if (buttonMask != 0) {
                myGamePad.GamepadButtonMask &= buttonMask ^ (-1);
            }
        }
    }

    float GetWithDeadZone(float x) {
        if (x > 0.25d || x < -0.25d) {
            return x;
        }
        return 0.0f;
    }

    @Override // com.bda.controller.ControllerListener
    public void onMotionEvent(com.bda.controller.MotionEvent event) {
        int index = GetDeviceIndex(0, 16);
        if (index != -1) {
            GamePad myGamePad = this.GamePads[index];
            if (myGamePad.GamepadType != 4 && myGamePad.GamepadType != 7) {
                myGamePad.GamepadType = GetMogaControllerType(index);
            }
            myGamePad.mobiX = GetWithDeadZone(event.getAxisValue(0));
            myGamePad.mobiY = GetWithDeadZone(event.getAxisValue(1));
            myGamePad.GamepadAxes[0] = myGamePad.mobiX;
            myGamePad.GamepadAxes[1] = myGamePad.mobiY;
            myGamePad.GamepadAxes[2] = GetWithDeadZone(event.getAxisValue(11));
            myGamePad.GamepadAxes[3] = GetWithDeadZone(event.getAxisValue(14));
        }
    }

    @Override // com.bda.controller.ControllerListener
    public void onStateEvent(StateEvent event) {
        int index;
        int index2 = GetDeviceIndex(0, 16);
        System.out.println("onStateEvent " + event + " getState " + event.getState() + " action " + event.getAction());
        switch (event.getState()) {
            case 1:
                switch (event.getAction()) {
                    case 0:
                        if (index2 > -1) {
                            this.GamePads[index2].GamepadType = -1;
                            this.GamePads[index2].active = false;
                            return;
                        }
                        return;
                    case 1:
                        if (index2 == -1 && (index = GetFreeIndex(0, 16)) > -1) {
                            this.GamePads[index].GamepadType = GetMogaControllerType(index);
                            return;
                        }
                        return;
                    default:
                        return;
                }
            case 2:
                if (event.getAction() == 1) {
                }
                return;
            default:
                return;
        }
    }
}