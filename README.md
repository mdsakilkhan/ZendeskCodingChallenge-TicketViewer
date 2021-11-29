# Zendesk Ticket Viewer
**By Sakil Khan** <br />
11/26/2021

## Description
**Zendesk Ticket Viewer** is an application that allows user to login to their Zendesk account and view their collection of tickets. Users can view the tickets in a list or see a detailed information about a particular ticket. The Ticket Viewer is displayed in a shell but has a colorful and user-friendly aesthetic.

## Repository breakdown
jar files : includes two .jar files I used for the program <br />
progress images : images used in the readme.md <br />
readme.md : which is me, hi! 😄 <br />
**TicketViewer.java** : the main program, all-in-one, contains all the necessary functions to run the Zendesk Ticket Viewer
TicketViewerLite.java : same as TicketViewer.java however emojis and color have been taken out 
TicketViewerTest.java : includes unit test Java file

## Instructions & Dependencies
- The program was run and tested on a **Windows 10** OS
- With **JavaSE-11 (jdk-11.0.11.9-hotspot)**
- Using IDE **Visual Studio Code (v1.62.3)**
- **Java Debugger Extention (v0.36.0)** in VSCode <br />
  Please use version 0.36, the new 0.37 seems to be having issues displaying color or emojis. If issues with color persists or if color codes appears *(ex. "33\[0m")* before text, please used the lite version of the program.
- **Must have Jar files** : other versions will likely work however the included the .jar files are what were used in the program and links to where they can be found is also provided. <br />
  - json-simple-1.1.jar : needed to parse json file <br />
    **link** http://www.java2s.com/Code/Jar/j/Downloadjsonsimple111jar.htm
  - org_junit_platform_junit-platform-console-standalone_1.6.0_junit-platform-console-standalone-1.6.0 (needed for unit testing) <br />
    **link** https://mvnrepository.com/artifact/org.junit.platform/junit-platform-console-standalone
- **Reference Jar files in VSCode** : as can be seen in the image below in VSCode (located in the bottom left corner of a Java project) the two files need to be added to **referenced libraries**. <br />
  <img src="https://github.com/mdsakilkhan/ZendeskCodingChallenge-TicketViewer/blob/main/progress%20images/references.jpg" /> <br />
- The main program and the test program were run through debugger extention however the specific run command can be seen in the image below. <br />
  <img src="https://github.com/mdsakilkhan/ZendeskCodingChallenge-TicketViewer/blob/main/progress%20images/run_command.jpg" /> <br /> 

## Assumptions
The program was created with certain assumptions and may run in to bugs if the assumptions are not true. <br />
1. Maximum tickets per page is 100 in the links *(ex. api/v2/tickets.json?page=2 ).* This can be changed in the code manually by changing the global variable ticketsPerPage. <br />
2. Each page is sorted by "id" value least to greatest (ex. 1-100 or 101-200) <br />

## Program Images
**Login Failed** <br /> <br />
  <img src="https://github.com/mdsakilkhan/ZendeskCodingChallenge-TicketViewer/blob/main/progress%20images/login_failed.jpg" width="600" /> <br />

**Login Successful** <br /> <br />
  <img src="https://github.com/mdsakilkhan/ZendeskCodingChallenge-TicketViewer/blob/main/progress%20images/login_successful_censored.jpg" width="600" /> <br />

**Single Ticket Display** <br /> <br />
  <img src="https://github.com/mdsakilkhan/ZendeskCodingChallenge-TicketViewer/blob/main/progress%20images/single_ticket_1.jpg" width="600" /> <br />
  <img src="https://github.com/mdsakilkhan/ZendeskCodingChallenge-TicketViewer/blob/main/progress%20images/single_ticket_2.jpg" width="600" /> <br />

**List View** <br /> <br />
  <img src="https://github.com/mdsakilkhan/ZendeskCodingChallenge-TicketViewer/blob/main/progress%20images/list_view_1.jpg" width="600" /> <br />
  <img src="https://github.com/mdsakilkhan/ZendeskCodingChallenge-TicketViewer/blob/main/progress%20images/list_view_2.jpg" width="600" /> <br />

**Quit Sequence** <br /> <br />
  <img src="https://github.com/mdsakilkhan/ZendeskCodingChallenge-TicketViewer/blob/main/progress%20images/quit.jpg" width="600" /> <br />

**Unit Test Output** <br /> <br />
  <img src="https://github.com/mdsakilkhan/ZendeskCodingChallenge-TicketViewer/blob/main/progress%20images/unit_test_results.jpg" width="300" /> <br />

## Dear Reviewer
**Thank you!!!** so much for taking the time to review my program. If you have any questions or concerns please feel free to contact me at *sakil2468@gmail.com*. Hope you have a blessed day. <br /> <br />
Warm regards, <br /> 
Sakil <br /> <br />

![](https://c.tenor.com/ra9DFVk9xzYAAAAC/thank-you.gif) <br />

