/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app.services

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.logging.Logger
import javafx.geometry.Point2D
import javafx.geometry.Rectangle2D
import javafx.scene.image.Image
import javafx.scene.input.MouseButton
import javafx.scene.robot.Robot
import java.util.Optional

private val log = Logger.get("OSService")

/**
 * An abstraction over OS-level functionality.
 *
 * @author Almas Baim (https://github.com/AlmasB)
 */
class OSService : EngineService() {

    private val robot: Robot by lazy { Async.startAsyncFX<Robot> { Robot() }.await() }

    val mouse: RobotMouse by lazy { RobotMouse(robot) }

    /**
     * @return a screenshot of the [region] in OS screen space, or an empty [Optional] if fails.
     */
    fun takeScreenCapture(region: Rectangle2D): Optional<Image> {
        try {
            return Optional.of(robot.getScreenCapture(null, region))
        } catch (e: Exception) {
            log.warning("Failed to take screen capture", e)
        }

        return Optional.empty()
    }
}

/**
 * An abstraction over an OS-level mouse allowing to perform
 * actions outside the game window.
 */
class RobotMouse
internal constructor(private val robot: Robot) {

    val position: Point2D
        get() = robot.mousePosition

    /**
     * Clicks the primary (typically left) mouse button
     * at the given position on the screen.
     */
    fun click(point: Point2D) {
        click(point.x, point.y)
    }

    /**
     * Clicks the primary (typically left) mouse button
     * at the given position on the screen.
     */
    fun click(x: Double, y: Double) {
        move(x, y)
        click()
    }

    /**
     * Clicks the primary (typically left) mouse button.
     */
    fun click() {
        try {
            robot.mouseClick(MouseButton.PRIMARY)
        } catch (e: Exception) {
            log.warning("Failed to click mouse", e)
        }
    }

    /**
     * Clicks the secondary (typically right) mouse button.
     */
    fun clickSecondary() {
        try {
            robot.mouseClick(MouseButton.SECONDARY)
        } catch (e: Exception) {
            log.warning("Failed to click mouse", e)
        }
    }

    /**
     * Moves the mouse cursor to the given position on the screen.
     */
    fun move(x: Double, y: Double) {
        try {
            robot.mouseMove(x, y)
        } catch (e: Exception) {
            log.warning("Failed to move mouse", e)
        }
    }
}