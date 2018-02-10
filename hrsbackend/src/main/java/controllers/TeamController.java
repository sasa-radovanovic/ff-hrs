package controllers;

import com.google.gson.Gson;
import domain.requests.AddUserToTeamRequest;

import domain.requests.NewTeamRequest;
import exceptions.ConflictException;
import service.TeamService;
import spark.Request;
import spark.Response;


/**
 * Created by sasaradovanovic on 12/29/17.
 */
public class TeamController {

    private TeamService teamService;


    public TeamController() {
        teamService = new TeamService();
    }


    public String getAllTeams(Request request, Response response) {
        return new Gson().toJson(teamService.getAllTeams(Integer.parseInt(request.params("offset"))));
    }

    public String getTeamData(String teamName) {
        return new Gson().toJson(teamService.getUsersFromTeam(teamName));
    }

    public String getPartialSearchForNewUser(String teamName, String criteria) {
        System.out.println("Do partial search for " + teamName + " using " + criteria);
        return new Gson().toJson(teamService.partialSearchForNewTeamMembers(teamName, criteria));
    }

    public String createNewTeam (Request request, Response response) throws ConflictException {
        NewTeamRequest newTeamRequest = new Gson().fromJson(request.body(), NewTeamRequest.class);
        return new Gson().toJson(teamService.createTeam(newTeamRequest));
    }

    public String addUserToTeam (Request request, Response response) {
        AddUserToTeamRequest addUserToTeamRequest = new Gson().fromJson(request.body(), AddUserToTeamRequest.class);
        return new Gson().toJson(teamService.addUserToTeam(addUserToTeamRequest));
    }

    public String deleteTeam (Request request, Response response) {
        return new Gson().toJson(teamService.deleteTeam(request.params("name")));
    }

    public String removeUserFromTeam (Request request, Response response) {
        AddUserToTeamRequest addUserToTeamRequest = new Gson().fromJson(request.body(), AddUserToTeamRequest.class);
        return new Gson().toJson(teamService.removeUserFromTeam(addUserToTeamRequest));
    }


    public String addTeamAdminToTeam (Request request, Response response) {
        AddUserToTeamRequest addUserToTeamRequest = new Gson().fromJson(request.body(), AddUserToTeamRequest.class);
        return new Gson().toJson(teamService.addTeamAdminToTeam(addUserToTeamRequest));
    }


    public String removeTeamAdminFromTeam (Request request, Response response) {
        AddUserToTeamRequest addUserToTeamRequest = new Gson().fromJson(request.body(), AddUserToTeamRequest.class);
        return new Gson().toJson(teamService.removeTeamAdminFromTeam(addUserToTeamRequest));
    }


    public String checkName (Request request, Response response) {
        return new Gson().toJson(teamService.checkName(request.params("name")));
    }
}
