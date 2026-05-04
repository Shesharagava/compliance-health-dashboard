package com.internship.tool.scheduler;

import com.internship.tool.entity.Compliance;
import com.internship.tool.repository.ComplianceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ComplianceScheduler {

    private final ComplianceRepository complianceRepository;

    // 1. Overdue check
    @Scheduled(cron = "0 0 9 * * ?")  
    public void checkOverdueItems() {

        System.out.println("🔥 Scheduler running (Overdue Check)...");

        LocalDate today = LocalDate.now();

        List<Compliance> overdueItems =
                complianceRepository.findByDueDateBefore(today);

        System.out.println("🔔 Overdue Items: " + overdueItems.size());

        overdueItems.forEach(item -> {
            item.setStatus("OVERDUE");
            complianceRepository.save(item);
            System.out.println("Overdue: " + item.getTitle());
        });
    }

    // 2. Upcoming check
    @Scheduled(cron = "0 0 10 * * ?")  
    public void upcomingDeadlineAlert() {

        System.out.println("🔥 Scheduler running (Upcoming Check)...");

        LocalDate today = LocalDate.now();
        LocalDate next7Days = today.plusDays(7);

        List<Compliance> upcomingItems =
                complianceRepository.findByDueDateBetween(today, next7Days);

        System.out.println("📅 Upcoming Deadlines: " + upcomingItems.size());

        upcomingItems.forEach(item -> {
            item.setStatus("UPCOMING");
            complianceRepository.save(item);
            System.out.println("Upcoming: " + item.getTitle());
        });
    }

    // 3. Weekly summary
    @Scheduled(cron = "0 0 9 ? * MON")
    public void weeklySummary() {

        System.out.println("🔥 Scheduler running (Weekly Summary)...");

        long total = complianceRepository.count();

        System.out.println("📊 Weekly Summary:");
        System.out.println("Total records: " + total);
    }
}