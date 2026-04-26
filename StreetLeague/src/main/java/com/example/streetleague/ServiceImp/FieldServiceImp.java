package com.example.streetleague.ServiceImp;

import com.example.streetleague.Repository.FieldRepository;
import com.example.streetleague.ServiceInterface.IFieldService;
import com.example.streetleague.Entity.Field;
import com.example.streetleague.Entity.SportType;
import com.example.streetleague.dto.FieldDto;
import com.example.streetleague.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FieldServiceImp implements IFieldService {

    private final FieldRepository fieldRepository;
    private final RestTemplate restTemplate;

    @Override
    public FieldDto createField(FieldDto dto) {
        Field field = Field.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .sportType(dto.getSportType())
                .location(dto.getLocation())
                .imageUrl(dto.getImageUrl())
                .pricePerHour(dto.getPricePerHour())
                .capacity(dto.getCapacity())
                .available(true)
                .build();
        return mapToDto(fieldRepository.save(field));
    }

    @Override
    @Transactional(readOnly = true)
    public FieldDto getFieldById(Long id) {
        return mapToDto(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FieldDto> getAllFields() {
        return fieldRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FieldDto> getAvailableFields() {
        return fieldRepository.findByAvailable(true).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FieldDto> getFieldsBySport(SportType sportType) {
        return fieldRepository.findBySportType(sportType).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public FieldDto updateField(Long id, FieldDto dto) {
        Field field = findById(id);
        field.setName(dto.getName());
        field.setDescription(dto.getDescription());
        field.setSportType(dto.getSportType());
        field.setLocation(dto.getLocation());
        field.setImageUrl(dto.getImageUrl());
        field.setPricePerHour(dto.getPricePerHour());
        field.setCapacity(dto.getCapacity());
        field.setAvailable(dto.isAvailable());
        return mapToDto(fieldRepository.save(field));
    }

    @Override
    public void deleteField(Long id) {
        Field field = findById(id);
        fieldRepository.delete(field);
    }

    @Override
    public FieldDto toggleAvailability(Long id) {
        Field field = findById(id);
        field.setAvailable(!field.isAvailable());
        return mapToDto(fieldRepository.save(field));
    }

    // ---- Helpers ----

    private Field findById(Long id) {
        return fieldRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Field not found with id: " + id));
    }

    private FieldDto mapToDto(Field f) {
        return FieldDto.builder()
                .id(f.getId())
                .name(f.getName())
                .description(f.getDescription())
                .sportType(f.getSportType())
                .location(f.getLocation())
                .imageUrl(f.getImageUrl())
                .pricePerHour(f.getPricePerHour())
                .capacity(f.getCapacity())
                .available(f.isAvailable())
                .latitude(f.getLatitude())    // ✅ AJOUTE
                .longitude(f.getLongitude())
                .build();
    }

    @Override
    public FieldDto geocodeField(Long id) {
        Field field = findById(id);

        if (field.getLocation() == null || field.getLocation().isBlank())
            throw new RuntimeException("Pas d'adresse pour ce terrain");

        String[] attempts = {
                field.getLocation() + ", Tunisia",
                field.getLocation().split(",")[0] + ", Tunisia",
                field.getName() + ", " + field.getLocation() + ", Tunisia",
        };

        for (String query : attempts) {
            Double[] coords = callNominatim(query);
            if (coords != null) {
                field.setLatitude(coords[0]);
                field.setLongitude(coords[1]);
                fieldRepository.save(field);
                System.out.println("✅ " + field.getName()
                        + " → " + coords[0] + ", " + coords[1]);
                return mapToDto(field);
            }
            try { Thread.sleep(600); } catch (InterruptedException ignored) {}
        }

        // Fallback par ville
        Double[] fallback = getFallbackCoords(field.getLocation());
        field.setLatitude(fallback[0]);
        field.setLongitude(fallback[1]);
        fieldRepository.save(field);
        return mapToDto(field);
    }

    private Double[] callNominatim(String query) {
        try {
            String url = "https://nominatim.openstreetmap.org/search?q="
                    + java.net.URLEncoder.encode(
                    query, java.nio.charset.StandardCharsets.UTF_8)
                    + "&format=json&limit=1&countrycodes=tn";

            org.springframework.http.HttpHeaders headers =
                    new org.springframework.http.HttpHeaders();
            headers.set("User-Agent", "StreetLeague/1.0");

            var response = restTemplate.exchange(
                    url,
                    org.springframework.http.HttpMethod.GET,
                    new org.springframework.http.HttpEntity<>(headers),
                    Object[].class
            );

            Object[] results = response.getBody();
            if (results != null && results.length > 0) {
                @SuppressWarnings("unchecked")
                var first = (java.util.Map<String, Object>) results[0];
                return new Double[]{
                        Double.parseDouble((String) first.get("lat")),
                        Double.parseDouble((String) first.get("lon"))
                };
            }
        } catch (Exception ignored) {}
        return null;
    }

    private Double[] getFallbackCoords(String location) {
        String loc = location.toLowerCase();
        if (loc.contains("menzah"))   return new Double[]{36.8356, 10.1583};
        if (loc.contains("ariana"))   return new Double[]{36.8625, 10.1956};
        if (loc.contains("marsa"))    return new Double[]{36.8781, 10.3247};
        if (loc.contains("carthage")) return new Double[]{36.8528, 10.3233};
        if (loc.contains("sousse"))   return new Double[]{35.8245, 10.6346};
        if (loc.contains("sfax"))     return new Double[]{34.7406, 10.7603};
        if (loc.contains("nabeul"))   return new Double[]{36.4561, 10.7376};
        if (loc.contains("bizerte"))  return new Double[]{37.2744,  9.8739};
        if (loc.contains("monastir")) return new Double[]{35.7643, 10.8113};
        return new Double[]{36.8190, 10.1658}; // Centre Tunis
    }




    @Override
    public List<FieldDto> getAllFieldsWithGps() {
        return fieldRepository.findAll().stream()
                .filter(f -> f.getLatitude() != null && f.getLongitude() != null)
                .map(this::mapToDto)
                .collect(java.util.stream.Collectors.toList());
    }


}
