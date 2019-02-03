package uk.me.desiderio.shiftt.data.database.model;

/**
 * To be implemented by room entities classes that provide the corresponding twitter data object
 */
public interface SeedProvider<T> {
    T getSeed();
}
