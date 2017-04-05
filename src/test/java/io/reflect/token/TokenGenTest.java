package io.reflect.token;

import org.junit.Test;
import org.junit.Assert;

public class TokenGenTest {
    private class TestCase {
        public String secretKey;
        public Parameter[] parameters;
        public String expectedTokenValue;

        public TestCase(String secretKey, Parameter[] parameters, String expectedTokenValue) {
            this.secretKey = secretKey;
            this.parameters = parameters;
            this.expectedTokenValue = expectedTokenValue;
        }
    }


    private static final Parameter[] parameters1 = new Parameter[]{
            new Parameter("Region", Parameter.Op.EQUALS, "Northwest"),
            new Parameter("Employee Type", Parameter.Op.EQUALS, "Human Resources")
    };

    private static final Parameter[] parameters2 = new Parameter[]{
            new Parameter("Name", Parameter.Op.NOT_EQUALS, "Bill"),
            new Parameter("Hobbies", Parameter.Op.CONTAINS, new String[]{"Fishing"})
    };

    private static final Parameter[] parameters3 = new Parameter[]{
            new Parameter("Name", Parameter.Op.NOT_EQUALS, "Bill")
    };

    private final TestCase[] testCases = new TestCase[]{
            new TestCase("a1b2c3d4", parameters1, "=2=mD8u93SxbcwoZfqtYrNNlf6vGxLWW/TyCQ3Pj5gI+Bk="),
            new TestCase("some-much-longer-token", parameters1, "=2=yGGBNT5ADaZlWJlQcut8GeR5SZ7oSaw+4vkG8XsXntE="),
            new TestCase("a1b2c3d4", parameters2, "=2=wKwx8xIARl4CFVw1+nvPo/XmgrB+N7Fh6p5EkfwjQa0="),
            new TestCase("some-much-longer-token", parameters2, "=2=aWWfIEUJBeP3Pz2Sd8EyyLkQ2q6Lx06v0mEXNMk68ls=")
    };

    private final TestCase[] failCases = new TestCase[]{
            new TestCase("a1b2c3d4", parameters3, "=2=mD8u93SxbcwoZfqtYrNNlf6vGxLWW/TyCQ3Pj5gI+Bk=")
    };

    @Test
    public void testTokens() {
        for (TestCase testCase : testCases) {
            Assert.assertEquals(testCase.expectedTokenValue, TokenGenerator.generate(testCase.secretKey, testCase.parameters));
        }

        for (TestCase testCase : failCases) {
            Assert.assertNotEquals(testCase.expectedTokenValue, TokenGenerator.generate(testCase.secretKey, testCase.parameters));
        }
    }
}