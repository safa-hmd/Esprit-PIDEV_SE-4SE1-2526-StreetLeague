package com.example.streetleague.ServiceInterface;

import com.example.streetleague.domain.Transporteur;
import com.example.streetleague.dto.TransporteurDTO;

import java.util.List;

public interface TransporteurService {
    Transporteur createTransporteur(TransporteurDTO dto);
    Transporteur updateTransporteur(Long id, TransporteurDTO dto);
    void deleteTransporteur(Long id);
    Transporteur getTransporteurById(Long id);
    List<Transporteur> getAllTransporteurs();
}