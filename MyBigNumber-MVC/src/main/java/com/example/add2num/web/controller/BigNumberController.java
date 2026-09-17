package com.example.add2num.web.controller;

import com.example.add2num.core.MyBigNumber;
import com.example.add2num.web.dto.BigNumberForm;
import com.example.add2num.web.model.OperationRecord;
import com.example.add2num.web.service.HistoryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BigNumberController {

    private static final Logger log = LoggerFactory.getLogger(BigNumberController.class);

    private final HistoryService historyService;
    // Reuses the Task 1 core module as a library (dependency), no modification needed.
    private final MyBigNumber myBigNumber = new MyBigNumber();

    public BigNumberController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/")
    public String showForm(Model model) {
        if (!model.containsAttribute("bigNumberForm")) {
            model.addAttribute("bigNumberForm", new BigNumberForm());
        }
        model.addAttribute("history", historyService.getAll());
        return "index";
    }

    @PostMapping("/sum")
    public String sum(@Valid @ModelAttribute("bigNumberForm") BigNumberForm form,
                       BindingResult bindingResult,
                       Model model) {

        model.addAttribute("history", historyService.getAll());

        if (bindingResult.hasErrors()) {
            log.warn("Invalid input submitted: {}", bindingResult.getAllErrors());
            return "index";
        }

        log.info("Received addition request from the Web UI: {} + {}", form.getNumber1(), form.getNumber2());
        String result = myBigNumber.sum(form.getNumber1(), form.getNumber2());

        model.addAttribute("result", result);
        model.addAttribute("steps", myBigNumber.getLastSteps());

        historyService.add(new OperationRecord(form.getNumber1(), form.getNumber2(), result));
        model.addAttribute("history", historyService.getAll());

        return "index";
    }
}
