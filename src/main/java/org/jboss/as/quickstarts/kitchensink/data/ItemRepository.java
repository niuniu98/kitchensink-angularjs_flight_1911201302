package org.jboss.as.quickstarts.kitchensink.data;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.as.quickstarts.kitchensink.model.Item;
@ApplicationScoped
public class ItemRepository {

	 @Inject
	    private EntityManager em;

	    public Item findById(Long id) {
	    	//System.out.println("#############Repository:findById");
	        return em.find(Item.class, id);
	    }

	    public Item findBySeatId(String seatId) {
	    	//System.out.println("#############Repository:findBySeatId "+seatId);
	        CriteriaBuilder cb = em.getCriteriaBuilder();
	        CriteriaQuery<Item> criteria = cb.createQuery(Item.class);
	        Root<Item> item = criteria.from(Item.class);
	        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
	        // feature in JPA 2.0
	        // criteria.select(member).where(cb.equal(member.get(Member_.email), email));
	        criteria.select(item).where(cb.equal(item.get("seat_id"), seatId));
	        Item items= em.createQuery(criteria).getSingleResult();
	        return items;
	    }

	    public List<Item> findAllOrderedByFlight() {
	    	//System.out.println("#############Repository:findAll");
	        CriteriaBuilder cb = em.getCriteriaBuilder();
	        CriteriaQuery<Item> criteria = cb.createQuery(Item.class);
	        Root<Item> item = criteria.from(Item.class);
	        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
	        // feature in JPA 2.0
	        // criteria.select(member).orderBy(cb.asc(member.get(Member_.name)));
	        //按航班号排序
	        criteria.select(item).orderBy(cb.asc(item.get("flight_num")));
	        return em.createQuery(criteria).getResultList();
	    }
}
