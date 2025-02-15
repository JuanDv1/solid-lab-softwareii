package co.edu.unicauca.mycompany.projects.access;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import co.edu.unicauca.mycompany.projects.domain.entities.Company;
import co.edu.unicauca.mycompany.projects.domain.entities.Sector;
import co.edu.unicauca.mycompany.projects.domain.services.CompanyService;
import java.sql.Connection;
import java.util.List;

/**
 * Implementación del repositorio con Sqlite
 *
 * @author Libardo, Julio
 */
public class CompanySqliteRepository implements ICompanyRepository {

    private Connection conn;
    
//    public CompanySqliteRepository(){
//        initDatabase();
//    }
    public void connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:src/database/mibd.db";
        //String url = "jdbc:sqlite::memory:";

        try {
            conn = DriverManager.getConnection(url);
            
        } catch (SQLException ex) {
            Logger.getLogger(CompanyService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void disconnect() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }
    
    
    @Override
    public boolean save(Company newCompany) {
        try {
            //Validate product
            if (newCompany == null || newCompany.getNit().isBlank() || newCompany.getName().isBlank() || newCompany.getEmail().isBlank() || newCompany.getPassword().isBlank()) {
                return false;
            }
            this.connect();

            String sql = "INSERT INTO Companies ( nit, name, phone, pageweb, sector, email, password) "
                    + "VALUES ( ?, ?, ?, ?, ?, ?, ? )";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newCompany.getNit());
            pstmt.setString(2, newCompany.getName());
            pstmt.setString(3, newCompany.getPhone());
            pstmt.setString(4, newCompany.getPageWeb());
            pstmt.setObject(5, newCompany.getSector().toString());
            pstmt.setString(6, newCompany.getEmail());
            pstmt.setString(7, newCompany.getPassword());
            
            pstmt.executeUpdate();
            this.disconnect();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(CompanyService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
   public List<Company> listAll() {
        List<Company> companies = new ArrayList<>();
        try {

            String sql = "SELECT nit, name, phone, pageweb, sector, email, password FROM Companies";
            this.connect();

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Company newCompany = new Company();
                newCompany.setNit(rs.getString("nit"));
                newCompany.setName(rs.getString("name"));
                newCompany.setPhone(rs.getString("phone"));
                newCompany.setPageWeb(rs.getString("pageweb"));
                newCompany.setSector(Sector.valueOf(rs.getString("sector")));
                newCompany.setEmail(rs.getString("email"));
                newCompany.setPhone(rs.getString("phone"));

                companies.add(newCompany);
            }
            this.disconnect();

        } catch (SQLException ex) {
            Logger.getLogger(CompanyService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return companies;
    }

}
