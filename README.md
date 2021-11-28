# Zendesk Ticket Viewer
By Sakil Khan <br />
11/26/2021

**Description** <br />
**Zendesk Ticket Viewer** is an application that allows user to login to their Zendesk account and view their collection of tickets. Users can view the tickets in a list or see a detailed information about a particular ticket. The Ticket Viewer is displayed in a shell but has a colorful and user-friendly aesthetic.

**Instructions/Dependencies** <br />
- I am on Windows 10
- I used JavaSE-11 jdk-11.0.11.9-hotspot
- Visual Studio Code v1.62.3
- Java Debugger 0.36.0 in VSCode **(make sure its version 0.36, the new 0.37 seems to be having issues displaying color or emojis)**
- included Jar files
  - json-simple-1.1.jar (needed to parse json file) <br />
    **link** http://www.java2s.com/Code/Jar/j/Downloadjsonsimple111jar.htm
  - org_junit_platform_junit-platform-console-standalone_1.6.0_junit-platform-console-standalone-1.6.0 (needed for unit testing) <br />
    **link** https://mvnrepository.com/artifact/org.junit.platform/junit-platform-console-standalone

**Assumptions** <br />
The program was created with certain assumptions and may run in to bugs if the assumptions are not true. <br />
1. Maximum tickets per page is 100 in the links (ex. api/v2/tickets.json?page=2 ). This can be changed in the code manually by sttring global variable ticketsPerPage. <br />
2. Each page is sorted by "id" value least to greatest (ex. 1-100 or 101-200) <br />

**Login** <br /> <br />
  <img src="https://github.com/mdsakilkhan/ZendeskCodingChallenge-TicketViewer/blob/main/progress%20images/login_output.jpg" />
  <br />

**Single Ticket** <br /> <br />
  <img src="https://github.com/mdsakilkhan/ZendeskCodingChallenge-TicketViewer/blob/main/progress%20images/single_ticket.jpg" />
  <br />

**List View** <br /> <br />
  <img src="https://github.com/mdsakilkhan/ZendeskCodingChallenge-TicketViewer/blob/main/progress%20images/list_view.jpg" />
  <br />


