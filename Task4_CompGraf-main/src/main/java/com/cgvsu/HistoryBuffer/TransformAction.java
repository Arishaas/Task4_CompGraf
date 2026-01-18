package com.cgvsu.HistoryBuffer;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.model.Model;
import com.cgvsu.model.ModelEditingTools;

public class TransformAction implements Action<Model> {
    private Model model;
    private final Matrix4f beforeMatrix;
    private final Matrix4f currentMatrix;

    public TransformAction(Model model) {
        this.model = model;
        this.beforeMatrix =  model.getTransformation(0);
        this.currentMatrix =  model.getTransformation(1);
    }

    @Override
    public Model getTarget() {
        return model;
    }

    @Override
    public void undo() {
        // Применяем матрицу для отмены действия
        ModelEditingTools.deformModelFromTransformationMatrix(beforeMatrix, model);
    }

    @Override
    public void redo() {
        // Применяем матрицу для повторения действия
        ModelEditingTools.deformModelFromTransformationMatrix(currentMatrix, model);
    }
}
