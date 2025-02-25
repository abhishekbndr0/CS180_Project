# CS180 Project - Messaging and User Implementations
This project is a Java-based program that handles messaging and user management. There is functionality to add, manage, and store user data, and includes unit tests to check functionality.

## Running the Project
To run the project, follow these steps:

1. **Compile the Project**:
    - Make sure you are in the project's root directory.
    - Run this command to compile the project:
      ```bash
      javac -d bin src/*.java
      ```

2. **Run the Tests**:
    - To execute the JUnit tests in `src.test.app.FriendsDBLocalTest`, run:
      ```bash
      java -cp .;path/to/junit.jar;path/to/hamcrest.jar src.test.app.FriendsDBLocalTest
      ```
    - To execute the JUnit tests in `src.test.app.MessagingDBLocalTest`, run:
      ```bash
      java -cp .;path/to/junit.jar;path/to/hamcrest.jar src.test.app.MessagingDBLocalTest
      ```
    - To execute the JUnit tests in `src.test.app.UserLocalTest`, run:
      ```bash
      java -cp .;path/to/junit.jar;path/to/hamcrest.jar src.test.app.UserLocalTest
      ```
    - To execute the JUnit tests in `src.test.app.ClientLocalTest`, run:
      ```bash
      java -cp .;path/to/junit.jar;path/to/hamcrest.jar src.test.app.ClientLocalTest
      ```
    - To execute the JUnit tests in `src.test.app.ServerLocalTest`, run:
      ```bash
      java -cp .;path/to/junit.jar;path/to/hamcrest.jar src.test.app.ServerLocalTest
      ```
    - Successful test runs will print "Excellent - Test ran successfully".

### ------------------------------------------------------------------------------
## IMPORTANT - PLEASE READ
3. **Configure Project Dependencies**:
    - Select the CS180-Team-Project folder.
    - Press `Control+Shift+A`.
    - Search for "project structure".
    - Go to the Modules tab on the left side.
    - Select Dependencies in the middle bar.
    - Click the "+" button, choose JARs/Directories, and select the path to the attached JAR file (link to the file).
### ------------------------------------------------------------------------------

4. **Load and Save User Data**:
    - When the application is running, user data will be saved to `userDatabase.ser` using `saveUsersToFile()`.
    - `loadUsersFromFile()` can retrieve saved data and reload users into the application.

## Project Structure
These are the main classes and interfaces of the program:

### Main App Classes

#### `User`
- **Description**: Represents a user in the system, with fields like `name`, `username`, `password`, `profile`, and `picture`. The class also manages user persistence using serialization to save and load data to a file.
- **Methods**:
    - `getName()`: Returns the name of the `User`.
    - `getUsername()`: Returns the username of the `User`.
    - `getPassword()`: Returns the password of the `User`.
    - `getProfile()`: Returns the profile description of the `User`.
    - `getPicture()`: Returns the profile picture of the `User`.
    - `getUsers()`: Returns the list of all `users`.
    - `run()`: Implementation of the `Runnable` interface. Prints a message when the user thread is running.
    - `saveUsersToFile()`: Saves the list of `users` to a file. Uses serialization to write the users ArrayList to a file specified by FILE_NAME. Uses synchronization to ensure thread safety.
    - `loadUsersFromFile()`: Loads the list of `users` from a file. Deserializes the users ArrayList from the file specified by FILE_NAME and starts a thread for each user in the list. Uses synchronization to ensure thread safety.
    - `addUser()`: Adds a new `User` to the `users` list if the `username` is available. Creates a new `User` instance, adds it to the list, and saves the list to a file. Uses synchronization to ensure thread safety.
    - `checkUserNameAvailability()`: Checks if a given username is available (i.e., not already used by an existing user). Iterates through the users list to see if the username matches any existing user. Uses synchronization to ensure thread safety.
    - `displayAllUsers()`: Displays the names and usernames of all users in the users list. Iterates through the users list and prints each user's name and username. Uses synchronization to ensure thread safety.
    - `searchUser()`: Searches for a user by username and returns their details if found and not blocked; otherwise, returns "User Not Found".
- **Implements**: `Runnable` and `Serializable` for multi-threading and persistence. It also implements `UserInterface`.

#### `Server`
- **Description**: Represents the server in the system, which listens for client connections and manages communication with connected clients using multi-threading. The server uses a thread pool to efficiently handle multiple clients simultaneously.
- **Methods**:
    - `start()`: Starts the server and listens for incoming client connections. Each client connection is handled by a separate thread from the thread pool.
    - `stop()`: Stops the server, shuts down the thread pool, and closes the server socket.
    - `main()`: Initializes the server with a specified port and starts it.
- **Implements**: `ServerInterface`.

#### `Client`
- **Description**: Represents a client connected to the server. Each instance of the `Client` class handles communication between the server and a specific client using input and output streams. It implements `Runnable` to handle client requests in a separate thread.
- **Methods**:
    - `run()`: This method is executed when a client is handled in a separate thread. It listens for incoming messages from the client and sends acknowledgment messages back to the client.
    - `getClientSocket()`: This method retrieves the `clientSocket` field, which represents the socket connection associated with the client.
- **Implements**: `Runnable` to enable multi-threading, allowing each client to be handled concurrently by the server. It also implements `ClientInterface`.

#### `ClientHandler`
- **Description**: Handles client connections in a server application. It manages communication between the server and individual clients, processes incoming messages, and handles client requests such as login and messaging.
- **Methods**:
  - `run()`: Continuously processes incoming messages from the client. Listens for requests, validates them, and performs actions like sending messages or handling user authentication.
  - `sendMessage(String message)`: Sends a response message back to the client.
  - `closeConnections()`: Closes the socket, input, and output streams to properly disconnect from the client.
- **Implements**: `Runnable` (for handling connections in a separate thread).

#### `FriendsDB`
- **Description**: Represents friend implementation and friend request management.
- **Methods**:
    - `sendFriendRequest()`: Initiates a friend request from one user (`requester`) to another (`requested`).
    - `approveFriendRequest()`: Approves a pending friend request, adding both users to each other's friends list.
    - `removeFriend()`: Removes a specified user from the `friends` list.
    - `blockUser()`: Blocks a specified user and removes them from the `friends` list if they were previously friends.
    - `isFriend()`: Checks if a specified user is in the `friends` list.
    - `isBlocked()`: Checks if a specified user is in the `blockedUsers` list.
- **Implements**: `Serializable` to allow its state, including users and their relationships, to be saved and restored later as a byte stream. Also implements `FriendsDBInterface`.

#### `Message`
- **Description**: Represents a message exchanged between two users. The message can either be a text message or a photo message. It includes information such as the sender, recipient, content, timestamp, and the photo (if applicable).
- **Methods**:
  - `getSender()`: Returns the sender of the message.
  - `getRecipient()`: Returns the recipient of the message.
  - `getContent()`: Returns the content of the text message (if applicable).
  - `getPhoto()`: Returns the photo file (if applicable).
  - `getTimestamp()`: Returns the timestamp of when the message was sent.
  - `toString()`: Returns a string representation of the message, indicating whether it's a text or photo message and including the timestamp, sender, and content.
- **Implements**: Implements `Serializable` for object serialization.


#### `MessagingDB`
- **Description**: Manages text and photo messaging between users. The class handles storing, adding, and deleting messages and photos. It ensures that messages and photos can only be exchanged between friends, as determined by the user's friend list.
- **Methods**:
    - `getMessages()`: Returns a list of text messages for a given recipient.
    - `getPhotos()`: Returns a list of photo messages for a given recipient.
    - `addMessage()`: Adds a message to the database.
    - `deleteMessage()`: Deletes a specified message from the database.
    - `canMessage()`: Checks if a user is a friend of the owner and can receive messages.
    - `photoMessage()`: Sends a photo message.
- **Implements**: Implements `Serializable` for object serialization and `MessagingDBInterface`.

### Project GUI Classes

#### `AppGUI`
- **Description**: Provides the graphical user interface (GUI) for interacting with the messaging application. It allows users to send messages, manage friend requests, and view their friends list through an intuitive interface.
- **Methods**:
  - `initialize()`: Initializes the GUI components, setting up the layout and event listeners.
  - `updateUI()`: Updates the GUI with the latest information, such as new messages or friend requests.
  - `sendMessage()`: Sends a message to a selected friend or group through the interface.
  - `receiveMessage()`: Displays incoming messages in the chat interface.
  - `showError()`: Displays error messages to the user in case of issues like failed connection or invalid input.
- **Implements**: Implements `AppGUIInterface`.

#### `LoginGUI`
- **Description**: Provides a graphical user interface for users to log in. It includes fields for username and password, buttons for login and navigation, and a background GIF that dynamically scales with the window size.
- **Methods**:
  - `LoginGUI()`: Constructor that sets up the GUI components, including the login form and background GIF.
  - `doLogin()`: Handles the login logic when the "Login" button is clicked. Verifies the credentials and displays a success or error message.
  - `main(String[] args)`: Main method to launch the `LoginGUI` independently for testing purposes. Loads user data from a file at startup.
- **Implements**: Implements `LoginGUIInterface`. Extends `JFrame` to create a windowed GUI. Uses `FlatLightLaf` for the theme and Swing components like `JTextField`, `JPasswordField`, and `JButton`.

#### `RegisterGUI`
- **Description**: Provides the graphical user interface (GUI) for user registration. This interface allows new users to enter necessary details such as name, username, password, profile description, and profile picture URL. It includes background styling with a GIF and custom input field styling.
- **Methods**:
  - `createStyledLabel(String text)`: Creates a custom-styled label with a specific font and color.
  - `createStyledTextField()`: Creates a semi-transparent styled text field.
  - `createStyledPasswordField()`: Creates a custom-styled password field.
  - `createStyledTextArea()`: Creates a styled text area for profile descriptions.
  - `createStyledButton(String text)`: Creates a custom-styled button.
  - `doRegister()`: Handles the registration logic when the "Register" button is clicked, including input validation, password strength check, username availability check, and adding the user to the system.
  - `main(String[] args)`: Launches the `RegisterGUI` for testing purposes.
- **Implements**: Implements `RegisterGUIInterface`.

#### `WelcomeGUI`
- **Description**: Displays a welcome screen for the Steezagram app with a custom GIF background, Pacifico font, and styled buttons. Users can navigate to the login or registration screens through the buttons provided. The GUI is centered on the screen and includes a title and a caption.
- **Methods**:
  - `WelcomeGUI()`: Constructor that sets up the GUI layout, including the GIF background, title, buttons for login and registration, and overlay panels.
  - `loadCustomFont(String fontPath, float size)`: Loads a custom font from the provided file path with the specified size. Falls back to Arial if the font loading fails.
  - `createStyledLabel(String text)`: Creates a custom-styled label with a specific font and color.
  - `createStyledButton(String text)`: Creates a custom-styled button with transparency and custom font.
  - `main(String[] args)`: Launches the `WelcomeGUI` with FlatLaf light theme applied for consistent styling.
- **Implements**: Implements `WelcomeGUIInterface`.

### Test App Classes

#### `FriendsDBLocalTest`
- **Description**: JUnit test cases for verifying the core functionality of the `FriendsDB` class, focusing on user interactions such as sending, approving, and removing friend requests, as well as blocking users.
- **Key Test Cases**:
    - `verifySendFriendRequestSuccess()`: Tests if friend requests can be initiated successfully.
    - `verifySendFriendRequestBlocked()`: Tests sending a friend request when a user is blocked.
    - `verifySendFriendRequestPending()`: Tests sending a duplicate friend request.
    - `verifyApproveFriendRequestSuccess()`: Tests successful approval of a friend request.
    - `verifyApproveFriendRequestNotFound()`: Tests approval when no request exists.
    - `verifyRemoveFriendSuccess()`: Tests successful removal of a friend.
    - `verifyRemoveFriendFailure()`: Tests failure when trying to remove a non-friend.
    - `verifyBlockUserStandard()`: Tests if a user can be blocked.
    - `verifyBlockUserNotFriend()`: Tests if a user can be blocked when not friends.
    - `verifyBlockUserFailure()`: Tests failure to block a user who is already blocked.

#### `MessagingDBLocalTest`
- **Description**: Contains JUnit test cases for the `MessagingDB` class, focusing on core messaging and photo messaging functionality, including success and failure scenarios based on user relationships (friends, blocked, pending requests).
- **Key Test Cases**:
    - `verifyAddMessageSuccessFriend()`: Tests successful addition of messages between friends.
    - `verifyAddMessageFailureBlocked()`: Tests failure to add messages when one user has blocked the other.
    - `verifyAddMessageFailureNotFriend()`: Tests failure to add messages between non-friends.
    - `verifyAddMessageFailurePending()`: Tests failure to add messages when a friend request is pending.
    - `verifyDeleteMessageSuccess()`: Tests successful deletion of a message.
    - `verifyDeleteMessageFailure()`: Tests failure to delete a non-existing message.
    - `canMessageSuccessFriend()`: Tests if users can message each other when they are friends.
    - `canMessageFailureNotFriend()`: Tests failure to message when users are not friends.
    - `canMessageFailureBlocked()`: Tests failure to message when one user has blocked the other.
    - `canMessageFailurePending()`: Tests failure to message when there is a pending friend request.
    - `verifyPhotoMessageSuccessFriend()`: Tests successful addition of photo messages between friends.
    - `verifyPhotoMessageFailureBlocked()`: Tests failure to add photo messages when one user has blocked the other.
    - `verifyPhotoMessageFailureNotFriend()`: Tests failure to add photo messages between non-friends.
    - `verifyPhotoMessageFailurePending()`: Tests failure to add photo messages when there is a pending friend request.

#### `UserLocalTest`
- **Description**: Contains JUnit test cases for the `User` class, verifying functionality related to user creation, file persistence, username availability, and user listing.
- **Key Test Cases**:
    - `verifyUserConstructorOutput()`: Tests if the `User` constructor correctly initializes all fields.
    - `verifyUserConstructorOutputNull()`: Ensures that empty input fields in the `User` constructor are set to `null`.
    - `verifySaveUsersToFile()`: Checks if users are correctly saved to a file and can be retrieved.
    - `verifyUsernameAvailabilityDuplicate()`: Ensures `checkUserNameAvailability()` returns `false` for a duplicate username.
    - `verifyUsernameAvailabilityUnique()`: Checks if `checkUserNameAvailability()` returns `true` for unique usernames.
    - `verifyDisplayAllUsers()`: Tests if users can be successfully displayed when saved to the list of users.
    - `verifyLoadUsersFromFile()`: Tests if users can be successfully loaded from a file.

#### `ClientLocalTest`
- **Description**: Contains JUnit test cases for the `Client` class, ensuring proper initialization, field existence, interface implementation, and method accessibility.
- **Key Test Cases**:
    - `testClientClassExists()`: Verifies that an instance of the `Client` class is created successfully.
    - `testClientConstructor()`: Tests that the `Client` constructor correctly initializes the Client object.
    - `testClientHasFieldForSocket()`: Ensures that the `Client` class has a `clientSocket` field.
    - `testClientImplementsRunnable()`: Validates that the `Client` class implements the `Runnable` interface.
    - `testClientRunMethod()`: Confirms the presence of the `run` method in the `Client` class, ensuring it is accessible.

#### `ServerLocalTest`
- **Description**: Contains JUnit test cases for the Server class, focusing on the existence of fields, the constructor, and the accessibility of methods.
- **Key Test Cases**:
    - `testServerClassExists()`: Verifies that an instance of the Server class is created successfully.
    - `testServerConstructor()`: Tests that the Server constructor initializes the serverPort field correctly.
    - `testServerHasFieldForPort()`: Ensures the Server class has a serverPort field.
    - `testServerHasFieldForThreadPool()`: Verifies that the Server class has a threadPool field.
    - `testServerStartMethod()`: Confirms the presence and accessibility of the start method in the Server class.
    - `testServerStopMethod()`: Ensures the stop method is present and accessible in the Server class.

Created by Abhishek Bandaru.
