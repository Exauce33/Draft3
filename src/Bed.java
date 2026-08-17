public class Bed {

    private final String bedNumber;
    private boolean occupied;
    private String patientId;

    public Bed(String bedNumber) {
        this.bedNumber = bedNumber;
        this.occupied = false;
        this.patientId = null;
    }

    public String getBedNumber() {
        return bedNumber;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public String getPatientId() {
        return patientId;
    }

    public void occupy(String patientId) {
        this.occupied = true;
        this.patientId = patientId;
    }

    public void release() {
        this.occupied = false;
        this.patientId = null;
    }

    @Override
    public String toString() {
        return occupied ? bedNumber + "[X]" : bedNumber + "[ ]";
    }
}
