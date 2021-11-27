import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.*;

/**
 * Zendesk Ticket Viewer
 *
 * @author Md Sakil Khan
 * @version 1.0
 */

public class TicketViewer{
	
    private static final String borderLine = String.format("%s%90s", "\033[1;30m", " ").replaceAll(" ", "x");
	
	private static final String fullListOptions = String.format("%n%s%s%n%s%n%s%n%s%n", "\033[0;36m",
											"Type \">\"           : next page",
											"Type \"<\"           : previous page",
											"Type \"{Ticket_ID}\" : to view detailed ticket information",
											"Type \"quit\"        : to exit program");
											
	private static final String openingOptions = String.format("%n%s%s%n%s%n%s%n", "\033[0;36m",
											"Type \"list\"        : to view full ticket list",
											"Type \"{Ticket_ID}\" : to view detailed ticket information",
											"Type \"quit\"        : to exit program");
	
	/**
	 * The main funtion starts with a welcome message asking the users
	 * to select options whether to display a list or a single ticket.
	 */
	public static void main(String []args) throws IOException, InterruptedException, ParseException {
		System.out.printf("%n%s%n%n%s%s%n", borderLine, 
							"\033[1;32m", "✨ Ticket Viewer ✨");

		login();

		System.out.printf("%s", openingOptions);
		Scanner input = new Scanner(System.in);					
		while (true) {			
			System.out.printf("%n\033[0;31m👉 ");
			String command = input.nextLine();
			
			try {				
				int id = Integer.parseInt(command);
				detailedTicket(id);
			}
			catch(Exception exc) {
				if (command.equals("quit")) {
					break;
				}
				else if (command.equals("list")) {
					fullListMenu();
				}
				else {
					System.out.printf("%s", openingOptions);
				}
			}
		}		
		input.close();
		quit();
	}

	/**
	 * This function allows user to enter the list view mode
	 * where they can view next page or pervious page or type in 
	 * numbers to display one detailed ticket.
	 */
	private static void fullListMenu() {
		int currentPage = 1;
		int totalPage = 4;
		
		fullListTable(currentPage, totalPage);
		System.out.printf("%s", fullListOptions);
		
		while(true){
			Scanner input = new Scanner(System.in);
			System.out.printf("%n\033[0;31m👉 ");
			String command = input.nextLine();
			
			try {
				int id = Integer.parseInt(command);
				detailedTicket(id);
			}
			catch (Exception exc) {
				if (command.equals("quit")) {
					input.close();
					quit();
				}
				else if (command.equals(">")) {
					if (currentPage < totalPage) {
						currentPage++;
						fullListTable(currentPage, totalPage);
						System.out.printf("%s", fullListOptions);
					}				
				}
				else if (command.equals("<")) {
					if (currentPage > 1) {
						currentPage--;
						fullListTable(currentPage, totalPage);
						System.out.printf("%s", fullListOptions);
					}
				}
				else {
					System.out.printf("%s", fullListOptions);
				}
			}
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
	private static void login() throws IOException, InterruptedException, ParseException {
		System.out.printf("%n\033[0;36mPlease login to continue%n");	
		
		int counter = 0;
		while(counter < 2) {
			counter = 0;
			Scanner input = new Scanner(System.in);
			System.out.printf("%n\033[0;31m{subdomain}👉 ");
			String domain = input.nextLine();
			System.out.printf("\033[0;31m{email_address}👉 ");
			String email = input.nextLine();
			System.out.printf("\033[0;31m{password}👉 ");
			String pass = input.nextLine();
			
			String ticketLink = String.format("curl https://%s.zendesk.com/api/v2/tickets.json -u %s:%s", domain, email, pass);
			System.out.printf("%nRunning... %s%n", ticketLink);
			
			ProcessBuilder processBuilder = new ProcessBuilder(ticketLink.split(" "));		
			var fileName = new File("ticket.json");        
			processBuilder.redirectOutput(fileName);
			var process = processBuilder.start();
			process.waitFor();
	
			try{
				Object obj = new JSONParser().parse(new FileReader("ticket.json"));
				JSONObject jo = (JSONObject) obj;
				if(jo.containsKey("tickets")){
					counter++;
				}
			}
			catch(Exception exc){}
	
			String countLink = String.format("curl https://%s.zendesk.com/api/v2/tickets/count -u %s:%s", domain, email, pass);
			System.out.printf("Running... %s", countLink);
	
			ProcessBuilder processBuilder2 = new ProcessBuilder(countLink.split(" "));		
			var fileName2 = new File("count.json");        
			processBuilder2.redirectOutput(fileName2);
			var process2 = processBuilder2.start();
			process2.waitFor();
			
			try{
				Object obj2 = new JSONParser().parse(new FileReader("count.json"));
				JSONObject jo2 = (JSONObject) obj2;
				if(jo2.containsKey("count")){
					counter++;
				}
			}
			catch(Exception exc){}

			if(counter < 2){
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
		System.out.printf("%nData retrieved!%n");
	}

	/**
	 * This function is not yet complete. It will display one detailed ticket.
	 * 
	 * @param	id	the integer id of the ticket that needs to be displayed.
	 */
	private static void detailedTicket(int id) {
		System.out.printf("%d%n", id);
	}

	/**
	 * This function is not yet complete. It will display the full list 
	 * 25 tickets per page.
	 *
	 * @param	currentPage		the integer currentPage tells us where to start the list from.
	 * @param	totalPage		the integer totalPage represents how many pages of 25 tickets are there.
	 */
	private static void fullListTable(int currentPage, int totalPage) {
		System.out.printf("%n\033[0mFull List page (%d/%d)", currentPage, totalPage);
		System.out.printf("%nblah blah...%n");
	}

	/**
	 * The quit function ends the program with a bye message.
	 */
	private static void quit() {
		System.out.printf("%n%s%s%n%n%s%n",
			"\033[1;32m", "Bye!!! 😊", borderLine);
		System.exit(0);
	}

}
