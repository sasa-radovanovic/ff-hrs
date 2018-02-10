package service;

import database.DatabaseHelper;
import domain.ResponseBuilder;
import domain.requests.AddUserToTeamRequest;
import domain.requests.NewTeamRequest;
import domain.responses.*;
import exceptions.ConflictException;
import models.HrsTeam;
import models.HrsUser;
import org.mongodb.morphia.query.Criteria;
import org.mongodb.morphia.query.FindOptions;
import org.mongodb.morphia.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sasaradovanovic on 12/29/17.
 */
public class TeamService {


    private DatabaseHelper databaseHelper;

    private ResponseBuilder responseBuilder;

    private UserService userService;


    public TeamService() {
        databaseHelper = new DatabaseHelper();
        responseBuilder = new ResponseBuilder();
        userService = new UserService();
    }


    public TeamOverviewData getAllTeams(int offset) {
        TeamOverviewData teamOverviewData = new TeamOverviewData();
        List<Team> teams = new ArrayList<>();
        int totalTeams = (int) databaseHelper.getDataStore().find(HrsTeam.class).count();

        List<HrsTeam> hrsTeams;
        Query<HrsTeam> hrsTeamsQuery = databaseHelper.getDataStore().createQuery(HrsTeam.class);
        if (offset == -1) {
            hrsTeams = hrsTeamsQuery.asList();
        } else {
            hrsTeams = hrsTeamsQuery.offset(offset).limit(10).asList();
        }

        if (hrsTeams != null && hrsTeams.size() > 0) {
            hrsTeams.forEach(t -> {
                teams.add(responseBuilder.hrsTeamToTeamDto(t));
            });
        }
        teamOverviewData.setLimit(10);
        teamOverviewData.setOffset(offset);
        teamOverviewData.setAvailable((totalTeams - offset) > 10 ? true : false);
        teamOverviewData.setTeams(teams);
        teamOverviewData.setTotalTeams(totalTeams);
        return teamOverviewData;
    }

    public TeamDetailed getUsersFromTeam(String teamName) {
        List<HrsUser> users = databaseHelper.getDataStore().createQuery(HrsUser.class)
                .field("team")
                .equalIgnoreCase(teamName).asList();

        List<UserDetailed> usersDetailed = users.stream().map(u -> {
            return userService.getDetailedUserData(u.getUsername());
        }).collect(Collectors.toList());

        List<HrsUser> teamAdmins = databaseHelper.getDataStore().createQuery(HrsUser.class)
                .field("team_admin")
                .contains(teamName).asList();

        List<UserDetailed> teamAdminsDetailed = teamAdmins.stream().map(u -> {
            return userService.getDetailedUserData(u.getUsername());
        }).collect(Collectors.toList());

        HrsTeam hrsTeam = databaseHelper.getDataStore().createQuery(HrsTeam.class)
                .field("team_name")
                .equalIgnoreCase(teamName).get();

        return responseBuilder.hrsTeamToTeamDetailedDto(hrsTeam, usersDetailed, teamAdminsDetailed);

    }


    public TeamDetailed createTeam (NewTeamRequest newTeamRequest) throws ConflictException {
        HrsTeam existingTeam = databaseHelper.getDataStore()
                .createQuery(HrsTeam.class)
                .field("team_name")
                .equalIgnoreCase(newTeamRequest.getName())
                .get();

        if (existingTeam != null) {
            throw new ConflictException("Team with given name already exists!");
        }

        HrsTeam newTeam = new HrsTeam();
        newTeam.setTeamName(newTeamRequest.getName());
        newTeam.setTeamMembers(0);

        databaseHelper.getDataStore().save(newTeam);

        return responseBuilder.hrsTeamToTeamDetailedDto(newTeam, new ArrayList<>(), new ArrayList<>());
    }


    public User addUserToTeam (AddUserToTeamRequest addUserToTeamRequest) {

        System.out.println(" Add user " + addUserToTeamRequest.getUsername() + " to team " + addUserToTeamRequest.getTeam());

        try {
            HrsUser userBeforeUpdate = databaseHelper.getDataStore().find(HrsUser.class).field("username").equalIgnoreCase(addUserToTeamRequest.getUsername()).get();

            if (userBeforeUpdate.getTeam() != null) {
                databaseHelper.getDataStore().update(
                        databaseHelper.getDataStore().find(HrsTeam.class).field("team_name").equalIgnoreCase(userBeforeUpdate.getTeam()),
                        databaseHelper.getDataStore().createUpdateOperations(HrsTeam.class).dec("team_members")
                );
            }

            databaseHelper.getDataStore().update(
                    databaseHelper.getDataStore().find(HrsUser.class).field("username").equalIgnoreCase(addUserToTeamRequest.getUsername()),
                    databaseHelper.getDataStore().createUpdateOperations(HrsUser.class).set("team", addUserToTeamRequest.getTeam()));

            databaseHelper.getDataStore().update(
                    databaseHelper.getDataStore().find(HrsTeam.class).field("team_name").equalIgnoreCase(addUserToTeamRequest.getTeam()),
                    databaseHelper.getDataStore().createUpdateOperations(HrsTeam.class).inc("team_members")
            );

            return responseBuilder.hrsUserToUserDto(databaseHelper.getDataStore()
                    .find(HrsUser.class)
                    .field("username")
                    .equalIgnoreCase(addUserToTeamRequest.getUsername()).get());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public User removeUserFromTeam (AddUserToTeamRequest addUserToTeamRequest) {


        databaseHelper.getDataStore().update(
                databaseHelper.getDataStore().find(HrsUser.class).field("username").equalIgnoreCase(addUserToTeamRequest.getUsername()),
                databaseHelper.getDataStore().createUpdateOperations(HrsUser.class).set("team", ""));


        databaseHelper.getDataStore().update(
                databaseHelper.getDataStore().find(HrsTeam.class).field("team_name").equalIgnoreCase(addUserToTeamRequest.getTeam()),
                databaseHelper.getDataStore().createUpdateOperations(HrsTeam.class).dec("team_members"));

        return responseBuilder.hrsUserToUserDto(databaseHelper.getDataStore()
                .find(HrsUser.class)
                .field("username")
                .equalIgnoreCase(addUserToTeamRequest.getUsername()).get());

    }



    public Team deleteTeam (String name) {
        databaseHelper.getDataStore().delete(
                databaseHelper.getDataStore().createQuery(HrsTeam.class).field("team_name").equalIgnoreCase(name)
        );
        databaseHelper.getDataStore().update(
                databaseHelper.getDataStore().find(HrsUser.class).field("team").equalIgnoreCase(name),
                databaseHelper.getDataStore().createUpdateOperations(HrsUser.class).set("team", "")
        );

        Team team = new Team();

        team.setName(name);
        team.setMembers(0);

        return team;
    }


    public User addTeamAdminToTeam (AddUserToTeamRequest addUserToTeamRequest) {


        HrsUser hrsUser = databaseHelper.getDataStore().find(HrsUser.class).field("username").equalIgnoreCase(addUserToTeamRequest.getUsername()).get();

        if (hrsUser.getTeamsAdmin() == null) {
            List<String> teamsAdmin = new ArrayList<>();
            teamsAdmin.add(addUserToTeamRequest.getTeam());
            hrsUser.setTeamsAdmin(teamsAdmin);
        } else {
            if (!hrsUser.getTeamsAdmin().contains(addUserToTeamRequest.getTeam())) {
                hrsUser.getTeamsAdmin().add(addUserToTeamRequest.getTeam());
            }
        }

        databaseHelper.getDataStore().save(hrsUser);

        return responseBuilder.hrsUserToUserDto(hrsUser);

    }



    public User removeTeamAdminFromTeam (AddUserToTeamRequest addUserToTeamRequest) {


        HrsUser hrsUser = databaseHelper.getDataStore().find(HrsUser.class).field("username").equalIgnoreCase(addUserToTeamRequest.getUsername()).get();

        if (hrsUser.getTeamsAdmin() != null) {
            if (hrsUser.getTeamsAdmin().contains(addUserToTeamRequest.getTeam())) {
                hrsUser.getTeamsAdmin().remove(addUserToTeamRequest.getTeam());
            }
        }


        databaseHelper.getDataStore().save(hrsUser);

        return responseBuilder.hrsUserToUserDto(hrsUser);

    }




    public List<PartialSearchUserDetails> partialSearchForNewTeamMembers (String teamName, String criteria) {
        List<PartialSearchUserDetails> returnList = new ArrayList<>();
        try {

            Query<HrsUser> query1 = databaseHelper.getDataStore().createQuery(HrsUser.class);

            query1.or(
                    query1.criteria("first_name").containsIgnoreCase(criteria)
            ).or(
                    query1.criteria("last_name").containsIgnoreCase(criteria)
            ).or(
                    query1.criteria("username").containsIgnoreCase(criteria)
            ).or(
                    query1.criteria("email").containsIgnoreCase(criteria)
            );

            List<HrsUser> users = query1.asList();

            System.out.println(" Found " + users.size());
            System.out.println(" Query " + query1.toString());

            returnList = users.stream().filter(u -> {
                return u.getSuperAdmin().booleanValue() == false;
            }).filter(u -> {
                if (u.getTeam() == null || !u.getTeam().equalsIgnoreCase(teamName)) {
                    return true;
                } else {
                    return false;
                }
            }).filter(u -> {
                if (u.getTeamsAdmin() == null || !u.getTeamsAdmin().contains(teamName)) {
                    return true;
                } else {
                    return false;
                }
            }).map(u -> {
                return responseBuilder.hrsUserToPartialSearchUser(u);
            }).collect(Collectors.toList());


        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnList;
    }



    public Team checkName(String name) {
        HrsTeam hrsTeam = databaseHelper.getDataStore().find(HrsTeam.class).field("team_name").equalIgnoreCase(name).get();

        if (hrsTeam != null) {
            return responseBuilder.hrsTeamToTeamDto(hrsTeam);
        }
        return null;
    }


}
