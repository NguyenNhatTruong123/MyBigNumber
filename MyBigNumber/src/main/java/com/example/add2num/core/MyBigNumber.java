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
 * Assumption (per the requirement): the input parameters only contain valid
 * digits (0-9); this class does not need to validate/handle bad input data.
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
     */
    public String sum(String stn1, String stn2) {
        lastSteps.clear();
        log.info("Starting addition: \"{}\" + \"{}\"", stn1, stn2);

        StringBuilder result = new StringBuilder();
        int i = stn1.length() - 1; // pointer walking stn1 from right to left
        int j = stn2.length() - 1; // pointer walking stn2 from right to left
        int carry = 0;
        int stepNo = 1;

        while (i >= 0 || j >= 0 || carry > 0) {
            Character c1 = i >= 0 ? stn1.charAt(i) : null;
            Character c2 = j >= 0 ? stn2.charAt(j) : null;
            int d1 = c1 != null ? (c1 - '0') : 0;
            int d2 = c2 != null ? (c2 - '0') : 0;

            int columnSum = d1 + d2 + carry;
            int resultDigit = columnSum % 10;
            int carryOut = columnSum / 10;

            result.insert(0, resultDigit);

            AdditionStep step = new AdditionStep(stepNo, c1, c2, carry, columnSum, resultDigit, carryOut);
            lastSteps.add(step);
            log.info(step.describe());

            carry = carryOut;
            i--;
            j--;
            stepNo++;
        }

        String finalResult = stripLeadingZeros(result.toString());
        log.info("Result: \"{}\" + \"{}\" = \"{}\"", stn1, stn2, finalResult);
        return finalResult;
    }

    /** Strips redundant leading zeros from the result string (e.g. "007" -> "7"), keeping at least 1 digit. */
    private String stripLeadingZeros(String s) {
        int idx = 0;
        while (idx < s.length() - 1 && s.charAt(idx) == '0') {
            idx++;
        }
        return s.substring(idx);
    }

    /** Returns the list of steps (progress) of the most recent sum() call, used to render the Web UI. */
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
