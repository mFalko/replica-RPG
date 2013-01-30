package com.falko.android.snowball;

import com.falko.android.snowball.core.BaseObject;
import com.falko.android.snowball.core.GameObject;
import com.falko.android.snowball.core.components.GameComponent;
import com.falko.android.snowball.core.components.RenderComponent;
import com.falko.android.snowball.core.graphics.DrawableBitmap;
import com.falko.android.snowball.input.InputButton;
import com.falko.android.snowball.input.InputGameInterface;
import com.falko.android.snowball.input.InputDPad;
import com.falko.android.snowball.utility.Vector2D;

public class GhostMovementGameComponent extends GameComponent {

	private final float GHOST_MOVEMENT_SPEED = 3.0f;
	Vector2D d;

	public GhostMovementGameComponent() {
		d = new Vector2D();
	}

	@Override
	public void reset() {

	}

	@Override
	public void update(float timeDelta, BaseObject parent) {

		InputGameInterface input = sSystemRegistry.inputGameInterface;
		Vector2D pos = ((GameObject) parent).getPosition();

		float deltaX = 0;
		float deltaY = 0;

		InputDPad dpad = input.getDpad();
		if (dpad.pressed()) {
			if (dpad.upPressed()) {
				deltaY += GHOST_MOVEMENT_SPEED;
			} else if (dpad.downPressed()) {
				deltaY += -GHOST_MOVEMENT_SPEED;
			}
			if (dpad.leftPressed()) {
				deltaX += -GHOST_MOVEMENT_SPEED;
			} else if (dpad.rightPressed()) {
				deltaX += GHOST_MOVEMENT_SPEED;
			}

			d.set(deltaX, deltaY);
			d.normalize();
			d.multiply(GHOST_MOVEMENT_SPEED);

			pos.y += d.y;
			pos.x += d.x;
		}

		DrawableBitmap bmap = sSystemRegistry.drawableFactory
				.allocateDrawableBitmap();
		bmap.setTexture(sSystemRegistry.shortTermTextureLibrary
				.getTextureByResource(R.drawable.debug_circle_red));
		bmap.resize(32, 32);
		reCom.setDrawable(bmap);

	}

	public void setRenderComponent(RenderComponent r) {
		reCom = r;
	}

	RenderComponent reCom;
}
