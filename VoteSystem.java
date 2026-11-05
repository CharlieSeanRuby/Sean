/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package schooleventregistration;
 import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
/**
 *
 * @author Sean Mino
 */
public class VoteSystem {
    private static final String ACCOUNTS_FILE = "accounts.csv";
    private static final String VOTES_FILE = "votes.csv";

    // In-memory trackers for the active session
    // Tracks overall completed ballots: Set of usernames
    private static final Set<String> finalizedVoters = new HashSet<>();

    // Tracks current session votes: Map<Username, Map<Position, CandidateID>>
    private static final Map<String, Map<String, String>> sessionVotes = new HashMap<>();

    /**
     * Integrates directly with your login style to verify credentials and ensure 
     * the user is a valid voter.
     * @param username
     */
    public static boolean authenticateVoter(String username, String password) {
        String role = login(username, password);
        
        // Ensure credentials are valid and the account is permitted to vote
        if (role == null) {
            System.out.println("Invalid username or password.");
            return false;
        }
        
        // Optional: If you have roles like "Teacher" or "Student", restrict as needed:
        if (!role.equalsIgnoreCase("Student")) {
            System.out.println("Access Denied: Only students are allowed to vote.");
            return false;
        }

        return true;
    }

    /**
     * Your existing login method (matches your accounts.csv structure)
     */
    public static String login(String username, String password) {
        username = username.trim();
        File file = new File(ACCOUNTS_FILE);

        if (!file.exists()) {
            return null;
        }

        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] data = line.split(",");
                if (data.length >= 3 && data[0].equalsIgnoreCase(username)) {
                    if (data[1].equals(password)) {
                        return data[2].trim(); // Returns accountType (e.g., "Student")
                    }
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Enforces: 1 Candidate per Position per Account.
     */
    public static String castVote(String username, String candidateId, String candidateName, String position) {
        username = username.trim();

        // Check if the user has already submitted a finalized ballot (either in memory or CSV)
        if (hasAlreadyVotedInCSV(username) || finalizedVoters.contains(username)) {
            return "Error: Account '" + username + "' has already submitted their votes.";
        }

        // Retrieve or initialize the user's ballot
        sessionVotes.putIfAbsent(username, new HashMap<>());
        Map<String, String> userBallot = sessionVotes.get(username);

        // Check 1 candidate per position restriction
        if (userBallot.containsKey(position)) {
            return "Error: You have already selected a candidate for position: " + position;
        }

        // Save selection to temporary session ballot
        userBallot.put(position, candidateId);
        return "Success: Voted for " + candidateName + " (" + candidateId + ") for " + position;
    }

    /**
     * Saves the user's votes permanently into votes.csv and locks their ballot.
     */
    public static boolean submitBallot(String username) {
        username = username.trim();
        Map<String, String> userBallot = sessionVotes.get(username);

        if (userBallot == null || userBallot.isEmpty()) {
            System.out.println("No votes selected to submit.");
            return false;
        }

        File file = new File(VOTES_FILE);

        try {
            // Create votes.csv with header if it doesn't exist
            if (!file.exists()) {
                try (PrintWriter writer = new PrintWriter(
                        new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
                    writer.println("username,position,candidateId");
                }
            }

            // Append all votes for this student
            try (PrintWriter writer = new PrintWriter(
                    new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8))) {
                for (Map.Entry<String, String> entry : userBallot.entrySet()) {
                    String position = entry.getKey();
                    String candidateId = entry.getValue();
                    writer.println(username + "," + position + "," + candidateId);
                }
            }

            // Lock account from voting again
            finalizedVoters.add(username);
            sessionVotes.remove(username);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks votes.csv to prevent users from voting again across program restarts.
     */
    private static boolean hasAlreadyVotedInCSV(String username) {
        File file = new File(VOTES_FILE);
        if (!file.exists()) return false;

        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] data = line.split(",");
                if (data.length >= 1 && data[0].equalsIgnoreCase(username)) {
                    return true; // Match found in votes record
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
