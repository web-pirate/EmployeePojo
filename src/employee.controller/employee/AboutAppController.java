package employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/about")
public class AboutAppController {
    @Autowired
    private AboutAppService service;

    @GetMapping("")
    public AboutAppData getDetails(){
        AboutAppData d = new AboutAppData();
        d.setName(service.getName());
        d.setVersion(service.getVersion());
        return d;
    }
}
