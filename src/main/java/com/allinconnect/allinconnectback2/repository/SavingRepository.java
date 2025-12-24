package com.allinconnect.allinconnectback2.repository;

import com.allinconnect.allinconnectback2.entity.Saving;
import com.allinconnect.allinconnectback2.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SavingRepository extends JpaRepository<Saving, Long> {
    List<Saving> findByUserOrderByDateDesc(User user);
}
