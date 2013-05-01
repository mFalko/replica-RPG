package com.replica;

import com.replica.core.BaseObject;
import com.replica.core.GameObject;
import com.replica.core.GameObject.ActionType;
import com.replica.core.components.GameComponent;
import com.replica.input.InputDPad;
import com.replica.input.InputGameInterface;
import com.replica.utility.Vector2;

public class GhostMovementGameComponent extends GameComponent {

	private final float GHOST_MOVEMENT_SPEED = 2.2f;
	Vector2 d;

	public GhostMovementGameComponent() {
		d = new Vector2();
		
		setPhase(ComponentPhases.MOVEMENT.ordinal());
	}

	@Override
	public void reset() {

	}

	@Override
	public void update(float timeDelta, BaseObject parent) {

		InputGameInterface input = sSystemRegistry.inputGameInterface;
		Vector2 pos = ((GameObject) parent).getPosition();
		GameObject parentObject = (GameObject) parent;
		float deltaX = 0;
		float deltaY = 0;

		InputDPad dpad = input.getDpad();
		if (dpad.pressed()) {
			if (dpad.upPressed()) {
				deltaY += GHOST_MOVEMENT_SPEED -1;
			} else if (dpad.downPressed()) {
				deltaY += -GHOST_MOVEMENT_SPEED -1;
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
			parentObject.setCurrentAction(ActionType.MOVE);
			
		} else {
			
			parentObject.setCurrentAction(ActionType.IDLE);
		}

	}

}
