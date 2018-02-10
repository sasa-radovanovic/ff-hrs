package domain.responses;

import lombok.Data;

/**
 * Created by sasaradovanovic on 12/29/17.
 */
@Data
public class User {

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String role;

    private Boolean active;

}
