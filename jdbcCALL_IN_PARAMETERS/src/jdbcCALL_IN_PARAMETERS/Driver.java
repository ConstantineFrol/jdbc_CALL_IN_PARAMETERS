/**
 * 
 */
package jdbcCALL_IN_PARAMETERS;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author 		Konstantin Frolov
 * Student No.	R00144177
 * email		konstantin.frolov@mycit.ie
 */
public class Driver {

	/**
	 * in MYSQL file there is already made up prepared Parameters (Functions)
	 * Using CallableStatement we can call them and and even pass some parameters
	 * There is IN , INOUT , OUT also they can return some result sets
	 * 
	 * 
	 * 
	 * Code for MYSQL WORKBENCH
	 * 
	 * DELIMITER $$
		DROP PROCEDURE IF EXISTS `increase_salaries_for_department`$$
		
		CREATE DEFINER=`root`@`localhost` PROCEDURE `increase_salaries_for_department`(IN the_department VARCHAR(64), IN increase_amount DECIMAL(10,2))
		BEGIN
		
			UPDATE EMPLOYEES SET salary= salary + increase_amount where department=the_department;
		
		END$$
		DELIMITER ;increase_salaries_for_department
	 */
	public static void main(String[] args) {

		String url = "jdbc:mysql://localhost:3306/demo1?autoReconnect=true&useSSL=false";
		String user = "root";
		String password = "root";

		Connection myConnection = null;
		CallableStatement myStatement = null;
		ResultSet myRs = null;
		
		try {
			// Get Connection to Database
			myConnection = DriverManager.getConnection(url, user, password);
			
			// Set variables for department name and amount to increase salary
			String theDepartment = "Engineering";
			int theIncreaseAmount = 10000;
			
			// Show salaries BEFORE
			System.out.println("Salaries BEFORE:\n");
			showSalaries(myConnection, myStatement, theDepartment, myRs);
			
			// Prepare the stored procedure call
			myStatement = myConnection.prepareCall("{call increase_salaries_for_department(?, ?)}");
			
			// Set the parameters
			myStatement.setString(1, theDepartment);
			myStatement.setDouble(2, theIncreaseAmount);
			
			// Call stored procedure
			System.out.println("\n\nCalling stored procedure.\nincrease_salaries_from_department(' " + theDepartment + " ', ' " + theIncreaseAmount + " ' )");
			myStatement.execute();
			System.out.println("Finished calling stored procedure\n");
			
			// Show salaries AFTER
			System.out.println("\n\nSalaries AFTER\n");
			showSalaries(myConnection,myStatement, theDepartment, myRs);
			
		}catch(SQLException e) {
			System.err.println("Oops! \n" + e.getMessage());
		}finally {
			closeConnections(myConnection, myStatement);
		}


	}

	/**
	 * @param myStmt 
	 * @param myConnection
	 * @param theDepartment
	 * @throws SQLException 
	 */
	private static void showSalaries(Connection myConn, Statement myStmt, String theDepartment, ResultSet myRs) throws SQLException {
		//TODO SELECT salary FROM employees WHERE department = 'Engineering'
		String query = "select * from employees where department = 'Engineering'";
		try {
			myStmt = myConn.createStatement();
			myRs = myStmt.executeQuery(query);
			while(myRs.next()) {
				int id = myRs.getInt("id");
				String department = myRs.getString("department");
				double salary = myRs.getDouble("salary");
				System.out.println(id + "\t" + department + "\t" + salary);
			}
		} catch (SQLException e) {
			System.out.println("Problem in myResultSet:\n");
			e.printStackTrace();
		}finally {
			if(myRs != null) {
				myRs.close();
			}
		}
	}

	/************
	 *	Methods	
	 * @param myRs *
	 ************/
	
	private static void closeConnections(Connection myConn, CallableStatement myStmt) {
		if(myConn != null && myStmt != null) {
			try {
				myConn.close();
				myStmt.close();
				System.out.println("\nAll Connections Terminated");
			} catch (SQLException e) {
				System.err.println("Oops! /n" + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	
}
