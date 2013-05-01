package com.replica.utility;

public class RectF {

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
    
}
