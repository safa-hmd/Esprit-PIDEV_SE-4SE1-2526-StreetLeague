package com.example.streetleague.ServiceImp;

import com.example.streetleague.Entity.*;
import com.example.streetleague.Repository.MatchRepository;
import com.example.streetleague.Repository.TeamRepository;
import com.example.streetleague.Repository.TrainingRepository;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.ServiceInterface.ItrainingService;
import com.example.streetleague.ServiceInterface.InotificationService;
import com.example.streetleague.domain.Role;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.TrainingRequest;
import com.example.streetleague.dto.TrainingResponse;
import com.example.streetleague.dto.TrainingUpdateRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class TrainingServiceImpl implements ItrainingService {

    TrainingRepository trainingRepo;
    TeamRepository     teamRepository;
    UserRepository     userRepository;
    MatchRepository    matchRepository;
    private final InotificationService notificationService;

    // ── ADD ───────────────────────────────────────────────────────────────
    @Transactional
    @Override
    public TrainingResponse addTraining(TrainingRequest dto, Long teamId, Long coachId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found: " + teamId));
        User coach = userRepository.findById(coachId)
                .orElseThrow(() -> new RuntimeException("User not found: " + coachId));

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
        t.setCoach(coach);
        t.setStatus(TrainingStatus.PLANNED);

        Training savedTraining = trainingRepo.save(t);

        // ══════════════════════════════════════════════════════════════════
        // ✅ NOTIFICATION — Message détaillé avec lieu, heure, durée
        // ══════════════════════════════════════════════════════════════════
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        String notifMsg = String.format(
                "🏋️ New Training: %s\n" +
                        "📅 Date: %s at %s\n" +
                        "📍 Location: %s\n" +
                        "⏱ Duration: %d min\n" +
                        "👨‍🏫 Coach: %s\n" +
                        "🏟 Team: %s",
                dto.title(),
                dto.trainingDate().format(dateFormatter),
                dto.trainingDate().format(timeFormatter),
                dto.location(),
                dto.durationInMinutes(),
                coach.getFullName(),
                team.getName()
        );

        List<User> targetUsers = new ArrayList<>(team.getPlayers());

        if (team.getCaptain() != null && !targetUsers.contains(team.getCaptain())) {
            targetUsers.add(team.getCaptain());
        }

        targetUsers.removeIf(u -> u.getIdUser().equals(coach.getIdUser()));

        if (!targetUsers.isEmpty()) {
            notificationService.createNotificationForUsers(targetUsers, notifMsg);
        }
        // ══════════════════════════════════════════════════════════════════

        return TrainingResponse.fromEntity(savedTraining);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────
    @Transactional
    @Override
    public TrainingResponse updateTraining(TrainingUpdateRequest dto, Long coachId) {
        Training existing = trainingRepo.findById(dto.idTraining())
                .orElseThrow(() -> new RuntimeException("Training not found: " + dto.idTraining()));
        User coach = userRepository.findById(coachId)
                .orElseThrow(() -> new RuntimeException("User not found: " + coachId));

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
        if (dto.status() != null) existing.setStatus(dto.status());

        Training saved = trainingRepo.save(existing);

        // ══════════════════════════════════════════════════════════════════
        // ✅ NOTIFICATION — Prévenir les membres que le training a changé
        // ══════════════════════════════════════════════════════════════════
        Team team = existing.getTeam();
        if (team != null) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            String updateMsg = String.format(
                    "📝 Training Updated: %s\n" +
                            "📅 Date: %s at %s\n" +
                            "📍 Location: %s\n" +
                            "⏱ Duration: %d min\n" +
                            "👨‍🏫 Coach: %s",
                    saved.getTitle(),
                    saved.getTrainingDate().format(dateFormatter),
                    saved.getTrainingDate().format(timeFormatter),
                    saved.getLocation(),
                    saved.getDurationInMinutes(),
                    coach.getFullName()
            );

            List<User> targetUsers = new ArrayList<>(team.getPlayers());
            if (team.getCaptain() != null && !targetUsers.contains(team.getCaptain())) {
                targetUsers.add(team.getCaptain());
            }
            targetUsers.removeIf(u -> u.getIdUser().equals(coach.getIdUser()));

            if (!targetUsers.isEmpty()) {
                notificationService.createNotificationForUsers(targetUsers, updateMsg);
            }
        }
        // ══════════════════════════════════════════════════════════════════

        return TrainingResponse.fromEntity(saved);
    }

    // ── DELETE ────────────────────────────────────────────────────────────
    @Override
    public void deleteTraining(Long idTraining, Long userId) {
        Training training = trainingRepo.findById(idTraining)
                .orElseThrow(() -> new RuntimeException("Training not found: " + idTraining));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isAdmin && user.getRole() != Role.COACH)
            throw new RuntimeException("Only a COACH or ADMIN can delete a training session");

        if (!isAdmin && training.getStatus() == TrainingStatus.COMPLETED)
            throw new RuntimeException("Cannot delete a completed training session");

        // ══════════════════════════════════════════════════════════════════
        // ✅ NOTIFICATION — Prévenir que le training est annulé
        // ══════════════════════════════════════════════════════════════════
        Team team = training.getTeam();
        if (team != null) {
            String cancelMsg = String.format(
                    "❌ Training Cancelled: %s\n" +
                            "📅 Was scheduled for: %s\n" +
                            "📍 Location: %s",
                    training.getTitle(),
                    training.getTrainingDate().toString(),
                    training.getLocation()
            );

            List<User> targetUsers = new ArrayList<>(team.getPlayers());
            if (team.getCaptain() != null && !targetUsers.contains(team.getCaptain())) {
                targetUsers.add(team.getCaptain());
            }
            targetUsers.removeIf(u -> u.getIdUser().equals(user.getIdUser()));

            if (!targetUsers.isEmpty()) {
                notificationService.createNotificationForUsers(targetUsers, cancelMsg);
            }
        }
        // ══════════════════════════════════════════════════════════════════

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

    @Override
    public List<TrainingResponse> getTrainingsByCoach(Long coachId) {
        return trainingRepo.findAll().stream()
                .filter(t -> t.getCoach() != null && t.getCoach().getIdUser().equals(coachId))
                .map(TrainingResponse::fromEntity)
                .toList();
    }

    @Override
    public List<TrainingResponse> getMyTeamTrainings(Long playerId) {
        User player = userRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("User not found: " + playerId));

        return trainingRepo.findAll().stream()
                .filter(t -> t.getTeam() != null &&
                        (t.getTeam().getPlayers().contains(player) ||
                                (t.getTeam().getCaptain() != null && t.getTeam().getCaptain().getIdUser().equals(playerId))))
                .map(TrainingResponse::fromEntity)
                .toList();
    }

    // ── POST-MATCH TRAINING TRIGGER ───────────────────────────────────────
    @Override
    public TrainingResponse generateTrainingFromMatch(Long matchId) {

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found: " + matchId));

        if (match.getStatus() != MatchStatus.FINISHED)
            throw new RuntimeException("Match must be FINISHED to generate a training session");

        int goalsConceeded = match.getScoreTeamB() != null ? match.getScoreTeamB() : 0;
        int scoreTeamA     = match.getScoreTeamA() != null ? match.getScoreTeamA() : 0;

        List<Match> finishedMatches = matchRepository.findAll().stream()
                .filter(m -> m.getStatus() == MatchStatus.FINISHED
                        && m.getTeamA().getIdTeam().equals(match.getTeamA().getIdTeam()))
                .toList();

        double avgGoalsConceded = finishedMatches.stream()
                .mapToInt(m -> m.getScoreTeamB() != null ? m.getScoreTeamB() : 0)
                .average().orElse(0.0);

        double stdDev = Math.sqrt(finishedMatches.stream()
                .mapToDouble(m -> {
                    double diff = (m.getScoreTeamB() != null ? m.getScoreTeamB() : 0) - avgGoalsConceded;
                    return diff * diff;
                }).average().orElse(0.0));

        List<String> weaknesses = new ArrayList<>();

        if (goalsConceeded > avgGoalsConceded + stdDev)
            weaknesses.add("Defensive Positioning Drills");
        if (scoreTeamA < 1)
            weaknesses.add("Short Passing & Combination Play");
        if (goalsConceeded - scoreTeamA >= 2)
            weaknesses.add("Physical Conditioning & Strength Training");
        if (weaknesses.isEmpty())
            weaknesses.add("General Technical Review");

        List<String> top3 = weaknesses.stream().limit(3).toList();
        String exercises  = String.join(", ", top3);

        Training session = new Training();
        session.setTitle("Post-Match Training — " + match.getTeamA().getName());
        session.setDescription("Auto-generated session based on match analysis.");
        session.setTrainingDate(match.getMatchDate().plusDays(2));
        session.setDurationInMinutes(75);
        session.setLocation(match.getLocation());
        session.setExercises(exercises);
        session.setTeam(match.getTeamA());
        session.setStatus(TrainingStatus.PLANNED);

        Training saved = trainingRepo.save(session);

        // ══════════════════════════════════════════════════════════════════
        // ✅ NOTIFICATION — Post-match auto training
        // ══════════════════════════════════════════════════════════════════
        Team team = match.getTeamA();
        if (team != null) {
            String autoMsg = String.format(
                    "🤖 Auto Training Generated from Match!\n" +
                            "🏋️ %s\n" +
                            "📅 Date: %s\n" +
                            "📍 Location: %s\n" +
                            "💪 Exercises: %s",
                    saved.getTitle(),
                    saved.getTrainingDate().toString(),
                    saved.getLocation(),
                    exercises
            );

            List<User> targetUsers = new ArrayList<>(team.getPlayers());
            if (team.getCaptain() != null && !targetUsers.contains(team.getCaptain())) {
                targetUsers.add(team.getCaptain());
            }

            if (!targetUsers.isEmpty()) {
                notificationService.createNotificationForUsers(targetUsers, autoMsg);
            }
        }
        // ══════════════════════════════════════════════════════════════════

        return TrainingResponse.fromEntity(saved);
    }
}