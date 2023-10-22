package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {
    //only mentions methods should not be public, made variables public in order to access in unit tests
    public int totalTicketAmount;
    public int totalPrice;
    public int seatsReserved;
    public TicketTypeRequest[] requests = new TicketTypeRequest[]{}; //each TicketTypeRequest for adult,child,infant pushed into array
    private TicketPaymentServiceImpl ticketPaymentService = new TicketPaymentServiceImpl();
    private SeatReservationServiceImpl seatReservationService = new SeatReservationServiceImpl();

    /**
     * Should only have private methods other than the one below.
     */
    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        if (VerifyAccountId(accountId)) { //ensure that a valid account has been passed through to this method before continuing
            CheckAdultTicketSelected(); //now check to make sure an adult ticket has been selected before doing further logic on all ticketrequests
            for (var requests : ticketTypeRequests) { //iterate through each request type: infant, child, adult
                if (CheckTicketAmount()) //make sure no more than 20 tickets are being selected before adding more tickets
                    AddTicket(requests); //execute AddTicket method on each request inside array
            }
            seatReservationService.reserveSeat(accountId, seatsReserved); //outside of loop, after data has been applied properly - reserve the seats
            ticketPaymentService.makePayment(accountId, totalPrice); //outside of loop, after data has been applied properly - make the payment
        }
    }

    private boolean VerifyAccountId(Long accountId) throws InvalidPurchaseException { //boolean method to return true or not if account ID is valid or not
        if (accountId < 1) {
            throw new InvalidPurchaseException("Invalid account ID"); //all exceptions outputting context for failure
        } else return true; //accountId is valid!
    }

    private boolean CheckTicketAmount() throws InvalidPurchaseException {
        int result = requests[0].getNoOfTickets() + requests[1].getNoOfTickets() + requests[2].getNoOfTickets(); //get total amount of tickets
        if (result > 20) {
            throw new InvalidPurchaseException("You cannot purchase more than 20 tickets at once");
        } else return true;

    }

    private void CheckAdultTicketSelected() throws InvalidPurchaseException {
        if (requests[0].getNoOfTickets() <= 0) {
            throw new InvalidPurchaseException("There are no adult tickets selected for purchase");
        }
    }

    private void AddTicket(TicketTypeRequest ticketTypeRequest) throws InvalidPurchaseException {
        switch (ticketTypeRequest.getTicketType()) { //switch to handle logic of different types of tickets
            case ADULT:
                totalPrice = totalPrice + (20 * ticketTypeRequest.getNoOfTickets());
                seatsReserved = seatsReserved + ticketTypeRequest.getNoOfTickets();
                totalTicketAmount = totalTicketAmount + ticketTypeRequest.getNoOfTickets();
                break;
            case CHILD:
                totalPrice = totalPrice + (10 * ticketTypeRequest.getNoOfTickets());
                seatsReserved = seatsReserved + ticketTypeRequest.getNoOfTickets();
                totalTicketAmount = totalTicketAmount + ticketTypeRequest.getNoOfTickets();
                break;
            case INFANT:
                totalTicketAmount = totalTicketAmount + ticketTypeRequest.getNoOfTickets();
                break;
        }
    }

}
