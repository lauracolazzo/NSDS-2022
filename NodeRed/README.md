# Evaluation lab - Node-RED

## Group number: 12

## Group members

- Laura Colazzo 
- Alessandro Meroni
- Filippo Giovanni Del Nero

## Assignment
You are to implement a Telegram bot using Node- RED capable of answering four queries
- Return the average temperature in Milano or Hamburg based on the last ten readings of sensor.community
- Return the maximum and minimum humidity in Milano or Hamburg based on the last ten readings of sensor.community
- Return the list of user names the bot interacted with, until the time of the query
- Confirm if the bot ever interacted with a specific user

### Assumptions
- User names are explicitly communicated to the bot 
	- Example, from the user: “Hi, my name is Luca!”
- User names are unique
- You can format the queries the way you like
	- But you must format answers too!
- You can assume the bot is installed on a Node-RED machine that is up 24/7

## Solution - Description of message flow
In the implementation two flows that receive the MQTT notifications from the sensor.community have been included: one for Milan and one for Hamburg. The functions in these two flows updates the 10-value-long arrays for both the temperature and the humidity that are stored in the global context.

The main flow receives a message from telegram and the dispatcher redirects it to the correct function:
- "Hi, my name is xxxx": the name is saved in the flow context and a personalised greeting is sent to the user
  
- "What the average temperature is in Milan/Hamburg?": the msg is redirected either to the Hamburg or Milan temperature node, there the temperature array is retrieved from the context and the average is computed on the values that are available at that moment, if the array is empty the user is asked to wait
  
- "What the humidity is in Milan/Hamburg?": the message is redirected either to the Hamburg or Milan humidity node, there the humidity array is retrieved from the context and the max and min are computed on the values that are available at that moment, if the array is empty the user is asked to wait
  
- "Who did you talk to?": the users array with all previous interactions is retrieved, formatted and sent to the user in the “query names” function
  
- "Have you ever talked to xxxx?": the name is searched in the users array and, if found, the reply to the user is sent to tell him the chatbot previously talked to him/her, otherwise the reply tells him the chatbot never talked to him
  
- Default: the chatbot replies saying he doesn't understand the message it received


