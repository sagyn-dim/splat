package splat.executor;

public class ValueBool extends Value{
    private boolean value;

    public ValueBool(boolean value) {
        this.value = value;
    }

    public ValueBool() {
        this.value = false;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
