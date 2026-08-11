package employee;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ApiController {

    @RequestMapping(value = "/say-hello", method = RequestMethod.POST)
    public UserData sayHello(@RequestBody UserForm userForm) {
        UserData data = new UserData();
        data.setName(userForm.getName());
        data.setMessage("Humangasore!!");
        return data;
    }

    @RequestMapping(value = "/say-goodbye", method = RequestMethod.GET)
    public String sayGoodbye() {
        return "Goodbye, World!";
    }
}
