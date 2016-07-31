# AMA
Get to know each other's secrets. 

![alt tag](https://raw.githubusercontent.com/haukesand/AMA/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png)

Ask Me Anonymously is a prototype for an interactive ice breaking game on your smartphone developed for the course Mobile Information Systems at the Bauhaus University Weimar. It is inspired by the paper : "Design and Evaluation of a Multi-Player Mobile Game for Icebreaking Activity"by Pradthana Jarusriboonchai, Aris Malapaschas and Thomas Olsson from the Tampere University of Technology. 
The differences to the original idea are: 
- Use a multi-platform serverless communication protocol: AllJoyn by the AllSeen Alliance.
- Got rid of the initial question answering round that keeps players quietly on their phones for a few minutes.
- Have a deeper Question DB that is targeted for adults and multiple usages. 
- Allow players to add their own questions to the Database.
- Let players rate between DB questions and their custom question so that the question which most players are interested in gets asked.
- Less interactivity on the phone: Players answer the questions in public. 
- Spin the bottle: Only one random selected player has to answer the troublesome question. 
- Flickering animation between all phones that stops at one player.

All in all this game (if further developed) has the potential to be not a one-time app but a social game that players can keep on their phones. It allows for interaction moderated by the smartphone app. Therefore both strangers and friends might enjoy using the app. 

Next milestones: 
- Improve the question DB with a broader set.
- Improve animation to really toggle between screens and not flicker randomly. 
- Deeper Use of AllJoyn communication protocol instead of "hacked in" player counter. 
- Nicer layout instead. 

Further documentation is on the [Wiki](../../wiki). 
