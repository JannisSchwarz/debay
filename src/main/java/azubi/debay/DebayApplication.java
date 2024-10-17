package azubi.debay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@SpringBootApplication
@RestController
public class DebayApplication {


	@GetMapping("/index")
	public String index(){
		return "index";
	}

	public static void main(String[] args) {
		SpringApplication.run(DebayApplication.class, args);
	}
	@Autowired
	private ItemRepository itemRepository;


	public List<Item> getAllItems() {
		return itemRepository.findAll();
	}
}
