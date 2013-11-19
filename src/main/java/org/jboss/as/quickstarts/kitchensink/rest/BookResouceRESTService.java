package org.jboss.as.quickstarts.kitchensink.rest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.as.quickstarts.kitchensink.data.BookRepository;
import org.jboss.as.quickstarts.kitchensink.data.ItemRepository;
import org.jboss.as.quickstarts.kitchensink.model.Booking;
import org.jboss.as.quickstarts.kitchensink.model.Item;
import org.jboss.as.quickstarts.kitchensink.service.BookingRegistration;
import org.jboss.as.quickstarts.kitchensink.service.ItemRegistration;

@Path("/bookings")
@RequestScoped
public class BookResouceRESTService {

	@Inject
    private Logger log;

    @Inject
    private Validator validator;

    @Inject
    private BookRepository repository;
    
    @Inject
    private ItemRepository itemRepository;

    @Inject
    BookingRegistration registration;
    
    @Inject
    ItemRegistration ItemRegistration;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Booking> listAllBookings() {
    	//System.out.println("#####################Rest:listAllBookings");
    	
        return repository.findAllOrderedByDate();
    }
    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Booking lookupMemberById(@PathParam("id") long id) {
    	//System.out.println("################Rest:lookupItemById");
        Booking booking = repository.findById(id);
        if (booking == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return booking;
    }
    
    @DELETE
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBookings(@PathParam("id") long id){
    	//System.out.println("#####################Rest:deleteBookings:"+id);
    	 Response.ResponseBuilder builder = null;

         try {
        	 Booking booking = repository.findById(id);
        	 Item item = itemRepository.findBySeatId(booking.getSeat_id());
        	 //update the value in the table flights
        	 item.setState(false);
        	 ItemRegistration.update(item);
        	 //delete the value in the table bookings
        	 registration.delete(booking);
        	 builder = Response.ok();
         } catch (ConstraintViolationException ce) {
             // Handle bean validation issues
             builder = createViolationResponse(ce.getConstraintViolations());
         } catch (Exception e) {
             // Handle generic exceptions
             Map<String, String> responseObj = new HashMap<String, String>();
             responseObj.put("error", e.getMessage());
             builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
         }

         return builder.build();
    }
    
    /**
     * Creates a new member from the values provided. Performs validation, and will return a JAX-RS response with either 200 ok,
     * or with a map of fields, and related errors.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createMember(Booking booking) {

        //System.out.println(booking.getBook_date()+"%%%%"+booking.getEmail()+"%%%%%%"+booking.getSeat_id());
    	Response.ResponseBuilder builder = null;

        try {
            // Validates member using bean validation
            validateBooking(booking);
            //add booking in the talbe Booking
            registration.register(booking);
            //update value in the table flight
       	 	Item item = itemRepository.findBySeatId(booking.getSeat_id());
       	 	//update the value in the table flights
       	 	item.setState(true);
       	 	ItemRegistration.update(item);
            // Create an "ok" response
            builder = Response.ok();
        } catch (ConstraintViolationException ce) {
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            // Handle the unique constrain violation
        	System.out.println("*************");
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("seat", "seat taken");
            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
        }catch (Exception e) {
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();
    }

    /**
     * <p>
     * Validates the given Member variable and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.
     * </p>
     * <p>
     * If the error is caused because an existing member with the same email is registered it throws a regular validation
     * exception so that it can be interpreted separately.
     * </p>
     * 
     * @param member Member to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If member with the same email already exists
     */
    private void validateBooking(Booking booking) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        if (seatAlreadyTakens(booking.getSeat_id(),booking.getBook_date())) {
            throw new ValidationException("seat has been taken");
        }
    }

    /**
     * Creates a JAX-RS "Bad Request" response including a map of all violation fields, and their message. This can then be used
     * by clients to show violations.
     * 
     * @param violations A set of violations that needs to be reported
     * @return JAX-RS response containing all violations
     */
    private Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
        log.fine("Validation completed. violations found: " + violations.size());

        Map<String, String> responseObj = new HashMap<String, String>();

        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
    }

    public boolean seatAlreadyTakens(String seat_id,String book_date) {
        Booking booking = null;
        try {
            booking = repository.findByseatDate(seat_id, book_date);
        } catch (NoResultException e) {
            // ignore
        	return false;
        }
        return true;
    }
}
