package Chat;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class DisaccordClient
{
    static boolean isConnected = false;
    static final String hostname = "0.0.0.0";
    static final int port = 4444;

    public static void main(String[] args)
    {
        System.out.println("Welcome to Disaccord!");
        System.out.println("Username: ");
        Scanner scan = new Scanner(System.in);
        String username = scan.nextLine();

        try (
            Socket userSocket = new Socket(hostname, port);
            PrintWriter out = new PrintWriter(userSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(userSocket.getInputStream()))
        )
        {
            isConnected = true;
            BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
            Thread fromServer = new Thread(new ReadingThread(in));
            fromServer.start();

            out.println(username + " has connected.");

            String fromUser;
            while ((fromUser = userIn.readLine()) != null)
            {
                out.println(username + ": " + fromUser);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    protected static class ReadingThread implements Runnable
    {
        private BufferedReader in;

        public ReadingThread(BufferedReader in)
        {
            this.in = in;
        }

        public void run()
        {
            try
            {
                String fromServer;
                while ((fromServer = in.readLine()) != null)
                {
                    System.out.println(fromServer);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}
