/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.almasb.fxgl.event;

import java.util.HashMap;
import java.util.Map;

import com.almasb.fxgl.FXGLManager;

import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * Provides access to mouse state and allows binding of actions
 * to key and mouse events
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class InputManager extends FXGLManager {

    private Scene gameScene;

    /**
     * Holds mouse state information
     */
    private Mouse mouse = new Mouse();

    private Map<KeyCode, Boolean> keys = new HashMap<>();
    private Map<KeyCode, Runnable> keyPressActions = new HashMap<>();
    private Map<KeyCode, Runnable> keyTypedActions = new HashMap<>();
    private Map<KeyCode, Runnable> keyReleasedActions = new HashMap<>();

    public void init(Scene mainScene) {
        this.gameScene = mainScene;
        gameScene.setOnKeyPressed(event -> {
            if (app.isGameMenuOpen())
                return;

            if (!isPressed(event.getCode()) && keyTypedActions.containsKey(event.getCode())) {
                keys.put(event.getCode(), true);
                keyTypedActions.get(event.getCode()).run();
            }
            else {
                keys.put(event.getCode(), true);
            }

        });
        gameScene.setOnKeyReleased(event -> {
            if (app.isGameMenuOpen())
                return;

            keys.put(event.getCode(), false);
            if (keyReleasedActions.containsKey(event.getCode())) {
                keyReleasedActions.get(event.getCode()).run();
            }
        });

        gameScene.setOnMousePressed(mouse::update);
        gameScene.setOnMouseDragged(mouse::update);
        gameScene.setOnMouseReleased(mouse::update);
        gameScene.setOnMouseMoved(mouse::update);
    }

    /**
     * Called by FXGL GameApplication to process all input.
     *
     * @param now
     */
    @Override
    protected void onUpdate(long now) {
        if (processActions)
            keyPressActions.forEach((key, action) -> {if (isPressed(key)) action.run();});

        Point2D origin = app.getViewportOrigin();
        mouse.x = mouse.screenX / app.getSizeRatio() + origin.getX();
        mouse.y = mouse.screenY / app.getSizeRatio() + origin.getY();
    }

    private boolean processActions = true;

    /**
     * Setting to false will not run any actions bound to key/mouse press.
     * The events will still continue to be registered.
     *
     * @param b
     */
    public void setProcessActions(boolean b) {
        processActions = b;
    }

    /**
     * @param key
     * @return
     *          true iff key is currently pressed
     */
    private boolean isPressed(KeyCode key) {
        return keys.getOrDefault(key, false);
    }

    /**
     * Add an action that is executed constantly
     * WHILE the key is physically pressed
     *
     * @param key
     * @param action
     */
    public void addKeyPressBinding(KeyCode key, Runnable action) {
        keyPressActions.put(key, action);
    }

    /**
     * Removes action bound to the given key.
     *
     * @param key
     */
    public void removeKeyPressBinding(KeyCode key) {
        keyPressActions.remove(key);
    }

    /**
     * Add an action that is executed only ONCE
     * per single physical key press
     *
     * @param key
     * @param action
     */
    public void addKeyTypedBinding(KeyCode key, Runnable action) {
        keyTypedActions.put(key, action);
    }

    /**
     * Removes action bound to the given key.
     *
     * @param key
     */
    public void removeKeyTypedBinding(KeyCode key) {
        keyTypedActions.remove(key);
    }

    /**
     * Add an action that is executed once, when
     * the key is released.
     *
     * @param key
     * @param action
     */
    public void addKeyReleasedBinding(KeyCode key, Runnable action) {
        keyReleasedActions.put(key, action);
    }

    /**
     * Removes action bound to release of the given key.
     *
     * @param key
     */
    public void removeKeyReleasedBinding(KeyCode key) {
        keyReleasedActions.remove(key);
    }

    // TODO: proper mouse bindings like keys, debug number of clicks
    /**
     * Add an action that is executed ONCE per single click of
     * given mouse button
     *
     * @param btn
     * @param action
     */
    public void addMouseClickedBinding(MouseButton btn, Runnable action) {
        gameScene.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (app.isGameMenuOpen())
                return;

            if (event.getButton() == btn && processActions) {
                action.run();
            }
        });
    }

    /**
     * Clears all input, that is releases all key presses and mouse clicks
     * for a single frame
     */
    public void clearAllInput() {
        keys.keySet().forEach(key -> keys.put(key, false));
        mouse.leftPressed = false;
        mouse.rightPressed = false;
    }

    /**
     * Returns mouse object that contains constantly updated
     * data about mouse state
     *
     * @return
     */
    public Mouse getMouse() {
        return mouse;
    }

    /**
     * Holds mouse state information
     *
     * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
     */
    public final class Mouse {
        private Mouse() {}

        /**
         * Hold the value of x and y coordinate of the mouse cursor
         * in the current frame (tick) within the game with applied translations
         */
        public double x, y;

        /**
         * Hold the value of x and y coordinate of the mouse cursor
         * in the current frame (tick) within the screen coordinate system
         */
        public double screenX, screenY;

        /**
         * Hold the state of left and right
         * mouse buttons in the current frame (tick)
         */
        public boolean leftPressed, rightPressed;

        /**
         * The last internal event
         */
        private MouseEvent event;

        private void update(MouseEvent event) {
            if (app.isGameMenuOpen())
                return;

            this.event = event;
            this.screenX = event.getSceneX();
            this.screenY = event.getSceneY();

            Point2D origin = app.getViewportOrigin();
            this.x = screenX / app.getSizeRatio() + origin.getX();
            this.y = screenY / app.getSizeRatio() + origin.getY();

            if (leftPressed) {
                if (event.getButton() == MouseButton.PRIMARY && isReleased(event)) {
                    leftPressed = false;
                }
            }
            else {
                leftPressed = event.getButton() == MouseButton.PRIMARY && isPressed(event);
            }

            if (rightPressed) {
                if (event.getButton() == MouseButton.SECONDARY && isReleased(event)) {
                    rightPressed = false;
                }
            }
            else {
                rightPressed = event.getButton() == MouseButton.SECONDARY && isPressed(event);
            }
        }

        private boolean isPressed(MouseEvent event) {
            return event.getEventType() == MouseEvent.MOUSE_PRESSED
                    || event.getEventType() == MouseEvent.MOUSE_DRAGGED;
        }

        private boolean isReleased(MouseEvent event) {
            return event.getEventType() == MouseEvent.MOUSE_RELEASED
                    || event.getEventType() == MouseEvent.MOUSE_MOVED;
        }

        /**
         * It's unlikely that you'll need this.
         *
         * @return last JavaFX mouse event
         */
        public final MouseEvent getEvent() {
            return event;
        }
    }
}
