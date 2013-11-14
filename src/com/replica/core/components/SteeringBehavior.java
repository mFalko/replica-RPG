package com.replica.core.components;

import java.util.Random;

import android.os.SystemClock;

import com.replica.core.BaseObject;
import com.replica.core.GameObject;
import com.replica.core.collision.HitPoint;
import com.replica.core.collision.HitPointPool;
import com.replica.core.collision.LineSegment;
import com.replica.core.components.VehicleComponent.SteeringAdapter;
import com.replica.core.systems.CollisionSystem;
import com.replica.utility.DebugSystem;
import com.replica.utility.FixedSizeArray;
import com.replica.utility.RectF;
import com.replica.utility.TObjectPool;
import com.replica.utility.Utils;
import com.replica.utility.Vector2;
import com.replica.utility.VectorPool;

public class SteeringBehavior extends GameComponent implements SteeringAdapter{

	
	
	public SteeringBehavior() {
        super();
        setPhase(ComponentPhases.FRAME_END.ordinal());
        commandQueue = new FixedSizeArray<SteeringCommand>(10); //TODO: magic number
        commandPool = new SteeringCommandPool(10);
        queryRect = new RectF();
        outputHitPoints = new FixedSizeArray<HitPoint>(1);
        wanderTarget_ = new Vector2();
        reset();
    }
     
    @Override
    public void reset() {
    	vehicleParent_ = null;
    	lastTimeDelta_ = 0.0f;
    	wanderTarget_.zero();
    	wanderTimer =  Float.MAX_VALUE;
    	clearCommands();
    }

    @Override
    public void update(float timeDelta, BaseObject parent) {
    	clearCommands();
    	lastTimeDelta_ = timeDelta;
    }
    
	public void setVehicleParent (GameObject parent) {
		vehicleParent_ = parent;
	}
	
	public void postCommand(Behavior command) {
		if (command != null
				&& commandPool.getSize() - commandPool.getAllocatedCount() > 0) {
			SteeringCommand steeringCommand = commandPool.allocate();
			steeringCommand.command = command;
			commandQueue.add(steeringCommand);
		}
	}
	
	public void postCommand(Behavior command, Vector2 target) {
		if (command != null
				&& commandPool.getSize() - commandPool.getAllocatedCount() > 0) {
			SteeringCommand steeringCommand = commandPool.allocate();
			steeringCommand.target = target;
			steeringCommand.command = command;
			commandQueue.add(steeringCommand);
		}
	}
    
	public void postCommand(Behavior command, GameObject actor) {
		
		if (!(actor == null || command == null)
				&& commandPool.getSize() - commandPool.getAllocatedCount() > 0) {
			SteeringCommand steeringCommand = commandPool.allocate();
			
			steeringCommand.actor = actor;
			steeringCommand.command = command;
			commandQueue.add(steeringCommand);
		}
	}
	
	@Override
	public void getSteeringForce(Vector2 steeringForce) {
		steeringForce.zero();
		final int steeringCommandCount = commandQueue.getCount();
		if (steeringCommandCount > 0) {
			final Object[] steeringCommandArray = commandQueue.getArray();
			VectorPool vectorPool = sSystemRegistry.vectorPool;
			Vector2 accumForce = vectorPool.allocate();
			
			for (int i = 0; i < steeringCommandCount; i++) {
				final SteeringCommand steeringCommand = (SteeringCommand) steeringCommandArray[i];

				switch (steeringCommand.command) {
				case Seek:
					Seek(steeringCommand.target, accumForce);
					break;

				case Flee:
					Flee(steeringCommand.target, accumForce);
					break;

				case Pursuit:
					if (steeringCommand.actor != null)
						Pursuit(steeringCommand.actor);
					break;

				case Evade:
					if (steeringCommand.actor != null)
						Evade(steeringCommand.actor);
					break;

				case Wander:
					Wander(accumForce);
					break;

				case WallAvoidance:
					WallAvoidance(accumForce);
					break;

				case FollowPath:
					FollowPath();
					break;

				case Cohesion:
						Cohesion();
					break;

				case Separation:
						Separation();
					break;

				case Alignment:
						Alignment();
					break;

				}
				
				steeringForce.add(accumForce);
				accumForce.zero();
			}

			vectorPool.release(accumForce);		
		}

	}
		
	private void clearCommands() {
    	
    	while (commandQueue.getCount() > 0) {
    		commandPool.release(commandQueue.get(0));
    		commandQueue.remove(0);
    	}
    	
    }
	
	private void Seek(Vector2 target, Vector2 resultVector) {
		resultVector.set(target);
		resultVector.subtract(vehicleParent_.getCenteredPositionX(), vehicleParent_.getCenteredPositionY());
		resultVector.normalize();
		resultVector.multiply(vehicleParent_.getMaxSpeed());
		resultVector.subtract(vehicleParent_.getVelocity());
		
	}

	private void Flee(Vector2 target, Vector2 resultVector) {
		resultVector.set(vehicleParent_.getCenteredPositionX(), vehicleParent_.getCenteredPositionY());
		resultVector.subtract(target);
		resultVector.normalize();
		resultVector.multiply(vehicleParent_.getMaxSpeed());
		resultVector.subtract(vehicleParent_.getVelocity());
	}


	private Vector2 Pursuit(GameObject agent) {
		return null;

	}

	private Vector2 Evade(GameObject agent) {
		return null;

	}

	private void Wander(Vector2 resultVector) {
		
		wanderTimer += lastTimeDelta_;
		
		if (wanderTimer < 2) {
			return;
		}
		
		float wanderMagic = lastTimeDelta_ * 400f;
		VectorPool pool = BaseObject.sSystemRegistry.vectorPool;
		wanderTarget_.add(randClamped() * wanderMagic, randClamped() * wanderMagic);
		wanderTarget_.normalize();
		wanderTarget_.multiply(30); //wander radius
		Vector2 distanceVector = pool.allocate();
		distanceVector.set(vehicleParent_.facingDirection);
		distanceVector.normalize();
		distanceVector.multiply(50); //wander distance
		Vector2 target = pool.allocate();
		target.set(vehicleParent_.getCenteredPositionX(), vehicleParent_.getCenteredPositionY());
		target.add(distanceVector);
		target.add(wanderTarget_);
		Seek(target, resultVector);
		pool.release(distanceVector);
		pool.release(target);
		wanderTimer = 0.0f;
	}
	
	private float randClamped() {
		
		return 2 * (RANDOM.nextFloat() - 0.5f);
	}

	private void WallAvoidance(Vector2 resultVector) {
		CollisionSystem collision = sSystemRegistry.collisionSystem;
		
		float x = vehicleParent_.getCenteredPositionX() - vehicleParent_.width / 2;
		float y = vehicleParent_.getCenteredPositionY() - vehicleParent_.height / 2;
		queryRect.set(x, y- vehicleParent_.width/8, vehicleParent_.width, vehicleParent_.height/1.5f);

		boolean hit = CollisionSystem.testBoxAgainstList(collision.queryBackgroundCollision(queryRect),
				queryRect.left_, queryRect.right_, queryRect.top_,
				queryRect.bottom_, vehicleParent_, Vector2.ZERO, outputHitPoints);	
		
		if (hit) {
			HitPointPool hitPool = sSystemRegistry.hitPointPool;
			VectorPool vectorPool = sSystemRegistry.vectorPool;
			Vector2 force = vectorPool.allocate();
			while (outputHitPoints.getCount() > 0) {
				HitPoint hitPoint = outputHitPoints.get(0);
				
				Flee(hitPoint.hitPoint, force);
				resultVector.add(force);
				
				vectorPool.release(hitPoint.hitNormal);
				vectorPool.release(hitPoint.hitPoint);
				hitPool.release(hitPoint);
				outputHitPoints.remove(0);
			}
			vectorPool.release(force);
		}
		
	}

	private Vector2 FollowPath() {
		return null;

	}

	

	private Vector2 Cohesion() {
		return null;

	}

	private Vector2 Separation() {
		return null;

	}

	private Vector2 Alignment() {
		return null;

	}
	
	enum Behavior {
		Seek,
		Flee,
		Pursuit,
		Evade,
		Wander,
		WallAvoidance,
		FollowPath,
		Cohesion,
		Separation,
		Alignment
	}
	
	public class SteeringCommand {
		public Behavior command;
		public Vector2 target;
		public GameObject actor;
		
		public SteeringCommand() {
			reset();
		}
		
		public void reset() {
			command = null;
			target = null;
			actor = null;
		}
	}
	
	public class SteeringCommandPool extends TObjectPool<SteeringCommand> {

		public SteeringCommandPool(int size) {
			super(size);
		}

		@Override
		protected void fill() {
			for (int x = 0; x < getSize(); x++) {
				getAvailable().add(new SteeringCommand());
			}
		}

		@Override
		public void release(Object entry) {
			((SteeringCommand) entry).reset();
			super.release(entry);
		}

	}
	
	private GameObject vehicleParent_;
	private FixedSizeArray<SteeringCommand> commandQueue;
	private SteeringCommandPool commandPool;
	private RectF queryRect;
	private FixedSizeArray<HitPoint> outputHitPoints;
	private float lastTimeDelta_;
	private Vector2 wanderTarget_;
	private float wanderTimer;
	private static final Random RANDOM = new Random((long) (System.currentTimeMillis() * (SystemClock.uptimeMillis() / Math.random()+.01f)));;
}