import java.util.Scanner;

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
	public static void main(String []args){
		System.out.printf("%n%s%n%n%s%s%n%s", borderLine, 
							"\033[1;32m", "✨ Ticket Viewer ✨", openingOptions);
							
		while (true) {
			Scanner input = new Scanner(System.in);
			System.out.printf("%n\033[0;31m👉 ");
			String command = input.nextLine();

			try {
				int id = Integer.parseInt(command);
				detailedTicket(id);
			}
			catch(Exception exc) {
				if (command.equals("quit")) {
					input.close();
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
		quit();
	}

	/**
	 * The quit function ends the program with a bye message.
	 */
	private static void quit() {
		System.out.printf("%n%s%s%n%n%s%n",
			"\033[1;32m", "Bye!!! 😊", borderLine);
		System.exit(0);
	}

	/**
	 * This function is not yet complete. It will display one detailed ticket.
	 * 
	 * @param	the integer id of the ticket that needs to be displayed.
	 */
	private static void detailedTicket(int id) {
		System.out.printf("%d%n", id);
	}

	/**
	 * This function is not yet complete. It will display the full list 
	 * 25 tickets per page.
	 *
	 * @param	the integer currentPage tells us where to start the list from.
	 * @param	the integer totalPage represents how many pages of 25 tickets are there.
	 */
	private static void fullListTable(int currentPage, int totalPage) {
		System.out.printf("%n\033[0mFull List page (%d/%d)", currentPage, totalPage);
		System.out.printf("%nblah blah...%n");
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
}