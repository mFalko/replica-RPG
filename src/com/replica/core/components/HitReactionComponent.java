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
import com.replica.core.GameObject;
import com.replica.core.GameObject.ActionType;
import com.replica.core.GameObject.Team;
import com.replica.core.GameObjectManager;
import com.replica.core.collision.CollisionParameters;
import com.replica.core.collision.CollisionParameters.HitType;
import com.replica.core.factory.GameObjectFactory;
import com.replica.core.factory.GameObjectFactory.GameObjectType;
import com.replica.core.systems.SoundSystem;
import com.replica.utility.DebugLog;
import com.replica.utility.TimeSystem;

/** 
 * A general-purpose component that responds to dynamic collision notifications.  This component
 * may be configured to produce common responses to hit (taking damage, being knocked back, etc), or
 * it can be derived for entirely different responses.  This component must exist on an object for
 * that object to respond to dynamic collisions.
 */
public class HitReactionComponent extends GameComponent {
    private static final float ATTACK_PAUSE_DELAY = (1.0f / 60) * 4;
    private final static float DEFAULT_BOUNCE_MAGNITUDE = 200.0f;
    private final static float EVENT_SEND_DELAY = 5.0f;
    private final static float INVINCIBLE_AFTER_HIT_TIME = 0.05f;
 
    private float mLastHitTime;
    private boolean mInvincible;
    private boolean mDieOnCollect;
    private boolean mDieOnAttack;
    private ChangeComponentsComponent mPossessionComponent;
    private LauncherComponent mLauncherComponent;
    private int mLauncherHitType;
    private float mInvincibleTime;
    private int mGameEventHitType;
    private float mLastGameEventTime;
    private boolean mForceInvincibility;
    private SoundSystem.Sound mTakeHitSound;
    private SoundSystem.Sound mDealHitSound;
    private int mDealHitSoundHitType;
    private int mTakeHitSoundHitType;

    private GameObjectFactory.GameObjectType mSpawnOnDealHitObjectType;
    private int mSpawnOnDealHitHitType;
    private boolean mAlignDealHitObjectToVictimX;
    private boolean mAlignDealHitObjectToVictimY;
    
    
    public HitReactionComponent() {
        super();
        reset();
        setPhase(ComponentPhases.PRE_DRAW.ordinal());
//        setPhase(ComponentPhases.COLLISION_DETECTION.ordinal());
    }
    
    @Override
    public void reset() {
        mInvincible = false;
        mDieOnCollect = false;
        mDieOnAttack = false;
        mPossessionComponent = null;
        mLauncherComponent = null;
        mInvincibleTime = 0.0f;
        mLastGameEventTime = -1.0f;
        mGameEventHitType = CollisionParameters.HitType.INVALID;
        mForceInvincibility = false;
        mTakeHitSound = null;
        mDealHitSound = null;
        mSpawnOnDealHitObjectType = GameObjectType.INVALID;
        mSpawnOnDealHitHitType = CollisionParameters.HitType.INVALID;
        mDealHitSoundHitType = CollisionParameters.HitType.INVALID;
        mAlignDealHitObjectToVictimX = false;
        mAlignDealHitObjectToVictimY = false;
    }
    
    /** Called when this object attacks another object. */
    public void hitVictim(GameObject parent, GameObject victim, int hitType, 
            boolean hitAccepted) {
    	
        if (hitAccepted) {
        	
        	//attacked the other GO
        	if (0 != (hitType & HitType.ATTACK)) {
				if (mDieOnAttack) {
					parent.life = 0;
				}
//				parent.currentAttack.attack_.onAttack(); 
				//TODO: have an attack callback? If one is needed put it here
        	}
        	
        	//Launch the other object
			if (0 != (hitType & HitType.LAUNCH) && mLauncherComponent != null) {
				mLauncherComponent.prepareToLaunch(victim, parent);
			}
        	
			if (mDealHitSound != null
					&& (0 !=(hitType &  mDealHitSoundHitType) || mDealHitSoundHitType == CollisionParameters.HitType.INVALID)) {
				SoundSystem sound = sSystemRegistry.soundSystem;
				if (sound != null) {
					sound.play(mDealHitSound, false,
							SoundSystem.PRIORITY_NORMAL);
				}
			}

            if (mSpawnOnDealHitObjectType != GameObjectType.INVALID && 
                    0 != (hitType & mSpawnOnDealHitHitType)) {
                final float x = mAlignDealHitObjectToVictimX ? 
                        victim.getPosition().x : parent.getPosition().x;
                final float y = mAlignDealHitObjectToVictimY ? 
                        victim.getPosition().y : parent.getPosition().y;     
                
                GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
                GameObjectManager manager = sSystemRegistry.gameObjectManager;
 
                if (factory != null) {
                    GameObject object = factory.spawn(mSpawnOnDealHitObjectType, x, 
                            y, parent.facingDirection.x < 0.0f);
    
                    if (object != null && manager != null) {
                        manager.add(object);
                    }
                }
            }
  
        }
    }
    //TODO: check caller to ensure return value is used properly
    /** Called when this object is hit by another object. */
    public boolean receivedHit(GameObject parent, GameObject attacker, int hitType) {
        final TimeSystem time = sSystemRegistry.timeSystem;
        final float gameTime = time.getGameTime();
 
        if (0 != (mGameEventHitType & hitType) && 
                mGameEventHitType != CollisionParameters.HitType.INVALID ) {
        	if (mLastGameEventTime < 0.0f || gameTime > mLastGameEventTime + EVENT_SEND_DELAY) {
				//TODO: wth do I do w/ game events
        		//maybe deal w/ hot spots here..
	        } else {
	        	// special case.  If we're waiting for a hit type to spawn an event and
	        	// another event has just happened, eat this hit so we don't miss
	        	// the chance to send the event.
	        	hitType = CollisionParameters.HitType.INVALID;
	        }
        	mLastGameEventTime = gameTime;
        }
        
        // don't hit our friends, if we have friends.
        final boolean sameTeam = (parent.team == attacker.team && parent.team != Team.NONE);
        if (0 != (hitType & HitType.ATTACK)) {
			if (!mForceInvincibility && !mInvincible && parent.life > 0 && !sameTeam) {
				//TODO: attack logic goes here
				parent.life -=20; 
				DebugLog.d("SnowBall", "Life = " + parent.life);
				
				//I dont like this, but there needs to be a hit delay, find a better way
				mInvincible = true;
                mInvincibleTime = INVINCIBLE_AFTER_HIT_TIME;
			} else {
				// Ignore this hit.
				hitType = CollisionParameters.HitType.INVALID;
			}
        }
        
        if (0 != (hitType & HitType.DEATH)) {
        	if (!sameTeam) {
        		parent.life = 0;
        	}
        }
        
        if (0 != (hitType & HitType.LAUNCH)) {
        	
        }

		if (0 != (hitType & HitType.COLLECT)) {
			if (parent.life > 0) {
				InventoryComponent attackerInventory = attacker
						.findByClass(InventoryComponent.class);
				if (attackerInventory != null) {
					//TODO: add item into attacker inventory 
				}

				if (mDieOnCollect) {
					parent.life = 0;
				}
			}
        }
        
        if (0 != (hitType & HitType.PRESS)) {
        	
        }
        
        if (0 != (hitType & HitType.POSSESS)) {
			if (mPossessionComponent != null && parent.life > 0
					&& attacker.life > 0) {
				mPossessionComponent.activate(parent);
			} else {
				hitType = CollisionParameters.HitType.INVALID;
			}
		}
        
        if (0 != (hitType & HitType.FLIP)) {
        	//fully handled here
        	hitType &= ~(HitType.FLIP);
        	
        	//this is going to happen A LOT, need to find a better way, possibly a dedicated component
        	final RenderComponent parentRenderComponent = (RenderComponent)parent.findByClass(RenderComponent.class);
        	final RenderComponent attackerRenderComponent = (RenderComponent)attacker.findByClass(RenderComponent.class);
        	
        	//attacker is behind parent if this is called
        	//ensure that draw order is correct
        	if (parentRenderComponent.getPriority() <= attackerRenderComponent.getPriority()) {
        		 attackerRenderComponent.setPriority(parentRenderComponent.getPriority() -1);
        	}
        	
        	
        	
        	//TODO: add check to make sure we don't flip under the background or into the effect layer
        }
        
        if (hitType != CollisionParameters.HitType.INVALID) {
            if (mTakeHitSound != null && 0 != (hitType & mTakeHitSoundHitType)) {
                SoundSystem sound = sSystemRegistry.soundSystem;
                if (sound != null) {
                    sound.play(mTakeHitSound, false, SoundSystem.PRIORITY_NORMAL);
                }
            }
            mLastHitTime = gameTime;
            parent.setCurrentAction(ActionType.HIT_REACT);
            parent.lastReceivedHitType = hitType; 
        }
        
        return hitType != CollisionParameters.HitType.INVALID;
    }
    
    @Override
    public void update(float timeDelta, BaseObject parent) {
        GameObject parentObject = (GameObject)parent;
        TimeSystem time = sSystemRegistry.timeSystem;
        
        final float gameTime = time.getGameTime();
       
        if (mInvincible && mInvincibleTime > 0) {
            if (time.getGameTime() > mLastHitTime + mInvincibleTime) {
                mInvincible = false;
            }
        }
        
        // This means that the lastReceivedHitType will persist for two frames, giving all systems
        // a chance to react.
        if (gameTime - mLastHitTime > timeDelta) {
            parentObject.lastReceivedHitType = CollisionParameters.HitType.INVALID;
        }
    }
    
    
    public void setDieWhenCollected(boolean die) {
        mDieOnCollect = true;
    }
    
    public void setDieOnAttack(boolean die) {
        mDieOnAttack = die;
    }
    
    public void setInvincible(boolean invincible) {
        mInvincible = invincible;
    }
    
    public void setPossessionComponent(ChangeComponentsComponent component) {
        mPossessionComponent = component;
    }
    
    public void setLauncherComponent(LauncherComponent component) {
        mLauncherComponent = component;
    }
    
    //TODO: how to handle events, that is the question...
    public void setSpawnGameEventOnHit(int hitType, int gameFlowEventType, int indexData) {
        mGameEventHitType = hitType;
//        mGameEventOnHit = gameFlowEventType;
//        mGameEventIndexData = indexData;
        if (hitType == HitType.INVALID) {
        	// The game event has been cleared, so reset the timer blocking a
        	// subsequent event.
        	mLastGameEventTime = -1.0f;
        }
    }

    public final void setForceInvincible(boolean force) {
        mForceInvincibility = force;
    }
    
    public final void setTakeHitSound(int hitType, SoundSystem.Sound sound) {
    	mTakeHitSoundHitType = hitType;
        mTakeHitSound = sound;
    }
    
    public final void setDealHitSound(int hitType, SoundSystem.Sound sound) {
        mDealHitSound = sound;
        mDealHitSoundHitType = hitType;
    }
    
    public final void setSpawnOnDealHit(int hitType, GameObjectType objectType, boolean alignToVictimX,
            boolean alignToVicitmY) {
        mSpawnOnDealHitObjectType = objectType;
        mSpawnOnDealHitHitType = hitType;
        mAlignDealHitObjectToVictimX = alignToVictimX;
        mAlignDealHitObjectToVictimY = alignToVicitmY;
    }
    
}
