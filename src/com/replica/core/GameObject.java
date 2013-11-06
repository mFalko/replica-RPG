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

package com.replica.core;

import com.replica.core.collision.CollisionParameters.HitType;
import com.replica.core.game.AttackConstants;
import com.replica.utility.HasBounds;
import com.replica.utility.RectF;
import com.replica.utility.Vector2;

/**
 * GameObject defines any object that resides in the game world (character, background, special
 * effect, enemy, etc).  It is a collection of GameComponents which implement its behavior;
 * GameObjects themselves have no intrinsic behavior.  GameObjects are also "bags of data" that
 * components can use to share state (direct component-to-component communication is discouraged).
 */
public class GameObject extends PhasedObjectManager implements HasBounds {
	
    // These fields are managed by components.
	// define as constants and use a alloc free map to access?
	
    private Vector2 mPosition;
    private Vector2 mVelocity;
    private float  mMaxSpeed;
    private float mMass;
    private Vector2 mBackgroundCollisionNormal;

    private RectF mBounds_;
  
    public boolean positionLocked;
    public boolean touchingWall;
    
    public float activationRadius;
    public boolean destroyOnDeactivation;
    
    private int maxLife;
    private int maxMana;
    
    public int life;
    public int mana;
    
    //TODO: make private and use enum to access via one method
    public int strength;
    public int agility;
    public int intellect;
    public int spirit;
    
    public int lastReceivedHitType;
    
    public Vector2 facingDirection;
    public float width;
    public float height;
    
    private static final int DEFAULT_LIFE = 1;
    
    public enum ActionType {
        INVALID,
        IDLE,
        MOVE,
        ATTACK,
        HIT_REACT,
        DEATH,
        DEAD,
        FROZEN
    }
    
    private ActionType mCurrentAction;
    public AttackConstants currentAttack = AttackConstants.FIREBALL_1;
    
    public enum Team {
        NONE,
        PLAYER,
        ENEMY
    }
    
    public Team team;
    
    
    
    public GameObject() {
        super();

        mPosition = new Vector2();
        mVelocity = new Vector2();
        mBackgroundCollisionNormal = new Vector2();
        mBounds_ = new RectF();
        facingDirection = new Vector2(1, 0);
        
        reset();
    }
    
    @Override
    public void reset() {
        removeAll();
        commitUpdates();
        
        mPosition.zero();
        mVelocity.zero();
        mBackgroundCollisionNormal.zero();
        facingDirection.set(1.0f, 1.0f);
        
        mCurrentAction = ActionType.INVALID;
        positionLocked = false;
        activationRadius = 0;
        destroyOnDeactivation = false;
        life = DEFAULT_LIFE;
        team = Team.NONE;
        width = 0.0f;
        height = 0.0f;
        lastReceivedHitType = HitType.INVALID;
    }
    


    public final Vector2 getPosition() {
        return mPosition;
    }

    public final void setPosition(Vector2 position) {
        mPosition.set(position);
    }
    
    public final float getCenteredPositionX() {
        return mPosition.x + (width / 2.0f);
    }
    
    public final float getCenteredPositionY() {
        return mPosition.y + (height / 2.0f);
    }

    public final Vector2 getVelocity() {
        return mVelocity;
    }

    public final void setVelocity(Vector2 velocity) {
        mVelocity.set(velocity);
    }
    
    public final float getMaxSpeed() {
    	return mMaxSpeed;
    }
    
    public final void setMaxSpeed(float maxSpeed) {
    	mMaxSpeed = maxSpeed;
    }

    public final Vector2 getBackgroundCollisionNormal() {
        return mBackgroundCollisionNormal;
    }

    public final void setBackgroundCollisionNormal(Vector2 normal) {
        mBackgroundCollisionNormal.set(normal);
    }
    
    public final ActionType getCurrentAction() {
        return mCurrentAction;
    }
    
    public final void setCurrentAction(ActionType type) {
        mCurrentAction = type;
    }

	public float getMass() {
		return mMass;
	}

	public void setMass(float mass) {
		mMass = mass;
	}

	@Override
	public RectF getBounds() {
		return mBounds_;
	}

	@Override
	public void setBounds(RectF bounds) {
		mBounds_.set(bounds);
		
	}
}
