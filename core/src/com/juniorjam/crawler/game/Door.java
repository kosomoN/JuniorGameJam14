package com.juniorjam.crawler.game;

import com.badlogic.gdx.utils.Array;

public class Door {

	private Array<Trigger> triggers = new Array<Trigger>();
	private int tileX, tileY;
	
	public Door(Array<Trigger> triggers, int x, int y) {
		this.triggers = triggers;
		tileX = x;
		tileY = y;
	}
	
	public Door(Trigger trigger, int x, int y) {
		triggers.add(trigger);
		tileX = x;
		tileY = y;
	}
	
	public void update(DungeonMap map) {
		boolean allDone = true;
		for(Trigger t : triggers) {
			if(!t.isTriggered()) {
				allDone = false;
				break;
			}
		}
		
		if(allDone) {
			map.blockedTiles[tileX][tileY] = false;
			map.tileLayer.getCell(tileX, tileY).getTile().setId(1);
		} else {
			map.blockedTiles[tileX][tileY] = true;
			map.tileLayer.getCell(tileX, tileY).getTile().setId(3);
		}
	}
}
