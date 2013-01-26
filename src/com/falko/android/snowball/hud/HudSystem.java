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

package com.falko.android.snowball.hud;

import com.falko.android.snowball.core.BaseObject;
import com.falko.android.snowball.core.ContextParameters;
import com.falko.android.snowball.core.GameObjectManager;
import com.falko.android.snowball.core.graphics.DrawableBitmap;
import com.falko.android.snowball.core.graphics.DrawableFactory;
import com.falko.android.snowball.core.graphics.Texture;
import com.falko.android.snowball.core.systems.RenderSystem;
import com.falko.android.snowball.utility.SortConstants;
import com.falko.android.snowball.utility.Vector2D;
import com.falko.android.snowball.utility.VectorPool;

/**
 * A very simple manager for orthographic in-game UI elements. TODO: This should
 * probably manage a number of hud objects in keeping with the component-centric
 * architecture of this engine. The current code is monolithic and should be
 * refactored.
 */
public class HudSystem extends BaseObject {
	

	public HudSystem() {
		super();
		

		reset();
	}

	@Override
	public void reset() {
		
	}

	@Override
	public void update(float timeDelta, BaseObject parent) {
		final RenderSystem render = sSystemRegistry.renderSystem;
		final VectorPool pool = sSystemRegistry.vectorPool;
		final ContextParameters params = sSystemRegistry.contextParameters;
		final DrawableFactory factory = sSystemRegistry.drawableFactory;

		final GameObjectManager manager = sSystemRegistry.gameObjectManager;

		if (manager != null && manager.getPlayer() != null) {
			// Only draw player-specific HUD elements when there's a player.

		}

	}

}
