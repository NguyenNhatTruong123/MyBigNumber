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

import java.util.ArrayList;
import java.util.List;

@Controller
public class BigNumberController {

    private static final Logger log = LoggerFactory.getLogger(BigNumberController.class);

    private final HistoryService historyService;

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
        // Core stores the last calculation's trace, so each request needs its own instance.
        MyBigNumber myBigNumber = new MyBigNumber();
        String result = myBigNumber.sum(form.getNumber1(), form.getNumber2());

        model.addAttribute("result", result);
        List<MyBigNumber.AdditionStep> columns = myBigNumber.getLastSteps();
        List<MyBigNumber.AdditionStep> steps = new ArrayList<>();
        int commonLength = Math.min(form.getNumber1().length(), form.getNumber2().length());
        int index = 0;
        // Keep actual additions and carry propagation; do not expand the lazy copied columns.
        while (index < columns.size()
                && (index < commonLength || columns.get(index - 1).getCarryOut() > 0)) {
            steps.add(columns.get(index));
            index++;
        }
        model.addAttribute("steps", steps);
        boolean fromFirst = form.getNumber1().length() > form.getNumber2().length();
        String longerOperand = fromFirst ? form.getNumber1() : form.getNumber2();
        if (index < longerOperand.length()) {
            model.addAttribute("copiedPrefix", longerOperand.substring(0, longerOperand.length() - index));
            model.addAttribute("copiedFromFirst", fromFirst);
        }

        historyService.add(new OperationRecord(form.getNumber1(), form.getNumber2(), result));
        model.addAttribute("history", historyService.getAll());

        return "index";
    }
}
