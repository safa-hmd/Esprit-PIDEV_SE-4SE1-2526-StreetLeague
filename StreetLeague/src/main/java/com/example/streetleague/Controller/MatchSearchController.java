// MatchSearchController.java
package com.example.streetleague.Controller;

import com.example.streetleague.ServiceImp.MatchSearchService;
import com.example.streetleague.dto.MatchResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matches")
@CrossOrigin("*")
public class MatchSearchController {

    private final MatchSearchService matchSearchService;

    public MatchSearchController(MatchSearchService matchSearchService) {
        this.matchSearchService = matchSearchService;
    }

    @GetMapping("/search")
    public List<MatchResponse> searchMatches(
            @RequestParam String keyword,
            @RequestParam(required = false) List<String> status) {

        return matchSearchService.searchMatches(keyword, status);
    }
}