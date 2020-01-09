import javax.swing.text.JTextComponent;
import java.net.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SocketThread implements Runnable {
    final JTextComponent textpane;
    Socket socket;
    boolean auto_answer = false;

    public SocketThread(Socket socket, JTextComponent textpane) {
        this.socket = socket;
        this.textpane = textpane;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String time = dtf.format(now);

        String status = "Connected "+socket.getRemoteSocketAddress() + " at " + time;
        textpane.setText(textpane.getText() + status +"\r\n");
        System.out.println(status);
    }

    @Override
    public void run() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.contains("bye")) break;
                textpane.setText(textpane.getText() + "->" + inputLine + "\r\n");
                if (auto_answer) {
                    try {Thread.sleep(1000*1);}
                    catch (InterruptedException e) {e.printStackTrace();}
                    out.println(inputLine);
                    textpane.setText(textpane.getText() + "<=" + inputLine + "\r\n");
                }
            }
            stop();
        }
        catch (IOException e) {System.err.println("t6: "+e.getMessage());}
    }

    public void stop() {
        try {
            socket.close();
            textpane.setText(textpane.getText() + "Socket " + socket.getPort() + " closed by respondent" + "\r\n");
            System.out.println("Socket " + socket.getPort() + " closed by respondent");
        }
        catch (IOException e) {e.printStackTrace();}
    }

}