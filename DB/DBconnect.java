package DB;
import java.sql.*;


public class DBconnect{
    
    public Statement getStmt(){
        
        Connection con = null;
        Statement st = null;
        
        try{
             Class.forName("com.mysql.jdbc.Driver");
             con = DriverManager.getConnection("jdbc:mysql://localhost:10061/mades", "root", "");
             st = con.createStatement();
             
         }catch(Exception e){
             System.out.println(e);
         }
        
        return st;
    }
//to use this make a dbconnect object, get a stmt and pass a query.    
    public ResultSet getRS(String query, Statement stmt ){
        ResultSet rs = null;
        
        try{
            rs = stmt.executeQuery(query);
            
        }catch(Exception e){
            System.out.println(e);
        }
        
        return rs;
    }
}
