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

package com.replica.core.factory;

import java.util.Comparator;

import com.replica.R;
import com.replica.core.BaseObject;
import com.replica.core.ContextParameters;
import com.replica.core.GameObject;
import com.replica.core.GameObject.ActionType;
import com.replica.core.GameObject.Team;
import com.replica.core.components.AttackAtDistanceComponent;
import com.replica.core.components.BackgroundCollisionComponent;
import com.replica.core.components.ButtonAnimationComponent;
import com.replica.core.components.CameraBiasComponent;
import com.replica.core.components.ChangeComponentsComponent;
import com.replica.core.components.CombatDummyComponent;
import com.replica.core.components.CrusherAndouComponent;
import com.replica.core.components.DoorAnimationComponent;
import com.replica.core.components.DynamicCollisionComponent;
import com.replica.core.components.FadeDrawableComponent;
import com.replica.core.components.FixedAnimationComponent;
import com.replica.core.components.FrameRateWatcherComponent;
import com.replica.core.components.GameComponent;
import com.replica.core.components.GameComponentPool;
import com.replica.core.components.GenericAnimationComponent;
import com.replica.core.components.GhostComponent;
import com.replica.core.components.GravityComponent;
import com.replica.core.components.HitPlayerComponent;
import com.replica.core.components.HitReactionComponent;
import com.replica.core.components.HumanoidAnimationComponent;
import com.replica.core.components.HumanoidAnimationComponent.SimpleSpriteUpdater;
import com.replica.core.components.InventoryComponent;
import com.replica.core.components.LaunchProjectileComponent;
import com.replica.core.components.LauncherComponent;
import com.replica.core.components.LifetimeComponent;
import com.replica.core.components.MotionBlurComponent;
import com.replica.core.components.MovementComponent;
import com.replica.core.components.NPCAnimationComponent;
import com.replica.core.components.NPCComponent;
import com.replica.core.components.OrbitalMagnetComponent;
import com.replica.core.components.PhysicsComponent;
import com.replica.core.components.PlaySingleSoundComponent;
import com.replica.core.components.PlayerComponent;
import com.replica.core.components.PopOutComponent;
import com.replica.core.components.RenderComponent;
import com.replica.core.components.ScrollerComponent;
import com.replica.core.components.SelectDialogComponent;
import com.replica.core.components.SimpleAnimationComponent;
import com.replica.core.components.SimpleCollisionComponent;
import com.replica.core.components.SolidSurfaceComponent;
import com.replica.core.components.SpriteComponent;
import com.replica.core.components.SteeringBehavior;
import com.replica.core.components.VehicleComponent;
import com.replica.core.factory.GameObjectFactory.GameObjectType;
import com.replica.core.game.AnimationType;
import com.replica.core.graphics.SpriteAnimation;
import com.replica.core.graphics.TextureLibrary;
import com.replica.core.systems.CameraSystem;
import com.replica.utility.DebugLog;
import com.replica.utility.FixedSizeArray;
import com.replica.utility.SortConstants;
import com.replica.utility.TObjectPool;
import com.replica.utility.Vector2;

/**
 * A class for generating game objects at runtime. This should really be
 * replaced with something that is data-driven, but it is hard to do data
 * parsing quickly at runtime. For the moment this class is full of large
 * functions that just patch pointers between objects, but in the future those
 * functions should either be a) generated from data at compile time, or b)
 * described by data at runtime.
 */
public class GameObjectFactory extends BaseObject {
	private final static int MAX_GAME_OBJECTS = 384;
	private final static ComponentPoolComparator sComponentPoolComparator = new ComponentPoolComparator();

	private FixedSizeArray<FixedSizeArray<BaseObject>> mStaticData;

	private FixedSizeArray<GameComponentPool> mComponentPools;
	private GameComponentPool mPoolSearchDummy;
	private GameObjectPool mGameObjectPool;

	private AnimationFactory animationFactory;

	private float mTightActivationRadius;
	private float mNormalActivationRadius;
	private float mWideActivationRadius;
	private float mAlwaysActive;

	// A list of game objects that can be spawned at runtime. Note that the
	// indicies of these
	// objects must match the order of the object tileset in the level editor in
	// order for the
	// level content to make sense.
	public enum GameObjectType {
		INVALID(-1),

		PLAYER(0),

		// Collectables 1-100

		// NPC Characters 101 - 200

		// NPC Enemies 201 - 300
		COMBAT_DUMMY(201),
		SKELETON(202),

		// Objects 301 - 400

		// Effects 401 - 500
		QUAKE(401),
		// Projectiles 501 - 600
		FIREBALL_SMALL(501),

		// Special Objects -- Not spawnable normally

		// End
		OBJECT_COUNT(-1);

		private final int mIndex;

		GameObjectType(int index) {
			this.mIndex = index;
		}

		public int index() {
			return mIndex;
		}

		// TODO: Is there any better way to do this?
		public static GameObjectType indexToType(int index) {
			final GameObjectType[] valuesArray = values();
			GameObjectType foundType = INVALID;
			for (int x = 0; x < valuesArray.length; x++) {
				GameObjectType type = valuesArray[x];
				if (type.mIndex == index) {
					foundType = type;
					break;
				}
			}
			return foundType;
		}

	}

	public GameObjectFactory() {
		super();

		mGameObjectPool = new GameObjectPool(MAX_GAME_OBJECTS);

		final int objectTypeCount = GameObjectType.OBJECT_COUNT.ordinal();
		mStaticData = new FixedSizeArray<FixedSizeArray<BaseObject>>(
				objectTypeCount);

		for (int x = 0; x < objectTypeCount; x++) {
			mStaticData.add(null);
		}

		final ContextParameters context = sSystemRegistry.contextParameters;
		final float halfHeight2 = (context.gameHeight * 0.5f)
				* (context.gameHeight * 0.5f);
		final float halfWidth2 = (context.gameWidth * 0.5f)
				* (context.gameWidth * 0.5f);
		final float screenSizeRadius = (float) Math.sqrt(halfHeight2
				+ halfWidth2);
		mTightActivationRadius = screenSizeRadius + 128.0f;
		mNormalActivationRadius = screenSizeRadius * 1.25f;
		mWideActivationRadius = screenSizeRadius * 2.0f;
		mAlwaysActive = -1.0f;

		// TODO: I wish there was a way to do this automatically, but the
		// ClassLoader doesn't seem
		// to provide access to the currently loaded class list. There's some
		// discussion of walking
		// the actual class file objects and using forName() to instantiate
		// them, but that sounds
		// really heavy-weight. For now I'll rely on (sucky) manual enumeration.
		class ComponentClass {
			public Class<?> type;
			public int poolSize;

			public ComponentClass(Class<?> classType, int size) {
				type = classType;
				poolSize = size;
			}
		}

		// TODO this enumeration doesn't make sense for this game, rework counts
		ComponentClass[] componentTypes = {
				
				new ComponentClass(AttackAtDistanceComponent.class, 16),
				new ComponentClass(BackgroundCollisionComponent.class, 192),
				new ComponentClass(ButtonAnimationComponent.class, 32),
				new ComponentClass(CameraBiasComponent.class, 8),
				new ComponentClass(ChangeComponentsComponent.class, 256),
				new ComponentClass(CombatDummyComponent.class, 32),
				new ComponentClass(CrusherAndouComponent.class, 1),
				new ComponentClass(DoorAnimationComponent.class, 256), // !
				new ComponentClass(DynamicCollisionComponent.class, 256),
				new ComponentClass(FadeDrawableComponent.class, 32),
				new ComponentClass(FixedAnimationComponent.class, 32),
				new ComponentClass(FrameRateWatcherComponent.class, 1),
				new ComponentClass(GenericAnimationComponent.class, 32),
				new ComponentClass(GhostComponent.class, 256),
				new ComponentClass(GravityComponent.class, 128),
				new ComponentClass(HitPlayerComponent.class, 256),
				new ComponentClass(HitReactionComponent.class, 256),
				new ComponentClass(HumanoidAnimationComponent.class, 256),
				new ComponentClass(InventoryComponent.class, 128),
				new ComponentClass(LauncherComponent.class, 16),
				new ComponentClass(LaunchProjectileComponent.class, 128),
				new ComponentClass(LifetimeComponent.class, 384),
				new ComponentClass(MotionBlurComponent.class, 1),
				new ComponentClass(MovementComponent.class, 128),
				new ComponentClass(NPCAnimationComponent.class, 32),
				new ComponentClass(NPCComponent.class, 32),
				new ComponentClass(OrbitalMagnetComponent.class, 1),
				new ComponentClass(PhysicsComponent.class, 32),
				new ComponentClass(PlayerComponent.class, 1),
				new ComponentClass(PlaySingleSoundComponent.class, 128),
				new ComponentClass(PopOutComponent.class, 32),
				new ComponentClass(RenderComponent.class, 384),
				new ComponentClass(ScrollerComponent.class, 8),
				new ComponentClass(SelectDialogComponent.class, 8),
				new ComponentClass(SimpleAnimationComponent.class, 64),
				new ComponentClass(SimpleCollisionComponent.class, 32),
				new ComponentClass(SolidSurfaceComponent.class, 16),
				new ComponentClass(SpriteComponent.class, 384),
				new ComponentClass(SteeringBehavior.class, 32),
				new ComponentClass(VehicleComponent.class, 32),
		};

		mComponentPools = new FixedSizeArray<GameComponentPool>(
				componentTypes.length, sComponentPoolComparator);
		for (int x = 0; x < componentTypes.length; x++) {
			ComponentClass component = componentTypes[x];
			mComponentPools.add(new GameComponentPool(component.type,
					component.poolSize));
		}
		mComponentPools.sort(true);

		mPoolSearchDummy = new GameComponentPool(Object.class, 1);

		animationFactory = new AnimationFactory();
	}

	@Override
	public void reset() {
	}

	protected GameComponentPool getComponentPool(Class<?> componentType) {
		GameComponentPool pool = null;
		mPoolSearchDummy.objectClass = componentType;
		final int index = mComponentPools.find(mPoolSearchDummy, false);
		if (index != -1) {
			pool = mComponentPools.get(index);
		}
		return pool;
	}

	protected GameComponent allocateComponent(Class<?> componentType) {
		GameComponentPool pool = getComponentPool(componentType);
		assert pool != null;
		GameComponent component = null;
		if (pool != null) {
			component = pool.allocate();
		}
		return component;
	}

	public void releaseComponent(GameComponent component) {
		GameComponentPool pool = getComponentPool(component.getClass());
		assert pool != null;
		if (pool != null) {
			component.reset();
			component.shared = false;
			pool.release(component);
		}
	}

	protected boolean componentAvailable(Class<?> componentType, int count) {
		boolean canAllocate = false;
		GameComponentPool pool = getComponentPool(componentType);
		assert pool != null;
		if (pool != null) {
			canAllocate = pool.getAllocatedCount() + count < pool.getSize();
		}
		return canAllocate;
	}

	public void preloadEffects() {
		// These textures appear in every level, so they are long-term.
		TextureLibrary textureLibrary = sSystemRegistry.longTermTextureLibrary;

		// skeleton humanoid
		textureLibrary
				.allocateTexture(R.drawable.body_skeleton_walkcycle_north);
		textureLibrary
				.allocateTexture(R.drawable.body_skeleton_walkcycle_south);
		textureLibrary.allocateTexture(R.drawable.body_skeleton_walkcycle_west);
		textureLibrary.allocateTexture(R.drawable.body_skeleton_walkcycle_east);
		textureLibrary
				.allocateTexture(R.drawable.body_skeleton_spellcast_north);
		textureLibrary
				.allocateTexture(R.drawable.body_skeleton_spellcast_south);
		textureLibrary.allocateTexture(R.drawable.body_skeleton_spellcast_west);
		textureLibrary.allocateTexture(R.drawable.body_skeleton_spellcast_east);

		// spell fireball
		textureLibrary.allocateTexture(R.drawable.attack_spell_fireball_north);
		textureLibrary.allocateTexture(R.drawable.attack_spell_fireball_south);
		textureLibrary.allocateTexture(R.drawable.attack_spell_fireball_west);
		textureLibrary.allocateTexture(R.drawable.attack_spell_fireball_east);
		
		textureLibrary.allocateTexture(R.drawable.quake);
		

	}

	public void destroy(GameObject object) {

		object.commitUpdates();
		final int componentCount = object.getCount();
		for (int x = 0; x < componentCount; x++) {
			GameComponent component = (GameComponent) object.get(x);
			if (!component.shared) {
				releaseComponent(component);
			}
		}
		object.removeAll();
		object.commitUpdates();
		mGameObjectPool.release(object);

	}

	private FixedSizeArray<BaseObject> getStaticData(GameObjectType type) {
		return mStaticData.get(type.ordinal());
	}

	private void setStaticData(GameObjectType type,
			FixedSizeArray<BaseObject> data) {
		int index = type.ordinal();
		assert mStaticData.get(index) == null;

		final int staticDataCount = data.getCount();

		for (int x = 0; x < staticDataCount; x++) {
			BaseObject entry = data.get(x);
			if (entry instanceof GameComponent) {
				((GameComponent) entry).shared = true;
			}
		}

		mStaticData.set(index, data);
	}

	private void addStaticData(GameObjectType type, GameObject object,
			SpriteComponent sprite) {
		FixedSizeArray<BaseObject> staticData = getStaticData(type);
		assert staticData != null;

		if (staticData != null) {
			final int staticDataCount = staticData.getCount();

			for (int x = 0; x < staticDataCount; x++) {
				BaseObject entry = staticData.get(x);
				if (entry instanceof GameComponent && object != null) {
					object.add((GameComponent) entry);
				} else if (entry instanceof SpriteAnimation && sprite != null) {
					sprite.addAnimation((SpriteAnimation) entry);
				}
			}
		}
	}

	public void clearStaticData() {
		final int typeCount = mStaticData.getCount();
		for (int x = 0; x < typeCount; x++) {
			FixedSizeArray<BaseObject> staticData = mStaticData.get(x);
			if (staticData != null) {
				final int count = staticData.getCount();
				for (int y = 0; y < count; y++) {
					BaseObject entry = staticData.get(y);
					if (entry != null) {
						if (entry instanceof GameComponent) {
							releaseComponent((GameComponent) entry);
						}
					}
				}
				staticData.clear();
				mStaticData.set(x, null);
			}
		}
	}

	public void sanityCheckPools() {
		final int outstandingObjects = mGameObjectPool.getAllocatedCount();
		if (outstandingObjects != 0) {
			DebugLog.d("Sanity Check", "Outstanding game object allocations! ("
					+ outstandingObjects + ")");
			assert false;
		}

		final int componentPoolCount = mComponentPools.getCount();
		for (int x = 0; x < componentPoolCount; x++) {
			final int outstandingComponents = mComponentPools.get(x)
					.getAllocatedCount();

			if (outstandingComponents != 0) {
				DebugLog.d(
						"Sanity Check",
						"Outstanding "
								+ mComponentPools.get(x).objectClass
										.getSimpleName() + " allocations! ("
								+ outstandingComponents + ")");
				// assert false;
			}
		}
	}

	public GameObject spawn(GameObjectType type, float x, float y) {
		GameObject newObject = null;
		switch (type) {

		case QUAKE: 
			newObject = spawnQuake(x,y);
			break;
			
			
		case FIREBALL_SMALL: 
			newObject = spawnFireball(x,y);
			break;
		}

		return newObject;
	}

	public GameObject spawnPlayer(float positionX, float positionY) {
		
		final float width = 50;
		final float height = 50;
		
		GameObject object = mGameObjectPool.allocate();
		object.getPosition().set(positionX, positionY);
		object.activationRadius = mAlwaysActive;
		object.width = width;
		object.height = height;

		FixedSizeArray<BaseObject> staticData = getStaticData(GameObjectType.PLAYER);

		if (staticData == null) {
			final int staticObjectCount = 12;
			staticData = new FixedSizeArray<BaseObject>(staticObjectCount);

			SpriteAnimation moveNorth = animationFactory.loadAnimation(
					R.raw.animation_humanoid_move_north,
					R.drawable.body_male_walkcycle_north, width, height);	
			moveNorth.setPhase(AnimationType.HUMANOID_MOVE_NORTH.ordinal());
			
			SpriteAnimation moveSouth = animationFactory.loadAnimation(
					R.raw.animation_humanoid_move_south,
					R.drawable.body_male_walkcycle_south, width, height);
			moveSouth.setPhase(AnimationType.HUMANOID_MOVE_SOUTH.ordinal());
			
			SpriteAnimation moveWest = animationFactory.loadAnimation(
					R.raw.animation_humanoid_move_west,
					R.drawable.body_male_walkcycle_west, width, height);
			moveWest.setPhase(AnimationType.HUMANOID_MOVE_WEST.ordinal());
			
			SpriteAnimation moveEast = animationFactory.loadAnimation(
					R.raw.animation_humanoid_move_east,
					R.drawable.body_male_walkcycle_east, width, height);
			moveEast.setPhase(AnimationType.HUMANOID_MOVE_EAST.ordinal());
			
			SpriteAnimation idleNorth = animationFactory.loadAnimation(
					R.raw.animation_humanoid_idle_north,
					R.drawable.body_male_walkcycle_north, width, height);
			idleNorth.setPhase(AnimationType.HUMANOID_IDLE_NORTH.ordinal());
			
			SpriteAnimation idleSouth = animationFactory.loadAnimation(
					R.raw.animation_humanoid_idle_south,
					R.drawable.body_male_walkcycle_south, width, height);
			idleSouth.setPhase(AnimationType.HUMANOID_IDLE_SOUTH.ordinal());
			
			SpriteAnimation idleWest = animationFactory.loadAnimation(
					R.raw.animation_humanoid_idle_west,
					R.drawable.body_male_walkcycle_west, width, height);
			idleWest.setPhase(AnimationType.HUMANOID_IDLE_WEST.ordinal());
			
			SpriteAnimation idleEast = animationFactory.loadAnimation(
					R.raw.animation_humanoid_idle_east,
					R.drawable.body_male_walkcycle_east, width, height);
			idleEast.setPhase(AnimationType.HUMANOID_IDLE_EAST.ordinal());
			
			SpriteAnimation spellcastNorth = animationFactory.loadAnimation(
					R.raw.animation_humanoid_attack_spell_north,
					R.drawable.body_male_spellcast_north, width, height);
			spellcastNorth.setPhase(AnimationType.HUMANOID_ATTACK_SPELL_NORTH.ordinal());
			
			SpriteAnimation spellcastSouth = animationFactory.loadAnimation(
					R.raw.animation_humanoid_attack_spell_south,
					R.drawable.body_male_spellcast_south, width, height);
			spellcastSouth.setPhase(AnimationType.HUMANOID_ATTACK_SPELL_SOUTH.ordinal());
			
			SpriteAnimation spellcastWest = animationFactory.loadAnimation(
					R.raw.animation_humanoid_attack_spell_west,
					R.drawable.body_male_spellcast_west, width, height);
			spellcastWest.setPhase(AnimationType.HUMANOID_ATTACK_SPELL_WEST.ordinal());
			
			SpriteAnimation spellcastEast = animationFactory.loadAnimation(
					R.raw.animation_humanoid_attack_spell_east,
					R.drawable.body_male_spellcast_east, width, height);
			spellcastEast.setPhase(AnimationType.HUMANOID_ATTACK_SPELL_EAST.ordinal());	

			staticData.add(moveNorth);
			staticData.add(moveSouth);
			staticData.add(moveWest);
			staticData.add(moveEast);
			staticData.add(idleNorth);
			staticData.add(idleSouth);
			staticData.add(idleWest);
			staticData.add(idleEast);
			staticData.add(spellcastNorth);
			staticData.add(spellcastSouth);
			staticData.add(spellcastWest);
			staticData.add(spellcastEast);

			setStaticData(GameObjectType.PLAYER, staticData);
		}

		PlayerComponent playerComponent = new PlayerComponent();

		HumanoidAnimationComponent playerAnimationComponent = (HumanoidAnimationComponent) allocateComponent(HumanoidAnimationComponent.class);

		SpriteComponent playerSpriteComponent = (SpriteComponent) allocateComponent(SpriteComponent.class);

		RenderComponent playerRenCom = (RenderComponent) allocateComponent(RenderComponent.class);

		BackgroundCollisionComponent backgroundCollisionComponent = (BackgroundCollisionComponent) allocateComponent(BackgroundCollisionComponent.class);
		backgroundCollisionComponent.setSize(10, 10);
		backgroundCollisionComponent.setOffset(20, 0);

		playerSpriteComponent.setSize((int) object.width, (int) object.height);
		playerSpriteComponent.setRenderComponent(playerRenCom);
		playerRenCom.setPriority(SortConstants.GAMEOBJECT_BASE_FLIP);
		
		
		// TODO: remove Sprite updater and make something better
		SimpleSpriteUpdater spriteUpdater = new SimpleSpriteUpdater();
		spriteUpdater.setSprite(playerSpriteComponent);
		playerAnimationComponent.setSpriteUpdater(spriteUpdater);

		
		
		DynamicCollisionComponent dynamicCollision = (DynamicCollisionComponent) allocateComponent(DynamicCollisionComponent.class);
		playerSpriteComponent.setCollisionComponent(dynamicCollision);
		
		HitReactionComponent hitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
        dynamicCollision.setHitReactionComponent(hitReact);
        playerComponent.setHitReactionComponent(hitReact);
        
        
        LaunchProjectileComponent launcher = (LaunchProjectileComponent)allocateComponent(LaunchProjectileComponent.class);
        playerComponent.setLaunchProjectileComponent(launcher);
        
		// object.life =
		object.team = Team.PLAYER;

		object.add(playerAnimationComponent);
		object.add(playerSpriteComponent);
		object.add(playerComponent);
		object.add(playerRenCom);
		object.add(backgroundCollisionComponent);
		object.add(dynamicCollision);
		object.add(hitReact);
		object.add(launcher);
		addStaticData(GameObjectType.PLAYER, object, playerSpriteComponent);

		object.setCurrentAction(ActionType.IDLE);
		
		CameraSystem camera = sSystemRegistry.cameraSystem;
        if (camera != null) {
            camera.setTarget(object);
        }
		
		return object;

	}

	public GameObject spawnFireball(float positionX, float positionY) {
		final float width = 30;
		final float height = 30;
		GameObject object = mGameObjectPool.allocate();
		object.getPosition().set(positionX, positionY);
		object.activationRadius = mTightActivationRadius;
		object.width = width;
		object.height = height;
		object.life = 1;
		object.destroyOnDeactivation = true;

		FixedSizeArray<BaseObject> staticData = getStaticData(GameObjectType.FIREBALL_SMALL);
		if (staticData == null) {
			final int staticObjectCount = 4;
			staticData = new FixedSizeArray<BaseObject>(staticObjectCount);
			
			SpriteAnimation moveNorth = animationFactory.loadAnimation(
					R.raw.animation_attack_spell_fireball_north,
					R.drawable.attack_spell_fireball_north, width, height);
			moveNorth.setPhase(AnimationType.NORTH.ordinal());	

			SpriteAnimation moveSouth =animationFactory.loadAnimation(
					R.raw.animation_attack_spell_fireball_south,
					R.drawable.attack_spell_fireball_south, width, height);
			moveSouth.setPhase(AnimationType.SOUTH.ordinal());	

			SpriteAnimation moveWest = animationFactory.loadAnimation(
					R.raw.animation_attack_spell_fireball_west,
					R.drawable.attack_spell_fireball_west, width, height);
			moveWest.setPhase(AnimationType.WEST.ordinal());	

			SpriteAnimation moveEast = animationFactory.loadAnimation(
					R.raw.animation_attack_spell_fireball_east,
					R.drawable.attack_spell_fireball_east, width, height);
			moveEast.setPhase(AnimationType.EAST.ordinal());	

			staticData.add(moveNorth);
			staticData.add(moveSouth);
			staticData.add(moveWest);
			staticData.add(moveEast);
			setStaticData(GameObjectType.FIREBALL_SMALL, staticData);
		}

		SpriteComponent objectSpriteComponent = (SpriteComponent) allocateComponent(SpriteComponent.class);
		RenderComponent objectRenCom = (RenderComponent) allocateComponent(RenderComponent.class);
		SimpleAnimationComponent animationCom = (SimpleAnimationComponent) allocateComponent(SimpleAnimationComponent.class);

		objectSpriteComponent.setSize((int) object.width, (int) object.height);
		objectSpriteComponent.setRenderComponent(objectRenCom);
		animationCom.setSpriteComponent(objectSpriteComponent);
		objectRenCom.setPriority(4);

		LifetimeComponent lifetimeCom = (LifetimeComponent) allocateComponent(LifetimeComponent.class);
		lifetimeCom.setDieOnHitBackground(true);
		lifetimeCom.setDieWhenInvisible(true);

		MovementComponent movementCom = (MovementComponent) allocateComponent(MovementComponent.class);
		SimpleCollisionComponent collisionCom = (SimpleCollisionComponent) allocateComponent(SimpleCollisionComponent.class);

		DynamicCollisionComponent dynamicCollision = (DynamicCollisionComponent) allocateComponent(DynamicCollisionComponent.class);
		objectSpriteComponent.setCollisionComponent(dynamicCollision);
		
		HitReactionComponent hitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
		hitReact.setDieOnAttack(true);
        dynamicCollision.setHitReactionComponent(hitReact);
        
		
		object.add(objectSpriteComponent);
		object.add(objectRenCom);
		object.add(animationCom);
		object.add(lifetimeCom);
		object.add(movementCom);
		object.add(collisionCom);
		object.add(dynamicCollision);
		
		addStaticData(GameObjectType.FIREBALL_SMALL, object,
				objectSpriteComponent);

		object.setCurrentAction(ActionType.MOVE);
		return object;
	}
	

	public GameObject spawnQuake(float positionX, float positionY) {
		final float width = 256;
		final float height = 128;
		GameObject object = mGameObjectPool.allocate();
		object.getPosition().set(positionX, positionY);
		object.activationRadius = mTightActivationRadius;
		object.width = width;
		object.height = height;
		object.life = 1;
		object.destroyOnDeactivation = true;

		FixedSizeArray<BaseObject> staticData = getStaticData(GameObjectType.QUAKE);
		if (staticData == null) {
			final int staticObjectCount = 1;
			staticData = new FixedSizeArray<BaseObject>(staticObjectCount);
			
			SpriteAnimation quakeAnimation = animationFactory.loadAnimation(
					R.raw.attack_quake,
					R.drawable.quake, width, height);
			quakeAnimation.setPhase(AnimationType.IDLE.ordinal());	

	

			staticData.add(quakeAnimation);
			setStaticData(GameObjectType.QUAKE, staticData);
		}

		SpriteComponent objectSpriteComponent = (SpriteComponent) allocateComponent(SpriteComponent.class);
		RenderComponent objectRenCom = (RenderComponent) allocateComponent(RenderComponent.class);
		SimpleAnimationComponent animationCom = (SimpleAnimationComponent) allocateComponent(SimpleAnimationComponent.class);

		objectSpriteComponent.setSize((int) object.width, (int) object.height);
		objectSpriteComponent.setRenderComponent(objectRenCom);
		animationCom.setSpriteComponent(objectSpriteComponent);
		objectRenCom.setPriority(4);

		LifetimeComponent lifetimeCom = (LifetimeComponent) allocateComponent(LifetimeComponent.class);
		lifetimeCom.setTimeUntilDeath(0.6f);

		DynamicCollisionComponent dynamicCollision = (DynamicCollisionComponent) allocateComponent(DynamicCollisionComponent.class);
		objectSpriteComponent.setCollisionComponent(dynamicCollision);
		
		HitReactionComponent hitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
        dynamicCollision.setHitReactionComponent(hitReact);
        
		object.add(objectSpriteComponent);
		object.add(objectRenCom);
		object.add(animationCom);
		object.add(lifetimeCom);
		object.add(dynamicCollision);
		addStaticData(GameObjectType.QUAKE, object,
				objectSpriteComponent);

		object.setCurrentAction(ActionType.IDLE);
		return object;
	}
	
	
	public GameObject spawnCombatDummy(float positionX, float positionY) {
		final float width = 50;
		final float height = 50;
		GameObject object = mGameObjectPool.allocate();
		object.getPosition().set(positionX, positionY);
		object.activationRadius = mTightActivationRadius;
		object.width = width;
		object.height = height;
		object.life = 100;
		object.facingDirection.set(0, -1); //always faces south...hack?

		FixedSizeArray<BaseObject> staticData = getStaticData(GameObjectType.COMBAT_DUMMY);
		if (staticData == null) {
			final int staticObjectCount = 5;
			staticData = new FixedSizeArray<BaseObject>(staticObjectCount);
			
			SpriteAnimation idleAnimation = animationFactory.loadAnimation(
					R.raw.animation_combat_dummy_alive_idle,
					R.drawable.animation_combat_dummy_body, width, height);
			idleAnimation.setPhase(AnimationType.HUMANOID_IDLE_SOUTH.ordinal());	

			SpriteAnimation hitReactAnimation =animationFactory.loadAnimation(
					R.raw.animation_combat_dummy_alive,
					R.drawable.animation_combat_dummy_body, width, height);
			hitReactAnimation.setPhase(AnimationType.HUMANOID_HIT_REACT_SOUTH.ordinal());	

			SpriteAnimation deathAnimation = animationFactory.loadAnimation(
					R.raw.animation_combat_dummy_death,
					R.drawable.animation_combat_dummy_death, width, height);
			deathAnimation.setPhase(AnimationType.HUMANOID_DEATH_SOUTH.ordinal());	

			SpriteAnimation deadIdleAnimation = animationFactory.loadAnimation(
					R.raw.animation_combat_dummy_dead_idle,
					R.drawable.animation_combat_dummy_death, width, height);
			deadIdleAnimation.setPhase(AnimationType.HUMANOID_DEAD_SOUTH.ordinal());	
			
			//TODO: this is a bad way to keep GOs from passing through eachother
			//make a dynamic collision box that uses HitType.PUSH
			SolidSurfaceComponent solidSurfaceComponent = (SolidSurfaceComponent) allocateComponent(SolidSurfaceComponent.class);
			solidSurfaceComponent.inititalize(4); //TODO: magic number >.<
			Vector2 topLeft = new Vector2(20, 6);
			Vector2 topRight = new Vector2(30, 6);
			Vector2 bottomLeft = new Vector2(20, 0);
			Vector2 bottomRight = new Vector2(30, 0);
			Vector2 normal = new Vector2(0,0);
			solidSurfaceComponent.addSurface(topLeft,     topRight,    normal);
			solidSurfaceComponent.addSurface(bottomLeft,  bottomRight, normal);
			solidSurfaceComponent.addSurface(bottomLeft,  topLeft,     normal);
			solidSurfaceComponent.addSurface(bottomRight, topRight,    normal);
			
			
			staticData.add(idleAnimation);
			staticData.add(hitReactAnimation);
			staticData.add(deathAnimation);
			staticData.add(deadIdleAnimation);
			staticData.add(solidSurfaceComponent);
			setStaticData(GameObjectType.COMBAT_DUMMY, staticData);
		}

		SpriteComponent objectSpriteComponent = (SpriteComponent) allocateComponent(SpriteComponent.class);
		RenderComponent objectRenCom = (RenderComponent) allocateComponent(RenderComponent.class);
		HumanoidAnimationComponent animationCom = (HumanoidAnimationComponent) allocateComponent(HumanoidAnimationComponent.class);

		objectSpriteComponent.setSize((int) object.width, (int) object.height);
		objectSpriteComponent.setRenderComponent(objectRenCom);
		
		// TODO: remove Sprite updater and make something better
		SimpleSpriteUpdater spriteUpdater = new SimpleSpriteUpdater();
		spriteUpdater.setSprite(objectSpriteComponent);
		animationCom.setSpriteUpdater(spriteUpdater);
		objectRenCom.setPriority(SortConstants.GAMEOBJECT_BASE_FLIP);

		DynamicCollisionComponent dynamicCollision = (DynamicCollisionComponent) allocateComponent(DynamicCollisionComponent.class);
		objectSpriteComponent.setCollisionComponent(dynamicCollision);
		
		HitReactionComponent hitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
        dynamicCollision.setHitReactionComponent(hitReact);
        
        CombatDummyComponent combatDummyComponent = (CombatDummyComponent)allocateComponent(CombatDummyComponent.class);
        
        object.add(combatDummyComponent);
		object.add(objectSpriteComponent);
		object.add(objectRenCom);
		object.add(animationCom);
		object.add(dynamicCollision);
		object.add(hitReact);
		
		addStaticData(GameObjectType.COMBAT_DUMMY, object,
				objectSpriteComponent);

		object.setCurrentAction(ActionType.IDLE);
		return object;
	}
	
	public GameObject spawnSkeleton(float positionX, float positionY) {
		
		final float width = 50;
		final float height = 50;
		
		GameObject object = mGameObjectPool.allocate();
		object.getPosition().set(positionX, positionY);
		object.activationRadius = mNormalActivationRadius;
		object.width = width;
		object.height = height;
		object.setMass(20);
        object.setMaxSpeed(20);
        object.facingDirection.y = 1;
		object.team = Team.ENEMY;
		
		
		FixedSizeArray<BaseObject> staticData = getStaticData(GameObjectType.SKELETON);

		if (staticData == null) {
			final int staticObjectCount = 12;
			staticData = new FixedSizeArray<BaseObject>(staticObjectCount);

			SpriteAnimation moveNorth = animationFactory.loadAnimation(
					R.raw.animation_humanoid_move_north,
					R.drawable.body_skeleton_walkcycle_north, width, height);	
			moveNorth.setPhase(AnimationType.HUMANOID_MOVE_NORTH.ordinal());
			
			SpriteAnimation moveSouth = animationFactory.loadAnimation(
					R.raw.animation_humanoid_move_south,
					R.drawable.body_skeleton_walkcycle_south, width, height);
			moveSouth.setPhase(AnimationType.HUMANOID_MOVE_SOUTH.ordinal());
			
			SpriteAnimation moveWest = animationFactory.loadAnimation(
					R.raw.animation_humanoid_move_west,
					R.drawable.body_skeleton_walkcycle_west, width, height);
			moveWest.setPhase(AnimationType.HUMANOID_MOVE_WEST.ordinal());
			
			SpriteAnimation moveEast = animationFactory.loadAnimation(
					R.raw.animation_humanoid_move_east,
					R.drawable.body_skeleton_walkcycle_east, width, height);
			moveEast.setPhase(AnimationType.HUMANOID_MOVE_EAST.ordinal());
			
			SpriteAnimation idleNorth = animationFactory.loadAnimation(
					R.raw.animation_humanoid_idle_north,
					R.drawable.body_skeleton_walkcycle_north, width, height);
			idleNorth.setPhase(AnimationType.HUMANOID_IDLE_NORTH.ordinal());
			
			SpriteAnimation idleSouth = animationFactory.loadAnimation(
					R.raw.animation_humanoid_idle_south,
					R.drawable.body_skeleton_walkcycle_south, width, height);
			idleSouth.setPhase(AnimationType.HUMANOID_IDLE_SOUTH.ordinal());
			
			SpriteAnimation idleWest = animationFactory.loadAnimation(
					R.raw.animation_humanoid_idle_west,
					R.drawable.body_skeleton_walkcycle_west, width, height);
			idleWest.setPhase(AnimationType.HUMANOID_IDLE_WEST.ordinal());
			
			SpriteAnimation idleEast = animationFactory.loadAnimation(
					R.raw.animation_humanoid_idle_east,
					R.drawable.body_skeleton_walkcycle_east, width, height);
			idleEast.setPhase(AnimationType.HUMANOID_IDLE_EAST.ordinal());
			
			SpriteAnimation spellcastNorth = animationFactory.loadAnimation(
					R.raw.animation_humanoid_attack_spell_north,
					R.drawable.body_skeleton_spellcast_north, width, height);
			spellcastNorth.setPhase(AnimationType.HUMANOID_ATTACK_SPELL_NORTH.ordinal());
			
			SpriteAnimation spellcastSouth = animationFactory.loadAnimation(
					R.raw.animation_humanoid_attack_spell_south,
					R.drawable.body_skeleton_spellcast_south, width, height);
			spellcastSouth.setPhase(AnimationType.HUMANOID_ATTACK_SPELL_SOUTH.ordinal());
			
			SpriteAnimation spellcastWest = animationFactory.loadAnimation(
					R.raw.animation_humanoid_attack_spell_west,
					R.drawable.body_skeleton_spellcast_west, width, height);
			spellcastWest.setPhase(AnimationType.HUMANOID_ATTACK_SPELL_WEST.ordinal());
			
			SpriteAnimation spellcastEast = animationFactory.loadAnimation(
					R.raw.animation_humanoid_attack_spell_east,
					R.drawable.body_skeleton_spellcast_east, width, height);
			spellcastEast.setPhase(AnimationType.HUMANOID_ATTACK_SPELL_EAST.ordinal());	

			staticData.add(moveNorth);
			staticData.add(moveSouth);
			staticData.add(moveWest);
			staticData.add(moveEast);
			staticData.add(idleNorth);
			staticData.add(idleSouth);
			staticData.add(idleWest);
			staticData.add(idleEast);
			staticData.add(spellcastNorth);
			staticData.add(spellcastSouth);
			staticData.add(spellcastWest);
			staticData.add(spellcastEast);

			setStaticData(GameObjectType.SKELETON, staticData);
		}

		

		HumanoidAnimationComponent playerAnimationComponent = (HumanoidAnimationComponent) allocateComponent(HumanoidAnimationComponent.class);

		SpriteComponent spriteComponent = (SpriteComponent) allocateComponent(SpriteComponent.class);

		RenderComponent playerRenCom = (RenderComponent) allocateComponent(RenderComponent.class);

		BackgroundCollisionComponent backgroundCollisionComponent = (BackgroundCollisionComponent) allocateComponent(BackgroundCollisionComponent.class);
		backgroundCollisionComponent.setSize(10, 10);
		backgroundCollisionComponent.setOffset(20, 0);

		spriteComponent.setSize((int) object.width, (int) object.height);
		spriteComponent.setRenderComponent(playerRenCom);
		playerRenCom.setPriority(SortConstants.GAMEOBJECT_BASE_FLIP); //TODO why do this here?
		
		
		
		// TODO: remove Sprite updater and make something better cannot allocate here
		SimpleSpriteUpdater spriteUpdater = new SimpleSpriteUpdater();
		spriteUpdater.setSprite(spriteComponent);
		playerAnimationComponent.setSpriteUpdater(spriteUpdater);

		
		
		DynamicCollisionComponent dynamicCollision = (DynamicCollisionComponent) allocateComponent(DynamicCollisionComponent.class);
		spriteComponent.setCollisionComponent(dynamicCollision);
		
		HitReactionComponent hitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
        dynamicCollision.setHitReactionComponent(hitReact);
        
        
        //playerComponent.setHitReactionComponent(hitReact);
        
        
        VehicleComponent vehicleComponent = (VehicleComponent) allocateComponent(VehicleComponent.class);
        NPCComponent npcComponent  = (NPCComponent) allocateComponent(NPCComponent.class);
        SteeringBehavior steeringBehavior = (SteeringBehavior) allocateComponent(SteeringBehavior.class);
        
        
        vehicleComponent.setSteeringAdapter(steeringBehavior);
        npcComponent.setHitReactionComponent(hitReact);
        npcComponent.setSteering(steeringBehavior);
        
     
        
        steeringBehavior.setVehicleParent(object);
        
        

		object.add(playerAnimationComponent);
		object.add(spriteComponent);
		object.add(playerRenCom);
		object.add(backgroundCollisionComponent);
		object.add(dynamicCollision);
		object.add(hitReact);
	
		object.add(vehicleComponent);
		object.add(npcComponent);
		object.add(steeringBehavior);
		addStaticData(GameObjectType.SKELETON, object, spriteComponent);

		object.setCurrentAction(ActionType.MOVE);
		
		
		return object;

	}

	/** Comparator for game objects objects. */
	private final static class ComponentPoolComparator implements
			Comparator<GameComponentPool> {
		public int compare(final GameComponentPool object1,
				final GameComponentPool object2) {
			int result = 0;
			if (object1 == null && object2 != null) {
				result = 1;
			} else if (object1 != null && object2 == null) {
				result = -1;
			} else if (object1 != null && object2 != null) {
				result = object1.objectClass.hashCode()
						- object2.objectClass.hashCode();
			}
			return result;
		}
	}

	public class GameObjectPool extends TObjectPool<GameObject> {

		public GameObjectPool() {
			super();
		}

		public GameObjectPool(int size) {
			super(size);
		}

		@Override
		protected void fill() {
			for (int x = 0; x < getSize(); x++) {
				getAvailable().add(new GameObject());
			}
		}

		@Override
		public void release(Object entry) {
			((GameObject) entry).reset();
			super.release(entry);
		}

	}

	public GameObject spawn(GameObjectType mObjectTypeToSpawn, float x,
			float y, boolean flip) {
		
		return spawn(mObjectTypeToSpawn, x, y);
	}
}
