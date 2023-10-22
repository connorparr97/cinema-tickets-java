import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

//this test file provides 100% coverage on the implementation i've created inside of ticketserviceimpl
public class TicketServiceImplTest {
    private TicketServiceImpl ticketService;

    @BeforeEach
    public void setup() {
        //setup before each test, create a template for what requests could look like
        ticketService = new TicketServiceImpl();
        var adults = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 4);
        var childs = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3);
        var infants = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 7);
        ticketService.requests = new TicketTypeRequest[]{adults, childs, infants};
    }

    @Test
    public void PurchaseTickets_Test() {
        ticketService.purchaseTickets(1L, ticketService.requests);
        Assertions.assertTrue(ticketService.totalPrice == 110); //from default template (4 adult, 3 child, 7 infant), total ticket price would be £110, so let's assert this
    }

    @Test
    public void InvalidPurchaseException_InvalidAccountId_Test() {
        Exception exception = Assertions.assertThrows(InvalidPurchaseException.class, () -> {
            ticketService.purchaseTickets(-1L, ticketService.requests); //pass invalid account number to check correct exception message thrown
        });
        Assertions.assertEquals("Invalid account ID", exception.getMessage());
    }

    @Test
    public void InvalidPurchaseException_NoAdultTickets_Test() {
        ticketService.requests = new TicketTypeRequest[] //create custom request for this test, as we want 0 adults to test exception
                {new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0), new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 10), new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 10)};

        Exception exception = Assertions.assertThrows(InvalidPurchaseException.class, () -> {
            ticketService.purchaseTickets(1L, ticketService.requests);
        });

        Assertions.assertEquals("There are no adult tickets selected for purchase", exception.getMessage());
    }

    @Test
    public void InvalidPurchaseException_TicketPurchaseLimit_Test() {
        ticketService.requests = new TicketTypeRequest[] //create custom requests for this test, as we want to force > 20 tickets exception check
                {new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 10), new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 10), new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 10)};

        Exception exception = Assertions.assertThrows(InvalidPurchaseException.class, () -> {
            ticketService.purchaseTickets(1L, ticketService.requests);
        });

        Assertions.assertEquals("You cannot purchase more than 20 tickets at once", exception.getMessage());
    }
}
