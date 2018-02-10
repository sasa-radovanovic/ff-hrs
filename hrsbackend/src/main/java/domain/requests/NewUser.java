package domain.requests;

import lombok.Data;

/**
 * Created by sasaradovanovic on 12/30/17.
 */
@Data
public class NewUser {

    private String username;

    private String email;

    private String firstName;

    private String lastName;

}
