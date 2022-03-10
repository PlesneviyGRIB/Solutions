import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.concurrent.*;

public class Tests {

    @Test
    public void pizzaTest(){
        Assertions.assertEquals("pizza FOURCHEESES LARGE", new Pizza(Pizza.KindOfPizza.FOURCHEESES, Pizza.PizzaSize.LARGE).toString());
        Assertions.assertEquals("pizza MARINARA SMALL", new Pizza(Pizza.KindOfPizza.MARINARA, Pizza.PizzaSize.SMALL).toString());
        Assertions.assertEquals(Pizza.getTimeOfCooking(Pizza.KindOfPizza.FOURCHEESES, Pizza.PizzaSize.MEDIUM), new Pizza(Pizza.KindOfPizza.FOURCHEESES, Pizza.PizzaSize.MEDIUM).getTime());
        Assertions.assertEquals(4, new Pizza(Pizza.KindOfPizza.CRUDO, Pizza.PizzaSize.LARGE).getTime());
    }

    @Test
    public void orderTest(){
        Pizza pizza = new Pizza(Pizza.KindOfPizza.CRUDO, Pizza.PizzaSize.SMALL);

        Assertions.assertEquals(pizza, new Order(pizza,10).getFood());
        Assertions.assertEquals(7, new Order(new Pizza(Pizza.KindOfPizza.MARGARITA, Pizza.PizzaSize.EXTRALARGE), 7).getDeliveryTimeRequired());

        OrdersGenerator ordersGenerator = new OrdersGenerator();

        Assertions.assertEquals(3,ordersGenerator.next().getId());
        Assertions.assertEquals(4,ordersGenerator.next().getId());
        Assertions.assertEquals(5,ordersGenerator.next().getId());

        Assertions.assertEquals("id: 6 time: 12  pizza NEAPOLITANO SMALL", new Order(new Pizza(Pizza.KindOfPizza.NEAPOLITANO, Pizza.PizzaSize.SMALL), 12).toString());
    }

    @Test
    public void storageTest() throws InterruptedException {
        Storage storage = new Storage(15);

        Assertions.assertEquals(15, storage.remainingCapacity());

        ExecutorService executorService = Executors.newCachedThreadPool();

        executorService.execute(new Deliveryman(storage, 1));
        executorService.execute(new Deliveryman(storage, 2));

        TimeUnit.SECONDS.sleep(1);

        Assertions.assertEquals(true, storage.isEmpty());
    }
}