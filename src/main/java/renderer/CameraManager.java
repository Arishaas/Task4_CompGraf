package renderer;

import java.util.ArrayList;
import java.util.List;

public class CameraManager {
    private final List<Camera> cameras = new ArrayList<>();
    private int activeIndex = -1;

    public void addCamera(Camera camera) {
        cameras.add(camera);
        if (activeIndex < 0) activeIndex = 0;
    }

    public void removeCamera(int index) {
        if (index < 0 || index >= cameras.size()) return;
        cameras.remove(index);
        if (cameras.isEmpty()) activeIndex = -1;
        else activeIndex = Math.min(activeIndex, cameras.size() - 1);
    }

    public void setActiveIndex(int index) {
        if (index < 0 || index >= cameras.size()) return;
        activeIndex = index;
    }

    public Camera getActive() {
        if (activeIndex < 0) throw new IllegalStateException("No cameras");
        return cameras.get(activeIndex);
    }

    public int size() { return cameras.size(); }
    public int getActiveIndex() { return activeIndex; }
    public List<Camera> getAll() { return List.copyOf(cameras); }
}