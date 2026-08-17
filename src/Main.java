import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final HospitalSystem system = new HospitalSystem();

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" MediCare Hospital - Patient Admission System");
        System.out.println("=================================================");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1: registerPatient(); break;
                case 2: searchPatient(); break;
                case 3: updatePatient(); break;
                case 4: deletePatient(); break;
                case 5: displayAllPatients(); break;
                case 6: allocateBed(); break;
                case 7: releaseBed(); break;
                case 8: system.getWard().displayLayout(); break;
                case 9: displayBeds(true); break;
                case 10: displayBeds(false); break;
                case 11: displayReports(); break;
                case 12: displaySorted(true); break;
                case 13: displaySorted(false); break;
                case 0: running = false; System.out.println("Goodbye."); break;
                default: System.out.println("Invalid choice, please try again.");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n----------------- MAIN MENU -----------------");
        System.out.println(" 1.  Register new patient");
        System.out.println(" 2.  Search patient by ID");
        System.out.println(" 3.  Update patient details");
        System.out.println(" 4.  Delete patient");
        System.out.println(" 5.  Display all patients");
        System.out.println(" 6.  Allocate bed to inpatient");
        System.out.println(" 7.  Release bed");
        System.out.println(" 8.  Display ward layout");
        System.out.println(" 9.  Display available beds");
        System.out.println("10.  Display occupied beds");
        System.out.println("11.  Display reports summary");
        System.out.println("12.  Display patients sorted by surname");
        System.out.println("13.  Display patients sorted by Patient ID");
        System.out.println(" 0.  Exit");
        System.out.println("----------------------------------------------");
    }


    private static void registerPatient() {
        System.out.println("\n-- Register New Patient --");
        String id = readNonEmpty("Patient ID: ");
        if (system.searchPatient(id) != null) {
            System.out.println("A patient with ID " + id + " already exists.");
            return;
        }
        String firstName = readNonEmpty("First Name: ");
        String lastName = readNonEmpty("Last Name: ");
        int age = readInt("Age: ");
        String gender = readNonEmpty("Gender: ");
        String condition = readNonEmpty("Medical Condition: ");
        PatientCategory category = readCategory();

        Patient patient;
        if (category == PatientCategory.INPATIENT) {
            String wardNumber = readNonEmpty("Ward Number: ");
            patient = new Inpatient(id, firstName, lastName, age, gender, condition, wardNumber);
        } else {
            patient = new Patient(id, firstName, lastName, age, gender, condition, category);
        }

        boolean ok = system.registerPatient(patient);
        System.out.println(ok ? "Patient registered successfully."
                : "Registration failed: duplicate Patient ID.");
    }

    private static void searchPatient() {
        String id = readNonEmpty("Enter Patient ID: ");
        Patient p = system.searchPatient(id);
        System.out.println(p == null ? "No patient found with ID " + id : p.displayDetails());
    }

    private static void updatePatient() {
        String id = readNonEmpty("Enter Patient ID to update: ");
        Patient p = system.searchPatient(id);
        if (p == null) {
            System.out.println("No patient found with ID " + id);
            return;
        }
        System.out.println("Current details: " + p.displayDetails());
        String firstName = readNonEmpty("New First Name: ");
        String lastName = readNonEmpty("New Last Name: ");
        int age = readInt("New Age: ");
        String gender = readNonEmpty("New Gender: ");
        String condition = readNonEmpty("New Medical Condition: ");
        system.updatePatient(id, firstName, lastName, age, gender, condition);
        System.out.println("Patient updated successfully.");
    }

    private static void deletePatient() {
        String id = readNonEmpty("Enter Patient ID to delete: ");
        boolean ok = system.deletePatient(id);
        System.out.println(ok ? "Patient deleted (bed released automatically if applicable)."
                : "No patient found with ID " + id);
    }

    private static void displayAllPatients() {
        List<Patient> all = system.getAllPatients();
        if (all.isEmpty()) {
            System.out.println("No patients registered.");
            return;
        }
        System.out.println("\n-- All Registered Patients --");
        for (Patient p : all) {
            System.out.println(p.displayDetails());
        }
    }

    private static void displaySorted(boolean bySurname) {
        List<Patient> list = bySurname ? system.sortPatientsBySurname() : system.sortPatientsById();
        if (list.isEmpty()) {
            System.out.println("No patients registered.");
            return;
        }
        System.out.println(bySurname ? "\n-- Patients sorted by surname --"
                : "\n-- Patients sorted by Patient ID --");
        for (Patient p : list) {
            System.out.println(p.displayDetails());
        }
    }



    private static void allocateBed() {
        String id = readNonEmpty("Enter Inpatient ID: ");
        System.out.print("Enter specific bed number (e.g. B05) or leave blank for next available: ");
        String bedNumber = scanner.nextLine().trim();
        String result = system.allocateBed(id, bedNumber.isEmpty() ? null : bedNumber);
        System.out.println(result);
    }

    private static void releaseBed() {
        String id = readNonEmpty("Enter Inpatient ID: ");
        String result = system.releaseBed(id);
        System.out.println(result);
    }

    private static void displayBeds(boolean available) {
        List<Bed> beds = available ? system.getWard().getAvailableBeds()
                : system.getWard().getOccupiedBeds();
        System.out.println(available ? "\n-- Available Beds --" : "\n-- Occupied Beds --");
        if (beds.isEmpty()) {
            System.out.println(available ? "No beds available." : "No beds currently occupied.");
            return;
        }
        for (Bed b : beds) {
            if (b.isOccupied()) {
                System.out.println(b.getBedNumber() + " - occupied by patient " + b.getPatientId());
            } else {
                System.out.println(b.getBedNumber() + " - free");
            }
        }
    }


    private static void displayReports() {
        System.out.println("\n===== WARD REPORT SUMMARY =====");
        System.out.println("Total registered patients : " + system.getTotalPatients());
        System.out.println("Total occupied beds        : " + system.getTotalOccupiedBeds());
        System.out.println("Total available beds       : " + system.getWard().getAvailableBeds().size());
        System.out.printf ("Ward occupancy              : %.1f%%%n", system.getOccupancyPercentage());
    }


    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) return line;
            System.out.println("This field cannot be empty.");
        }
    }

    private static PatientCategory readCategory() {
        while (true) {
            System.out.print("Patient Category (1=Inpatient, 2=Outpatient, 3=Emergency): ");
            String line = scanner.nextLine().trim();
            if (line.equals("1")) return PatientCategory.INPATIENT;
            if (line.equals("2")) return PatientCategory.OUTPATIENT;
            if (line.equals("3")) return PatientCategory.EMERGENCY;
            System.out.println("Invalid option, please choose 1, 2 or 3.");
        }
    }
}

