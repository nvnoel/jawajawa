package com.bda.controller;

public interface ControllerListener {
    void onKeyEvent(KeyEvent keyEvent);

    void onMotionEvent(MotionEvent motionEvent);

    void onStateEvent(StateEvent stateEvent);
}