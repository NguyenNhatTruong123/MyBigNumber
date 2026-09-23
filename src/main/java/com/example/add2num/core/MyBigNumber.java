package com.example.add2num.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Core class implementing the addition of two very large natural numbers,
 * represented as strings, using the exact same "carry" addition algorithm
 * taught in elementary school:
 * walk both strings simultaneously from right to left, extract each character,
 * convert it to a digit, add the digits together with the carry, and store the
 * units digit of that column's sum into the result.
 *
 * Inputs must be non-empty strings containing only ASCII digits (0-9).
 * Each character is validated as its column is added, without a separate scan.
 */
public class MyBigNumber {

    private static final Logger log = LoggerFactory.getLogger(MyBigNumber.class);

    /** Stores each step (each column) of the most recent sum() call, used to display progress. */
    private final List<AdditionStep> lastSteps = new ArrayList<>();

    /**
     * Adds two very large natural numbers stn1 and stn2 (as digit strings).
     *
     * @param stn1 the first operand
     * @param stn2 the second operand
     * @return the sum of stn1 and stn2, as a digit string
     * @throws IllegalArgumentException if either operand is null, empty, or contains a non-digit
     */
    public String sum(String stn1, String stn2) {
        lastSteps.clear();
        if (stn1 == null || stn2 == null || stn1.isEmpty() || stn2.isEmpty()) {
            throw new IllegalArgumentException("Both numbers must be non-null, non-empty digit strings (0-9).");
        }
        log.info("Starting addition: \"{}\" + \"{}\"", stn1, stn2);

        // Reserve one extra column for the final carry and fill from right to left.
        char[] result = new char[Math.max(stn1.length(), stn2.length()) + 1];
        int resultStart = result.length;
        int i = stn1.length() - 1; // pointer walking stn1 from right to left
        int j = stn2.length() - 1; // pointer walking stn2 from right to left
        int carry = 0;
        int stepNo = 1;
        Character c1 = null;
        Character c2 = null;
        int d1 = 0;
        int d2 = 0;
        int columnSum = 0;
        int resultDigit = 0;
        int carryOut = 0;
        AdditionStep step;

        while (i >= 0 || j >= 0 || carry > 0) {
            c1 = i >= 0 ? stn1.charAt(i) : null;
            c2 = j >= 0 ? stn2.charAt(j) : null;
            if ((c1 != null && (c1 < '0' || c1 > '9'))
                    || (c2 != null && (c2 < '0' || c2 > '9'))) {
                lastSteps.clear();
                throw new IllegalArgumentException("Both numbers must contain digits only (0-9).");
            }
            d1 = c1 != null ? (c1 - '0') : 0;
            d2 = c2 != null ? (c2 - '0') : 0;

            columnSum = d1 + d2 + carry;
            resultDigit = columnSum % 10;
            carryOut = columnSum / 10;

            result[--resultStart] = (char) ('0' + resultDigit);

            step = new AdditionStep(stepNo, c1, c2, carry, columnSum, resultDigit, carryOut);
            lastSteps.add(step);
            log.info(step.describe());

            carry = carryOut;
            i--;
            j--;
            stepNo++;
        }

        // Skip redundant leading zeros in the buffer, keeping at least one digit.
        while (resultStart < result.length - 1 && result[resultStart] == '0') {
            resultStart++;
        }
        String finalResult = new String(result, resultStart, result.length - resultStart);
        log.info("Result: \"{}\" + \"{}\" = \"{}\"", stn1, stn2, finalResult);
        return finalResult;
    }

    /** Returns the most recent sum() call's steps, or an empty list if its input was invalid. */
    public List<AdditionStep> getLastSteps() {
        return Collections.unmodifiableList(lastSteps);
    }

    /**
     * One step (one column) of the carry-addition process.
     * Used to: (1) log the operation history, (2) display the calculation progress on the Web UI.
     */
    public static class AdditionStep {
        private final int stepNumber;
        private final Character digit1;
        private final Character digit2;
        private final int carryIn;
        private final int columnSum;
        private final int resultDigit;
        private final int carryOut;

        public AdditionStep(int stepNumber, Character digit1, Character digit2,
                             int carryIn, int columnSum, int resultDigit, int carryOut) {
            this.stepNumber = stepNumber;
            this.digit1 = digit1;
            this.digit2 = digit2;
            this.carryIn = carryIn;
            this.columnSum = columnSum;
            this.resultDigit = resultDigit;
            this.carryOut = carryOut;
        }

        public int getStepNumber() { return stepNumber; }
        /** "-" when that string has run out of digits (one number shorter than the other). */
        public String getDigit1() { return digit1 != null ? digit1.toString() : "-"; }
        public String getDigit2() { return digit2 != null ? digit2.toString() : "-"; }
        public int getCarryIn() { return carryIn; }
        public int getColumnSum() { return columnSum; }
        public int getResultDigit() { return resultDigit; }
        public int getCarryOut() { return carryOut; }

        public String describe() {
            return String.format(
                    "Step %d: %s + %s (carry-in %d) = %d -> store %d in the result, carry-out %d",
                    stepNumber, getDigit1(), getDigit2(), carryIn, columnSum, resultDigit, carryOut);
        }
    }
}
