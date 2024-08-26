import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class HotelReservation {
	private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
	private static final String username ="root";
	private static final String password = "root@123";

	public static void main(String[] args) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		}catch(ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
		
		try {
			Connection con = DriverManager.getConnection(url, username, password);
			while(true) {
				System.out.println("Hotel Management System");
				Scanner sc = new Scanner(System.in);
				System.out.println("1. Reserve a room");
				System.out.println("2. View Reservation");
				System.out.println("3. Get Room Number");
				System.out.println("4. Update Reservation");
				System.out.println("5. Delete Resevations");
				System.out.println("0. Exit");
				System.out.println("Choose an option:");
				int choice = sc.nextInt();
				switch(choice) {
				case 1:
					reserveRoom(con, sc);
					break;
					
				case 2:
					viewReservation(con);
					break;
					
				case 3:
					getRoomNumber(con,sc);
					break;
					
				case 4:
					updateReservation(con,sc);
					break;
					
				case 5:
					deleteReservation(con,sc);
					break;
					
				case 0:
					exit();
					sc.close();
					return;
					
				default:
					System.out.println("Invalid choice. Try again.");
				}
			}
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}catch(InterruptedException e) {
			throw new RuntimeException(e);
		}

	}

	public static void exit() throws InterruptedException {
		System.out.println("Existing System");
		int i = 5;
		while(i!=0) {
			System.out.println(".");
			Thread.sleep(1000);
			i--;
		}
		System.out.println("Thank You for using Hotel Reservation System !!!");
		
	}

	private static void deleteReservation(Connection con, Scanner sc) {
		 try {
			 System.out.println("Enter reservation ID to delete: ");
			 int reservationId = sc.nextInt();
			 
			 
			 if(!reservationExists(con,reservationId)) {
				 System.out.println("Reservation not found for the given ID.");
				 return;
			 }
			 
			 String sql = "DELETE FROM reservations WHERE reservation_id = "+reservationId;
			 
			 try(Statement stmt = con.createStatement()){
				 int affectedRows = stmt.executeUpdate(sql);
				 
				 if(affectedRows > 0) {
					 System.out.println("Reservation deleted successfully..!");
				 }else {
					 System.out.println("Reservation deletion failed.");
				 }
			 }
		 }catch(SQLException e) {
			 e.printStackTrace();
		 }
		
	}

	private static void updateReservation(Connection con, Scanner sc) {
		try {
			System.out.println("Enter reservation ID to update");
			int reservationId = sc.nextInt();
			sc.nextLine(); //Consume the newline character
			
			if(!reservationExists(con,reservationId)) {
				System.out.println("Reservation not found for the given ID");
				return;
			}
			
			System.out.println("Enter new guest name: ");
			String newGuestName = sc.nextLine();
			System.out.println("Enter new room number: ");
			int newRoomNumber = sc.nextInt();
			System.out.println("Enter new contact number: ");
			String newContactNumber = sc.next();
			
			String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "', " +
                    "room_number = " + newRoomNumber + ", " +
                    "contact_number = '" + newContactNumber + "' " +
                    "WHERE reservation_id = " + reservationId;
			
			try(Statement stmt = con.createStatement()){
				int affectRows = stmt.executeUpdate(sql);
				
				if(affectRows > 0) {
					System.out.println("Reservation Updated Successfully");
				}else {
					System.out.println("Reservation update failed");
				}
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
	}

	 private static boolean reservationExists(Connection connection, int reservationId) {
	        try {
	            String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = " + reservationId;

	            try (Statement statement = connection.createStatement();
	                 ResultSet resultSet = statement.executeQuery(sql)) {

	                return resultSet.next(); // If there's a result, the reservation exists
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	            return false; // Handle database errors as needed
	        }
	    }

	private static void getRoomNumber(Connection con, Scanner sc) {
		try {
			System.out.println("Enter Reservation Id: ");
			int reservationId = sc.nextInt();
			System.out.println("Enter Guest Name: ");
			String guestName = sc.next();
			
            String sql = "SELECT room_number FROM reservations " +
                    "WHERE reservation_id = " + reservationId +
                    " AND guest_name = '" + guestName + "'";

			
			try(Statement stmt = con.createStatement();
				ResultSet resultSet = stmt.executeQuery(sql)){
				
				if(resultSet.next()) {
					int roomNumber = resultSet.getInt("room_number");
					System.out.println("Room number for Reservation ID "+reservationId+
							"and Guest "+ guestName + "is: "+roomNumber);
				}else {
					System.out.println("Reservation not found for the given ID and Guest Name.");
				}
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
	}

	private static void viewReservation(Connection con) throws SQLException {
		String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservations";
		
		try(Statement stmt = con.createStatement();
			 ResultSet resultSet = stmt.executeQuery(sql)){
			
	            System.out.println("Current Reservations:");
	            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
	            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
	            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
	            
	            while(resultSet.next()){
	            	int reservationId = resultSet.getInt("reservation_id");
	            	String guestName = resultSet.getNString("guest_name");
	            	int roomNumber = resultSet.getInt("room_number");
	            	String contactNumber = resultSet.getNString("contact_number");
	            	String reservationDate = resultSet.getTimestamp("reservation_date").toString();
	            	
	            	// Format and display the reservation data in a table-like format
	                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
	                        reservationId, guestName, roomNumber, contactNumber, reservationDate);
	            }            			
		}
		
	}

	private static void reserveRoom(Connection con, Scanner sc) {
		try {
			System.out.println("Enter Guest Name: ");
			String guestName = sc.next();
			sc.nextLine();
			System.out.println("Enter Room Number: ");
			int roomNumber = sc.nextInt();
			System.out.println("Enter Contact Number: ");
			String contactNumber = sc.next();
			
			// Query
			String sql ="INSERT INTO reservations(guest_name, room_number, contact_number)"+
						"VALUES ('"+ guestName+"',"+ roomNumber +",'"+ contactNumber +"')";
			
			try (Statement stmt = con.createStatement()){
				int affectedRows = stmt.executeUpdate(sql);
				
				if(affectedRows > 0) {
					System.out.println("Reservation Succcessfull..!");
				}else {
					System.out.println("Reservation Failed.");
				}
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
	}

}
