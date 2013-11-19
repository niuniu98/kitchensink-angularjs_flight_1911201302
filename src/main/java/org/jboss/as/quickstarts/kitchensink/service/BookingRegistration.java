package org.jboss.as.quickstarts.kitchensink.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.as.quickstarts.kitchensink.model.Booking;

@Stateless
public class BookingRegistration {

	 @Inject
	    private Logger log;

	    @Inject
	    private EntityManager em;

	    @Inject
	    private Event<Booking> memberEventSrc;

	    public void register(Booking booking) throws Exception {
	        log.info("Registering " + booking.getSeat_id());
	        em.persist(booking);
	        memberEventSrc.fire(booking);
	    }
	    public void delete(Booking booking) throws Exception {
	    	//System.out.println("%%%%%%%%%%%%delete");
	        log.info("delete " + booking.getSeat_id());
	        //em.remove(booking);
	       
	        em.remove(em.contains(booking)? booking:em.merge(booking));
	        memberEventSrc.fire(booking);

	    }
}
