package db;

import java.sql.*;

public class Factory {
    // DB Config options
    private static final String USERNAME = "amcguiness";
    private static final String PASSWORD = "orange";

    /**
     * Read an employee from the database, parse the data from it and insert into an Employee object.
     *
     * @param empID EMPID of the row to load.
     * @return The Employee containing the row information or null if not found.
     */
    public static Employee getEmployee(String empID) {
        Employee employee = null;

        try {
            Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@Picard2:1521:itec2", USERNAME, PASSWORD);

            CallableStatement statement = conn.prepareCall("SELECT * FROM EMPS WHERE empid=?");
            statement.setString(1, empID);

            ResultSet rset = statement.executeQuery();

            if (rset.next()) {
                employee = new Employee(
                        rset.getString("EMPID"),
                        rset.getString("LNAME"),
                        rset.getString("FNAME"),
                        rset.getInt("SALARY"),
                        rset.getString("STREET"),
                        rset.getString("CITY"),
                        rset.getString("STATE"),
                        rset.getString("ZIP"),
                        rset.getInt("REVNUM")
                );
            }

            conn.close();
        } catch (SQLException e) {
            System.err.println("Could not load the db" + e);
        } catch (LengthException le) {
            System.err.println("Issue with field lengths." + le);
        }

        return employee;
    }

    /**
     * Update an employee's information in the database.
     *
     * @param employee Employee to update in the database.
     * @return String containing the results of the transaction.  "SUCCESS" of there were no errors.
     */
    public static String saveEmployee(Employee employee) {
        Connection conn = null;
        String result;

        try {
            conn = DriverManager.getConnection("jdbc:oracle:thin:@Picard2:1521:itec2", USERNAME, PASSWORD);
            conn.setAutoCommit(false);

            int revnum = getRevNumWithLock(conn, employee.getEmpID());
            if (revnum == employee.getRevnum()) {
                CallableStatement statement = conn.prepareCall(
                        "UPDATE EMPS SET LNAME = ?, FNAME = ?, SALARY = ?, STREET = ?, CITY = ?, STATE = ?, ZIP = ? " +
                                "WHERE empid = ?"
                );
                createUpdateCommand(employee, statement);
                statement.executeUpdate();

                conn.commit();
                result = "SUCCESS";
            } else {
                result = "REVNUM_ERROR";
                conn.rollback();
            }

            conn.close();
        } catch (SQLException e) {
            result = e.getMessage();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * Helper to insert an Employee's data into a CallableStatement.
     *
     * @param employee  source of the data.
     * @param statement destination for the data.
     * @throws SQLException CallableStatement may throw an exception when setting variables.
     */
    private static void createUpdateCommand(Employee employee, CallableStatement statement) throws SQLException {
        statement.setString(1, employee.getlName());
        statement.setString(2, employee.getfName());
        statement.setInt(3, employee.getSalary());
        statement.setString(4, employee.getStreet());
        statement.setString(5, employee.getCity());
        statement.setString(6, employee.getState());
        statement.setString(7, employee.getZip());
        statement.setString(8, employee.getEmpID());
    }

    /**
     * Create a lock on a row and get obtain the revnum of the row.
     *
     * @param conn  DB Connection to use.
     * @param empID EMPID of the row.
     * @return The current revnum of the row or -1 if no row was found.
     * @throws SQLException CallableStatement and ResultSet may throw exceptions.
     */
    private static int getRevNumWithLock(Connection conn, String empID) throws SQLException {
        int revnum = -1;

        CallableStatement statement = conn.prepareCall("SELECT revnum FROM EMPS WHERE empid=? FOR UPDATE WAIT 5");
        statement.setString(1, empID);

        ResultSet rset = statement.executeQuery();


        if (rset.next()) {
            revnum = rset.getInt("revnum");
        }

        return revnum;
    }
}
