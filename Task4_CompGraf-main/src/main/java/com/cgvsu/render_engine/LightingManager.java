package com.cgvsu.render_engine;


import com.cgvsu.math.Matrix4f;

import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.NormalCalculator;
import javafx.scene.paint.Color;

import java.util.*;

public class LightingManager {
    private Map<Integer, Light> lights;
    private Set<Integer> activeLightIds;
    private int nextLightId;
    private boolean isBoundToCamera;
    private Camera boundCamera;
    private Integer boundLightId;

    public LightingManager() {
        lights = new HashMap<>();
        activeLightIds = new HashSet<>();
        nextLightId = 0;
        isBoundToCamera = false;
        boundLightId = null;
    }

    public int addLight(Vector3f position, Color color,float intensity,  Model model, RenderParameters renderParameters) {
        Light light = new Light(position, color, intensity);
        light.setSphere(model);
        light.setRenderParameters(renderParameters);
        int id = nextLightId++;
        lights.put(id, light);
        return id;
    }

    public void removeLight(int id) {
        lights.remove(id);
        activeLightIds.remove(id);

        if (boundLightId != null && id == boundLightId) {
            unbindFromCamera();
        }
    }

    public List<Light> getActiveLights() {
        List<Light> activeLights = new ArrayList<>();
        for (int id : activeLightIds) {
            Light light = lights.get(id);
            if (light != null) {
                if (isBoundToCamera && boundCamera != null && id == boundLightId) {
                    Vector3f position = boundCamera.getPosition();
                    light.setPosition(position);
                }
                //updateLightSpherePosition(light); // Обновляем позицию сферы
                activeLights.add(light);
            }
        }
        return activeLights;
    }


    public void bindToCamera(Camera camera) {
        if (activeLightIds.size() != 1) {
            throw new IllegalStateException("Выберите только один источник освещения.");
        }

        boundLightId = activeLightIds.iterator().next();
        Light boundLight = lights.get(boundLightId);
        if (boundLight != null) {
            boundLight.setDrawable(false); // Выключаем отрисовку сферы
        }
        this.boundCamera = camera;
        this.isBoundToCamera = true;
    }


    public void unbindFromCamera() {
        if (boundLightId != null) {
            Light boundLight = lights.get(boundLightId);
            if (boundLight != null) {
                boundLight.setDrawable(true);

                Vector3f lightPosition = boundLight.getPosition();


                Vector3f origin = new Vector3f(0, 0, 0);

                Vector3f direction = new Vector3f(
                        lightPosition.getX() - origin.getX(),
                        lightPosition.getY() - origin.getY(),
                        lightPosition.getZ() - origin.getZ()
                );

                direction.normalize();
                float offsetDistance = 100.0f;
                Vector3f offset = new Vector3f(
                        direction.getX() * offsetDistance,
                        direction.getY() * offsetDistance,
                        direction.getZ() * offsetDistance
                );
                Vector3f currentPosition = boundCamera.getPosition();
                Vector3f newPosition = new Vector3f(
                        currentPosition.getX() + offset.getX(),
                        currentPosition.getY() + offset.getY(),
                        currentPosition.getZ() + offset.getZ()
                );
                boundCamera.setPosition(newPosition);
                boundCamera.setAzimuthAndElevation();

                updateLightSpherePosition(boundLight);
            }
        }

        this.boundCamera = null;
        this.isBoundToCamera = false;
        this.boundLightId = null;
    }


    public Integer getBoundLightId() {
        return boundLightId;
    }

    public void setActiveLights(Set<Integer> activeLightIds) {
        this.activeLightIds = activeLightIds;

        if (!activeLightIds.contains(boundLightId) && isBoundToCamera) {
            unbindFromCamera();
        }
    }

    public boolean isBoundToCamera() {
        return isBoundToCamera;
    }

    public Map<Integer, Light> getLights() {
        return lights;
    }

    public Set<Integer> getActiveLightIds() {
        return activeLightIds;
    }

    private void updateLightSpherePosition(Light light) {
        if (light.getSphere() != null && light.isDrawable()) {

            Matrix4f transformationMatrix = GraphicConveyor.scaleRotateTranslate(
                    new Vector3f(0, 0, 0),
                    new Vector3f(1, 1, 1),
                    light.getPosition());

            ArrayList<Vector3f> transformedVertices = new ArrayList<>();
            for (Vector3f vertex : light.getSphere().getOriginalVertices()) {
                Vector3f transformedVertex = GraphicConveyor.multiplyMatrix4ByVector3(transformationMatrix, vertex);
                transformedVertices.add(transformedVertex);
            }

            light.getSphere().setVertices(transformedVertices);
            light.getSphere().setNormals(NormalCalculator.calculateNormals(light.getSphere()));


        }
    }



    public Camera getBoundCamera() {
        return boundCamera;
    }

    public void setBoundCamera(Camera boundCamera) {
        this.boundCamera = boundCamera;
    }
}