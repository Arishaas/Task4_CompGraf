package com.cgvsu.HistoryBuffer;

public class ActionHistory<T extends Action<?>> {
    private static class ActionNode<T> {
        T action;
        ActionNode<T> prev;
        ActionNode<T> next;

        ActionNode(T action) {
            this.action = action;
        }

        @Override
        public String toString() {
            return "ActionNode{action=" + action + "}";
        }
    }

    private ActionNode<T> head;
    private ActionNode<T> tail;
    private ActionNode<T> current;
    private final int maxDepth;
    private int size;

    public ActionHistory(int maxDepth) {
        this.maxDepth = maxDepth;
        this.size = 0;
        debugLog("Initialized ActionHistory");
    }

    // Добавление нового действия
// Добавление нового действия
    public void addAction(T action) {
        ActionNode<T> newNode = new ActionNode<>(action);

        // Если это первое действие
        if (head == null) {
            head = newNode;
            tail = newNode;
            current = head;
        } else {
            // Удаляем все действия, которые идут после current
            if (current != null && current.next != null) {
                ActionNode<T> temp = current.next;
                while (temp != null) {
                    ActionNode<T> next = temp.next;
                    temp.prev = null; // Очищаем ссылки узла
                    temp.next = null;
                    temp = next;
                    size--; // Уменьшаем размер за каждое удалённое действие
                }
                current.next = null; // Устанавливаем конец списка на current
                tail = current; // Обновляем tail
            }

            // Добавляем новое действие
            newNode.prev = current;
            if (current != null) {
                current.next = newNode;
            }
            current = newNode; // Перемещаем current на новое действие
            tail = newNode; // Обновляем tail
        }

        size++;

        // Удаляем старые действия, если превышена maxDepth
        if (size > maxDepth) {
            head = head.next;
            if (head != null) {
                head.prev = null;
            }
            size--;
        }

        debugLog("Action added");
    }




    // Отмена действия
    public void undo() {
        if (current == null) {
            debugLog("Undo failed: no action to undo");
            return;
        }

        if (current == head) { // Если уже на первом элементе
            debugLog("Undo failed: already at the first action");
            return;
        }

        current.action.undo(); // Выполняем отмену действия
        current = current.prev; // Смещаем current назад
        debugLog("Undo performed");
    }

    // Повтор действия
    public void redo() {
        if (current == null || current.next == null) { // Если на последнем элементе или нет действий для повтора
            debugLog("Redo failed: no action to redo");
            return;
        }

        current = current.next; // Смещаем current вперёд
        current.action.redo(); // Выполняем повтор действия
        debugLog("Redo performed");
    }


    // Удаление некорректного действия
    public void removeInvalid(ActionNode<T> node) {
        if (node == null) return;

        if (node == head) {
            head = head.next;
            if (head != null) head.prev = null;
        } else if (node == tail) {
            tail = tail.prev;
            if (tail != null) tail.next = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        size--;
        debugLog("Invalid action removed");
    }

    // Логирование состояния
    private void debugLog(String message) {
        System.out.println("\n[DEBUG] " + message);
        System.out.println("Current state of ActionHistory:");
        System.out.println("  Size: " + size);
        System.out.println("  Max Depth: " + maxDepth);

        System.out.print("  Nodes: ");
        ActionNode<T> temp = head;
        while (temp != null) {
            if (temp == current) {
                System.out.print("[*Current* -> " + temp + "] ");
            } else {
                System.out.print(temp + " ");
            }
            temp = temp.next;
        }
        System.out.println("\n  Head: " + (head != null ? head : "null"));
        System.out.println("  Tail: " + (tail != null ? tail : "null"));
        System.out.println("  Current: " + (current != null ? current : "null"));
        System.out.println("------------------------------------");
    }
}
