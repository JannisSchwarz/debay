package azubi.debay.controller;

import azubi.debay.entity.OrderHistory;
import azubi.debay.entity.User;
import azubi.debay.repository.OrderHistoryRepository;
import azubi.debay.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private UserRepository userRepository;

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    @GetMapping("/history")
    public String getOrderHistory(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/"; // Redirect to login if user is not in session
        }

        List<OrderHistory> orderHistories = orderHistoryRepository.findByUser(user);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<String> formattedDates = orderHistories.stream()
                .map(order -> order.getPurchaseDate().format(formatter))
                .collect(Collectors.toList());

        model.addAttribute("orderHistories", orderHistories);
        model.addAttribute("formattedDates", formattedDates); // Add formatted dates to the model

        return "orderHistory"; // Return the name of the HTML template to render
    }
}
