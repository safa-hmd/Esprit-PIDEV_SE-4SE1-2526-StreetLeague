package com.example.streetleague.ServiceInterface;

import com.example.streetleague.dto.TrainingRequest;
import com.example.streetleague.dto.TrainingResponse;
import com.example.streetleague.dto.TrainingUpdateRequest;

import java.util.List;

public interface ItrainingService {
    TrainingResponse addTraining(TrainingRequest t, Long teamId, Long coachId);
    TrainingResponse updateTraining(TrainingUpdateRequest t, Long coachId);
    void deleteTraining(Long idTraining, Long coachId);
    List<TrainingResponse> ShowTrainings();
    TrainingResponse ShowTraining(Long idTraining);
    TrainingResponse joinTraining(Long trainingId, Long playerId);
    TrainingResponse leaveTraining(Long trainingId, Long playerId);
    List<TrainingResponse> getTrainingsByCoach(Long coachId);
}