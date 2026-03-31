package com.example.streetleague.ServiceImp;

import com.example.streetleague.Entity.waterReminder;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.Repository.WaterReminderRepository;
import com.example.streetleague.ServiceInterface.waterReminderService;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.WaterReminderResponseDTO;
import com.example.streetleague.dto.waterReminderDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class waterReminderServiceIMPL  implements waterReminderService {
    private final   WaterReminderRepository waterReminderRepository;
    private final UserRepository userRepository;

    @Override
    public waterReminder addwaterReminder(waterReminderDTO dto) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        waterReminder reminder = waterReminder.builder()

                .time(LocalDateTime.now())
                .frequency(dto.getFrequency())
                .quantity(dto.getQuantity())
                .active(dto.isActive())
                .user(currentUser)
                .build();

        return waterReminderRepository.save(reminder);
    }

    @Override
    public waterReminder updatewaterReminder(Long id, waterReminderDTO dto) {
        waterReminder existing = waterReminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("WaterReminder not found"));

        existing.setFrequency(dto.getFrequency());
        existing.setQuantity(dto.getQuantity());
        existing.setActive(dto.isActive());

        return waterReminderRepository.save(existing);
    }

    @Override
    public waterReminder getwaterReminder(Long id) {
        return waterReminderRepository.findById(id).orElse(null);
    }

    @Override
    public void deletewaterReminder(Long id) {
        waterReminderRepository.deleteById(id);
    }

    @Override
    public List<WaterReminderResponseDTO> getAllReminders() {
        return waterReminderRepository.findAll().stream().map(r -> {
            WaterReminderResponseDTO dto = new WaterReminderResponseDTO();
            dto.setId(r.getId());
            dto.setFrequency(r.getFrequency());
            dto.setQuantity(r.getQuantity());
            dto.setActive(r.isActive());
            dto.setUserName(r.getUser() != null ? r.getUser().getFullName() : "Unknown");
            dto.setUserEmail(r.getUser() != null ? r.getUser().getEmail() : "");
            return dto;
        }).collect(Collectors.toList());
    }
}
