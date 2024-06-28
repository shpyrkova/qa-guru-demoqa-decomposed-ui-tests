package models.lombok;

import com.github.javafaker.Faker;
import lombok.Data;

@Data
public class LoginRequestBodyModel {

    private String userName;
    private String password;

}
