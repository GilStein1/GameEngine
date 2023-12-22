package pack.Arrays;

public class Queue<T>
{
    private Node<T> first;
    private Node<T> last;

    public Queue()
    {
        this.first = null;
        this.last = null;
    }
    public void insert(T x)
    {
        Node<T> temp = new Node<T>(x);
        if (first == null)
            first = temp;
        else
            last.setNext(temp);
        last = temp;
    }
    public T remove()
    {
        T x = first.getValue();
        first = first.getNext();
        if (first == null)
            last = null;
        return x;
    }
    public T head()
    {
        return first.getValue();
    }
    public boolean isEmpty()
    {
        return first == null;
    }

    @Override
    public  String toString()
    {
    String s = "[";
    Node<T> p = this.first;
    while (p != null)
    {
        s = s + p.getValue().toString();
        if (p.getNext() != null)
            s = s + ",";
        p = p.getNext();
    }
    s = s + "]";
    return s;
}

}