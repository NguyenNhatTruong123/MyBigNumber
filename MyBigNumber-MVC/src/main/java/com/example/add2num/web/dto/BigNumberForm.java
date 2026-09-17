package com.example.add2num.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Data submitted by the user on the Web form: the two numbers to add (as digit strings).
 * Validation messages are kept in Vietnamese since they are shown directly on the (Vietnamese) UI.
 */
public class BigNumberForm {

    @NotBlank(message = "Vui lòng nhập số thứ nhất")
    @Pattern(regexp = "^[0-9]+$", message = "Số thứ nhất chỉ được chứa chữ số (0-9)")
    private String number1;

    @NotBlank(message = "Vui lòng nhập số thứ hai")
    @Pattern(regexp = "^[0-9]+$", message = "Số thứ hai chỉ được chứa chữ số (0-9)")
    private String number2;

    public String getNumber1() { return number1; }
    public void setNumber1(String number1) { this.number1 = number1; }

    public String getNumber2() { return number2; }
    public void setNumber2(String number2) { this.number2 = number2; }
}
