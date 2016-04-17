
public class Message
{
	// Exit
	public static final String exitMessage			= "Exiting now";
	
	// Database
	public static final String locatingDatabase		= "Locating user database...";
	public static final String noDatabaseError		= "Unable to locate database. Attempting creation...";
	public static final String databaseCreateError	= "Unable to create database. Make sure that the permissions are set correctly!";
	public static final String databaseFound		= "Successfully located database file, testing permissions now...";
	public static final String readFileError		= "Unable to read file!\nPlease make sure that the permissions are set correctly!";
	public static final String writeFileError		= "Unable to write to file!\nPlease make sure that the permissions are set correctly!\nStarting in read only mode...";
	
	// Encryption
	public static final String readingRSA			= "Reading RSA keypair.";
	public static final String readRSA				= "Successfully read RSA keypair";
	
	// Server
	public static final String invalidPort			= "Invalid port specified. Defaulting to port ";
	public static final String startingServer		= "Starting server on port ";
	public static final String startedServer		= "Successfully started server on port ";
	
	// Client
	public static final String newClient			= "New client connected with IP ";
	public static final String disconnectClient		= " disconnected.";
	public static final String invalidHandshake		= "No successfull handshake after three attempts, kicking client with IP ";
	public static final String invalidAES			= "Recieved invalid AES key, kicking client with IP ";
	public static final String invalidUsername		= "No valid username recieved after three attempts, kicking client with IP ";
	public static final String invalidPassword		= "No valid password recieved after three attempts, kicking client ";
	public static final String authComplete			= "Authentication completed, please welcome user ";
	
	// Communication
	
	public static final String requestUsername		= "Please enter your username:";
	public static final String requestToken			= "Please enter your token:";
	public static final String requestPassword		= "Please enter your password:";
	public static final String requestNewPassword	= "Please enter your new password:";
	public static final String requestConfirm		= "Please enter your new password again:";
	public static final String passwordChanged		= "Successfully changed your password!";
	public static final String changeAborted		= "Password change aborted!";
	public static final String awaitingCommand		= "To change your password, type cgpass. To start streaming, type stream. To disconnect, type exit.";
	public static final String unknownMessage		= "Unkown command. Available commands are cgpass, stream and exit.";
	public static final String startingStream		= "Starting Stream.";
}
