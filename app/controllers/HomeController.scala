package controllers

import java.util._;
import javax.inject._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Event

import com.amazonaws.auth._;
import com.amazonaws.services.cloudwatchevents.AmazonCloudWatchEventsClient;
import com.amazonaws.services.cloudwatchevents.model._;
/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() extends Controller {

    val AWS_ACCESS_KEY = "AKIAIWZ3ABJ2VCSV7IBQ";
    val AWS_SECRET_KEY = "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";
    
  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
   
   def eventForm = Form(mapping("name" -> nonEmptyText,
    "value" -> nonEmptyText)(Event.apply)(Event.unapply))
    
    
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
  
  def sendEvent= Action{
      
      
      implicit request =>
      eventForm.bindFromRequest.fold(
        formWithErrors => {Ok("Error")
            },
        eventData => {
        /* binding success, you get the actual value. */
            val newEvent = models.Event(eventData.name, eventData.value)
            
            val result=publishEvent(eventData.name, eventData.value)
            Ok(result)
              }
        )
    }
    
    
    
    def publishEvent(name :String,value:String):String={
        
        val accessKey =AWS_ACCESS_KEY;
        val secretKey = AWS_SECRET_KEY;

        
        val awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        val client = new AmazonCloudWatchEventsClient(awsCredentials);
        System.out.println("client: "+client.toString());
        
        
        
        val requestEntry = new PutEventsRequestEntry()
        .withTime(new Date())
        .withSource(name)
        .withDetailType("myDetailType")
        .withResources("resource1", "resource2")
        .withDetail("{\"state\": [\""+value+"\"]}");
        
        
      
         val request = new PutEventsRequest()
            .withEntries(requestEntry);
        
        var result = request.toString();
        
        result=result+"\nResponse:  "+client.putEvents(request);
        
        System.out.println(result.toString())
        
       /* for (PutEventsResultEntry resultEntry : result.getEntries()) {
            if (resultEntry.getEventId() != null) {
                System.out.println("Event Id: " + resultEntry.getEventId());
            } else {
                System.out.println("Injection failed with Error Code: " + resultEntry.getErrorCode());
            }
        }
        */
        
        return result.toString()
    }
    
    

}



/*


{
  "source": [
    "com.siprad"
  ],
  "detail-type": [
    "myDetailType"
  ],
  "detail": {
    "state": [
      "running"
    ]
  }
}
*/
