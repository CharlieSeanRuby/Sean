    package schooleventregistration;

import java.io.*;
import java.util.*;

public class VoteSystem1 {

    private static final String VOTES_FILE = "votes.csv";

    // Did this student already submit? (checks the csv)
    public static boolean hasVoted(String username) {
        File file = new File(VOTES_FILE);
        if (!file.exists()) {
            return false;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // skip the header
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equalsIgnoreCase(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Save the whole ballot: one line per position
    public static boolean saveVotes(String username, Map<String, String> ballot) {
        File file = new File(VOTES_FILE);
        boolean isNew = !file.exists();

        try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
            if (isNew) {
                writer.println("username,position,candidate");
            }
            for (Map.Entry<String, String> entry : ballot.entrySet()) {
                writer.println(username + "," + entry.getKey() + "," + entry.getValue());
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Count votes: position -> (candidate -> number of votes)
    public static Map<String, Map<String, Integer>> getTally() {
        Map<String, Map<String, Integer>> tally = new LinkedHashMap<>();
        File file = new File(VOTES_FILE);
        if (!file.exists()) {
            return tally;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // skip the header
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 3) continue;

                String position = data[1];
                String candidate = data[2];

                tally.putIfAbsent(position, new LinkedHashMap<>());
                Map<String, Integer> counts = tally.get(position);
                counts.put(candidate, counts.getOrDefault(candidate, 0) + 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tally;
    }
}