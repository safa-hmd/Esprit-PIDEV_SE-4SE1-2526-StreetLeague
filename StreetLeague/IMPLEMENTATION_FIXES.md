# StreetLeague Accommodation Module — Backend Implementation Fixes

## CRITICAL FIXES REQUIRED

### FIX 1: Update pom.xml — Add PDF Library

Add the iText PDF library to dependencies. Open `pom.xml` and add this before `</dependencies>`:

```xml
<!-- iText for PDF Generation -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itextpdf</artifactId>
    <version>5.5.13.3</version>
</dependency>
```

---

### FIX 2: Update AccommodationRequest Entity

Add missing fields to `src/main/java/com/example/streetleague/Entity/AccommodationRequest.java`:

**ADD these fields after the existing fields:**

```java
@Column(name = "coach_name")
private String coachName;

@Column(name = "decided_at")
private LocalDateTime decidedAt;
```

**Complete updated class:**

```java
package com.example.streetleague.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccommodationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "accommodation_id", nullable = false)
    Accommodation accommodation;

    Long tournamentId;
    Long coachId;
    
    @Column(name = "coach_name")
    String coachName;

    @ElementCollection
    @CollectionTable(name = "accommodation_request_members", joinColumns = @JoinColumn(name = "request_id"))
    @Column(name = "member_id")
    List<Long> memberIds;

    @Builder.Default
    @Column(name = "status", columnDefinition = "VARCHAR(20) DEFAULT 'PENDING'")
    private String status = "PENDING";

    @Column(name = "admin_comment")
    private String adminComment;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "decided_at")
    private LocalDateTime decidedAt;

    @Column(name = "total_amount")
    private Double totalAmount;
}
```

---

### FIX 3: Update AccommodationRequestResponseDto

Add two fields to `src/main/java/com/example/streetleague/dto/AccommodationRequestResponseDto.java`:

**ADD these fields:**

```java
private String coachName;
private LocalDateTime decidedAt;
```

**Complete updated DTO:**

```java
package com.example.streetleague.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationRequestResponseDto {
    private Long id;
    private AccommodationDto accommodation;
    private List<Long> memberIds;
    private Long tournamentId;
    private Long coachId;
    private String coachName;
    private String status;
    private String adminComment;
    private LocalDateTime createdAt;
    private LocalDateTime decidedAt;
    private Double totalAmount;
}
```

---

### FIX 4: Create PdfGeneratorService

Create new file: `src/main/java/com/example/streetleague/service/PdfGeneratorService.java`

```java
package com.example.streetleague.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfGeneratorService {

    public byte[] generateAccommodationConfirmationPdf(
            Long requestId,
            String coachName,
            String accommodationType,
            String address,
            String formula,
            Integer numberOfNights,
            Double pricePerNight,
            List<String> memberNames,
            Double totalAmount,
            String status,
            String adminComment,
            LocalDateTime decidedAt) {

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Add header
            if ("APPROVED".equals(status)) {
                addHeader(document, "ACCOMMODATION REQUEST CONFIRMED", Color.GREEN);
            } else {
                addHeader(document, "ACCOMMODATION REQUEST REJECTED", Color.RED);
            }

            // Add request details
            addSection(document, "REQUEST DETAILS");
            addField(document, "Request ID:", "#REQ-" + requestId);
            addField(document, "Coach:", coachName);
            addField(document, "Status:", status + (" APPROVED".equals(status) ? " ✓" : " ✗"));
            if (decidedAt != null) {
                addField(document, "Date:", formatDate(decidedAt));
            }

            // Add accommodation details
            addSection(document, "ACCOMMODATION DETAILS");
            addField(document, "Type:", accommodationType);
            addField(document, "Formula:", formatFormula(formula));
            addField(document, "Address:", address);
            addField(document, "Duration:", numberOfNights + " nights");
            addField(document, "Price/Night:", pricePerNight + " TND");

            // Add members
            addSection(document, "SELECTED MEMBERS (" + memberNames.size() + ")");
            for (String member : memberNames) {
                addField(document, "✓", member);
            }

            // Add financial summary
            addSection(document, "FINANCIAL SUMMARY");
            addField(document, "Price/Night:", pricePerNight + " TND");
            addField(document, "# Nights:", numberOfNights.toString());
            addField(document, "# Members:", memberNames.size() + "");
            
            Paragraph totalPara = new Paragraph();
            totalPara.add(new Chunk("TOTAL AMOUNT: ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.RED)));
            totalPara.add(new Chunk(totalAmount + " TND", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.RED)));
            totalPara.setSpacingBefore(10);
            totalPara.setSpacingAfter(10);
            document.add(totalPara);

            // Add reason if rejected
            if ("REJECTED".equals(status)) {
                addSection(document, "REJECTION REASON");
                if (adminComment != null && !adminComment.isEmpty()) {
                    Paragraph reason = new Paragraph(adminComment, FontFactory.getFont(FontFactory.HELVETICA, 11));
                    reason.setSpacingBefore(5);
                    document.add(reason);
                }
            } else if (adminComment != null && !adminComment.isEmpty()) {
                addSection(document, "ADMIN COMMENT");
                Paragraph comment = new Paragraph(adminComment, FontFactory.getFont(FontFactory.HELVETICA, 11));
                comment.setSpacingBefore(5);
                document.add(comment);
            }

            // Add footer
            Paragraph footer = new Paragraph();
            footer.add(new Chunk("Generated on: " + formatDate(LocalDateTime.now()), 
                    FontFactory.getFont(FontFactory.HELVETICA, 9, Color.GRAY)));
            footer.setSpacingBefore(20);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage());
        }
    }

    private void addHeader(Document document, String title, Color color) throws DocumentException {
        Paragraph header = new Paragraph();
        header.add(new Chunk("═══════════════════════════════════════════════════════════════\n", 
                FontFactory.getFont(FontFactory.COURIER, 12, color)));
        header.add(new Chunk("StreetLeague\n", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK)));
        header.add(new Chunk(title + "\n", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, color)));
        header.add(new Chunk("═══════════════════════════════════════════════════════════════\n", 
                FontFactory.getFont(FontFactory.COURIER, 12, color)));
        header.setSpacingAfter(20);
        document.add(header);
    }

    private void addSection(Document document, String title) throws DocumentException {
        Paragraph section = new Paragraph();
        section.add(new Chunk(title + "\n", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK)));
        section.add(new Chunk("───────────────────────────────────────────────────────────────\n", 
                FontFactory.getFont(FontFactory.COURIER, 10, Color.LIGHT_GRAY)));
        section.setSpacingBefore(15);
        section.setSpacingAfter(10);
        document.add(section);
    }

    private void addField(Document document, String label, String value) throws DocumentException {
        Paragraph para = new Paragraph();
        para.add(new Chunk(label, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
        para.add(new Chunk("  " + value, FontFactory.getFont(FontFactory.HELVETICA, 11)));
        para.setSpacingBefore(3);
        para.setSpacingAfter(3);
        document.add(para);
    }

    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) return "-";
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    private String formatFormula(String formula) {
        if (formula == null) return "-";
        return formula.replace("_", " ");
    }
}
```

---

### FIX 5: Add Method to AccommodationRequestRepository

Add this method to `src/main/java/com/example/streetleague/Repository/AccommodationRequestRepository.java`:

```java
List<AccommodationRequest> findByStatus(String status);
```

**Updated repository:**

```java
package com.example.streetleague.Repository;

import com.example.streetleague.Entity.AccommodationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccommodationRequestRepository extends JpaRepository<AccommodationRequest, Long> {
    List<AccommodationRequest> findByCoachId(Long coachId);
    List<AccommodationRequest> findByAccommodationId(Long accommodationId);
    List<AccommodationRequest> findByStatus(String status);
}
```

---

### FIX 6: Update CoachTravelService Interface

Add these methods to `src/main/java/com/example/streetleague/ServiceInterface/CoachTravelService.java`:

```java
byte[] downloadAccommodationRequestPdf(Long requestId);
```

---

### FIX 7: Update CoachTravelServiceImpl

Update `src/main/java/com/example/streetleague/ServiceImp/CoachTravelServiceImpl.java`:

**Add import:**
```java
import com.example.streetleague.service.PdfGeneratorService;
import com.example.streetleague.domain.User;
```

**Add field:**
```java
private final PdfGeneratorService pdfGeneratorService;
```

**Add method:**
```java
@Override
public byte[] downloadAccommodationRequestPdf(Long requestId) {
    AccommodationRequest request = accommodationRequestRepository
            .findById(requestId)
            .orElseThrow(() -> new BusinessValidationException("Request not found"));

    // Get member names
    List<String> memberNames = new ArrayList<>();
    if (request.getMemberIds() != null && !request.getMemberIds().isEmpty()) {
        for (Long memberId : request.getMemberIds()) {
            User user = userRepository.findById(memberId).orElse(null);
            if (user != null) {
                memberNames.add(user.getFullName() != null ? user.getFullName() : "Member " + memberId);
            }
        }
    }

    return pdfGeneratorService.generateAccommodationConfirmationPdf(
            request.getId(),
            request.getCoachName(),
            request.getAccommodation().getType(),
            request.getAccommodation().getAddress(),
            request.getAccommodation().getFormula(),
            request.getAccommodation().getNumberOfNights(),
            request.getAccommodation().getPricePerNight(),
            memberNames,
            request.getTotalAmount(),
            request.getStatus(),
            request.getAdminComment(),
            request.getDecidedAt()
    );
}
```

**Update buildAccommodationRequestResponse method to include coachName and decidedAt:**

```java
private AccommodationRequestResponseDto buildAccommodationRequestResponse(
        AccommodationRequest saved, Accommodation accommodation) {

    return AccommodationRequestResponseDto.builder()
            .id(saved.getId())
            .accommodation(AccommodationDto.builder()
                    .id(accommodation.getId())
                    .type(accommodation.getType())
                    .numberOfNights(accommodation.getNumberOfNights())
                    .pricePerNight(accommodation.getPricePerNight())
                    .address(accommodation.getAddress())
                    .formula(accommodation.getFormula())
                    .capacity(accommodation.getCapacity())
                    .status(accommodation.getStatus())
                    .build())
            .memberIds(saved.getMemberIds())
            .tournamentId(saved.getTournamentId())
            .coachId(saved.getCoachId())
            .coachName(saved.getCoachName())
            .status(saved.getStatus())
            .adminComment(saved.getAdminComment())
            .createdAt(saved.getCreatedAt())
            .decidedAt(saved.getDecidedAt())
            .totalAmount(saved.getTotalAmount())
            .build();
}
```

**Update submitAccommodationRequest to include coachName:**

```java
@Override
@Transactional
public AccommodationRequestResponseDto submitAccommodationRequest(
        AccommodationRequestDto requestDto) {

    Accommodation accommodation = accommodationRepository
            .findById(requestDto.getAccommodationId())
            .orElseThrow(() -> new BusinessValidationException(
                    "Accommodation not found"));

    // Get coach name from database
    String coachName = requestDto.getCoachName();
    if (coachName == null || coachName.isEmpty()) {
        User coach = userRepository.findById(requestDto.getCoachId()).orElse(null);
        if (coach != null) {
            coachName = coach.getFullName() != null ? coach.getFullName() : "Coach";
        } else {
            coachName = "Coach";
        }
    }

    AccommodationRequest request = AccommodationRequest.builder()
            .accommodation(accommodation)
            .memberIds(requestDto.getMemberIds())
            .tournamentId(requestDto.getTournamentId())
            .coachId(requestDto.getCoachId() != null
                    ? requestDto.getCoachId() : 1L)
            .coachName(coachName)
            .totalAmount(requestDto.getTotalAmount())
            .build();

    AccommodationRequest saved =
            accommodationRequestRepository.save(request);

    return buildAccommodationRequestResponse(saved, accommodation);
}
```

**Update getMyAccommodationRequests to filter by coachId:**

```java
@Override
public List<AccommodationRequestResponseDto> getMyAccommodationRequests(
        Long coachId) {

    List<AccommodationRequestResponseDto> result = new ArrayList<>();
    for (AccommodationRequest saved :
            accommodationRequestRepository.findByCoachId(coachId)) {
        result.add(buildAccommodationRequestResponse(
                saved, saved.getAccommodation()));
    }
    return result;
}
```

---

### FIX 8: Update AdminTravelService Interface

Update `src/main/java/com/example/streetleague/ServiceInterface/AdminTravelService.java`:

**Add these methods:**

```java
import com.example.streetleague.dto.AccommodationRequestResponseDto;

// Add these method signatures:
List<AccommodationRequestResponseDto> getAllAccommodationRequests();
AccommodationRequestResponseDto approveAccommodationRequest(Long id, DecisionDto decision);
AccommodationRequestResponseDto rejectAccommodationRequest(Long id, DecisionDto decision);
byte[] downloadAccommodationRequestPdf(Long requestId);
```

---

### FIX 9: Update AdminTravelServiceImpl

Add to `src/main/java/com/example/streetleague/ServiceImp/AdminTravelServiceImpl.java`:

**Add imports:**
```java
import com.example.streetleague.Entity.AccommodationRequest;
import com.example.streetleague.Repository.AccommodationRequestRepository;
import com.example.streetleague.dto.AccommodationRequestResponseDto;
import com.example.streetleague.service.PdfGeneratorService;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.domain.User;
import java.time.LocalDateTime;
```

**Add fields:**
```java
private final AccommodationRequestRepository accommodationRequestRepository;
private final PdfGeneratorService pdfGeneratorService;
private final UserRepository userRepository;
```

**Add methods:**

```java
@Override
public List<AccommodationRequestResponseDto> getAllAccommodationRequests() {
    return accommodationRequestRepository.findAll()
            .stream()
            .map(this::mapToAccommodationRequestResponseDto)
            .collect(Collectors.toList());
}

@Override
@Transactional
public AccommodationRequestResponseDto approveAccommodationRequest(
        Long id, DecisionDto decision) {
    
    AccommodationRequest req = accommodationRequestRepository
            .findById(id)
            .orElseThrow(() -> new BusinessValidationException("Request not found"));
    
    req.setStatus("APPROVED");
    if (decision != null && decision.getAdminComment() != null) {
        req.setAdminComment(decision.getAdminComment());
    }
    req.setDecidedAt(LocalDateTime.now());
    
    AccommodationRequest saved = accommodationRequestRepository.save(req);
    return mapToAccommodationRequestResponseDto(saved);
}

@Override
@Transactional
public AccommodationRequestResponseDto rejectAccommodationRequest(
        Long id, DecisionDto decision) {
    
    AccommodationRequest req = accommodationRequestRepository
            .findById(id)
            .orElseThrow(() -> new BusinessValidationException("Request not found"));
    
    req.setStatus("REJECTED");
    if (decision != null && decision.getAdminComment() != null) {
        req.setAdminComment(decision.getAdminComment());
    }
    req.setDecidedAt(LocalDateTime.now());
    
    AccommodationRequest saved = accommodationRequestRepository.save(req);
    return mapToAccommodationRequestResponseDto(saved);
}

@Override
public byte[] downloadAccommodationRequestPdf(Long requestId) {
    AccommodationRequest request = accommodationRequestRepository
            .findById(requestId)
            .orElseThrow(() -> new BusinessValidationException("Request not found"));

    // Get member names
    List<String> memberNames = new ArrayList<>();
    if (request.getMemberIds() != null && !request.getMemberIds().isEmpty()) {
        for (Long memberId : request.getMemberIds()) {
            User user = userRepository.findById(memberId).orElse(null);
            if (user != null) {
                memberNames.add(user.getFullName() != null ? user.getFullName() : "Member " + memberId);
            }
        }
    }

    return pdfGeneratorService.generateAccommodationConfirmationPdf(
            request.getId(),
            request.getCoachName(),
            request.getAccommodation().getType(),
            request.getAccommodation().getAddress(),
            request.getAccommodation().getFormula(),
            request.getAccommodation().getNumberOfNights(),
            request.getAccommodation().getPricePerNight(),
            memberNames,
            request.getTotalAmount(),
            request.getStatus(),
            request.getAdminComment(),
            request.getDecidedAt()
    );
}

private AccommodationRequestResponseDto mapToAccommodationRequestResponseDto(
        AccommodationRequest req) {
    return AccommodationRequestResponseDto.builder()
            .id(req.getId())
            .coachId(req.getCoachId())
            .coachName(req.getCoachName())
            .tournamentId(req.getTournamentId())
            .memberIds(req.getMemberIds())
            .totalAmount(req.getTotalAmount())
            .status(req.getStatus())
            .adminComment(req.getAdminComment())
            .createdAt(req.getCreatedAt())
            .decidedAt(req.getDecidedAt())
            .accommodation(req.getAccommodation() != null ?
                AccommodationDto.builder()
                    .id(req.getAccommodation().getId())
                    .type(req.getAccommodation().getType())
                    .formula(req.getAccommodation().getFormula())
                    .numberOfNights(req.getAccommodation().getNumberOfNights())
                    .pricePerNight(req.getAccommodation().getPricePerNight())
                    .address(req.getAccommodation().getAddress())
                    .capacity(req.getAccommodation().getCapacity())
                    .status(req.getAccommodation().getStatus())
                    .build() : null)
            .build();
}
```

---

### FIX 10: Add Endpoints to CoachTravelController

Add to `src/main/java/com/example/streetleague/Controller/CoachTravelController.java`:

```java
@GetMapping("/accommodation-requests/{id}/pdf")
public ResponseEntity<byte[]> downloadAccommodationRequestPdf(
        @PathVariable Long id) {
    byte[] pdfBytes = coachTravelService.downloadAccommodationRequestPdf(id);
    return ResponseEntity.ok()
            .header("Content-Type", "application/pdf")
            .header("Content-Disposition", "attachment; filename=accommodation-request-" + id + ".pdf")
            .body(pdfBytes);
}
```

---

### FIX 11: Add Endpoints to AdminTravelController

Add to `src/main/java/com/example/streetleague/Controller/AdminTravelController.java`:

```java
@GetMapping("/accommodation-requests")
public ResponseEntity<List<AccommodationRequestResponseDto>> getAllAccommodationRequests() {
    return ResponseEntity.ok(
            adminTravelService.getAllAccommodationRequests());
}

@PutMapping("/accommodation-requests/{id}/approve")
public ResponseEntity<AccommodationRequestResponseDto> approveAccommodationRequest(
        @PathVariable Long id,
        @RequestBody(required = false) DecisionDto decision) {
    return ResponseEntity.ok(
            adminTravelService.approveAccommodationRequest(id, decision));
}

@PutMapping("/accommodation-requests/{id}/reject")
public ResponseEntity<AccommodationRequestResponseDto> rejectAccommodationRequest(
        @PathVariable Long id,
        @RequestBody DecisionDto decision) {
    return ResponseEntity.ok(
            adminTravelService.rejectAccommodationRequest(id, decision));
}

@GetMapping("/accommodation-requests/{id}/pdf")
public ResponseEntity<byte[]> downloadAccommodationRequestPdf(
        @PathVariable Long id) {
    byte[] pdfBytes = adminTravelService.downloadAccommodationRequestPdf(id);
    return ResponseEntity.ok()
            .header("Content-Type", "application/pdf")
            .header("Content-Disposition", "attachment; filename=accommodation-request-" + id + ".pdf")
            .body(pdfBytes);
}
```

---

### FIX 12: Create Database Migration

Run this SQL in your MySQL database:

```sql
ALTER TABLE accommodation_request 
ADD COLUMN IF NOT EXISTS coach_name VARCHAR(255);

ALTER TABLE accommodation_request 
ADD COLUMN IF NOT EXISTS decided_at DATETIME;

ALTER TABLE accommodation_request 
MODIFY COLUMN status VARCHAR(20) DEFAULT 'PENDING';
```

---

## VERIFICATION CHECKLIST

After applying all fixes:

- [ ] pom.xml has iText dependency
- [ ] AccommodationRequest entity has coachName and decidedAt fields
- [ ] AccommodationRequestResponseDto has coachName and decidedAt fields
- [ ] PdfGeneratorService.java created
- [ ] AccommodationRequestRepository has findByStatus method
- [ ] CoachTravelService has downloadAccommodationRequestPdf method
- [ ] CoachTravelServiceImpl implements the new method
- [ ] AdminTravelService has new AccommodationRequest methods
- [ ] AdminTravelServiceImpl implements all new methods
- [ ] CoachTravelController has /accommodation-requests/{id}/pdf endpoint
- [ ] AdminTravelController has all 4 new endpoints
- [ ] Database migration SQL executed
- [ ] Backend compiles without errors
- [ ] Frontend calls working APIs
- [ ] PDFs generate successfully

---

## COMPILATION & TESTING

1. **Clean & Build:**
   ```bash
   mvn clean install
   ```

2. **Run Backend:**
   ```bash
   mvn spring-boot:run
   ```

3. **Test in Frontend:**
   - Admin can see all accommodation requests (test with F12 Network tab)
   - Admin can approve requests (status changes to APPROVED)
   - Admin can reject requests (status changes to REJECTED)
   - PDF downloads work for both coach and admin
   - Coach can see "My Requests" table with PDF buttons

