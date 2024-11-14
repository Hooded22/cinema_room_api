package cinema.domain.seats;

import cinema.domain.exceptions.SeatNotFoundException;
import cinema.domain.exceptions.SeatOccupiedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SeatsServiceTest {
    @Mock
    private SeatsRepository seatsRepository;

    @InjectMocks
    private SeatsService seatsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllSeats() {
        Seat seat = new Seat();
        seat.setColumn(2);
        seat.setRow(1);
        Seat seat2 = new Seat();
        seat2.setColumn(3);
        seat2.setRow(1);
        List<Seat> seats = List.of(seat, seat2);

        Mockito.when(seatsRepository.findAll()).thenReturn(seats);

        List<Seat> allSeats = seatsService.getAllSeats();

        assertEquals(seats, allSeats);
    }

    @Test
    void purchaseSeat_shouldReturnSeat_whenSuccessfullyPurchaseSeat() {
        Seat seat = new Seat();
        seat.setColumn(2);
        seat.setRow(1);

        Mockito.when(seatsRepository.findByRowAndColumn(1,2)).thenReturn(seat);
        Mockito.when(seatsRepository.findByIdAndUpdateIsOccupied(seat.getId(), true)).thenReturn(seat);

        Seat purchaseSeat = seatsService.purchaseSeat(1, 2);

        assertEquals(seat, purchaseSeat);
    }

    @Test
    void purchaseSeat_shouldThrowException_whenSeatNotFound() {
        Mockito.when(seatsRepository.findByRowAndColumn(1,2)).thenReturn(null);

        SeatNotFoundException seatNotFoundException = assertThrows(SeatNotFoundException.class, () -> seatsService.purchaseSeat(1, 2));

        Assertions.assertEquals(seatNotFoundException.getMessage(), "The number of a row or a column is out of bounds!");
    }

    @Test
    void purchaseSeat_shouldThrowException_whenSeatIsOccupied() {
        Seat seat = new Seat();
        seat.setOccupied(true);

        Mockito.when(seatsRepository.findByRowAndColumn(1,2)).thenReturn(seat);

        SeatOccupiedException seatNotFoundException = assertThrows(SeatOccupiedException.class, () -> seatsService.purchaseSeat(1, 2));

        Assertions.assertEquals(seatNotFoundException.getMessage(), "Seat is already occupied.");
    }

    @Test
    void freeSeat_shouldReturnSeat_whenSuccessfullyFreed() {
        Seat seat = new Seat();
        seat.setColumn(2);
        seat.setRow(1);

        Mockito.when(seatsRepository.findByRowAndColumn(1,2)).thenReturn(seat);
        Mockito.when(seatsRepository.findByIdAndUpdateIsOccupied(seat.getId(), false)).thenReturn(seat);
        Seat freeSeat = seatsService.freeSeat(1, 2);

        assertEquals(seat, freeSeat);
    }

    @Test
    void freeSeat_shouldThrowException_whenSeatNotFound() {
        Mockito.when(seatsRepository.findByRowAndColumn(1,2)).thenReturn(null);
        SeatNotFoundException seatNotFoundException = assertThrows(SeatNotFoundException.class, () -> seatsService.freeSeat(1, 2));

        Assertions.assertEquals(seatNotFoundException.getMessage(), "The number of a row or a column is out of bounds!");
    }

    @Test
    void getNumberOfFreeSeats() {
        Seat seat = new Seat();
        Seat seat2 = new Seat();
        Seat seat3 = new Seat();
        seat3.setOccupied(true);
        List<Seat> seats = List.of(seat, seat2, seat3);

        Mockito.when(seatsRepository.findAll()).thenReturn(seats);

        int numberOfFreeSeats = seatsService.getNumberOfFreeSeats();

        assertEquals(numberOfFreeSeats, 2);
    }
}