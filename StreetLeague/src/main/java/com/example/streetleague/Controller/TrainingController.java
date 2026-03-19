package com.example.streetleague.Controller;

import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.ServiceInterface.ItrainingService;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.TrainingRequest;
import com.example.streetleague.dto.TrainingResponse;
import com.example.streetleague.dto.TrainingUpdateRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("training")
public class TrainingController {

    ItrainingService trainingService;
    UserRepository   userRepository;

    // POST /training/add?teamId=1&email=coach@mail.com
    @PostMapping("add")
    public TrainingResponse addTraining(@RequestBody TrainingRequest dto,
                                        @RequestParam Long teamId,
                                        @RequestParam String email) {
        User coach = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        return trainingService.addTraining(dto, teamId, coach.getIdUser());
    }

    // PUT /training/update?email=coach@mail.com
    @PutMapping("update")
    public TrainingResponse updateTraining(@RequestBody TrainingUpdateRequest dto,
                                           @RequestParam String email) {
        User coach = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        return trainingService.updateTraining(dto, coach.getIdUser());
    }

    // DELETE /training/delete/1?email=coach@mail.com
    @DeleteMapping("delete/{idTraining}")
    public void deleteTraining(@PathVariable Long idTraining,
                               @RequestParam String email) {
        User coach = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        trainingService.deleteTraining(idTraining, coach.getIdUser());
    }

    // GET /training/showTrainings
    @GetMapping("showTrainings")
    public List<TrainingResponse> showTrainings() {
        return trainingService.ShowTrainings();
    }

    // GET /training/showTrainingById/1
    @GetMapping("showTrainingById/{idTraining}")
    public TrainingResponse showTraining(@PathVariable Long idTraining) {
        return trainingService.ShowTraining(idTraining);
    }

    // POST /training/1/join?email=player@mail.com
    @PostMapping("{idTraining}/join")
    public TrainingResponse joinTraining(@PathVariable Long idTraining,
                                         @RequestParam String email) {
        User player = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        return trainingService.joinTraining(idTraining, player.getIdUser());
    }

    // DELETE /training/1/leave?email=player@mail.com
    @DeleteMapping("{idTraining}/leave")
    public TrainingResponse leaveTraining(@PathVariable Long idTraining,
                                          @RequestParam String email) {
        User player = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        return trainingService.leaveTraining(idTraining, player.getIdUser());
    }
}