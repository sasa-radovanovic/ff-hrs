package domain.responses;

import lombok.Data;

import java.util.List;

/**
 * Created by sasaradovanovic on 1/2/18.
 */
@Data
public class TeamOverviewData {

    private int totalTeams;

    private boolean available;

    private int offset;

    private int limit;

    private List<Team> teams;

}
