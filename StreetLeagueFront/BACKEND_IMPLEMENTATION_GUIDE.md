# BACKEND IMPLEMENTATION GUIDE — Accommodation Request Confirmation Workflow

## OVERVIEW
This guide provides complete backend implementation steps for the accommodation request confirmation workflow. The frontend is now complete and expects the following backend endpoints and functionality.

---

## PART 1: DEPENDENCIES

### Add to pom.xml (Maven)

Locate the `<dependencies>` section and add:

```xml
<!-- PDF Generation -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itextpdf</artifactId>
    <version>5.5.13.3</version>
</dependency>
```

Then rebuild: `mvn clean install`

---

## PART 2: ENTITY UPDATES

### 1. Update AccommodationRequest.java

Make sure it has ALL these fields:

```java
package com.example.streetleague.Entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "accommodation_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "accommodation_id")
    private Accommodation accommodation;

    @Column(name = "tournament_id")
    private Long tournamentId;

    @Column(name = "coach_id")
    private String coachId; // Note: String, not Long (can be email or ID)

    @Column(name = "coach_name")
    private String coachName;

    @ElementCollection
    @CollectionTable(name = "accommodation_request_members", 
                     joinColumns = @JoinColumn(name = "request_id"))
    @Column(name = "member_id")
    private List<Long> memberIds;

    @Column(name = "status", length = 20)
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED

    @Column(name = "admin_comment", length = 500)
    private String adminComment;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "decided_at")
    private LocalDateTime decidedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

---

## PART 3: DTOs

### 1. Create AccommodationRequestDto.java

```java
package com.example.streetleague.Dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationRequestDto {
    private Long accommodationId;
    private Long tournamentId;
    private String coachId;
    private String coachName;
    private List<Long> memberIds;
    private Double totalAmount;
}
```

### 2. Create AccommodationRequestResponseDto.java

```java
package com.example.streetleague.Dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationRequestResponseDto {
    private Long id;
    private AccommodationDto accommodation;
    private Long tournamentId;
    private String coachId;
    private String coachName;
    private List<Long> memberIds;
    private Double totalAmount;
    private String status;
    private String adminComment;
    private String createdAt;
    private String decidedAt;
}
```

### 3. Create DecisionDto.java (for admin decision)

```java
package com.example.streetleague.Dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DecisionDto {
    private String adminComment;
}
```

---

## PART 4: REPOSITORIES

### Create AccommodationRequestRepository.java

```java
package com.example.streetleague.Repository;

import com.example.streetleague.Entity.AccommodationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AccommodationRequestRepository 
        extends JpaRepository<AccommodationRequest, Long> {
    
    List<AccommodationRequest> findByCoachId(String coachId);
    
    List<AccommodationRequest> findByStatus(String status);
    
    List<AccommodationRequest> findAll();
}
```

---

## PART 5: PDF GENERATOR SERVICE

### Create PdfGeneratorService.java

```java
package com.example.streetleague.ServiceImp;

import com.example.streetleague.Entity.AccommodationRequest;
import com.example.streetleague.Entity.Accommodation;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class PdfGeneratorService {

    public byte[] generateConfirmationPdf(
            AccommodationRequest request,
            Accommodation accommodation,
            List<String> memberNames) throws DocumentException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        // Title
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BaseColor.DARK_GRAY);
        Paragraph title = new Paragraph("ACCOMMODATION REQUEST CONFIRMED", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph logo = new Paragraph("StreetLeague", 
                new Font(Font.FontFamily.HELVETICA, 14, Font.ITALIC, new BaseColor(33, 150, 243)));
        logo.setAlignment(Element.ALIGN_CENTER);
        document.add(logo);
        
        document.add(new Paragraph("\n"));

        // Date
        String currentDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
        Paragraph dateP = new Paragraph("Date: " + currentDate, 
                new Font(Font.FontFamily.HELVETICA, 10));
        document.add(dateP);
        document.add(new Paragraph("\n"));

        // REQUEST DETAILS Section
        addSection(document, "REQUEST DETAILS");
        addRow(document, "Request ID:", "#REQ-" + request.getId());
        addRow(document, "Coach:", request.getCoachName());
        addRow(document, "Status:", "APPROVED ✓");
        addRow(document, "Date:", currentDate);
        
        document.add(new Paragraph("\n"));

        // ACCOMMODATION DETAILS
        addSection(document, "ACCOMMODATION DETAILS");
        addRow(document, "Type:", accommodation.getType());
        addRow(document, "Formula:", accommodation.getFormula());
        addRow(document, "Address:", accommodation.getAddress());
        addRow(document, "Duration:", accommodation.getNumberOfNights() + " nights");
        addRow(document, "Price per Night:", accommodation.getPricePerNight() + " TND");
        
        document.add(new Paragraph("\n"));

        // SELECTED MEMBERS
        addSection(document, "SELECTED MEMBERS (" + memberNames.size() + " players)");
        for (String name : memberNames) {
            Paragraph memberP = new Paragraph("✓ " + name, 
                    new Font(Font.FontFamily.HELVETICA, 11));
            memberP.setIndentationLeft(20);
            document.add(memberP);
        }
        
        document.add(new Paragraph("\n"));

        // FINANCIAL SUMMARY
        addSection(document, "FINANCIAL SUMMARY");
        addRow(document, "Price per Night:", accommodation.getPricePerNight() + " TND");
        addRow(document, "Number of Nights:", accommodation.getNumberOfNights().toString());
        addRow(document, "Number of Members:", memberNames.size() + "");
        
        Font totalFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.RED);
        Paragraph totalP = new Paragraph("TOTAL AMOUNT: " + request.getTotalAmount() + " TND", totalFont);
        totalP.setAlignment(Element.ALIGN_RIGHT);
        document.add(totalP);

        document.add(new Paragraph("\n\n"));

        // Footer
        addFooterText(document, "This document serves as official confirmation of accommodation reservation for StreetLeague tournament.");
        addFooterText(document, "Generated on: " + currentDate);
        if (request.getAdminComment() != null && !request.getAdminComment().isEmpty()) {
            addFooterText(document, "Admin Comment: " + request.getAdminComment());
        }

        document.close();
        return outputStream.toByteArray();
    }

    public byte[] generateRejectionPdf(
            AccommodationRequest request,
            Accommodation accommodation,
            String adminComment) throws DocumentException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        // Title
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BaseColor.RED);
        Paragraph title = new Paragraph("ACCOMMODATION REQUEST REJECTED", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph logo = new Paragraph("StreetLeague", 
                new Font(Font.FontFamily.HELVETICA, 14, Font.ITALIC, new BaseColor(33, 150, 243)));
        logo.setAlignment(Element.ALIGN_CENTER);
        document.add(logo);
        
        document.add(new Paragraph("\n"));

        // Date
        String currentDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
        Paragraph dateP = new Paragraph("Date: " + currentDate, 
                new Font(Font.FontFamily.HELVETICA, 10));
        document.add(dateP);
        document.add(new Paragraph("\n"));

        // REQUEST DETAILS
        addSection(document, "REQUEST DETAILS");
        addRow(document, "Request ID:", "#REQ-" + request.getId());
        addRow(document, "Coach:", request.getCoachName());
        addRow(document, "Status:", "REJECTED ✗");
        addRow(document, "Date:", currentDate);
        
        document.add(new Paragraph("\n"));

        // ACCOMMODATION DETAILS
        addSection(document, "ACCOMMODATION REQUESTED");
        addRow(document, "Type:", accommodation.getType());
        addRow(document, "Formula:", accommodation.getFormula());
        addRow(document, "Address:", accommodation.getAddress());
        addRow(document, "Duration:", accommodation.getNumberOfNights() + " nights");
        
        document.add(new Paragraph("\n"));

        // REJECTION REASON
        addSection(document, "REJECTION REASON");
        Font reasonFont = new Font(Font.FontFamily.HELVETICA, 11);
        Paragraph reason = new Paragraph(adminComment != null ? adminComment : "No reason provided", reasonFont);
        reason.setIndentationLeft(20);
        document.add(reason);

        document.add(new Paragraph("\n\n"));

        // Footer
        addFooterText(document, "This request has been rejected by StreetLeague administration.");
        addFooterText(document, "Please contact admin for more information.");
        addFooterText(document, "Generated on: " + currentDate);

        document.close();
        return outputStream.toByteArray();
    }

    private void addSection(Document document, String title) throws DocumentException {
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, BaseColor.DARK_GRAY);
        Paragraph section = new Paragraph(title, sectionFont);
        section.setSpacingBefore(5);
        section.setSpacingAfter(5);
        document.add(section);
    }

    private void addRow(Document document, String label, String value) throws DocumentException {
        Paragraph row = new Paragraph();
        row.add(new Chunk(label + " ", new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD)));
        row.add(new Chunk(value, new Font(Font.FontFamily.HELVETICA, 11)));
        row.setIndentationLeft(20);
        document.add(row);
    }

    private void addFooterText(Document document, String text) throws DocumentException {
        Paragraph p = new Paragraph(text, new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, new BaseColor(100, 100, 100)));
        p.setAlignment(Element.ALIGN_CENTER);
        document.add(p);
    }
}
```

---

## PART 6: SERVICES

### Create CoachTravelService Interface

```java
package com.example.streetleague.Service;

import com.example.streetleague.Dto.*;
import java.util.List;

public interface CoachTravelService {
    AccommodationRequestResponseDto submitAccommodationRequest(AccommodationRequestDto dto);
    List<AccommodationRequestResponseDto> getMyAccommodationRequests(String coachId);
    byte[] generateRequestPdf(Long requestId);
}
```

### Create AdminTravelService Interface

```java
package com.example.streetleague.Service;

import com.example.streetleague.Dto.*;
import java.util.List;

public interface AdminTravelService {
    List<AccommodationRequestResponseDto> getAllAccommodationRequests();
    AccommodationRequestResponseDto approveAccommodationRequest(Long id, DecisionDto decision);
    AccommodationRequestResponseDto rejectAccommodationRequest(Long id, DecisionDto decision);
    byte[] generateRequestPdf(Long requestId);
}
```

### Implement CoachTravelServiceImpl

```java
package com.example.streetleague.ServiceImp;

import com.example.streetleague.Dto.*;
import com.example.streetleague.Entity.*;
import com.example.streetleague.Repository.*;
import com.example.streetleague.Service.CoachTravelService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoachTravelServiceImpl implements CoachTravelService {

    @Autowired
    private AccommodationRequestRepository accommodationRequestRepository;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public AccommodationRequestResponseDto submitAccommodationRequest(AccommodationRequestDto dto) {
        System.out.println("Coach submitting accommodation request: " + dto.getCoachId());

        // Create entity
        AccommodationRequest request = new AccommodationRequest();
        request.setAccommodation(accommodationRepository.findById(dto.getAccommodationId())
                .orElseThrow(() -> new RuntimeException("Accommodation not found")));
        request.setTournamentId(dto.getTournamentId());
        request.setCoachId(dto.getCoachId());
        request.setCoachName(dto.getCoachName());
        request.setMemberIds(dto.getMemberIds());
        request.setTotalAmount(dto.getTotalAmount());
        request.setStatus("PENDING");

        AccommodationRequest saved = accommodationRequestRepository.save(request);
        System.out.println("Request saved with ID: " + saved.getId());

        return convertToDto(saved);
    }

    @Override
    public List<AccommodationRequestResponseDto> getMyAccommodationRequests(String coachId) {
        return accommodationRequestRepository.findByCoachId(coachId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public byte[] generateRequestPdf(Long requestId) throws RuntimeException {
        AccommodationRequest request = accommodationRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        Accommodation accommodation = request.getAccommodation();

        List<String> memberNames = request.getMemberIds().stream()
                .map(id -> userRepository.findById(id)
                        .map(u -> u.getFullName())
                        .orElse("Member #" + id))
                .collect(Collectors.toList());

        try {
            if ("APPROVED".equals(request.getStatus())) {
                return pdfGeneratorService.generateConfirmationPdf(request, accommodation, memberNames);
            } else {
                return pdfGeneratorService.generateRejectionPdf(request, accommodation, request.getAdminComment());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage());
        }
    }

    private AccommodationRequestResponseDto convertToDto(AccommodationRequest entity) {
        return modelMapper.map(entity, AccommodationRequestResponseDto.class);
    }
}
```

### Implement AdminTravelServiceImpl

```java
package com.example.streetleague.ServiceImp;

import com.example.streetleague.Dto.*;
import com.example.streetleague.Entity.*;
import com.example.streetleague.Repository.*;
import com.example.streetleague.Service.AdminTravelService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminTravelServiceImpl implements AdminTravelService {

    @Autowired
    private AccommodationRequestRepository accommodationRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<AccommodationRequestResponseDto> getAllAccommodationRequests() {
        return accommodationRequestRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public AccommodationRequestResponseDto approveAccommodationRequest(Long id, DecisionDto decision) {
        AccommodationRequest request = accommodationRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus("APPROVED");
        request.setAdminComment(decision.getAdminComment());
        request.setDecidedAt(LocalDateTime.now());

        AccommodationRequest saved = accommodationRequestRepository.save(request);
        return convertToDto(saved);
    }

    @Override
    public AccommodationRequestResponseDto rejectAccommodationRequest(Long id, DecisionDto decision) {
        AccommodationRequest request = accommodationRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus("REJECTED");
        request.setAdminComment(decision.getAdminComment());
        request.setDecidedAt(LocalDateTime.now());

        AccommodationRequest saved = accommodationRequestRepository.save(request);
        return convertToDto(saved);
    }

    @Override
    public byte[] generateRequestPdf(Long requestId) {
        AccommodationRequest request = accommodationRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        Accommodation accommodation = request.getAccommodation();

        List<String> memberNames = request.getMemberIds().stream()
                .map(id -> userRepository.findById(id)
                        .map(u -> u.getFullName())
                        .orElse("Member #" + id))
                .collect(Collectors.toList());

        try {
            if ("APPROVED".equals(request.getStatus())) {
                return pdfGeneratorService.generateConfirmationPdf(request, accommodation, memberNames);
            } else {
                return pdfGeneratorService.generateRejectionPdf(request, accommodation, request.getAdminComment());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF");
        }
    }

    private AccommodationRequestResponseDto convertToDto(AccommodationRequest entity) {
        return modelMapper.map(entity, AccommodationRequestResponseDto.class);
    }
}
```

---

## PART 7: CONTROLLERS

### Update/Create CoachTravelController.java

Add these endpoints:

```java
@PostMapping("/accommodation-request")
public ResponseEntity<AccommodationRequestResponseDto> submitRequest(
        @RequestBody AccommodationRequestDto dto) {
    return ResponseEntity.ok(coachTravelService.submitAccommodationRequest(dto));
}

@GetMapping("/accommodation-requests/my")
public ResponseEntity<List<AccommodationRequestResponseDto>> getMyRequests(
        @RequestParam String coachId) {
    return ResponseEntity.ok(coachTravelService.getMyAccommodationRequests(coachId));
}

@GetMapping("/accommodation-requests/{id}/pdf")
public ResponseEntity<byte[]> getCoachPdf(@PathVariable Long id) {
    byte[] pdf = coachTravelService.generateRequestPdf(id);
    return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=request-" + id + ".pdf")
            .header("Content-Type", "application/pdf")
            .body(pdf);
}
```

### Create AdminTravelController.java (or add to existing)

```java
@RestController
@RequestMapping("/api/admin/travel")
public class AdminTravelController {

    @Autowired
    private AdminTravelService adminTravelService;

    @GetMapping("/accommodation-requests")
    public ResponseEntity<List<AccommodationRequestResponseDto>> getAllRequests() {
        return ResponseEntity.ok(adminTravelService.getAllAccommodationRequests());
    }

    @PutMapping("/accommodation-requests/{id}/approve")
    public ResponseEntity<AccommodationRequestResponseDto> approveRequest(
            @PathVariable Long id,
            @RequestBody DecisionDto decision) {
        return ResponseEntity.ok(adminTravelService.approveAccommodationRequest(id, decision));
    }

    @PutMapping("/accommodation-requests/{id}/reject")
    public ResponseEntity<AccommodationRequestResponseDto> rejectRequest(
            @PathVariable Long id,
            @RequestBody DecisionDto decision) {
        return ResponseEntity.ok(adminTravelService.rejectAccommodationRequest(id, decision));
    }

    @GetMapping("/accommodation-requests/{id}/pdf")
    public ResponseEntity<byte[]> getAdminPdf(@PathVariable Long id) {
        byte[] pdf = adminTravelService.generateRequestPdf(id);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=request-" + id + ".pdf")
                .header("Content-Type", "application/pdf")
                .body(pdf);
    }
}
```

---

## PART 8: DATABASE MIGRATION

Create a Liquibase/Flyway migration or SQL script:

```sql
CREATE TABLE accommodation_requests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    accommodation_id BIGINT NOT NULL,
    tournament_id BIGINT,
    coach_id VARCHAR(255),
    coach_name VARCHAR(255),
    status VARCHAR(20) DEFAULT 'PENDING',
    admin_comment VARCHAR(500),
    total_amount DECIMAL(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    decided_at TIMESTAMP NULL,
    FOREIGN KEY (accommodation_id) REFERENCES accommodation(id)
);

CREATE TABLE accommodation_request_members (
    request_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    FOREIGN KEY (request_id) REFERENCES accommodation_requests(id)
);
```

---

## PART 9: TESTING

After implementation, test these endpoints:

### 1. Submit a request
```bash
POST /api/coach/travel/accommodation-request
Content-Type: application/json

{
  "accommodationId": 1,
  "tournamentId": 1,
  "coachId": "7",
  "coachName": "Coach Name",
  "memberIds": [9, 10, 11, 12],
  "totalAmount": 540
}
```

Response: `{ "id": 1, "status": "PENDING", ... }`

### 2. Get coach's requests
```bash
GET /api/coach/travel/accommodation-requests/my?coachId=7
```

### 3. Approve request (admin)
```bash
PUT /api/admin/travel/accommodation-requests/1/approve
Content-Type: application/json

{ "adminComment": "Approved - booking confirmed" }
```

### 4. Download PDF
```bash
GET /api/coach/travel/accommodation-requests/1/pdf
```

Returns PDF file with confirmation or rejection document.

---

## COMPLETION CHECKLIST

- [ ] Add iText dependency to pom.xml
- [ ] Create AccommodationRequest entity with all fields
- [ ] Create DTOs (Request, Response, Decision)
- [ ] Create repository interfaces
- [ ] Create PdfGeneratorService
- [ ] Create service interfaces and implementations
- [ ] Add/update controller endpoints
- [ ] Run database migrations
- [ ] Test all endpoints
- [ ] Rebuild application with `mvn clean install`
- [ ] Restart Spring Boot server

After completing these steps, the frontend workflow will be fully functional!
