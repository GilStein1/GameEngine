package pack.Arrays;

public class Queue<T>
{
    private Node<T> first;
    private Node<T> last;

    /* הפעולה בונה ומחזירה תור ריק **/
    public Queue()
    {
        this.first = null;
        this.last = null;
    }
    /* הפעולה מכניסה את הערך x לסוף התור הנוכחי **/
    public void insert(T x)
    {
        Node<T> temp = new Node<T>(x);
        if (first == null)
            first = temp;
        else
            last.setNext(temp);
        last = temp;
    }
    /* הפעולה מוציאה ומחזירה את הערך הנמצא  בראש התור הנוכחי **/
    public T remove()
    {
        T x = first.getValue();
        first = first.getNext();
        if (first == null)
            last = null;
        return x;
    }
    /* הפעולה מחזירה את הערך הנמצא  בראש התור הנוכחי **/
    public T head()
    {
        return first.getValue();
    }
    /* הפעולה מחזירה אמת אם התור הנוכחי ריק או שקר אחרת **/
    public boolean isEmpty()
    {
        return first == null;
    }

    /* הפעולה מחזירה מחרוזת המתארת את התור הנוכחי */
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