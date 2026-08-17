import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HospitalSystemTest {

    private HospitalSystem system;

    @BeforeEach
    void setUp() {
        system = new HospitalSystem();
    }

    private Patient outpatient(String id, String first, String last) {
        return new Patient(id, first, last, 30, "Female", "Flu", PatientCategory.OUTPATIENT);
    }

    private Inpatient inpatient(String id, String first, String last) {
        return new Inpatient(id, first, last, 45, "Male", "Fracture", "W1");
    }


    @Test
    void registerPatient_addsNewPatient() {
        assertTrue(system.registerPatient(outpatient("P001", "Jane", "Doe")));
        assertEquals(1, system.getTotalPatients());
    }

    @Test
    void registerPatient_rejectsDuplicatePatientId() {
        system.registerPatient(outpatient("P001", "Jane", "Doe"));
        boolean secondAttempt = system.registerPatient(outpatient("P001", "John", "Smith"));

        assertFalse(secondAttempt);
        assertEquals(1, system.getTotalPatients());
    }

    @Test
    void searchPatient_findsRegisteredPatient() {
        system.registerPatient(outpatient("P002", "Alice", "Jones"));

        Patient found = system.searchPatient("P002");

        assertNotNull(found);
        assertEquals("Alice", found.getFirstName());
    }

    @Test
    void searchPatient_returnsNullForUnknownId() {
        assertNull(system.searchPatient("DOES_NOT_EXIST"));
    }

    @Test
    void updatePatient_changesDetails() {
        system.registerPatient(outpatient("P003", "Bob", "Brown"));

        boolean updated = system.updatePatient("P003", "Robert", "Brown", 50, "Male", "Diabetes");

        assertTrue(updated);
        Patient p = system.searchPatient("P003");
        assertEquals("Robert", p.getFirstName());
        assertEquals(50, p.getAge());
        assertEquals("Diabetes", p.getMedicalCondition());
    }

    @Test
    void updatePatient_returnsFalseForUnknownId() {
        assertFalse(system.updatePatient("NOPE", "A", "B", 1, "M", "C"));
    }

    @Test
    void deletePatient_removesPatientAndReleasesBed() {
        Inpatient patient = inpatient("P004", "Carl", "White");
        system.registerPatient(patient);
        system.allocateBed("P004", "B01");

        boolean deleted = system.deletePatient("P004");

        assertTrue(deleted);
        assertNull(system.searchPatient("P004"));
        assertFalse(system.getWard().findBed("B01").isOccupied());
    }

    @Test
    void deletePatient_returnsFalseForUnknownId() {
        assertFalse(system.deletePatient("NOPE"));
    }


    @Test
    void allocateBed_succeedsForInpatient() {
        system.registerPatient(inpatient("P005", "Dana", "Green"));

        String result = system.allocateBed("P005", "B02");

        assertTrue(result.toLowerCase().contains("successfully"));
        assertTrue(system.getWard().findBed("B02").isOccupied());
        Inpatient p = (Inpatient) system.searchPatient("P005");
        assertEquals("B02", p.getBedNumber());
    }

    @Test
    void allocateBed_rejectsNonInpatientCategory() {
        system.registerPatient(outpatient("P006", "Eve", "Black"));

        String result = system.allocateBed("P006", "B03");

        assertFalse(result.toLowerCase().contains("successfully"));
    }

    @Test
    void allocateBed_rejectsAlreadyOccupiedBed() {
        system.registerPatient(inpatient("P007", "Frank", "Grey"));
        system.registerPatient(inpatient("P008", "Grace", "Blue"));
        system.allocateBed("P007", "B04");

        String result = system.allocateBed("P008", "B04");

        assertTrue(result.toLowerCase().contains("unavailable"));
        assertNull(((Inpatient) system.searchPatient("P008")).getBedNumber());
    }

    @Test
    void allocateBed_failsWhenAllTwentyBedsOccupied() {
        for (int i = 1; i <= 20; i++) {
            String id = "F" + i;
            system.registerPatient(inpatient(id, "Test", "Patient" + i));
            system.allocateBed(id, null); // auto-assign
        }
        assertEquals(20, system.getTotalOccupiedBeds());

        system.registerPatient(inpatient("P021", "Extra", "Patient"));
        String result = system.allocateBed("P021", null);

        assertTrue(result.toLowerCase().contains("no beds available"));
    }

    @Test
    void releaseBed_freesTheBed() {
        system.registerPatient(inpatient("P009", "Holly", "Pink"));
        system.allocateBed("P009", "B05");

        String result = system.releaseBed("P009");

        assertTrue(result.toLowerCase().contains("successfully"));
        assertFalse(system.getWard().findBed("B05").isOccupied());
        assertNull(((Inpatient) system.searchPatient("P009")).getBedNumber());
    }

    @Test
    void releaseBed_onPatientWithNoBedReturnsMessageNotException() {
        system.registerPatient(inpatient("P010", "Ivy", "Orange"));

        String result = system.releaseBed("P010");

        assertTrue(result.toLowerCase().contains("does not currently occupy"));
    }


    @Test
    void sortPatientsBySurname_ordersAlphabetically() {
        system.registerPatient(outpatient("S1", "Zack", "Zephyr"));
        system.registerPatient(outpatient("S2", "Amy", "Apple"));
        system.registerPatient(outpatient("S3", "Mid", "Mango"));

        List<Patient> sorted = system.sortPatientsBySurname();

        assertEquals("Apple", sorted.get(0).getLastName());
        assertEquals("Mango", sorted.get(1).getLastName());
        assertEquals("Zephyr", sorted.get(2).getLastName());
    }

    @Test
    void sortPatientsById_ordersById() {
        system.registerPatient(outpatient("P010", "A", "A"));
        system.registerPatient(outpatient("P002", "B", "B"));
        system.registerPatient(outpatient("P005", "C", "C"));

        List<Patient> sorted = system.sortPatientsById();

        assertEquals("P002", sorted.get(0).getPatientId());
        assertEquals("P005", sorted.get(1).getPatientId());
        assertEquals("P010", sorted.get(2).getPatientId());
    }
}
