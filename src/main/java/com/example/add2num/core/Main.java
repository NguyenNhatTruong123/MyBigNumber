package com.example.add2num.core;

import java.util.Scanner;
import java.util.List;

/**
 * Entry point to run the two-large-numbers addition program STANDALONE,
 * without needing the Web module (MyBigNumber-MVC). Useful for shipping this
 * module as its own console program/library (e.g. pushed to a "core" branch).
 *
 * Usage:
 *   java -jar MyBigNumber-0.0.1-jar-with-dependencies.jar 1234 897
 * or run without arguments to type the numbers interactively:
 *   java -jar MyBigNumber-0.0.1-jar-with-dependencies.jar
 */
public class Main {

    public static void main(String[] args) {
        String stn1;
        String stn2;

        if (args.length >= 2) {
            stn1 = args[0];
            stn2 = args[1];
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the first number: ");
            stn1 = scanner.nextLine().trim();
            System.out.print("Enter the second number: ");
            stn2 = scanner.nextLine().trim();
        }

        MyBigNumber myBigNumber = new MyBigNumber();
        String result;
        try {
            result = myBigNumber.sum(stn1, stn2);
        } catch (IllegalArgumentException ex) {
            System.err.println("Error: " + ex.getMessage());
            System.exit(1);
            return;
        }

        System.out.println();
        System.out.println("Calculation progress (see the log lines above as well, via SLF4J):");
        List<String> descriptions = myBigNumber.describeLastSteps();
        int index;
        for (index = 0; index < descriptions.size(); index++) {
            System.out.println("  " + descriptions.get(index));
        }

        System.out.println();
        System.out.println(stn1 + " + " + stn2 + " = " + result);
    }
}
