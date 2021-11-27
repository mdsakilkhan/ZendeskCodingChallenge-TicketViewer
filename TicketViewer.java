import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Zendesk Ticket Viewer
 *
 * @author Md Sakil Khan
 * @version 1.0
 */

public class TicketViewer{
	
	private static int numOfFiles = 0;

	private static int totalPage = 0;

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
	
	/**
	 * The main funtion controls the entire session. It will start with a welcome 
	 * message asking the users to provide login credentials. Once verified users 
	 * can select options whether to display a list or a single ticket or exit program.
	 */
	public static void main(String []args) throws IOException, InterruptedException, ParseException {
		int currentPage = 1;

		System.out.printf("%n%s%n%n%s%s%n", borderLine, "\033[1;32m", "✨ Ticket Viewer ✨");

		login();

		System.out.printf("%s", openingOptions);

		// used to get total tickets count
		Object obj = new JSONParser().parse(new FileReader("tickets.json"));
		JSONObject jo = (JSONObject) obj;		
		int totaltickets = ((Long) jo.get("count")).intValue();
		TicketViewer.totalPage = (int) Math.ceil(totaltickets/25.0);
		
		
		int menuFlag = 0; 	// 0-main menu 1-full list menu
		Scanner input = new Scanner(System.in);	
		while (true) {			
			System.out.printf("%n\033[0;31m👉 ");
			String command = input.nextLine();
			
			try {				
				int id = Integer.parseInt(command)-1;
				detailedTicket(id);
			}
			catch(Exception exc) {
				if (command.equals("quit")) {
					break;
				}
				else if (command.equals("list")) {
					fullListTable(currentPage);
					menuFlag = 1;
				}
				else if (menuFlag > 0) {
					if (command.equals(">") && currentPage < TicketViewer.totalPage) {
						currentPage++;
						fullListTable(currentPage);
					}
					else if (command.equals("<") && currentPage > 1) {
						currentPage--;
						fullListTable(currentPage);
					}
				}
			}

			if(menuFlag == 0) {
				System.out.printf("%s", openingOptions);
			}
			else {
				System.out.printf("%s", fullListOptions);
			}
		}

		input.close();
		deleteAllFile();
		quit();	
	}

	/**
	 * This funtion deletes the temporary ticket files that were created.
	 *
	 * @returns  0	if file was not deleted
	 * @returns  1	if file deleted successfully
	 */
	public static int deleteAllFile() {
		try{
			String fileName;
			for (int i = 1; i <= TicketViewer.numOfFiles; i++) {
				if(i==1) {
					fileName = "tickets.json";
				}
				else {
					fileName = String.format("tickets%d.json", i);
				}
				String path = String.format("%s\\%s", System.getProperty("user.dir"), fileName);
				(new File(path)).delete();
				System.out.println(path);
			}
			return 1;
		}
		catch(Exception exc) {
			return 0;
		}
	}

	/**
	 * this is the login function where users are prompted to enter 
	 * their login info inorder to use curl to get the tickets and also count.
	 * I realized that tickets.json already contains count so that will be removed
	 * I have also notice that there are more tickets at ?page2 so i will need
	 * to add that json file also.
	 *
	 * Once logged in properly the JSONParser class will get all the individual 
	 * data from the files for the display. Since I am creating temporary files
	 * no need to save user login info.
	 */
	public static void login() throws IOException, InterruptedException, ParseException {
		System.out.printf("%n\033[0;36mPlease login to continue%n");	
		
		while(true) {
			Scanner input = new Scanner(System.in);
			System.out.printf("%n\033[0;31m{subdomain}👉 ");
			String domain = input.nextLine();
			System.out.printf("\033[0;31m{email_address}👉 ");
			String email = input.nextLine();
			System.out.printf("\033[0;31m{password}👉 ");
			String password = input.nextLine();

			String ticketLink = String.format("curl https://%s.zendesk.com/api/v2/tickets.json -u %s:%s", domain, email, password);
			System.out.printf("%nRunning... %s", ticketLink);
			
			// forks a process to put content of the api in to a json file
			ProcessBuilder processBuilder = new ProcessBuilder(ticketLink.split(" "));
			var fileName = new File("tickets.json");
			processBuilder.redirectOutput(fileName);
			var process = processBuilder.start();
			process.waitFor();
	
			try {
				Object obj = new JSONParser().parse(new FileReader("tickets.json"));
				JSONObject jo = (JSONObject) obj;
				
				// varifies if the api has tickets or error message
				if(jo.containsKey("tickets")) {
					getAllFiles(email, password);
					break;
				}
			}
			catch(Exception exc){
				System.out.printf("%n%n%s%s%n%s%n%s%n%s%n%n", "\033[0;36m",
						"Couldn't authenticate you",
						"Would you like to try again?",
						"Press any key  : to try again",
						"Type \"quit\"    : to exit program");
				
				System.out.printf("\033[0;31m👉 ");	
				String command = input.nextLine();	

				if (command.equals("quit")) {
					input.close();
					quit();
				}
			}		
		}

		Object obj = new JSONParser().parse(new FileReader("tickets.json"));
		JSONObject jo = (JSONObject) obj;
		System.out.printf("%n\033[0mData retrieved!%nYou have: %d Tickets%n", jo.get("count"));
	}

	/**
	 * This function creates files for any other links with tickets.
	 * 
	 * @param	email	the users email.
	 * @param	pass	the users password.
	 */
	public static void getAllFiles(String email, String pass) throws FileNotFoundException, IOException, ParseException, InterruptedException {
		Object obj = new JSONParser().parse(new FileReader("tickets.json"));
		JSONObject jo = (JSONObject) obj;
		String next_page = (String) jo.get("next_page");
		
		TicketViewer.numOfFiles = 1;
		while (next_page != null) {
			System.out.printf("%nGetting files from... %s%n", next_page);
			TicketViewer.numOfFiles += 1;
			String ticketLink = String.format("curl %s -u %s:%s", next_page, email, pass);
			
			ProcessBuilder processBuilder = new ProcessBuilder(ticketLink.split(" "));
			var fileName = new File(String.format("tickets%d.json", TicketViewer.numOfFiles));
			processBuilder.redirectOutput(fileName);
			var process = processBuilder.start();
			process.waitFor();	

			Object obj1 = new JSONParser().parse(new FileReader(String.format("tickets%d.json", TicketViewer.numOfFiles)));
			JSONObject jo1 = (JSONObject) obj1;
			next_page = (String) jo1.get("next_page");
		}
	}

	/**
	 * This function displays one detailed ticket. Using the JSONParser class we can get 
	 * data that we want from the temporary json files that were created in the login process.
	 * 
	 * @param	id	the integer id of the ticket that needs to be displayed.
	 */
	public static void detailedTicket(int id) throws FileNotFoundException, IOException, ParseException {
		String fileName;
		for (int i = 1; i <= TicketViewer.numOfFiles; i++) {
			if(i==1) {
				fileName = "tickets.json";
			}
			else {
				fileName = String.format("tickets%d.json", i);
			}

			Object obj = new JSONParser().parse(new FileReader(fileName));
			JSONObject jo = (JSONObject) obj;
			JSONArray array = (JSONArray)jo.get("tickets");

			try {
				JSONObject ajo = (JSONObject) array.get(id-100*(i-1));
				
				Long submitter_id = (Long) ajo.get("submitter_id");

				String created_at = (String) ajo.get("created_at");
				created_at = created_at.substring(0, 10);
				
				String updated_at = (String) ajo.get("updated_at");
				updated_at = updated_at.substring(0, 10);

				String subject = (String) ajo.get("subject");
				if (subject.length() > 73) {
					subject = String.format("%s...", subject.substring(0, 70));
				}

				String description = (String) ajo.get("description");

				System.out.printf("%n%s%n%s%-70d%n%s%-70s%n%s%-70s%n%s%-70s%n%s%n%n", tableBorderLine, 
											"Submitter ID : ", submitter_id,  
											"Created Date : ", created_at,   
											"Updated Date : ", created_at, 
											"Subject      : ", subject, 
											"Description  :- ");
				
				for (int j = 0; j < description.length(); j++) {
					System.out.printf("%s", description.charAt(j));
					if(j%87 == 0 && j != 0){
						System.out.printf("%n");
					}
				}
				System.out.printf("%n%n%s%n", tableBorderLine);

				break;
			} 
			catch (Exception exc) {}
		}		
	}

	/**
	 * This function displays the full list of 25 tickets per page depending on the current 
	 * page number. Than puts the data in a table.
	 *
	 * @param	currentPage		the integer currentPage tells us where to start the list from.
	 */
	public static void fullListTable(int currentPage) throws FileNotFoundException, IOException, ParseException {
		System.out.printf("%n\033[0m%s (%d/%d)%n%s%n| %s | %s | %-40s | %-16s |%n%s%n", 
							"Full List Page", currentPage, TicketViewer.totalPage, tableBorderLine, 
							"Ticket ID", "Created Date", "Subject", "Submitter ID", tableBorderLine);

		String fileName;
		int fileNum = (int) Math.ceil(currentPage/4.0);
		
		if(fileNum == 1) {
			fileName = "tickets.json";
		}
		else {
			fileName = String.format("tickets%d.json", fileNum);
		}

		Object obj = new JSONParser().parse(new FileReader(fileName));
		JSONObject jo = (JSONObject) obj;
		JSONArray array = (JSONArray)jo.get("tickets");
			
		for (int i = (currentPage-1)*25; i < (currentPage-1)*25+25; i++) {
			try {
				JSONObject ajo = (JSONObject) array.get(i-100*(fileNum-1));
				Long id = (Long) ajo.get("id");
				String created_at = (String) ajo.get("created_at");
				String subject = (String) ajo.get("subject");
				Long submitter_id = (Long) ajo.get("submitter_id");

				created_at = created_at.substring(0, 10);
				if (subject.length() > 40) {
					subject = String.format("%s...", subject.substring(0, 37));
				}

				System.out.printf("| %-9d | %-12s | %-40s | %-16d |%n", id, created_at, subject, submitter_id);
			} catch (Exception exc) {}
		}
		System.out.println(tableBorderLine);
	}

	/**
	 * The quit function ends the program with a bye message.
	 */
	public static void quit() {
		System.out.printf("%n%s%s%n%n%s%n",	"\033[1;32m", "Bye!!! 😊", borderLine);
		System.exit(0);
	}

}
