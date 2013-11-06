package com.replica.core.collision;


import com.replica.core.AllocationGuard;
import com.replica.core.GameObject;
import com.replica.utility.HasBounds;
import com.replica.utility.RectF;
import com.replica.utility.Vector2;


  /**
     * A class describing a single surface in the collision world.  Surfaces are stored as a line
     * segment and a normal. The normal must be normalized (its length must be 1.0) and should 
     * describe the direction that the segment "pushes against" in a collision.
     */
    public class LineSegment extends AllocationGuard implements HasBounds{
    	private short slope;
        private RectF bounds;
        public GameObject owner;
        
        
        public LineSegment() {
            super();
            bounds = new RectF();

        }
        
        public LineSegment(int startx, int starty, int endx, int endy) {
        	this();

        	updateBounds(startx, starty, endx, endy);
        }
        
        /* Sets up the line segment.  Values are copied to local storage. */
        public void set(Vector2 start, Vector2 end) {

            updateBounds(start.x, start.y, end.x, end.y);
        }
        
        public void setOwner(GameObject ownerObject) {
            owner = ownerObject;
        }
        
        public RectF getBounds() {
			return bounds;
		}

		public void setBounds(RectF bounds) {
			//Not used
		}
		
		private void updateBounds(float startx, float starty, float endx, float endy) {			
			bounds.left_ = startx;
			bounds.right_ = endx;
			
			if (starty < endy) {
				slope = 1;
				bounds.bottom_ = starty;
				bounds.top_ = endy;
			} else {
				slope = -1;
				bounds.top_ = starty;
				bounds.bottom_ = endy;
			}
		}
        
        /**
         * Checks to see if these lines intersect by projecting one onto the other and then
         * assuring that the collision point is within the range of each segment.
         */
        public boolean calculateIntersection(Vector2 otherStart, Vector2 otherEnd,
                Vector2 hitPoint) {
            boolean intersecting = false;
            
            // Reference: http://local.wasp.uwa.edu.au/~pbourke/geometry/lineline2d/
            final float x1 = bounds.left_;
            final float x2 = bounds.right_;
            final float x3 = otherStart.x;
            final float x4 = otherEnd.x;
            final float y1;
            final float y2;
            if (slope > 0) {
            	y1 = bounds.bottom_;
            	y2 = bounds.top_;
            } else {
            	y1 = bounds.top_;
            	y2 = bounds.bottom_;
            }
            
            final float y3 = otherStart.y;
            final float y4 = otherEnd.y;
            
            final float denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
            if (denom != 0) {
             final float uA = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / denom;
             final float uB = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / denom; 
             
             if (uA >= 0.0f && uA <= 1.0f && uB >= 0.0f && uB <= 1.0f) {
                 final float hitX = x1 + (uA * (x2 - x1));
                 final float hitY = y1 + (uA * (y2 - y1));
                 hitPoint.set(hitX, hitY);
                 intersecting = true;
             }
            }
            return intersecting;
        }
        
        // Based on http://www.garagegames.com/community/resources/view/309
        public boolean calculateIntersectionBox(float left, float right, float top, float bottom, 
                Vector2 hitPoint) {
            
        	final float x1 = bounds.left_;
            final float x2 = bounds.right_;
            final float y1;
            final float y2;
            if (slope > 0) {
            	y1 = bounds.bottom_;
            	y2 = bounds.top_;
            } else {
            	y1 = bounds.top_;
            	y2 = bounds.bottom_;
            }
            
            float startIntersect;
            float endIntersect;
            float intersectTimeStart = 0.0f;
            float intersectTimeEnd = 1.0f;
            
            if (x1 < x2) {
                if (x1 > right || x2 < left) {
                    return false;
                }
                final float deltaX = x2 - x1;
                startIntersect = (x1 < left) ? (left - x1) / deltaX : 0.0f;
                endIntersect = (x2 > right) ? (right - x1) / deltaX : 1.0f;
            } else {
                if (x2 > right || x1 < left) {
                    return false;
                }
                final float deltaX = x2 - x1;
                startIntersect = (x1 > right) ? (right - x1) / deltaX : 0.0f;
                endIntersect = (x2 < left) ? (left - x1) / deltaX : 1.0f;
            }
            
            if (startIntersect > intersectTimeStart) {
                intersectTimeStart = startIntersect;
            }
            if (endIntersect < intersectTimeEnd) {
                intersectTimeEnd = endIntersect;
            }
            if (intersectTimeEnd < intersectTimeStart) {
                return false;
            }
            
            // y
            if (y1 < y2) {
                if (y1 > top || y2 < bottom) {
                    return false;
                }
                final float deltaY = y2 - y1;
                startIntersect = (y1 < bottom) ? (bottom - y1) / deltaY : 0.0f;
                endIntersect = (y2 > top) ? (top - y1) / deltaY : 1.0f;
            } else {
                if (y2 > top || y1 < bottom) {
                    return false;
                }
                final float deltaY = y2 - y1;
                startIntersect = (y1 > top) ? (top - y1) / deltaY : 0.0f;
                endIntersect = (y2 < bottom) ? (bottom - y1) / deltaY : 1.0f;
            }
            
            if (startIntersect > intersectTimeStart) {
                intersectTimeStart = startIntersect;
            }
            if (endIntersect < intersectTimeEnd) {
                intersectTimeEnd = endIntersect;
            }
            if (intersectTimeEnd < intersectTimeStart) {
                return false;
            }
            
            //TODO: fix the hit point  maybe fixed
         
            hitPoint.set(x2,y2);
            hitPoint.subtract(x1,y1);
            hitPoint.multiply(intersectTimeStart);
            hitPoint.add(x1,y1);
            
            return true;
        }        
    }