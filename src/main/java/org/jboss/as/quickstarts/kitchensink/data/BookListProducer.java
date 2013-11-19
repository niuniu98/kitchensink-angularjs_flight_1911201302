package org.jboss.as.quickstarts.kitchensink.data;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.as.quickstarts.kitchensink.model.Booking;

@RequestScoped
public class BookListProducer {

	@Inject
    private BookRepository bookRepository;

    private List<Booking> bookings;

    // @Named provides access the return value via the EL variable name "members" in the UI (e.g.
    // Facelets or JSP view)
    @Produces
    @Named
    public List<Booking> getBookings() { 
        return bookings;
    }

    public void onBookingListChanged(Booking booking) {
        retrieveAllBookingsOrderedByDate();
    }

    @PostConstruct
    public void retrieveAllBookingsOrderedByDate() {
        bookings = bookRepository.findAllOrderedByDate();
    }
}
