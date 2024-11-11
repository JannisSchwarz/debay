package azubi.debay;

import azubi.debay.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Service
public class AuthService {

    public User checkLoggedIn(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new UnauthorizedException("User not found in the session.");
        }
        return user;
    }

    @ExceptionHandler(UnauthorizedException.class)
    public String handleUnauthorized(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Please log in to access this page.");
        return "redirect:/";
    }
    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }
}
