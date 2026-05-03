package com.example.streetleague.ServiceImp;

import com.example.streetleague.ServiceInterface.TransporteurService;
import com.example.streetleague.domain.Transporteur;
import com.example.streetleague.dto.TransporteurDTO;
import com.example.streetleague.Repository.TransporteurRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransporteurServiceImp implements TransporteurService {

    private final TransporteurRepository transporteurRepository;

    @Override
    public Transporteur createTransporteur(@Valid TransporteurDTO dto) {
        Transporteur t = Transporteur.builder()
                .nomSociete(dto.getNomSociete())
                .telephone(dto.getTelephone())
                .email(dto.getEmail())
                .build();
        return transporteurRepository.save(t);
    }

    @Override
    public Transporteur updateTransporteur(Long id, @Valid TransporteurDTO dto) {
        Transporteur t = transporteurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transporteur introuvable"));
        t.setNomSociete(dto.getNomSociete());
        t.setTelephone(dto.getTelephone());
        t.setEmail(dto.getEmail());
        return transporteurRepository.save(t);
    }

    @Override
    public void deleteTransporteur(Long id) {
        transporteurRepository.deleteById(id);
    }

    @Override
    public Transporteur getTransporteurById(Long id) {
        return transporteurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transporteur introuvable"));
    }

    @Override
    public List<Transporteur> getAllTransporteurs() {
        return transporteurRepository.findAll();
    }
}