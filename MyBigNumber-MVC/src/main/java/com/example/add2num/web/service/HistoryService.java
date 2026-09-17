package com.example.add2num.web.service;

import com.example.add2num.web.model.OperationRecord;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Keeps a history of the additions performed so far, in memory, for display on the home page.
 * Holds up to MAX_HISTORY most recent records, with the newest one shown first.
 */
@Service
public class HistoryService {

    private static final int MAX_HISTORY = 50;
    private final LinkedList<OperationRecord> history = new LinkedList<>();

    public synchronized void add(OperationRecord record) {
        history.addFirst(record);
        while (history.size() > MAX_HISTORY) {
            history.removeLast();
        }
    }

    public synchronized List<OperationRecord> getAll() {
        return Collections.unmodifiableList(new LinkedList<>(history));
    }
}
