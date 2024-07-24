package works.weave.socks.cart.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import works.weave.socks.cart.cart.CartDAO;
import works.weave.socks.cart.cart.CartResource;
import works.weave.socks.cart.entities.Item;
import works.weave.socks.cart.item.FoundItem;
import works.weave.socks.cart.item.ItemDAO;
import works.weave.socks.cart.item.ItemResource;
import java.util.List;
import java.util.function.Supplier;



@RestController
@RequestMapping(value = "/carts/{customerId:.*}/items")
public class ItemsController {
    private final Logger LOG = getLogger(getClass());

    @Autowired
    private ItemDAO itemDAO;
    @Autowired
    private CartsController cartsController;
    @Autowired
    private CartDAO cartDAO;

    @ResponseStatus()
    @RequestMapping(value = "/{itemId:.*}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public Item get(@PathVariable String customerId, @PathVariable String itemId) {
        return new FoundItem(() -> getItems(customerId), () -> new Item(itemId)).get();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public List<Item> getItems(@PathVariable String customerId) {
        return cartsController.get(customerId).contents();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public Item addToCart(@PathVariable String customerId, @RequestBody Item item) {
        FoundItem foundItem = new FoundItem(() -> cartsController.get(customerId).contents(), () -> item);
        if (!foundItem.hasItem()) {
            Supplier<Item> newItem = new ItemResource(itemDAO, () -> item).create();
            LOG.debug("Did not find item. Creating item for user: " + customerId + ", " + newItem.get());
            new CartResource(cartDAO, customerId).contents().get().add(newItem).run();
            return item;
        } else {
            Item newItem = new Item(foundItem.get(), foundItem.get().quantity() + 1);
            LOG.debug("Found item in cart. Incrementing for user: " + customerId + ", " + newItem);
            u(customerId, newItem);
            return newItem;
        }
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(value = "/{itemId:.*}", method = RequestMethod.DELETE)
    public void removeItem(@PathVariable String customerId, @PathVariable String itemId) {
        FoundItem foundItem = new FoundItem(() -> getItems(customerId), () -> new Item(itemId));
        Item item = foundItem.get();

        LOG.debug("Removing item from cart: " + item);
        new CartResource(cartDAO, customerId).contents().get().delete(() -> item).run();

        LOG.debug("Removing item from repository: " + item);
        new ItemResource(itemDAO, () -> item).destroy().run();
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.PATCH)
    public void updateItem(@PathVariable String customerId, @RequestBody Item item) {

        LOG.debug("Merging item in cart for user: " + customerId + ", " + item);
        itemResource.merge(item).run();
    }

    @GetMapping("/getById")
    public Item getById(String customerId, String itemId) {
        if(itemId.equals("null"))){
            return null;
        }
        new FoundItem(() -> getItems(customerId), () -> new Item(itemId)).get();
    }
}
