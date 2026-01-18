package com.cgvsu.render_engine;

import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import javafx.scene.paint.Color;

public class Light {
    private Vector3f position;  // Позиция источника света
    private Color color;
    private float intensity;
    private boolean isEnabled;
    private Model sphere;
    private boolean isDrawable = true;
    private RenderParameters renderParameters;

    public Light(Vector3f position, Color color, float intensity) {
        this.position = position;
        this.color = color;
        this.intensity = intensity;
        this.isEnabled = true;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Color getColor() {
        return color;
    }

    public float getIntensity() {
        return intensity;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    public Model getSphere() {
        return sphere;
    }

    public void setSphere(Model sphere) {
        this.sphere = sphere;
    }

    public RenderParameters getRenderParameters() {
        return renderParameters;
    }

    public void setRenderParameters(RenderParameters renderParameters) {
        this.renderParameters = renderParameters;
    }

    public boolean isDrawable() {
        return isDrawable;
    }

    public void setDrawable(boolean drawable) {
        isDrawable = drawable;
    }
}
