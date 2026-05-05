package com.internship.tool.service;

import com.internship.tool.entity.Compliance;
import com.internship.tool.repository.ComplianceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Service
public class ComplianceService {

    private final ComplianceRepository repository;

    public ComplianceService(ComplianceRepository repository) {
        this.repository = repository;
    }

    // ✅ PAGINATION METHOD
    public Page<Compliance> getAll(int page, int size, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return repository.findAll(pageable);
    }

    // ✅ CSV EXPORT METHOD (ADDED HERE)
    public String exportToCSV() {

        List<Compliance> list = repository.findAll();

        StringBuilder csv = new StringBuilder();

        // HEADER
        csv.append("ID,Title,Status,CreatedAt\n");

        for (Compliance c : list) {
            csv.append(c.getId()).append(",")
               .append(c.getTitle()).append(",")
               .append(c.getStatus()).append(",")
               .append(c.getCreatedAt()).append("\n");
        }

        return csv.toString();
    }

    // ✅ CREATE
    public Compliance create(Compliance c) {
        return repository.save(c);
    }

    // ✅ UPDATE
    public Compliance update(Long id, Compliance updated) {

        Compliance existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setStatus(updated.getStatus());
        existing.setScore(updated.getScore());

        return repository.save(existing);
    }

    // ✅ DELETE
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // ✅ SOFT DELETE
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