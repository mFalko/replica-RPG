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

import java.util.ArrayList;

import com.falko.android.snowball.core.collision.HitPointPool;
import com.falko.android.snowball.core.graphics.BufferLibrary;
import com.falko.android.snowball.core.graphics.DrawableFactory;
import com.falko.android.snowball.core.graphics.TextureLibrary;
import com.falko.android.snowball.core.systems.CameraSystem;
import com.falko.android.snowball.core.systems.CollisionSystem;
import com.falko.android.snowball.core.systems.GameObjectCollisionSystem;
import com.falko.android.snowball.core.systems.OpenGLSystem;
import com.falko.android.snowball.core.systems.RenderSystem;
import com.falko.android.snowball.core.systems.SoundSystem;
import com.falko.android.snowball.core.systems.VibrationSystem;
import com.falko.android.snowball.core.zoneloder.Zone;
import com.falko.android.snowball.hud.HudSystem;
import com.falko.android.snowball.input.InputGameInterface;
import com.falko.android.snowball.input.InputSystem;
import com.falko.android.snowball.utility.DebugSystem;
import com.falko.android.snowball.utility.EventRecorder;
import com.falko.android.snowball.utility.TimeSystem;
import com.falko.android.snowball.utility.VectorPool;

/**
 * The object registry manages a collection of global singleton objects.  However, it differs from
 * the standard singleton pattern in a few important ways:
 * - The objects managed by the registry have an undefined lifetime.  They may become invalid at 
 *   any time, and they may not be valid at the beginning of the program.
 * - The only object that is always guaranteed to be valid is the ObjectRegistry itself.
 * - There may be more than one ObjectRegistry, and there may be more than one instance of any of
 *   the systems managed by ObjectRegistry allocated at once.  For example, separate threads may
 *   maintain their own separate ObjectRegistry instances.
 */
public class ObjectRegistry extends BaseObject {

    public BufferLibrary bufferLibrary;
    public CameraSystem cameraSystem;
//    public ChannelSystem channelSystem;
    public CollisionSystem collisionSystem;
    public ContextParameters contextParameters;
//    public CustomToastSystem customToastSystem;
    public DebugSystem debugSystem;
    public DrawableFactory drawableFactory;
    public EventRecorder eventRecorder;
    public GameObjectCollisionSystem gameObjectCollisionSystem;
    public GameObjectFactory gameObjectFactory;
    public GameObjectManager gameObjectManager;
    public HitPointPool hitPointPool;
//    public HotSpotSystem hotSpotSystem;
    public HudSystem hudSystem;
	public InputGameInterface inputGameInterface;
    public InputSystem inputSystem;
//    public LevelBuilder levelBuilder;
//    public LevelSystem levelSystem;
    public OpenGLSystem openGLSystem;
    public SoundSystem soundSystem;
    public TextureLibrary shortTermTextureLibrary;
    public TextureLibrary longTermTextureLibrary;
    public TimeSystem timeSystem;
    public RenderSystem renderSystem;
    public VectorPool vectorPool;
    public VibrationSystem vibrationSystem;
    
    public Zone zone;
    
	
    private ArrayList<BaseObject> mItemsNeedingReset = new ArrayList<BaseObject>();
    
    public ObjectRegistry() {
        super();
    }
    
    public void registerForReset(BaseObject object) {
    	final boolean contained = mItemsNeedingReset.contains(object);
    	assert !contained;
    	if (!contained) {
    		mItemsNeedingReset.add(object);
    	}
    }
    
    @Override
    public void reset() {
    	final int count = mItemsNeedingReset.size();
    	for (int x = 0; x < count; x++) {
    		mItemsNeedingReset.get(x).reset();
    	}
    }
    
}
