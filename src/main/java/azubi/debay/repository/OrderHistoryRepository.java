package azubi.debay.repository;


import azubi.debay.entity.OrderHistory;
import azubi.debay.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {
    List<OrderHistory> findByUser(User user);
}
