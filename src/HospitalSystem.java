import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HospitalSystem {

    private final Map<String, Patient> patients;
    private final Ward ward;

    public HospitalSystem() {
        patients = new LinkedHashMap<>();
        ward = new Ward();
    }

    public Ward getWard() {
        return ward;
    }

    public boolean registerPatient(Patient patient) {
        if (patient == null || patient.getPatientId() == null) return false;
        if (patients.containsKey(patient.getPatientId())) {
            return false;
        }
        patients.put(patient.getPatientId(), patient);
        return true;
    }

    public Patient searchPatient(String patientId) {
        return patients.get(patientId);
    }

    public boolean updatePatient(String patientId, String firstName, String lastName,
                                 int age, String gender, String medicalCondition) {
        Patient p = patients.get(patientId);
        if (p == null) return false;
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setAge(age);
        p.setGender(gender);
        p.setMedicalCondition(medicalCondition);
        return true;
    }

    public boolean deletePatient(String patientId) {
        Patient p = patients.get(patientId);
        if (p == null) return false;
        if (p instanceof Inpatient) {
            ward.releaseBedByPatient(patientId);
        }
        patients.remove(patientId);
        return true;
    }

    public List<Patient> getAllPatients() {
        return new ArrayList<>(patients.values());
    }

    public List<Patient> sortPatientsBySurname() {
        List<Patient> list = getAllPatients();
        list.sort(Comparator.comparing(Patient::getLastName, String.CASE_INSENSITIVE_ORDER));
        return list;
    }

    public List<Patient> sortPatientsById() {
        List<Patient> list = getAllPatients();
        list.sort(Comparator.comparing(Patient::getPatientId));
        return list;
    }


    public String allocateBed(String patientId, String bedNumber) {
        Patient p = patients.get(patientId);
        if (p == null) {
            return "Patient not found.";
        }
        if (!(p instanceof Inpatient)) {
            return "Only inpatients may be allocated a bed.";
        }
        Inpatient inpatient = (Inpatient) p;
        if (inpatient.getBedNumber() != null) {
            return "Patient already has a bed allocated: " + inpatient.getBedNumber();
        }
        if (!ward.hasAvailableBed()) {
            return "No beds available.";
        }

        if (bedNumber == null || bedNumber.isBlank()) {
            String assigned = ward.allocateNextAvailableBed(patientId);
            if (assigned == null) {
                return "No beds available.";
            }
            inpatient.setBedNumber(assigned);
            return "Bed allocated successfully: " + assigned;
        }

        boolean success = ward.allocateBed(bedNumber, patientId);
        if (!success) {
            return "Bed " + bedNumber + " is unavailable or does not exist.";
        }
        inpatient.setBedNumber(bedNumber.toUpperCase());
        return "Bed allocated successfully: " + inpatient.getBedNumber();
    }

    public String releaseBed(String patientId) {
        Patient p = patients.get(patientId);
        if (p == null) {
            return "Patient not found.";
        }
        if (!(p instanceof Inpatient)) {
            return "Patient is not an inpatient.";
        }
        Inpatient inpatient = (Inpatient) p;
        if (inpatient.getBedNumber() == null) {
            return "Patient does not currently occupy a bed.";
        }
        ward.releaseBed(inpatient.getBedNumber());
        inpatient.setBedNumber(null);
        return "Bed released successfully.";
    }


    public int getTotalPatients() {
        return patients.size();
    }

    public int getTotalOccupiedBeds() {
        return ward.getOccupiedBeds().size();
    }

    public double getOccupancyPercentage() {
        return (getTotalOccupiedBeds() * 100.0) / ward.getTotalBeds();
    }
}