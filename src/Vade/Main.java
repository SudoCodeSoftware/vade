//TODO: Chunking

package Vade;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

public class Main extends SimpleApplication {
 
    private BulletAppState bulletAppState;  //Physics
    
    private Spaceship spaceship;
    private SpaceshipCam spaceshipCam;
    
    public static void main(String[] args){
        Main app = new Main();
        app.start(); // start the game
    }
    
    @Override
    public void simpleInitApp() {
        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().setGravity(Vector3f.ZERO);
        
        this.flyCam.setEnabled(false);
        
        this.spaceship = new Spaceship(this.rootNode, this.assetManager, this.bulletAppState.getPhysicsSpace(), "Models/Spaceship/Spaceship.j3o");
        this.spaceshipCam = new SpaceshipCam(this.spaceship, this.cam);
        
        Box b = new Box(10, 10, 10);
        Geometry[][][] placeholders = new Geometry[5][5][100];
        Material mat = new Material(this.assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        
        for (int x = 0; x < placeholders.length; x++) {
            for (int y = 0; y < placeholders[0].length; y++) {
                for (int z = 0; z < placeholders[0][0].length; z++) {
                    
                    placeholders[x][y][z] = new Geometry("Box", b);
                    placeholders[x][y][z].setMaterial(mat);
                    placeholders[x][y][z].setLocalTranslation(x * 1000 - placeholders.length * 500, y * 1000 - placeholders[0].length * 500, z * 1000 - placeholders.length * 500);
                    this.rootNode.attachChild(placeholders[x][y][z]);
                }
            }
        }
        
        DirectionalLight light = new DirectionalLight();
        light.setDirection(new Vector3f(-1, -1, -1).normalizeLocal());
        this.rootNode.addLight(light);
        
        DirectionalLight light2 = new DirectionalLight();
        light2.setDirection(new Vector3f(1, 1, 1).normalizeLocal());
        this.rootNode.addLight(light2);
        
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.3f));
        this.rootNode.addLight(al);
        
        initKeys();
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        this.spaceshipCam.update(tpf);  //SpaceshipCam.update MUST be called first
        this.spaceship.update(tpf);
    }
    
    private void initKeys() {
    
        this.inputManager.addMapping("Accelerate", new KeyTrigger(KeyInput.KEY_W));
        this.inputManager.addMapping("Decelerate", new KeyTrigger(KeyInput.KEY_S));
        this.inputManager.addMapping("YawLeft",    new KeyTrigger(KeyInput.KEY_A));
        this.inputManager.addMapping("YawRight",   new KeyTrigger(KeyInput.KEY_D));
        this.inputManager.addMapping("RollLeft",   new KeyTrigger(KeyInput.KEY_LEFT));
        this.inputManager.addMapping("RollRight",  new KeyTrigger(KeyInput.KEY_RIGHT));
        this.inputManager.addMapping("PitchUp",    new KeyTrigger(KeyInput.KEY_DOWN));
        this.inputManager.addMapping("PitchDown",  new KeyTrigger(KeyInput.KEY_UP));
        this.inputManager.addMapping("ZoomIn",     new KeyTrigger(KeyInput.KEY_EQUALS));
        this.inputManager.addMapping("ZoomOut",    new KeyTrigger(KeyInput.KEY_MINUS));
        
        this.inputManager.addListener(this.analogListener, 
                "Accelerate", "Decelerate", 
                "YawLeft", "YawRight", 
                "RollLeft", "RollRight", 
                "PitchUp", "PitchDown", 
                "ZoomIn", "ZoomOut");
    }
    
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            
        }
    };
 
    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float value, float tpf) {
            
            if (name.equals("Accelerate")) {
                Main.this.spaceship.accelerate(tpf);
            }
            if (name.equals("Decelerate")) {
                Main.this.spaceship.decelerate(tpf);
            }
            
            if (name.equals("YawLeft")) {
                Main.this.spaceship.yawLeft(tpf);
            }
            
            if (name.equals("YawRight")) {
                Main.this.spaceship.yawRight(tpf);
            }
            
            if (name.equals("RollLeft")) {
                Main.this.spaceship.rollLeft(tpf);
            }
            
            if (name.equals("RollRight")) {
                Main.this.spaceship.rollRight(tpf);
            }
            
            if (name.equals("PitchUp")) {
                Main.this.spaceship.pitchUp(tpf);
            }
            
            if (name.equals("PitchDown")) {
                Main.this.spaceship.pitchDown(tpf);
            }
            
            if (name.equals("ZoomIn")) {
                Main.this.spaceshipCam.zoomIn(tpf);
            }
            
            if (name.equals("ZoomOut")) {
                Main.this.spaceshipCam.zoomOut(tpf);
            }
        }
    };
}