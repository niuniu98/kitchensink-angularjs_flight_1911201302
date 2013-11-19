package org.jboss.as.quickstarts.kitchensink.data;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.as.quickstarts.kitchensink.model.Booking;

@ApplicationScoped
public class BookRepository {

	@Inject
    private EntityManager em;

    public Booking findById(Long id) {
    	System.out.println("#############BookingRepository:findById");
        return em.find(Booking.class, id);
    }

    public Booking findByseatId(String seatId) {
    	//System.out.println("#############BookingRepository:findBySeatId "+bookingId);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Booking> criteria = cb.createQuery(Booking.class);
        Root<Booking> booking = criteria.from(Booking.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(member).where(cb.equal(member.get(Member_.email), email));
        criteria.select(booking).where(cb.equal(booking.get("seat_id"), seatId));
        Booking bookings= em.createQuery(criteria).getSingleResult();
        return bookings;
    }
    
    public Booking findByseatDate(String seatId,String date) {
    	//System.out.println("#############BookingRepository:findBySeatId "+seatId+"&&&&&&&&&date:"+date);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Booking> criteria = cb.createQuery(Booking.class);
        Root<Booking> booking = criteria.from(Booking.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(member).where(cb.equal(member.get(Member_.email), email));
        criteria.select(booking).where(cb.and(cb.equal(booking.get("seat_id"), seatId) , cb.equal(booking.get("book_date"), date)));
        Booking bookings= em.createQuery(criteria).getSingleResult();
        return bookings;
    }

    public List<Booking> findAllOrderedByDate() {
    	//System.out.println("#############BookingRepository:findAll");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Booking> criteria = cb.createQuery(Booking.class);
        Root<Booking> booking = criteria.from(Booking.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(member).orderBy(cb.asc(member.get(Member_.name)));
        //按航班号排序
        criteria.select(booking).orderBy(cb.asc(booking.get("id")));
        return em.createQuery(criteria).getResultList();
    }
}
