import com.google.gson.Gson;
import controllers.AuthController;
import controllers.RequestController;
import controllers.TeamController;
import controllers.UserController;
import database.DatabaseHelper;
import domain.responses.ErrorResponse;
import exceptions.ConflictException;
import exceptions.NotAuthenticatedException;
import exceptions.NotAuthorizedException;
import utils.Constants;

import java.time.LocalDate;
import java.util.Date;

import static spark.Spark.*;

/**
 * Created by sasaradovanovic on 12/28/17.
 */
public class Application {

    private AuthController authController;
    private TeamController teamController;
    private UserController userController;
    private RequestController requestController;

    private DatabaseHelper databaseHelper;

    public static void main(String[] args) {
        new Application().init();
    }

    public void init() {
        port(8087);

        staticFiles.location("/dist");

        System.out.println("Starting application...");

        initControllers();
        databaseHelper = new DatabaseHelper();


        enableCORS("*", "GET,POST,PUT,DELETE,OPTIONS", "Access-Control-Allow-Origin,Access-Control-Allow-Headers");

        path("/api", () -> {

            path("/auth", () -> {

                post("/login", (req, res) -> {
                    System.out.println("[PATH] Hit /auth/login");
                    return authController.handleLoginRequest(req, res);
                });
                get("/activation-hash", (req, res) -> {
                    System.out.println("[PATH] Hit /activation-hash");
                    return authController.validateActivationHash(req, res);
                });
                post("/set-password", (req, res) -> {
                    System.out.println("[PATH] Hit /set-password");
                    return authController.setPassword(req, res);
                });
                path("/user", () -> {
                    before("/*", (req, res) -> {
                        System.out.println("[Filter /auth/user/* | Before] Called");
                        if (!authController.ensureUserIsLoggedIn(req, res)) {
                            //halt(401);
                        }
                    });
                    get("/data", (req, res) -> {
                        System.out.println("[PATH] Hit /auth/user/data");
                        return authController.handleUserData(req, res);
                    });
                    get("/logout", (req, res) -> {
                        System.out.println("[PATH] Hit /auth/login");
                        return authController.handleLogoutRequest(req, res);
                    });
                });
            });

            path("/teams", () -> {
                before("/*", (req, res) -> {
                    System.out.println("[Filter /teams/* | Before] Called");
                    if (!authController.ensureUserIsLoggedIn(req, res)) {
                        //halt(401);
                    }
                });
                path("/admin", () -> {
                    before("/*", (req, res) -> {
                        System.out.println("[Filter /teams/admin/* | Before] Called");
                        if (!authController.ensureUserIsSuperAdmin(req, res)) {
                            //halt(403);
                        }
                    });
                    get("/all/:offset", (req, res) -> {
                        System.out.println("[PATH] Hit /teams/admin/all");
                        return teamController.getAllTeams(req, res);
                    });
                    post("/create", (req, res) -> {
                        System.out.println("[PATH] Hit /teams/admin/create");
                        return teamController.createNewTeam(req, res);
                    });
                    post("/add-user", (req, res) -> {
                        System.out.println("[PATH] Hit /teams/admin/add-user");
                        return teamController.addUserToTeam(req, res);
                    });
                    post("/add-team-admin", (req, res) -> {
                        System.out.println("[PATH] Hit /teams/admin/add-team-admin");
                        return teamController.addTeamAdminToTeam(req, res);
                    });
                    post("/remove-user", (req, res) -> {
                        System.out.println("[PATH] Hit /teams/admin/remove-user");
                        return teamController.removeUserFromTeam(req, res);
                    });
                    post("/remove-team-admin", (req, res) -> {
                        System.out.println("[PATH] Hit /teams/admin/remove-team-admin");
                        return teamController.removeTeamAdminFromTeam(req, res);
                    });
                    delete("/delete/:name", (req, res) -> {
                        return teamController.deleteTeam(req, res);
                    });
                    get("/check-name/:name", (req, res) -> {
                        return teamController.checkName(req, res);
                    });
                });

                path("/details", () -> {
                    before("/*", (req, res) -> {
                        System.out.println("[Filter /teams/details/* | Before] Called");
                        if (!authController.ensureUserHasAccessToTeamDetails(req, res, req.params("name"))) {
                            //halt(403);
                        }
                    });
                    get("/:name", (req, res) -> {
                        System.out.println("[PATH] Hit /teams/details/:name");
                        return teamController.getTeamData(req.params("name"));
                    });
                    get("/new-team-member/:team", (req, res) -> {
                        System.out.println("[PATH] Hit /teams/details/new-team-member/:team");
                        return teamController.getPartialSearchForNewUser(req.params("team"), req.queryParams("criteria"));
                    });
                });

            });

            path("/user", () -> {
                before("/*", (req, res) -> {
                    System.out.println("[Filter /user/* | Before] Called");
                    if (!authController.ensureUserIsSuperAdmin(req, res)) {
                        //halt(403);
                    }
                });
                post("/create-edit", (req, res) -> {
                    return userController.addOrEditUser(req, res);
                });
                delete("/remove/:username", (req, res) -> {
                    return userController.removeUser(req, res);
                });
                get("/data/:username", (req, res) -> {
                    System.out.println("[PATH] Hit /user/data/:username");
                    return userController.getUserDetailedData(req, res);
                });
                get("/partial-search", (req, res) -> {
                    System.out.println("[PATH] Hit /user/partial-search");
                    return userController.partialSearchOfUsers(req.queryParams("criteria"));
                });
                get("/check-email/:criteria", (req, res) -> {
                    System.out.println("[PATH] Hit /user/check-email");
                    return userController.checkIfEmailIsUsed(req.params("criteria"));
                });
                get("/check-username/:criteria", (req, res) -> {
                    System.out.println("[PATH] Hit /user/check-username");
                    return userController.checkIfUsernameIsUsed(req.params("criteria"));
                });
            });
            path("/request", () -> {
                path("/admin", () -> {
                    before((req, res) -> {
                        System.out.println("[Filter /request/admin/* | Before] Called");
                        if (!authController.ensureUserIsSuperAdmin(req, res)) {
                            //halt(403);
                        }
                    });
                    get("/req/pending", (req, res) -> {
                        return requestController.pendingRequests();
                    });
                    get("/req/approved", (req, res) -> {
                        return requestController.approveRequests();
                    });
                    post("/req/:id", (req, res) -> {
                        return requestController.approveRequest(true, req.params("id"));
                    });
                    delete("/req/:id", (req, res) -> {
                        return requestController.deleteRequest(null, req.params("id"));
                    });
                });
                get("/req/my-requests", (req, res) -> {
                    String username = "timko.adminko";
                    return requestController.getMyRequests(username);
                });
                get("/req/validate-dates", (req, res) -> {
                    LocalDate startDate = LocalDate.parse(req.queryParams("startDate"), Constants.TimeConstants.localDateFormatter);
                    LocalDate endDate = LocalDate.parse(req.queryParams("endDate"), Constants.TimeConstants.localDateFormatter);

                    String username;
                    if (req.session() != null && req.session().attribute("username") != null) {
                        username = req.session().attribute("username");
                    } else {
                        username = "timko.adminko";
                    }
                    return requestController.validateDates(username, startDate, endDate);
                });
                post("/req/new", (req, res) -> {
                    System.out.println("[PATH] Hit /request/req/new");
                    return requestController.newRequest(req, res);
                });
                delete("/req/:id", (req, res) -> {
                    String username;
                    if (req.session() != null && req.session().attribute("username") != null) {
                        username = req.session().attribute("username");
                    } else {
                        username = "timko.adminko";
                    }
                    return requestController.deleteRequest(username, req.params("id"));
                });

            });
            // After hook is being called after every /api call
            after("/*", (req, res) -> {
                res.type("application/json");
                System.out.println("[Filter | After] After hook " + req.pathInfo());
            });
        });


        // 404 response
        notFound("<html><body><h1>Custom 404 handling</h1></body></html>");

        // 500 response
        //internalServerError("<html><body><h1>Custom 500 handling</h1></body></html>");

        exception(NotAuthorizedException.class, (exception, request, response) -> {
            ErrorResponse error = new ErrorResponse();
            error.setMessage("You are not authorized to access this resource.");
            response.status(403);
            response.type("application/json");
            response.body(new Gson().toJson(error));
        });

        exception(NotAuthenticatedException.class, (exception, request, response) -> {
            ErrorResponse error = new ErrorResponse();
            error.setMessage("You are not authenticated. Please login first!");
            response.status(401);
            response.type("application/json");
            response.body(new Gson().toJson(error));
        });

        exception(ConflictException.class, (exception, request, response) -> {
            ErrorResponse error = new ErrorResponse();
            error.setMessage(exception.getConflictMessage());
            response.status(409);
            response.type("application/json");
            response.body(new Gson().toJson(error));
        });


    }


    private void initControllers() {
        authController = new AuthController();
        teamController = new TeamController();
        userController = new UserController();
        requestController = new RequestController();
    }


    // Enables CORS on requests. This method is an initialization method and should be called once.
    private static void enableCORS(final String origin, final String methods, final String headers) {

        options("/*", (request, response) -> {

            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
        });
    }
}
