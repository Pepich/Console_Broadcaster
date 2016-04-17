import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.crypto.SealedObject;
import javax.crypto.SecretKey;

public class Client extends Thread
{
	protected final String IP;
	protected String username;
	
	private final ObjectOutputStream out;
	private final ObjectInputStream in;
	private ClientState state = ClientState.getName;
	private Ciphers ciphers;
	
	protected Client(Socket s) throws IOException
	{
		out = new ObjectOutputStream(s.getOutputStream());
		in = new ObjectInputStream(s.getInputStream());
		IP = s.getInetAddress().toString();
	}
	
	protected void stream(String message)
	{
		if (state == ClientState.stream)
			send(message);
	}
	
	private void send(String message)
	{
		try
		{
			out.writeObject(new SealedObject(message, ciphers.AES_ENCODE));
		}
		catch (Exception e)
		{
			disconnect();
		}
	}
	
	public void run()
	{
		System.out.println(Message.newClient + IP);
		try
		{
			ciphers = new Ciphers((SecretKey)((SealedObject)in.readObject()).getObject(Ciphers.RSA_DECODE));
			send(Message.requestUsername);
			
			while (true)
			{
				process((String)((SealedObject)in.readObject()).getObject(ciphers.AES_DECODE));
			}
		}
		catch (Exception e)
		{
			disconnect();
		}
		
	}
	
	protected void disconnect()
	{
		Main.getServer().notifyDisconnect(this);
		try
		{
			in.close();
			out.close();
		}
		catch (IOException e) { }
	}
	
	private void process(String message)
	{
		switch(state)
		{
		case getName:
			setUsername(message);
			break;
		case getPassword:
			authenticate(message);
			break;
		case awaitCommand:
			command(message);
			break;
		case changePassword:
			changePassword(message);
			break;
		case requestConfirm:
			confirm(message);
			break;
		case stream:
			command(message);
			break;
		}
	}
	
	private void command(String command)
	{
		if (command.equals("cgpass"))
		{
			state = ClientState.changePassword;
			send(Message.requestNewPassword);
		}
		else if (command.equals("stream"))
		{
			state = ClientState.stream;
			sendBuffer();
		}
		else if (command.equals("exit"))
		{
			disconnect();
		}
		else
		{
			send(Message.unknownMessage);
		}
	}
	
	private void sendBuffer()
	{
		send(Message.startingStream);
		ArrayList<String> buffer = Main.getServer().getBuffer();
		for (String s : buffer.toArray(new String[0]))
			send(s);
	}
	
	private void setUsername(String username)
	{
		this.username = username;
		state = ClientState.getPassword;
		send(Message.requestPassword);
	}

	private void authenticate(String password)
	{
		try
		{
			int answer = UserManager.authenticate(username, password);
			
			if (answer == -1)
			{
				send(Message.requestPassword);
				return;
			}
			if (answer == 1)
			{
				state = ClientState.awaitCommand;
				send(Message.awaitingCommand);
				return;
			}
			if (answer == 2)
			{
				state = ClientState.changePassword;
				send(Message.requestNewPassword);
				return;
			}
		}
		catch (Exception e)
		{
			disconnect();
		} 
	}
	
	private void changePassword(String password)
	{
		UserManager.changePassword(username, password);
		send(Message.requestConfirm);
		state = ClientState.requestConfirm;
	}
	
	private void confirm(String password)
	{
		if (UserManager.confirm(username, password))
		{
			send(Message.passwordChanged);
			send(Message.awaitingCommand);
			state = ClientState.awaitCommand;
		}
		else
		{
			send(Message.changeAborted);
			send(Message.awaitingCommand);
			state = ClientState.awaitCommand;
		}
	}
}
