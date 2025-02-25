package src.test.app;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import src.main.app.*;
import java.io.*;
import static org.junit.Assert.*;

/**
 * Team Project -- FriendsDBLocalTest
 *
 * This program defines the test cases for verifying the behavior
 * of the FriendsDB class, such as determining the different cases of how
 * users friend/block each other.
 *
 * @author Haiyan Xuan, lab sec L18
 *
 * @version November 3, 2024
 */

@RunWith(Enclosed.class)
public class FriendsDBLocalTest {
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
     * Team Project -- TestCase
     *
     * This defines the test cases for this file
     *
     * @author Haiyan Xuan, lab sec L18
     *
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

        //Case A: Test if friend requests can be initiated successfully
        @Test(timeout = 1000)
        public void verifySendFriendRequestSuccess() {
            User user1 = new User("A", "B", "C", "D", "E");
            User user2 = new User("V", "W", "X", "Y", "Z");
            user1.getFriendsDB().sendFriendRequest(user2);

            assertTrue(user1.getFriendsDB().getPendingRequests().contains(user2));
        }

        //Case B: Test if friend requests can be sent if a either the blocker or blocked is blocked
        @Test(timeout = 1000)
        public void verifySendFriendRequestBlocked() {
            User user1 = new User("A", "B", "C", "D", "E");
            User user2 = new User("V", "W", "X", "Y", "Z");
            user1.getFriendsDB().blockUser(user2);
            user1.getFriendsDB().sendFriendRequest(user2);

            assertFalse(user1.getFriendsDB().getPendingRequests().contains(user2));
        }

        //Case C: Test if friend requests can be sent if it has already been sent before
        @Test(timeout = 1000)
        public void verifySendFriendRequestPending() {
            User user1 = new User("A", "B", "C", "D", "E");
            User user2 = new User("V", "W", "X", "Y", "Z");
            user1.getFriendsDB().sendFriendRequest(user2);
            user1.getFriendsDB().sendFriendRequest(user2);

            assertTrue(user1.getFriendsDB().getPendingRequests().contains(user2));
        }

        //Case D: Test if friend requests can be approved sucessfully
        @Test(timeout = 1000)
        public void verifyApproveFriendRequestSuccess() {
            User user1 = new User("A", "B", "C", "D", "E");
            User user2 = new User("V", "W", "X", "Y", "Z");
            user1.getFriendsDB().sendFriendRequest(user2);
            user1.getFriendsDB().approveFriendRequest(user2);

            assertTrue(user1.getFriendsDB().isFriend(user2));
        }

        //Case E: Test if friend requests can be approved even if a request has not been sent before
        //This should not be allowed, since it is not possible to approve something that does not exists
        @Test(timeout = 1000)
        public void verifyApproveFriendRequestNotFound() {
            User user1 = new User("A", "B", "C", "D", "E");
            User user2 = new User("V", "W", "X", "Y", "Z");
            user1.getFriendsDB().approveFriendRequest(user2);

            assertFalse(user1.getFriendsDB().isFriend(user2));
            assertFalse(user2.getFriendsDB().isFriend(user1));
        }

        //Case F: Test if friends can be removed sucessfully
        @Test(timeout = 1000)
        public void verifyRemoveFriendSuccess() {
            User user1 = new User("A", "B", "C", "D", "E");
            User user2 = new User("V", "W", "X", "Y", "Z");
            user1.getFriendsDB().sendFriendRequest(user2);
            user1.getFriendsDB().approveFriendRequest(user2);
            user1.getFriendsDB().removeFriend(user2);

            assertFalse(user1.getFriendsDB().isFriend(user2));
            assertFalse(user2.getFriendsDB().isFriend(user1));
        }

        //Case G: Test if friends can be removed in the case they are not friends before
        //This should not be allowed, since they were not friends before removing
        @Test(timeout = 1000)
        public void verifyRemoveFriendFailure() {
            User user1 = new User("A", "B", "C", "D", "E");
            User user2 = new User("V", "W", "X", "Y", "Z");
            user1.getFriendsDB().removeFriend(user2);

            assertFalse(user1.getFriendsDB().isFriend(user2));
            assertFalse(user2.getFriendsDB().isFriend(user1));
        }

        //Case H: Test if users can be blocked
        @Test(timeout = 1000)
        public void verifyBlockUserStandard() {
            User user1 = new User("A", "B", "C", "D", "E");
            User user2 = new User("V", "W", "X", "Y", "Z");
            user1.getFriendsDB().sendFriendRequest(user2);
            user1.getFriendsDB().approveFriendRequest(user2);
            user1.getFriendsDB().blockUser(user2);

            assertTrue(user1.getFriendsDB().getBlockedUsers().contains(user2));
        }

        //Case H: Test if users can be blocked
        @Test(timeout = 1000)
        public void verifyBlockUserNotFriend() {
            User user1 = new User("A", "B", "C", "D", "E");
            User user2 = new User("V", "W", "X", "Y", "Z");
            user1.getFriendsDB().blockUser(user2);

            assertTrue(user1.getFriendsDB().getBlockedUsers().contains(user2));
        }

        //Case I: Test if users can be blocked even if they have already been blocked before
        //This should not be possible, since they have been blocked before
        @Test(timeout = 1000)
        public void verifyBlockUserFailure() {
            User user1 = new User("A", "B", "C", "D", "E");
            User user2 = new User("V", "W", "X", "Y", "Z");
            user1.getFriendsDB().blockUser(user2);
            user1.getFriendsDB().blockUser(user2);

            assertTrue(user1.getFriendsDB().getBlockedUsers().contains(user2));
        }
    }
}