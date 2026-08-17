import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WardTest {

    @Test
    void wardHasTwentyBedsInFourByFiveLayout() {
        Ward ward = new Ward();
        assertEquals(20, ward.getTotalBeds());
        assertEquals(4, Ward.ROWS);
        assertEquals(5, Ward.COLS);
    }

    @Test
    void bedNumbersFollowB01ToB20Format() {
        Ward ward = new Ward();
        assertNotNull(ward.findBed("B01"));
        assertNotNull(ward.findBed("B20"));
        assertNull(ward.findBed("B21"));
    }



    @Test
    void allNewBedsStartAvailable() {
        Ward ward = new Ward();
        assertEquals(20, ward.getAvailableBeds().size());
        assertEquals(0, ward.getOccupiedBeds().size());
    }
}
