import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

public class Main
{
	private static int			port      = 9001;
	private static PrivateKey 	privateKey;
	private static Server 		server;
	
	private static final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	
	public static void main(String[] args) throws IOException
	{
		try
		{
			Ciphers.init();
			
			try
			{
				port = Integer.valueOf(args[0]);
			}
			catch (Exception e)
			{
				System.err.println(Message.invalidPort + port);
			}
			
			long starttime = System.currentTimeMillis();
			System.out.println(Message.locatingDatabase);
			File f = new File("RemoteConsole/passwords.data");
			File f2 = new File("RemoteConsole/tokens.data");
			if (!f.exists() || !f2.exists())
			{
				System.out.println(Message.noDatabaseError);
				try
				{
					File dir = new File("RemoteConsole");
					dir.mkdir();
					f.createNewFile();
					f2.createNewFile();
				}
				catch (Exception e)
				{
					System.err.println(Message.databaseCreateError);
					System.err.println(Message.exitMessage);
					e.printStackTrace();
					System.exit(0);
				}
			}
			System.out.println(Message.databaseFound);
			if (!f.canRead() || !f2.canRead())
			{
				System.err.println(Message.readFileError);
				System.err.println(Message.exitMessage);
			}
			if (!f.canWrite() || !f2.canWrite())
			{
				System.err.println(Message.writeFileError);
				UserManager.init(false);
			}
			else UserManager.init(true);
			
			starttime = System.currentTimeMillis();
			System.out.println(Message.readingRSA);
			File privateKeyFile = new File("private_key");
			FileInputStream fis = new FileInputStream(privateKeyFile);
			byte[] encodedPrivateKey = new byte[(int) privateKeyFile.length()];
			fis.read(encodedPrivateKey);
			fis.close();
			KeyFactory keyFac = KeyFactory.getInstance("RSA");
			PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
			privateKey = keyFac.generatePrivate(privateSpec);
			System.out.println(Message.readRSA + " after " + (System.currentTimeMillis() - starttime) + "ms.");
			
			starttime = System.currentTimeMillis();
			System.out.println(Message.startingServer + port);
			server = new Server(port);
			server.start();
			System.out.println(Message.startedServer + port + " after " + (System.currentTimeMillis() - starttime) + "ms.");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		while (true)
		{
			server.buffer((char)input.read());
		}
	}
	
	public static Server getServer()
	{
		return server;
	}
	
	public static PrivateKey getPrivateKey()
	{
		return privateKey;
	}
}
