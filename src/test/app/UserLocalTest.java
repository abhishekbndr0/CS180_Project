package src.test.app;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import src.main.app.User;

import java.io.*;
import java.util.ArrayList;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.*;

/**
 * Team Project -- UserLocalTest
 *
 * This program defines the test cases for verifying the behavior
 * of the User class, such as verifying the constructors, data persistence,
 * etc.
 *
 * @author Haiyan Xuan, lab sec L18
 *
 * @version November 3, 2024
 *
 */

@RunWith(Enclosed.class)
public class UserLocalTest {
    //Main Method
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestCase.class);
        if (result.wasSuccessful()) {
            System.out.println("Excellent - Test ran successfully");
        } else {
            for (Failure failure : result.getFailures()) {
                System.out.println(failure.toString());
            }
        }
    }

    /**
     * Team Project -- Test Case
     * <p>
     * This class contains the test case methods that are checked by this User Local Test
     *
     * @author Haiyan Xuan, lab sec L18
     * @version November 3, 2024
     */

    public static class TestCase {
        //Define field/methods to extract output from System.out
        private final PrintStream originalOutput = System.out;

        @SuppressWarnings("FieldCanBeLocal")
        private ByteArrayOutputStream testOut;

        @Before
        public void outputStart() {
            testOut = new ByteArrayOutputStream();
            System.setOut(new PrintStream(testOut));
        }

        @After
        public void restoreInputAndOutput() {
            System.setOut(originalOutput);
        }

        private String getOutput() {
            return testOut.toString();
        }

        //Case A: Test if constructors work as intended
        @Test(timeout = 1000)
        public void verifyUserConstructorOutput() throws NoSuchAlgorithmException {
            User user = new User("John Doe", "thejohndoe", "password1234", "Hello! I'm John Doe.", "johndoe.png");
            assertEquals("John Doe", user.getName());
            assertEquals("thejohndoe", user.getUsername());
            assertEquals("uclQZA4bN0DpisuT5mnGV2b2Zw3RYJupH/QQUrpIxvM=", user.getPassword());
            assertEquals("Hello! I'm John Doe.", user.getProfile());
            assertEquals("johndoe.png", user.getPicture());
        }

        //Case B: Test if constructors work as intended, if one or more fields are empty strings
        //This means that the account should not be created, therefore we check if the fields are null or not
        @Test(timeout = 1000)
        public void verifyUserConstructorOutputNull() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                // Call the constructor that is expected to throw the exception
                new User("", "", "", "", "");
            });
            assertEquals("All fields must be filled.", exception.getMessage());
        }

        //Case C: Test if a user can be successfully saved to a file
        // read file and see if username matches our initial user's username
        @Test(timeout = 1000)
        public void verifySaveUsersToFile() throws FileNotFoundException {
            User.addUser("A", "B", "C", "D", "E");

            User.saveUsersToFile();

            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("userDatabase.ser"))) {
                while (true) {
                    try {
                        CopyOnWriteArrayList<User> users = (CopyOnWriteArrayList<User>) in.readObject();
                        assertEquals(1, users.size());
                        assertEquals("A", users.get(0).getName());
                    } catch (EOFException | ClassNotFoundException e) {
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Case D: Test if a user with a pre-existing username is allowed
        //This shouldn't be allowed and should return False
        @Test(timeout = 1000)
        public void verifyUsernameAvailabilityDuplicate() throws FileNotFoundException {
            User.addUser("A", "B", "C", "D", "E");

            assertFalse(User.checkUserNameAvailability("B"));
        }

        //Case E: Test if a user with a unique username is allowed
        //Should return True
        @Test(timeout = 1000)
        public void verifyUsernameAvailabilityUnique() throws FileNotFoundException {
            User.addUser("A", "B", "C", "D", "E");

            assertTrue(User.checkUserNameAvailability("A"));
        }
    }

}