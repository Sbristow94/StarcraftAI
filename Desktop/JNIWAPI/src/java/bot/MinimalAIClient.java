package bot;



/**

 * Example of a Java AI Client that does nothing.

 */



import java.util.ArrayList;

import java.util.HashSet;



import jnibwapi.BWAPIEventListener;

import jnibwapi.JNIBWAPI;

import jnibwapi.Position;

import jnibwapi.Unit;

import jnibwapi.types.UnitType.UnitTypes;

import jnibwapi.util.BWColor;



public class MinimalAIClient implements BWAPIEventListener {

 private JNIBWAPI bwapi;

 /** used for mineral splits */

 private final HashSet<Unit> claimedMinerals = new HashSet<>();

 private final ArrayList<Unit> probeList = new ArrayList<>();

 private final ArrayList<Unit> armyList = new ArrayList<>();

 private final ArrayList<Unit> buildingList = new ArrayList<>();

 private final ArrayList<Unit> pylonList = new ArrayList<>();



 private Unit pylonProbe = null;

 private Unit gateProbe = null;

 int pylonProbeCounter = 0;

 int gateCounter = 0;

 int nexusX = 0;
 int nexusY = 0;

 boolean zealotTime = false;

 boolean pylonBuilding = false;

 boolean gateTime = false;
 
 boolean pylonTime = false;

 private int supplyCap;

 int pylonCounter;

 int zealotCounter;
 int absY = 0;
 int absX = 0;

 private Unit gateZealot;

 private Unit gateZealotTwo;

 private Unit nexusHolder;

 public static void main(String[] args) {

  new MinimalAIClient();

 }



 public MinimalAIClient() {

  bwapi = new JNIBWAPI(this, false);

  bwapi.start();

 }



 @Override

 public void connected() {}



 @Override

 public void matchStart()

 {

  bwapi.enableUserInput();

  bwapi.enablePerfectInformation();

  bwapi.setGameSpeed(7);     //SUPER SPEED

  claimedMinerals.clear();

  pylonProbe = null;

  gateProbe = null;

  supplyCap = 0;



 }



 @Override

 public void matchFrame() {

  //  for (Unit u : bwapi.getAllUnits()) {

  //   bwapi.drawCircle(u.getPosition(), 5, BWColor.Red, true, false);

  //  }




  claimedMinerals.clear();

  probeList.clear();

  armyList.clear();

  buildingList.clear();

  pylonList.clear();



  for(Unit unit : bwapi.getMyUnits())

  {

   if(unit.getType() == UnitTypes.Protoss_Probe)

   {

    probeList.add(unit);

   }

   else if(unit.getType() == UnitTypes.Protoss_Zealot || unit.getType() == UnitTypes.Protoss_Dragoon)

   {

    armyList.add(unit);

   }

   else if(unit.getType() == UnitTypes.Protoss_Pylon)

   {

    pylonList.add(unit);

   }

   else

   {

    buildingList.add(unit);




   }

  }






  //when to build pylons




  //  set a dedicated builder probe

  //  if (bwapi.getSelf().getMinerals() >= 200 && pylonProbe == null)

  //  {

  //   for (Unit unit : bwapi.getMyUnits())

  //   {

  //    if (unit.getType() == UnitTypes.Protoss_Probe)

  //    {

  //     pylonProbe = unit;

  //     break;

  //    }

  //   }

  //  }



  //THIS IS WHERE POSITION STUFF HAPPENS







  /*

   // build the pool under the overlord

   for (Unit unit : bwapi.getMyUnits()) {

    if (unit.getType() == UnitTypes.Zerg_Overlord) {

     poolDrone.build(unit.getPosition(), UnitTypes.Zerg_Spawning_Pool);

    }

   }

  }

   */



  //Finding the two gates

  for(Unit gate : bwapi.getMyUnits())

  {

   if (gate.getType() == UnitTypes.Protoss_Gateway)

   {

    if(gateZealot != gate)

     gateZealot = gate;

    else

     gateZealotTwo = gate;

   }

  }



  for(Unit nexus : bwapi.getMyUnits())

  {

   if (nexus.getType() == UnitTypes.Protoss_Nexus)

   {

    nexusHolder = nexus;

   }

  }

  //Spawning loop

  for (Unit unit : bwapi.getMyUnits())

  {

   if(probeList.size() + nexusHolder.getTrainingQueueSize() < 8 && pylonList.size() == 0 && !pylonBuilding)

    unit.train(UnitTypes.Protoss_Probe);

   //   if (bwapi.getMyUnits().size() < 20 && unit.getType() == UnitTypes.Protoss_Nexus && bwapi.getMyUnits().size() != 8 /*&& bwapi.getMyUnits().size() != 17*/ && bwapi.getMyUnits().size() != 13 && bwapi.getMyUnits().size() != 12  && bwapi.getMyUnits().size() != 15 && bwapi.getMyUnits().size() != 17 && bwapi.getMyUnits().size() != 19/*  && bwapi.getMyUnits().size() != 18 && bwapi.getMyUnits().size() != 19 */&& bwapi.getSelf().getMinerals() >= 50 )

   //    unit.train(UnitTypes.Protoss_Probe);

   else if(probeList.size() + nexusHolder.getTrainingQueueSize() < 12 && pylonList.size() == 1 && !gateTime)

    unit.train(UnitTypes.Protoss_Probe);

   else if(probeList.size() + nexusHolder.getTrainingQueueSize() < 16 && pylonList.size() >=1 && buildingList.size() >= 2 && !gateTime)

    unit.train(UnitTypes.Protoss_Probe);

   if(buildingList.size() >= 3 && !gateTime)

   {

    System.out.println("Entered zealot");

    if(bwapi.getSelf().getMinerals() >= 100)

    {

     //System.out.println("Zealot loop");

     if(gateZealot.getTrainingQueueSize() <= gateZealotTwo.getTrainingQueueSize())

     {

      gateZealot.train(UnitTypes.Protoss_Zealot);

     }

     else

     {

      gateZealotTwo.train(UnitTypes.Protoss_Zealot);

     }

    }

   }



  }




  //This is the Pylon spawning condition.

  if(bwapi.getMyUnits().size()*2 + 2 >= bwapi.getSelf().getSupplyTotal())

   //  if (bwapi.getMyUnits().size() == 8 || bwapi.getMyUnits().size() == 15  || bwapi.getMyUnits().size() == 19 || bwapi.getMyUnits().size() == 29 /*&& (bwapi.getSelf().getSupplyTotal() > supplyCap)*/)

  {

   if(!pylonBuilding)

   {

    //System.out.println("Enetered level 1");
    
    

    if (bwapi.getSelf().getMinerals() >= 100)

    {

     //System.out.println("Enetered level 2");

     for(Unit probe : bwapi.getMyUnits())

     {

      if (probe.getType() == UnitTypes.Protoss_Probe && pylonProbe == null)

      {

       //System.out.println("Pylon Probe made");

       pylonProbe = probe;

       //break;

      }

      else if((pylonProbe != null) && (bwapi.getSelf().getMinerals() >= 100))

      {



       //System.out.println("Enetered level 3");

       //probe.build(probe.getPosition(), UnitTypes.Protoss_Pylon);



       int xPosSum = 0;

       int yPosSum = 0;

       int xAvg = 0;

       int yAvg = 0;

       int n = 0;



       for(Unit mineral : bwapi.getNeutralUnits())

       {

        if (mineral.getType().isMineralField())

        {

         double distance = probe.getDistance(mineral);



         // System.out.println(bwapi.getMyUnits().size() + "inside");

         if (distance < 300)

         {

          xPosSum += mineral.getX();

          yPosSum += mineral.getY();

          n++;

         }

        }



       }

       //      if(bwapi.getMyUnits().size() < 9)

       if(pylonList.size() == 0)

        //      if(bwapi.getMyUnits().size() == 8)

       {

        xAvg = (int)(xPosSum/8);

        yAvg = (int)(yPosSum/8);

        Position avgMineralPos = new Position(xAvg, yAvg);

        int nexusMineralXDist, nexusMineralYDist;

        Position buildPosition = new Position(0,0);




        for(Unit nexus : bwapi.getMyUnits())

        {

         if(nexus.getType() == UnitTypes.Protoss_Nexus)

         {

          nexusX = nexus.getPosition().getBX() * 32;
          nexusY = nexus.getPosition().getBY() * 32;

          //System.out.println("Enetered level 4");

          nexusMineralXDist = nexus.getX() - xAvg;

          nexusMineralYDist = nexus.getY() - yAvg;

          buildPosition = new Position(nexusMineralXDist + nexus.getX(), nexusMineralYDist + nexus.getY());

         }

        }



        pylonProbe.build(buildPosition, UnitTypes.Protoss_Pylon);



        pylonBuilding = true;

       }

       //      else if(bwapi.getMyUnits().size() >= 9 && bwapi.getMyUnits().size() < 16)

       //      else if (bwapi.getMyUnits().size() == 15 || bwapi.getMyUnits().size() == 19 || bwapi.getMyUnits().size() == 29)

       else

       {



        if(bwapi.getSelf().getMinerals() >= 100)//added this!

        {

         Position buildPosition = new Position(0,0);

         for(Unit pylon : bwapi.getMyUnits())

         {



          if(pylon.getType() == UnitTypes.Protoss_Pylon)

          {

           absY = Math.abs(pylon.getPosition().getBY()*32 - nexusY);
           absX = Math.abs(pylon.getPosition().getBX()*32 - nexusX);

           System.out.println("Nexus Y: " + nexusY + " PylonY: " + pylon.getPosition().getBY()*32);
           if(pylon.getPosition().getBX()*32 > nexusX)
           {
            System.out.println("right1");
            if(absY < 128)
            {
             System.out.println("right");
             buildPosition = new Position(pylon.getX() - 25, pylon.getY() + 32);
            }
            
           }
           if(pylon.getPosition().getBX()*32 < nexusX)
           {
            System.out.println("left1");
            if(absY < 128)
            {
             System.out.println("left");
             buildPosition = new Position(pylon.getX() - 25, pylon.getY() + 32);
            }
           }
           if(pylon.getPosition().getBY()*32 > nexusY)
           {
            System.out.println("down1");
            if(absX < 128)
            {
             System.out.println("down");
             buildPosition = new Position(pylon.getX() + 32, pylon.getY() - 25);
            }
            //System.out.println("lower");
           }
           if(pylon.getPosition().getBY()*32 < nexusY)
           {
            System.out.println("up1");
            if(absX < 128)
            {
             System.out.println("up");
             buildPosition = new Position(pylon.getX() - 96, pylon.getY() - 25);
            }
           }
           //buildPosition = new Position(pylon.getX() - 25, pylon.getY() + 32);

          }

         }

         //System.out.println("pylons");

         //System.out.println(pylonProbeCounter);

         pylonProbe.build(buildPosition, UnitTypes.Protoss_Pylon);

         pylonBuilding = true;

        }

       }



       supplyCap = bwapi.getSelf().getSupplyTotal();

      }

     }

    }

   }

  }

  else if(bwapi.getMyUnits().size()*2 + 2 < bwapi.getSelf().getSupplyTotal())

   pylonBuilding = false;

  if ((pylonList.size() == 1 || pylonList.size() == 2) && gateCounter < 2)

  {



   // System.out.println("Enetered level 1");



   gateTime = true;

   if(bwapi.getSelf().getMinerals() >= 150)//added this!

   {


    for(Unit probe : bwapi.getMyUnits())

    {

     if (probe.getType() == UnitTypes.Protoss_Probe && (gateProbe == null || gateProbe == pylonProbe))

     {

      //System.out.println("gate Probe made");

      gateProbe = probe;

      //break;

     }

     if((gateProbe != null)/* && (bwapi.getSelf().getMinerals() >= 150)*/)

     {

      // System.out.println("Enetered level 2");

      //System.out.println("Enetered level 3");

      //probe.build(probe.getPosition(), UnitTypes.Protoss_Pylon);



      int xPosSum = 0;

      int yPosSum = 0;

      int xAvg = 0;

      int yAvg = 0;

      int n = 0;



      for(Unit mineral : bwapi.getNeutralUnits())

      {

       if (mineral.getType().isMineralField())

       {

        double distance = probe.getDistance(mineral);



        //System.out.println("inside");

        if (distance < 300)

        {

         xPosSum += mineral.getX();

         yPosSum += mineral.getY();

         n++;

        }

       }



      }



      xAvg = (int)(xPosSum/8);

      yAvg = (int)(yPosSum/8);

      Position avgMineralPos = new Position(xAvg, yAvg);

      int nexusMineralXDist, nexusMineralYDist;

      Position buildPosition = new Position(0,0);
      
      


      if(pylonList.size() == 1)

      {

       for(Unit pylon : bwapi.getMyUnits())

       {

        if(pylon.getType() == UnitTypes.Protoss_Pylon)

        {


         absY = Math.abs(pylon.getPosition().getBY()*32 - nexusY);
         absX = Math.abs(pylon.getPosition().getBX()*32 - nexusX);

         System.out.println("Nexus Y: " + nexusY + " PylonY: " + pylon.getPosition().getBY()*32);
         if(pylon.getPosition().getBX()*32 > nexusX)
         {
          System.out.println("right1");
          if(absY < 128)
          {
           System.out.println("right");
           buildPosition = new Position(pylon.getPosition().getBX()*32 + 128,pylon.getPosition().getBY()*32);
          }
          
         }
         if(pylon.getPosition().getBX()*32 < nexusX)
         {
          System.out.println("left1");
          if(absY < 128)
          {
           System.out.println("left");
           buildPosition = new Position(pylon.getPosition().getBX()*32 - 160,pylon.getPosition().getBY()*32 - 96);
          }
         }
         if(pylon.getPosition().getBY()*32 > nexusY)
         {
          System.out.println("down1");
          if(absX < 128)
          {
           System.out.println("down");
           buildPosition = new Position(pylon.getPosition().getBX()*32+64,pylon.getPosition().getBY()*32 +64);
          }
          //System.out.println("lower");
         }
         if(pylon.getPosition().getBY()*32 < nexusY)
         {
          System.out.println("up1");
          if(absX < 128)
          {
           System.out.println("up");
           buildPosition = new Position(pylon.getPosition().getBX()*32 -64,pylon.getPosition().getBY()*32 - 128);
          }
         }

        }
       }

       //System.out.println("build");

       if(bwapi.getSelf().getMinerals() >= 150)//added this!

       {

        //System.out.println(buildPosition);

        bwapi.drawCircle(buildPosition, 5, BWColor.Red, true, false);

        pylonProbe.build(buildPosition, UnitTypes.Protoss_Gateway);

        gateTime = false;

        gateCounter = 1;

       }

      }

      else if(pylonList.size() == 2 && gateCounter == 1)

      {

       System.out.println("gate 2 ");

       if(bwapi.getSelf().getMinerals() >= 150)//added this!)

       {


        for(Unit pylon : bwapi.getMyUnits())

        {



         if(pylon.getType() == UnitTypes.Protoss_Pylon)

         {
          absY = Math.abs(pylon.getPosition().getBY()*32 - nexusY);
          absX = Math.abs(pylon.getPosition().getBX()*32 - nexusX);

          System.out.println("Nexus Y: " + nexusY + " PylonY: " + pylon.getPosition().getBY()*32);
          if(pylon.getPosition().getBX()*32 > nexusX)
          {
           System.out.println("right1");
           if(absY < 128)
           {
            System.out.println("right");
            buildPosition = new Position(pylon.getPosition().getBX()*32 + 128,pylon.getPosition().getBY()*32+64);
           }
           
          }
          if(pylon.getPosition().getBX()*32 < nexusX)
          {
           System.out.println("left1");
           if(absY < 128)
           {
            System.out.println("left");
            buildPosition = new Position(pylon.getPosition().getBX()*32 - 160,pylon.getPosition().getBY()*32 - 160);
           }
          }
          if(pylon.getPosition().getBY()*32 > nexusY)
          {
           System.out.println("down1");
           if(absX < 128)
           {
            System.out.println("down");
            buildPosition = new Position(pylon.getPosition().getBX()*32-160,pylon.getPosition().getBY()*32 +64);
           }
           //System.out.println("lower");
          }
          if(pylon.getPosition().getBY()*32 < nexusY)
          {
           System.out.println("up1");
           if(absX < 128)
           {
            System.out.println("up");
            buildPosition = new Position(pylon.getPosition().getBX()*32 -128,pylon.getPosition().getBY()*32 - 128);
           }
          }


         }

        }



        //System.out.println("pylons");

        //System.out.println(pylonProbeCounter);

        if(bwapi.getSelf().getMinerals() >= 150)//added this!)

        {

         bwapi.drawCircle(buildPosition, 5, BWColor.Red, true, false);

         pylonProbe.build(buildPosition, UnitTypes.Protoss_Gateway);

         gateTime = false;

         gateCounter = 2;


        }

       }

      }

     }



    }

   }


  // System.out.println(gateTime);

   supplyCap = bwapi.getSelf().getSupplyTotal();

  }






  //  pylonCounter = 0;

  //  for(Unit pylon : bwapi.getMyUnits())

  //  {

  //   if(pylon.getType() == UnitTypes.Protoss_Probe)

  //    pylonCounter++;

  //  }

  // 

  //  if(pylonCounter == 3)

  //   zealotTime = true;



  supplyCap = bwapi.getSelf().getSupplyTotal();



  for (Unit probe : bwapi.getMyUnits())

  {

   if (probe.getType() == UnitTypes.Protoss_Probe && probe.isIdle() /*&& probe != pylonProbe && probe != gateProbe*/)

   {

    // You can use referential equality for units, too



    for (Unit minerals : bwapi.getNeutralUnits())

    {

     if (minerals.getType().isMineralField() && !claimedMinerals.contains(minerals))

     {

      double distance = probe.getDistance(minerals);



      // System.out.println(bwapi.getMyUnits().size() + "inside");

      if (distance < 300)

      {

       probe.gather(minerals,false);

       claimedMinerals.add(minerals);

       break;

      }

     }

    }

   }

  }



  if(bwapi.getMyUnits().size() > 20)

  {

   for (Unit minerals : bwapi.getNeutralUnits())

   {

    if (minerals.getType().isMineralField() && !claimedMinerals.contains(minerals))

    {

     double distance = gateProbe.getDistance(minerals);

     //System.out.println(distance);

     //System.out.println(bwapi.getMyUnits().size() + "inside");

     if (distance < 500)

     {

      gateProbe.gather(minerals,false);

      claimedMinerals.add(minerals);

      break;

     }

    }

   }

  }

  zealotCounter = 0;



  for (Unit zealot : bwapi.getMyUnits())

  {

   if(zealot.getType() == UnitTypes.Protoss_Zealot)

    zealotCounter++;

  }



  if(zealotCounter > 5)





  {

   for (Unit zealot : bwapi.getMyUnits())

   {

    if(zealot.getType() == UnitTypes.Protoss_Zealot)

    {

     for (Unit enemy : bwapi.getEnemyUnits())

     {

      if(!enemy.getType().isBuilding())

      {

       zealot.attack(enemy.getPosition(), false);

       break;

      }

     }

    }

   }

  }
 }

//  int xPosSum = 0;

 //  int yPosSum = 0;

 //  int xAvg = 0;

 //  int yAvg = 0;

 //  int n = 0;

 //  for(Unit mineral : claimedMinerals)

 //  {

 //   xPosSum += mineral.getX();

 //   yPosSum += mineral.getY();

 //   n++;

 //  }

 //  xAvg = (int)(xPosSum/n);

 //  yAvg = (int)(yPosSum/n);

 //  Position avgMineralPos = new Position(xAvg, yAvg);

 //  int nexusMineralXDist, nexusMineralYDist;

 //  Position buildPosition = new Position(0,0);

 //  for(Unit nexus : bwapi.getMyUnits())

 //  {

 //   if(nexus.getType() == UnitTypes.Protoss_Nexus)

 //   {

 //    nexusMineralXDist = nexus.getX() - xAvg;

 //    nexusMineralYDist = nexus.getY() - yAvg;

 //    buildPosition = new Position(xAvg + nexus.getX(), yAvg + nexus.getY());

 //   }

 //  }






 @Override

 public void keyPressed(int keyCode) {}

 @Override

 public void matchEnd(boolean winner) {}

 @Override

 public void sendText(String text) {}

 @Override

 public void receiveText(String text) {}

 @Override

 public void nukeDetect(Position p) {}

 @Override

 public void nukeDetect() {}

 @Override

 public void playerLeft(int playerID) {}

 @Override

 public void unitCreate(int unitID) {}

 @Override

 public void unitDestroy(int unitID) {}

 @Override

 public void unitDiscover(int unitID) {}

 @Override

 public void unitEvade(int unitID) {}

 @Override

 public void unitHide(int unitID) {}

 @Override

 public void unitMorph(int unitID) {}

 @Override

 public void unitShow(int unitID) {}

 @Override

 public void unitRenegade(int unitID) {}

 @Override

 public void saveGame(String gameName) {}

 @Override

 public void unitComplete(int unitID) {}

 @Override

 public void playerDropped(int playerID) {}

}