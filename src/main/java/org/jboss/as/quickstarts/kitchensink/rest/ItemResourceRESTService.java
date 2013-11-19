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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.as.quickstarts.kitchensink.data.ItemRepository;
import org.jboss.as.quickstarts.kitchensink.model.Item;
import org.jboss.as.quickstarts.kitchensink.service.ItemRegistration;
/**
 * JAX-RS Example
 * <p/>
 * This class produces a RESTful service to read/write the contents of the members table.
 */
@Path("/items")
@RequestScoped
public class ItemResourceRESTService {

	 @Inject
	    private Logger log;

	    @Inject
	    private Validator validator;

	    @Inject
	    private ItemRepository repository;

	    @Inject
	    ItemRegistration registration;

	    @GET
	    @Produces(MediaType.APPLICATION_JSON)
	    public List<Item> listAllItems() {
	    	//System.out.println("#####################Rest:listAllItems");
	    	
	        return repository.findAllOrderedByFlight();
	    }

	    @GET
	    @Path("/{id:[0-9][0-9]*}")
	    @Produces(MediaType.APPLICATION_JSON)
	    public Item lookupItemById(@PathParam("id") long id) {
	    	//System.out.println("################Rest:lookupItemById");
	        Item item = repository.findById(id);
	        if (item == null) {
	            throw new WebApplicationException(Response.Status.NOT_FOUND);
	        }
	        return item;
	    }

	    /**
	     * Creates a new member from the values provided. Performs validation, and will return a JAX-RS response with either 200 ok,
	     * or with a map of fields, and related errors.
	     */
	    @POST
	    @Consumes(MediaType.APPLICATION_JSON)
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response createItem(Item item) {
	    	
	    	//System.out.println("################Rest:createItem");
	        Response.ResponseBuilder builder = null;

	        try {
	            // Validates member using bean validation
	            validateItem(item);

	            registration.register(item);

	            // Create an "ok" response
	            builder = Response.ok();
	        } catch (ConstraintViolationException ce) {
	            // Handle bean validation issues
	            builder = createViolationResponse(ce.getConstraintViolations());
	        } catch (ValidationException e) {
	            // Handle the unique constrain violation
	            Map<String, String> responseObj = new HashMap<String, String>();
	            responseObj.put("seat_id", "SeatId taken");
	            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
	        } catch (Exception e) {
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
	    private void validateItem(Item item) throws ConstraintViolationException, ValidationException {
	        // Create a bean validator and check for issues.
	        Set<ConstraintViolation<Item>> violations = validator.validate(item);

	        if (!violations.isEmpty()) {
	            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
	        }

	        // Check the uniqueness of the email address
	        if (flightSeatAlreadyExists(item.getSeat_id())) {
	            throw new ValidationException("Unique flight seat");
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

	    /**
	     * Checks if a member with the same email address is already registered. This is the only way to easily capture the
	     * "@UniqueConstraint(columnNames = "email")" constraint from the Member class.
	     * 
	     * @param email The email to check
	     * @return True if the email already exists, and false otherwise
	     */
	    public boolean flightSeatAlreadyExists(String seatId) {
	        Item item = null;
	        try {
	            item = repository.findBySeatId(seatId);
	            //System.out.println("#################Rest"+seatId+"#########"+item);
	        } catch (NoResultException e) {
	            // ignore
	        	//System.out.println("************No Result");
	        }
	        return item != null;
	    }
}
