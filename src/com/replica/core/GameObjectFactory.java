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

package com.falko.android.snowball.core;

import java.util.Comparator;

import android.util.Log;

import com.falko.android.snowball.GhostMovementGameComponent;
import com.falko.android.snowball.R;
import com.falko.android.snowball.core.GameObject.ActionType;
import com.falko.android.snowball.core.components.AnimationComponent;
import com.falko.android.snowball.core.components.AnimationComponent.PlayerAnimations;
import com.falko.android.snowball.core.components.GameComponent;
import com.falko.android.snowball.core.components.GameComponentPool;
import com.falko.android.snowball.core.components.MotionBlurComponent;
import com.falko.android.snowball.core.components.RenderComponent;
import com.falko.android.snowball.core.components.SpriteComponent;
import com.falko.android.snowball.core.graphics.AnimationFrame;
import com.falko.android.snowball.core.graphics.SpriteAnimation;
import com.falko.android.snowball.core.graphics.Texture;
import com.falko.android.snowball.core.graphics.TextureLibrary;
import com.falko.android.snowball.utility.FixedSizeArray;
import com.falko.android.snowball.utility.TObjectPool;

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

		// Collectables

		// Characters

		// AI

		// Objects

		// Effects

		// Special Spawnable

		// Projectiles

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

		//
		// mComponentPools = new
		// FixedSizeArray<GameComponentPool>(componentTypes.length,
		// sComponentPoolComparator);
		// for (int x = 0; x < componentTypes.length; x++) {
		// ComponentClass component = componentTypes[x];
		// mComponentPools.add(new GameComponentPool(component.type,
		// component.poolSize));
		// }
		// mComponentPools.sort(true);

		mPoolSearchDummy = new GameComponentPool(Object.class, 1);

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

	protected void releaseComponent(GameComponent component) {
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

	public GameObject spawn(GameObjectType type, float x, float y,
			boolean horzFlip) {
		GameObject newObject = null;
		switch (type) {

		}

		return newObject;
	}

	public GameObject spawnPlayer(float positionX, float positionY) {
		GameObject object = new GameObject();
		object.getPosition().set(positionX, positionY);
		object.activationRadius = mAlwaysActive;
		object.width = 50;
		object.height = 50;

		AnimationComponent objectAnimationComponent = new AnimationComponent();
		SpriteComponent objectSpriteComponent = new SpriteComponent();
		RenderComponent objectRenCom = new RenderComponent();
		GhostMovementGameComponent gm = new GhostMovementGameComponent();
//		MotionBlurComponent mbc = new MotionBlurComponent();

		SpriteAnimation moveNorth = loadAnimation(
				R.drawable.body_skeleton_walkcycle_north,
				PlayerAnimations.MOVE_NORTH.ordinal(), 8, 64, 64, 64, true);
		SpriteAnimation moveSouth = loadAnimation(
				R.drawable.body_skeleton_walkcycle_south,
				PlayerAnimations.MOVE_SOUTH.ordinal(), 8, 64, 64, 64, true);
		SpriteAnimation moveWest = loadAnimation(
				R.drawable.body_skeleton_walkcycle_west,
				PlayerAnimations.MOVE_WEST.ordinal(), 8, 64, 64, 64, true);
		SpriteAnimation moveEast = loadAnimation(
				R.drawable.body_skeleton_walkcycle_east,
				PlayerAnimations.MOVE_EAST.ordinal(), 8, 64, 64, 64, true);
		
		SpriteAnimation idleNorth = loadAnimation(
				R.drawable.body_skeleton_walkcycle_north,
				PlayerAnimations.IDLE_NORTH.ordinal(), 1, 0, 64, 64, true);
		SpriteAnimation idleSouth = loadAnimation(
				R.drawable.body_skeleton_walkcycle_south,
				PlayerAnimations.IDLE_SOUTH.ordinal(), 1, 0, 64, 64, true);
		SpriteAnimation idleWest = loadAnimation(
				R.drawable.body_skeleton_walkcycle_west,
				PlayerAnimations.IDLE_WEST.ordinal(), 1, 0, 64, 64, true);
		SpriteAnimation idleEast = loadAnimation(
				R.drawable.body_skeleton_walkcycle_east,
				PlayerAnimations.IDLE_EAST.ordinal(), 1, 0, 64, 64, true);
		

		objectSpriteComponent.addAnimation(moveNorth);
		objectSpriteComponent.addAnimation(moveSouth);
		objectSpriteComponent.addAnimation(moveWest);
		objectSpriteComponent.addAnimation(moveEast);
		objectSpriteComponent.addAnimation(idleNorth);
		objectSpriteComponent.addAnimation(idleSouth);
		objectSpriteComponent.addAnimation(idleWest);
		objectSpriteComponent.addAnimation(idleEast);
		

		objectSpriteComponent.setSize((int) object.width, (int) object.height);
		objectSpriteComponent.setRenderComponent(objectRenCom);

		objectAnimationComponent.setSprite(objectSpriteComponent);

		objectRenCom.setCameraRelative(true);
		objectRenCom.setPriority(3);
		
//		mbc.setTarget(objectRenCom);

		object.add(objectAnimationComponent);
		object.add(objectSpriteComponent);
//		object.add(mbc);
		object.add(gm);
		object.add(objectRenCom);
		
		object.setCurrentAction(ActionType.IDLE);
		return object;

	}

	private SpriteAnimation loadAnimation(int rID, int animationID,
			int frames, int offset, int frameHeight, int frameWidth,
			boolean loop) {

		SpriteAnimation animation = new SpriteAnimation(animationID, frames);

		Texture tex = BaseObject.sSystemRegistry.shortTermTextureLibrary
				.allocateTexture(rID);

		Log.v("SnowBAll", "RID " + tex.resource);

		for (int i = 0; i < frames; ++i) {

			int[] crop = new int[4];
			crop[0] = offset + frameWidth * i;
			crop[1] = frameHeight;
			crop[2] = frameWidth;
			crop[3] = frameHeight;

			AnimationFrame frame = new AnimationFrame(tex, 0.1f);
			frame.mCrop = crop;
			animation.addFrame(frame);
		}
		animation.setLoop(true);
		return animation;
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
}