package Vade;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Spaceship {
    
    public class SpaceshipControl extends RigidBodyControl implements PhysicsTickListener
    {
        public SpaceshipControl(PhysicsSpace physicsSpace, CollisionShape shape, float mass) {
            super(shape, mass);
        }
        
        @Override
        public void prePhysicsTick(PhysicsSpace physicsSpace, float tpf){
            
        }
        
        @Override
        public void physicsTick(PhysicsSpace physicsSpace, float tpf){
            
        }
    }
    
    private Spatial spaceship;
    private Node spaceshipNode;
    private Node rootNode;  //Keep a refernce to the rootNode since we will be moving it
    
    CapsuleCollisionShape capsuleColShape;
    SpaceshipControl spaceshipPhys;
    
    private float forwardSpeed;   //Hpw fast the spaceship is currently going forwards
    private float rollSpeed;   //How fast the spaceship is going around the z axis
    private float pitchSpeed;  //How fast the spaceship is goin around the x axis
    private float yawSpeed;
    
    private boolean accelerating;   //Whether or not the spaceship is accelerating
    private boolean decelerating;   //Whether or not the spaceship is decelerating
    private boolean rolling;   //Whether or not the spaceship is actively
    private boolean pitching;  //changing roll or pitch or yaw (not just drifting)
    private boolean yawing;
    
    private final float maxRollSpeed = 4f;
    private final float maxPitchSpeed = 2f;
    private final float maxYawSpeed = 1f;
    
    private final float forwardAccel = 500f; //How quickly the spaceship accelerating
    private final float rollAccel = 2f;        //forwards, rolling, pitching and yawing
    private final float pitchAccel = 2f;
    private final float yawAccel = 2f;
    
    public Spaceship(Node rootNode, AssetManager assetManager, PhysicsSpace physicsSpace, String modelFilepath) {
    
        this.spaceshipNode = new Node("SpaceshipNode");
        this.spaceship = assetManager.loadModel(modelFilepath);
        
        this.capsuleColShape = new CapsuleCollisionShape(10f, 5f);
        this.spaceshipPhys = new SpaceshipControl(physicsSpace, capsuleColShape, 1f);
        
        this.spaceshipNode.addControl(spaceshipPhys);
        physicsSpace.add(spaceshipPhys);
        
        this.spaceshipNode.attachChild(this.spaceship);
        rootNode.attachChild(this.spaceshipNode);
        
        this.rootNode = rootNode;
    }
    
    public void update(float tpf) {
        
        if (!this.rolling && this.rollSpeed < 0f) {
            
            this.rollSpeed += -this.rollSpeed * this.rollAccel * tpf;
            
            if (this.rollSpeed > 0f) {   //It jiggles if this isn't here
                this.rollSpeed = 0;
            }
        }
        
        else if (!this.rolling && this.rollSpeed > 0f) {
            
            this.rollSpeed -= this.rollSpeed * this.rollAccel * tpf;
            
            if (this.rollSpeed < 0f) {
                this.rollSpeed = 0;
            }
        }
        
        if (!this.pitching && this.pitchSpeed < -0f) {
            
            this.pitchSpeed += -this.pitchSpeed * pitchAccel * tpf;
            
            if (this.pitchSpeed > 0f) {
                this.pitchSpeed = 0;
            }
        }
        
        else if (!this.pitching && this.pitchSpeed > 0f) {
            
            this.pitchSpeed -= this.pitchSpeed * this.pitchAccel * tpf;
            
            if (this.pitchSpeed < 0f) {
                this.pitchSpeed = 0;
            }
        }
        
        if (!this.yawing && this.yawSpeed < -0f) {
            
            this.yawSpeed += -this.yawSpeed * yawAccel * tpf;
            
            if (this.yawSpeed > 0f) {
                this.yawSpeed = 0;
            }
        }
        
        else if (!this.yawing && this.yawSpeed > 0f) {
            
            this.yawSpeed -= this.yawSpeed * this.yawAccel * tpf;
            
            if (this.yawSpeed < 0f) {
                this.yawSpeed = 0;
            }
        }
        
        this.rootNode.move(     //forwards
                -this.forwardSpeed * this.spaceshipPhys.getPhysicsRotation().getRotationColumn(2).x * tpf,
                -this.forwardSpeed * this.spaceshipPhys.getPhysicsRotation().getRotationColumn(2).y * tpf,
                -this.forwardSpeed * this.spaceshipPhys.getPhysicsRotation().getRotationColumn(2).z * tpf
        );
        
        this.spaceshipPhys.setAngularVelocity(this.spaceshipPhys.getPhysicsRotationMatrix().mult(new Vector3f(
                this.pitchSpeed,
                this.yawSpeed,
                this.rollSpeed
        )));
        
        this.accelerating = false;
        this.decelerating = false;
        this.rolling = false;
        this.pitching = false;
        this.yawing = false;
    }
    
    public Spatial getSpatial() {
        return this.spaceship;
    }
    
    public Node getNode() {
        return this.spaceshipNode;
    }
    
    public SpaceshipControl getPhys() {
        return this.spaceshipPhys;
    }
    
    public float getAcceleration() {
        
        if (this.accelerating && !this.decelerating) {
            return this.forwardAccel;
        }
        
        else if (this.decelerating && !this.accelerating) {
            return -this.forwardAccel;
        }
        
        else return 0;
    }
    
    public float getRollSpeed() {
        return this.rollSpeed;
    }
    
    public float getPitchSpeed() {
        return this.pitchSpeed;
    }
    
    public float getYawSpeed() {
        return this.yawSpeed;
    }
    
    public void accelerate(float tpf) {
        this.forwardSpeed += this.forwardAccel * tpf;
        this.accelerating = true;
    }
    
    public void decelerate(float tpf) {
        this.forwardSpeed -= this.forwardAccel * tpf;
        this.decelerating = true;
    }
    
    public void rollLeft(float tpf) {
        
        this.rolling = true;
        
        if (this.rollSpeed > -this.maxRollSpeed) {
            
            this.rollSpeed -= (this.maxRollSpeed + this.rollSpeed) * this.rollAccel * tpf;
            
            if (this.rollSpeed < -this.maxRollSpeed) {
                this.rollSpeed = -this.maxRollSpeed;
            }
        }
    }
    
    public void rollRight(float tpf) {
        
        this.rolling = true;
        
        if (this.rollSpeed < this.maxRollSpeed) {
            
            this.rollSpeed += (this.maxRollSpeed - this.rollSpeed) * this.rollAccel * tpf;
            
            if (this.rollSpeed > this.maxRollSpeed) {
                this.rollSpeed = this.maxRollSpeed;
            }
        }
    }
    
    public void pitchUp(float tpf) {
        
        this.pitching = true;
        
        if (this.pitchSpeed > -this.maxPitchSpeed) {
            
            this.pitchSpeed -= (this.maxPitchSpeed + this.pitchSpeed) * this.pitchAccel * tpf;
            
            if (this.pitchSpeed < -this.maxPitchSpeed) {
                this.pitchSpeed = -this.maxPitchSpeed;
            }
        }
    }
    
    public void pitchDown(float tpf) {
        
        this.pitching = true;
        
        if (this.pitchSpeed < this.maxPitchSpeed) {
            
            this.pitchSpeed += (this.maxPitchSpeed - this.pitchSpeed) * this.pitchAccel * tpf;
            
            if (this.pitchSpeed > this.maxPitchSpeed) {
                this.pitchSpeed = this.maxPitchSpeed;
            }
        }
    }
    
    public void yawLeft(float tpf) {
        
        this.yawing = true;
        
        if (this.yawSpeed < this.maxYawSpeed) {
            
            this.yawSpeed += (this.maxYawSpeed - this.yawSpeed) * this.yawAccel * tpf;
            
            if (this.yawSpeed > this.maxYawSpeed) {
                this.yawSpeed = this.maxYawSpeed;
            }
        }
    }
    
    public void yawRight(float tpf) {
        
        this.yawing = true;
        
        if (this.yawSpeed > -this.maxYawSpeed) {
            
            this.yawSpeed -= (this.maxYawSpeed + this.yawSpeed) * this.yawAccel * tpf;
            
            if (this.yawSpeed < -this.maxYawSpeed) {
                this.yawSpeed = -this.maxYawSpeed;
            }
        }
    }
}