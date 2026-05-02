package com.example.streetleague.ServiceImp;

import com.example.streetleague.Repository.PaymentRepository;
import com.example.streetleague.ServiceInterface.DashboardService;
import com.example.streetleague.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.streetleague.Entity.PaymentStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImp implements DashboardService {

    private final PaymentRepository paymentRepository;

    @Override
    public FinancialSummaryDto getFinancialSummary() {
        Double totalRevenue = paymentRepository.sumPaidRevenue();
        Long totalPayments  = paymentRepository.countByStatus(PaymentStatus.PAID);
        Long totalRefunds   = paymentRepository.countByStatus(PaymentStatus.REFUNDED);
        Long pendingCount   = paymentRepository.countByStatus(PaymentStatus.PENDING);

        return new FinancialSummaryDto(
                totalRevenue  != null ? totalRevenue : 0.0,
                totalPayments != null ? totalPayments : 0L,
                totalRefunds  != null ? totalRefunds  : 0L,
                pendingCount  != null ? pendingCount  : 0L
        );
    }



    @Override
    public List<RevenueByFieldDto> getRevenueByField() {
        return paymentRepository.revenueByField();
    }

    @Override
    public List<RevenueBySportDto> getRevenueBySport() {
        return paymentRepository.revenueBySport();
    }
/*
    @Override
    public List<RevenueByMonthDto> getRevenueByMonth() {
        return paymentRepository.revenueByMonth();
    }*/

    @Override
    public List<RevenueByMonthDto> getRevenueByMonth() {
        return paymentRepository.revenueByDay();
    }

    @Override
    public List<TopPlayerDto> getTopPlayers() {
        return paymentRepository.topPlayers();
    }
}