package ClientSide;

import ChatServerSystem.ChatServer;
import ExceptionHandler.RpmsErrorHandling;
import java.util.logging.Logger;

/**
 * Represents a client in the chat system, capable of sending messages and viewing chat history.
 */
public class Client {
    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
    private final String userRole;
    private final ChatServer server;

    /**
     * Constructs a new Client with a user role and associated chat server.
     * @param server The chat server to communicate with.
     * @param userRole The client's identifier or role.
     * @throws IllegalArgumentException If server is null.
     * @throws RpmsErrorHandling If userRole is null or empty.
     */
    public Client(ChatServer server, String userRole) throws RpmsErrorHandling {
        if (server == null) {
            throw new IllegalArgumentException("ChatServer cannot be null");
        }
        if (userRole == null || userRole.trim().isEmpty()) {
            throw new RpmsErrorHandling("User role cannot be null or empty");
        }
        this.server = server;
        this.userRole = userRole;
    }

    /**
     * Sends a message to the chat server.
     * @param message The message to send.
     * @throws RpmsErrorHandling If the message is null, empty, or the server rejects the message.
     */
    public void sendMessage(String message) throws RpmsErrorHandling {
        if (message == null || message.trim().isEmpty()) {
            throw new RpmsErrorHandling("Message cannot be empty");
        }
        server.routeMessage(userRole, message);
    }

    /**
     * Displays the chat history from the server.
     * @param onlyOwnMessages If true, only displays messages sent by this client.
     */
    public void displayChat(boolean onlyOwnMessages) {
        try {
            LOGGER.info("\n---Chat History---");
            for (String chat : server.getSessionTranscript()) {
                if (!onlyOwnMessages || chat.startsWith("[" + userRole + "]:")) {
                    LOGGER.info(chat);
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Error retrieving chat history: " + e.getMessage());
        }
    }

    /**
     * Returns the client's user role.
     * @return The user role.
     */
    public String getUserRole() {
        return userRole;
    }
}