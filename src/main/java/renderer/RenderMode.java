package renderer;

public enum RenderMode {
    WIREFRAME,          // только сетка (линии)
    TEXTURE,            // только текстура
    LIGHTING,           // только освещение (по нормалям / ламберт)
    TEXTURE_LIGHTING,   // текстура + освещение
    ALL                 // сетка + текстура + освещение + Z-буфер
}