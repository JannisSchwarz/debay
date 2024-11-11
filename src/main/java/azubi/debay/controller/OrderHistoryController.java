package azubi.debay.controller;

import azubi.debay.AuthService;
import azubi.debay.entity.OrderHistory;
import azubi.debay.entity.User;
import azubi.debay.repository.OrderHistoryRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/orders")
public class OrderHistoryController {

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    private final AuthService authorizer = new AuthService();

    @GetMapping("/history")
    public String getOrderHistory(HttpSession session, Model model) {
        User user = authorizer.checkLoggedIn(session);

        List<OrderHistory> orderHistories = orderHistoryRepository.findByUser(user);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<String> formattedDates = orderHistories.stream()
                .map(order -> order.getPurchaseDate().format(formatter))
                .collect(Collectors.toList());

        model.addAttribute("orderHistories", orderHistories);
        model.addAttribute("formattedDates", formattedDates);

        return "orderHistory";
    }
}
