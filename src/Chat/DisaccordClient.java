package Chat;

import java.net.*;
import java.io.*;

public class DisaccordClient
{
    static boolean isConnected = false;
    static final String hostname = "10.15.207.71";
    static final int port = 4444;

    public static void main(String[] args)
    {
        System.out.println("Welcome to Disaccord!");
        System.out.print("Username: ");

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

            //Username Setup
            String username = "";
            while (username.length() == 0) {
                username = userIn.readLine();
                if (username.contains(" ")) {
                    username = "";
                    System.out.println("Your username cannot contain a space. Please choose a different username.");
                    System.out.print("Username: ");
                }
            }

            System.out.println("--------------------------------------------------------------");
            System.out.println();

            out.println(username);

            String fromUser;
            while ((fromUser = userIn.readLine()) != null && !fromUser.matches("!dc.*"))
            {
                out.println(fromUser);
            }
            out.println(fromUser);

            userSocket.close();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    protected static class ReadingThread implements Runnable
    {
        private final BufferedReader in;

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
