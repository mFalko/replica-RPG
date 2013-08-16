package com.replica.core.game;


import com.replica.core.GameObject;
import com.replica.core.factory.GameObjectFactory.GameObjectType;

public class AttackConstants {
	
	public enum AttackType {
		SPELL,
		SWORD,
		POLE, 
		ARROW
	}
	
	private AttackConstants(GameObjectType attackObject, 
			float time, boolean waitForCast, AttackType attackType) {
		attackObject_ = attackObject;
		castTime_ = time;
		waitForCastTime_ = waitForCast;
		attackType_ = attackType;
	}
	
	public final GameObjectType attackObject_;
	public final float castTime_;
	public final boolean waitForCastTime_;  //true - launch GO after cast time : false - launch as many as possible during cast time
	public final AttackType attackType_;
	public final Attack attack_ = new Attack();
	
	//based off of
	//http://gamedev.stackexchange.com/questions/5626/how-to-design-the-attack-class-in-an-rpg-game
	public class Attack {
		private int baseDamage_;
		private int statModifierLevel_;
		private float damageMultiplier_;
		
		public void attack(GameObject defender) {
			defender.life -= baseDamage_ + statModifierLevel_ * damageMultiplier_;
		}
		
		public void initAttack(int baseDamage, int statModifierLevel, float damageMultiplier) {
			baseDamage_ = baseDamage;
			statModifierLevel_ = statModifierLevel;
			damageMultiplier_ = damageMultiplier;
		}
	}
	
	
	
	
	// use data? seems like too much work ...
	// How do I use data and keep all attack constants static
	
	//Spell Attacks
	public static final AttackConstants FIREBALL_1 =
			new AttackConstants(GameObjectType.FIREBALL_SMALL, 1.0f, true, AttackType.SPELL);
	
	

}
