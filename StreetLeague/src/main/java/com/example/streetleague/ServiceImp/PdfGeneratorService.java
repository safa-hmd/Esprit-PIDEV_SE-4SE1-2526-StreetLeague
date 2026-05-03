package com.example.streetleague.ServiceImp;

import com.example.streetleague.Entity.AccommodationRequest;
import com.example.streetleague.Entity.Transport;
import com.example.streetleague.Entity.TravelRequest;
import com.example.streetleague.Repository.AccommodationRequestRepository;
import com.example.streetleague.Repository.TransportRepository;
import com.example.streetleague.Repository.TravelRequestRepository;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.exception.BusinessValidationException;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PdfGeneratorService {

    private final AccommodationRequestRepository requestRepository;
    private final TransportRepository transportRepository;
    private final TravelRequestRepository travelRequestRepository;
    private final UserRepository userRepository;

    private static final Color PRIMARY_COLOR = new Color(26, 35, 126); // Deep Indigo
    private static final Color SECONDARY_COLOR = new Color(13, 71, 161); // Blue
    private static final Color TEXT_COLOR = new Color(33, 33, 33);
    private static final float MARGIN = 50;

    private void drawHeader(PDPageContentStream content, String title, String subtitle) throws Exception {
        // Draw Header Background
        content.setNonStrokingColor(PRIMARY_COLOR);
        content.addRect(0, 750, 600, 100);
        content.fill();

        // Logo/Title Text
        content.setNonStrokingColor(Color.WHITE);
       // content.setFont(PDType1Font.HELVETICA_BOLD, 22);
        content.beginText();
        content.newLineAtOffset(MARGIN, 800);
        content.showText("STREETLEAGUE");
        content.endText();

       // content.setFont(PDType1Font.HELVETICA, 12);
        content.beginText();
        content.newLineAtOffset(MARGIN, 780);
        content.showText("ATHLETIC EDITORIAL - LOGISTICS HQ");
        content.endText();

        // Right side: Document Type
       // content.setFont(PDType1Font.HELVETICA_BOLD, 14);
        content.beginText();
        content.newLineAtOffset(400, 800);
        content.showText(title);
        content.endText();

        if (subtitle != null) {
           // content.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
            content.beginText();
            content.newLineAtOffset(400, 780);
            content.showText(subtitle);
            content.endText();
        }

        // Horizontal Line
        content.setStrokingColor(SECONDARY_COLOR);
        content.setLineWidth(2);
        content.moveTo(MARGIN, 740);
        content.lineTo(545, 740);
        content.stroke();
    }

    private void drawFooter(PDPageContentStream content) throws Exception {
        content.setStrokingColor(new Color(200, 200, 200));
        content.setLineWidth(1);
        content.moveTo(MARGIN, 60);
        content.lineTo(545, 60);
        content.stroke();

        content.setNonStrokingColor(new Color(158, 158, 158));
        //content.setFont(PDType1Font.HELVETICA_OBLIQUE, 8);
        content.beginText();
        content.newLineAtOffset(MARGIN, 40);
        content.showText("This document is generated dynamically by the StreetLeague Management System. Proprietary & Confidential.");
        content.endText();

        content.beginText();
        content.newLineAtOffset(450, 40);
        content.showText("Export Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        content.endText();
    }

    public byte[] generateTransportPdf(Long transportId) {
        Transport transport = transportRepository.findById(transportId)
                .orElseThrow(() -> new BusinessValidationException("Transport not found"));

        boolean isApproved = "APPROVED".equals(transport.getStatus());
        String ownerName = "StreetLeague Official";
        if (transport.getCoachId() != null) {
            ownerName = userRepository.findById(transport.getCoachId())
                    .map(u -> (u.getFullName() != null ? u.getFullName() : u.getEmail()) + " (COACH)")
                    .orElse("Official Partner");
        }

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                drawHeader(content, "TRANSPORT DEED", transport.getType() != null ? transport.getType().toString() : "Logistics Asset");

                float y = 700;
                float lineHeight = 22;

                // Decision Marker
                content.setNonStrokingColor(isApproved ? new Color(46, 125, 50) : new Color(198, 40, 40));
                //content.setFont(PDType1Font.HELVETICA_BOLD, 14);
                content.beginText();
                content.newLineAtOffset(MARGIN, y);
                content.showText("CURRENT STATUS: " + transport.getStatus().toUpperCase());
                content.endText();
                y -= lineHeight * 2;

                // Owner
                content.setNonStrokingColor(TEXT_COLOR);
                //content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                content.beginText();
                content.newLineAtOffset(MARGIN, y);
                content.showText("REGISTERED CARRIER: " + ownerName);
                content.endText();
                y -= lineHeight * 1.5f;

                // Table Section
               // content.setFont(PDType1Font.HELVETICA_BOLD, 11);
                content.beginText();
                content.newLineAtOffset(MARGIN, y);
                content.showText("LOGISTICS CHARACTERISTICS");
                content.endText();
                y -= lineHeight;

                //content.setFont(PDType1Font.HELVETICA, 11);
                String[][] details = {
                    {"Destination Hub:", transport.getDestination() != null ? transport.getDestination() : "Regional Center"},
                    {"Internal Rate:", (transport.getPricePerSeat() != null ? transport.getPricePerSeat() : 0.0) + " TND per occupant"},
                    {"Available Seats:", transport.getAvailableSeats() + " Units"},
                    {"Initial Departure:", transport.getDepartureTime() != null ? transport.getDepartureTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy 'at' HH:mm")) : "To be defined"},
                    {"Estimated Return:", transport.getReturnTime() != null ? transport.getReturnTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy 'at' HH:mm")) : "To be defined"}
                };

                for (String[] row : details) {
                    content.beginText();
                    content.newLineAtOffset(MARGIN + 10, y);
                    content.showText(row[0]);
                    content.endText();

                   // content.setFont(PDType1Font.HELVETICA_BOLD, 11);
                    content.beginText();
                    content.newLineAtOffset(MARGIN + 180, y);
                    content.showText(row[1]);
                    content.endText();
                   // content.setFont(PDType1Font.HELVETICA, 11);

                    y -= lineHeight;
                }

                drawFooter(content);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Transport PDF generation failed: " + e.getMessage(), e);
        }
    }

    public byte[] generateTravelRequestPdf(Long requestId) {
        TravelRequest req = travelRequestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessValidationException("Travel Request not found"));

        List<String> memberNames = req.getSelectedMemberIds() != null ? req.getSelectedMemberIds().stream()
            .map(memberId -> userRepository.findById(memberId)
                .map(u -> (u.getFullName() != null ? u.getFullName() : u.getEmail()) + " (" + (u.getRole() != null ? u.getRole().toString() : "PLAYER") + ")")
                .orElse("Member #" + memberId))
            .collect(Collectors.toList()) : List.of();

        String coachName = "Official Delegation Lead";
        if (req.getTeam() != null && req.getTeam().getCaptain() != null) {
            coachName = req.getTeam().getCaptain().getFullName() + " (LEAD)";
        }

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                drawHeader(content, "TRANSIT VOUCHER", req.getTeam() != null ? "TEAM: " + req.getTeam().getName() : "Team Mobility");

                float y = 700;
                float lineHeight = 20;

                // Status Block
                content.setNonStrokingColor(req.getStatus() == null || "PENDING".equals(req.getStatus().toString()) ? new Color(255, 152, 0) : 
                    ("APPROVED".equals(req.getStatus().toString()) ? new Color(46, 125, 50) : new Color(198, 40, 40)));
                
               // content.setFont(PDType1Font.HELVETICA_BOLD, 14);
                content.beginText();
                content.newLineAtOffset(MARGIN, y);
                content.showText("VALIDATION STATUS: " + (req.getStatus() != null ? req.getStatus().toString() : "PENDING"));
                content.endText();
                y -= lineHeight * 2;

                content.setNonStrokingColor(TEXT_COLOR);
                //content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                content.beginText();
                content.newLineAtOffset(MARGIN, y);
                content.showText("DELEGATION METRICS");
                content.endText();
                y -= lineHeight;

                //content.setFont(PDType1Font.HELVETICA, 10);
                String[][] details = {
                    {"Delegation Lead:", coachName},
                    {"Host Tournament:", req.getTournament() != null ? req.getTournament().getName() + " (" + req.getTournament().getCity() + ")" : "Official Tournament"},
                    {"Event Schedule:", req.getTournament() != null ? req.getTournament().getStartDate() + " to " + req.getTournament().getEndDate() : "N/A"},
                    {"Transport Mode:", (req.getTransport() != null && req.getTransport().getType() != null) ? req.getTransport().getType().toString() : "BUS (Standard)"},
                    {"Allocated Budget:", (req.getTotalAmount() != null ? req.getTotalAmount() : 0.0) + " TND"},
                    {"Validation Note:", req.getAdminComment() != null ? req.getAdminComment() : "System reviewed & processed."}
                };

                for (String[] row : details) {
                    content.beginText();
                    content.newLineAtOffset(MARGIN + 10, y);
                    content.showText(row[0]);
                    content.endText();

                   // content.setFont(PDType1Font.HELVETICA_BOLD, 10);
                    content.beginText();
                    content.newLineAtOffset(MARGIN + 150, y);
                    content.showText(row[1]);
                    content.endText();
                  //  content.setFont(PDType1Font.HELVETICA, 10);

                    y -= lineHeight;
                }

                // Member List
                y -= 10;
               // content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                content.beginText();
                content.newLineAtOffset(MARGIN, y);
                content.showText("PASSENGER MANIFEST (" + memberNames.size() + " Total)");
                content.endText();
                y -= lineHeight;

               // content.setFont(PDType1Font.HELVETICA, 9);
                for (String name : memberNames) {
                    if (y < 80) {
                        content.beginText();
                        content.newLineAtOffset(MARGIN + 10, y);
                        content.showText("... more members listed in digital records.");
                        content.endText();
                        break;
                    }
                    content.beginText();
                    content.newLineAtOffset(MARGIN + 10, y);
                    content.showText("• " + name);
                    content.endText();
                    y -= 12;
                }

                drawFooter(content);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Travel Request PDF failed: " + e.getMessage(), e);
        }
    }

    public byte[] generateRequestPdf(Long requestId) {
        AccommodationRequest req = requestRepository.findById(requestId)
            .orElseThrow(() -> new BusinessValidationException("Request not found"));

        List<String> memberNames = req.getMemberIds() != null ? req.getMemberIds().stream()
            .map(memberId -> userRepository.findById(memberId)
                .map(u -> (u.getFullName() != null ? u.getFullName() : u.getEmail()) + " (" + (u.getRole() != null ? u.getRole().toString() : "PLAYER") + ")")
                .orElse("Member #" + memberId))
            .collect(Collectors.toList()) : List.of();

        boolean isApproved = "APPROVED".equals(req.getStatus());

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                drawHeader(content, "LODGING VOUCHER", req.getAccommodation() != null ? "VOUCHER#" + requestId : "Accommodation Voucher");

                float y = 700;
                float lineHeight = 18;

                // Decision Block
                content.setNonStrokingColor(isApproved ? new Color(46, 125, 50) : ("REJECTED".equals(req.getStatus()) ? new Color(198, 40, 40) : new Color(255, 152, 0)));
               // content.setFont(PDType1Font.HELVETICA_BOLD, 14);
                content.beginText();
                content.newLineAtOffset(MARGIN, y);
                content.showText("RESERVATION STATUS: " + (req.getStatus() != null ? req.getStatus().toUpperCase() : "PENDING"));
                content.endText();
                y -= lineHeight * 2;

                content.setNonStrokingColor(TEXT_COLOR);
                //content.setFont(PDType1Font.HELVETICA, 10);
                String[][] details = {
                    {"Delegation Manager:", (req.getCoachName() != null ? req.getCoachName() : "Official Coach") + " (COACH)"},
                    {"Tournament Context:", "Ref #" + req.getTournamentId()},
                    {"Consolidated Amount:", (req.getTotalAmount() != null ? req.getTotalAmount() : 0.0) + " TND"},
                    {"Admin Assessment:", req.getAdminComment() != null ? req.getAdminComment() : "System reviewed & processed."}
                };

                for (String[] row : details) {
                    content.beginText();
                    content.newLineAtOffset(MARGIN + 10, y);
                    content.showText(row[0]);
                    content.endText();

                  //  content.setFont(PDType1Font.HELVETICA_BOLD, 10);
                    content.beginText();
                    content.newLineAtOffset(MARGIN + 150, y);
                    content.showText(row[1]);
                    content.endText();
                    //content.setFont(PDType1Font.HELVETICA, 10);
                    y -= lineHeight;
                }

                if (req.getAccommodation() != null) {
                    y -= 10;
                  //  content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    content.beginText();
                    content.newLineAtOffset(MARGIN, y);
                    content.showText("FACILITY SPECIFICATIONS");
                    content.endText();
                    y -= lineHeight;

                    //content.setFont(PDType1Font.HELVETICA, 10);
                    String[][] accDetails = {
                        {"Facility Category:", req.getAccommodation().getType() != null ? req.getAccommodation().getType().toString() : "N/A"},
                        {"Geographical Site:", req.getAccommodation().getAddress() != null ? req.getAccommodation().getAddress() : "N/A"},
                        {"Boarding Formula:", req.getAccommodation().getFormula() != null ? req.getAccommodation().getFormula().toString() : "N/A"},
                        {"Planned Stay:", (req.getAccommodation().getNumberOfNights() != null ? req.getAccommodation().getNumberOfNights() : 0) + " consecutive nights"}
                    };

                    for (String[] row : accDetails) {
                        content.beginText();
                        content.newLineAtOffset(MARGIN + 10, y);
                        content.showText(row[0]);
                        content.endText();

                       /// content.setFont(PDType1Font.HELVETICA_BOLD, 10);
                        content.beginText();
                        content.newLineAtOffset(MARGIN + 150, y);
                        content.showText(row[1]);
                        content.endText();
                       // content.setFont(PDType1Font.HELVETICA, 10);
                        y -= lineHeight;
                    }
                }

                y -= 10;
                //content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                content.beginText();
                content.newLineAtOffset(MARGIN, y);
                content.showText("LODGING DELEGATION (" + memberNames.size() + " Total)");
                content.endText();
                y -= lineHeight;

              //  content.setFont(PDType1Font.HELVETICA, 9);
                for (String name : memberNames) {
                    if (y < 80) break;
                    content.beginText();
                    content.newLineAtOffset(MARGIN + 10, y);
                    content.showText("• " + name);
                    content.endText();
                    y -= 12;
                }

                drawFooter(content);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Accommodation PDF failed: " + e.getMessage(), e);
        }
    }
}
