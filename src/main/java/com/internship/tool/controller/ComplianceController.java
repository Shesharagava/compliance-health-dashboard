package com.internship.tool.controller;

import com.internship.tool.entity.Compliance;
import com.internship.tool.service.ComplianceService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/compliance")
public class ComplianceController {

    private final ComplianceService service;

    public ComplianceController(ComplianceService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Compliance>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    public ResponseEntity<Compliance> create(@RequestBody Compliance compliance) {
        return ResponseEntity.ok(service.create(compliance));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    public List<Compliance> getByStatus(@PathVariable String status) {
        return service.getByStatus(status);
    }

    @GetMapping("/date")
    public List<Compliance> getByDateRange(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end
    ) {
        return service.getByDateRange(start, end);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Compliance>> search(@RequestParam("q") String q) {
        return ResponseEntity.ok(service.search(q));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(service.getStats());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Compliance> update(@PathVariable Long id,
                                            @RequestBody Compliance compliance) {
        return ResponseEntity.ok(service.update(id, compliance));
    }
}