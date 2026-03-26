package com.example.streetleague.ServiceImp;

import com.example.streetleague.Repository.TournamentRepository;
import com.example.streetleague.ServiceInterface.ITournamentService;
import com.example.streetleague.Entity.Tournament;
import com.example.streetleague.Entity.TournamentStatus;
import com.example.streetleague.dto.TournamentDto;
import com.example.streetleague.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TournamentServiceImp implements ITournamentService {

    private final TournamentRepository tournamentRepository;

    // ---------------- CREATE ----------------

    @Override
    public TournamentDto createTournament(TournamentDto dto) {

        if (tournamentRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new IllegalArgumentException("Tournament with this name already exists");
        }

        Tournament tournament = mapToEntity(dto);
        tournament.setStatus(computeStatus(dto.getStartDate(), dto.getEndDate()));

        return mapToDto(tournamentRepository.save(tournament));
    }

    // ---------------- READ ----------------

    @Override
    @Transactional(readOnly = true)
    public TournamentDto getTournamentById(Long id) {
        return mapToDto(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TournamentDto> getAllTournaments() {
        return tournamentRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TournamentDto> getUpcomingTournaments() {
        return tournamentRepository.findByStatus(TournamentStatus.UPCOMING)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // ---------------- UPDATE ----------------

    @Override
    public TournamentDto updateTournament(Long id, TournamentDto dto) {

        Tournament tournament = findById(id);

        if (tournament.getStatus() == TournamentStatus.CANCELLED) {
            throw new IllegalStateException("Cannot update a cancelled tournament");
        }

        tournament.setName(dto.getName());
        tournament.setDescription(dto.getDescription());
        tournament.setSportType(dto.getSportType());
        tournament.setTournamentType(dto.getTournamentType());
        tournament.setStartDate(dto.getStartDate());
        tournament.setEndDate(dto.getEndDate());
        tournament.setRegistrationDeadline(dto.getRegistrationDeadline());
        tournament.setMaxParticipants(dto.getMaxParticipants());
        tournament.setLocation(dto.getLocation());
        tournament.setPrizePool(dto.getPrizePool());

        if (dto.getStatus() == TournamentStatus.CANCELLED) {
            tournament.setStatus(TournamentStatus.CANCELLED);
        } else {
            tournament.setStatus(computeStatus(dto.getStartDate(), dto.getEndDate()));
        }

        return mapToDto(tournamentRepository.save(tournament));
    }

    // ---------------- DELETE ----------------

    @Override
    public void deleteTournament(Long id) {
        Tournament tournament = findById(id);
        tournamentRepository.delete(tournament);
    }

    // ---------------- CANCEL ----------------

    @Override
    public TournamentDto cancelTournament(Long id) {

        Tournament tournament = findById(id);

        if (tournament.getStatus() == TournamentStatus.CANCELLED) {
            throw new IllegalStateException("Tournament is already cancelled");
        }

        tournament.setStatus(TournamentStatus.CANCELLED);

        return mapToDto(tournamentRepository.save(tournament));
    }

    // ---------------- HELPERS ----------------

    private Tournament findById(Long id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Tournament not found with id: " + id));
    }

    private TournamentDto mapToDto(Tournament t) {

        return TournamentDto.builder()
                .id(t.getId())
                .name(t.getName())
                .description(t.getDescription())
                .sportType(t.getSportType())
                .tournamentType(t.getTournamentType())
                .status(t.getStatus())
                .startDate(t.getStartDate())
                .endDate(t.getEndDate())
                .registrationDeadline(t.getRegistrationDeadline())
                .maxParticipants(t.getMaxParticipants())
                .location(t.getLocation())
                .prizePool(t.getPrizePool())
                .registeredCount(
                        t.getRegistrations() != null ?
                                t.getRegistrations().size() : 0
                )
                .build();
    }

    private Tournament mapToEntity(TournamentDto dto) {

        return Tournament.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .sportType(dto.getSportType())
                .tournamentType(dto.getTournamentType())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .registrationDeadline(dto.getRegistrationDeadline())
                .maxParticipants(dto.getMaxParticipants())
                .location(dto.getLocation())
                .prizePool(dto.getPrizePool())
                .build();
    }
    // ---------------- STATUS LOGIC ----------------

    /**
     * Computes tournament status automatically from dates:
     *
     *  startDate > today          → UPCOMING   (not started yet)
     *  startDate ≤ today ≤ endDate → ONGOING   (in progress)
     *  endDate < today            → COMPLETED  (finished)
     */
    private TournamentStatus computeStatus(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();

        if (startDate == null || endDate == null) {
            return TournamentStatus.UPCOMING;
        }

        if (today.isBefore(startDate)) {
            return TournamentStatus.UPCOMING;
        } else if (!today.isAfter(endDate)) {
            return TournamentStatus.ONGOING;
        } else {
            return TournamentStatus.COMPLETED;
        }
    }

}