package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestConnessione {
    public static void main(String[] args){
        try(Connection conn = ConnessioneDatabase.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM utente")){

            while(rs.next()){
                String username = rs.getString("username");
                System.out.println("Utente trovato: " + username);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
