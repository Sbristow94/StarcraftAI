package bot;

/**
 * Example of a Java AI Client that does nothing.
 */
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
	
	private Unit pylonProbe = null;
	
	private int supplyCap;
	
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
		//bwapi.setGameSpeed(0);     //SUPER SPEED
		claimedMinerals.clear();
		pylonProbe = null;
		supplyCap = 0;
	}
	
	@Override
	public void matchFrame() {
		for (Unit u : bwapi.getAllUnits()) {
			bwapi.drawCircle(u.getPosition(), 5, BWColor.Red, true, false);
		}
		
		//when to build pylons
		if ((bwapi.getSelf().getSupplyUsed() + 2 >= bwapi.getSelf().getSupplyTotal()) /*&& (bwapi.getSelf().getSupplyTotal() > supplyCap)*/) 
		{
			System.out.println("Enetered level 1");
			if (bwapi.getSelf().getMinerals() >= 100) 
			{
				System.out.println("Enetered level 2");
				for(Unit probe : bwapi.getMyUnits())
				{
					if (probe.getType() == UnitTypes.Protoss_Probe && pylonProbe == null) 
					{
							System.out.println("Pylon Probe made");
							pylonProbe = probe;
							break;	
					}
					else if((pylonProbe != null) && (bwapi.getSelf().getMinerals() >= 100))
					{
						
						System.out.println("Enetered level 3");
						//probe.build(probe.getPosition(), UnitTypes.Protoss_Pylon);

						int xPosSum = 0;
						int yPosSum = 0;
						int xAvg = 0;
						int yAvg = 0;
						int n = 0;
						for(Unit mineral : claimedMinerals)
						{
							xPosSum += mineral.getX();
							yPosSum += mineral.getY();
							n++;
						}
						xAvg = (int)(xPosSum/n);
						yAvg = (int)(yPosSum/n);
						Position avgMineralPos = new Position(xAvg, yAvg);
						int nexusMineralXDist, nexusMineralYDist;
						Position buildPosition = new Position(0,0);
						for(Unit nexus : bwapi.getMyUnits())
						{
							if(nexus.getType() == UnitTypes.Protoss_Nexus)
							{
								System.out.println("Enetered level 4");
								nexusMineralXDist = nexus.getX() - xAvg;
								nexusMineralYDist = nexus.getY() - yAvg;
								buildPosition = new Position(nexusMineralXDist + nexus.getX(), nexusMineralYDist + nexus.getY());
							}
						}
						pylonProbe.build(buildPosition, UnitTypes.Protoss_Pylon);
						
						supplyCap = bwapi.getSelf().getSupplyTotal();
					}
				}
			}
		}
		
//		set a dedicated builder probe
//		if (bwapi.getSelf().getMinerals() >= 200 && pylonProbe == null) 
//		{
//			for (Unit unit : bwapi.getMyUnits()) 
//			{
//				if (unit.getType() == UnitTypes.Protoss_Probe) 
//				{
//					pylonProbe = unit;
//					break;
//				}
//			}
//		}
			
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
	
		//Spawning loop
		for (Unit unit : bwapi.getMyUnits())
		{
			// Note you can use referential equality
			//Checks if the unit is a Nexus and checks if the mineral count is 50 or over
//			if(bwapi.getMyUnits().size() == 7) //If we have 6 probes
//			{
//				if(unit.getType() == UnitTypes.Protoss_Probe && bwapi.getSelf().getMinerals() >= 100)
//				{
//					bwapi.getMyUnits().get(6).build(bwapi.getMyUnits().get(6).getPosition(), UnitTypes.Protoss_Pylon);
//				}
//			}
			/*else*/ if(bwapi.getMyUnits().size()  == 10)
			{
				if(unit.getType() == UnitTypes.Protoss_Probe && bwapi.getSelf().getMinerals() >= 150)
				{
					System.out.println(bwapi.getMyUnits().get(9).getPosition());
					bwapi.getMyUnits().get(9).build(bwapi.getMyUnits().get(9).getPosition(), UnitTypes.Protoss_Gateway);
				}
			}
			else if(bwapi.getMyUnits().size() == 14)
			{
				if(bwapi.getMyUnits().get(12).getType() == UnitTypes.Protoss_Probe && bwapi.getSelf().getMinerals() >= 150)
				{
					System.out.println("2 gate");
					bwapi.getMyUnits().get(12).build(bwapi.getMyUnits().get(12).getPosition(), UnitTypes.Protoss_Gateway);
				}
			}
			else if(bwapi.getMyUnits().size() == 15)
			{
				if(unit.getType() == UnitTypes.Protoss_Probe && bwapi.getSelf().getMinerals() >= 100)
				{
					System.out.println("2 pylon");
					bwapi.getMyUnits().get(12).build(bwapi.getMyUnits().get(12).getPosition(), UnitTypes.Protoss_Pylon);
				}
			}
			else if (unit.getType() == UnitTypes.Protoss_Nexus && bwapi.getSelf().getMinerals() >= 50) 
				unit.train(UnitTypes.Protoss_Probe);	
				
		}
		

		for (int i = 0; i < bwapi.getMyUnits().size()-1; i++) 
		{
			if (bwapi.getMyUnits().get(i).getType() == UnitTypes.Protoss_Probe && bwapi.getMyUnits().get(i).isIdle()) 
			{
				// You can use referential equality for units, too

				for (Unit minerals : bwapi.getNeutralUnits()) 
				{
					if (minerals.getType().isMineralField() && !claimedMinerals.contains(minerals)) 
					{
						double distance = bwapi.getMyUnits().get(i).getDistance(minerals);

						//	System.out.println(bwapi.getMyUnits().size() + "inside");
						if (distance < 300) 
						{

							bwapi.getMyUnits().get(i).gather(minerals,false);
							claimedMinerals.add(minerals);
							break;
						}
					}
				}
			}
		}
//		int xPosSum = 0;
//		int yPosSum = 0;
//		int xAvg = 0;
//		int yAvg = 0;
//		int n = 0;
//		for(Unit mineral : claimedMinerals)
//		{
//			xPosSum += mineral.getX();
//			yPosSum += mineral.getY();
//			n++;
//		}
//		xAvg = (int)(xPosSum/n);
//		yAvg = (int)(yPosSum/n);
//		Position avgMineralPos = new Position(xAvg, yAvg);
//		int nexusMineralXDist, nexusMineralYDist;
//		Position buildPosition = new Position(0,0);
//		for(Unit nexus : bwapi.getMyUnits())
//		{
//			if(nexus.getType() == UnitTypes.Protoss_Nexus)
//			{
//				nexusMineralXDist = nexus.getX() - xAvg;
//				nexusMineralYDist = nexus.getY() - yAvg;
//				buildPosition = new Position(xAvg + nexus.getX(), yAvg + nexus.getY());
//			}
//		}
		
	}

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