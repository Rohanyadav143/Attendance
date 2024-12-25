import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class AttendanceSystem {
    private static String url = "jdbc:mysql://localhost:3306/project";
    private static String id = "root";
    private static String pass = "rr55555";

    public static void main(String[] args) {
        loading();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection Con = DriverManager.getConnection(url, id, pass);
            Scanner Sc = new Scanner(System.in);
            while (true) {
                System.out.println(" 1. Mark Attendance \n 2. Register Student \n 3. View Total \n " +
                        "4. View Section-Wise \n 5. View Solo Student Information \n 6. Update Details \n " +
                        "7. Update Attendance \n 8. Delete Student Detail \n 0. Exit");
                System.out.print("\nChoose an Option : ");
                int option = -1;
                try {
                    option = Sc.nextInt();
                } catch (InputMismatchException e) {}
                Sc.nextLine();
                switch (option) {
                    case 1:
                        markAttendance(Con, Sc);
                        break;
                    case 2:
                        registerStudent(Con, Sc);
                        break;
                    case 3:
                        viewTotal(Con, Sc);
                        break;
                    case 4:
                        viewSectionWise(Con, Sc);
                        break;
                    case 5:
                        viewSoloStudent(Con, Sc);
                        break;
                    case 6:
                        updateDetails(Con , Sc);
                        break;
                    case 7:
                        updateAttendance(Con , Sc);
                        break;
                    case 8:
                        deleteStudentDetails(Con , Sc);
                        break;
                    case 0:
                        exit();
                        Sc.close();
                        break;
                    default:
                        System.out.println("\n--------------Invalid ! Enter 0 - 8----------------\n");
                }
            }
        } catch (ClassNotFoundException e1) {
            System.out.println("Error : " + e1.getMessage());
        } catch (SQLException e2) {
            System.out.println("Error : " + e2.getMessage());
        } catch (InterruptedException e3) {
            System.out.println("Error : " + e3.getMessage());
        }
    }

    private static void loading() {
        System.out.println("\n\n----------WELCOME TO ATTENDANCE MANAGEMENT SYSTEM------------\n");
        System.out.print("Loading System");
        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(100);
                System.out.print(".");
            } catch (InterruptedException e) {
                System.out.println("Error : " + e.getMessage());
            }
        }
        System.out.println("\n");
    }

    private static String RollNum(Scanner Sc) {
        String Rnum;
        while (true) {
            System.out.print("\nEnter Roll Number : ");
            Rnum = Sc.nextLine();
            if (Rnum.matches("\\d{13}$")) {
                return Rnum;
            } else {
                System.out.println("\n-----------Invalid ! Enter Correct Roll Number-------------");
            }
        }
    }

    private static String StdName(Scanner Sc) {
        String Std;
        System.out.print("Enter Student Name : ");
        Std = Sc.nextLine().toUpperCase();
        return Std;
    }

    private static String ValidContact(Scanner Sc) {
        String contact;
        while (true) {
            System.out.print("Enter Contact Number : ");
            contact = Sc.nextLine();
            if (contact.matches("^[5-9]\\d{9}$")) {
                return contact;
            } else {
                System.out.println("\n-----------Invalid ! Number Enter Correct Number-------------\n");
            }
        }
    }

    private static String Branch(Scanner Sc) {
        String branch;
        System.out.print("Enter Branch Name : ");
        branch = Sc.nextLine().toUpperCase();
        return branch;
    }

    private static String Section(Scanner Sc) {
        String section;
        System.out.print("Enter Section Name : ");
        section = Sc.nextLine().toUpperCase();
        return section;
    }

    private static void printDetails(PreparedStatement ps , int totalDay) {
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                System.out.println("\n   +-------------------+-------------------+--------------------+--------------+------------+-------------+---------------+");
                System.out.println("   |  Std.Roll_Number  |   Student_Name    |   Contact Number   |    Branch    |   Section  |   Present   |   Attendance  |");
                System.out.println("   +-------------------+-------------------+--------------------+--------------+------------+-------------+---------------+");
                do {
                    String roll = rs.getString("Roll_No");
                    String name = rs.getString("Std_Name");
                    String contact = rs.getString("Pho_Num");
                    String branch = rs.getString("Branch");
                    String section = rs.getString("Section");
                    int pre = rs.getInt("Present_Day");
                    float percent = ((float)pre/totalDay * 100);
                    System.out.printf("   |  %-11s    |  %-15s  |    %-11s     |     %-7s  |     %-6s |     %-6d  |     %-6.2f    |\n", roll, name, contact, branch, section, pre, percent);
                } while (rs.next());
                System.out.println("   +-------------------+-------------------+--------------------+--------------+------------+-------------+---------------+\n");
                System.out.println("                                         TOTAL NUMBERS OF DAYS CLASS : "+totalDay + "\n");
            } else {
                System.out.println("\n-----------------Oops ! No Data Found----------------------\n");
            }
        } catch (SQLException e) {
            System.out.println("Error : " + e.getMessage());
        }
    }

    private static void printforMark(Connection Con, PreparedStatement ps , Scanner Sc) {
        try (ResultSet rs = ps.executeQuery()) {
            int i = 1;
            if (rs.next()) {
                do {
                    String roll = rs.getString("Roll_No");
                    String name = rs.getString("Std_Name");
                    System.out.print(i + ". "+roll+"  "+name+" : ");
                    while (true) {
                        String present = Sc.nextLine().toUpperCase();
                        if (present.equals("P")) {
                            printAnsMark(Con,roll);
                            break;
                        } else if (present.equals("A")) {
                            break;
                        } else {
                            System.out.print("Oops ! Enter 'A' or 'P' : ");
                        }
                    }
                    i++;
                } while (rs.next());
                System.out.println("\n---------------All Attendance Marked---------------\n");
            }
            else {
                System.out.println("\n-----------------Oops ! No Data Found----------------------\n");
            }
        } catch (SQLException e) {
            System.out.println("Error : " + e.getMessage());
        }
    }

    private static void printAnsMark(Connection Con,String roll_n){
        String Query = "UPDATE Attendance SET Present_Day = Present_Day + 1 WHERE Roll_No = ?";
        try (PreparedStatement ps = Con.prepareStatement(Query)){
            ps.setString(1,roll_n);
            ps.executeUpdate();
        }
        catch (SQLException e){
            System.out.println("Error : "+e.getMessage());
        }
    }

    private static void markAttendance(Connection Con, Scanner Sc) {
       System.out.print("\nEnter Section : ");
        String Sec = Sc.nextLine().toUpperCase();
        String query = "SELECT Roll_No , Std_Name FROM Attendance WHERE Section = ? ORDER BY Roll_No";
        System.out.println("\n-------------------Enter 'A' for Absent AND 'P' fro present------------------------\n");
        try(PreparedStatement pr = Con.prepareStatement(query)){
            pr.setString(1,Sec);
            printforMark(Con,pr,Sc);
        }
        catch (SQLException e){
            System.out.println("Error : "+e.getMessage());
        }
    }

    private static void registerStudent(Connection Con, Scanner Sc) {
        while (true) {
            try {
                String roll_num = RollNum(Sc);
                String name = StdName(Sc);
                String contact = ValidContact(Sc);
                String branch = Branch(Sc);
                String Sect = Section(Sc);
                String query = "INSERT INTO attendance (Roll_No, Std_Name, Pho_Num, Branch, Section) VALUES (?, ?, ?, ? ,?)";
                try (PreparedStatement ps = Con.prepareStatement(query)) {
                    ps.setString(1, roll_num);
                    ps.setString(2, name);
                    ps.setString(3, contact);
                    ps.setString(4, branch);
                    ps.setString(5, Sect);
                    int affectedRow = ps.executeUpdate();
                    if (affectedRow > 0) {
                        System.out.println("\n----------Registration Successfully Done ! -----------\n");
                        return;
                    } else {
                        System.out.println("\n------------Oops! Something Wrong , Registration Failed--------------\n");
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error : " + e.getMessage());
            }
        }
    }

    private static void viewTotal(Connection Con, Scanner Sc) {
        int totalDay = TotalDay(Sc);
        String query = "SELECT * FROM Attendance ORDER BY Section,Roll_No";
        try(PreparedStatement pr = Con.prepareStatement(query)){
            printDetails(pr , totalDay);
        }
        catch (SQLException e){
            System.out.println("Error : "+e.getMessage());
        }
    }

    private static void viewSectionWise(Connection Con, Scanner Sc) {
        System.out.print("Enter Section : ");
        String Sec = Sc.nextLine().toUpperCase();
        int totalDay = TotalDay(Sc);
        String query = "SELECT * FROM Attendance WHERE Section = ? ORDER BY Roll_No";
        try(PreparedStatement pr = Con.prepareStatement(query)){
            pr.setString(1,Sec);
            printDetails(pr , totalDay);
        }
        catch (SQLException e){
            System.out.println("Error : "+e.getMessage());
        }
    }

    private static void viewSoloStudent(Connection Con, Scanner Sc) {
        String r_num = RollNum(Sc);
        int totalday = TotalDay(Sc);
        String query = "SELECT * FROM attendance WHERE Roll_No = ?";
        try (PreparedStatement ps = Con.prepareStatement(query)) {
            ps.setString(1, r_num);
            printDetails(ps , totalday);
        } catch (SQLException e) {
            System.out.println("Error : " + e.getMessage());
        }
    }

    private static int TotalDay(Scanner Sc){
        while (true){
            try{
                System.out.print("Enter total number of days : ");
                int num = Sc.nextInt();
                Sc.nextLine();
                if(num > 0){
                    return num;
                }
                else {
                    System.out.println("\n-----------------Total Days Must be equal or greater------------------\n");
                }
            }
            catch (InputMismatchException e){
                System.out.println("Error "+ e.getMessage());
                Sc.nextLine();
            }
        }
    }

    private static void updateDetails(Connection Con, Scanner Sc) {
        try {
            String roll_num = RollNum(Sc);
            String name = StdName(Sc);
            String contact = ValidContact(Sc);
            String branch = Branch(Sc);
            String Sect = Section(Sc);
            String query1 = "UPDATE attendance SET Std_Name = ?, Pho_Num = ?, Branch = ?, Section = ? WHERE Roll_No = ?";
            try (PreparedStatement ps = Con.prepareStatement(query1)) {
                ps.setString(1, name);
                ps.setString(2, contact);
                ps.setString(3, branch);
                ps.setString(4, Sect);
                ps.setString(5, roll_num);
                int affectedRow = ps.executeUpdate();
                if (affectedRow > 0) {
                    System.out.println("\n----------Updation Successfully Done ! -----------\n");
                } else {
                    System.out.println("\n------------Oops! Data Not Updated , Check Roll Number--------------\n");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error : " + e.getMessage());
        }
    }

    private static int value(Scanner Sc){
        while (true){
            try{
                System.out.print("Number of Present to Update : ");
                int num = Sc.nextInt();
                Sc.nextLine();
                if(num < 10){
                    return num;
                }
                else {
                    System.out.println("Updation Must be less than 10");
                }
            }
            catch (InputMismatchException e){
                System.out.println("Error "+ e.getMessage());
            }
        }
    }

    private static void updateAttendance(Connection Con, Scanner Sc) {
        String roll_num = RollNum(Sc);
        int days = value(Sc);
        String query = "UPDATE Attendance SET present_Day = present_Day + ? WHERE Roll_No = ?";
        try(PreparedStatement ps = Con.prepareStatement(query)){
            ps.setInt(1,days);
            ps.setString(2,roll_num);
            int Affectedrow = ps.executeUpdate();
            if(Affectedrow > 0){
                System.out.println("\n----------------Attendance Update Successfully-----------------\n");
            }
            else {
                System.out.println("\n------------Oops! Data Not Updated , Check Roll Number--------------\n");
            }
        }
        catch (SQLException e){
            System.out.println("Error : "+e.getMessage());
        }
    }

    private static void deleteStudentDetails(Connection Con, Scanner Sc) {
        String roll_num = RollNum(Sc);
        String query = "DELETE FROM Attendance WHERE Roll_No = ? ";
        try(PreparedStatement pr = Con.prepareStatement(query)){
            pr.setString(1,roll_num);
            int rowAffected = pr.executeUpdate();
            if(rowAffected > 0){
                System.out.println("\n------------Data Deleted Successfully !-------------\n");
            }
            else {
                System.out.println("\n----------------No Match Found---------------\n");
            }
        }
        catch (SQLException e){
            System.out.println("Error : "+e.getMessage());
        }
    }

    private static void exit() throws InterruptedException {
        System.out.print("\nExiting System");
        for (int i = 0; i < 5; i++) {
            System.out.print(".");
            Thread.sleep(100);
        }
        System.out.println("\n\n----------------------------THANKS FOR USING---------------------------------");
        System.exit(0);
    }
}