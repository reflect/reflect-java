package io.reflect.token;

import java.io.IOException;
import java.util.Arrays;

import net.minidev.json.JSONAwareEx;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStreamAwareEx;
import net.minidev.json.JSONStyle;

public class Parameter implements JSONAwareEx, JSONStreamAwareEx {
    public enum Op {
        EQUALS("="),
        NOT_EQUALS("!="),
        GREATER_THAN(">"),
        LESS_THAN("<"),
        GREATER_THAN_OR_EQUAL_TO(">="),
        LESS_THAN_OR_EQUAL_TO("<="),
        CONTAINS("=~"),
        NOT_CONTAINS("!~");

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

    public String toJSONString() {
        return toJSONObject().toJSONString();
    }

    public String toJSONString(JSONStyle compression) {
        return toJSONObject().toJSONString(compression);
    }

    public void writeJSONString(Appendable out) throws IOException {
        toJSONObject().writeJSONString(out);
    }

    public void writeJSONString(Appendable out, JSONStyle compression) throws IOException {
        toJSONObject().writeJSONString(out, compression);
    }

    private JSONObject toJSONObject() {
        JSONObject object = new JSONObject();
        object.put("field", this.field);
        object.put("op", this.op.toString());
        object.put("value", this.value);
        object.put("any", this.anyValue);

        return object;
    }
}
