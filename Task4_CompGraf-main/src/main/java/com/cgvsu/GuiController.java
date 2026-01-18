package com.cgvsu;

import com.cgvsu.HistoryBuffer.ActionHistory;
import com.cgvsu.HistoryBuffer.TransformAction;
import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Point2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.*;
import com.cgvsu.model.CalculateNormals;
import com.cgvsu.model.ModelEditingTools;
import com.cgvsu.model.Model;
import com.cgvsu.objWriter.FileDialogHandler;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.render_engine.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import com.cgvsu.scene.Scene;

import javax.imageio.ImageIO;

import static com.cgvsu.render_engine.RenderEngine.render;

public class GuiController {

    private static final float TRANSLATION = 0.5F;
    @FXML
    private Label XLabel;
    @FXML
    private Label XTranslationLabel;
    @FXML
    private Label YTranslationLabel;
    @FXML
    private Label ZTranslationLabel;
    @FXML
    private Label XScaleLabel;
    @FXML
    private Label YScaleLabel;
    @FXML
    private Label ZScaleLabel;
    @FXML
    private Label XRotationLabel;
    @FXML
    private Label YRotationLabel;
    @FXML
    private Label ZRotationLabel;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Canvas canvas;

    @FXML
    private VBox transformationBox;

    @FXML
    private TextField translationX, translationY, translationZ;

    @FXML
    private TextField scaleX, scaleY, scaleZ;

    @FXML
    private TextField rotationX, rotationY, rotationZ;

    @FXML
    private Button applyButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button deletePolygonButton;

    @FXML
    private CheckBox saveDeformationCheckBox;

    @FXML
    private RadioButton useTextureRadioButton;

    @FXML
    private RadioButton useTriangleRadioButton;

    @FXML
    private RadioButton useColorRadioButton;

    @FXML
    private RadioButton useAllColorRadioButton;

    @FXML
    private RadioButton useWithoutColorRadioButton;

    @FXML
    private TextField textFieldCameraPositionX;

    @FXML
    private TextField textFieldCameraPositionY;

    @FXML
    private TextField textFieldCameraPositionZ;

    @FXML
    private TextField textFieldCameraPointOfDirectionX;

    @FXML
    private TextField textFieldCameraPointOfDirectionY;

    @FXML
    private TextField textFieldCameraPointOfDirectionZ;

    @FXML
    private VBox camerasVBox;

    @FXML
    private ToggleButton bindToCameraButton;

    @FXML
    private Button addNewCamera;

    @FXML
    private ComboBox<Model> modelComboBox;

    @FXML
    private Label selectModelLabel;

    @FXML
    private Label translationLabel;

    @FXML
    private Label scaleLabel;

    @FXML
    private Label rotationLabel;

    private Scene scene;

    private Timeline timeline;

    private Model model = new Model();
    private Camera camera = new Camera(
            new Vector3f(0, 0, 100),
            new Vector3f(0, 0, 0),
            1.0f, 1, 0.01f, 1000.0f);

    private boolean isLeftButtonPressed = false;
    private boolean isMiddleButtonPressed = false;
    private double lastMouseX, lastMouseY;
    private boolean isAltPressed = false;
    private boolean isFPressed = false;
    private boolean isDarkTheme = false;
    private ActionHistory historyBuffer;

    boolean isFirstDrawPolygons = false;

    private RenderParameters params = new RenderParameters();

    private final CameraManager cameraManager = new CameraManager();
    boolean isAllColor = false;

    @FXML
    private void initialize() {
        System.out.println("initialize");

        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));
        historyBuffer = new ActionHistory(5);
        scene = new Scene();
        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(100), event -> {
            render();
        });

        cameraManager.getActiveCamera().setAzimuth(0);
        cameraManager.getActiveCamera().setElevation(0);
        cameraManager.getActiveCamera().setDistance(100);

        timeline.getKeyFrames().add(frame);
        timeline.play();

        applyButton.setOnAction(event -> applyTransformation());
        saveButton.setOnAction(event -> saveModel());
        deleteButton.setOnAction(event -> deleteModel());
        deletePolygonButton.setOnAction(event -> deletePolygon());

        addNewCamera.setOnAction(event -> addNewCameraButton());

        useTextureRadioButton.setOnAction(event -> applyTexture(useTextureRadioButton.isSelected()));
        useTriangleRadioButton.setOnAction(event -> applyTriangle(useTriangleRadioButton.isSelected()));
        useColorRadioButton.setOnAction(event -> applyColor(useColorRadioButton.isSelected()));
        useAllColorRadioButton.setOnAction(event -> applyAllColor(useAllColorRadioButton.isSelected()));
        useWithoutColorRadioButton.setOnAction(event -> applyWithoutColor(useWithoutColorRadioButton.isSelected()));
        resetTransformationFields();
        canvas.setOnMouseClicked(event -> canvas.requestFocus());
        canvas.requestFocus();

        modelComboBox.setOnAction(event -> setActiveModel());
        canvas.setOnMousePressed(this::handleMousePressed);
        canvas.setOnMouseReleased(this::handleMouseReleased);
        canvas.setOnMouseDragged(this::handleMouseDragged);
        canvas.setOnScroll(this::handleOnScroll);

        canvas.setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.Z) {
                if (event.isShiftDown()) {
                    historyBuffer.redo();
                } else {
                    historyBuffer.undo();
                }
            }
            if (event.getCode() == KeyCode.F) {
                scene.getActiveCamera().cameraReset();
            }
        });

        updateModelComboBox();
    }

    private void render() {
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
        cameraManager.getActiveCamera().setAspectRatio((float) (width / height));

        RenderEngine.render(canvas.getGraphicsContext2D(), cameraManager.getActiveCamera(), scene, (int) width, (int) height, this.params);
    }

    private void addTexture(Model model) {
        model.loadTexture("/images/123.jpg");
        render();
    }

    private void removeTexture(Model model) {
        model.clearTexture();
        render();
    }

    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = createFileChooser("Model (*.obj)", "*.obj", "Load Model");
        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());

        try {
            String fileContent = Files.readString(fileName);
            Model model = ObjReader.read(fileContent);

            model.setName(file.getName());
            model.setOriginalVertices(model.getVertices());
            scene.addModel(model);
            scene.setActiveModel(model);
            updateModelComboBox();
            modelComboBox.getSelectionModel().select(model);
            model.setOriginalPolygons(model.getPolygons());
            historyBuffer.addAction(new TransformAction(model));
        } catch (IOException exception) {
            System.err.println("Error reading file: " + exception.getMessage());
        }
    }

    private FileChooser createFileChooser(String description, String extension, String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(description, extension));
        fileChooser.setTitle(title);
        File initialDirectory = new File("3DModels");
        if (initialDirectory.exists()) {
            fileChooser.setInitialDirectory(initialDirectory);
        }
        return fileChooser;
    }

    @FXML
    private void applyTransformation() {
        Model activeModel = scene.getActiveModel();
        if (activeModel == null) {
            System.err.println("Error: No active model selected. Please select a model.");
            return;
        }
        try {
            float tX = -1 * Float.parseFloat(translationX.getText());
            float tY = Float.parseFloat(translationY.getText());
            float tZ = Float.parseFloat(translationZ.getText());

            float sX = Float.parseFloat(scaleX.getText());
            float sY = Float.parseFloat(scaleY.getText());
            float sZ = Float.parseFloat(scaleZ.getText());

            float rX = Float.parseFloat(rotationX.getText());
            float rY = Float.parseFloat(rotationY.getText());
            float rZ = Float.parseFloat(rotationZ.getText());

            float[] transformations = {tX, tY, tZ, sX, sY, sZ, rX, rY, rZ};
            System.out.println("Transformations applied: ");
            for (float value : transformations) {
                System.out.print(value + " ");
            }
            System.out.println();

            ArrayList<Vector3f> transformationList = new ArrayList<>();
            transformationList.add(new Vector3f(rX, rY, rZ));
            transformationList.add(new Vector3f(sX, sY, sZ));
            transformationList.add(new Vector3f(tX, tY, tZ));

            ModelEditingTools.deformModelFromRawData(transformationList, activeModel);
            historyBuffer.addAction(new TransformAction(activeModel));

            resetTransformationFields();
        } catch (NumberFormatException e) {
            System.err.println("Invalid input in transformation fields.");
        }
    }

    private void recalculateNormals(ArrayList<Vector3f> trList, Model model) {
        Matrix4f transformationMatrix = GraphicConveyor.scaleRotateTranslate(trList.get(0), trList.get(1), trList.get(2));
        ArrayList<Vector3f> transformedVertices = new ArrayList<>();

        for (Vector3f vertex : model.getOriginalVertices()) {
            Vector3f transformedVertex = GraphicConveyor.multiplyMatrix4ByVector3(transformationMatrix, vertex);
            transformedVertices.add(transformedVertex);
        }

        model.setVertices(transformedVertices);
        model.setNormals(CalculateNormals.calculateNormals(model));
        System.out.println("8888888");
    }

    private void resetTransformationFields() {
        translationX.setText("0");
        translationY.setText("0");
        translationZ.setText("0");

        scaleX.setText("1");
        scaleY.setText("1");
        scaleZ.setText("1");

        rotationX.setText("0");
        rotationY.setText("0");
        rotationZ.setText("0");
    }

    @FXML
    private void saveModel() {
        Model activeModel = scene.getActiveModel();
        if (activeModel == null) {
            System.err.println("Error: No active model selected. Please select a model.");
            return;
        }

        boolean saveDeformation = saveDeformationCheckBox.isSelected();
        FileDialogHandler.saveModel(activeModel, saveDeformation);
    }

    @FXML
    private void deleteModel() {
        Model selectedModel = modelComboBox.getValue();
        if (selectedModel != null) {
            scene.deleteModel(selectedModel);
            updateModelComboBox();
        }
    }

    @FXML
    private void deletePolygon() {
        Model activeModel = scene.getActiveModel();
        if (activeModel == null) {
            System.err.println("Error: No active model selected. Please select a model.");
            return;
        }

        int selectedPolygonIndex = modelComboBox.getSelectionModel().getSelectedIndex();
        if (selectedPolygonIndex < 0 || selectedPolygonIndex >= activeModel.getPolygons().size()) {
            System.err.println("Error: Invalid polygon index.");
            return;
        }

        activeModel.deletePolygon(selectedPolygonIndex);
        render();
    }

    private void applyTriangle(boolean is) {
        System.out.println("applyTriangle");
        useTextureRadioButton.setSelected(false);
        useColorRadioButton.setSelected(false);
        useAllColorRadioButton.setSelected(false);
        useWithoutColorRadioButton.setSelected(false);

        Model activeModel = scene.getActiveModel();

        if (activeModel == null) {
            System.err.println("Error: No active model selected. Please select a model.");
            return;
        }

        if (!isFirstDrawPolygons) {
            model.setOriginalPolygons(activeModel.getPolygons());
        }

        if (is) {
            boolean triangle = useTriangleRadioButton.isSelected();
            boolean color = useColorRadioButton.isSelected();
            boolean texture = useTextureRadioButton.isSelected();
            boolean allcolor = useAllColorRadioButton.isSelected();
            this.params.setAllParams(triangle, color, texture, allcolor);
            ModelTriangulator.setModelTriangulator(activeModel);
            isFirstDrawPolygons = true;
        } else {
            scene.getActiveModel().setPolygons(model.getOriginalPolygons());
        }
    }

    private void applyTexture(boolean is) {
        System.out.println("applyTexture");
        useTriangleRadioButton.setSelected(false);
        useColorRadioButton.setSelected(false);
        useAllColorRadioButton.setSelected(false);
        useWithoutColorRadioButton.setSelected(false);

        Model activeModel = scene.getActiveModel();

        if (activeModel == null) {
            System.err.println("Error: No active model selected. Please select a model.");
            return;
        }

        if (!isFirstDrawPolygons) {
            model.setOriginalPolygons(activeModel.getPolygons());
        }

        double width = canvas.getWidth();
        double height = canvas.getHeight();

        if (is) {
            boolean triangle = useTriangleRadioButton.isSelected();
            boolean color = useColorRadioButton.isSelected();
            boolean texture = useTextureRadioButton.isSelected();
            boolean allcolor = useAllColorRadioButton.isSelected();
            this.params.setAllParams(triangle, color, texture, allcolor);
            isAllColor = false;
            isFirstDrawPolygons = true;

            model.loadTexture("/images/123.jpg");
        } else {
            scene.getActiveModel().setPolygons(model.getOriginalPolygons());
        }
    }

    private void applyColor(boolean is) {
        useTextureRadioButton.setSelected(false);
        useTriangleRadioButton.setSelected(false);
        useAllColorRadioButton.setSelected(false);
        useWithoutColorRadioButton.setSelected(false);

        Model activeModel = scene.getActiveModel();

        if (activeModel == null) {
            System.err.println("Error: No active model selected. Please select a model.");
            return;
        }

        if (!isFirstDrawPolygons) {
            model.setOriginalPolygons(activeModel.getPolygons());
        }

        double width = canvas.getWidth();
        double height = canvas.getHeight();

        if (is) {
            boolean triangle = useTriangleRadioButton.isSelected();
            boolean color = useColorRadioButton.isSelected();
            boolean texture = useTextureRadioButton.isSelected();
            boolean allcolor = useAllColorRadioButton.isSelected();
            this.params.setAllParams(triangle, color, texture, allcolor);

            ModelTriangulator.setModelTriangulator(activeModel);
            isFirstDrawPolygons = true;
        } else {
            scene.getActiveModel().setPolygons(model.getOriginalPolygons());
        }
    }

    private void applyAllColor(boolean is) {
        useTextureRadioButton.setSelected(false);
        useColorRadioButton.setSelected(false);
        useTriangleRadioButton.setSelected(false);
        useWithoutColorRadioButton.setSelected(false);

        Model activeModel = scene.getActiveModel();

        if (activeModel == null) {
            System.err.println("Error: No active model selected. Please select a model.");
            return;
        }

        if (!isFirstDrawPolygons) {
            model.setOriginalPolygons(activeModel.getPolygons());
        }

        double width = canvas.getWidth();
        double height = canvas.getHeight();

        if (is) {
            boolean triangle = useTriangleRadioButton.isSelected();
            boolean color = useColorRadioButton.isSelected();
            boolean texture = useTextureRadioButton.isSelected();
            boolean allcolor = useAllColorRadioButton.isSelected();
            this.params.setAllParams(triangle, color, texture, allcolor);

            isFirstDrawPolygons = true;
        } else {
            boolean triangle = useTriangleRadioButton.isSelected();
            boolean color = useColorRadioButton.isSelected();
            boolean texture = useTextureRadioButton.isSelected();
            boolean allcolor = useAllColorRadioButton.isSelected();
            this.params.setAllParams(triangle, color, texture, allcolor);
            scene.getActiveModel().setPolygons(model.getOriginalPolygons());
        }
    }

    private void applyWithoutColor(boolean is) {
        useTextureRadioButton.setSelected(false);
        useColorRadioButton.setSelected(false);
        useAllColorRadioButton.setSelected(false);
        useTriangleRadioButton.setSelected(false);
        Model activeModel = scene.getActiveModel();

        if (activeModel == null) {
            System.err.println("Error: No active model selected. Please select a model.");
            return;
        }

        if (canvas == null || canvas.getGraphicsContext2D() == null) {
            System.err.println("Error: Canvas or GraphicsContext is null.");
            return;
        }
        if (scene.getActiveCamera() == null) {
            System.err.println("Error: No active camera selected.");
            return;
        }
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        if (is) {
            boolean triangle = useTriangleRadioButton.isSelected();
            boolean color = useColorRadioButton.isSelected();
            boolean texture = useTextureRadioButton.isSelected();
            boolean allcolor = useAllColorRadioButton.isSelected();
            this.params.setAllParams(triangle, color, texture, allcolor);

            scene.getActiveModel().setPolygons(model.getOriginalPolygons());
        }
    }

    public static Image loadImageFromResources(String path) {
        URL url = Main.class.getResource(path);
        if (url == null) {
            System.err.println("Error: Could not find resource: " + path);
            return null;
        }
        InputStream inputStream = Main.class.getResourceAsStream(path);
        if (inputStream == null) {
            System.err.println("Error: Could not get resource input stream: " + path);
            return null;
        }
        try {
            return new Image(inputStream);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return null;
        }
    }

    private void rotateCamera(double deltaX, double deltaY) {
        float sensitivity = 0.5f;
        float azimuth = cameraManager.getActiveCamera().getAzimuth();
        float elevation = cameraManager.getActiveCamera().getElevation();

        azimuth += (float) (deltaX * sensitivity);
        elevation += (float) (deltaY * sensitivity);

        elevation = Math.max(-89, Math.min(89, elevation));
        azimuth = azimuth % 360;
        if (azimuth < 0) azimuth += 360;

        cameraManager.getActiveCamera().setAzimuth(azimuth);
        cameraManager.getActiveCamera().setElevation(elevation);

        cameraManager.getActiveCamera().updatePosition();
    }

    private void panCamera(double deltaX, double deltaY) {
        float panSensitivity = 0.05f;

        Vector3f direction = cameraManager.getActiveCamera().getTarget().sub(cameraManager.getActiveCamera().getPosition());
        direction.normalize();

        Vector3f right = direction.cross(new Vector3f(0, 1, 0));
        right.normalize();
        right.multiply((float) deltaX * panSensitivity);

        Vector3f up = new Vector3f(0, 1, 0);
        up.multiply((float) deltaY * panSensitivity);

        cameraManager.getActiveCamera().setTarget(cameraManager.getActiveCamera().getTarget().add(right).add(up));
        cameraManager.getActiveCamera().updatePosition();
    }

    private void handleOnScroll(ScrollEvent event) {
        double delta = event.getDeltaY();
        float zoomSensitivity = 0.1f;

        float distance = cameraManager.getActiveCamera().getDistance();
        distance -= (float) (delta * zoomSensitivity);
        distance = Math.max(10.0f, distance);
        cameraManager.getActiveCamera().setDistance(distance);
        cameraManager.getActiveCamera().updatePosition();
    }

    private void handleMouseReleased(MouseEvent event) {
        if (!event.isPrimaryButtonDown()) {
            isLeftButtonPressed = false;
        }
    }

    private void handleMousePressed(MouseEvent event) {
        isAltPressed = event.isAltDown();

        if (event.isPrimaryButtonDown()) {
            isLeftButtonPressed = true;
        }
        if (event.isMiddleButtonDown()) {
            isMiddleButtonPressed = true;
        }

        lastMouseX = event.getX();
        lastMouseY = event.getY();
    }

    private void handleMouseDragged(MouseEvent event) {
        double deltaX = event.getX() - lastMouseX;
        double deltaY = event.getY() - lastMouseY;

        if (isAltPressed) {
            if (isLeftButtonPressed) {
                rotateCamera(deltaX, deltaY);
            }
            if (isMiddleButtonPressed) {
                //panCamera(deltaX, deltaY);
            }
        }

        lastMouseX = event.getX();
        lastMouseY = event.getY();
    }

    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        cameraManager.getActiveCamera().movePosition(new Vector3f(0, 0, -TRANSLATION));
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        cameraManager.getActiveCamera().movePosition(new Vector3f(0, 0, TRANSLATION));
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        cameraManager.getActiveCamera().movePosition(new Vector3f(TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        cameraManager.getActiveCamera().movePosition(new Vector3f(-TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        cameraManager.getActiveCamera().movePosition(new Vector3f(0, TRANSLATION, 0));
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        cameraManager.getActiveCamera().movePosition(new Vector3f(0, -TRANSLATION, 0));
    }

    private void setActiveModel() {
        Model selectedModel = modelComboBox.getValue();
        if (selectedModel != null) {
            scene.setActiveModel(selectedModel);
        }
    }

    private void updateModelComboBox() {
        modelComboBox.getItems().clear();
        modelComboBox.getItems().addAll(scene.getModels());
    }

    private void handleMouseClicked(MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
            double x = event.getX();
            double y = event.getY();
            Vector3f point = new Vector3f((float) x, (float) y, 0);
            Model model = scene.getModelAt(point);
            if (model != null) {
                scene.setActiveModel(model);
                modelComboBox.getSelectionModel().select(model);
            }
        }
    }

    @FXML
    private void addNewCameraButton() {
        try {
            float posX = Float.parseFloat(textFieldCameraPositionX.getText());
            float posY = Float.parseFloat(textFieldCameraPositionY.getText());
            float posZ = Float.parseFloat(textFieldCameraPositionZ.getText());

            float dirX = Float.parseFloat(textFieldCameraPointOfDirectionX.getText());
            float dirY = Float.parseFloat(textFieldCameraPointOfDirectionY.getText());
            float dirZ = Float.parseFloat(textFieldCameraPointOfDirectionZ.getText());

            Vector3f position = new Vector3f(posX, posY, posZ);
            Vector3f target = new Vector3f(dirX, dirY, dirZ);

            Camera newCamera = new Camera(
                    position,
                    target,
                    1F,
                    1,
                    25F,
                    2000
            );

            newCamera.setAzimuthAndElevation();

            int cameraId = cameraManager.addCamera(newCamera);
            addCameraControls(cameraId);
            switchToCamera(cameraId);
            updateCameraPosition();
            render();
        } catch (NumberFormatException e) {
            System.err.println("ERROR " + "Input error: incorrect data" +
                    "Enter numeric values for the camera position and direction coordinates and try again");
        }
    }

    private void addCameraControls(int cameraId) {
        Button cameraButton = new Button("Camera " + cameraId);
        cameraButton.setOnAction(event -> switchToCamera(cameraId));

        Button deleteCamera = new Button("Delete");
        deleteCamera.setOnAction(event -> deleteCamera(cameraId));

        HBox cameraControls = new HBox(5, cameraButton, deleteCamera);
        camerasVBox.getChildren().add(cameraControls);
    }

    private void deleteCamera(int cameraId) {
        cameraManager.removeCamera(cameraId);
        camerasVBox.getChildren().removeIf(node -> {
            if (node instanceof HBox hBox) {
                return hBox.getChildren().stream()
                        .anyMatch(child -> child instanceof Button button &&
                                button.getText().equals("Camera " + cameraId));
            }
            return false;
        });
        render();
    }

    private void switchToCamera(int cameraId) {
        cameraManager.setActiveCamera(cameraId);
        Camera activeCamera = cameraManager.getActiveCamera();

        textFieldCameraPositionX.setText(String.valueOf(activeCamera.getPosition().getX()));
        textFieldCameraPositionY.setText(String.valueOf(activeCamera.getPosition().getY()));
        textFieldCameraPositionZ.setText(String.valueOf(activeCamera.getPosition().getZ()));

        Vector3f target = activeCamera.getTarget();
        textFieldCameraPointOfDirectionX.setText(String.valueOf(target.getX()));
        textFieldCameraPointOfDirectionY.setText(String.valueOf(target.getY()));
        textFieldCameraPointOfDirectionZ.setText(String.valueOf(target.getZ()));

        render();
    }

    private void updateCameraPosition() {
        Camera now = cameraManager.getActiveCamera();
        now.updatePosition();
    }

    @FXML
    private void toggleDarkTheme(ActionEvent event) {
        isDarkTheme = !isDarkTheme;
        if (isDarkTheme) {
            anchorPane.setStyle("-fx-background-color: #444444;");
            transformationBox.setStyle("-fx-background-color: #444444;");
            selectModelLabel.setStyle("-fx-text-fill: white;");
            translationLabel.setStyle("-fx-text-fill: white;");
            scaleLabel.setStyle("-fx-text-fill: white;");
            rotationLabel.setStyle("-fx-text-fill: white;");
            applyButton.setStyle("-fx-text-fill: black;");
            deleteButton.setStyle("-fx-text-fill: black;");
            saveDeformationCheckBox.setStyle("-fx-text-fill: white;");
            useTextureRadioButton.setStyle("-fx-text-fill: white;");
            useColorRadioButton.setStyle("-fx-text-fill: white;");
            useAllColorRadioButton.setStyle("-fx-text-fill: white;");
            useTriangleRadioButton.setStyle("-fx-text-fill: white;");
            useWithoutColorRadioButton.setStyle("-fx-text-fill: white;");
            saveButton.setStyle("-fx-text-fill: black;");
            modelComboBox.setStyle("-fx-text-fill: black; -fx-background-color: #ffffff;");
            translationX.setStyle("-fx-text-fill: black; -fx-background-color: #ffffff;");
            translationY.setStyle("-fx-text-fill: black; -fx-background-color: #ffffff;");
            translationZ.setStyle("-fx-text-fill: black; -fx-background-color: #ffffff;");
            scaleX.setStyle("-fx-text-fill: black; -fx-background-color: #ffffff;");
            scaleY.setStyle("-fx-text-fill: black; -fx-background-color: #ffffff;");
            scaleZ.setStyle("-fx-text-fill: black; -fx-background-color: #ffffff;");
            rotationX.setStyle("-fx-text-fill: black; -fx-background-color: #ffffff;");
            rotationY.setStyle("-fx-text-fill: black; -fx-background-color: #ffffff;");
            rotationZ.setStyle("-fx-text-fill: black; -fx-background-color: #ffffff;");
            XTranslationLabel.setStyle("-fx-text-fill: white;");
            YTranslationLabel.setStyle("-fx-text-fill: white;");
            ZTranslationLabel.setStyle("-fx-text-fill: white;");
            XRotationLabel.setStyle("-fx-text-fill: white;");
            YRotationLabel.setStyle("-fx-text-fill: white;");
            ZRotationLabel.setStyle("-fx-text-fill: white;");
            XScaleLabel.setStyle("-fx-text-fill: white;");
            YScaleLabel.setStyle("-fx-text-fill: white;");
            ZScaleLabel.setStyle("-fx-text-fill: white;");
        } else {
            anchorPane.setStyle("-fx-background-color: #ffffff;");
            transformationBox.setStyle("-fx-background-color: #ffffff;");
            selectModelLabel.setStyle("-fx-text-fill: black;");
            translationLabel.setStyle("-fx-text-fill: black;");
            scaleLabel.setStyle("-fx-text-fill: black;");
            rotationLabel.setStyle("-fx-text-fill: black;");
            applyButton.setStyle("-fx-text-fill: black;");
            deleteButton.setStyle("-fx-text-fill: black;");
            saveDeformationCheckBox.setStyle("-fx-text-fill: black;");
            useTextureRadioButton.setStyle("-fx-text-fill: black;");
            useColorRadioButton.setStyle("-fx-text-fill: black;");
            useAllColorRadioButton.setStyle("-fx-text-fill: black;");
            useTriangleRadioButton.setStyle("-fx-text-fill: black;");
            useWithoutColorRadioButton.setStyle("-fx-text-fill: black;");
            saveButton.setStyle("-fx-text-fill: black;");
            modelComboBox.setStyle("-fx-text-fill: black; -fx-background-color: #ffffff;");
            translationX.setStyle("-fx-text-fill: black; -fx-background-color: #ffffff;");
            translationY.setStyle("-fx-text-fill: black; -fx-background-color: #ffffff;");
            translationZ.setStyle("-fx-text-fill: black; -fx-background-color: #ffffff;");
            scaleX.setStyle("-fx-text-fill: black; -fx-background-color: #ffffff;");
            scaleY.setStyle("-fx-text-fill: black; -fx-background-color: #ffffff;");
            scaleZ.setStyle("-fx-text-fill: black; -fx-background-color: #ffffff;");
            rotationX.setStyle("-fx-text-fill: black; -fx-background-color: #ffffff;");
            rotationY.setStyle("-fx-text-fill: black; -fx-background-color: #ffffff;");
            rotationZ.setStyle("-fx-text-fill: black; -fx-background-color: #ffffff;");
            XTranslationLabel.setStyle("-fx-text-fill: black;");
            YTranslationLabel.setStyle("-fx-text-fill: black;");
            ZTranslationLabel.setStyle("-fx-text-fill: black;");
            XRotationLabel.setStyle("-fx-text-fill: black;");
            YRotationLabel.setStyle("-fx-text-fill: black;");
            ZRotationLabel.setStyle("-fx-text-fill: black;");
            XScaleLabel.setStyle("-fx-text-fill: black;");
            YScaleLabel.setStyle("-fx-text-fill: black;");
            ZScaleLabel.setStyle("-fx-text-fill: black;");
        }
    }
}
