package Chat;

import java.util.*;
import java.net.*;
import java.io.*;

public class DisaccordServer
{
    static final int PORT = 4444;
    static HashMap<String, PrintWriter> users;
    static boolean isRunning;

    public static void main(String[] args) throws IOException {
        users = new HashMap<>();
        users.put("Server", new PrintWriter(System.out, true));
        isRunning = true;
        Thread disconnectThread = new Thread(new DisconnectThread());
        disconnectThread.start();

        do
        {
            try (ServerSocket serverSocket = new ServerSocket(PORT))
            {
                Thread userThread = new Thread(new DisaccordUserThread(serverSocket.accept()));
                userThread.start();
            }
        } while (isRunning);
    }

    //Handles server-side interactions w/ a single user
    protected static class DisaccordUserThread implements Runnable
    {
        private final Socket clientSocket;
        private String name;

        public DisaccordUserThread(Socket client)
        {
            clientSocket = client;
        }

        @Override
        public void run()
        {
            try
            (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())))
            {
                boolean isConnected;

                String inputLine = in.readLine();
                name = inputLine;

                if (!users.containsKey(name)) {
                    isConnected = true;

                    users.put(name, out);
                    for (PrintWriter pw : users.values()) {
                        pw.println(name + " has joined.");
                    }

                    while (isConnected && (inputLine = in.readLine()) != null) {
                        if (inputLine.matches("![dD][cC].*"))
                        {
                            for (PrintWriter pw : users.values()) {
                                pw.println(name + " has left.");
                            }
                            users.remove(name);
                            isConnected = false;
                            clientSocket.close();
                        }
                        else if (inputLine.matches("![pP][mM].*"))
                        {
                            if (inputLine.length() > 4 && inputLine.indexOf(" ") == 3)
                            {
                                if (inputLine.substring(4).contains(" "))
                                {
                                    String target = inputLine.substring(4, inputLine.substring(4).indexOf(" ") + 4);
                                    String msg = inputLine.substring(inputLine.substring(4).indexOf(" ") + 5);
                                    users.get(target).println(name + " whispers: " + msg);
                                    users.get(name).println("Whispered to " + target + ": " + msg);
                                }
                            }
                            else
                                out.println("No user specified! Correct Usage: !pm <username> <msg>");
                        }
                        else {
                            for (PrintWriter pw : users.values()) {
                                pw.println(name + ": " + inputLine);
                            }
                        }
                    }
                }
                else
                {
                    out.println("Name already taken! Disconnecting from server...");
                    clientSocket.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                users.remove(name);
                System.exit(1);
            }
        }
    }

    protected static class DisconnectThread implements Runnable
    {
        private Scanner in;

        public DisconnectThread() {in = new Scanner(System.in);}

        @Override
        public void run()
        {
            String inputLine;
            while (isRunning && (inputLine = in.nextLine()) != null) {
                if (inputLine.matches("![dD][cC].*"))
                {
                    for (PrintWriter pw : users.values()) {
                        pw.println("Server disconnecting! Closing program...");
                        pw.close();
                    }
                    isRunning = false;
                }
                else if (inputLine.matches("![pP][mM].*"))
                {
                    if (inputLine.length() > 4 && inputLine.indexOf(" ") == 3)
                    {
                        if (inputLine.substring(4).contains(" "))
                        {
                            String target = inputLine.substring(4, inputLine.substring(4).indexOf(" ") + 4);
                            String msg = inputLine.substring(inputLine.substring(4).indexOf(" ") + 5);
                            users.get(target).println("Server whispers: " + msg);
                            System.out.println("Whispered to " + target + ": " + msg);
                        }
                    }
                    else
                        System.out.println("No user specified! Correct Usage: !pm <username> <msg>");
                }
                else {
                    for (PrintWriter pw : users.values()) {
                        pw.println("Server: " + inputLine);
                    }
                }
            }
        }
    }
}
