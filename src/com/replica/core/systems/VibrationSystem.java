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
 
package com.replica.core.systems;

import android.content.Context;
import android.os.Vibrator;

import com.replica.core.BaseObject;
import com.replica.core.ContextParameters;

/** A system for accessing the Android vibrator.  Note that this system requires the app's 
 * AndroidManifest.xml to contain permissions for the Vibrator service.
 */
public class VibrationSystem extends BaseObject {

    public VibrationSystem() {
        super();
    }
    
    @Override
    public void reset() {
    }
    
    public void vibrate(float seconds) {
        ContextParameters params = sSystemRegistry.contextParameters;
        if (params != null && params.context != null) {
            Vibrator vibrator = (Vibrator)params.context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate((int)(seconds * 1000));
            }
        }
    }
}
