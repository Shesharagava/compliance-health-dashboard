package com.internship.tool.service;

import com.internship.tool.entity.Compliance;
import com.internship.tool.repository.ComplianceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ComplianceService {

    private final ComplianceRepository repository;

    public ComplianceService(ComplianceRepository repository) {
        this.repository = repository;
    }

    // ✅ GET ALL
    public List<Compliance> getAll() {
        return repository.findAll();
    }

    // ✅ CREATE (CUD for AOP)
    public Compliance create(Compliance c) {
        return repository.save(c);
    }

    // ✅ UPDATE (CUD for AOP)
    public Compliance update(Long id, Compliance updated) {

        Compliance existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setStatus(updated.getStatus());
        existing.setScore(updated.getScore());

        return repository.save(existing);
    }

    // ✅ DELETE (CUD for AOP)
    public void delete(Long id) {
    repository.deleteById(id);
}

    // ✅ OPTIONAL (soft delete if needed)
    public void softDelete(Long id) {
        Compliance compliance = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        compliance.setDeleted(true);
        repository.save(compliance);
    }

    // ✅ SEARCH
    public List<Compliance> search(String q) {
        return repository.findByTitleContainingIgnoreCaseAndDeletedFalse(q);
    }

    // ✅ STATS
    public Map<String, Long> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", repository.countActive());
        stats.put("open", repository.countOpen());
        stats.put("closed", repository.countClosed());
        return stats;
    }

    // ✅ FILTER
    public List<Compliance> getByStatus(String status) {
        return repository.findByStatus(status);
    }

    public List<Compliance> getByDateRange(LocalDateTime start, LocalDateTime end) {
        return repository.findByDateRange(start, end);
    }
}