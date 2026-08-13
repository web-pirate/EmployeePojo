package employee;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AboutAppService {

    @Value("${app.name:Employee Management App}")
    private String name;
    @Value("${app.version:1.0.0}")
    private String version;
    public String getName(){
return name;
    }
    public String getVersion( ){
return version;
    }
}
