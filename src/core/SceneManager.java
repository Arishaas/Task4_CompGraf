package core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Хранит список моделей и текущий выбор.
 * Уведомляет UI через простой Observer.
 */
public class SceneManager {

    private final List<Model3D> models = new ArrayList<>();
    private final List<Model3D> selected = new ArrayList<>();
    private final List<SceneListener> listeners = new ArrayList<>();

    public void addModel(Model3D m) {
        models.add(m);
        fireSceneChanged();
    }

    public void removeModel(Model3D m) {
        models.remove(m);
        selected.remove(m);
        fireSceneChanged();
        fireSelectionChanged();
    }

    public List<Model3D> getModels() {
        return Collections.unmodifiableList(models);
    }

    public List<Model3D> getSelected() {
        return Collections.unmodifiableList(selected);
    }

    public void setSelected(Collection<Model3D> sel) {
        selected.clear();
        if (sel != null) selected.addAll(sel);
        fireSelectionChanged();
    }

    public Model3D getSingleSelected() {
        return selected.isEmpty() ? null : selected.get(0);
    }

    public void addListener(SceneListener l) {
        listeners.add(l);
    }

    public void removeListener(SceneListener l) {
        listeners.remove(l);
    }

    private void fireSceneChanged() {
        for (SceneListener l : listeners) l.sceneChanged();
    }

    private void fireSelectionChanged() {
        for (SceneListener l : listeners) l.selectionChanged();
    }

    public interface SceneListener {
        void sceneChanged();
        void selectionChanged();
    }
}
