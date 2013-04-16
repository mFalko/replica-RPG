package com.falko.android.snowball.utility;

import android.graphics.RectF;

public interface HasBounds {
	public RectF getBounds();
	public void setBounds(RectF bounds);
//	public boolean contains(HasBounds item);
//	public boolean intersects(HasBounds item);
}
