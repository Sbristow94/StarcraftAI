package bot;
/**
 * Protoss AI Client.
 * Created By Sean Bristow, Logan Bancroft Boucher, Kevin Gerstein, Adam Lind, Brian Perera
 * CSC-568, due 4/24/15
 * 
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

	/**Used to count Probes */
	private final ArrayList<Unit> probeList = new ArrayList<>();

	/**Used to count the total number of units in the Protoss Army (mostly just Zealots) */
	private final ArrayList<Unit> armyList = new ArrayList<>();

	/**Counts the number of Nexus's and Gateway's */
	private final ArrayList<Unit> buildingList = new ArrayList<>();

	/**Counts the number of Pylons */
	private final ArrayList<Unit> pylonList = new ArrayList<>();
	/** Designated Probe that makes the pylons and the gateways */
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
	/**Initializes BWAPI */
	public MinimalAIClient() {
		bwapi = new JNIBWAPI(this, false);
		bwapi.start();
	}
	@Override
	public void connected() {}
	@Override
	/** Sets parameters at the start of the match */
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
	public void matchFrame() 
	{
		/** Clears list every frame */
		claimedMinerals.clear();
		/** Clears list every frame */
		probeList.clear();
		/** Clears list every frame */
		armyList.clear();
		/** Clears list every frame */
		buildingList.clear();
		/** Clears list every frame */
		pylonList.clear();
		
		/** Adds all units to their proper lists */
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
		
		/**Finds and stores the two gateways */
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
		/** Finds and stores the Nexus */
		for(Unit nexus : bwapi.getMyUnits())
		{
			if (nexus.getType() == UnitTypes.Protoss_Nexus)
			{
				nexusHolder = nexus;
			}
		}
		/**Spawning loop for Probes and Zealots*/
		for (Unit unit : bwapi.getMyUnits())
		{
			if(probeList.size() + nexusHolder.getTrainingQueueSize() < 8 && pylonList.size() == 0 && !pylonBuilding)
				unit.train(UnitTypes.Protoss_Probe);
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
		/** Sets conditions in order to spawn a Pylon */
		if(bwapi.getMyUnits().size()*2 + 2 >= bwapi.getSelf().getSupplyTotal())
		{
			if(!pylonBuilding)
			{
				//System.out.println("Enetered level 1");
				if (bwapi.getSelf().getMinerals() >= 100)
				{
					//System.out.println("Enetered level 2");
					for(Unit probe : bwapi.getMyUnits())
					{   /** Designates a probe for Pylon and Gateway creation */
						if (probe.getType() == UnitTypes.Protoss_Probe && pylonProbe == null)
						{
							//System.out.println("Pylon Probe made");
							pylonProbe = probe;
							//break;
						}
						/** Makes sure that there is a pylon probe generated,
						 * and checks if there are enough minerals to build a Pylon
						 */
						else if((pylonProbe != null) && (bwapi.getSelf().getMinerals() >= 100))
						{
							//System.out.println("Enetered level 3");
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
							/** Checks to see if its the first Pylon.
							 * If yes, generates a position for the Pylon
							 */
							if(pylonList.size() == 0)
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
							/** Checks to see if it is the 2nd or greater Pylon.
							 * If yes, positions Pylon in respect to previous
							 */
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
		
		/** Makes sure you don't "double build" Pylons*/
		else if(bwapi.getMyUnits().size()*2 + 2 < bwapi.getSelf().getSupplyTotal())
			pylonBuilding = false;
		
		/**Start of the Gateway building */
		if ((pylonList.size() == 1 || pylonList.size() == 2) && gateCounter < 2)
		{
			// System.out.println("Enetered level 1");
			gateTime = true;
			if(bwapi.getSelf().getMinerals() >= 150)//added this!
			{
				for(Unit probe : bwapi.getMyUnits())
				{
					/** If there are no probes assigned to the Gateway, then it finds a probe meant for the Gateway */
					if (probe.getType() == UnitTypes.Protoss_Probe && (gateProbe == null || gateProbe == pylonProbe))
					{
						//System.out.println("gate Probe made");
						gateProbe = probe;
						//break;
					}
					if((gateProbe != null))
					{
						// System.out.println("Enetered level 2");
						//System.out.println("Enetered level 3");
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
							/** Sets up first gate at an offset, from the pylon(s) */
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
							/**Where we build the Gateway */
							if(bwapi.getSelf().getMinerals() >= 150)//added this!
							{
								//System.out.println(buildPosition);
								bwapi.drawCircle(buildPosition, 5, BWColor.Red, true, false);
								pylonProbe.build(buildPosition, UnitTypes.Protoss_Gateway);
								gateTime = false;
								gateCounter = 1;
							}
						}
						/** Else if statement for the second gate, only spawns if there's one gate and two pylons */
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
		supplyCap = bwapi.getSelf().getSupplyTotal();
		
		/** Mineral gathering loop */
		for (Unit probe : bwapi.getMyUnits())
		{
			if (probe.getType() == UnitTypes.Protoss_Probe && probe.isIdle())
			{
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
		} /** Makes the GateProbe go back to Mining */
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
		/** Counts the number of Zealots */
		for (Unit zealot : bwapi.getMyUnits())
		{

			if(zealot.getType() == UnitTypes.Protoss_Zealot)

				zealotCounter++;
		}
		/** If there are more than 5 Zealots, a Zealot rush commences
		 * Issues with Zealot grouping in mid-game attack
		 */
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