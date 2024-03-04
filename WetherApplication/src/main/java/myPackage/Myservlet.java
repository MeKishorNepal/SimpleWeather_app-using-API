package myPackage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@WebServlet("/MyServlet")
public class Myservlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
   
    public Myservlet() {
        super();
       
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	 try {
		
	      //API setup
	      String apikey="cc5faec7344469bc25914e7fc0b6bd0e";
	      //get the city from input
	      String city=request.getParameter("city");
	    //URL encode the city parameter
	      String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
	      //create the url for the openweathermap api request
	      String apiUrl="https://api.openweathermap.org/data/2.5/weather?q="+ encodedCity +"&appid="+ apikey ;
	      
	      //api integration
	      URL url=new URL(apiUrl);
	      
	      HttpURLConnection connection=(HttpURLConnection) url.openConnection();
	      connection.setRequestMethod("GET");
	      
	      //reading data from network
	      InputStream inputStream=connection.getInputStream();
	      InputStreamReader reader=new InputStreamReader(inputStream);
	      //System.out.println(reader);
	      
	      Scanner scanner=new Scanner(reader);
	      StringBuilder responseContent=new StringBuilder();
	      
	      
	      while(scanner.hasNext()) {
	    	  responseContent.append(scanner.nextLine());
	      }
	      scanner.close();
	      
	      //typecasting  parsing the data into json
	      Gson gson=new Gson();
	      JsonObject jsonObject=gson.fromJson(responseContent.toString(),JsonObject.class);
	      System.out.println(jsonObject);
	      
	      //Date and Time
	      long dateTimestamp=jsonObject.get("dt").getAsLong()*1000;
	      String date=new Date(dateTimestamp).toString();
	      
	      //Temperature
	      double temperatureKelvin=jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
	      int temperatureCelsius=(int)(temperatureKelvin-273.15);
	      
	      //Humidity
	      int humidity=jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
	      
	      //Wind speed
	      double windSpeed=jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
	      
	      //Weather condition
	      String weatherCondition=jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
	      
	      
	      //set the data as request attributes (for sending to the jsp page)
	      request.setAttribute("date", date);
	      request.setAttribute("city", city);
	      request.setAttribute("temperature", temperatureCelsius);
	      request.setAttribute("weatherCondition", weatherCondition);
	      request.setAttribute("humidity", humidity);
	      request.setAttribute("windSpeed", windSpeed);
	      request.setAttribute("weatherData", responseContent.toString());
	      
	      
	      connection.disconnect();
	      
	 }catch(IOException e) {
		 e.printStackTrace();
	 }
	 
	 
	 //Forward the request to the weather.jsp page for rendering
	 request.getRequestDispatcher("index.jsp").forward(request, response);
	      
		
	}

}
