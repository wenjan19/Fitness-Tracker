import javax.swing.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class FITT {
    public static void main(String[] args) {
        Map<String, User> users = new HashMap<>();
        User currentUser = null;

        while (true) {
            String[] options = {"Register", "Login", "Exit"};
            int choice = JOptionPane.showOptionDialog(null, "WELCOME TO FITNESS TRACKER!", "Fitness Tracker",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            if (choice == 0) {
                // Register
                String username = JOptionPane.showInputDialog("Enter a username:");
                String password = JOptionPane.showInputDialog("Enter a password:");
                double weight = Double.parseDouble(JOptionPane.showInputDialog("Enter your weight (kg):"));
                double height = Double.parseDouble(JOptionPane.showInputDialog("Enter your height (m):"));

                Profile profile = new Profile(weight, height);
                User user = new User(username, password, profile);
                users.put(username, user);

                JOptionPane.showMessageDialog(null, "Registration successful!");
            } else if (choice == 1) {
                // Login
                String username = JOptionPane.showInputDialog("Enter your username:");
                String password = JOptionPane.showInputDialog("Enter your password:");

                User user = users.get(username);

                if (user != null && user.getPassword().equals(password)) {
                    currentUser = user;
                    JOptionPane.showMessageDialog(null, "Login successful!");

                    while (currentUser != null) {
                        String[] userOptions = {"Create Workout Plan\n", "View Workout Plans\n", "Delete Workout Plan\n", "Search Workout Plan\n", "Calculate BMI\n", "Sleep Tracking\n", "View Sleep Data\n", "Reset Sleep Data\n", "Logout\n"};
                        int userChoice = JOptionPane.showOptionDialog(null, "Choose an option", "Fitness Tracker",
                                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, userOptions, userOptions[0]);

                        if (userChoice == 0) {
                            // Create Workout Plan
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = null;
                            try {
                                date = dateFormat.parse(JOptionPane.showInputDialog("Enter the date (yyyy-MM-dd):"));
                            } catch (ParseException e) {
                                JOptionPane.showMessageDialog(null, "Invalid date format.");
                                continue;
                            }
                            String time = JOptionPane.showInputDialog("Enter the time (HH:mm):");
                            String[] exercises = JOptionPane.showInputDialog("Enter exercises separated by commas:").split(",");
                            String title = JOptionPane.showInputDialog("Enter a title for the workout plan:");

                            WorkoutPlan workoutPlan = new WorkoutPlan(date, time, Arrays.asList(exercises), title);
                            currentUser.addWorkoutPlan(title, workoutPlan);
                            JOptionPane.showMessageDialog(null, "Workout plan created!");
                        } else if (userChoice == 1) {
                            // View Workout Plans
                            StringBuilder workoutPlanBuilder = new StringBuilder();
                            Map<String, WorkoutPlan> workoutPlansMap = currentUser.getWorkoutPlansMap();
                            if (workoutPlansMap.isEmpty()) {
                                workoutPlanBuilder.append("No workout plans available.");
                            } else {
                                workoutPlanBuilder.append("Workout Plans:\n");
                                for (Map.Entry<String, WorkoutPlan> entry : workoutPlansMap.entrySet()) {
                                    String title = entry.getKey();
                                    WorkoutPlan workoutPlan = entry.getValue();
                                    workoutPlanBuilder.append("\nTitle: ").append(title);
                                    workoutPlanBuilder.append("\nDate: ").append(workoutPlan.getDate());
                                    workoutPlanBuilder.append("\nTime: ").append(workoutPlan.getTime());
                                    workoutPlanBuilder.append("\nExercises: ").append(workoutPlan.getExercises());
                                    workoutPlanBuilder.append("\n");
                                }
                            }
                            JOptionPane.showMessageDialog(null, workoutPlanBuilder.toString());
                        } else if (userChoice == 2) {
                            // Delete Workout Plan
                            String title = JOptionPane.showInputDialog("Enter the title of the workout plan to delete:");
                            boolean deleted = currentUser.deleteWorkoutPlan(title);
                            if (deleted) {
                                JOptionPane.showMessageDialog(null, "Workout plan deleted!");
                            } else {
                                JOptionPane.showMessageDialog(null, "No such workout plan exists.");
                            }
                        } else if (userChoice == 3) {
                            // Search Workout Plan
                            String keyword = JOptionPane.showInputDialog("Enter the title of the workout plan to search for:");
                            List<WorkoutPlan> searchResults = currentUser.searchWorkoutPlan(keyword);
                            if (searchResults.isEmpty()) {
                                JOptionPane.showMessageDialog(null, "No matching workout plans found.");
                            } else {
                                StringBuilder searchResultBuilder = new StringBuilder();
                                searchResultBuilder.append("Search Results:\n");
                                for (WorkoutPlan workoutPlan : searchResults) {
                                    searchResultBuilder.append("\nTitle: ").append(workoutPlan.getTitle());
                                    searchResultBuilder.append("\nDate: ").append(workoutPlan.getDate());
                                    searchResultBuilder.append("\nTime: ").append(workoutPlan.getTime());
                                    searchResultBuilder.append("\nExercises: ").append(workoutPlan.getExercises());
                                    searchResultBuilder.append("\n");
                                }
                                JOptionPane.showMessageDialog(null, searchResultBuilder.toString());
                            }
                        } else if (userChoice == 4) {
                            // Calculate BMI
                            double weight = currentUser.getProfile().getWeight();
                            double height = currentUser.getProfile().getHeight();
                            double bmi = calculateBMI(weight, height);
                            JOptionPane.showMessageDialog(null, "Your BMI is: " + bmi);
                        } else if (userChoice == 5) {
                            // Sleep Tracking
                            String sleepDuration = JOptionPane.showInputDialog("Enter your sleep duration in hours:");
                            try {
                                double duration = Double.parseDouble(sleepDuration);
                                double quality = Double.parseDouble(JOptionPane.showInputDialog("Enter your sleep quality (0-10):"));
                                String stage = JOptionPane.showInputDialog("Enter your sleep stage:");
                                SleepData sleepData = new SleepData(duration, quality, stage);
                                currentUser.getProfile().addSleepData(sleepData);
                                JOptionPane.showMessageDialog(null, "Sleep data recorded successfully!");
                            } catch (NumberFormatException e) {
                                JOptionPane.showMessageDialog(null, "Invalid sleep duration or quality.");
                            }
                        } else if (userChoice == 6) {
                            // View Sleep Data
                            List<SleepData> sleepDataList = currentUser.getProfile().getSleepDataList();
                            if (sleepDataList.isEmpty()) {
                                JOptionPane.showMessageDialog(null, "No sleep data available.");
                            } else {
                                double averageDuration = calculateAverageSleepDuration(sleepDataList);
                                double averageQuality = calculateAverageSleepQuality(sleepDataList);
                                Map<String, Integer> stageCounts = calculateSleepStageCounts(sleepDataList);

                                StringBuilder sleepDataBuilder = new StringBuilder();
                                sleepDataBuilder.append("Sleep Data:\n");
                                sleepDataBuilder.append("Average Sleep Duration: ").append(averageDuration).append(" hours\n");
                                sleepDataBuilder.append("Average Sleep Quality: ").append(averageQuality).append("/10\n");
                                sleepDataBuilder.append("Sleep Stage Counts:\n");
                                for (Map.Entry<String, Integer> entry : stageCounts.entrySet()) {
                                    String stage = entry.getKey();
                                    int count = entry.getValue();
                                    sleepDataBuilder.append(stage).append(": ").append(count).append("\n");
                                }

                                JOptionPane.showMessageDialog(null, sleepDataBuilder.toString());
                            }
                        } else if (userChoice == 7) {
                            // Reset Sleep Data
                            int confirmChoice = JOptionPane.showConfirmDialog(null, "Are you sure you want to reset sleep data?", "Confirm Reset", JOptionPane.YES_NO_OPTION);
                            if (confirmChoice == JOptionPane.YES_OPTION) {
                                currentUser.getProfile().resetSleepData();
                                JOptionPane.showMessageDialog(null, "Sleep data reset successfully!");
                            }
                        } else if (userChoice == 8) {
                            // Logout
                            currentUser = null;
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password.");
                }
            } else if (choice == 2) {
                // Exit
                break;
            }
        }
    }

    private static double calculateBMI(double weight, double height) {
        return weight / (height * height);
    }

    private static double calculateAverageSleepDuration(List<SleepData> sleepDataList) {
        double sum = 0;
        for (SleepData sleepData : sleepDataList) {
            sum += sleepData.getDuration();
        }
        return sum / sleepDataList.size();
    }

    private static double calculateAverageSleepQuality(List<SleepData> sleepDataList) {
        double sum = 0;
        for (SleepData sleepData : sleepDataList) {
            sum += sleepData.getQuality();
        }
        return sum / sleepDataList.size();
    }

    private static Map<String, Integer> calculateSleepStageCounts(List<SleepData> sleepDataList) {
        Map<String, Integer> stageCounts = new HashMap<>();
        for (SleepData sleepData : sleepDataList) {
            String stage = sleepData.getStage();
            stageCounts.put(stage, stageCounts.getOrDefault(stage, 0) + 1);
        }
        return stageCounts;
    }
}

class User {
    private String username;
    private String password;
    private Profile profile;
    private Map<String, WorkoutPlan> workoutPlansMap;

    public User(String username, String password, Profile profile) {
        this.username = username;
        this.password = password;
        this.profile = profile;
        this.workoutPlansMap = new HashMap<>();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Profile getProfile() {
        return profile;
    }

    public Map<String, WorkoutPlan> getWorkoutPlansMap() {
        return workoutPlansMap;
    }

    public void addWorkoutPlan(String title, WorkoutPlan workoutPlan) {
        workoutPlansMap.put(title, workoutPlan);
    }

    public boolean deleteWorkoutPlan(String title) {
        return workoutPlansMap.remove(title) != null;
    }

    public List<WorkoutPlan> searchWorkoutPlan(String keyword) {
        List<WorkoutPlan> searchResults = new ArrayList<>();
        for (WorkoutPlan workoutPlan : workoutPlansMap.values()) {
            if (workoutPlan.getTitle().contains(keyword) || workoutPlan.getExercises().contains(keyword)) {
                searchResults.add(workoutPlan);
            }
        }
        return searchResults;
    }
}

class Profile {
    private double weight;
    private double height;
    private List<SleepData> sleepDataList;

    public Profile(double weight, double height) {
        this.weight = weight;
        this.height = height;
        this.sleepDataList = new ArrayList<>();
    }

    public double getWeight() {
        return weight;
    }

    public double getHeight() {
        return height;
    }

    public List<SleepData> getSleepDataList() {
        return sleepDataList;
    }

    public void addSleepData(SleepData sleepData) {
        sleepDataList.add(sleepData);
    }

    public void resetSleepData() {
        sleepDataList.clear();
    }
}

class WorkoutPlan {
    private Date date;
    private String time;
    private List<String> exercises;
    private String title;

    public WorkoutPlan(Date date, String time, List<String> exercises, String title) {
        this.date = date;
        this.time = time;
        this.exercises = exercises;
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public List<String> getExercises() {
        return exercises;
    }

    public String getTitle() {
        return title;
    }
}

class SleepData {
    private double duration;
    private double quality;
    private String stage;

    public SleepData(double duration, double quality, String stage) {
        this.duration = duration;
        this.quality = quality;
        this.stage = stage;
    }

    public double getDuration() {
        return duration;
    }

    public double getQuality() {
        return quality;
    }

    public String getStage() {
        return stage;
    }
}
