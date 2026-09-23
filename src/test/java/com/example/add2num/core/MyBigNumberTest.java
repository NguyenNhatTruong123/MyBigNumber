package com.example.add2num.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigInteger;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
            "000, 00000, 0",
            "0007, 0000, 7",
            "0000, 0007, 7",
            "0009, 1, 10",
            "0999, 0001, 1000",
            "1, 99999, 100000",
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

    @Test
    void testFinalCarryStepIsRecorded() {
        assertEquals("1000", bigNumber.sum("999", "1"));
        assertEquals(4, bigNumber.getLastSteps().size());

        MyBigNumber.AdditionStep finalStep = bigNumber.getLastSteps().get(3);
        assertEquals(4, finalStep.getStepNumber());
        assertEquals("-", finalStep.getDigit1());
        assertEquals("-", finalStep.getDigit2());
        assertEquals(1, finalStep.getCarryIn());
        assertEquals(1, finalStep.getColumnSum());
        assertEquals(1, finalStep.getResultDigit());
        assertEquals(0, finalStep.getCarryOut());
    }

    @Test
    void testLeadingZerosDoNotRemoveCalculationSteps() {
        assertEquals("10", bigNumber.sum("0009", "1"));
        assertEquals(4, bigNumber.getLastSteps().size());
        assertEquals("0", bigNumber.getLastSteps().get(3).getDigit1());
        assertEquals(0, bigNumber.getLastSteps().get(3).getResultDigit());
    }

    @Test
    void testLongCarryChain() {
        String nines = "9".repeat(10_000);
        assertEquals("1" + "0".repeat(10_000), bigNumber.sum(nines, "1"));
        assertEquals(10_001, bigNumber.getLastSteps().size());
    }

    @Test
    void testSumsMatchBigInteger() {
        Random random = new Random(20260922L);
        String a;
        String b;
        String expected;
        int sample;

        for (sample = 0; sample < 200; sample++) {
            a = "0".repeat(random.nextInt(5))
                    + new BigInteger(1 + random.nextInt(2048), random).toString();
            b = "0".repeat(random.nextInt(5))
                    + new BigInteger(1 + random.nextInt(2048), random).toString();
            expected = new BigInteger(a).add(new BigInteger(b)).toString();
            assertEquals(expected, bigNumber.sum(a, b));
        }
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            " ", " 12", "12 ", "1 2", "1\t2", "12\n",
            "-1", "+1", "1.2", "x123", "12x3", "123x", "/", ":",
            "\u0661", "\uFF11"
    })
    void testInvalidOperandsAreRejectedAndStepsCleared(String invalid) {
        bigNumber.sum("48", "9");
        assertThrows(IllegalArgumentException.class, () -> bigNumber.sum(invalid, "7"));
        assertTrue(bigNumber.getLastSteps().isEmpty());

        bigNumber.sum("48", "9");
        assertThrows(IllegalArgumentException.class, () -> bigNumber.sum("7", invalid));
        assertTrue(bigNumber.getLastSteps().isEmpty());

        assertEquals("5", bigNumber.sum("2", "3"));
        assertEquals(1, bigNumber.getLastSteps().size());
    }
}
