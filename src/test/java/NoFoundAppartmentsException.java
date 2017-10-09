
package sportahome;
import java.io.*;

 public class NoFoundAppartmentsException extends Exception 
 {
    private String text;
    public NoFoundAppartmentsException(String text)
    {
      this.text = text;
    }
    
    public String getText(){
      return text;
    }
   
 }