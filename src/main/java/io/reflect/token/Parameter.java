package io.reflect.token;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;

public class Parameter {
    public enum Op {
        EQUALS("="),
        NOT_EQUALS("!="),
        GREATER_THAN(">"),
        LESS_THAN("<"),
        GREATER_THAN_OR_EQUAL_TO(">="),
        LESS_THAN_OR_EQUAL_TO("<="),
        CONTAINS("=~");

        private final String stringValue;

        Op(final String val) {
            stringValue = val;
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }

    private String field;
    private String value;
    private Op op;
    private String[] anyValue;

    public Parameter(String field, Op op, String value) {
        this.field = field;
        this.op = op;
        this.value = value;
        this.anyValue = new String[0];
    }

    public Parameter(String field, Op op, String[] anyValue) {
        Arrays.sort(anyValue);

        this.field = field;
        this.op = op;
        this.value = "";
        this.anyValue = anyValue;
    }

    @Override
    public String toString() {
        Gson serializer = new GsonBuilder()
                .disableHtmlEscaping()
                .create();
        Object[] s = new Object[]{
                this.field, this.op.toString(), this.value, this.anyValue
        };
        return serializer.toJson(s);
    }
}
