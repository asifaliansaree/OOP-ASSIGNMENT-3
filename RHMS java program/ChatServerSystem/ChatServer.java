package ChatServerSystem;

import ExceptionHandler.RpmsErrorHandling;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ChatServer {
    private static final Logger LOGGER = Logger.getLogger(ChatServer.class.getName());
    private final List<String> chatHistory;
    private boolean isActiveSession;

    public ChatServer() {
        this.chatHistory = new ArrayList<>();
        this.isActiveSession = false;
    }

    /**
     * Starts a new chat session.
     * @throws RpmsErrorHandling If a session is already active.
     */
    public synchronized void startSession() throws RpmsErrorHandling {
        if (isActiveSession) {
            throw new RpmsErrorHandling("Chat session already in progress!");
        }
        chatHistory.clear();
        isActiveSession = true;
        logSystemMessage("Chat session started");
    }

    /**
     * Routes a message from a sender to the chat session.
     * @param sender The sender's identifier.
     * @param message The message content.
     * @throws RpmsErrorHandling If no active session exists or inputs are invalid.
     */
    public synchronized void routeMessage(String sender, String message) throws RpmsErrorHandling {
        if (!isActiveSession) {
            throw new RpmsErrorHandling("No active chat session!");
        }
        if (sender == null || sender.trim().isEmpty()) {
            throw new RpmsErrorHandling("Sender cannot be null or empty");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new RpmsErrorHandling("Message cannot be null or empty");
        }
        String formattedMessage = "[" + sender + "]:" + message;
        chatHistory.add(formattedMessage);
        LOGGER.info("Message routed: " + formattedMessage);
    }

    /**
     * Ends the current chat session.
     * @throws RpmsErrorHandling If no session is active.
     */
    public synchronized void endSession() throws RpmsErrorHandling {
        if (!isActiveSession) {
            throw new RpmsErrorHandling("Session is not active");
        }
        logSystemMessage("Session ended");
        isActiveSession = false;
    }

    /**
     * Returns a copy of the current session's transcript.
     * @return A list of messages in the session.
     */
    public synchronized List<String> getSessionTranscript() {
        return new ArrayList<>(chatHistory);
    }

    /**
     * Logs a system message to the chat history.
     * @param message The system message to log.
     */
    private void logSystemMessage(String message) {
        String systemMessage = "[System]:" + message;
        chatHistory.add(systemMessage);
        LOGGER.info(systemMessage);
    }

    /**
     * Checks if a chat session is active.
     * @return True if a session is active, false otherwise.
     */
    public boolean isSessionActive() {
        return isActiveSession;
    }
}