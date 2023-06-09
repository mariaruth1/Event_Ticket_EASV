package ticketSystemEASV.dal;

import ticketSystemEASV.be.Event;
import ticketSystemEASV.be.Role;
import ticketSystemEASV.be.User;
import ticketSystemEASV.dal.Interfaces.DAO;
import ticketSystemEASV.dal.Interfaces.IUserDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UserDAO extends DAO<User> implements IUserDAO {
    private DBConnection dbConnection;
    private EventDAO eventDAO;
    private UserBuilder userBuilder;
    private RoleDAO roleDAO;
    private Map<String, Role> roles;

    public UserDAO() {
        dbConnection = DBConnection.getInstance();
        roleDAO = new RoleDAO();
        roles = roleDAO.getAllRoles();
        userBuilder = new UserBuilder();
        eventDAO = new EventDAO();
    }

    public User getUser(UUID userID) {
        String sql = "SELECT * FROM [User] " +
                "LEFT JOIN UserRole ON UserRole.UserID = [User].Id " +
                "LEFT JOIN Role ON Role.Id = UserRole.RoleId " +
                "WHERE [User].Id=?";

        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, userID.toString());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                User user = constructEventCoordinator(resultSet);
                if (Objects.equals(resultSet.getString("RoleName"), "Admin")) {
                    user.setRole(roles.get("Admin"));
                    break;
                }
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection(connection);
        }
        return null;
    }

    public boolean isInRole(UUID userID, String role) {
        String sql = "SELECT * FROM UserRole " +
                "JOIN Role ON UserRole.RoleId = Role.Id\n" +
                "WHERE UserId=? AND Role.RoleName=?";

        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, userID.toString());
            statement.setString(2, role);
            statement.execute();
            return statement.getResultSet().next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection(connection);
        }
        return false;
    }

    public boolean logIn(String name, String password) {
        String sql = "SELECT * FROM [User] WHERE UserName=? AND Password=?";
        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, password);
            statement.execute();
            return statement.getResultSet().next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection(connection);
        }
        return false;
    }

    public String signUp(User user) {
        String message = "";
        String sql = "DECLARE @UserID uniqueidentifier;" +
                "INSERT INTO [User] (name, userName, Password, profilePicture)" +
                "VALUES (?, ?, ?, ?)" +
                "SET @UserID = (SELECT ID FROM [User] WHERE UserName = ?)" +
                "INSERT INTO UserRole (UserID, RoleID)" +
                "VALUES (@UserID,(SELECT Id FROM Role WHERE RoleName LIKE 'EventCoordinator'));";

        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, user.getName());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPassword());
            statement.setBytes(4, user.getProfilePicture());
            statement.setString(5, user.getUsername());
            statement.execute();
        } catch (SQLException e) {
            message = e.getMessage();
        } finally {
            releaseConnection(connection);
        }
        return message;
    }

    public String updateUser(User user) {
        String message = "";
        String sql = "UPDATE [User] SET name=?, userName=?, password=?, profilePicture=? WHERE id=?;";
        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            fillPreparedStatement(statement, user);
            statement.setString(5, user.getId().toString());
            statement.execute();
        } catch (SQLException e) {
            message = e.getMessage();
        } finally {
            releaseConnection(connection);
        }
        return message;
    }

    public String deleteUser(User user) {
        String sql = "UPDATE [User] SET deleted=1 WHERE id=?;";
        String message = "";
        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, user.getId().toString());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            message = e.getMessage();
        } finally {
            releaseConnection(connection);
        }
        return message;
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM [User] " +
                "RIGHT JOIN UserRole ON [User].Id = UserRole.UserId " +
                "LEFT JOIN Role R2 on R2.Id = UserRole.RoleId " +
                "WHERE UserName=?;";
        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            statement.execute();
            if (statement.getResultSet().next()) {
                return constructEventCoordinator(statement.getResultSet());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection(connection);
        }
        return null;
    }

    // Event coordinators
    public HashMap<UUID, User> getAllEventCoordinators() {
        HashMap<UUID, User> eventCoordinators = new HashMap<>();
        String sql = "SELECT * FROM [User] " +
                "JOIN UserRole ON [User].ID = UserRole.UserID " +
                "JOIN [Role] ON UserRole.RoleID = Role.ID " +
                "WHERE [User].deleted=0;";

        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                if (eventCoordinators.containsKey(UUID.fromString(resultSet.getString("Id")))) {
                    if (eventCoordinators.get(UUID.fromString(resultSet.getString("Id"))).getRole() == roles.get("EventCoordinator")
                            && Objects.equals(resultSet.getString("RoleName"), "Admin")) {
                        eventCoordinators.get(UUID.fromString(resultSet.getString("Id"))).setRole(roles.get("Admin"));
                    }
                    if (eventCoordinators.get(UUID.fromString(resultSet.getString("Id"))).getRole() == roles.get("Admin")
                            && Objects.equals(resultSet.getString("RoleName"), "EventCoordinator")) {
                        eventCoordinators.get(UUID.fromString(resultSet.getString("Id"))).setRole(roles.get("Admin"));
                    }
                    continue;
                }

                User u = constructEventCoordinator(resultSet);
                eventCoordinators.put(u.getId(), u);
            }
            return eventCoordinators;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            releaseConnection(connection);
        }
    }

    public void getEventsAssignedToEventCoordinator(User eventCoordinator){
        if (eventCoordinator.getRole() == roles.get("Admin")) {
            eventCoordinator.setAssignedEvents((HashMap<Integer, Event>) eventDAO.getAllEvents());
            return;
        }

        String sql = "SELECT eventID FROM User_Event_Link WHERE userID=?;";
        Connection connection = null;
        try {
            connection = dbConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, eventCoordinator.getId().toString());
            ResultSet resultSet = statement.executeQuery();
            List<Integer> eventIds = new ArrayList<>();
            while (resultSet.next()) {
                int eventID = resultSet.getInt("eventID");
                eventIds.add(eventID);
            }
            if (!eventIds.isEmpty()) {
                // Batch get events by IDs
                HashMap<Integer, Event> events = eventDAO.getEventsByIds(eventIds);
                eventCoordinator.setAssignedEvents(events);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection(connection);
        }
    }

    // Helper methods
    private void fillPreparedStatement(PreparedStatement statement, User user) throws SQLException {
        statement.setString(1, user.getName());
        statement.setString(2, user.getUsername());
        statement.setString(3, user.getPassword());
        statement.setBytes(4, user.getProfilePicture());
    }

    private User constructEventCoordinator(ResultSet resultSet) throws SQLException {
        User user = userBuilder.setId(UUID.fromString(resultSet.getString("Id")))
                .setName(resultSet.getString("Name"))
                .setUsername( resultSet.getString("UserName"))
                .setPassword(resultSet.getString("Password"))
                .setProfilePicture(resultSet.getBytes("profilePicture"))
                .setRole(roles.get(resultSet.getString("RoleName")))
                .build();
        getEventsAssignedToEventCoordinator(user);
        return user;
    }

    public Map<String , Role> getAllRoles() {
        return roles;
    }
}
