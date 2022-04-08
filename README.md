## Description

This is project of chat application build using Java Socket. Beside basic communication between users, there is also possible to create private channels. Another feature is saving history of the chat in a file - after starting application the history is loaded to each client (each client has access to history of the channels which is member).

## Requirements

- JAVA 11
- Lombok

## Setup

To start the application you need to follow this steps:
1. Run the ChatServer file - specify the port number when running the server program from the command line.
2. Run client - in the command line you need to specify: localhost, port (the same as server port) and username. To connect more than 1 user just run again the client and specify different name.
3. Commands list from the application:
    - CREATE <group_name> - creates new group
    - JOIN <group_name> - joining to the group for the first time
    - SWITCH <group_name> - changing to another group where user is a member
    - QUIT - exit the application
    
