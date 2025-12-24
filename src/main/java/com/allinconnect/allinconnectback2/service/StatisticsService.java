package com.allinconnect.allinconnectback2.service;

import com.allinconnect.allinconnectback2.entity.MonthlyStat;
import com.allinconnect.allinconnectback2.repository.MonthlyStatRepository;
import com.allinconnect.allinconnectback2.repository.PaymentRepository;
import com.allinconnect.allinconnectback2.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@Transactional
public class StatisticsService {

    private static final Logger log = LoggerFactory.getLogger(StatisticsService.class);
    private final MonthlyStatRepository monthlyStatRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    public StatisticsService(MonthlyStatRepository monthlyStatRepository, UserRepository userRepository, PaymentRepository paymentRepository) {
        this.monthlyStatRepository = monthlyStatRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional(readOnly = true)
    public MonthlyStat getCurrentMonthStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
        LocalDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);

        long activeUsers = userRepository.countBySubscriptionPlanIsNotNull();
        long totalUsers = userRepository.count();
        Double revenue = paymentRepository.sumRevenueBetween(startOfMonth, endOfMonth);

        return new MonthlyStat(now.getYear(), now.getMonthValue(), activeUsers, totalUsers, revenue != null ? revenue : 0.0);
    }

    @Transactional(readOnly = true)
    public List<MonthlyStat> getHistory() {
        return monthlyStatRepository.findAll();
    }

    public MonthlyStat freezeMonth(int year, int month) {
        log.info("Freezing stats for {}/{}", month, year);
        
        // Vérifier si déjà figé
        if (monthlyStatRepository.findByYearAndMonth(year, month).isPresent()) {
            throw new RuntimeException("Stats for this month are already frozen");
        }

        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0).with(LocalTime.MIN);
        LocalDateTime endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);

        // Pour un mois passé, on prend les revenus de ce mois
        Double revenue = paymentRepository.sumRevenueBetween(startOfMonth, endOfMonth);
        
        // Pour les utilisateurs, c'est une photo à l'instant T (on prend les valeurs actuelles par défaut 
        // ou on pourrait complexifier si on avait un historique par jour)
        long activeUsers = userRepository.countBySubscriptionPlanIsNotNull();
        long totalUsers = userRepository.count();

        MonthlyStat stat = new MonthlyStat(year, month, activeUsers, totalUsers, revenue != null ? revenue : 0.0);
        return monthlyStatRepository.save(stat);
    }
}
