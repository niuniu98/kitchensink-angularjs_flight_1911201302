package org.jboss.as.quickstarts.kitchensink.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.as.quickstarts.kitchensink.model.Item;
@Stateless
public class ItemRegistration {

	 @Inject
	    private Logger log;

	    @Inject
	    private EntityManager em;

	    @Inject
	    private Event<Item> itemEventSrc;

	    public void register(Item item) throws Exception {
	        log.info("Registering " + item.getSeat_id());
	        em.persist(item);
	        itemEventSrc.fire(item);
	    }
	    
	    public void update(Item item) throws Exception {
	        log.info("update " + item.getId());
	        em.merge(item);
	        itemEventSrc.fire(item);
	    }
}
