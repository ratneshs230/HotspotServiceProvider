const functions = require('firebase-functions');
const admin = require('firebase-admin');
const { DataSnapshot } = require('firebase-functions/lib/providers/database');
admin.initializeApp(functions.config().firebase);

var database=admin.database();

exports.grantSignupReward = functions.database.ref('Partner/{uid}/referredBy')
    .onCreate((snapshot,context) => {
      
      if(snapshot.exists){
        console.log("snapshot exists"+snapshot.val())
      }else 
      console.log("datasnapshot doesNot exist")


      const referralKey=snapshot.val();
      const userId=context.params.uid;

      database.ref("Partner/{userId}").orderByChild("referralCode").equalTo(referralKey)
      .once('value').then(datasnapshot => {
 
        if(datasnapshot.exists){
          console.log("Second snapshot exists"+datasnapshot.)  
        }else 
        console.log("Second datasnapshot doesNot exist")
  
       var referredId=context.params.userId;

        console.log(userId+" is referred By "+referredId);

   if(referrerId===null)
        return 0 // I assume you don't want to return undefined
        else
        return referrerId
    }).catch(console.error("Error"));
    
  })
