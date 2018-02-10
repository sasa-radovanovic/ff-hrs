package controllers;

import com.google.gson.Gson;
import database.DatabaseHelper;
import domain.ResponseBuilder;
import domain.requests.LoginRequest;
import domain.requests.SetPasswordRequest;
import domain.responses.LoginResponse;
import exceptions.NotAuthenticatedException;
import exceptions.NotAuthorizedException;
import models.HrsUser;
import service.UserService;
import spark.Request;
import spark.Response;
import utils.Constants;
import utils.PasswordUtil;

import java.time.LocalDateTime;

/**
 * Created by sasaradovanovic on 12/28/17.
 */
public class AuthController {

    private UserService userService;

    private DatabaseHelper databaseHelper;

    private ResponseBuilder responseBuilder;


    public AuthController() {

        userService = new UserService();

        databaseHelper = new DatabaseHelper();

        responseBuilder = new ResponseBuilder();
    }

    public boolean ensureUserIsLoggedIn(Request request, Response response) {
        System.out.println("[AuthController] Checking session...");
        if (request.session() == null || request.session().attribute("username") == null) {
            System.out.println("[AuthController] User does not hold valid session!");
            return false;
        }
        System.out.println("[AuthController] Session checked");
        return true;
    }


    public boolean ensureUserIsSuperAdmin(Request request, Response response) {
        System.out.println("[AuthController] Checking role " + Constants.Roles.ROLE_ADMIN.toString());
        if (request.session() == null || request.session().attribute("role") == null ||
                !((String)request.session().attribute("role")).equalsIgnoreCase(Constants.Roles.ROLE_ADMIN.toString())) {
            System.out.println("[AuthController] User does not hold valid privilege!");
            return false;
        }
        System.out.println("[AuthController] Role checked checked");
        return true;
    }


    public boolean ensureUserHasAccessToTeamDetails(Request request, Response response, String teamName) {
        System.out.println("[AuthController] Checking user has access to team " + teamName);
        if (request.session() == null || request.session().attribute("role") == null) {
            System.out.println("[AuthController] User does not hold valid privilege!");
            return false;
        }
        if (((String)request.session().attribute("role")).equalsIgnoreCase(Constants.Roles.ROLE_ADMIN.toString())) {
            System.out.println("[AuthController] Super admin has access to team details");
            return true;
        }
        if (((String)request.session().attribute("role")).equalsIgnoreCase(Constants.Roles.ROLE_TEAM_ADMIN.toString())) {
            HrsUser hrsUser = userService.getRawUserData(request.session().attribute("username"));
            if (hrsUser.getTeamsAdmin() != null && hrsUser.getTeamsAdmin().size() > 0 && hrsUser.getTeamsAdmin().contains(teamName)) {
                System.out.println("[AuthController] Team admin has access to team details");
                return true;
            }
            System.out.println("[AuthController] Team admin does not hold valid privilege!");
            return false;
        }
        System.out.println("[AuthController] User does not hold valid privilege!");
        return false;
    }



    public String handleLoginRequest(Request request, Response response) throws NotAuthenticatedException {
        LoginRequest loginRequest = new Gson().fromJson(request.body(), LoginRequest.class);
        System.out.println("Login Request: " + loginRequest.getUsername() + " >>> " + loginRequest.getPassword());
        LoginResponse loginResponse = new LoginResponse();
        if (userService.checkCredentials(loginRequest.getUsername(), loginRequest.getPassword())) {
            response.status(200);
            loginResponse.setAuthenticated(true);
            loginResponse.setUsername(loginRequest.getUsername());
            loginResponse.setAuthenticatedTime(LocalDateTime.now().toString());
            loginResponse.setSessionExpiration(300);
            request.session(true);
            request.session().attribute("role", userService.getUserData(loginRequest.getUsername()).getRole());
            request.session().attribute("username", loginRequest.getUsername());
            request.session().maxInactiveInterval(300);
        } else {
            throw new NotAuthenticatedException();
        }
        return new Gson().toJson(loginResponse);
    }


    public String validateActivationHash(Request request, Response response) throws NotAuthorizedException {
        String hash = request.queryParams("hash");
        String username = request.queryParams("username");

        System.out.println("Received hash " + hash);

        if (hash == null || username == null) {
            throw new NotAuthorizedException();
        }

        HrsUser hrsUser = databaseHelper.getDataStore().find(HrsUser.class).field("username").equalIgnoreCase(username).get();

        if (hrsUser == null || hrsUser.getActive() == null || hrsUser.getActive().booleanValue() == true) {
            throw new NotAuthorizedException();
        }

        System.out.println("AFTER SAVE ID " + hrsUser.getUsername() + " >>>> HASHED " + PasswordUtil.hashActivationCode(hrsUser.getUsername()));
        System.out.println("SENT HASH " + hash);

        if (!PasswordUtil.checkActivationCode(hash, hrsUser.getUsername())) {
            throw new NotAuthorizedException();
        }

        return new Gson().toJson(responseBuilder.hrsUserToUserDto(hrsUser));
    }


    public String handleLogoutRequest(Request request, Response response) {
        LoginResponse loginResponse = new LoginResponse();

        loginResponse.setAuthenticated(false);
        loginResponse.setUsername(request.session().attribute("username"));
        loginResponse.setAuthenticatedTime(LocalDateTime.now().toString());
        loginResponse.setSessionExpiration(0);
        request.session().invalidate();

        return new Gson().toJson(loginResponse);
    }



    public String handleUserData(Request request, Response response) {
        response.status(200);
        return new Gson().toJson(userService.getUserData(request.session().attribute("username")));
    }


    public String setPassword(Request req, Response res) throws NotAuthorizedException {

        SetPasswordRequest setPasswordRequest = new Gson().fromJson(req.body(), SetPasswordRequest.class);

        if (setPasswordRequest.getHash() == null || setPasswordRequest.getUsername() == null || setPasswordRequest.getPassword() == null) {
            throw new NotAuthorizedException();
        }

        HrsUser hrsUser = databaseHelper.getDataStore().find(HrsUser.class).field("username").equalIgnoreCase(setPasswordRequest.getUsername()).get();

        if (hrsUser == null) {
            throw new NotAuthorizedException();
        }


        if (!PasswordUtil.checkActivationCode(setPasswordRequest.getHash(), hrsUser.getUsername())) {
            throw new NotAuthorizedException();
        }

        if (setPasswordRequest.getPassword().length() != 8) {
            throw new NotAuthorizedException();
        }

        hrsUser.setPassword(PasswordUtil.hashPassword(setPasswordRequest.getPassword()));
        hrsUser.setActive(true);

        databaseHelper.getDataStore().save(hrsUser);

        return new Gson().toJson(responseBuilder.hrsUserToUserDto(hrsUser));
    }
}
