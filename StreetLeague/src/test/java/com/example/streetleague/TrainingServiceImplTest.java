package com.example.streetleague;

import com.example.streetleague.Entity.*;
import com.example.streetleague.Repository.*;
import com.example.streetleague.ServiceImp.TrainingServiceImpl;
import com.example.streetleague.domain.Role;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.TrainingRequest;
import com.example.streetleague.dto.TrainingResponse;
import com.example.streetleague.dto.TrainingUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @InjectMocks private TrainingServiceImpl trainingService;
    @Mock private TrainingRepository trainingRepo;
    @Mock private TeamRepository     teamRepository;
    @Mock private UserRepository     userRepository;

    private User coach;
    private User player;
    private User admin;
    private Team team;
    private Training training;

    @BeforeEach
    void setUp() {
        coach = new User();
        coach.setIdUser(1L);
        coach.setRole(Role.COACH);

        player = new User();
        player.setIdUser(2L);
        player.setRole(Role.PLAYER);

        admin = new User();
        admin.setIdUser(3L);
        admin.setRole(Role.ADMIN);

        team = new Team();
        team.setIdTeam(1L);
        team.setName("Thunder FC");

        training = new Training();
        training.setIdTraining(1L);
        training.setTitle("Soccer Techniques");
        training.setLocation("Tunis");
        training.setDurationInMinutes(60);
        training.setTrainingDate(LocalDateTime.now().plusDays(1));
        training.setStatus(TrainingStatus.PLANNED);
        training.setTeam(team);
        training.setParticipants(new ArrayList<>());
    }

    @Test
    void addTrainingTest() {
        TrainingRequest dto = new TrainingRequest(
                "Soccer Techniques", "desc",
                LocalDateTime.now().plusDays(1),
                60, "Tunis", null
        );
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(userRepository.findById(1L)).thenReturn(Optional.of(coach));
        when(trainingRepo.save(any())).thenReturn(training);

        TrainingResponse res = trainingService.addTraining(dto, 1L, 1L);

        assertNotNull(res);
        assertEquals("Soccer Techniques", res.title());
        verify(trainingRepo, times(1)).save(any());
    }

    @Test
    void updateTrainingTest() {
        when(trainingRepo.findById(1L)).thenReturn(Optional.of(training));
        when(userRepository.findById(1L)).thenReturn(Optional.of(coach));
        when(trainingRepo.save(any())).thenReturn(training);

        TrainingUpdateRequest dto = new TrainingUpdateRequest(
                1L, "New Title", null, null, null, null, null, TrainingStatus.PLANNED
        );

        TrainingResponse res = trainingService.updateTraining(dto, 1L);

        assertNotNull(res);
        verify(trainingRepo, times(1)).save(any());
    }

    @Test
    void deleteTrainingTest() {
        when(trainingRepo.findById(1L)).thenReturn(Optional.of(training));
        when(userRepository.findById(3L)).thenReturn(Optional.of(admin));

        trainingService.deleteTraining(1L, 3L);

        verify(trainingRepo).deleteById(1L);
    }

    @Test
    void showTrainingsTest() {
        when(trainingRepo.findAll()).thenReturn(List.of(training));

        List<TrainingResponse> result = trainingService.ShowTrainings();

        assertEquals(1, result.size());
        assertEquals("Soccer Techniques", result.get(0).title());
    }

    @Test
    void showTrainingTest() {
        when(trainingRepo.findById(1L)).thenReturn(Optional.of(training));

        TrainingResponse result = trainingService.ShowTraining(1L);

        assertNotNull(result);
        assertEquals("Soccer Techniques", result.title());
    }

    @Test
    void joinTrainingTest() {
        when(trainingRepo.findById(1L)).thenReturn(Optional.of(training));
        when(userRepository.findById(2L)).thenReturn(Optional.of(player));
        when(trainingRepo.save(any())).thenReturn(training);

        TrainingResponse res = trainingService.joinTraining(1L, 2L);

        assertNotNull(res);
        assertTrue(training.getParticipants().contains(player));
    }

    @Test
    void leaveTrainingTest() {
        training.getParticipants().add(player);
        when(trainingRepo.findById(1L)).thenReturn(Optional.of(training));
        when(userRepository.findById(2L)).thenReturn(Optional.of(player));
        when(trainingRepo.save(any())).thenReturn(training);

        TrainingResponse res = trainingService.leaveTraining(1L, 2L);

        assertNotNull(res);
        assertFalse(training.getParticipants().contains(player));
    }
}