package domain.responses;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Created by sasaradovanovic on 12/29/17.
 */
@Data
public class LoginResponse {

    private String username;

    private boolean authenticated;

    private String authenticatedTime;

    private int sessionExpiration;

}
