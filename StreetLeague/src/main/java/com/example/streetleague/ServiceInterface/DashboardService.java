package com.example.streetleague.ServiceInterface;

import com.example.streetleague.dto.*;
import java.util.List;

public interface DashboardService {
    FinancialSummaryDto getFinancialSummary();
    List<RevenueByFieldDto> getRevenueByField();
    List<RevenueBySportDto> getRevenueBySport();
    List<RevenueByMonthDto> getRevenueByMonth();
    List<TopPlayerDto> getTopPlayers();
}