# Quiz application developed for Android in Kotlin programming language

What you can do in this app?
<br/><br/>
**Select one of interesting topics with 10 questions which was generated and fetched from open trivia database (https://opentdb.com/) or create your own quiz with own questions. Also you can edit already existing questions.**
<br/><br/>
<img src="https://github.com/kura1aym/AndroidProject/assets/113928333/6ff3d701-3d43-4b28-8a62-ddd724c32a5a" height="320">
<img src="https://github.com/kura1aym/AndroidProject/assets/113928333/4b443c40-50c1-4677-a494-d7fd19c2276a" height="320">
<img src="https://github.com/kura1aym/AndroidProject/assets/113928333/e808e299-d354-47aa-b9ad-659a68061dd9" height="320">
<br/><br/>
**Questions have two types: multiple choices or short answer questions. All short answer questions fetched from an api only true or false questions, but your own short answer questions can accept any answer. 
Try quiz by pressing play button, you can answer questions or skip them.**
<br/><br/>
<img src="https://github.com/kura1aym/AndroidProject/assets/113928333/557115ea-f166-4078-8329-2595dd461cb9" height="320">
<br/><br/>
**At the end see your score and the leaderboard**
<br/><br/>
<img src="https://github.com/kura1aym/AndroidProject/assets/113928333/58b1d49a-42bf-45f7-8fb3-fb2b16f2de64" height="320">
<img src="https://github.com/kura1aym/AndroidProject/assets/113928333/0f7681ca-4aed-4279-b592-1e6023ea90af" height="320">
<br/><br/>
*All screenshots are in dark mode.

**Project Features:**
Check Android Manifest xml where application and activities are listed. The first entry point of the project is Quiz Application. 
1. UI
    - Used Recycler View to show multiple choices, show list of players in leaderboard, show quizzes and etc. Used floating button actions, material design, light and dark themes to enhance ui. 
2. Data Management: The data layer encapsulates the details of data sources.
   - QuizRoomDatabase:
     - This class serves as the entry point for accessing the underlying SQLite database using Room. 
     - Annotated with @Database, it defines the database entities (Quiz and Leaderboard), version number, and whether to export the schema. 
   - QuizDao:
     - Annotated with @Dao, it defines methods for accessing and manipulating data in the database. 
     - Contains methods like insert, update, and delete for CRUD operations on both Quiz and Leaderboard entities. 
     - Queries such as getQuizData() and getLeaderboardData() are defined to retrieve data from the respective tables.
3. Networking: Part of the data layer 
    - We get list of 10 questions periodically from https://opentdb.com/api.php?amount=10 and store them in room database. We implemented error handling by using try and catch blocks and pagination by limiting number of questions (data) we load from an api.
4. Concurrency
   - We used suspend functions to fetch questions from an api
5. Clean Architecture
   - We divided our project into 3 main layers: data for databases and networking (api), domain for business logic and use cases and ui layer.
6. Design Patterns
   - We used Observable to observe LiveData objects (viewModel.listQuestion and viewModel.deleteButtonStatus) within the ViewModel. When the data within these LiveData objects changes, the corresponding UI elements are updated accordingly.
7. Documentation 
8. Additional features:
   - We integrated camera into our project to set the interesting images iin quizzes. 






