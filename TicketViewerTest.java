import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import org.junit.Test;

/**
 * Zendesk Ticket Viewer Unit Tests
 *
 * @author Sakil Khan
 * @version 1.0
 */

public class TicketViewerTest {

	/*
	Example Ticket Json
						{"tickets": [{
							"id": 10,
							"subject": "sakil"
							}],
						"count": 2,
						"url": "a.com"}
	*/
	private final static String ticketExample = "{\"tickets\":[{\"id\": 23,\"subject\":\"sakil\"}],\"count\": 2,\"url\": \"a.com\"}";
		
	@Test
	public void testCreateFiles() {
		// testing working link
		assertTrue(TicketViewer.createFile("curl https://a.zendesk.com/api/v2/tickets.json", "a.txt"));
		
		// testing non executable code link 
		assertFalse(TicketViewer.createFile("https://a.zendesk.com/api/v2/tickets.json", "a.txt"));
	}
	
	@Test
	public void testFileHasTickets() throws FileNotFoundException, UnsupportedEncodingException {
		// testing file with tickets
		PrintWriter writer = new PrintWriter("a.json", "UTF-8");
		writer.println(ticketExample);
		writer.close();
		assertTrue(TicketViewer.fileHasTickets("a.json"));
		
		// testing no extention
		writer = new PrintWriter("b", "UTF-8");
		writer.println(ticketExample);
		writer.close();
		assertTrue(TicketViewer.fileHasTickets("b"));
				
		// testing file exist but does not have tickets	
		writer = new PrintWriter("c.json", "UTF-8");
		writer.println("hi");
		writer.close();
		assertFalse(TicketViewer.fileHasTickets("c.json"));
		
		// testing if file DNE
		assertFalse(TicketViewer.fileHasTickets("d.json"));
	}
	
	@Test
	public void testGetFromJson() throws FileNotFoundException, UnsupportedEncodingException {		
		String fileName = "a.txt";
		PrintWriter writer = new PrintWriter(fileName, "UTF-8");
		writer.println(ticketExample);
		writer.close();
		
		// testing getLongFromJson
		assertEquals(2, TicketViewer.getLongFromJson(fileName, "count"));

		// testing getStringFromJson
		assertEquals("a.com", TicketViewer.getStringFromJson(fileName, "url"));

		// testing getStringFromJson
		assertEquals(23, TicketViewer.getTicketInfoLong(fileName, "id", 0));

		// testing getStringFromJson
		assertEquals("sakil", TicketViewer.getTicketInfoString(fileName, "subject", 0));
	}
	
	@Test
	public void testGetFileName(){
		assertEquals("tickets125.json", TicketViewer.getFileName(125));
		assertEquals(null, TicketViewer.getFileName(0));
		assertEquals("tickets.json", TicketViewer.getFileName(1));
	}
		
	@Test
	public void testFullListTable() {
		assertEquals(false, TicketViewer.fullListTable(0, 5));
		assertEquals(false, TicketViewer.fullListTable(7, 3));
		assertEquals(true, TicketViewer.fullListTable(132, 1244));
	}

	@Test
	public void testDeleteFile() throws IOException, FileNotFoundException {		
		// makes sure files exist to test if deleteFile is working
		File file = new File("a.txt");
		file.delete();
		assertEquals(true, file.createNewFile());
		
		TicketViewer.deleteFile("a.txt");
		assertFalse(file.exists());

		// testing if file DNE
		assertFalse(TicketViewer.deleteFile("a.txt"));
	}
 
}  
