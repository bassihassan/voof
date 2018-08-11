package me.bassihassan.core;

public class CellError {
    private String index;
    private String value;
    private String cause;

    public CellError() {
    }

    public CellError(String index, String value, String cause) {
        this.index = index;
        this.value = value;
        this.cause = cause;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    @Override
    public String toString() {
        return "CellError{" +
                "index='" + index + '\'' +
                ", value='" + value + '\'' +
                ", cause='" + cause + '\'' +
                '}';
    }
}
