import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server extends Thread
{
	private final ServerSocket serverSocket;
	private final ArrayList<Client> clients = new ArrayList<Client>();
	private final ArrayList<String> buffer = new ArrayList<String>();
	private String line = "";
	
	public Server(int port) throws IOException
	{
		serverSocket = new ServerSocket(port);
	}
	
	public void run()
	{
		while (true)
		{
			try
			{
				Client c = new Client(serverSocket.accept());
				c.start();
				clients.add(c);
			}
			catch (IOException e){ }
		}
	}
	
	public void buffer(char c)
	{
		line += c;
		if (line.endsWith("\n"))
		{
			send(filter(line));
			line = "";
		}
	}
	
	public String filter(String message)
	{
		if (message.startsWith(">.gettoken "))
		{
			message = message.replaceFirst(">.gettoken ", "").replace("\n", "").replaceAll("\\p{C}", "");
			String token = UserManager.genToken();
			System.out.println("[REMOTE CONSOLE] Here's the token for " + message + ": " + token);
			try
			{
				UserManager.setToken(message, token);
			}
			catch (Exception e) {}
			return null;
		}
		if (message.startsWith(">.deny "))
		{
			message = message.replaceFirst(">.deny ", "").replace("\n", "").replaceAll("\\p{C}", "");
			UserManager.removeUser(message);
			kick(message);
			System.out.println("[REMOTE CONSOLE] Removed user " + message);
			return null;
		}
		if (message.startsWith(">."))
		{
			return null;
		}
		
		if (message.contains("issued server command: /login"))
			return null;
		if (message.contains(" logged in with entity id "))
			return null;
		buffer.add(message);
		try
		{
			buffer.remove(50);
		}
		catch (Exception e) { }
		return message;
	}
	
	public void send(String message)
	{
		if (message != null)
		{
			for (Object c : clients.toArray())
				((Client)c).stream(message);
		}
	}
	
	public ArrayList<String> getBuffer()
	{
		return buffer;
	}

	public void notifyDisconnect(Client c)
	{
		System.out.println(Message.disconnectClient + c.IP);
		clients.remove(c);
	}
	
	private void kick(String username)
	{
		for (Client c : clients)
		{
			try
			{
				if (c.username.equals(username))
					c.disconnect();
			}
			catch (Exception e){ }
		}
	}
}
