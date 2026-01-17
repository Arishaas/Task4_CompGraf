package app;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import renderer.*;

import java.io.File;

public class MainController {
    @FXML private ImageView viewport;
    @FXML private ComboBox<RenderMode> modeCombo;
    @FXML private Button nextCameraBtn, prevCameraBtn;
    @FXML private CheckBox chkWireframe, chkTexture, chkLighting;
    @FXML private ColorPicker colorPicker;
    @FXML private Label cameraLabel;

    private final RenderEngine engine = new RenderEngine();
    private final CameraManager cameraManager = new CameraManager();
    private WritableImage frame;

    private RenderEngine.ModelAdapter modelAdapter;
    private RenderEngine.Mat4 modelMatrix;
    private Image texture;

    @FXML
    public void initialize() {
        // Настройка ComboBox
        modeCombo.getItems().setAll(RenderMode.values());
        modeCombo.getSelectionModel().select(RenderMode.ALL);

        // Добавление начальных камер
        addDefaultCameras();

        // Настройка обработчиков
        setupEventHandlers();

        // Создание буфера для отрисовки
        createFrameBuffer(800, 600);

        updateCameraLabel();
    }

    private void addDefaultCameras() {
        // Камера 1 - спереди
        cameraManager.addCamera(new Camera(
                new RenderEngine.Vec3(0, 0, 5),
                new RenderEngine.Vec3(0, 0, 0),
                new RenderEngine.Vec3(0, 1, 0),
                (float) Math.toRadians(60),
                16f/9f, 0.1f, 100f
        ));

        // Камера 2 - сверху
        cameraManager.addCamera(new Camera(
                new RenderEngine.Vec3(0, 5, 0),
                new RenderEngine.Vec3(0, 0, 0),
                new RenderEngine.Vec3(0, 0, -1),
                (float) Math.toRadians(60),
                16f/9f, 0.1f, 100f
        ));

        // Камера 3 - сбоку
        cameraManager.addCamera(new Camera(
                new RenderEngine.Vec3(5, 0, 0),
                new RenderEngine.Vec3(0, 0, 0),
                new RenderEngine.Vec3(0, 1, 0),
                (float) Math.toRadians(60),
                16f/9f, 0.1f, 100f
        ));
    }

    private void setupEventHandlers() {
        nextCameraBtn.setOnAction(e -> {
            switchCamera(1);
            updateCameraLabel();
        });

        prevCameraBtn.setOnAction(e -> {
            switchCamera(-1);
            updateCameraLabel();
        });

        modeCombo.setOnAction(e -> renderFrame());

        // Если используешь галочки вместо ComboBox
        chkWireframe.setOnAction(e -> updateModeFromCheckboxes());
        chkTexture.setOnAction(e -> updateModeFromCheckboxes());
        chkLighting.setOnAction(e -> updateModeFromCheckboxes());
        colorPicker.setOnAction(e -> renderFrame());
    }

    private void switchCamera(int direction) {
        int size = cameraManager.size();
        if (size == 0) return;
        int idx = cameraManager.getActiveIndex();
        idx = (idx + direction + size) % size;
        cameraManager.setActiveIndex(idx);
        renderFrame();
    }

    private void updateCameraLabel() {
        int current = cameraManager.getActiveIndex() + 1;
        int total = cameraManager.size();
        cameraLabel.setText("Камера: " + current + "/" + total);
    }

    private void updateModeFromCheckboxes() {
        boolean wire = chkWireframe.isSelected();
        boolean tex = chkTexture.isSelected();
        boolean light = chkLighting.isSelected();

        // Определяем режим по галочкам
        RenderMode mode;
        if (!wire && !tex && !light) {
            // Ничего не выбрано - статический цвет
            mode = null;
        } else if (tex && light) {
            mode = RenderMode.TEXTURE_LIGHTING;
        } else if (tex) {
            mode = RenderMode.TEXTURE;
        } else if (light) {
            mode = RenderMode.LIGHTING;
        } else if (wire) {
            mode = RenderMode.WIREFRAME;
        } else {
            mode = RenderMode.ALL;
        }

        if (mode != null) {
            modeCombo.getSelectionModel().select(mode);
        }
        renderFrame();
    }

    private void createFrameBuffer(int width, int height) {
        frame = new WritableImage(width, height);
        viewport.setImage(frame);
        viewport.setFitWidth(width);
        viewport.setFitHeight(height);
    }

    public void renderFrame() {
        if (frame == null || modelAdapter == null) {
            // Если модель не загружена, просто очищаем экран
            if (frame != null) {
                engine.clear(frame, Color.rgb(20, 20, 30), true);
            }
            return;
        }

        int w = (int) frame.getWidth();
        int h = (int) frame.getHeight();

        // Получаем текущую камеру и обновляем aspect ratio
        Camera cam = cameraManager.getActive();
        Camera adjustedCam = new Camera(
                cam.eye, cam.target, cam.up,
                cam.fovYRadians,
                (float) w / h,
                cam.near, cam.far
        );

        // Очищаем экран
        engine.clear(frame, Color.rgb(20, 20, 30), true);

        // Получаем режим рендеринга
        RenderMode mode = modeCombo.getSelectionModel().getSelectedItem();

        // Если ни одна галочка не выбрана - используем статический цвет
        if (!chkWireframe.isSelected() && !chkTexture.isSelected() && !chkLighting.isSelected()) {
            // Заливаем цветом из colorPicker
            engine.clear(frame, colorPicker.getValue(), true);
            // Можно добавить простую отрисовку одним цветом
        } else {
            // Рендерим модель
            engine.renderModel(modelAdapter, modelMatrix,
                    adjustedCam, frame, mode, texture);
        }
    }

    // ====================== ПУБЛИЧНЫЕ МЕТОДЫ ДЛЯ ВНЕШНЕГО ВЫЗОВА ======================

    /**
     * Установить модель для отрисовки.
     */
    public void setModelAdapter(RenderEngine.ModelAdapter adapter) {
        this.modelAdapter = adapter;
        // Единичная матрица модели (без преобразований)
        this.modelMatrix = new RenderEngine.Mat4(new float[]{
                1,0,0,0, 0,1,0,0, 0,0,1,0, 0,0,0,1
        });
        renderFrame();
    }

    /**
     * Загрузить текстуру из файла.
     */
    public void loadTexture(File file) {
        if (file != null && file.exists()) {
            this.texture = new Image("file:" + file.getAbsolutePath());
            chkTexture.setSelected(true);
            renderFrame();
        }
    }

    /**
     * Изменить размер буфера отрисовки.
     */
    public void resizeFrame(int width, int height) {
        createFrameBuffer(width, height);
        renderFrame();
    }

    /**
     * Добавить новую камеру.
     */
    public void addCamera(Camera camera) {
        cameraManager.addCamera(camera);
        updateCameraLabel();
        renderFrame();
    }

    /**
     * Удалить текущую камеру.
     */
    public void removeCurrentCamera() {
        int idx = cameraManager.getActiveIndex();
        if (idx >= 0) {
            cameraManager.removeCamera(idx);
            updateCameraLabel();
            renderFrame();
        }
    }

    /**
     * Получить текущий режим рендеринга.
     */
    public RenderMode getCurrentRenderMode() {
        return modeCombo.getSelectionModel().getSelectedItem();
    }

    /**
     * Установить режим рендеринга.
     */
    public void set