package domain.responses;

import lombok.Data;

/**
 * Created by sasaradovanovic on 1/7/18.
 */
@Data
public class PartialSearchUserDetails {

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String team;

}
