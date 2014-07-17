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
 package com.replica.utility;

public class RectF{

	public float top_ = 0;
	public float bottom_ = 0;
	public float left_ = 0;
	public float right_ = 0;

	public RectF() {}
	
	public RectF(RectF r) {
		set(r);
	}
	
	public RectF(float x, float y, float width, float height) {
		set(x, y, width, height);
	}
	
	public static boolean intersects(RectF a, RectF b) {
		return a.left_ < b.right_ && b.left_ < a.right_
                && a.top_ > b.bottom_ && b.top_ > a.bottom_;
	}

	public void setEmpty() {
		left_ = right_ = top_ = bottom_ = 0;
	}

	public void set(RectF r) {
		top_ = r.top_;
		bottom_ = r.bottom_;
		left_ = r.left_;
		right_ = r.right_;
	}

	public void set(float x, float y, float width, float height) {
		top_ = y + height;
		bottom_ = y;
		left_ = x;
		right_ = x + width;
	}

	public boolean contains(RectF r) {
		return top_ > bottom_ && left_ < right_
				&& top_ > r.top_ && bottom_ < r.bottom_
				&& left_ < r.left_ && right_ > r.right_;
	}
	
	public boolean intersectsTop(RectF r) {
		return false;
	}
	
	public boolean intersectsBottom(RectF r) {
		return false;
	}
	
	public boolean intersectsLeft(RectF r) {
		return false;
	}
	
	public boolean intersectsRight(RectF r) {
		return false;
	}
	
	public final float width() {
        return right_ - left_;
    }

 
    public final float height() {
        return top_ - bottom_;
    }
    
    public final float centerX() {
        return (left_ + right_) * 0.5f;
    }

    
    public final float centerY() {
        return (bottom_ + top_) * 0.5f;
    }
    
    public void offset(float dx, float dy) {
    	top_ += dy;
		bottom_ += dy ;
		left_ += dx;
		right_ += dx;
    }
    
    public void offsetTo(float newLeft, float newBottom) {
    	float width = right_ - left_;
    	float height = top_ - bottom_;
    	
    	left_ = newLeft;
    	bottom_ = newBottom;
    	right_ = left_ + width;
    	top_ = bottom_ + height;
    }
    
    public void setCenter(float centerX, float centerY) {
    	float width = right_ - left_;
    	float height = top_ - bottom_;
    	
    	left_ = centerX - (width/2);
    	bottom_ = centerY - (height/2);
    	right_ = left_ + width;
    	top_ = bottom_ + height;
    }

    
}
