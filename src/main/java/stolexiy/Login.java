package stolexiy;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class Login {
    public String getLogged() {
        return "logged";
    }
}
