import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class TicketViewerTest {
  
	@Test
	@DisplayName("😊")
    public void testEmoji() {
		TicketViewer.quit();
    }
	
	@Test
    public void loginTest() throws IOException, InterruptedException, ParseException {
		TicketViewer.login();
		String path = String.format("%s\\%s", System.getProperty("user.dir"), "tickets.json");
		File file = new File(path);
		assertEquals(true, file.exists());
    }
    
    public static void main(String []args) {
    }

	@Test
    public void testGetAllFiles() {

	}
 
}