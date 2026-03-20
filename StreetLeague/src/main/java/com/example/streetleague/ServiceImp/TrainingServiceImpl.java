package com.example.streetleague.ServiceImp;

import com.example.streetleague.Entity.Team;
import com.example.streetleague.Entity.Training;
import com.example.streetleague.Entity.TrainingStatus;
import com.example.streetleague.Repository.TeamRepository;
import com.example.streetleague.Repository.TrainingRepository;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.ServiceInterface.ItrainingService;
import com.example.streetleague.domain.Role;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.TrainingRequest;
import com.example.streetleague.dto.TrainingResponse;
import com.example.streetleague.dto.TrainingUpdateRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class TrainingServiceImpl implements ItrainingService {

    TrainingRepository trainingRepo;
    TeamRepository     teamRepository;
    UserRepository     userRepository;

    // ── ADD ───────────────────────────────────────────────────────────────
    @Override
    public TrainingResponse addTraining(TrainingRequest dto, Long teamId, Long coachId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found: " + teamId));
        User coach = userRepository.findById(coachId)
                .orElseThrow(() -> new RuntimeException("User not found: " + coachId));

        // ✅ Role.COACH (pas ROLE_COACH)
        if (coach.getRole() != Role.COACH)
            throw new RuntimeException("Only a COACH can create a training session");

        if (dto.title() == null || dto.title().isBlank())
            throw new RuntimeException("Title is required");
        if (dto.title().length() < 3 || dto.title().length() > 100)
            throw new RuntimeException("Title must be between 3 and 100 characters");
        if (dto.location() == null || dto.location().isBlank())
            throw new RuntimeException("Location is required");
        if (dto.location().length() < 3 || dto.location().length() > 100)
            throw new RuntimeException("Location must be between 3 and 100 characters");
        if (dto.trainingDate() == null)
            throw new RuntimeException("Training date is required");
        if (dto.trainingDate().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Training date must be in the future");
        if (dto.durationInMinutes() == null)
            throw new RuntimeException("Duration is required");
        if (dto.durationInMinutes() < 15)
            throw new RuntimeException("Duration must be at least 15 minutes");
        if (dto.durationInMinutes() > 480)
            throw new RuntimeException("Duration cannot exceed 480 minutes (8 hours)");
        if (dto.description() != null && dto.description().length() > 500)
            throw new RuntimeException("Description cannot exceed 500 characters");

        Training t = new Training();
        t.setTitle(dto.title());
        t.setDescription(dto.description());
        t.setTrainingDate(dto.trainingDate());
        t.setDurationInMinutes(dto.durationInMinutes());
        t.setLocation(dto.location());
        t.setExercises(dto.exercises());
        t.setTeam(team);
        t.setStatus(TrainingStatus.PLANNED);

        return TrainingResponse.fromEntity(trainingRepo.save(t));
    }

    // ── UPDATE ────────────────────────────────────────────────────────────
    @Override
    public TrainingResponse updateTraining(TrainingUpdateRequest dto, Long coachId) {
        Training existing = trainingRepo.findById(dto.idTraining())
                .orElseThrow(() -> new RuntimeException("Training not found: " + dto.idTraining()));
        User coach = userRepository.findById(coachId)
                .orElseThrow(() -> new RuntimeException("User not found: " + coachId));

        // ✅ Role.COACH
        if (coach.getRole() != Role.COACH)
            throw new RuntimeException("Only a COACH can update a training session");

        if (existing.getStatus() == TrainingStatus.CANCELLED)
            throw new RuntimeException("Cannot edit a cancelled training session");

        if (dto.title() != null) {
            if (dto.title().isBlank())
                throw new RuntimeException("Title cannot be empty");
            if (dto.title().length() < 3 || dto.title().length() > 100)
                throw new RuntimeException("Title must be between 3 and 100 characters");
            existing.setTitle(dto.title());
        }
        if (dto.location() != null) {
            if (dto.location().isBlank())
                throw new RuntimeException("Location cannot be empty");
            if (dto.location().length() < 3 || dto.location().length() > 100)
                throw new RuntimeException("Location must be between 3 and 100 characters");
            existing.setLocation(dto.location());
        }
        if (dto.trainingDate() != null) {
            if (dto.trainingDate().isBefore(LocalDateTime.now()))
                throw new RuntimeException("Training date must be in the future");
            existing.setTrainingDate(dto.trainingDate());
        }
        if (dto.durationInMinutes() != null) {
            if (dto.durationInMinutes() < 15)
                throw new RuntimeException("Duration must be at least 15 minutes");
            if (dto.durationInMinutes() > 480)
                throw new RuntimeException("Duration cannot exceed 480 minutes (8 hours)");
            existing.setDurationInMinutes(dto.durationInMinutes());
        }
        if (dto.description() != null) {
            if (dto.description().length() > 500)
                throw new RuntimeException("Description cannot exceed 500 characters");
            existing.setDescription(dto.description());
        }
        if (dto.exercises() != null) existing.setExercises(dto.exercises());
        if (dto.status()    != null) existing.setStatus(dto.status());

        return TrainingResponse.fromEntity(trainingRepo.save(existing));
    }

    // ── DELETE ────────────────────────────────────────────────────────────
    @Override
    public void deleteTraining(Long idTraining, Long userId) {
        Training training = trainingRepo.findById(idTraining)
                .orElseThrow(() -> new RuntimeException("Training not found: " + idTraining));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // ✅ ADMIN peut supprimer, sinon seulement un COACH
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isAdmin && user.getRole() != Role.COACH)
            throw new RuntimeException("Only a COACH or ADMIN can delete a training session");

        if (!isAdmin && training.getStatus() == TrainingStatus.COMPLETED)
            throw new RuntimeException("Cannot delete a completed training session");

        trainingRepo.deleteById(idTraining);
    }

    // ── SHOW ALL ──────────────────────────────────────────────────────────
    @Override
    public List<TrainingResponse> ShowTrainings() {
        return trainingRepo.findAll().stream()
                .map(TrainingResponse::fromEntity).toList();
    }

    // ── SHOW ONE ──────────────────────────────────────────────────────────
    @Override
    public TrainingResponse ShowTraining(Long idTraining) {
        return TrainingResponse.fromEntity(
                trainingRepo.findById(idTraining)
                        .orElseThrow(() -> new RuntimeException("Training not found: " + idTraining)));
    }

    // ── JOIN ──────────────────────────────────────────────────────────────
    @Override
    public TrainingResponse joinTraining(Long trainingId, Long playerId) {
        Training training = trainingRepo.findById(trainingId)
                .orElseThrow(() -> new RuntimeException("Training not found: " + trainingId));
        User player = userRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("User not found: " + playerId));

        // ✅ Role.PLAYER (pas ROLE_PLAYER)
        if (player.getRole() != Role.PLAYER)
            throw new RuntimeException("Only a PLAYER can join a training session");

        if (training.getStatus() != TrainingStatus.PLANNED)
            throw new RuntimeException("Can only join PLANNED training sessions");
        if (training.getParticipants().contains(player))
            throw new RuntimeException("Player already joined this training");

        training.getParticipants().add(player);
        return TrainingResponse.fromEntity(trainingRepo.save(training));
    }

    // ── LEAVE ─────────────────────────────────────────────────────────────
    @Override
    public TrainingResponse leaveTraining(Long trainingId, Long playerId) {
        Training training = trainingRepo.findById(trainingId)
                .orElseThrow(() -> new RuntimeException("Training not found: " + trainingId));
        User player = userRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("User not found: " + playerId));

        if (!training.getParticipants().contains(player))
            throw new RuntimeException("Player is not part of this training");

        training.getParticipants().remove(player);
        return TrainingResponse.fromEntity(trainingRepo.save(training));
    }
}