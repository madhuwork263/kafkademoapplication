package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.service.CanonicalService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
public class CanonicalController {

    private final CanonicalService canonicalService;

    public CanonicalController(CanonicalService canonicalService) {
        this.canonicalService = canonicalService;
    }

    // 1️⃣ JSON
    @PostMapping(value = "/storeline", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String processStoreline(@RequestBody StoreLineTransaction txn) {
        canonicalService.processStoreLine(txn);
        return "✅ StoreLine JSON processed successfully";
    }

    // 2️⃣ XML
    @PostMapping(value = "/toshiba", consumes = MediaType.APPLICATION_XML_VALUE)
    public String processToshiba(@RequestBody ToshibaTransaction txn) {
        canonicalService.processToshiba(txn);
        return "✅ Toshiba XML processed successfully";
    }

    // 3️⃣ CSV
    @PostMapping(value = "/emerald", consumes = {"text/csv", "text/plain"})
    public String processEmerald(@RequestBody String csvData) {
        canonicalService.processEmeraldCSV(csvData);
        return "✅ Emerald CSV processed successfully";
    }
}
