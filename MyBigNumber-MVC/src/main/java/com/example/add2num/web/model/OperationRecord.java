package com.example.add2num.web.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Records one past addition operation, to be displayed on the Web UI's history table. */
public class OperationRecord {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final LocalDateTime timestamp;
    private final String number1;
    private final String number2;
    private final String result;

    public OperationRecord(String number1, String number2, String result) {
        this.timestamp = LocalDateTime.now();
        this.number1 = number1;
        this.number2 = number2;
        this.result = result;
    }

    public String getFormattedTimestamp() { return timestamp.format(FORMATTER); }
    public String getNumber1() { return number1; }
    public String getNumber2() { return number2; }
    public String getResult() { return result; }
}
