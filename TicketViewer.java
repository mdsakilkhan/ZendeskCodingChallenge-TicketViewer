import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Zendesk Ticket Viewer
 *
 * @author Sakil Khan
 * @version 1.0
 */

public class TicketViewer{
	
	private final static int ticketsPerPage = 100;
	
	private static String domain = null;
	private static String email = null;
	private static String password = null;

    private final static String borderLine = String.format("%s%90s", "\033[1;30m", " ").replaceAll(" ", "x");
	
	private final static String tableBorderLine = String.format("%s%90s", "\033[0m", " ").replaceAll(" ", "-");
	
	private final static String fullListOptions = String.format("%n%s%s%n%s%n%s%n%s%n%s%n", "\033[0;36m",
											"Type \"list\"        : to view list again",
											"Type \">\"           : for next page",
											"Type \"<\"           : for previous page",
											"Type \"{Ticket_ID}\" : to view detailed ticket information",
											"Type \"quit\"        : to exit program");
											
	private final static String openingOptions = String.format("%n%s%s%n%s%n%s%n", "\033[0;36m",
											"Type \"list\"        : to view full ticket list",
											"Type \"{Ticket_ID}\" : to view detailed ticket information",
											"Type \"quit\"        : to exit program");
											
	private final static String failedAuth = String.format("%n%s%s%n%s%n%n%s%n%s", "\033[0;36m",
											"Couldn't authenticate you",
											"Would you like to try again?",
											"Press any key  : to try again",
											"Type \"quit\"    : to exit program");
	
	/**
	 * The main funtion controls the entire session. It will start with a welcome 
	 * message asking the users to provide login credentials. Once verified users 
	 * can select options whether to display a list or a single ticket or exit program.
	 */
	public static void main(String []args) {
		System.out.printf("%n%s%n%n%s%s%n", borderLine, "\033[1;32m", "✨ Ticket Viewer ✨");	
		
		Scanner input = new Scanner(System.in);	

		System.out.printf("%n\033[0;36mPlease login to continue%n");
		
		String fileName = "tickets.json";
		long ticketCount = 0;
		int filesCreated = 0;
		while(true) {
			String ticketLink = getTicketLink(input);
			createFiles(ticketLink, fileName);
			System.out.printf("%nRunning... %s%n", ticketLink);
			
			if (fileHasTickets(fileName)){
				filesCreated = createAllFiles(email, password);				
				
				ticketCount = getLongFromJson(fileName, "count");
				System.out.printf("%n\033[0mData retrieved!%nYou have: %d Tickets%n", ticketCount);
				break;
			}
			else{
				System.out.println(failedAuth);
				getUserInput(input, filesCreated);
			}
		}

		System.out.printf("%s", openingOptions);

		int currentPage = 1;
		boolean menuFlag = false; 	// 0-main menu 1-full list menu		
		int totalPage = (int) Math.ceil(ticketCount/25.0); 
		
		while (true) {
			String command = getUserInput(input, filesCreated);
			try {
				int id = Integer.parseInt(command)-1;
				detailedTicket(id);
			}
			catch(Exception exc) {
				if (command.equals("list")) {
					fullListTable(currentPage, totalPage);
					menuFlag = true;
				}
				else if (menuFlag) {
					if (command.equals(">") && currentPage < totalPage) {
						currentPage++;
						fullListTable(currentPage, totalPage);
					}
					else if (command.equals("<") && currentPage > 1) {
						currentPage--;
						fullListTable(currentPage, totalPage);
					}
				}
			}

			if(menuFlag) {
				System.out.printf("%s", fullListOptions);
			}
			else {
				System.out.printf("%s", openingOptions);
			}
		}
	}
	
	/**
	 * This function gets users response and returns it. Also can quit program when user types quit.
	 *
	 * @param	input				I/O scanner class refrence
	 * @param	inputfilesCreated	the amount of files created (set 0 initially)
	 * @return						a string with the users response
	 */
	private static String getUserInput(Scanner input, int filesCreated) {
		String command = null;
		
		System.out.printf("%n\033[0;31m👉 ");	
		command = input.nextLine();	

		if (command.equals("quit")) {
			quit(input, filesCreated);
		}

		return command;
	}
	
	/**
	 * This function gets users login credentials and concat a string that can be run to get data
	 * from their account. 
	 *
	 * @param	input			I/O scanner class refrence
	 * @return					a string with executeable command
	 */
	private static String getTicketLink(Scanner input) {
		System.out.printf("%n\033[0;31m{subdomain}👉 ");
		domain = input.nextLine();
		System.out.printf("\033[0;31m{email_address}👉 ");
		email = input.nextLine();
		System.out.printf("\033[0;31m{password}👉 ");
		password = input.nextLine();

		String ticketLink = String.format("curl https://%s.zendesk.com/api/v2/tickets.json -u %s:%s", domain, email, password);
		return ticketLink;
	}
	
	/**
	 * This function creates a process that runs the curl command and puts the contents 
	 * in a file in the working directory.
	 *
	 * @param	ticketLink		the executeable code, curl included
	 * @param	destFileName	the name of the file to be created
	 * @return					true if executed sucessfully
	 */
	private static boolean createFiles(String ticketLink, String destFileName) {		
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(ticketLink.split(" "));
			var fileName = new File(destFileName);
			processBuilder.redirectOutput(fileName);
			var process = processBuilder.start();
			process.waitFor();
			return true;
		} 
		catch (Exception exc) {
			return false;
		}
	}
	
	/**
	 * This function varifies that file has tickets.
	 * 
	 * @param	fileName	the name of the file with extention
	 * @return				true if tickets are found in the json
	 */
	private static boolean fileHasTickets(String fileName) {
		try {
			FileReader fr = new FileReader(fileName);
			Object obj = new JSONParser().parse(fr);
			JSONObject jo = (JSONObject) obj;
			fr.close();
			if(jo.containsKey("tickets")) {
				return true;
			}
			else{
				return false;
			}
		} 
		catch (Exception exc) {
			return false;
		}
	}

	/**
	 * This function uses the JSONParser class to get long values.
	 * 
	 * @param	fileName	name of the file
	 * @param	objectName	key of the object
	 * @return	 			long value of the object
	 */
	private static long getLongFromJson(String fileName, String objectName) {
		try {
			FileReader fr = new FileReader(fileName);
			Object obj = new JSONParser().parse(fr);
			JSONObject jo = (JSONObject) obj;
			fr.close();
			return (Long) jo.get(objectName);			
		} 
		catch (Exception exc) {
			return -1;
		}
	}

	/**
	 * This function uses the JSONParser class to get String values.
	 * 
	 * @param	fileName	name of the file
	 * @param	objectName	key of the object
	 * @return	 			String value of the object
	 */
	public static String getStringFromJson(String fileName, String objectName) {
		try {
			FileReader fr = new FileReader(fileName);
			Object obj = new JSONParser().parse(fr);
			JSONObject jo = (JSONObject) obj;
			fr.close();
			return (String) jo.get(objectName);			
		} 
		catch (Exception exc) {
			return null;
		}
	}

	/**
	 * This function uses the JSONArray class to get higher order String values.
	 * 
	 * @param	fileName	name of the file
	 * @param	objectName	key of the object
	 * @param	objectPos	the ordered position of the object
	 * @return	 			String value of the object
	 */
	public static String getTicketInfoString(String fileName, String objectName, int objectPos) {
		try {
			FileReader fr = new FileReader(fileName);
			Object obj = new JSONParser().parse(fr);
			JSONObject jo = (JSONObject) obj;
			fr.close();
			JSONArray array = (JSONArray) jo.get("tickets");			
			
			JSONObject ajo = (JSONObject) array.get(objectPos);
			
			return (String) ajo.get(objectName);	
		}
		catch (Exception exc) {
			return null;
		}		
	}

	/**
	 * This function uses the JSONArray class to get higher order long values.
	 * 
	 * @param	fileName	name of the file
	 * @param	objectName	key of the object
	 * @param	objectPos	the ordered position of the object
	 * @return	 			long value of the object
	 */
	public static long getTicketInfoLong(String fileName, String objectName, int objectPos) {
		try {
			FileReader fr = new FileReader(fileName);
			Object obj = new JSONParser().parse(fr);
			JSONObject jo = (JSONObject) obj;
			fr.close();
			JSONArray array = (JSONArray) jo.get("tickets");			
			
			JSONObject ajo = (JSONObject) array.get(objectPos);

			return (Long) ajo.get(objectName);	
		}
		catch (Exception exc) {
			return -1;
		}
	}

	/**
	 * This function creates files for any other links with tickets on the users account.
	 * 
	 * @param	email	the users email
	 * @param	pass	the users password
	 * @return	 		the amount of files created
	 */
	public static int createAllFiles(String email, String pass) {
		try {
			// gets the first next page link
			String next_page = getStringFromJson("tickets.json", "next_page");			
			int numOfFiles = 1;

			while (next_page != null) {
				System.out.printf("Storing files from... %s%n", next_page);
				numOfFiles += 1;
				
				String ticketLink = String.format("curl %s -u %s:%s", next_page, email, pass);
				String fileName = String.format("tickets%d.json", numOfFiles);
				
				// create ticket files for every next page
				createFiles(ticketLink, fileName);

				next_page = getStringFromJson(fileName, "next_page");				
			}
			return numOfFiles;
		} 
		catch (Exception exc) {
			System.out.println("A problem has occured getting other files.");
			return 0;
		}
	}

	/**
	 * This function displays one detailed ticket. Using get json object functions we get 
	 * data that we want from the temporary json files that were created in the login process.
	 * 
	 * @param	fileNum		the file number that is displayed in the name (ex. 2 in tickets2.json)
	 * @return				a string of the files name including .json extention
	 */
	public static String getFileName(int fileNum){		
		String fileName = null;

		if(fileNum == 1) {
			fileName = "tickets.json";
		}
		else {
			fileName = String.format("tickets%d.json", fileNum);
		}

		return fileName;
	}

	/**
	 * This function displays one detailed ticket. Using get json object functions we get 
	 * data that we want from the temporary json files that were created in the login process.
	 * 
	 * @param	id	the integer id of the ticket that needs to be displayed
	 * @return		true if executed sucessfully
	 */
	public static boolean detailedTicket(int id) {
		try {
			int fileNum = (int) Math.ceil((id+1)/(ticketsPerPage*1.0));
			int objectPos = id-ticketsPerPage*(fileNum-1);
			String fileName = getFileName(fileNum);

			Long submitter_id = getTicketInfoLong(fileName, "submitter_id", objectPos);

			String created_at = getTicketInfoString(fileName, "created_at", objectPos);
			created_at = created_at.substring(0, 10);
			
			String updated_at = getTicketInfoString(fileName, "updated_at", objectPos);
			updated_at = updated_at.substring(0, 10);

			String subject = getTicketInfoString(fileName, "subject", objectPos);
			if (subject.length() > 73) {
				subject = String.format("%s...", subject.substring(0, 70));
			}

			String description = getTicketInfoString(fileName, "description", objectPos);

			System.out.printf("%n%s%n%s%-70d%n%s%-70s%n%s%-70s%n%s%-70s%n%s%n%n", tableBorderLine, 
										"Submitter ID : ", submitter_id,  
										"Created Date : ", created_at,   
										"Updated Date : ", created_at, 
										"Subject      : ", subject, 
										"Description  :- ");
			
			for (int i = 0; i < description.length(); i++) {
				System.out.printf("%s", description.charAt(i));
				if(i%87 == 0 && i != 0){
					System.out.printf("%n");
				}
			}

			System.out.printf("%n%n%s%n", tableBorderLine);
			return true;
		}
		catch (Exception exc){
			return false;
		}
	}
	
	/**
	 * This funtion deletes the temporary ticket files that were created.
	 *
	 * @param	fileName	the name of the file where the object we are trying to get is
	 * @param	objectPos	the order (from 1 to ticketsPerPage) of an item in a file
	 * @return 				a string already formatted for table view with API data
	 */
	public static String listItemPrinter(String fileName, int objectPos) {
		try {
			Long id = getTicketInfoLong(fileName, "id", objectPos);
			String created_at = getTicketInfoString(fileName, "created_at", objectPos);
			String subject = getTicketInfoString(fileName, "subject", objectPos);
			Long submitter_id = getTicketInfoLong(fileName, "submitter_id", objectPos);

			created_at = created_at.substring(0, 10);
			if (subject.length() > 40) {
				subject = String.format("%s...", subject.substring(0, 37));
			}

			String output = String.format("| %-9d | %-12s | %-40s | %-16d |", id, created_at, subject, submitter_id);
			return output;
		} 
		catch (Exception exc) {
			return null;
		}
	}
	
	/**
	 * This function displays the full list of 25 tickets per page depending on the current 
	 * page number. Than puts the data in a table.
	 *
	 * @param	currentPage		the integer tells us where to start the list from
	 * @param	totalPage		the integer tells us the total amount of pages
	 * @return					true if executed sucessfully
	 */
	public static boolean fullListTable(int currentPage, int totalPage) {
		try{
			System.out.printf("%n\033[0m%s (%d/%d)%n%s%n| %s | %s | %-40s | %-16s |%n%s%n", 
								"Full List Page", currentPage, totalPage, tableBorderLine, 
								"Ticket ID", "Created Date", "Subject", "Submitter ID", tableBorderLine);

			int fileNum = (int) Math.ceil(currentPage/4.0);
			String fileName = getFileName(fileNum);

			for (int i = (currentPage-1)*25; i < (currentPage-1)*25+25; i++) {
				String output = listItemPrinter(fileName, i-ticketsPerPage*(fileNum-1));
				if (output != null){
					System.out.println(output);
				}
			}

			System.out.println(tableBorderLine);
			return true;
		} 
		catch (Exception exc) {
			return false;
		}
	}

	/**
	 * This funtion deletes the temporary ticket files that were created.
	 *
	 * @param	fileNum		the total number of the files created at the begainning of the program
	 * @return	 			the amount of files deleted successfully
	 */
	public static int deleteAllFile(int fileNum) {
		System.out.printf("%nClearing temporary data...%n");
		int counter = 0;
		for (int i = 1; i <= fileNum; i++) {
			String fileName = getFileName(i);
			String path = String.format("%s\\%s", System.getProperty("user.dir"), fileName);
			
			File file = new File(path);
			if(file.exists()){
				file.delete();
				System.out.printf("Successfully deleted: %s%n", path);
				counter++;
			}
			else {
				System.out.printf("Failed to deleted: %s%n", path);
			}
		}
		return counter; 
	}

	/**
	 * The quit function closes the scanner class, than deletes the files created 
	 * and ends the program with a bye message.
	 * 
	 * @param	input			the scanner class
	 * @param	filesCreated	the amount of files created at the beginning of the program
	 */
	public static void quit(Scanner input, int filesCreated) {
		input.close();

		if (filesCreated > 0) {
			System.out.printf("%d files deleted.%n", deleteAllFile(filesCreated));
		}

		System.out.printf("%n%s%s%n%n%s%n",	"\033[1;32m", "Bye!!! 😊", borderLine);
		System.exit(0);
	}

}
