/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.dynamics.contacts;

import com.almasb.fxgl.physics.box2d.collision.Manifold;
import com.almasb.fxgl.physics.box2d.collision.shapes.CircleShape;
import com.almasb.fxgl.physics.box2d.common.Transform;
import com.almasb.fxgl.physics.box2d.pooling.IWorldPool;

public class CircleContact extends Contact {

    public CircleContact(IWorldPool argPool) {
        super(argPool);
    }

    @Override
    public void evaluate(Manifold manifold, Transform xfA, Transform xfB) {
        pool.getCollision().collideCircles(manifold, (CircleShape) m_fixtureA.getShape(), xfA,
                (CircleShape) m_fixtureB.getShape(), xfB);
    }
}
