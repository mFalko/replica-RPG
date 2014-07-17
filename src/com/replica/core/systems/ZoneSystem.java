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
 

package com.replica.core.systems;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

import com.replica.core.BaseObject;
import com.replica.core.GameObject;
import com.replica.core.components.RenderComponent;
import com.replica.core.components.ScrollerComponent;
import com.replica.core.graphics.Texture;
import com.replica.core.graphics.TiledVertexGrid;
import com.replica.core.graphics.VertexGrid;
import com.replica.core.zoneloder.Zone;
import com.replica.core.zoneloder.Zone.DrawLayerInfo;
import com.replica.core.zoneloder.ZoneLoader;
import com.replica.utility.DebugLog;
import com.replica.utility.RectF;
import com.replica.utility.Vector2;

/*
 * 
 */

public class ZoneSystem extends BaseObject{

	private ZoneLoader zoneLoader_;
	private Zone currentZone;
	private ZoneLoadingThread remapThread;
	private boolean pendingRemap;
	private int layeredGridTileWidth;
	private int layeredGridTileHeight;
	private int tilePixelHeight = 32;
	private int tilePixelWidth = 32;   //FIXME: epic hack
	
	private RectF loadBounds;
	private RectF cameraBounds;
	private LayeredGrid[] layeredGrids;
	private float[][] UVWorkspace;
	private static final int GRID_COUNT = 2;
	private static final int ACTIVE_GRID = 0;
	private static final int BUFFERED_GRID = 1;
	
	/**
	 * 
	 * @param viewWidth
	 * @param viewHeight
	 */
	public ZoneSystem(int viewWidth, int viewHeight) {
		
		//TODO: remove magic numbers
		layeredGridTileWidth = (viewWidth/32) * 2;
		layeredGridTileHeight = (viewHeight/32) * 3;
		
		loadBounds = new RectF(0, 0, (int)(viewWidth *1.5f), (int)(viewHeight*2.5f));
		cameraBounds = new RectF(0, 0, viewWidth, viewHeight);
		
		layeredGrids = new LayeredGrid[GRID_COUNT];
		for (int i = 0; i < GRID_COUNT; ++i) {
			layeredGrids[i] = new LayeredGrid(viewWidth, viewHeight);
		}
		
		UVWorkspace = new float[4][2];
		pendingRemap = false;
		remapThread = new ZoneLoadingThread();
		remapThread.start();
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		remapThread.shutdown();
		try {
			remapThread.join();
		} catch (InterruptedException e) {
			remapThread.interrupt();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.replica.core.BaseObject#reset()
	 */
	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * @see com.replica.core.BaseObject#update(float, com.replica.core.BaseObject)
	 */
	@Override
	public void update(float timeDelta, BaseObject parent) {
		
		
		
		if (pendingRemap) {
			
			if (remapThread.loadingComplete()) {
				LayeredGrid grid = 	layeredGrids[BUFFERED_GRID];
				layeredGrids[BUFFERED_GRID] = layeredGrids[ACTIVE_GRID];
				layeredGrids[ACTIVE_GRID] = grid;
				pendingRemap = false;
			}
			
		} else { 
			CameraSystem camera = BaseObject.sSystemRegistry.cameraSystem;
			cameraBounds.setCenter(camera.getFocusPositionX(), camera.getFocusPositionY());
			
			if (!loadBounds.contains(cameraBounds)) {

				LayeredGrid grid = layeredGrids[BUFFERED_GRID];
				centerGridOnPlayer(grid);
				remapThread.postGridToLoad(grid);
				pendingRemap = true;
			}
		}
		
		// submit current grid to draw
		layeredGrids[ACTIVE_GRID].gameObject.update(timeDelta, parent);
	}
	
	/**
	 * 
	 * @param grid
	 */
	private void remapGrid(LayeredGrid grid) {
		final int layerCount = grid.layerCount;
		final int worldTileWidth = currentZone.getWorldTileWidth();
		final int worldTileHeight = currentZone.getWorldTileHeight();
		final int startTileX = (int) Math.floor(grid.gameObject.getPosition().x / tilePixelWidth);
		final int startTileY = (int) Math.floor(grid.gameObject.getPosition().y / tilePixelHeight);
		final DrawLayerInfo[] drawInfo = currentZone.getDrawLayerInfo();

		for (int tileY = 0; tileY < layeredGridTileHeight; tileY++) {
			for (int tileX = 0; tileX < layeredGridTileWidth; tileX++) {
				for (int i = 0; i < layerCount; ++i) {

					final int worldTileX = startTileX + tileX;
					final int worldTileY = startTileY + tileY;

					if (worldTileX < worldTileWidth && worldTileY < worldTileHeight) {
						
						GIDToUV(drawInfo[i].mapDataUVs_[worldTileY][worldTileX], UVWorkspace);
						grid.grids[i].setUV(tileX, tileY, UVWorkspace);
					}
				}
			}
		}
	}
	
	
	/**
	 * 
	 * @param GID
	 * @param uv
	 */
	private void GIDToUV(long GID, float[][] uv) {
		
		final float GL_MAGIC_OFFSET = 0.5f;
		final long FLIPPED_HORIZONTALLY_FLAG = 0x80000000;
		final long FLIPPED_VERTICALLY_FLAG = 0x40000000;
		final long FLIPPED_DIAGONALLY_FLAG = 0x20000000;
		final int FIRST_GID = 1;
		
		long id = GID & ~(FLIPPED_HORIZONTALLY_FLAG | FLIPPED_VERTICALLY_FLAG | FLIPPED_DIAGONALLY_FLAG);
		
		final int tileWidth = currentZone.getTilePixelWidth();
		final int tileHeight = currentZone.getTilePixelHeight();
		final int pixelWidth = currentZone.getTexture().width;
		final int pixelHeight = currentZone.getTexture().height;
		final int width = pixelWidth / tileWidth;

		//FIXME: allow tile rotations!!! This is important
//		final boolean flippedHorizontally = (GID & FLIPPED_HORIZONTALLY_FLAG) > 1;
//		final boolean flippedVertically = (GID & FLIPPED_VERTICALLY_FLAG) > 1;
//		final boolean flippedDiagonally = (GID & FLIPPED_DIAGONALLY_FLAG) > 1;

		long gid = id - FIRST_GID;// zero based
		if (gid < 0) gid = 0;
		final float y = (gid / width) * tileHeight;
		final float x = (gid % width) * tileWidth;

		float[] uv0 = { (x + GL_MAGIC_OFFSET) / pixelWidth ,
						(y + GL_MAGIC_OFFSET) / pixelHeight};
		
		float[] uv1 = { (x + GL_MAGIC_OFFSET) / pixelWidth, 
						(y + tileHeight - GL_MAGIC_OFFSET) / pixelHeight};
		
		float[] uv2 = { (x + tileWidth - GL_MAGIC_OFFSET) / pixelWidth,
						(y + tileHeight - GL_MAGIC_OFFSET) / pixelHeight };
		
		float[] uv3 = { (x + tileWidth - GL_MAGIC_OFFSET) / pixelWidth,
						(y + GL_MAGIC_OFFSET) / pixelHeight };

		uv[0] = uv0;
		uv[1] = uv1;
		uv[2] = uv2;
		uv[3] = uv3;
	}
	
	/**
	 * 
	 * @param level
	 * @return
	 */

	public boolean load(String level) {
		Context c = BaseObject.sSystemRegistry.contextParameters.context;
		try {
			InputStream in = c.getAssets().open(level);
			currentZone = zoneLoader_.loadZone(in, c);
			in.close();
		} catch (IOException e) {
			DebugLog.v("SnowBall", "IOException during zoneload");
			DebugLog.v("SnowBall", e.toString());
			return false;
		}
		
		if (currentZone == null) {
			DebugLog.v("SnowBall", "NULL Zone");
			return false;
		}
		
		BaseObject.sSystemRegistry.zone = currentZone;
		
		//set texture to load on the GL thread //FIXME: hack
		Texture oldTex = currentZone.getTexture();
		Texture temp = BaseObject.sSystemRegistry.shortTermTextureLibrary.allocateTexture(oldTex.resource);
		temp.height = oldTex.height;
		temp.width = oldTex.width;
		currentZone.setTexture(temp);
		
		//load all collision
		CollisionSystem collision = BaseObject.sSystemRegistry.collisionSystem;
		collision.initialize(currentZone.getCollisionLines(),
				currentZone.getWorldWidth(), currentZone.getWorldHeight());
		
		//load all game objects
		//TODO: load game objects
		
		//set starting tile positions and UVs
		tilePixelHeight = currentZone.getTilePixelHeight();
		tilePixelWidth = currentZone.getTilePixelWidth();
		
		for (int i = 0; i < layeredGrids.length; ++i) {
			final LayeredGrid grid = layeredGrids[i];
			grid.setTilePositions();
			grid.gameObject.removeAll();
			grid.gameObject.commitUpdates();
			grid.layerCount = currentZone.getDrawLayerInfo().length;
			
			for (int j = 0; j < grid.layerCount; ++j) {
				TiledVertexGrid tiledVertexGrid = grid.scrollers[j].getVertexGrid();
				tiledVertexGrid.setTexture(currentZone.getTexture());
				tiledVertexGrid.setTilePixelWidth(tilePixelWidth);
				tiledVertexGrid.setTilePixelHeight(tilePixelHeight);
				
				grid.renders[j].setPriority(currentZone.getDrawLayerInfo()[j].priority_);
				
				grid.gameObject.add(grid.scrollers[j]);
				grid.gameObject.add(grid.renders[j]);
			}
			grid.gameObject.commitUpdates();
		}
		
		centerGridOnPlayer(layeredGrids[ACTIVE_GRID]);
		remapGrid(layeredGrids[ACTIVE_GRID]);
		
		return true;
	}
	
	/**
	 * 
	 * @param grid
	 */
	
	//TODO : change to center on camera target
	private void centerGridOnPlayer(LayeredGrid grid) {
		final Vector2 playerPos = BaseObject.sSystemRegistry.gameObjectManager.getPlayer().getPosition();
		float left = snapFocalPointToWorldBoundsX(playerPos.x)-(layeredGridTileWidth*tilePixelWidth)/2;
		float bottom = snapFocalPointToWorldBoundsY(playerPos.y)-(layeredGridTileHeight*tilePixelHeight)/2;
		left -= (left%tilePixelWidth);
		bottom -= (bottom%tilePixelHeight);
		grid.gameObject.getPosition().set(left, bottom);
		loadBounds.setCenter(playerPos.x, playerPos.y);  //TODO: need better way to calculate reload bounds
	}
	
	/**
	 * 
	 * @param worldX
	 * @return
	 */
	
	private float snapFocalPointToWorldBoundsX(float worldX) {
        float focalPositionX = worldX;
        final float width = layeredGridTileWidth*tilePixelWidth; 
        if (currentZone != null) {
            final float worldPixelWidth = Math.max(currentZone.getWorldWidth(), width);
            final float rightEdge = focalPositionX  + (width / 2.0f);
            final float leftEdge = focalPositionX - (width / 2.0f);
    
            if (rightEdge > worldPixelWidth) {
                focalPositionX = worldPixelWidth- (width / 2.0f);
            } else if (leftEdge < 0) {
                focalPositionX = width / 2.0f;
            }
        }
        return focalPositionX;
    }
	
	/**
	 * 
	 * @param worldY
	 * @return
	 */
	
	private float snapFocalPointToWorldBoundsY(float worldY) {
        float focalPositionY = worldY;

        final float height = layeredGridTileHeight*tilePixelHeight;
        if (currentZone != null) {
            final float worldPixelHeight = Math.max(currentZone.getWorldHeight(), height);
            final float topEdge = focalPositionY + (height / 2.0f);
            final float bottomEdge = focalPositionY - (height / 2.0f);
    
            if (topEdge > worldPixelHeight) {
                focalPositionY = worldPixelHeight -(height / 2.0f);
            } else if (bottomEdge < 0) {
                focalPositionY = height / 2.0f;
            }
        }
        
        return focalPositionY;
    }
	
	/**
	 * 
	 * @param loader
	 */
	
	public void setLoader(ZoneLoader loader) {
		zoneLoader_ = loader;
	}
	
	/**
	 * 
	 * @author matt
	 *
	 */
	
	private class LayeredGrid {
		static final int MAX_LAYER_COUNT = 10;
		int layerCount;
		GameObject gameObject;
		VertexGrid[] grids;
		RenderComponent[] renders;
		ScrollerComponent[] scrollers;
		
		/**
		 * 
		 * @param viewWidth
		 * @param viewHeight
		 */
		//FIXME: tilePixelHeight & tilePixelWidth are used before being set
		LayeredGrid(int viewWidth, int viewHeight) {
			layerCount = 0;
			gameObject = new GameObject();
			grids = new VertexGrid[MAX_LAYER_COUNT];
			renders = new RenderComponent[MAX_LAYER_COUNT];
			scrollers = new ScrollerComponent[MAX_LAYER_COUNT];
			
			for (int i = 0; i < MAX_LAYER_COUNT; ++i) {
				grids[i] = new VertexGrid(layeredGridTileWidth, layeredGridTileHeight);
				
				TiledVertexGrid layer = new TiledVertexGrid(null, 
															grids[i],
															viewWidth,
															viewHeight,
															tilePixelWidth, 
															tilePixelHeight,
															layeredGridTileWidth,
															layeredGridTileHeight);
				
				
				renders[i] = new RenderComponent();
				renders[i].setCameraRelative(false);
				float xScrollSpeed = 1.0f;
		        float yScrollSpeed = 1.0f;
		        scrollers[i] = new ScrollerComponent(xScrollSpeed,
		        													yScrollSpeed,
		        													viewWidth,
		        													viewHeight,
		        													layer);
		        scrollers[i].setRenderComponent(renders[i]);
			}
		}
	
		/**
		 * 
		 */
		public void setTilePositions() {
			
			for (int tileY = 0; tileY < layeredGridTileHeight; tileY++) {
				for (int tileX = 0; tileX < layeredGridTileWidth; tileX++) {

					final float offsetX = tileX * tilePixelWidth;
					final float offsetY = tileY * tilePixelHeight;

					final float[] p0 = { offsetX,                  offsetY + tilePixelHeight, 0.0f };
					final float[] p1 = { offsetX,                  offsetY,                   0.0f };
					final float[] p2 = { offsetX + tilePixelWidth, offsetY,                   0.0f };
					final float[] p3 = { offsetX + tilePixelWidth, offsetY + tilePixelHeight, 0.0f };

					final float[][] positions = { p0, p1, p2, p3 };
					for (int i = 0; i < MAX_LAYER_COUNT; ++i) {
						grids[i].setPosition(tileX, tileY, positions);
					}
				}
			}
		}
		
	}
	
	/**
	 * 
	 * @author matt
	 *
	 */
	
	private class ZoneLoadingThread extends Thread {

		
		private Object varUpdateLock;
		private LayeredGrid gridToLoad;
		private boolean loading;
		private boolean requestShutdown;
		
		/**
		 * 
		 */
		public ZoneLoadingThread() {
			varUpdateLock = new Object();
			gridToLoad = null;
			loading = false;
			requestShutdown = false;
			setName("ZoneLoadingThread");
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			LayeredGrid grid = null;
			boolean running = true;
			try {
				while (running) {
					synchronized (varUpdateLock) {
						while (true) {
							
							if (requestShutdown) {
								running = false;
								break;
							}

							if (gridToLoad != null) {
								grid = gridToLoad;
								gridToLoad = null;
								loading = true;
								break;
							}
							
							loading = false;
							varUpdateLock.wait();
						}
					}

					if (running) {
						remapGrid(grid);
						grid = null;
					}

				}
			
			} catch (InterruptedException e) {
				DebugLog.e("SnowBall", e.toString());
			}

		}
		
		/**
		 * 
		 */
		public void shutdown() {
			synchronized(varUpdateLock) {
				requestShutdown = true;
				varUpdateLock.notifyAll();
			}
		}
		
		/**
		 * 
		 * @return
		 */
		public boolean loadingComplete() {
			synchronized(varUpdateLock) {
				return !loading;
			}
		}
		
		/**
		 * 
		 * @param grid
		 */
		public void postGridToLoad(LayeredGrid grid) {
			synchronized(varUpdateLock) {
				gridToLoad = grid;
				varUpdateLock.notifyAll();
			}
		}

	}

}
