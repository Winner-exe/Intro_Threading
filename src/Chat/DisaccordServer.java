package Chat;

import java.util.*;
import java.net.*;
import java.io.*;

public class DisaccordServer
{
    static final int PORT = 4444;
    static HashMap<String, PrintWriter> users;

    public static void main(String[] args) throws IOException {
        users = new HashMap<>();

        do
        {
            try (ServerSocket serverSocket = new ServerSocket(PORT);
            Socket clientSocket = serverSocket.accept())
            {
                Thread userThread = new Thread(new DisaccordUserThread(clientSocket));
                userThread.start();

                users.put(clientSocket.getInetAddress().toString(), new PrintWriter(clientSocket.getOutputStream(), true));
            }
        } while (!users.isEmpty());
    }

    //Handles server-side interactions w/ a single user
    protected static class DisaccordUserThread implements Runnable
    {
        private final Socket clientSocket;

        public DisaccordUserThread(Socket client)
        {
            clientSocket = client;
        }

        public void run()
        {
            try
            (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())))
            {
                String inputLine;

                while ((inputLine = in.readLine()) != null)
                {
                    String finalInputLine = inputLine;
                    users.forEach((s, pw) -> out.println(s + ": " + finalInputLine));
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
