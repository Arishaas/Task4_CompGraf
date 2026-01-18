package com.cgvsu.HistoryBuffer;

public interface Action<T> {
    T getTarget(); // Возвращает объект, над которым выполняется действие
    void undo();   // Отмена действия
    void redo();   // Повтор действия
}
