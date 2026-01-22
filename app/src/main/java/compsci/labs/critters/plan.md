# Plan

## Initialization

### Client

Client will init by taking a server address in as either a program argument or a constant.

### Server

Server will take a bind address, listening on a websocket.

## Architecture

Bi-Directional communication managed by each AnimalManager class - one assigned to each player
Server is single source of truth. 
AnimalManager classes are stateless, and only contain logic for getting the next move, color, and toString().
On each game loop, client executes it's classes, and sends the return values of getMove(), getColor(), and toString() to server. No untrusted code is run on server.
Game loop is:
 - Server sends state to each AnimalManager (Server-Side), which sends CritterInfo to client for processing
   - Use AnimalManager UUIDs to enable unique communication with client.
 - AnimalManager (Client-Side) executes getMove(), getColor(), and toString() and sends back the return values to server.
 - Server updates state based on return values from AnimalManager (Server-Side)

Execute in parallel where possible to speed up processing since we have multiple clients (new Thread for each AnimalManager)
Server doesn't send full game state to each client, just the changed state, reducing bandwidth.