package com.allinconnect.allinconnectback2.repository;

import com.allinconnect.allinconnectback2.entity.MonthlyStat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MonthlyStatRepository extends JpaRepository<MonthlyStat, Long> {
    Optional<MonthlyStat> findByYearAndMonth(int year, int month);
}
