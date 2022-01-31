package splat.executor;

public class ValueString extends Value{
    private String value;

    public ValueString(String value) {
        this.value = value;
    }

    public ValueString() {
        this.value = "";
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
