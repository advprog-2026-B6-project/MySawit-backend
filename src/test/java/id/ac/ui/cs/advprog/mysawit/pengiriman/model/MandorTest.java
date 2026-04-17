package id.ac.ui.cs.advprog.mysawit.pengiriman.model;

import id.ac.ui.cs.advprog.mysawit.model.Role;
import id.ac.ui.cs.advprog.mysawit.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MandorTest {

    @Test
    void testMandorIsUserWithMandorRole() {
        User mandor = new User("Ahmad Bin Ali", "ahmad", "secret", Role.MANDOR, "CERT-001");

        assertEquals(Role.MANDOR, mandor.getRole());
        assertEquals("Ahmad Bin Ali", mandor.getFullname());
        assertEquals("ahmad", mandor.getUsername());
        assertEquals("CERT-001", mandor.getCertificationNumber());
    }

    @Test
    void testMandorRoleIsDistinctFromOtherRoles() {
        User mandor = new User("Ahmad", "ahmad", "secret", Role.MANDOR, "CERT-001");
        User buruh  = new User("Budi",  "budi",  "secret", Role.BURUH,  null);
        User supir  = new User("Cici",  "cici",  "secret", Role.SUPIR,  null);
        User admin  = new User("Dodi",  "dodi",  "secret", Role.ADMIN,  null);

        assertTrue(mandor.getRole() == Role.MANDOR);
        assertFalse(buruh.getRole()  == Role.MANDOR);
        assertFalse(supir.getRole()  == Role.MANDOR);
        assertFalse(admin.getRole()  == Role.MANDOR);
    }

    @Test
    void testMandorSettersAndGetters() {
        User mandor = new User();
        mandor.setFullname("Eko Wahyudi");
        mandor.setUsername("eko");
        mandor.setPassword("password");
        mandor.setRole(Role.MANDOR);
        mandor.setCertificationNumber("CERT-999");

        assertEquals("Eko Wahyudi", mandor.getFullname());
        assertEquals("eko", mandor.getUsername());
        assertEquals(Role.MANDOR, mandor.getRole());
        assertEquals("CERT-999", mandor.getCertificationNumber());
    }

    @Test
    void testMandorRoleEnumValue() {
        //memastikan role mandro ada
        assertEquals("MANDOR", Role.MANDOR.name());
    }

    @Test
    void testMandorWithNoCertificationNumber() {
        User mandor = new User("Fani", "fani", "pass", Role.MANDOR, null);

        assertEquals(Role.MANDOR, mandor.getRole());
        assertNull(mandor.getCertificationNumber());
    }
}
