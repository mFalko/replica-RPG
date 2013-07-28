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

package com.replica.core.collision;

/** 
 * CollisionParamaters defines global parameters related to dynamic (object vs object) collisions.
 */
public final class CollisionParameters {
    // HitType describes the type of hit that a victim object receives.  Victims may choose to 
    // react differently to the intersection depending on the hit type.
	// TODO: Make this a bit field so that objects can support multiple hit types.
    public final class HitType {
    	public static final int INVALID = 0;			// No type.
    	public static final int ATTACK  = 1 << 0;		// Attack runs code from attack class to calculate damage
    	public static final int DEATH   = 1 << 1;		// Causes instant death.
    	public static final int LAUNCH  = 1 << 2;		// A hit indicating that the attacker will launch the victim.
    	public static final int COLLECT = 1 << 3;		// Causes collectable objects to be collected by the attacker.
    	public static final int PRESS   = 1 << 4;		// A hit indicating that the attacker is pressing into the victim.
    	public static final int POSSESS = 1 << 5;		// Causes possessable objects to become possessed.
    	public static final int FLIP    = 1 << 6;		// Used to calulate draw order
    }
    
   
}
