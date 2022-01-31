package splat.executor;

public class ValueInt extends Value{
    private int value;

    public ValueInt(int value) {
        this.value = value;
    }

    public ValueInt() {
        this.value = 0;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
