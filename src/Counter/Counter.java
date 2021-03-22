package Counter;

public class Counter implements Runnable
{
    private int count;
    private final int id;
    private static int id_count = 0;

    public Counter()
    {
        count = 0;
        id_count++;
        id = id_count;
    }

    public void run()
    {
        while (count <= 50)
        {
            System.out.println("Counter " + id + ": " + count);
            count++;
        }
    }
}
