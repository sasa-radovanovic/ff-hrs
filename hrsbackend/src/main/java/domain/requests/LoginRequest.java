package domain.requests;

import lombok.Data;

/**
 * Created by sasaradovanovic on 12/28/17.
 */
@Data
public class LoginRequest {

    private String username;

    private String password;

}
