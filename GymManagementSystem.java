import java.util.*;
import java.io.*;
import java.time.LocalDate;

class Member {
    int id;
    String name;
    int age;
    double height, weight, bmi;
    String goal;
    boolean feePaid = false;

    Trainer assignedTrainer = null;
    double totalCost = 0.0;

    String membershipType;
    double membershipPrice;
    LocalDate joinDate;
    LocalDate expiryDate;

    Member(int id, String name, int age, double height, double weight) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.bmi = weight / (height * height);
        this.goal = getBMICategory(bmi);
    }

    public void setMembership(String type, int monthsToAdd, double price) {
        this.membershipType = type;
        this.membershipPrice = price;
        this.totalCost += price;
        this.joinDate = LocalDate.now();
        this.expiryDate = this.joinDate.plusMonths(monthsToAdd);
        this.feePaid = true;
    }

    public void assignTrainer(Trainer trainer) {
        this.assignedTrainer = trainer;
        this.totalCost += trainer.fee;
        trainer.addClient(this);
    }

    static String getBMICategory(double bmi) {
        if (bmi < 18.5)
            return "Weight Gain";
        else if (bmi < 24.9)
            return "Maintain";
        else
            return "Weight Loss";
    }
}

class Trainer {
    int id;
    String name;
    String specialization;
    String type;
    double fee;

    ArrayList<Member> clients = new ArrayList<>();

    Trainer(int id, String name, String specialization, String type, double fee) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.type = type;
        this.fee = fee;
    }

    public void addClient(Member m) {
        this.clients.add(m);
    }
}

public class GymManagementSystem {

    static Scanner sc = new Scanner(System.in);
    static ArrayList<Member> members = new ArrayList<>();
    static ArrayList<Trainer> trainers = new ArrayList<>();

    static String[] weeklyLog = new String[7];
    static String[] muscles = { "chest", "back", "legs", "shoulders", "arms" };

    static double previousWeight = 0;
    static final String FILE = "progress.txt";

    static {
        trainers.add(new Trainer(101, "Ramesh", "Bodybuilding", "General", 500.00));
        trainers.add(new Trainer(102, "Suresh", "Weight Loss", "General", 500.00));
        trainers.add(new Trainer(103, "Vijay", "Strength & Conditioning", "Personal", 2500.00));
        trainers.add(new Trainer(104, "Priya", "Yoga & Flexibility", "Personal", 2000.00));
    }

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n----------------------------------------");
            System.out.println("          GYM MANAGEMENT SYSTEM         ");
            System.out.println("----------------------------------------");

            System.out.println("\nSelect your role to continue:");
            System.out.println("1. Trainer Portal");
            System.out.println("2. Client Portal");
            System.out.println("3. Exit System");
            System.out.println();

            int roleChoice = getInt("Enter choice (1-3): ");

            switch (roleChoice) {
                case 1:
                    trainerPortal();
                    break;
                case 2:
                    clientPortal();
                    break;
                case 3: {
                    System.out.println("\nExiting System. Goodbye!\n");
                    System.exit(0);
                    break;
                }
                default:
                    System.out.println("\n[!] Invalid choice! Please select 1-3.");
                    break;
            }
        }
    }

    static void trainerPortal() {
        System.out.println("\n--- TRAINER LOGIN ---\n");
        int tId = getInt("Enter your Trainer ID: ");

        Trainer currentTrainer = null;
        for (Trainer t : trainers) {
            if (t.id == tId) {
                currentTrainer = t;
                break;
            }
        }

        if (currentTrainer == null) {
            System.out.println("[!] Invalid Trainer ID. Returning to main menu.");
            return;
        }

        System.out.println("\nWelcome back, Trainer " + currentTrainer.name + " (" + currentTrainer.type + ")!");

        while (true) {
            System.out.println("\n-----------------------------------------");
            System.out.println("          TRAINER DASHBOARD              ");
            System.out.println("-----------------------------------------");
            System.out.println("1. View Assigned Clients");
            System.out.println("2. Monitor Client Workouts");
            System.out.println("3. Logout to Main Menu");
            System.out.println();

            int choice = getInt("Enter choice (1-3): ");

            switch (choice) {
                case 1: {
                    System.out.println("\n--- ASSIGNED CLIENTS ---");
                    if (currentTrainer.clients.isEmpty()) {
                        System.out.println("You have no clients assigned right now.");
                    } else {
                        for (Member c : currentTrainer.clients) {
                            System.out.printf("ID: %d | Name: %s | BMI: %.2f | Goal: %s\n", c.id, c.name, c.bmi,
                                    c.goal);
                        }
                    }
                    break;
                }
                case 2: {
                    System.out.println("\n--- MONITOR CLIENT ---");
                    String clientName = getString("Enter the exact name of the client to view progress: ");
                    viewReport(clientName);
                    break;
                }
                case 3: {
                    System.out.println("\nLogging out...");
                    return;
                }
                default:
                    System.out.println("\n[!] Invalid choice!");
                    break;
            }
        }
    }

    static void clientPortal() {
        System.out.println("\n--- CLIENT PORTAL ---");
        System.out.println("1. Login (Existing Member)");
        System.out.println("2. Register (New Member)");
        System.out.println("3. Go Back");
        int clientChoice = getInt("Enter choice (1-3): ");

        Member currentMember = null;

        if (clientChoice == 1) {
            String name = getString("Enter Your Name: ");
            for (Member m : members) {
                if (m.name.equalsIgnoreCase(name)) {
                    currentMember = m;
                    break;
                }
            }
            if (currentMember == null) {
                System.out.println("\n[!] Member not found! Please register first.");
                return;
            }
        } else if (clientChoice == 2) {
            String name = getString("\nEnter Name: ");
            int age = getInt("Enter Age: ");
            double weight = getDouble("Enter Weight (kg): ");
            double height = getDouble("Enter Height (m): ");

            previousWeight = weight;

            currentMember = new Member(members.size() + 1, name, age, height, weight);
            members.add(currentMember);

            System.out.println("\n-----------------------------------------");
            System.out.printf("BMI: %.2f | Goal: %s\n", currentMember.bmi, currentMember.goal);
            System.out.println("-----------------------------------------");

            System.out.println("\n----------------------------------------");
            System.out.println("             MEMBERSHIP PLANS            ");
            System.out.println("-----------------------------------------");
            System.out.println("\n1. Monthly   (1 Month)   - RS/-1500.00");
            System.out.println("\n2. Quarterly (3 Months)  - Rs/-4000.00");
            System.out.println("\n3. Yearly    (12 Months) - Rs/-12000.00");
            System.out.println();
            int memChoice = getInt("Select Membership Type (1-3): ");

            switch (memChoice) {
                case 1:
                    currentMember.setMembership("Monthly", 1, 1500.00);
                    break;
                case 2:
                    currentMember.setMembership("Quarterly", 3, 4000.00);
                    break;
                case 3:
                    currentMember.setMembership("Yearly", 12, 12000.00);
                    break;
                default: {
                    System.out.println("\n[!] Invalid selection. Defaulting to Monthly.");
                    currentMember.setMembership("Monthly", 1, 1500.00);
                    break;
                }
            }

            System.out.println("\n-----------------------------------------");
            System.out.println("             TRAINER SELECTION           ");
            System.out.println("-----------------------------------------");
            System.out.println("Available Trainers:");

            for (int i = 0; i < trainers.size(); i++) {
                Trainer t = trainers.get(i);
                System.out.printf("%d. %s - %s Trainer (%s) | Fee: Rs/-%.2f\n",
                        i + 1, t.name, t.type, t.specialization, t.fee);
            }
            System.out.println("0. Skip Trainer Assignment");
            System.out.println();

            int trainerChoice = getInt("Select a Trainer (or 0 to skip): ");
            if (trainerChoice > 0 && trainerChoice <= trainers.size()) {
                Trainer selected = trainers.get(trainerChoice - 1);
                currentMember.assignTrainer(selected);
                System.out.println("\n[+] Trainer " + selected.name + " assigned!");
            } else {
                System.out.println("\n[!] No trainer assigned.");
            }

            System.out.println("\n----------------------------------------");
            System.out.println("           MEMBERSHIP SUMMARY            ");
            System.out.println("-----------------------------------------");
            System.out.println("Status:    Active (" + currentMember.membershipType + ")");
            System.out.println("Joined:    " + currentMember.joinDate);
            System.out.println("Expires:   " + currentMember.expiryDate);
            if (currentMember.assignedTrainer != null) {
                System.out.println("Trainer:   " + currentMember.assignedTrainer.name + " ("
                        + currentMember.assignedTrainer.type + ")");
            } else {
                System.out.println("Trainer:   None");
            }
            System.out.println("Total Fee: ₹" + String.format("%.2f", currentMember.totalCost));
            System.out.println();

        } else {
            return;
        }

        while (true) {
            System.out.println("\n----------------------------------------");
            System.out.println("             CLIENT DASHBOARD            ");
            System.out.println("------------------------------------------\n");
            System.out.println("\n1. View Workout Plan");
            System.out.println("\n2. Track Weekly Workout");
            System.out.println("\n3. View Progress Report");
            System.out.println("\n4. Logout to Main Menu");
            System.out.println();

            int choice = getInt("Enter choice (1-4): ");

            switch (choice) {
                case 1:
                    viewPlan();
                    break;
                case 2:
                    trackWorkout(currentMember.name);
                    break;
                case 3:
                    viewReport(currentMember.name);
                    break;
                case 4: {
                    System.out.println("\nLogging out...\n");
                    return;
                }
                default:
                    System.out.println("\n[!] Invalid choice! Please select 1-4.");
                    break;
            }
        }
    }

    static void viewPlan() {
        System.out.println("\n----------------------------------------");
        System.out.println("              WORKOUT PLANS              ");
        System.out.println("-----------------------------------------\n");
        System.out.println("\n1. Weight Loss / Fat Burn");
        System.out.println("\n2. Muscle Gain / Hypertrophy");
        System.out.println("\n3. General Fitness / Maintain");
        System.out.println();

        int choice = getInt("Select Plan Strategy (1-3): ");

        System.out.println("\n-----------------------------------------");
        switch (choice) {
            case 1:
                viewWeightLossPlan();
                break;
            case 2:
                viewMuscleGainPlan();
                break;
            case 3:
                viewGeneralFitnessPlan();
                break;
            default: {
                System.out.println("[!] Invalid choice. Displaying General Fitness Plan.\n");
                viewGeneralFitnessPlan();
                break;
            }
        }
        System.out.println("-----------------------------------------");
    }

    static void viewWeightLossPlan() {
        System.out.println("       WEIGHT LOSS PLAN (6 DAYS)         \n");
        System.out.println("Focus: High Intensity, Cardio, Compound Movements\n");
        System.out.println("Day 1: Full Body Circuit + 30m Cardio");
        System.out.println("       Goblet Squats 3x15, Push-ups 3x15, Kettlebell Swings 3x20, Plank 3x60s\n");
        System.out.println("Day 2: 45m HIIT Cardio (Treadmill/Bike)\n");
        System.out.println("Day 3: Upper Body + Core");
        System.out.println("       Dumbbell Press 3x15, Lat Pulldown 3x15, Mountain Climbers 3x30, Crunches 3x20\n");
        System.out.println("Day 4: Active Recovery / Yoga\n");
        System.out.println("Day 5: Lower Body + 30m Cardio");
        System.out.println("       Lunges 3x15/leg, Leg Press 3x15, Calf Raises 3x20, Jumping Jacks 3x40\n");
        System.out.println("Day 6: Full Body HIIT");
        System.out.println("       Burpees 3x10, Box Jumps 3x12, Battle Ropes 3x30s, Russian Twists 3x20\n");
        System.out.println("Day 7: Rest");
    }

    static void viewMuscleGainPlan() {
        System.out.println("       MUSCLE GAIN PLAN (6 DAYS - PPL)   \n");
        System.out.println("Focus: Progressive Overload, Heavy Weights, Hypertrophy\n");
        System.out.println("Day 1: Push (Chest, Shoulders, Triceps)");
        System.out
                .println("       Bench Press 4x8, Overhead Press 3x10, Incline DB Press 3x10, Triceps Pushdown 3x12\n");
        System.out.println("Day 2: Pull (Back, Biceps)");
        System.out.println("       Deadlift 4x6, Pull-ups 3x8, Barbell Row 3x10, Barbell Curls 3x10\n");
        System.out.println("Day 3: Legs (Quads, Hamstrings, Calves)");
        System.out.println("       Squats 4x8, Romanian Deadlift 3x10, Leg Press 3x12, Calf Raises 4x15\n");
        System.out.println("Day 4: Push (Chest, Shoulders, Triceps)");
        System.out.println("       DB Bench Press 4x10, Lateral Raises 4x12, Pec Deck Fly 3x12, Skull Crushers 3x10\n");
        System.out.println("Day 5: Pull (Back, Biceps)");
        System.out.println("       Lat Pulldown 4x10, Seated Cable Row 3x12, Face Pulls 3x15, Hammer Curls 3x12\n");
        System.out.println("Day 6: Legs & Core");
        System.out.println("       Hack Squat 3x10, Leg Extensions 3x15, Leg Curls 3x15, Cable Crunches 3x20\n");
        System.out.println("Day 7: Rest");
    }

    static void viewGeneralFitnessPlan() {
        System.out.println("      GENERAL FITNESS PLAN (3-4 DAYS)    \n");
        System.out.println("Focus: Balance of Strength, Conditioning, and Mobility\n");
        System.out.println("Day 1: Full Body Strength");
        System.out.println("       Squats 3x10, Bench Press 3x10, Barbell Row 3x10, Plank 3x45s\n");
        System.out.println("Day 2: 30m Moderate Cardio + Core");
        System.out.println("       Jogging/Cycling, Crunches 3x15, Leg Raises 3x15\n");
        System.out.println("Day 3: Rest / Active Recovery\n");
        System.out.println("Day 4: Full Body Strength");
        System.out.println("       Deadlift 3x8, Overhead Press 3x10, Lat Pulldown 3x12, Bicep Curls 3x12\n");
        System.out.println("Day 5: 30m Moderate Cardio + Mobility");
        System.out.println("       Rowing machine, Stretching routines\n");
        System.out.println("Day 6: Bodyweight / Functional");
        System.out.println("       Push-ups 3xMax, Pull-ups 3xMax, Lunges 3x12, Kettlebell Swings 3x15\n");
        System.out.println("Day 7: Rest");
    }

    static void trackWorkout(String name) {
        System.out.println("\n----------------------------------------");
        System.out.println("             TRACK WORKOUT               ");
        System.out.println("------------------------------------------\n");

        sc.nextLine();

        for (int i = 0; i < 7; i++) {
            System.out.print("Day " + (i + 1) + ": ");
            weeklyLog[i] = sc.nextLine().toLowerCase();
        }

        saveToFile(name);
    }

    static void saveToFile(String name) {
        try {
            FileWriter fw = new FileWriter(FILE, true);

            fw.write("User:" + name + "\n");
            for (int i = 0; i < 7; i++) {
                fw.write("Day " + (i + 1) + ": " + weeklyLog[i] + "\n");
            }
            fw.write("----------------\n");

            fw.close();
            System.out.println("\n[+] Weekly log saved successfully!");
        } catch (Exception e) {
            System.out.println("\n[-] Error saving log!");
        }
    }

    static void viewReport(String name) {
        System.out.println("\n----------------------------------------");
        System.out.println("                PROGRESS REPORT          ");
        System.out.println("------------------------------------------\n");

        String[] repLog = new String[7];

        try {
            Scanner fileScanner = new Scanner(new File(FILE));
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (line.equalsIgnoreCase("User:" + name)) {

                    for (int i = 0; i < 7; i++) {
                        if (fileScanner.hasNextLine()) {
                            String dayLine = fileScanner.nextLine();
                            int colonIdx = dayLine.indexOf(":");
                            if (colonIdx != -1) {
                                repLog[i] = dayLine.substring(colonIdx + 1).trim();
                            }
                        }
                    }
                }
            }
            fileScanner.close();
        } catch (Exception e) {

        }

        boolean hasData = false;
        for (String dl : repLog) {
            if (dl != null && !dl.isEmpty()) {
                hasData = true;
                break;
            }
        }
        if (!hasData) {
            System.arraycopy(weeklyLog, 0, repLog, 0, 7);
        }

        HashSet<String> trained = new HashSet<>();

        for (String day : repLog) {

            if (day != null && !day.trim().isEmpty() && !day.equals("rest")) {
                trained.add(day);
            }
        }

        System.out.println("\n---> Trained Muscles/Workouts:");
        if (trained.isEmpty()) {
            System.out.println("     None recorded yet.");
        } else {
            for (String m : trained) {
                System.out.println("     - " + m);
            }
        }

        System.out.println("\n---> Missed Core Muscles:");
        boolean missedAny = false;
        for (String m : muscles) {
            boolean found = false;
            for (String logged : trained) {
                if (logged.contains(m)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("     - " + m);
                missedAny = true;
            }
        }
        if (!missedAny) {
            System.out.println("     None! Great job hitting all core muscles.");
        }

        compareProgress();
        System.out.println("----------------------------------------");
    }

    static void compareProgress() {
        System.out.println("\n-----------------------------------------");
        double newWeight = getDouble("Enter current weight (kg): ");
        System.out.println();

        if (newWeight < previousWeight)
            System.out.println("[RESULT]: Lost " + String.format("%.2f", previousWeight - newWeight) + " kg!!");
        else if (newWeight > previousWeight)
            System.out.println("[RESULT]: Gained " + String.format("%.2f", newWeight - previousWeight) + " kg");
        else
            System.out.println("[RESULT]: No change in weight.");

        previousWeight = newWeight;
    }

    static int getInt(String msg) {
        while (true) {
            try {
                System.out.print(msg);
                return sc.nextInt();
            } catch (Exception e) {
                System.out.println("[!] Invalid input! Please enter a number.");
                sc.next();
            }
        }
    }

    static double getDouble(String msg) {
        while (true) {
            try {
                System.out.print(msg);
                return sc.nextDouble();
            } catch (Exception e) {
                System.out.println("[!] Invalid input! Please enter a number.");
                sc.next();
            }
        }
    }

    static String getString(String msg) {
        System.out.print(msg);
        return sc.next();
    }
}
