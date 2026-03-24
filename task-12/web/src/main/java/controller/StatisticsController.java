package controller;

import model.entity.DTO.BookDTO;
import model.entity.DTO.OrderDTO;
import model.service.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/orders")
    public List<OrderDTO> getCompletedOrders(
                @RequestParam("start_date") LocalDate startDate,
                @RequestParam("end_date") LocalDate endDate
            ) {
        return statisticsService.getCompletedOrdersByPeriod(startDate, endDate)
                .stream()
                .map(OrderDTO::new)
                .toList();
    }

    @GetMapping("/income")
    public double getIncome(
            @RequestParam("start_date") LocalDate startDate,
            @RequestParam("end_date") LocalDate endDate
            ) {
        return statisticsService.getIncomeByPeriod(startDate, endDate);
    }

    @GetMapping("/books")
    public List<BookDTO> getOverstockedBooks() {
        return statisticsService.getOverstockedBooks()
                .stream()
                .map(BookDTO::new)
                .toList();
    }
}
