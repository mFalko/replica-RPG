/*
 * Copyright (C) 2010 Matthew Falkoski
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

package com.replica.core.game;

/**
 * an enum to describe the different types of animations. They are not associated with any
 * textures. Used to by components to select the proper animation and by Animation factory
 * to bind static animation data to textures and produce a SpriteAnimation
 * @author matt
 *
 */
public enum AnimationType {
		//Humanoid animations
		HUMANOID_IDLE,
		HUMANOID_IDLE_NORTH,
		HUMANOID_IDLE_SOUTH,
		HUMANOID_IDLE_WEST,
		HUMANOID_IDLE_EAST, 
		
		HUMANOID_MOVE,
		HUMANOID_MOVE_NORTH,
		HUMANOID_MOVE_SOUTH,
		HUMANOID_MOVE_WEST,
		HUMANOID_MOVE_EAST,
		
		HUMANOID_DEATH,
		HUMANOID_DEATH_NORTH,
		HUMANOID_DEATH_SOUTH,
		HUMANOID_DEATH_WEST,
		HUMANOID_DEATH_EAST,
		
		HUMANOID_DEAD,
		HUMANOID_DEAD_NORTH,
		HUMANOID_DEAD_SOUTH,
		HUMANOID_DEAD_WEST,
		HUMANOID_DEAD_EAST,
		
		HUMANOID_HIT_REACT,
		HUMANOID_HIT_REACT_NORTH,
		HUMANOID_HIT_REACT_SOUTH,
		HUMANOID_HIT_REACT_WEST,
		HUMANOID_HIT_REACT_EAST,

		HUMANOID_ATTACK_SPELL,
		HUMANOID_ATTACK_SPELL_NORTH,
		HUMANOID_ATTACK_SPELL_SOUTH,
		HUMANOID_ATTACK_SPELL_WEST,
		HUMANOID_ATTACK_SPELL_EAST,
		
		HUMANOID_ATTACK_SWORD,
		HUMANOID_ATTACK_SWORD_NORTH,
		HUMANOID_ATTACK_SWORD_SOUTH,
		HUMANOID_ATTACK_SWORD_WEST,
		HUMANOID_ATTACK_SWORD_EAST,
		
		HUMANOID_ATTACK_POLE,
		HUMANOID_ATTACK_POLE_NORTH,
		HUMANOID_ATTACK_POLE_SOUTH,
		HUMANOID_ATTACK_POLE_WEST,
		HUMANOID_ATTACK_POLE_EAST,
		
		HUMANOID_ATTACK_ARROW,
		HUMANOID_ATTACK_ARROW_NORTH,
		HUMANOID_ATTACK_ARROW_SOUTH,
		HUMANOID_ATTACK_ARROW_WEST,
		HUMANOID_ATTACK_ARROW_EAST,
	
		//SimpleDirection
		IDLE,
		NORTH,
		NORTH_EAST,
		EAST,
		SOUTH_EAST,
		SOUTH,
		SOUTH_WEST,
		WEST,
		NORTH_WEST,
	
}
