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

import com.replica.core.GameObject;
import com.replica.core.factory.GameObjectFactory.GameObjectType;
import com.replica.utility.Vector2;

public class AttackConstants {

	public enum AttackType {
		SPELL, SWORD, POLE, ARROW
	}

	private AttackConstants(GameObjectType attackObject, float time,
			boolean waitForCast, float speed, AttackType attackType,
			boolean offsetByWidth) {
		attackObject_ = attackObject;
		castTime_ = time;
		waitForCastTime_ = waitForCast;
		attackType_ = attackType;
		speed_ = speed;
		offsetByWidth_ = offsetByWidth;
	}

	public final GameObjectType attackObject_;
	public final float castTime_;
	public final boolean waitForCastTime_; // true - launch GO after cast time :

	public final float speed_;
	public final AttackType attackType_;
	public final boolean offsetByWidth_;

	// based off of
	// http://gamedev.stackexchange.com/questions/5626/how-to-design-the-attack-class-in-an-rpg-game
	public class Attack {
		private int baseDamage_;
		private int statModifierLevel_;
		private float damageMultiplier_;

		public void attack(GameObject defender) {
			defender.life -= baseDamage_ + statModifierLevel_
					* damageMultiplier_;
		}

		public void initAttack(int baseDamage, int statModifierLevel,
				float damageMultiplier) {
			baseDamage_ = baseDamage;
			statModifierLevel_ = statModifierLevel;
			damageMultiplier_ = damageMultiplier;
		}
	}

	// use data? seems like too much work ...
	// How do I use data and keep all attack constants static

	// Spell Attacks
	public static final AttackConstants FIREBALL_1 = new AttackConstants(
			GameObjectType.FIREBALL_SMALL, 1.0f, true, 300, AttackType.SPELL, true);

	public static final AttackConstants QUAKE = new AttackConstants(
			GameObjectType.QUAKE, 0.6f, false, 0, AttackType.SPELL, false);
}
