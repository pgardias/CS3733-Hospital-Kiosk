/*

import edu.wpi.cs3733d18.teamp.Exceptions.AccessNotAllowedException;
import edu.wpi.cs3733d18.teamp.Exceptions.EmployeeNotFoundException;
import edu.wpi.cs3733d18.teamp.Exceptions.LoginInvalidException;
import edu.wpi.cs3733d18.teamp.Database.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmployeeRepoTesting {
        int employeeID1 = 90;
        int employeeID2 = 2345;
        private EmployeeRepo emRepo;
        private static DBHandler db;

        @BeforeClass
        public static void init(){
            db = new DBHandler();
            db.init();
        }

        @Before
        public void setup(){
            emRepo = new EmployeeRepo();
        }

        @Test(expected = EmployeeNotFoundException.class)
        public void testGetOneEmployee() throws Exception{
            emRepo.getOneEmployee(employeeID1);
        }

        @Test
        public void testGettingEmployee() throws Exception{
            assertEquals(emRepo.getAllEmployees().get(employeeID2).getUserID(),(emRepo.getOneEmployee(employeeID2)).getUserID());
        }

        @Test(expected = LoginInvalidException.class)
        public void testCheckEmployeeLogin() throws Exception{
            emRepo.checkEmployeeLogin(2345,"notthepassword");
            emRepo.checkEmployeeLogin(employeeID1, "password");
        }

        @Test(expected = LoginInvalidException.class)
        public void testCheckAdminLogin() throws Exception{
            emRepo.checkAdminLogin(1234, "password");
            emRepo.checkAdminLogin(2345, "databases");
        }

        @Test(expected = AccessNotAllowedException.class)
        public void testCheckAdminLoginAccess() throws Exception{
            emRepo.checkAdminLogin(employeeID2, "password");
        }

        @AfterClass
        public static void shutoff(){
            db.shutdown();
        }

}
*/
