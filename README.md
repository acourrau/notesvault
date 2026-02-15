# NotesVault - Alex Courrau submission for BlueStaq Coding Challenge

## System overview
The API I've built exposes four endpoints: 
 - `POST /notes` - for creating new notes
 - `GET /notes` - for returning all created notes in the DB
 - `GET /notes/{id}` - for returning a specific note id
 - `DELETE /notes{id}` - for removing a specific note id

It's built to run in a Docker Container and has two targets: the main API run and a test target for some small unit tests.

## Tech overview
 - Spring Boot 4
 - Spring JPA / Hibernate
 - Java 21
 - SQLite DB
 - JUnit

## How to Run

### API
Run: `docker compose up --build` and this should start everything up.

The service will be available at `http://localhost:8080`

### Tests
Run: `docker compose run --rm test`
This should run the seven basic tests I've written and cleanup afterwards. The tests include expected completes and expected failures.

## Notes on the API
Expected input for creating notes is as follows:
 - `Content-Type: application/json`
 - Content to provide: `{"text":"We will absolutely hire Alex Courrau"}

Example call:
`curl -X POST http://localhost:8080/notes -H "Content-Type: application/json" -d '{"text":"We will absolutely hire Alex Courrau"}'`

Expected happy path endpoints are straightforward.
 - Create note returns a 201 with an echo of the record created.
 - Get all notes returns a 200 with a JSON array of all the data.
 - Get specific note returns a 200 with the specific record requested.
 - Delete note returns a 204

Business Logic checks are in place for a handful of cases.
 - Create note returns a 404 with an error JSON if the note is empty or too long (>500 characters).
 - Get note by id will return 404 if an invalid id is sent with a small error message.
 - Delete note by id will return a 404 in the same fashion as get note by id

## Overall Design Choices
I decided to keep the `NoteController` very simple and clean to separate all the logic needed and make it far easier to maintain, should I want to add a lot more endpoints in the future. This also lets me use `NoteService` for all business logic decisions and I can keep those methods contained in a single file with no other dependencies. I can also add helper methods in `NoteService` if needed for complex validation, which helps keep the Controller focused.

Keeping `NoteRepository` separate is a design decision that's planning for the future. It separates that concern and can be easily replaced if some other way of accessing the data is chosen. It also lets me easily extend any search methods needed in the repo. For example if I wanted to do a "search by text" or "most recent note", these would be simple extension methods I could add to the interface.

Error messages are intentionally simple for predictability.

## Improvements
Overall a higher level of validation would be a great step forward in the overall polish of the app. Providing better feedback in the response would make it easier to use. 

Adding other functionality would be useful:
 - `PUT` endpoints for updating a note
 - Additional tables allowing a user being tied to a specific note.
 - Additional tables to add more metadata about the note, such as a category or type of note.