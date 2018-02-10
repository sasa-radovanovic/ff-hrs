package domain;

import com.google.gson.Gson;
import domain.responses.*;
import models.HrsRequests;
import models.HrsTeam;
import models.HrsUser;
import spark.Response;
import utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sasaradovanovic on 12/28/17.
 */
public class ResponseBuilder {


    public User hrsUserToUserDto(HrsUser hrsUser) {
        User user = new User();
        user.setFirstName(hrsUser.getFirstName());
        user.setLastName(hrsUser.getLastName());
        user.setEmail(hrsUser.getEmail());
        user.setUsername(hrsUser.getUsername());
        user.setActive(hrsUser.getActive());
        if (hrsUser.getSuperAdmin() != null && hrsUser.getSuperAdmin().booleanValue() == true) {
            user.setRole(Constants.Roles.ROLE_ADMIN.toString());
        } else if (hrsUser.getTeamsAdmin() != null && hrsUser.getTeamsAdmin().size() > 0) {
            user.setRole(Constants.Roles.ROLE_TEAM_ADMIN.toString());
        } else {
            user.setRole(Constants.Roles.ROLE_USER.toString());
        }
        return user;
    }


    public Team hrsTeamToTeamDto(HrsTeam hrsTeam) {
        Team team = new Team();
        team.setName(hrsTeam.getTeamName());
        team.setMembers(hrsTeam.getTeamMembers());
        return team;
    }


    public TeamDetailed hrsTeamToTeamDetailedDto(HrsTeam hrsTeam, List<UserDetailed> users, List<UserDetailed> teamAdmins) {
        TeamDetailed teamDetailed = new TeamDetailed();
        teamDetailed.setName(hrsTeam.getTeamName());
        teamDetailed.setTeamAdmins(teamAdmins);
        teamDetailed.setUsers(users);
        return teamDetailed;
    }


    public UserDetailed formUserDetailed (HrsUser hrsUser, List<HrsRequests> requests) {
        UserDetailed user = new UserDetailed();
        user.setFirstName(hrsUser.getFirstName());
        user.setLastName(hrsUser.getLastName());
        user.setEmail(hrsUser.getEmail());
        user.setUsername(hrsUser.getUsername());
        user.setTeam(hrsUser.getTeam());
        user.setAdminTeams(hrsUser.getTeamsAdmin());
        user.setActive(hrsUser.getActive());
        if (hrsUser.getSuperAdmin() != null && hrsUser.getSuperAdmin().booleanValue() == true) {
            user.setRole(Constants.Roles.ROLE_ADMIN.toString());
        } else if (hrsUser.getTeamsAdmin() != null && hrsUser.getTeamsAdmin().size() > 0) {
            user.setRole(Constants.Roles.ROLE_TEAM_ADMIN.toString());
        } else {
            user.setRole(Constants.Roles.ROLE_USER.toString());
        }
        ArrayList<HolidayRequest> holidayRequests = new ArrayList<>();
        requests.forEach(r -> {
            holidayRequests.add(hrsRequestToHolidayRequest(r));
        });
        user.setRequests(holidayRequests);
        return user;
    }


    public HolidayRequest hrsRequestToHolidayRequest (HrsRequests hrsRequests) {
        HolidayRequest holidayRequest = new HolidayRequest();
        holidayRequest.setStartDate(Constants.TimeConstants.formatter.format(hrsRequests.getStartDate()));
        holidayRequest.setEndDate(Constants.TimeConstants.formatter.format(hrsRequests.getEndDate()));
        holidayRequest.setApprovedByTeamAdmin(hrsRequests.isApprovedByTeamAdmin());
        holidayRequest.setApprovedBySuperAdmin(hrsRequests.isApprovedByAdmin());
        holidayRequest.setId(hrsRequests.getId().toString());
        return holidayRequest;
    }

    public HolidayRequestDetailed hrsRequestToHolidayRequestDetailed (HrsRequests hrsRequests) {
        HolidayRequestDetailed holidayRequest = new HolidayRequestDetailed();
        holidayRequest.setStartDate(Constants.TimeConstants.formatter.format(hrsRequests.getStartDate()));
        holidayRequest.setEndDate(Constants.TimeConstants.formatter.format(hrsRequests.getEndDate()));
        holidayRequest.setApprovedByTeamAdmin(hrsRequests.isApprovedByTeamAdmin());
        holidayRequest.setApprovedBySuperAdmin(hrsRequests.isApprovedByAdmin());
        holidayRequest.setId(hrsRequests.getId().toString());
        holidayRequest.setFirstName(hrsRequests.getUser().getFirstName());
        holidayRequest.setLastName(hrsRequests.getUser().getLastName());
        holidayRequest.setTeam(hrsRequests.getUser().getTeam());
        holidayRequest.setUsername(hrsRequests.getUser().getUsername());
        return holidayRequest;
    }


    public PartialSearchUserDetails hrsUserToPartialSearchUser (HrsUser hrsUser) {
        PartialSearchUserDetails partialSearchUserDetails = new PartialSearchUserDetails();
        partialSearchUserDetails.setFirstName(hrsUser.getFirstName());
        partialSearchUserDetails.setLastName(hrsUser.getLastName());
        partialSearchUserDetails.setUsername(hrsUser.getUsername());
        partialSearchUserDetails.setEmail(hrsUser.getEmail());
        partialSearchUserDetails.setTeam(hrsUser.getTeam());
        return partialSearchUserDetails;
    }

}
