package domain.requests;

import lombok.Data;

/**
 * Created by sasaradovanovic on 1/14/18.
 */
@Data
public class SetPasswordRequest {

    private String username;

    private String hash;

    private String password;

}
