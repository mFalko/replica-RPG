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



package com.replica.utility;


/**
 * a quick 'n dirty hash table
 * @author matt
 *
 */
public class QDHashTable<Value> {
	
	static final int DEFAULT_SIZE = 512;
	Value[] valueTable_;
	
	@SuppressWarnings("unchecked")
	public QDHashTable() {
		
		valueTable_ = (Value[])new Object[DEFAULT_SIZE];

        for (int x = 0; x < valueTable_.length; x++) {
        	valueTable_[x] = null;
        }
    }

}
