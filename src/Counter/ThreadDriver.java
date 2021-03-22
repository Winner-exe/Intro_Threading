package Counter;

public class ThreadDriver
{
    public static void main(String[] args)
    {
        Counter[] counters = new Counter[3];
        for (int i = 0; i < counters.length; i++)
            counters[i] = new Counter();

        Thread[] threads = new Thread[3];
        for (int i = 0; i < threads.length; i++)
        {
            threads[i] = new Thread(counters[i]);
            threads[i].start();
        }
    }
}
