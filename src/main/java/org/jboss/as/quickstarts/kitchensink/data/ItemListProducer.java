package org.jboss.as.quickstarts.kitchensink.data;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.as.quickstarts.kitchensink.model.Item;
@RequestScoped
public class ItemListProducer {

	@Inject
    private ItemRepository itemRepository;

    private List<Item> items;

    // @Named provides access the return value via the EL variable name "members" in the UI (e.g.
    // Facelets or JSP view)
    @Produces
    @Named
    public List<Item> getItems() {
        return items;
    }

    public void onItemListChanged(Item item) {
        retrieveAllItemsOrderedByFlight();
    }

    @PostConstruct
    public void retrieveAllItemsOrderedByFlight() {
        items = itemRepository.findAllOrderedByFlight();
    }
}
