import java.util.ArrayList;
import java.util.List;

public class Ward {

    public static final int ROWS = 4;
    public static final int COLS = 5;
    public static final int TOTAL_BEDS = ROWS * COLS;

    private final List<Bed> beds;

    public Ward() {
        beds = new ArrayList<>();
        for (int i = 1; i <= TOTAL_BEDS; i++) {
            beds.add(new Bed(String.format("B%02d", i)));
        }
    }

    public Bed findBed(String bedNumber) {
        if (bedNumber == null) return null;
        for (Bed bed : beds) {
            if (bed.getBedNumber().equalsIgnoreCase(bedNumber)) {
                return bed;
            }
        }
        return null;
    }

    /**
     * Allocate a specific bed number to a patient.
     * @return true if allocation succeeded, false if the bed does not
     *         exist or is already occupied.
     */
    public boolean allocateBed(String bedNumber, String patientId) {
        Bed bed = findBed(bedNumber);
        if (bed == null || bed.isOccupied()) {
            return false;
        }
        bed.occupy(patientId);
        return true;
    }

    /**
     * Allocate the first free bed found, in B01..B20 order.
     * @return the bed number allocated, or null if the ward is full.
     */
    public String allocateNextAvailableBed(String patientId) {
        for (Bed bed : beds) {
            if (!bed.isOccupied()) {
                bed.occupy(patientId);
                return bed.getBedNumber();
            }
        }
        return null;
    }

    public boolean releaseBed(String bedNumber) {
        Bed bed = findBed(bedNumber);
        if (bed == null || !bed.isOccupied()) {
            return false;
        }
        bed.release();
        return true;
    }

    public boolean releaseBedByPatient(String patientId) {
        for (Bed bed : beds) {
            if (bed.isOccupied() && bed.getPatientId().equals(patientId)) {
                bed.release();
                return true;
            }
        }
        return false;
    }

    public List<Bed> getAvailableBeds() {
        List<Bed> available = new ArrayList<>();
        for (Bed bed : beds) {
            if (!bed.isOccupied()) available.add(bed);
        }
        return available;
    }

    public List<Bed> getOccupiedBeds() {
        List<Bed> occupied = new ArrayList<>();
        for (Bed bed : beds) {
            if (bed.isOccupied()) occupied.add(bed);
        }
        return occupied;
    }

    public boolean hasAvailableBed() {
        return !getAvailableBeds().isEmpty();
    }

    public int getTotalBeds() {
        return TOTAL_BEDS;
    }

    public void displayLayout() {
        System.out.println("\n===== WARD LAYOUT (4x5) =====");
        for (int r = 0; r < ROWS; r++) {
            StringBuilder sb = new StringBuilder();
            for (int c = 0; c < COLS; c++) {
                sb.append(beds.get(r * COLS + c).toString()).append("  ");
            }
            System.out.println(sb.toString());
        }
        System.out.println("[ ] = Available   [X] = Occupied");
    }
}
