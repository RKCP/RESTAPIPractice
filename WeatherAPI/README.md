# Weather API Application

This is a Java application that provides weather information using data stored in MongoDB. It allows you to retrieve weather details for specific locations without making repeated API calls.

## Features and Design Choices

- **Loading JSON Weather Data into MongoDB**: Upon starting the application, it loads JSON weather data into MongoDB. This preloading of data allows us to avoid making multiple API calls just to fetch the latitude and longitude of a location.

- **Thymeleaf for HTML Generation**: The application uses Thymeleaf, a Java-based templating engine, to generate HTML pages. Thymeleaf provides a convenient way to integrate server-side and client-side rendering, making it easier to present weather information in a user-friendly format.

## Prerequisites

Before running the application, make sure you have the following dependencies installed:

- Java Development Kit (JDK)
- Docker and Docker Compose

## Running the Application

Follow these steps to run the Weather API application:

1. Clone the repository:

   ```bash
   git clone https://github.com/your-username/weather-api.git
   ```
2. Change into the project directory:

   ```bash
   cd WeatherApi
   ```
3. Open Docker Desktop and then start the MongoDB container using Docker Compose (This will spin up a MongoDB container running on your local machine and will be visibile in Docker Desktop):

   ```bash
   docker-compose up -d
   ```

4. Build the application using Maven:

   ```bash
   mvn clean install
   ```  
5. Start the Weather API application:

   ```bash
   mvn spring-boot:run
   ```  

This will start the application, load the JSON weather data into MongoDB, and make the API available at http://localhost:8080.


6. Open your web browser and visit http://localhost:8080 to access the Weather API. You can type in a city name and decide weather you want the current weather or a five day forecast, based on the buttons below the search bar.

Please note that you need to have Docker and Maven installed on your system for the above steps to work correctly.  