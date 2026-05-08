package id.ac.ui.cs.advprog.mysawit.auth.serviceimpl;

import id.ac.ui.cs.advprog.mysawit.auth.dto.UserDto;
import id.ac.ui.cs.advprog.mysawit.auth.model.Role;
import id.ac.ui.cs.advprog.mysawit.auth.model.User;
import id.ac.ui.cs.advprog.mysawit.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getAllUsers_mapsToDtos() {
        List<User> users = List.of(
                new User(1L, "A", "a", "p", Role.BURUH, null, null),
                new User(2L, "B", "b", "p", Role.MANDOR, "C-1", null));
        when(userRepository.findAll()).thenReturn(users);
        List<UserDto> dtos = userService.getAllUsers();
        assertEquals(2, dtos.size());
        assertEquals("a", dtos.get(0).getUsername());
        assertEquals(Role.MANDOR, dtos.get(1).getRole());
    }

    @Test
    void getUserById_found() {
        User u = new User(5L, "N", "n", "p", Role.SUPIR, null, null);
        when(userRepository.findById(5L)).thenReturn(Optional.of(u));
        Optional<UserDto> dto = userService.getUserById(5L);
        assertTrue(dto.isPresent());
        assertEquals("n", dto.get().getUsername());
    }

    @Test
    void getUserById_notFound() {
        when(userRepository.findById(9L)).thenReturn(Optional.empty());
        assertTrue(userService.getUserById(9L).isEmpty());
    }

    @Test
    void getUserByUsername_found() {
        User u = new User(3L, "U", "user", "p", Role.BURUH, null, null);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(u));
        Optional<UserDto> dto = userService.getUserByUsername("user");
        assertTrue(dto.isPresent());
        assertEquals(3L, dto.get().getId());
    }

    @Test
    void existsByUsername_delegates() {
        when(userRepository.existsByUsername("x")).thenReturn(true);
        assertTrue(userService.existsByUsername("x"));
    }

    @Test
    void deleteUserById_found_deletesAndReturnsDto() {
        User u = new User(7L, "U", "u", "p", Role.BURUH, null, null);
        when(userRepository.findById(7L)).thenReturn(Optional.of(u));
        Optional<UserDto> dto = userService.deleteUserById(7L);
        assertTrue(dto.isPresent());
        verify(userRepository).deleteById(7L);
    }

    @Test
    void deleteUserById_notFound_returnsEmpty() {
        when(userRepository.findById(11L)).thenReturn(Optional.empty());
        Optional<UserDto> dto = userService.deleteUserById(11L);
        assertTrue(dto.isEmpty());
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void assignBuruhToMandor_success() {
        User buruh = new User(1L, "Buruh", "buruh", "p", Role.BURUH, null, null);
        User mandor = new User(2L, "Mandor", "mandor", "p", Role.MANDOR, "C-1", null);
        when(userRepository.findByUsername("buruh")).thenReturn(Optional.of(buruh));
        when(userRepository.findByUsername("mandor")).thenReturn(Optional.of(mandor));

        Optional<UserDto> dto = userService.assignBuruhToMandor("buruh", "mandor");
        assertTrue(dto.isPresent());
        assertEquals("mandor", dto.get().getMandorUsername());
        verify(userRepository).save(buruh);
    }

    @Test
    void assignBuruhToMandor_missingUsers_returnsEmpty() {
        when(userRepository.findByUsername("buruh")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("mandor")).thenReturn(Optional.empty());
        assertTrue(userService.assignBuruhToMandor("buruh", "mandor").isEmpty());
    }

    @Test
    void assignBuruhToMandor_onlyBuruhMissing_returnsEmpty() {
        User mandor = new User(2L, "Mandor", "mandor", "p", Role.MANDOR, "C-1", null);
        when(userRepository.findByUsername("buruh")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("mandor")).thenReturn(Optional.of(mandor));
        assertTrue(userService.assignBuruhToMandor("buruh", "mandor").isEmpty());
    }

    @Test
    void assignBuruhToMandor_onlyMandorMissing_returnsEmpty() {
        User buruh = new User(1L, "Buruh", "buruh", "p", Role.BURUH, null, null);
        when(userRepository.findByUsername("buruh")).thenReturn(Optional.of(buruh));
        when(userRepository.findByUsername("mandor")).thenReturn(Optional.empty());
        assertTrue(userService.assignBuruhToMandor("buruh", "mandor").isEmpty());
    }

    @Test
    void assignBuruhToMandor_invalidRoles_throw() {
        User notBuruh = new User(1L, "X", "x", "p", Role.ADMIN, null, null);
        User mandor = new User(2L, "M", "m", "p", Role.MANDOR, "C-1", null);
        when(userRepository.findByUsername("x")).thenReturn(Optional.of(notBuruh));
        when(userRepository.findByUsername("m")).thenReturn(Optional.of(mandor));
        assertThrows(IllegalArgumentException.class, () -> userService.assignBuruhToMandor("x", "m"));

        User buruh = new User(3L, "B", "b", "p", Role.BURUH, null, null);
        User notMandor = new User(4L, "N", "n", "p", Role.SUPIR, null, null);
        when(userRepository.findByUsername("b")).thenReturn(Optional.of(buruh));
        when(userRepository.findByUsername("n")).thenReturn(Optional.of(notMandor));
        assertThrows(IllegalArgumentException.class, () -> userService.assignBuruhToMandor("b", "n"));
    }

    @Test
    void assignBuruhToMandor_alreadyAssigned_throws() {
        User buruh = new User(1L, "B", "b", "p", Role.BURUH, null, "mandor1");
        User mandor = new User(2L, "M", "m", "p", Role.MANDOR, "C-1", null);
        when(userRepository.findByUsername("b")).thenReturn(Optional.of(buruh));
        when(userRepository.findByUsername("m")).thenReturn(Optional.of(mandor));
        assertThrows(IllegalStateException.class, () -> userService.assignBuruhToMandor("b", "m"));
    }

    @Test
    void assignBuruhToMandor_blankExistingMandor_allowsAssignment() {
        User buruh = new User(1L, "B", "b", "p", Role.BURUH, null, "   ");
        User mandor = new User(2L, "M", "m", "p", Role.MANDOR, "C-1", null);
        when(userRepository.findByUsername("b")).thenReturn(Optional.of(buruh));
        when(userRepository.findByUsername("m")).thenReturn(Optional.of(mandor));
        Optional<UserDto> dto = userService.assignBuruhToMandor("b", "m");
        assertTrue(dto.isPresent());
        assertEquals("m", dto.get().getMandorUsername());
        verify(userRepository).save(buruh);
    }

    @Test
    void reassignBuruh_success() {
        User buruh = new User(1L, "B", "b", "p", Role.BURUH, null, "old");
        User newMandor = new User(2L, "M2", "m2", "p", Role.MANDOR, "C-2", null);
        when(userRepository.findByUsername("b")).thenReturn(Optional.of(buruh));
        when(userRepository.findByUsername("m2")).thenReturn(Optional.of(newMandor));
        Optional<UserDto> dto = userService.reassignBuruhToMandor("b", "m2");
        assertTrue(dto.isPresent());
        assertEquals("m2", dto.get().getMandorUsername());
        verify(userRepository).save(buruh);
    }

    @Test
    void reassignBuruh_missingUsers_returnsEmpty() {
        when(userRepository.findByUsername("b")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("m2")).thenReturn(Optional.empty());
        assertTrue(userService.reassignBuruhToMandor("b", "m2").isEmpty());
    }

    @Test
    void reassignBuruh_onlyNewMandorMissing_returnsEmpty() {
        User buruh = new User(1L, "B", "b", "p", Role.BURUH, null, null);
        when(userRepository.findByUsername("b")).thenReturn(Optional.of(buruh));
        when(userRepository.findByUsername("m2")).thenReturn(Optional.empty());
        assertTrue(userService.reassignBuruhToMandor("b", "m2").isEmpty());
    }

    @Test
    void reassignBuruh_invalidRoles_throw() {
        User notBuruh = new User(1L, "X", "x", "p", Role.ADMIN, null, null);
        User mandor = new User(2L, "M", "m", "p", Role.MANDOR, "C-1", null);
        when(userRepository.findByUsername("x")).thenReturn(Optional.of(notBuruh));
        when(userRepository.findByUsername("m")).thenReturn(Optional.of(mandor));
        assertThrows(IllegalArgumentException.class, () -> userService.reassignBuruhToMandor("x", "m"));

        User buruh = new User(3L, "B", "b", "p", Role.BURUH, null, null);
        User notMandor = new User(4L, "N", "n", "p", Role.SUPIR, null, null);
        when(userRepository.findByUsername("b")).thenReturn(Optional.of(buruh));
        when(userRepository.findByUsername("n")).thenReturn(Optional.of(notMandor));
        assertThrows(IllegalArgumentException.class, () -> userService.reassignBuruhToMandor("b", "n"));
    }
}
