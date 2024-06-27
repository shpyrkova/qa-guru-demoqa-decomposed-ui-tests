package models.lombok;

import lombok.Data;

@Data
public class LoginRequestBodyModel {
    private String userName = System.getProperty("username");
    private String password = System.getProperty("password");
}
