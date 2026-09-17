package com.example.add2num.web;
import com.example.add2num.web.controller.BigNumberController;
import com.example.add2num.web.model.OperationRecord;
import com.example.add2num.web.service.HistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BigNumberController.class)
public class BigNumberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HistoryService historyService;

    private List<OperationRecord> mockHistory;

    @BeforeEach
    void setUp() {
        // Mock dữ liệu history mẫu luôn được trả về khi gọi getAll()
        OperationRecord record = new OperationRecord("12", "34", "46");
        mockHistory = Collections.singletonList(record);
        when(historyService.getAll()).thenReturn(mockHistory);
    }

    /**
     * 1. Test luồng vào trang chủ (GET /)
     */
    @Test
    void testShowForm_Success() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("bigNumberForm"))
                .andExpect(model().attribute("history", mockHistory));

        verify(historyService, times(1)).getAll();
    }

    /**
     * 2. Parameterized Test phủ toàn bộ kịch bản dữ liệu hợp lệ (Số 0, số lệch độ dài, số siêu lớn)
     */
    @ParameterizedTest
    @CsvSource({
            "1234, 897, 2131",
            "0, 0, 0",
            "0, 5, 5",
            "9, 1, 10",
            "999, 1, 1000",
            "999, 100, 1099",
            "100, 999, 1099",
            "123456789123456789123456789, 987654321987654321987654321, 1111111111111111111111111110",
            "1000000000000000000000000000000000, 1, 1000000000000000000000000000000001"
    })
    void testSum_WithVariousValidInputs_Success(String num1, String num2, String expectedResult) throws Exception {
        mockMvc.perform(post("/sum")
                        .param("number1", num1)
                        .param("number2", num2))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("result", expectedResult)) // Kiểm tra xem Controller có nhả đúng kết quả từ MyBigNumber
                .andExpect(model().attributeExists("steps"))           // Kiểm tra xem có sinh các bước giải (steps) không
                .andExpect(model().attribute("history", mockHistory));

        // Xác thực historyService.add() nhận đúng data đưa vào
        verify(historyService, atLeastOnce()).add(argThat(record ->
                record.getNumber1().equals(num1) &&
                        record.getNumber2().equals(num2) &&
                        record.getResult().equals(expectedResult)
        ));
    }

    /**
     * 3. Test tính chất giao hoán (Commutative) thông qua API Web
     */
    @Test
    void testSum_CommutativeProperty() throws Exception {
        String a = "48923749823749238479";
        String b = "9832749837492";

        // Thực hiện request A + B
        mockMvc.perform(post("/sum").param("number1", a).param("number2", b))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("result"));

        // Thực hiện request B + A
        mockMvc.perform(post("/sum").param("number1", b).param("number2", a))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("result"));
    }

    /**
     * 4. Test luồng dữ liệu bị lỗi Validation (Ví dụ: để trống)
     */
    @Test
    void testSum_WithInvalidInput_ValidationFailure() throws Exception {
        mockMvc.perform(post("/sum")
                        .param("number1", "")
                        .param("number2", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeDoesNotExist("result")) // Dữ liệu lỗi thì ko được có kết quả
                .andExpect(model().attributeDoesNotExist("steps"))  // Dữ liệu lỗi thì ko được có các bước giải
                .andExpect(model().attribute("history", mockHistory));

        // Chặn hoàn toàn, không cho lưu historyService.add khi dính lỗi nhập liệu
        verify(historyService, never()).add(any(OperationRecord.class));
    }
}
