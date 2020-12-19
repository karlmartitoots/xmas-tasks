package ee.kmtster.xmastasks;

import org.bukkit.Bukkit;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class RandomCollection<E> {
    private final NavigableMap<Double, E> map = new TreeMap<Double, E>();
    private final Random random;
    private double total = 0;

    public RandomCollection() {
        this(new Random());
    }

    public RandomCollection(Random random) {
        this.random = random;
    }

    public RandomCollection<E> add(double weight, E result) {
        if (weight <= 0) return this;
        total += weight;
        map.put(total, result);
        return this;
    }

    public E next() {
        double value = random.nextDouble() * total;
        Bukkit.getLogger().info(String.format("generating next: total=%s, value=%s", total, value));
        Bukkit.getLogger().info(String.format("map: %s", map));
        return map.higherEntry(value).getValue();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean contains(E element){
        return map.containsValue(element);
    }
}