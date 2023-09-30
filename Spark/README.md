# Evaluation lab - Apache Kafka
## Group number: 12

## Group members

- Laura Colazzo
- Filippo Giovanni Del Nero
- Alessandro Meroni


## Assignment 

Three input datasets
1. citiesRegion
	- Type: static, csv file 
	- Fields: <u>city</u>, <u>region</u>
2. citiesPopulation
	- Type: static, csv file
	- Fields: <u>cid</u> (of the city),<u>city</u>, <u>population</u>
3. bookings
	- Type: dynamic, stream
	- Fields: <u>timestamp</u>, <u>value</u>
	- Each entry with value x indicates that someone booked a hotel in the city with id x

For all queries: limit unnecessary recomputations as much as possible!
- Q1: compute the total population for each region
- Q2: compute the number of cities and the population of the most populated city for each region
- Q3: Print the evolution of the population in Italy year by year until the total population in Italy overcomes 100M people
- Assume that the population evolves as follows:
	- In cities with more than 1000 inhabitants, it increases by 1% every year
	- In cities with less than 1000 inhabitants, it decreased by 1% every year 
- The output on the terminal should be a sequence of lines:
	- Year: 1, totalpopulation:xxx 
	- Year: 2, totalpopulation:yyy 
	- ...
- You may round the population of each city to the nearest integer during your computation
- Q4: compute the total number of bookings for each region, in a window of 30 seconds, sliding every 5 seconds