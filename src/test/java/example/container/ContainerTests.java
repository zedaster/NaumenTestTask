package example.container;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Тесты для добавления и удаления элементов из {@link Container}
 */
public class ContainerTests {
    /**
     * Контейнер
     */
    private Container container;

    /**
     * Пересоздаем контейнер перед каждым тестом
     */
    @Before
    public void recreateContainer() {
        this.container = new Container();
    }

    @Test
    public void addNull() {
        this.container.add(null);
        Assert.assertEquals(1, this.container.size());
        Assert.assertNull(this.container.get(0));
    }

    @Test
    public void addCorrectItem() {
        Item item = new Item(10);
        this.container.add(item);
        Assert.assertEquals(1, this.container.size());
        Assert.assertEquals(item, this.container.get(0));
    }

    @Test
    public void addManyItems() {
        Item itemOne = new Item(10);
        Item itemTwo = new Item(20);
        this.container.add(itemOne);
        this.container.add(itemTwo);
        Assert.assertEquals(2, this.container.size());
        Assert.assertEquals(itemOne, this.container.get(0));
        Assert.assertEquals(itemTwo, this.container.get(1));
    }

    @Test
    public void addEqualItems() {
        Item itemOne = new Item(10);
        Item itemSame = new Item(10);
        this.container.add(itemOne);
        this.container.add(itemSame);
        Assert.assertEquals(2, this.container.size());
        Assert.assertEquals(itemOne, this.container.get(0));
        Assert.assertEquals(itemSame, this.container.get(1));
    }

    @Test
    public void addSameLinkItems() {
        Item itemOne = new Item(10);
        this.container.add(itemOne);
        this.container.add(itemOne);
        Assert.assertEquals(2, this.container.size());
        Assert.assertEquals(itemOne, this.container.get(0));
        Assert.assertEquals(itemOne, this.container.get(1));
    }

    @Test
    public void removeExistingItem() {
        Item item = new Item(10);
        this.container.add(item);
        this.container.remove(item);
        Assert.assertEquals(0, this.container.size());
    }

    @Test
    public void removeOneOfManyItems() {
        Item itemOne = new Item(10);
        Item itemTwo = new Item(20);
        this.container.add(itemOne);
        this.container.add(itemTwo);
        this.container.remove(itemTwo);
        Assert.assertEquals(1, this.container.size());
        Assert.assertEquals(itemOne, this.container.get(0));
    }

    @Test
    public void removeOneOfTwoEqualItems() {
        Item itemOne = new Item(10);
        Item itemTwo = new Item(10);
        this.container.add(itemOne);
        this.container.add(itemTwo);
        this.container.remove(itemOne);
        Assert.assertEquals(1, this.container.size());
        Assert.assertEquals(itemTwo, this.container.get(0));
    }

    @Test
    public void removeOneOfSameEqualItems() {
        Item item = new Item(10);
        this.container.add(item);
        this.container.add(item);
        this.container.remove(item);
        Assert.assertEquals(1, this.container.size());
        Assert.assertEquals(item, this.container.get(0));
    }


    @Test
    public void removeNonExistingItems() {
        Item item = new Item(10);
        this.container.remove(item);
        // it should be quiet
    }

    @Test
    public void removeNull() {
        this.container.remove(null);
        // it should be quiet
    }
}
