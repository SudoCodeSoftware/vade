package Vade;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;

public class SpaceshipCam {
    
    private CameraNode cameraNode;
    private Spaceship spaceship;
    
    private float defaultBackDist = 20f;    //The distance behind the camera that the camera will go back to after acceleration/deceleration
    private float backDist = this.defaultBackDist; //How far back the camera is
    private float defaultBackDistMin = 15f;   //Minimum distance that can be zoomed forwards
    private float defaultBackDistMax = 50f;  //Maximum distance that can be zoomed backwards
    private float backDistBoundSize = this.defaultBackDist / 4f;  //How far from the defaultBackDist the camera can move due to acceleration and deceleration
    private final float zoomSpeed = 20f;     //How fast the camera zooms in and out
    
    //Note that are mostly result in different camera transformations from each other so the numbers can't really be compared on the same scale
    private final float accelCameraSpeed = 2f;    //How fast the camera movement due to acceleration, 
    private final float rollCameraSpeed = 0.1f;        //roll, pitch and yaw is. accelCameraSpeed
    private final float pitchCameraSpeed = 0.5f;       //also affects the fluidity of zooming
    private final float yawCameraSpeed = 1f;
    
    public SpaceshipCam(Spaceship spaceship, Camera cam) {
        
        this.spaceship = spaceship;
        
        this.cameraNode = new CameraNode("Camera Node", cam);
        this.cameraNode.setLocalTranslation(0, 0, -3f);
        this.cameraNode.lookAt(spaceship.getSpatial().getLocalTranslation(), Vector3f.UNIT_Y);
        this.cameraNode.getCamera().setFrustumFar(100000000f);
        spaceship.getNode().attachChild(this.cameraNode);
    }
    
    public void zoomIn(float tpf) {
        
        if (this.defaultBackDist > this.defaultBackDistMin) {
                
            this.defaultBackDist -= this.zoomSpeed * tpf;

            if (this.defaultBackDist < this.defaultBackDistMin) {
                this.defaultBackDist = this.defaultBackDistMin;
            }
        }
        
        backDistBoundSize = this.defaultBackDist / 4f;
    }
    
    public void zoomOut(float tpf) {
        
        if (this.defaultBackDist < this.defaultBackDistMax) {
                
            this.defaultBackDist += this.zoomSpeed * tpf;

            if (this.defaultBackDist > this.defaultBackDistMax) {
                this.defaultBackDist = this.defaultBackDistMax;
            }
        }
        
        backDistBoundSize = this.defaultBackDist / 4f;
    }
    
    public void update(float tpf) { //Must be called BEFORE Spaceship.update(tpf)
        
        //If the ship is accelerating and the distance hasn't got to the limit yet
        if (this.spaceship.getAcceleration() != 0) {
            
            if (this.spaceship.getAcceleration() > 0) {
                this.backDist += ((this.defaultBackDist + this.backDistBoundSize) - this.backDist) * this.accelCameraSpeed * tpf;
            }
            
            else if (this.spaceship.getAcceleration() < 0) {
                this.backDist -= (this.backDist - (this.defaultBackDist - this.backDistBoundSize)) * this.accelCameraSpeed * tpf;
            }
        }
        
        //The spaceship isn't accelerating and the camera is closer than the default
        else if (this.backDist < defaultBackDist && this.spaceship.getAcceleration() == 0) {
            
            this.backDist += (this.defaultBackDist - this.backDist) * this.accelCameraSpeed * tpf;
            
            if (this.backDist > this.defaultBackDist) {
                this.backDist = this.defaultBackDist;
            }
        }
        
        //The spaceship isn't accelerating and the camera is further than the default
        else if (this.backDist > defaultBackDist && this.spaceship.getAcceleration() == 0) {
            
            this.backDist -= (this.backDist - this.defaultBackDist) * this.accelCameraSpeed * tpf;
            
            if (this.backDist < this.defaultBackDist) {
                this.backDist = this.defaultBackDist;
            }
        }
        
        this.cameraNode.setLocalTranslation(-this.spaceship.getYawSpeed() * this.yawCameraSpeed, -this.spaceship.getPitchSpeed() * this.pitchCameraSpeed, -this.backDist);
        
        Quaternion cameraRotation = new Quaternion();
        cameraRotation.fromAngles(0, 0, -this.spaceship.getRollSpeed() * this.rollCameraSpeed);
        this.cameraNode.setLocalRotation(cameraRotation);
    }
}