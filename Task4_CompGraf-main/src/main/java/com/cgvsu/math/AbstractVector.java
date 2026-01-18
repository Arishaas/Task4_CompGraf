package com.cgvsu.math;



// todo навести красоту, сейчас мне впадлу...
public abstract class AbstractVector {
    protected float[] components;
    protected float length;



    public AbstractVector(float... components) {
        if (components.length != getSize()) {
            throw new IndexOutOfBoundsException("Неверная длина");
        }
        this.components = components;
        calcLength();
    }


    public AbstractVector() {
        components = new float[getSize()];
        for (int i = 0; i < getSize(); i++) {
            this.components[i] = 0;
        }
        calcLength();
    }



    protected void calcLength() {
        float res = 0;
        for (int i = 0; i < getSize(); i++) {
            res += (components[i] * components[i]);
        }
        length = (float) Math.sqrt(res);
    }

    protected abstract int getSize();

    protected abstract AbstractVector instantiateVector(float[] elements);

    // Вычисление длины вектора ... пол факту, тут надо поставить каклкДлины, на всякий, но опять же... мне впадлу
    public float length() {
        calcLength();
        return length;
    }


    public AbstractVector add(AbstractVector other) {
        return addVector(other);
    }

    private AbstractVector addVector(AbstractVector other) {
        equalsLength(other);
        float[] res = new float[getSize()];
        for (int i = 0; i < getSize(); i++) {
            res[i] = this.components[i] + other.components[i];
        }
        return instantiateVector(res);
    }



    public void addV(AbstractVector other) {
        this.components = addVector(other).components;
    }


    private AbstractVector subVector(AbstractVector other) {
        equalsLength(other);
        float[] res = new float[getSize()];
        for (int i = 0; i < getSize(); i++) {
            res[i] = this.components[i] - other.components[i];
        }
        return instantiateVector(res);
    }


    public AbstractVector sub(AbstractVector other) {
        return subVector(other);
    }

    public void subV(AbstractVector other) {
        this.components = subVector(other).components;
    }

    // я дол сих пор хз. что это...
    // todo понять, что это нахуй такое и надо ли оно :? ... Хз что это, но пусть будет.. мало ли
    public void sub(AbstractVector first, AbstractVector second) {
        equalsLength(first);
        equalsLength(second);
        for (int i = 0; i < getSize(); i++) {
            this.components[i] = first.components[i] - second.components[i];
        }
    }


    public void multiply(float scalar) {
        for (int i = 0; i < components.length; i++) {
            this.components[i] *= scalar;
        }
        calcLength();
    }


    public AbstractVector multiplyV(float scalar) {
        AbstractVector res = instantiateVector(this.components);
        res.multiply(scalar);
        return res;
    }



    public void divide(float scalar) {
        if (scalar == 0) {
            throw new ArithmeticException("Деление на ноль");
        }
        for (int i = 0; i < components.length; i++) {
            this.components[i] /= scalar;
        }
        calcLength();
    }


    public AbstractVector divideV(float scalar) {
        AbstractVector res = instantiateVector(this.components);
        res.divide(scalar);
        return res;
    }


    public float dot(AbstractVector other) {
        equalsLength(other);
        float res = 0;
        for (int i = 0; i < getSize(); i++) {
            res += (this.components[i] * other.components[i]);
        }
        return res;
    }

    public void normalize() {
        calcNormalize();
    }

    public AbstractVector normalizeV() {
        AbstractVector res = instantiateVector(this.components);
        res.normalize();
        return res;
    }

    public boolean positiveVector(){
        for (int i = 0; i < components.length; i++) {
            if (components[i]<=0){
                return false;
            }
        }
        return true;
    }


    public float getNum(int a) {
        if (a < 0 || a >= components.length) {
            throw new IndexOutOfBoundsException("Invalid index: " + a);
        }
        return components[a];
    }



    public void setNum(int a, float num) {
        if (a < 0 || a >= components.length) {
        }
        components[a] = num;
        calcLength();
    }

    public void setElements(float... component) {
        if (component.length != components.length){
            throw new IndexOutOfBoundsException("Invalid length: ");
        }
        this.components = component;
    }



    private void equalsLength(AbstractVector other) {
        if (this.components.length != other.components.length) {
            throw new IndexOutOfBoundsException("Разная длина");
        }
    }


    private void calcNormalize() {
        calcLength();
        if (length == 0) {
            return;
        }
        divide(length);
    }

}