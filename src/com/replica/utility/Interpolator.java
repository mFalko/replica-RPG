/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
/*
 * This file has been modified from the original.
 * 
 * The original file can be found at:
 *		https://code.google.com/p/replicaisland/
 */
 
package com.replica.utility;

import com.replica.core.AllocationGuard;

/**
 * Helper class for interpolating velocity over time given a target velocity and acceleration.
 * The current velocity will be accelerated towards the target until the target is reached.
 * Note that acceleration is effectively an absolute value--it always points in the direction of
 * the target velocity.
 */
public class Interpolator extends AllocationGuard {

    private float mCurrent;
    private float mAcceleration;
    
    public Interpolator() {
        super();
    }
    
    // Rather than simply interpolating acceleration and velocity for each time step
    // (as in, position += (velocity * time); velocity += (acceleration * time);),
    // we actually perform the work needed to calculate the integral of velocity with respect to
    // time.
    //
    // The integral of velocity is:
    //
    // integral[(v + aT)dT]
    //
    // Simplified to:
    //
    // vT + 1/2 * aT^2
    //
    // Thus:
    // change in position = velocity * time + (0.5 * acceleration * (time^2))
    // change in velocity = acceleration * time

    public void set(float current, float acceleration) {
        mCurrent = current;
        mAcceleration = acceleration;
    }

    // While this function writes directly to velocity, it doesn't affect
    // position.  Instead, the position offset is returned so that it can be blended.
    public float interpolate(float secondsDelta) {
        float oldVelocity = mCurrent;

        // calculate scaled acceleration (0.5 * acceleration * (time^2))
        float scaledAcceleration;
        scaledAcceleration = scaleAcceleration(mAcceleration, secondsDelta);

        // calculate the change in position
        float positionOffset = (oldVelocity * secondsDelta) + scaledAcceleration;

        // change in velocity = v + aT
        float newVelocity = oldVelocity + (mAcceleration * secondsDelta);

        mCurrent = newVelocity;

        return positionOffset;
    }

    public float getCurrent() {
        return mCurrent;
    }

    // calculates 1/2 aT^2
    private float scaleAcceleration(float acceleration, float secondsDelta) {
        float timeSquared = (secondsDelta * secondsDelta);
        float scaledAccel = acceleration * timeSquared;
        scaledAccel *= 0.5f;

        return scaledAccel;
    }
}
