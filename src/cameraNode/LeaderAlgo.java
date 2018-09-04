package cameraNode;

import java.util.ArrayList;
import javax.swing.Timer;

public class LeaderAlgo {
	 private final int DEBUG_LEVEL = 2;      
	 private final int SHORT_TIMEOUT = 250;
	 private final int LONG_TIMEOUT = 30000;
	 Timer burstTimer, bwTimer;
	 private ArrayList<Integer> cameraFlags = new ArrayList<Integer>();
	 private int sumOfCamflagDifferences[] = {-1, -1};
	 private int sumOfDerivatives[] = {-2, -2};

	    ///////////////////////////////////////////////////////////////////
		///// following methods only used by Swarm Leader in BW control////
		/////														   ////
		///////////////////////////////////////////////////////////////////
   synchronized int requestLeaderBandwidthChange(int camId, int request){
	   if (camId >= cameraFlags.size()) return -1;  		// protect against network rediscovery process; receiver will ignore
	   final int currentFlagValue = cameraFlags.get(camId).intValue();     // current flag value of requesting camera

	   if (burstTimer != null && burstTimer.isRunning()) return currentFlagValue; //  do nothing if in "burst supression" mode
	   if (request == -1 && bwTimer != null && bwTimer.isRunning()) return currentFlagValue;

	   // 
	   //	The next line the heart of the algorithm, implementing the following logic:
	   // if (camVal < currentFlagValue && request is to reduce BW) || (camVal > currentFlagValue && request is to increase BW) then 
	   //       the response is false - you cannot change BW in these conditions. This is in the "allMatch" clause of the filter
	   // It is preceded by a filter where flag values of -1 are ignored (priority) as are flag values equal to the test value 

	   final int response = (cameraFlags.stream()
			   		.filter(value -> (value != -1 && value != currentFlagValue))
			   		.anyMatch(camVal -> ((camVal-currentFlagValue) / Math.abs(camVal-currentFlagValue) * request < 0)) ? 0 : request);
	   if (response == 0) return currentFlagValue;					  	
	   else {							// Logic to handle BW fluctuating around a value
		   setBurstTimer();							
		   if (testForFluctuatingBandwidh(camId, currentFlagValue+response) == true && response == -1){   
			   setBwUpSupressTimer();
			   return currentFlagValue;
		   }
		   else {
			   cameraFlags.set(camId, currentFlagValue+response);
			   return currentFlagValue+response;
		   }
	   }
   }

   private synchronized boolean testForFluctuatingBandwidh(int camId, int requestedValue) {
	   updateSumOfCamflagDifferencesArray(camId, requestedValue);
	   updateSumOfDerivativesArray();
	   final int sumOfSums = sumOfDerivatives[0]+sumOfDerivatives[1];
	   if (DEBUG_LEVEL > 1) if (sumOfSums == 0) System.out.println("BINGO!");
	   return ((sumOfSums == 0) ? true : false);
   }

   private synchronized void updateSumOfCamflagDifferencesArray(int camId, int newFlag) {
	   ArrayList<Integer> tempArray = new ArrayList<Integer>(cameraFlags);
	   tempArray.set(camId, newFlag);
	   final int diff = cameraFlags.stream().mapToInt(i -> i.intValue()).sum() -
			   			tempArray.stream().mapToInt(i -> i.intValue()).sum();
	   sumOfCamflagDifferences[0] = sumOfCamflagDifferences[1];
	   sumOfCamflagDifferences[1] = diff;
   }
   private synchronized void updateSumOfDerivativesArray() {
	   sumOfDerivatives[0] = sumOfDerivatives[1];
	   sumOfDerivatives[1] = sumOfCamflagDifferences[0] + sumOfCamflagDifferences[1];
   }

   private synchronized void setBwUpSupressTimer() {
	   bwTimer = new Timer (LONG_TIMEOUT, null);
	   bwTimer.setRepeats(false);
	   bwTimer.start();
   }
   private synchronized void setBurstTimer() {
	   int numNodes = cameraFlags.size();
	   burstTimer = new Timer (SHORT_TIMEOUT*numNodes, null);
	   burstTimer.setRepeats(false);
	   burstTimer.start();
   }   
	
   void killTimers(){
	  if (burstTimer != null) burstTimer.stop();
	  if (bwTimer != null) bwTimer.stop();
   }
   void updateCameraArray (int numNodes) {
	  Integer element = 3;										 //initially set to min resolution	    
	  cameraFlags.clear();
	  for (int i=0; i< numNodes;i++) {
			cameraFlags.add(element);
	  }
	}
   void clearCameraArray(){
	  cameraFlags.clear(); 
   }
   ArrayList<Integer> getCameraFlags() {
	   return cameraFlags;
   }

}
