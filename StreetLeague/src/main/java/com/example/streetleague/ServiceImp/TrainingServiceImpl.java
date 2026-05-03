package com.example.streetleague.ServiceImp;

import com.example.streetleague.Entity.Match;
import com.example.streetleague.Entity.MatchStatus;
import com.example.streetleague.Entity.Team;
import com.example.streetleague.Entity.Training;
import com.example.streetleague.Entity.TrainingStatus;
import com.example.streetleague.Repository.MatchRepository;
import com.example.streetleague.Repository.TeamRepository;
import com.example.streetleague.Repository.TrainingRepository;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.ServiceInterface.InotificationService;
import com.example.streetleague.ServiceInterface.ItrainingService;
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
    TeamRepository teamRepository;
    UserRepository userRepository;
    MatchRepository matchRepository;
    private final InotificationService notificationService;


    @Override
    @Transactional
    public TrainingResponse addTraining(TrainingRequest dto, Long teamId, Long coachId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found: " + teamId));
        User coach = userRepository.findById(coachId)
                .orElseThrow(() -> new RuntimeException("User not found: " + coachId));

        if (coach.getRole() != Role.COACH)
            throw new RuntimeException("Only a COACH can create a training session");
        if (dto.getTitle() == null || dto.getTitle().isBlank())
            throw new RuntimeException("Title is required");
        if (dto.getTitle().length() < 3 || dto.getTitle().length() > 100)
            throw new RuntimeException("Title must be between 3 and 100 characters");
        if (dto.getLocation() == null || dto.getLocation().isBlank())
            throw new RuntimeException("Location is required");
        if (dto.getLocation().length() < 3 || dto.getLocation().length() > 100)
            throw new RuntimeException("Location must be between 3 and 100 characters");
        if (dto.getStartTime() == null)
            throw new RuntimeException("Training date is required");
        if (dto.getStartTime().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Training date must be in the future");
        if (dto.getStartTime() == null)
            throw new RuntimeException("Duration is required");
        // Note: duration calculation would need to be implemented based on startTime and endTime
        if (dto.getStartTime() != null && dto.getStartTime().isBefore(LocalDateTime.now().minusMinutes(15)))
            throw new RuntimeException("Start time must be valid");
        if (dto.getDescription() != null && dto.getDescription().length() > 500)
            throw new RuntimeException("Description cannot exceed 500 characters");

        Training t = new Training();
        t.setTitle(dto.getTitle());
        t.setDescription(dto.getDescription());
        t.setTrainingDate(dto.getStartTime());
        t.setDurationInMinutes(60); // Default duration, could be calculated from startTime and endTime
        t.setLocation(dto.getLocation());
        t.setExercises(""); // Default empty exercises
        t.setTeam(team);
        t.setCoach(coach);
        t.setStatus(TrainingStatus.SCHEDULED);

        Training savedTraining = trainingRepo.save(t);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        String notifMsg = String.format(
                "New Training: %s%nDate: %s at %s%nLocation: %s%nDuration: %d min%nCoach: %s%nTeam: %s",
                dto.getTitle(),
                dto.getStartTime().format(dateFormatter),
                dto.getStartTime().format(timeFormatter),
                dto.getLocation(),
                60, // Default duration in minutes
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

        return TrainingResponse.fromEntity(savedTraining);
    }


    @Override
    @Transactional
    public TrainingResponse updateTraining(TrainingUpdateRequest dto, Long coachId) {
        Training existing = trainingRepo.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Training not found: " + dto.getId()));
        User coach = userRepository.findById(coachId)
                .orElseThrow(() -> new RuntimeException("User not found: " + coachId));

        if (coach.getRole() != Role.COACH)
            throw new RuntimeException("Only a COACH can update a training session");

        if (existing.getCoach() == null || !existing.getCoach().getIdUser().equals(coachId))
            throw new RuntimeException("Only the coach who created this training can update it");

        if (existing.getStatus() == TrainingStatus.CANCELLED)
            throw new RuntimeException("Cannot edit a cancelled training session");

        if (dto.getTitle() != null) {
            if (dto.getTitle().isBlank())
                throw new RuntimeException("Title cannot be blank");
            if (dto.getTitle().length() < 3 || dto.getTitle().length() > 100)
                throw new RuntimeException("Title must be between 3 and 100 characters");
            existing.setTitle(dto.getTitle());
        }
        if (dto.getLocation() != null) {
            if (dto.getLocation().isBlank())
                throw new RuntimeException("Location cannot be empty");
            if (dto.getLocation().length() < 3 || dto.getLocation().length() > 100)
                throw new RuntimeException("Location must be between 3 and 100 characters");
            existing.setLocation(dto.getLocation());
        }
        if (dto.getStartTime() != null) {
            if (dto.getStartTime().isBefore(LocalDateTime.now()))
                throw new RuntimeException("Training date must be in the future");
            existing.setTrainingDate(dto.getStartTime());
        }
        // Note: duration would need to be calculated from startTime and endTime
        if (dto.getDescription() != null) {
            if (dto.getDescription().length() > 500)
                throw new RuntimeException("Description cannot exceed 500 characters");
            existing.setDescription(dto.getDescription());
        }
        // Note: exercises field doesn't exist in TrainingUpdateRequest
        // if (dto.getExercises() != null) existing.setExercises(dto.getExercises());
        if (dto.getStatus() != null) existing.setStatus(TrainingStatus.valueOf(dto.getStatus().toUpperCase()));

        Training saved = trainingRepo.save(existing);

        Team team = existing.getTeam();
        if (team != null) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            String updateMsg = String.format(
                    "Training Updated: %s%nDate: %s at %s%nLocation: %s%nDuration: %d min%nCoach: %s",
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

        return TrainingResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public void deleteTraining(Long idTraining, Long userId) {
        Training training = trainingRepo.findById(idTraining)
                .orElseThrow(() -> new RuntimeException("Training not found: " + idTraining));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isAdmin && user.getRole() != Role.COACH)
            throw new RuntimeException("Only a COACH or ADMIN can delete a training session");

        if (!isAdmin && (training.getCoach() == null || !training.getCoach().getIdUser().equals(userId)))
            throw new RuntimeException("Only the coach who created this training can delete it");
        if (!isAdmin && training.getStatus() == TrainingStatus.COMPLETED)
            throw new RuntimeException("Cannot delete a completed training session");

        Team team = training.getTeam();
        if (team != null) {
            String cancelMsg = String.format(
                    "Training Cancelled: %s%nWas scheduled for: %s%nLocation: %s",
                    training.getTitle(),
                    training.getTrainingDate(),
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


        trainingRepo.deleteById(idTraining);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingResponse> ShowTrainings() {
        return trainingRepo.findAll().stream()
                .map(TrainingResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TrainingResponse ShowTraining(Long idTraining) {
        return TrainingResponse.fromEntity(
                trainingRepo.findById(idTraining)
                        .orElseThrow(() -> new RuntimeException("Training not found: " + idTraining)));
    }

    @Override
    @Transactional
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
        if (training.getTeam() == null)
            throw new RuntimeException("Training is not linked to a team");

        boolean belongsToTeam = training.getTeam().getPlayers().contains(player)
                || (training.getTeam().getCaptain() != null
                && training.getTeam().getCaptain().getIdUser().equals(playerId));
        if (!belongsToTeam)
            throw new RuntimeException("Player must belong to the training team to join");

        training.getParticipants().add(player);
        return TrainingResponse.fromEntity(trainingRepo.save(training));
    }

    @Override
    @Transactional
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
    @Transactional(readOnly = true)
    public List<TrainingResponse> getTrainingsByCoach(Long coachId) {
        return trainingRepo.findAll().stream()
                .filter(t -> t.getCoach() != null && t.getCoach().getIdUser().equals(coachId))
                .map(TrainingResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
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

    @Override
    public TrainingResponse generateTrainingFromMatch(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found: " + matchId));

        if (match.getStatus() != MatchStatus.FINISHED)
            throw new RuntimeException("Match must be FINISHED to generate a training session");
        if (match.getScoreTeamA() == null || match.getScoreTeamB() == null)
            throw new RuntimeException("Scores are required to generate a post-match training");

        Team targetTeam;
        int goalsScored;
        int goalsConceded;

        if (match.getScoreTeamA() <= match.getScoreTeamB()) {
            targetTeam = match.getTeamA();
            goalsScored = match.getScoreTeamA();
            goalsConceded = match.getScoreTeamB();
        } else {
            targetTeam = match.getTeamB();
            goalsScored = match.getScoreTeamB();
            goalsConceded = match.getScoreTeamA();
        }

        List<Match> finishedMatches = matchRepository.findAll().stream()
                .filter(m -> m.getStatus() == MatchStatus.FINISHED)
                .filter(m -> involvesTeam(m, targetTeam.getIdTeam()))
                .toList();

        double avgGoalsConceded = finishedMatches.stream()
                .mapToInt(m -> goalsConcededForTeam(m, targetTeam.getIdTeam()))
                .average()
                .orElse(0.0);

        double stdDev = Math.sqrt(finishedMatches.stream()
                .mapToDouble(m -> {
                    double diff = goalsConcededForTeam(m, targetTeam.getIdTeam()) - avgGoalsConceded;
                    return diff * diff;
                })
                .average()
                .orElse(0.0));

        List<String> weaknesses = new ArrayList<>();
        if (goalsConceded > avgGoalsConceded + stdDev)
            weaknesses.add("Defensive Positioning Drills");
        if (goalsScored < 1)
            weaknesses.add("Short Passing & Combination Play");
        if (goalsConceded - goalsScored >= 2)
            weaknesses.add("Physical Conditioning & Strength Training");
        if (weaknesses.isEmpty())
            weaknesses.add("General Technical Review");

        String exercises = String.join(", ", weaknesses.stream().limit(3).toList());

        Training session = new Training();
        session.setTitle("Post-Match Training - " + targetTeam.getName());
        session.setDescription("Auto-generated session based on match analysis.");
        session.setTrainingDate(match.getMatchDate().plusDays(2).isAfter(LocalDateTime.now())
                ? match.getMatchDate().plusDays(2)
                : LocalDateTime.now().plusHours(2));
        session.setDurationInMinutes(75);
        session.setLocation(match.getLocation());
        session.setExercises(exercises);
        session.setTeam(targetTeam);
        session.setCoach(targetTeam.getCoach());
        session.setStatus(TrainingStatus.PLANNED);

        Training saved = trainingRepo.save(session);

        List<User> targetUsers = new ArrayList<>(targetTeam.getPlayers());
        if (targetTeam.getCaptain() != null && !targetUsers.contains(targetTeam.getCaptain())) {
            targetUsers.add(targetTeam.getCaptain());
        }
        if (!targetUsers.isEmpty()) {
            String autoMsg = String.format(
                    "Auto Training Generated from Match%n%s%nDate: %s%nLocation: %s%nExercises: %s",
                    saved.getTitle(),
                    saved.getTrainingDate(),
                    saved.getLocation(),
                    exercises
            );
            notificationService.createNotificationForUsers(targetUsers, autoMsg);
        }

        return TrainingResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingResponse> getUpcomingTrainingsWithDetails() {
        return trainingRepo.findUpcomingTrainingsWithTeamAndCoach(LocalDateTime.now())
                .stream()
                .map(TrainingResponse::fromEntity)
                .toList();
    }

    @Override
    public List<TrainingResponse> getCompletedTrainingsWithDetails(Long teamId) {
        return trainingRepo.findCompletedTrainingsWithParticipantsByTeam(teamId)
                .stream()
                .distinct()
                .map(TrainingResponse::fromEntity)
                .toList();
    }

    private boolean involvesTeam(Match match, Long teamId) {
        return (match.getTeamA() != null && match.getTeamA().getIdTeam().equals(teamId))
                || (match.getTeamB() != null && match.getTeamB().getIdTeam().equals(teamId));
    }

    private int goalsConcededForTeam(Match match, Long teamId) {
        if (match.getTeamA() != null && match.getTeamA().getIdTeam().equals(teamId)) {
            return match.getScoreTeamB() != null ? match.getScoreTeamB() : 0;
        }
        if (match.getTeamB() != null && match.getTeamB().getIdTeam().equals(teamId)) {
            return match.getScoreTeamA() != null ? match.getScoreTeamA() : 0;
        }
        return 0;
    }
}
