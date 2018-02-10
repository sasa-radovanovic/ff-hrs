package service;

import database.DatabaseHelper;
import domain.ResponseBuilder;
import domain.requests.NewUser;
import domain.responses.PartialSearchUserDetails;
import domain.responses.User;
import domain.responses.UserDetailed;
import models.HrsRequests;
import models.HrsTeam;
import models.HrsUser;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import utils.PasswordUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sasaradovanovic on 12/29/17.
 */
public class UserService {

    private DatabaseHelper databaseHelper;

    private ResponseBuilder responseBuilder;

    private EmailService emailService;


    public UserService() {
        databaseHelper = new DatabaseHelper();
        responseBuilder = new ResponseBuilder();
        emailService = new EmailService();
    }


    public boolean checkCredentials(String username, String password) {
        HrsUser hrsUser = databaseHelper.getDataStore().createQuery(HrsUser.class)
                .field("username").equalIgnoreCase(username).get();
        if (hrsUser == null) {
            System.out.println("User not found.");
            return false;
        }

        if (!PasswordUtil.checkPassword(password, hrsUser.getPassword())) {
            System.out.println("Passwords do not match");
            return false;
        }

        if (hrsUser.getActive() == null || hrsUser.getActive().booleanValue() == false) {
            System.out.println("User is not active");
            return false;
        }

        return true;
    }


    public User getUserData(String username) {
        HrsUser hrsUser = databaseHelper.getDataStore().createQuery(HrsUser.class)
                .field("username").equalIgnoreCase(username).get();
        return responseBuilder.hrsUserToUserDto(hrsUser);
    }

    public HrsUser getRawUserData(String username) {
        return databaseHelper.getDataStore().createQuery(HrsUser.class)
                .field("username").equalIgnoreCase(username).get();
    }


    public User createOrUpdateUser(NewUser newUser) {

        HrsUser hrsUser = databaseHelper.getDataStore().find(HrsUser.class).field("username").containsIgnoreCase(newUser.getUsername()).get();

        boolean isNew = false;

        if (hrsUser == null) {
            isNew = true;
            hrsUser = new HrsUser();
            hrsUser.setEmail(newUser.getEmail());
            hrsUser.setTeam("");
            hrsUser.setFirstName(newUser.getFirstName());
            hrsUser.setLastName(newUser.getLastName());
            hrsUser.setTeamsAdmin(new ArrayList<>());
            hrsUser.setSuperAdmin(false);
            hrsUser.setUsername(newUser.getUsername());
            hrsUser.setPassword("");
            hrsUser.setActive(false);

        } else {
            hrsUser.setEmail(newUser.getEmail());
            hrsUser.setFirstName(newUser.getFirstName());
            hrsUser.setLastName(newUser.getLastName());
        }
        databaseHelper.getDataStore().save(hrsUser);

        if (isNew) {
            try {
                HrsUser afterSave = databaseHelper.getDataStore().find(HrsUser.class).field("username").equal(hrsUser.getUsername()).get();
                String activationHash = PasswordUtil.hashActivationCode(afterSave.getUsername());
                System.out.println("ACTIVATION HASH >>>> " + activationHash + " for username " + afterSave.getUsername());
                emailService.sendActivationEmail(hrsUser.getEmail(), activationHash, afterSave.getUsername());
            } catch (Exception e) {
                System.err.println("ERROR SENDING EMAIL");
                e.printStackTrace();
            }
        }

        return responseBuilder.hrsUserToUserDto(hrsUser);
    }


    public User removeUser(String username) {
        HrsUser hrsUser = databaseHelper.getDataStore().find(HrsUser.class).field("username").containsIgnoreCase(username).get();

        databaseHelper.getDataStore().update(
                databaseHelper.getDataStore().find(HrsTeam.class).field("team_name").equalIgnoreCase(hrsUser.getTeam()),
                databaseHelper.getDataStore().createUpdateOperations(HrsTeam.class).dec("team_members"));

        databaseHelper.getDataStore().delete(hrsUser);

        return responseBuilder.hrsUserToUserDto(hrsUser);
    }


    public UserDetailed getDetailedUserData(String username) {

        System.out.println("Retrieve detailed data for " + username);

        HrsUser hrsUser = databaseHelper.getDataStore().find(HrsUser.class).field("username").containsIgnoreCase(username).get();

        System.out.println("Found user " + hrsUser.getUsername() + " " + hrsUser.getId().toString());

        List<HrsRequests> userRequests = databaseHelper.getDataStore().find(HrsRequests.class).disableValidation().field("user.$id").equal(hrsUser.getId()).asList();

        System.out.println("Found requests " + userRequests.size());
        return responseBuilder.formUserDetailed(hrsUser, userRequests);


    }

    public List<UserDetailed> partialSearch(String criteria) {
        List<UserDetailed> returnList = new ArrayList<>();
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

            returnList = users.stream().map(u -> {
                return responseBuilder.formUserDetailed(u, new ArrayList<>());
            }).collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnList;
    }


    public User isEmailInUse (String criteria) {
        try {
            System.out.println("CRIT IS " + criteria);
            HrsUser hrsUser = databaseHelper.getDataStore().find(HrsUser.class).field("email").equalIgnoreCase(criteria).get();

            if (hrsUser == null) {
                return null;
            }

            return responseBuilder.hrsUserToUserDto(hrsUser);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public User isUsernameInUse (String criteria) {
        HrsUser hrsUser = databaseHelper.getDataStore().find(HrsUser.class).field("username").equalIgnoreCase(criteria).get();

        if (hrsUser == null) {
            return null;
        }

        return responseBuilder.hrsUserToUserDto(hrsUser);
    }

}
