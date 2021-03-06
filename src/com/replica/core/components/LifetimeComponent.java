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
 
package com.replica.core.components;

import com.replica.core.BaseObject;
import com.replica.core.ContextParameters;
import com.replica.core.GameObject;
import com.replica.core.GameObjectManager;
import com.replica.core.factory.GameObjectFactory;
import com.replica.core.systems.CameraSystem;
import com.replica.core.systems.SoundSystem;
import com.replica.core.systems.SoundSystem.Sound;
import com.replica.utility.EventRecorder;
import com.replica.utility.Vector2;

/** 
 * This component allows objects to die and be deleted when their life is reduced to zero or they
 * meet other configurable criteria.
 */
public class LifetimeComponent extends GameComponent {
    private boolean mDieWhenInvisible;
    private float mTimeUntilDeath;
    private GameObjectFactory.GameObjectType mSpawnOnDeathType;
    private LaunchProjectileComponent mTrackingSpawner;
    private Vector2 mHotSpotTestPoint;
    private boolean mDieOnHitBackground;
    private Sound mDeathSound;
    private boolean mIncrementEventCounter;
    private int mEventCounter;
    
    public LifetimeComponent() {
        super();
        mHotSpotTestPoint = new Vector2();
        reset();
        setPhase(ComponentPhases.THINK.ordinal());
    }
    
    @Override
    public void reset() {
        mDieWhenInvisible = false;
        mTimeUntilDeath = -1;
        mSpawnOnDeathType = GameObjectFactory.GameObjectType.INVALID;
        mTrackingSpawner = null;
        mHotSpotTestPoint.zero();
        mDieOnHitBackground = false;
        mDeathSound = null;
        mIncrementEventCounter = false;
        mEventCounter = -1;
    }
    
    public void setDieWhenInvisible(boolean die) {
        mDieWhenInvisible = die;
    }
    
    public void setTimeUntilDeath(float time) {
        mTimeUntilDeath = time;
    }
    
    public void setObjectToSpawnOnDeath(GameObjectFactory.GameObjectType type) {
        mSpawnOnDeathType = type;
    }
    
    public void setIncrementEventCounter(int event) {
    	mIncrementEventCounter = true;
    	mEventCounter = event;
    }
    
    @Override
    public void update(float timeDelta, BaseObject parent) {
        GameObject parentObject = (GameObject)parent;
        if (mTimeUntilDeath > 0) {
            mTimeUntilDeath -= timeDelta;
            if (mTimeUntilDeath <= 0) {
                die(parentObject);
                return;
            }
        }
        
        if (mDieWhenInvisible) {
            CameraSystem camera = sSystemRegistry.cameraSystem;
            ContextParameters context = sSystemRegistry.contextParameters;
            final float dx = 
                Math.abs(parentObject.getPosition().x - camera.getFocusPositionX());
            final float dy = 
                Math.abs(parentObject.getPosition().y - camera.getFocusPositionY());
            if (dx > context.gameWidth || dy > context.gameHeight) {
                // the position of this object is off the screen, destroy!
                // TODO: this is a pretty dumb test.  We should have a bounding volume instead.
                die(parentObject);
                return;
            }
        }
        
        if (parentObject.life > 0 && mDieOnHitBackground) {
        	if (parentObject.getBackgroundCollisionNormal().length2() > 0.0f) {
        		parentObject.life = 0;
        	}
        }
        
        if (parentObject.life <= 0) {
            die(parentObject);
            return;
        }
    }
    
    private void die(GameObject parentObject) {
        GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
        GameObjectManager manager = sSystemRegistry.gameObjectManager;
        
      
        
        if (mIncrementEventCounter) {
        	EventRecorder recorder = sSystemRegistry.eventRecorder;
        	recorder.incrementEventCounter(mEventCounter);
        }

        if (mSpawnOnDeathType != GameObjectFactory.GameObjectType.INVALID) {
            GameObject object = factory.spawn(mSpawnOnDeathType, parentObject.getPosition().x, 
                    parentObject.getPosition().y, parentObject.facingDirection.x < 0.0f);

            if (object != null && manager != null) {
                manager.add(object);
            }
            
        }
        
        if (mTrackingSpawner != null) {
            mTrackingSpawner.trackedProjectileDestroyed();
        }
        
        
        if (manager != null) {
            manager.destroy(parentObject);
        }
        
        if (mDeathSound != null) {
        	SoundSystem sound = sSystemRegistry.soundSystem;
        	if (sound != null) {
        		sound.play(mDeathSound, false, SoundSystem.PRIORITY_NORMAL);
        	}
        }

    }
    
    public final void setTrackingSpawner(LaunchProjectileComponent spawner) {
        mTrackingSpawner = spawner;
    }
    
    public final void setDieOnHitBackground(boolean die) {
    	mDieOnHitBackground = die;
    }

    
    public final void setDeathSound(Sound deathSound) {
    	mDeathSound = deathSound;
    }
}
