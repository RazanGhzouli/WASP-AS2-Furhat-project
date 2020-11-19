# WASP-AS2-Furhat-project
A repository for Furhat assignmnet  as part of WASP-AS2 course

## Running the project

1. Clone the GitHub repo to your "SDK" directory under ".furhat" working space.
2. Start IntelliJ and choose the open or import" option.
3. Navigate to the "KidsFriend" folder and press ok.
4. In the open projects part (left side of IntelliJ window) right click the "KidsFriend" folder and choose "Build Module "KidsFriend"".
5. Start your Furhat SDK server.
5. In IntelliJ, navigate to the "main.kt" file of the skill and press the green play button next to the main() method to start the skill.
6. Go to the web interface of Furhat server (using the default port, it will be hosted on http://localhost:8080 if your SDK server is running).
7. Go to the Dashboard using the left menu.
8. As you can see, there are two zones around Furhat (shown as grey circles in the situation view). When the user enters the inner zone, it is deemed to enter the interaction, and Furhat will start interacting with the user. To simulate this in the SDK, you can double-click on the situation view somewhere inside Furhat's inner interaction space to add a virtual user. This should trigger the interaction and allow you to test it.

## Singing Task 

Adding a song:
1. Specify the [song title] in the Song class in nlu.kt file (in /src/main/kotlin/furhatos/app/kidsfriend/nlu folder)
2. The [song title] and the lyric file ([song title].txt) must be the same
3. Add the lyric at /src/main/kotlin/furhatos/app/kidsfriend/text_files/lyrics/ folder


Script:\
B: Hello there!\
B: What do you want to do?\
H: what can you do\
B: Great! I can sing and quiz\
B: What do you want?\
B: I can sing baby shark and twinkle twinkle little star\
H: Twinkle Twinkle Little Star\
B: Twinkle Twinkle Little Star, what a lovely song!\
B: Do you know it?\
H: Maybe\
B: Okay, Let's take turn singing it!\
B: twinkle, twinkle, little star\
H: how I wonder what you are\
B: up above the world so high\
H: like a diamond in the sky\
B: twinkle, twinkle little star\
H: how I wonder what you are\
B: Great! What a collaboration!\
B: What do you want to do?\
H: no\
B: Okay, that's sad. Have a splendid day!

