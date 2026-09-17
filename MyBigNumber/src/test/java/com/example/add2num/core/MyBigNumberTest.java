package com.example.add2num.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MyBigNumberTest {

    private final MyBigNumber bigNumber = new MyBigNumber();

    @Test
    void testExampleFromRequirement() {
        // Example taken directly from the requirement document: sum("1234", "897")
        assertEquals("2131", bigNumber.sum("1234", "897"));
    }

    @ParameterizedTest
    @CsvSource({
            "0, 0, 0",
            "0, 5, 5",
            "9, 1, 10",
            "999, 1, 1000",
            "123456789123456789123456789, 987654321987654321987654321, 1111111111111111111111111110",
            "1000000000000000000000000000000000, 1, 1000000000000000000000000000000001"
    })
    void testVariousSums(String a, String b, String expected) {
        assertEquals(expected, bigNumber.sum(a, b));
    }

    @Test
    void testDifferentLengths() {
        assertEquals("1099", bigNumber.sum("999", "100"));
        assertEquals("1099", bigNumber.sum("100", "999"));
    }

    @Test
    void testCommutative() {
        String a = "48923749823749238479";
        String b = "9832749837492";
        assertEquals(bigNumber.sum(a, b), bigNumber.sum(b, a));
    }

    @Test
    void testStepsAreRecordedForLastCall() {
        // 48 + 9 = 57, computed in 2 steps (units column: 8+9=17, tens column: 4+0+carry1=5)
        String result = bigNumber.sum("48", "9");
        assertEquals("57", result);
        assertEquals(2, bigNumber.getLastSteps().size());

        MyBigNumber.AdditionStep step1 = bigNumber.getLastSteps().get(0);
        assertEquals("8", step1.getDigit1());
        assertEquals("9", step1.getDigit2());
        assertEquals(0, step1.getCarryIn());
        assertEquals(7, step1.getResultDigit());
        assertEquals(1, step1.getCarryOut());

        MyBigNumber.AdditionStep step2 = bigNumber.getLastSteps().get(1);
        assertEquals("4", step2.getDigit1());
        assertEquals("-", step2.getDigit2());
        assertEquals(1, step2.getCarryIn());
        assertEquals(5, step2.getResultDigit());
        assertEquals(0, step2.getCarryOut());
    }

    @Test
    void testLastStepsResetEachCall() {
        bigNumber.sum("123456", "1");
        int firstCallSteps = bigNumber.getLastSteps().size();
        bigNumber.sum("1", "2");
        assertEquals(1, bigNumber.getLastSteps().size());
        assertEquals(6, firstCallSteps);
    }
}
