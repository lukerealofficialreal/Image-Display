//Any class which extends this interface has some set of input values which represent a specific possible
//instance of the class. Any class member with a limited possible range of values can have a representative value.
package interfaces;

public interface RepresentativeValue<T> {
    public T getRepVal();
}
