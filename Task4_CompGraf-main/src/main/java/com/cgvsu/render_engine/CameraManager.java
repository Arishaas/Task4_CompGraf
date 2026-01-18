package com.cgvsu.render_engine;

import com.cgvsu.math.Vector3f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CameraManager {
    private Map<Integer, Camera> cameras;
    private int activeCamera;
    private int nextCameraId;

    public CameraManager() {
        cameras = new HashMap<>();
        nextCameraId = 0;

        // Initialize with default camera
        Camera defaultCamera = new Camera(
                new Vector3f(0, 0, 100),
                new Vector3f(0, 0, 0),
                1F, 1, 25F, 2000);
        addCamera(defaultCamera);
        activeCamera = 0;
    }

    public int addCamera(Camera camera) {
        int id = nextCameraId++;
        cameras.put(id, camera);
        return id;
    }

    public void removeCamera(int id) {
        if (cameras.size() > 1) { // Keep at least one camera
            cameras.remove(id);
            if (activeCamera == id) {
                // Set active camera to first available
                activeCamera = cameras.keySet().iterator().next();
            }
        }
    }

    public Camera getActiveCamera() {
        return cameras.get(activeCamera);
    }

    public void setActiveCamera(int id) {
        if (cameras.containsKey(id)) {
            activeCamera = id;
        }
    }

    public Camera getCamera(int id) {
        return cameras.get(id);
    }

    public List<Integer> getCameraIds() {
        return new ArrayList<>(cameras.keySet());
    }

    public void updateCameraPosition(int id, Vector3f position) {
        Camera camera = cameras.get(id);
        if (camera != null) {
            camera.setPosition(position);
        }
    }

    public void updateCameraTarget(int id, Vector3f target) {
        Camera camera = cameras.get(id);
        if (camera != null) {
            camera.setTarget(target);
        }
    }
}