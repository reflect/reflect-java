package io.reflect.token;

public class Main {
    public static void main(String[] args) {
        Parameter regionParam = new Parameter("Region", Parameter.Op.EQUALS, "Northwest");
        System.out.println(TokenGenerator.generate("a1b2c3", new Parameter[]{regionParam}));
    }
}