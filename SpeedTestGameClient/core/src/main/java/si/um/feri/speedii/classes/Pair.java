package si.um.feri.speedii.classes;

public class Pair<T1, T2> {
    private T1 key;
    private T2 value;

    public Pair(T1 key, T2 value) {
        this.key = key;
        this.value = value;
    }

    public T1 getFirst() {
        return key;
    }

    public T2 getSecond() {
        return value;
    }

    public void setFirst(T1 key) {
        this.key = key;
    }

    public void setSecond(T2 value) {
        this.value = value;
    }
}
