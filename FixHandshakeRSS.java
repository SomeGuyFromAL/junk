
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

 

 

public class FixHandshakeRSS {

   static int events = 0;
   static String titles = "";
   static String[] eventNames;
   static String[] dateNames;
   static long[] datetimes;



   /**
    * Extracts the titles from a given rss feed and returns them as a string with one title per line.
    * @param address the url of the rss feed
    * @return
    */
   private static void getTitles(String address) {
      try {
         URL source = new URL(address);
         BufferedReader in = new BufferedReader(new InputStreamReader(source.openStream()));
         String line;
         String titles = "";
      
      
      
         while ((line = in.readLine()) != null) {
            int titleEndIndex = 0;
            int titleStartIndex = 0;
            while (titleStartIndex >= 0) {
               titleStartIndex = line.indexOf("<title>", titleEndIndex);
               if (titleStartIndex >= 0) {
                  titleEndIndex = line.indexOf("</title>", titleStartIndex);
                  titles += line.substring(titleStartIndex + "<title>".length(), titleEndIndex) + "\n";
               }
            }
         }
         in.close();
         FixHandshakeRSS.titles = titles.trim();
         events = titles.split("\r\n|\r|\n").length - 1;
      } catch (MalformedURLException e) {
         System.out.println("Invalid URL. Process Terminated.");
         System.exit(1);
      } catch (IOException e) {
         System.out.println("Content could not be retrieved. Process Terminated.");
         System.exit(2);
      }
   }



   /**
    * Seperates the dates and event names. Assumes the format will be: EVENT NAME (DATE).
    * The separation uses the index of the last '(' charater and trims the last character
    * (assumed to be ')' ) from the date.
    */
   private static void seperateDates() {
   
      eventNames = new String[events];
      dateNames = new String[events];
      Scanner t = new Scanner(titles);
      t.nextLine(); //skips content title line
      int i = 0;
      while (t.hasNextLine()) {
         String line = t.nextLine();
         int sep = line.lastIndexOf('(');
         eventNames[i] = line.substring(0, sep).trim();
         dateNames[i] = line.substring(sep+1, line.length()-1).trim();
         i++;
      }
      t.close();
   }



   /**
    * Converts the given String date to an int so that the date can be used by other systems.
    * Breaks the date up into useful constituent parts and then calculates the number of
    * milliseconds that have passed between the date and 1970-01-01 and returns that number.
    * @param s a String in the format Day, Month, XXth, X:XX am/pm - X:XX am/pm,
    * where X is any integer value
    * @throws ParseException 
    */
   private static long convertDateToLong(String s) {
      //remove end time from string
      String dt = s.substring(0, s.indexOf('-')).trim();
   
      //remove chars from number on the day
      String[] info = dt.split(" ");
      info[0] = info[0].substring(0, 3);
      info[1] = info[1].substring(0, 3);
      info[2] = info[2].replaceFirst("(st|nd|rd|th)$", "");
      if (info[4].length() == 4) { //add a leading 0 to the single digit hours
         info[4] = 0 + info[4];
      }
      if (info[2].length() == 1) { //add a leading 0 to the single digit hours
         info[2] = 0 + info[2];
      }
      dt = "";
      for (int i = 0; i < info.length; i++) {
         dt += info[i] + " ";
      }
      dt = dt.trim();
   
      // Define the date format pattern with a placeholder for the day and its suffix
      String dateFormatPattern = "EEE MMM dd yyyy, hh:mm aa";
   
   
   
      // Parse the date
      SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatPattern);
      Date date;
      try {
         date = dateFormat.parse(dt);
         return date.getTime();
      } catch (ParseException e) {
         System.out.println("Unexpected Date Format.");
         e.printStackTrace();
      }
      return 0;
   
   }
   
   /**
    * Calls convertDateToLong for each date string and stores the result in an array of longs.
    */
   private static void makeDateLongs() {
      datetimes = new long[events];
      for (int i = 0; i < dateNames.length; i++) {
         if (dateNames[i] != null) {
            datetimes[i] = convertDateToLong(dateNames[i]);
         }
      }
   }
   
   public static String fixThis(String url) {
      getTitles(url);
      seperateDates();
      makeDateLongs();
      
      String out = "";
      for (int i = 0; i < eventNames.length; i++) {
         out += "<title>" + eventNames[i] + "</title>\n<startdate>" + datetimes[i] + "</startdate>\n";
      }
      
      return out;
   }
   
   
   public static void main(String[] args) {
      String url = "https://auburn.joinhandshake.com/external_feeds/18121/public.rss?token=Ylf_yi4c2ZhdXpRRXY3QREKBlXgYajmtezFiXs6YanbJUpBUa7QODA";
      System.out.print(fixThis(url));
   }
}

