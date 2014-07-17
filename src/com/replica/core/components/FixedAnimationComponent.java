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

public class FixedAnimationComponent extends GameComponent {
    private int mAnimationIndex;
    
    public FixedAnimationComponent() {
        super();
        setPhase(ComponentPhases.ANIMATION.ordinal());
        reset();
    }
    
    @Override
    public void reset() {
        mAnimationIndex = 0;
    }
    
    @Override
    public void update(float timeDelta, BaseObject parent) {
        // We look up the sprite component each frame so that this component can be shared.
        GameObject parentObject = (GameObject)parent;
        SpriteComponent sprite = parentObject.findByClass(SpriteComponent.class);
        if (sprite != null) {
            sprite.playAnimation(mAnimationIndex);
        }
    }
    
    public void setAnimation(int index) {
        mAnimationIndex = index;
    }
}
