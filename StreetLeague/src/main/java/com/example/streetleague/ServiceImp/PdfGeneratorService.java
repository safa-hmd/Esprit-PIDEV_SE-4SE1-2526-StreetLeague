package com.example.streetleague.ServiceImp;

import com.example.streetleague.Entity.AccommodationRequest;
import com.example.streetleague.Repository.AccommodationRequestRepository;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.exception.BusinessValidationException;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PdfGeneratorService {

    private final AccommodationRequestRepository requestRepository;
    private final UserRepository userRepository;

    public byte[] generateRequestPdf(Long requestId) {
        AccommodationRequest req = requestRepository.findById(requestId)
            .orElseThrow(() -> new BusinessValidationException(
                "Request not found"));

        List<String> memberNames = req.getMemberIds().stream()
            .map(memberId -> userRepository.findById(memberId)
                .map(u -> u.getFullName() != null ? 
                    u.getFullName() : u.getEmail())
                .orElse("Member #" + memberId))
            .collect(Collectors.toList());

        boolean isApproved = "APPROVED".equals(req.getStatus());

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream content = 
                new PDPageContentStream(document, page)) {

                float margin = 50;
                float yStart = 780;
                float y = yStart;
                float lineHeight = 20;

                // Header
                content.setFont(PDType1Font.HELVETICA_BOLD, 18);
                content.beginText();
                content.newLineAtOffset(margin, y);
                content.showText("STREETLEAGUE - ACCOMMODATION REQUEST");
                content.endText();
                y -= lineHeight * 2;

                // Status
                content.setFont(PDType1Font.HELVETICA_BOLD, 14);
                content.beginText();
                content.newLineAtOffset(margin, y);
                String statusText = isApproved ? 
                    "STATUS: APPROVED" : "STATUS: REJECTED";
                content.showText(statusText);
                content.endText();
                y -= lineHeight * 2;

                // Request details
                content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                content.beginText();
                content.newLineAtOffset(margin, y);
                content.showText("REQUEST DETAILS");
                content.endText();
                y -= lineHeight;

                content.setFont(PDType1Font.HELVETICA, 11);
                
                String[][] details = {
                    {"Request ID:", "#" + req.getId()},
                    {"Coach:", req.getCoachName() != null ? 
                        req.getCoachName() : String.valueOf(req.getCoachId())},
                    {"Date:", LocalDate.now().toString()},
                    {"Tournament ID:", String.valueOf(req.getTournamentId())}
                };

                for (String[] row : details) {
                    content.beginText();
                    content.newLineAtOffset(margin, y);
                    content.showText(row[0] + " " + row[1]);
                    content.endText();
                    y -= lineHeight;
                }

                y -= lineHeight;

                // Accommodation details
                content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                content.beginText();
                content.newLineAtOffset(margin, y);
                content.showText("ACCOMMODATION DETAILS");
                content.endText();
                y -= lineHeight;

                if (req.getAccommodation() != null) {
                    content.setFont(PDType1Font.HELVETICA, 11);
                    String[][] accDetails = {
                        {"Type:", req.getAccommodation().getType() != null ?
                            req.getAccommodation().getType().toString() : ""},
                        {"Address:", req.getAccommodation().getAddress() != null ?
                            req.getAccommodation().getAddress() : ""},
                        {"Formula:", req.getAccommodation().getFormula() != null ?
                            req.getAccommodation().getFormula().toString() : ""},
                        {"Duration:", req.getAccommodation().getNumberOfNights() 
                            + " nights"},
                        {"Price/Night:", req.getAccommodation().getPricePerNight() 
                            + " TND"}
                    };

                    for (String[] row : accDetails) {
                        content.beginText();
                        content.newLineAtOffset(margin, y);
                        content.showText(row[0] + " " + row[1]);
                        content.endText();
                        y -= lineHeight;
                    }
                }

                y -= lineHeight;

                // Members
                content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                content.beginText();
                content.newLineAtOffset(margin, y);
                content.showText("SELECTED PLAYERS (" + 
                    memberNames.size() + ")");
                content.endText();
                y -= lineHeight;

                content.setFont(PDType1Font.HELVETICA, 11);
                for (String name : memberNames) {
                    content.beginText();
                    content.newLineAtOffset(margin, y);
                    content.showText("  - " + name);
                    content.endText();
                    y -= lineHeight;
                }

                y -= lineHeight;

                // Financial summary
                content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                content.beginText();
                content.newLineAtOffset(margin, y);
                content.showText("FINANCIAL SUMMARY");
                content.endText();
                y -= lineHeight;

                content.setFont(PDType1Font.HELVETICA, 11);
                content.beginText();
                content.newLineAtOffset(margin, y);
                content.showText("TOTAL AMOUNT: " + 
                    req.getTotalAmount() + " TND");
                content.endText();
                y -= lineHeight * 2;

                // Admin comment
                if (req.getAdminComment() != null && 
                    !req.getAdminComment().isEmpty()) {
                    content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    content.beginText();
                    content.newLineAtOffset(margin, y);
                    content.showText(isApproved ? 
                        "APPROVAL COMMENT:" : "REJECTION REASON:");
                    content.endText();
                    y -= lineHeight;

                    content.setFont(PDType1Font.HELVETICA, 11);
                    content.beginText();
                    content.newLineAtOffset(margin, y);
                    content.showText(req.getAdminComment());
                    content.endText();
                    y -= lineHeight * 2;
                }

                // Footer
                content.setFont(PDType1Font.HELVETICA_OBLIQUE, 9);
                content.beginText();
                content.newLineAtOffset(margin, 50);
                content.showText(
                    "Generated by StreetLeague System on " + 
                    LocalDate.now().toString());
                content.endText();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(
                "PDF generation failed: " + e.getMessage(), e);
        }
    }
}
