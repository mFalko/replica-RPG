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

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.os.Build;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.falko.android.snowball.GhostMovementGameComponent;
import com.falko.android.snowball.R;
import com.falko.android.snowball.core.components.RenderComponent;
import com.falko.android.snowball.core.graphics.BufferLibrary;
import com.falko.android.snowball.core.graphics.DrawableFactory;
import com.falko.android.snowball.core.graphics.GLSurfaceView;
import com.falko.android.snowball.core.graphics.GameRenderer;
import com.falko.android.snowball.core.graphics.Texture;
import com.falko.android.snowball.core.graphics.TextureLibrary;
import com.falko.android.snowball.core.systems.CameraSystem;
import com.falko.android.snowball.core.systems.OpenGLSystem;
import com.falko.android.snowball.core.systems.RenderSystem;
import com.falko.android.snowball.core.zoneloder.XMLZoneLoader;
import com.falko.android.snowball.core.zoneloder.Zone;
import com.falko.android.snowball.core.zoneloder.ZoneLoader;
import com.falko.android.snowball.hud.HudSystem;
import com.falko.android.snowball.input.InputGameInterface;
import com.falko.android.snowball.input.InputSystem;
import com.falko.android.snowball.input.MultiTouchFilter;
import com.falko.android.snowball.input.SingleTouchFilter;
import com.falko.android.snowball.input.TouchFilter;
import com.falko.android.snowball.utility.DebugLog;
import com.falko.android.snowball.utility.TimeSystem;
import com.falko.android.snowball.utility.Vector2D;

/**
 * High-level setup object for the AndouKun game engine. This class sets up the
 * core game engine objects and threads. It also passes events to the game
 * thread from the main UI thread.
 */
public class Game extends AllocationGuard {
	private GameThread mGameThread;
	private Thread mGame;
	private ObjectManager mGameRoot;

	private GameRenderer mRenderer;
	private GLSurfaceView mSurfaceView;
	private boolean mRunning;
	private boolean mBootstrapComplete;

	
	private Zone zone_;
	
	private boolean mGLDataLoaded;
	private ContextParameters mContextParameters;
	private TouchFilter mTouchFilter;

	public Game() {
		super();
		mRunning = false;
		mBootstrapComplete = false;
		mGLDataLoaded = false;
		mContextParameters = new ContextParameters();
		
	}

	/**
	 * Creates core game objects and constructs the game engine object graph.
	 * Note that the game does not actually begin running after this function is
	 * called (see start() below). Also note that textures are not loaded from
	 * the resource pack by this function, as OpenGl isn't yet available.
	 * 
	 * @param context
	 */
	public void bootstrap(Context context, int viewWidth, int viewHeight,
			int gameWidth, int gameHeight) {
		if (!mBootstrapComplete) {
			mRenderer = new GameRenderer(context, this, gameWidth, gameHeight);
			// Create core systems
			BaseObject.sSystemRegistry.openGLSystem = new OpenGLSystem(null);

			// BaseObject.sSystemRegistry.customToastSystem = new
			// CustomToastSystem(context);

			ContextParameters params = mContextParameters;
			params.viewWidth = viewWidth;
			params.viewHeight = viewHeight;
			params.gameWidth = gameWidth;
			params.gameHeight = gameHeight;
			params.viewScaleX = (float) viewWidth / gameWidth;
			params.viewScaleY = (float) viewHeight / gameHeight;
			params.context = context;
			BaseObject.sSystemRegistry.contextParameters = params;

			final int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
			if (sdkVersion < Build.VERSION_CODES.ECLAIR) {
				mTouchFilter = new SingleTouchFilter();
			} else {
				mTouchFilter = new MultiTouchFilter();
			}

			// Short-term textures are cleared between levels.
			TextureLibrary shortTermTextureLibrary = new TextureLibrary();
			BaseObject.sSystemRegistry.shortTermTextureLibrary = shortTermTextureLibrary;

			// Long-term textures persist between levels.
			TextureLibrary longTermTextureLibrary = new TextureLibrary();
			BaseObject.sSystemRegistry.longTermTextureLibrary = longTermTextureLibrary;

			// The buffer library manages hardware VBOs.
			BaseObject.sSystemRegistry.bufferLibrary = new BufferLibrary();

//			BaseObject.sSystemRegistry.soundSystem = new SoundSystem();

			// The root of the game graph.
			MainLoop gameRoot = new MainLoop();

			InputSystem input = new InputSystem();
			BaseObject.sSystemRegistry.inputSystem = input;
			BaseObject.sSystemRegistry.registerForReset(input);

		
			
//			WindowManager windowMgr = (WindowManager) context
//					.getSystemService(Context.WINDOW_SERVICE);
//			int rotationIndex = windowMgr.getDefaultDisplay().getOrientation();
//			input.setScreenRotation(rotationIndex);

			InputGameInterface inputInterface = new InputGameInterface();
			gameRoot.add(inputInterface);
			BaseObject.sSystemRegistry.inputGameInterface = inputInterface;

			// LevelSystem level = new LevelSystem();
			// BaseObject.sSystemRegistry.levelSystem = level;

			// CollisionSystem collision = new CollisionSystem();
			// BaseObject.sSystemRegistry.collisionSystem = collision;
			// BaseObject.sSystemRegistry.hitPointPool = new HitPointPool();

			GameObjectManager gameManager = new GameObjectManager(
					params.viewWidth * 2);
			BaseObject.sSystemRegistry.gameObjectManager = gameManager;

//			GameObjectFactory objectFactory = new GameObjectFactory();
//			BaseObject.sSystemRegistry.gameObjectFactory = objectFactory;

			// BaseObject.sSystemRegistry.hotSpotSystem = new HotSpotSystem();
			//
			// BaseObject.sSystemRegistry.levelBuilder = new LevelBuilder();
			//
			// BaseObject.sSystemRegistry.channelSystem = new ChannelSystem();
			// BaseObject.sSystemRegistry.registerForReset(BaseObject.sSystemRegistry.channelSystem);

			CameraSystem camera = new CameraSystem();

			BaseObject.sSystemRegistry.cameraSystem = camera;
			BaseObject.sSystemRegistry.registerForReset(camera);

			// collision.loadCollisionTiles(context.getResources().openRawResource(R.raw.collision));

			gameRoot.add(gameManager);

			// Camera must come after the game manager so that the camera target
			// moves before the camera
			// centers.

			gameRoot.add(camera);
			
			

			// More basic systems.

			// GameObjectCollisionSystem dynamicCollision = new
			// GameObjectCollisionSystem();
			// gameRoot.add(dynamicCollision);
			// BaseObject.sSystemRegistry.gameObjectCollisionSystem =
			// dynamicCollision;

			RenderSystem renderer = new RenderSystem();
			BaseObject.sSystemRegistry.renderSystem = renderer;
//			BaseObject.sSystemRegistry.vectorPool = new VectorPool();
			BaseObject.sSystemRegistry.drawableFactory = new DrawableFactory();

			// hud system
			HudSystem hud = new HudSystem();
			
			BaseObject.sSystemRegistry.shortTermTextureLibrary.allocateTexture(R.drawable.dpad_inactive_up);
			BaseObject.sSystemRegistry.shortTermTextureLibrary.allocateTexture(R.drawable.dpad_inactive_down);
			BaseObject.sSystemRegistry.shortTermTextureLibrary.allocateTexture(R.drawable.dpad_inactive_left);
			BaseObject.sSystemRegistry.shortTermTextureLibrary.allocateTexture(R.drawable.dpad_inactive_right);
			BaseObject.sSystemRegistry.shortTermTextureLibrary.allocateTexture(R.drawable.dpad_center);
			BaseObject.sSystemRegistry.hudSystem = hud;
			gameRoot.add(hud);
			
			
//			BaseObject.sSystemRegistry.vibrationSystem = new VibrationSystem();

//			EventRecorder eventRecorder = new EventRecorder();
//			BaseObject.sSystemRegistry.eventRecorder = eventRecorder;
//			BaseObject.sSystemRegistry.registerForReset(eventRecorder);

			// gameRoot.add(collision);

			// debug systems
			// BaseObject.sSystemRegistry.debugSystem = new
			// DebugSystem(longTermTextureLibrary);
			// dynamicCollision.setDebugPrefs(false, true);

//			objectFactory.preloadEffects();
			
			
			
			
			Texture texture = BaseObject.sSystemRegistry.shortTermTextureLibrary.allocateTexture(R.drawable.debug_circle_red);
			texture.width = 32;
			texture.height = 32;
			GameObject ghost = new GameObject();
			ghost.width = 32;
			ghost.height = 32;
			RenderComponent ghostRenCom = new RenderComponent();
			ghostRenCom.setCameraRelative(true);
			ghostRenCom.setPriority(3);
			GhostMovementGameComponent gm  = new GhostMovementGameComponent();
			gm.setRenderComponent(ghostRenCom);
			ghost.add(gm);
			ghost.add(ghostRenCom);
			gameRoot.add(ghost);
			camera.setTarget(ghost);
			
			
			
			try {
				InputStream in = context.getAssets().open("Skeleton.tmx");
				ZoneLoader loader = new XMLZoneLoader();
				zone_ = loader.loadZone(in, context);
			} catch (IOException e) {
				
			}
			gameRoot.add(zone_);
			BaseObject.sSystemRegistry.zone = zone_;
			
			
			
			
			
			mGameRoot = gameRoot;

			 mGameThread = new GameThread(mRenderer);
			 mGameThread.setGameRoot(mGameRoot);

			// mCurrentLevel = null;

			mBootstrapComplete = true;
		}
	}

	protected synchronized void stopLevel() {
		stop();
		GameObjectManager manager = BaseObject.sSystemRegistry.gameObjectManager;
		manager.destroyAll();
		manager.commitUpdates();

		// TODO: it's not strictly necessary to clear the static data here, but
		// if I don't do it
		// then two things happen: first, the static data will refer to junk
		// Texture objects, and
		// second, memory that may not be needed for the next level will hang
		// around. One solution
		// would be to break up the texture library into static and non-static
		// things, and
		// then selectively clear static game components based on their
		// usefulness next level,
		// but this is way simpler.
		GameObjectFactory factory = BaseObject.sSystemRegistry.gameObjectFactory;
//		 factory.clearStaticData();
		// factory.sanityCheckPools();

		// Reset the level
		// BaseObject.sSystemRegistry.levelSystem.reset();

		// Ensure sounds have stopped.
		BaseObject.sSystemRegistry.soundSystem.stopAll();

		// Reset systems that need it.
		BaseObject.sSystemRegistry.reset();

		// Dump the short-term texture objects only.
		// mSurfaceView.flushTextures(BaseObject.sSystemRegistry.shortTermTextureLibrary);
		BaseObject.sSystemRegistry.shortTermTextureLibrary.removeAll();
		// mSurfaceView.flushBuffers(BaseObject.sSystemRegistry.bufferLibrary);
		BaseObject.sSystemRegistry.bufferLibrary.removeAll();
	}

	public synchronized void requestNewLevel() {
		// tell the Renderer to call us back when the
		// render thread is ready to manage some texture memory.
		// mRenderer.requestCallback();
	}

	public synchronized void restartLevel() {
		DebugLog.d("AndouKun", "Restarting...");
		// final LevelTree.Level level = mCurrentLevel;
		stop();

		// Destroy all game objects and respawn them. No need to destroy other
		// systems.
		GameObjectManager manager = BaseObject.sSystemRegistry.gameObjectManager;
		manager.destroyAll();
		manager.commitUpdates();

		// Ensure sounds have stopped.
		BaseObject.sSystemRegistry.soundSystem.stopAll();

		// Reset systems that need it.
		BaseObject.sSystemRegistry.reset();

		// LevelSystem levelSystem = BaseObject.sSystemRegistry.levelSystem;
		// levelSystem.incrementAttemptsCount();
		// levelSystem.spawnObjects();
		//

		// mCurrentLevel = level;
		// mPendingLevel = null;
		start();
	}

	protected synchronized void goToLevel() {// LevelTree.Level level

		ContextParameters params = BaseObject.sSystemRegistry.contextParameters;
		// BaseObject.sSystemRegistry.levelSystem.loadLevel(level,
		// params.context.getResources().openRawResource(level.resource),
		// mGameRoot);

		Context context = params.context;
		// mRenderer.setContext(context);
		// mSurfaceView.loadTextures(BaseObject.sSystemRegistry.longTermTextureLibrary);
		// mSurfaceView.loadTextures(BaseObject.sSystemRegistry.shortTermTextureLibrary);
		// mSurfaceView.loadBuffers(BaseObject.sSystemRegistry.bufferLibrary);

		mGLDataLoaded = true;

		// mCurrentLevel = level;
		// mPendingLevel = null;

		TimeSystem time = BaseObject.sSystemRegistry.timeSystem;
		time.reset();

		

		// CustomToastSystem toast =
		// BaseObject.sSystemRegistry.customToastSystem;
		// if (toast != null) {
		// if (level.inThePast) {
		// toast.toast(context.getString(R.string.memory_playback_start),
		// Toast.LENGTH_LONG);
		// } else {
		// if (mLastLevel != null && mLastLevel.inThePast) {
		// toast.toast(context.getString(R.string.memory_playback_complete),
		// Toast.LENGTH_LONG);
		// }
		// }
		// }

		// mLastLevel = level;

		start();
	}

	/** Starts the game running. */
	public void start() {
		if (!mRunning) {
			assert mGame == null;
			// Now's a good time to run the GC.
			Runtime r = Runtime.getRuntime();
			r.gc();
			DebugLog.d("AndouKun", "Start!");
			mGame = new Thread(mGameThread);
			mGame.setName("Game");
			mGame.start();
			mRunning = true;
			AllocationGuard.sGuardActive = false;
		} else {
			mGameThread.resumeGame();
		}
	}

	public void stop() {
		if (mRunning) {
			DebugLog.d("AndouKun", "Stop!");
			if (mGameThread.getPaused()) {
				mGameThread.resumeGame();
			}
			mGameThread.stopGame();
			try {
				mGame.join();
			} catch (InterruptedException e) {
				mGame.interrupt();
			}
			mGame = null;
			mRunning = false;
			// mCurrentLevel = null;
			AllocationGuard.sGuardActive = false;
		}
	}

	public boolean onTrackballEvent(MotionEvent event) {
		if (mRunning) {
			if (event.getAction() == MotionEvent.ACTION_MOVE) {
				BaseObject.sSystemRegistry.inputSystem.roll(event.getRawX(),
						event.getRawY());
			} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
				onKeyDownEvent(KeyEvent.KEYCODE_DPAD_CENTER);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				onKeyUpEvent(KeyEvent.KEYCODE_DPAD_CENTER);
			}
		}
		return true;
	}

	public boolean onOrientationEvent(float x, float y, float z) {
		if (mRunning) {
			BaseObject.sSystemRegistry.inputSystem.setOrientation(x, y, z);
		}
		return true;
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (mRunning) {
			mTouchFilter.updateTouch(event);
//			Log.v("SnowBall", "On touch Event");
		}
		return true;
	}

	public boolean onKeyDownEvent(int keyCode) {
		boolean result = false;
		if (mRunning) {
			BaseObject.sSystemRegistry.inputSystem.keyDown(keyCode);
		}
		return result;
	}

	public boolean onKeyUpEvent(int keyCode) {
		boolean result = false;
		if (mRunning) {
			BaseObject.sSystemRegistry.inputSystem.keyUp(keyCode);
		}
		return result;
	}

	public GameRenderer getRenderer() {
		return mRenderer;
	}

	public void onPause() {
		if (mRunning) {
			mGameThread.pauseGame();
		}
	}

	public void onResume(Context context, boolean force) {
		if (force && mRunning) {
			mGameThread.resumeGame();
		} else {
			mRenderer.setContext(context);
			// Don't explicitly resume the game here. We'll do that in
			// the SurfaceReady() callback, which will prevent the game
			// starting before the render thread is ready to go.
			BaseObject.sSystemRegistry.contextParameters.context = context;
		}
	}

	public void onSurfaceReady() {
		DebugLog.d("AndouKun", "Surface Ready");

		// if (mPendingLevel != null && mPendingLevel != mCurrentLevel) {
		// if (mRunning) {
		// stopLevel();
		// }
		// goToLevel(mPendingLevel);
		// } else if (mGameThread.getPaused() && mRunning) {
		// mGameThread.resumeGame();
		// }
	}

	public void setSurfaceView(GLSurfaceView view) {
		mSurfaceView = view;
	}

	public void onSurfaceLost() {
		DebugLog.d("AndouKun", "Surface Lost");

		BaseObject.sSystemRegistry.shortTermTextureLibrary.invalidateAll();
		BaseObject.sSystemRegistry.longTermTextureLibrary.invalidateAll();
		BaseObject.sSystemRegistry.bufferLibrary.invalidateHardwareBuffers();

		mGLDataLoaded = false;
	}

	public void onSurfaceCreated(GL10 gl, Context context) {
		DebugLog.d("AndouKun", "Surface Created");

		// TODO: this is dumb. SurfaceView doesn't need to control everything
		// here.
		// GL should just be passed to this function and then set up directly.

		// if (!mGLDataLoaded && mGameThread.getPaused() && mRunning &&
		// mPendingLevel == null) {
		//
		// mSurfaceView.loadTextures(BaseObject.sSystemRegistry.longTermTextureLibrary);
		// mSurfaceView.loadTextures(BaseObject.sSystemRegistry.shortTermTextureLibrary);
		// mSurfaceView.loadBuffers(BaseObject.sSystemRegistry.bufferLibrary);
		// mGLDataLoaded = true;
		// }
		
		BaseObject.sSystemRegistry.shortTermTextureLibrary.invalidateAll();
		BaseObject.sSystemRegistry.shortTermTextureLibrary.loadAll(context, gl);
		BaseObject.sSystemRegistry.longTermTextureLibrary.invalidateAll();
		BaseObject.sSystemRegistry.longTermTextureLibrary.loadAll(context, gl);
	}

	// public void setPendingLevel(LevelTree.Level level) {
	// // mPendingLevel = level;
	// }

	public void setSoundEnabled(boolean soundEnabled) {
		BaseObject.sSystemRegistry.soundSystem.setSoundEnabled(soundEnabled);
	}

	public void setControlOptions(boolean clickAttack, boolean tiltControls,
			int tiltSensitivity, int movementSensitivity,
			boolean onScreenControls) {

	
		BaseObject.sSystemRegistry.inputGameInterface
				.setUseOnScreenControls(onScreenControls);
	}

	public void setSafeMode(boolean safe) {
		// mSurfaceView.setSafeMode(safe);
	}

	public float getGameTime() {
		return BaseObject.sSystemRegistry.timeSystem.getGameTime();
	}

	public Vector2D getLastDeathPosition() {
		return BaseObject.sSystemRegistry.eventRecorder.getLastDeathPosition();
	}

	public void setLastEnding(int ending) {
		BaseObject.sSystemRegistry.eventRecorder.setLastEnding(ending);
	}

	public int getLastEnding() {
		return BaseObject.sSystemRegistry.eventRecorder.getLastEnding();
	}

	public boolean isPaused() {
		return (mRunning && mGameThread != null && mGameThread.getPaused());
	}



	public int getRobotsDestroyed() {
		return BaseObject.sSystemRegistry.eventRecorder.getRobotsDestroyed();
	}

	public int getPearlsCollected() {
		return BaseObject.sSystemRegistry.eventRecorder.getPearlsCollected();
	}

	public int getPearlsTotal() {
		return BaseObject.sSystemRegistry.eventRecorder.getPearlsTotal();
	}
}
