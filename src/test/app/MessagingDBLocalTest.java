package src.test.app;

import org.junit.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import src.main.app.User;
import java.io.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

/**
 * Team Project -- MessagingDBLocalTest
 *
 * This program defines the test cases for verifying the behavior
 * of the MessagingDB class, such as determine capabilities of basic
 * messaging and photo messaging methods.
 *
 * @author Haiyan Xuan, lab sec L18
 *
 * @version November 3, 2024
 */

@RunWith(Enclosed.class)
public class MessagingDBLocalTest {
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
     *
     * The following defines each test case used in this file
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

        //Case A: Test if messages can be successfully added, when two users are friends (successful)
        @Test(timeout = 1000)
        public void verifyAddMessageSuccessFriend() {
            User user1 = new User("A", "B", "C", "D", "E");
            User user2 = new User("V", "W", "X", "Y", "Z");
            user1.getFriendsDB().sendFriendRequest(user2);
            user1.getFriendsDB().approveFriendRequest(user2);

            assertTrue(user1.getMessagingDB().addMessage("Hi", user2));
        }

        //Case B: Test if messages are not added when one of two users has blocked each other (failure)
        @Test(timeout = 1000)
        public void verifyAddMessageFailureBlocked() {
            User user1 = new User("A", "B", "C", "D", "E");
            User user2 = new User("V", "W", "X", "Y", "Z");
            user1.getFriendsDB().blockUser(user2);

            assertFalse(user1.getMessagingDB().addMessage("Hello", user2));
            assertTrue(user1.getMessagingDB().getMessages(user2).isEmpty()); //since an ArrayList of message won't be created
        }

        //Case C: Test if messages are not added when two users are not friends (failure)
        @Test(timeout = 1000)
        public void verifyAddMessageFailureNotFriend() {
            User user1 = new User("A", "B", "C", "D", "E");
            User user2 = new User("V", "W", "X", "Y", "Z");

            assertFalse(user1.getMessagingDB().addMessage("Hi There", user2));
            assertTrue(user1.getMessagingDB().getMessages(user2).isEmpty());
        }

        //Case D: Test if messages are not added when two users have pending requests (failure)
        @Test(timeout = 1000)
        public void verifyAddMessageFailurePending() {
            User user1 = new User("A", "B", "C", "D", "E");
            User user2 = new User("V", "W", "X", "Y", "Z");
            user1.getFriendsDB().sendFriendRequest(user2);

            assertFalse(user1.getMessagingDB().addMessage("Hi", user2));
            assertTrue(user1.getMessagingDB().getMessages(user2).isEmpty());
        }

        //Case E: Test if messages can be successfully deleted
        @Test(timeout = 1000)
        public void verifyDeleteMessageSuccess() {
            User user1 = new User("A", "B", "C", "D", "E");
            User user2 = new User("V", "W", "X", "Y", "Z");
            user1.getFriendsDB().sendFriendRequest(user2);
            user1.getFriendsDB().approveFriendRequest(user2);
            assertTrue(user1.getMessagingDB().addMessage("Hi", user2));
            user1.getMessagingDB().deleteMessage("Hi", user2);
        }

        //Case F: Test if messages that do not exist can, failure
        @Test(timeout = 1000)
        public void verifyDeleteMessageFailure() {
            User user1 = new User("A", "B", "C", "D", "E");
            User user2 = new User("V", "W", "X", "Y", "Z");
            user1.getFriendsDB().sendFriendRequest(user2);
            user1.getFriendsDB().approveFriendRequest(user2);
            assertFalse(user1.getMessagingDB().deleteMessage("Hi", user2));
        }

        //Case G: Test if users can message each other if they are friends (successful)
        @Test(timeout = 1000)
        public void canMessageSuccessFriend() {
            User user1 = new User("A", "B", "C", "D", "E");
            User user2 = new User("V", "W", "X", "Y", "Z");
            user1.getFriendsDB().sendFriendRequest(user2);
            user1.getFriendsDB().approveFriendRequest(user2);
            assertTrue(user1.getMessagingDB().canMessage(user2));
        }

        //Case H: Test if users can message each other if they are not friends (failure)
        @Test(timeout = 1000)
        public void canMessageFailureNotFriend() {
            User user1 = new User("A", "B", "C", "D", "E");
            User user2 = new User("V", "W", "X", "Y", "Z");
            assertFalse(user1.getMessagingDB().canMessage(user2));
        }

        //Case H: Test if users can message each other if they are not friends (failure)
        @Test(timeout = 1000)
        public void canMessageFailureBlocked() {
            User user1 = new User("A", "B", "C", "D", "E");
            User user2 = new User("V", "W", "X", "Y", "Z");
            user1.getFriendsDB().blockUser(user2);
            assertFalse(user1.getMessagingDB().canMessage(user2));
        }

        //Case H: Test if users can message each other if they are not friends (failure)
        @Test(timeout = 1000)
        public void canMessageFailurePending() {
            User user1 = new User("A", "B", "C", "D", "E");
            User user2 = new User("V", "W", "X", "Y", "Z");
            user1.getFriendsDB().sendFriendRequest(user2);
            assertFalse(user1.getMessagingDB().canMessage(user2));
        }

        //Case I: Test if files can be successfully added into photos list if they are friends (successful)
        @Test(timeout = 1000)
        public void verifyPhotoMessageSuccessFriend() {
            File photo = new File("test.jpg");
            User user1 = new User("A", "B", "C", "D", "E");
            User user2 = new User("V", "W", "X", "Y", "Z");
            user1.getFriendsDB().sendFriendRequest(user2);
            user1.getFriendsDB().approveFriendRequest(user2);
            assertTrue(user1.getMessagingDB().photoMessage(photo, user2));
            assertTrue(user1.getMessagingDB().getPhotos(user2).contains(photo));
        }

        //Case J: Test if files can not be added into photos list if they are blocked (failure)
        @Test(timeout = 1000)
        public void verifyPhotoMessageFailureBlocked() {
            File photo = new File("test.jpg");
            User user1 = new User("A", "B", "C", "D", "E");
            User user2 = new User("V", "W", "X", "Y", "Z");
            user1.getFriendsDB().blockUser(user2);
            assertFalse(user1.getMessagingDB().photoMessage(photo, user2));
            assertTrue(user1.getMessagingDB().getPhotos(user2).isEmpty());
        }

        //Case K: Test if files can not be added into photos list if they are not friends (failure)
        @Test(timeout = 1000)
        public void verifyPhotoMessageFailureNotFriend() {
            File photo = new File("test.jpg");
            User user1 = new User("A", "B", "C", "D", "E");
            User user2 = new User("V", "W", "X", "Y", "Z");

            assertFalse(user1.getMessagingDB().photoMessage(photo, user2));
            assertTrue(user1.getMessagingDB().getPhotos(user2).isEmpty());
        }

        //Case L: Test if files can not be added into photos list if they have pending friend requests (failure)
        @Test(timeout = 1000)
        public void verifyPhotoMessageFailurePending() {
            File photo = new File("test.jpg");
            User user1 = new User("A", "B", "C", "D", "E");
            User user2 = new User("V", "W", "X", "Y", "Z");
            user1.getFriendsDB().sendFriendRequest(user2);

            assertFalse(user2.getMessagingDB().photoMessage(photo, user2));
            assertTrue(user1.getMessagingDB().getPhotos(user2).isEmpty());
        }
    }
}