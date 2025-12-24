package com.allinconnect.allinconnectback2.service;

import com.allinconnect.allinconnectback2.entity.Saving;
import com.allinconnect.allinconnectback2.entity.User;
import com.allinconnect.allinconnectback2.repository.SavingRepository;
import com.allinconnect.allinconnectback2.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SavingService {

    private static final Logger log = LoggerFactory.getLogger(SavingService.class);
    private final SavingRepository savingRepository;
    private final UserRepository userRepository;

    public SavingService(SavingRepository savingRepository, UserRepository userRepository) {
        this.savingRepository = savingRepository;
        this.userRepository = userRepository;
    }

    public Saving addSaving(Saving saving, User user) {
        log.debug("Service: Adding saving for user {}", user.getEmail());
        saving.setUser(user);
        return savingRepository.save(saving);
    }

    @Transactional(readOnly = true)
    public List<Saving> getAllSavingsByUser(User user) {
        log.debug("Service: Getting all savings for user {}", user.getEmail());
        return savingRepository.findByUserOrderByDateDesc(user);
    }

    public Saving updateSaving(Long id, Saving details, User user) {
        log.debug("Service: Updating saving {} for user {}", id, user.getEmail());
        Saving saving = savingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Saving not found"));
        
        if (!saving.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only update your own savings");
        }
        
        saving.setShopName(details.getShopName());
        saving.setDescription(details.getDescription());
        saving.setAmount(details.getAmount());
        saving.setDate(details.getDate());
        
        return savingRepository.save(saving);
    }

    public void deleteSaving(Long id, User user) {
        log.debug("Service: Deleting saving {} for user {}", id, user.getEmail());
        Saving saving = savingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Saving not found"));
        
        if (!saving.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own savings");
        }
        
        savingRepository.delete(saving);
    }
}
