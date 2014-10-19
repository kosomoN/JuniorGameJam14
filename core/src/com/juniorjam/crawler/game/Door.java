package com.juniorjam.crawler.game;

import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Array;

public class Door {

	private Array<Trigger> triggers = new Array<Trigger>();
	private int tileX, tileY;
	private boolean horizontal;
	
	public Door(Array<Trigger> triggers, int x, int y, boolean horizontal) {
		this.triggers = triggers;
		this.horizontal = horizontal;
		tileX = x;
		tileY = y;
	}
	
	public Door(Trigger trigger, int x, int y, boolean horizontal) {
		triggers.add(trigger);
		this.horizontal = horizontal;
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
			System.out.println("herp");
			map.blockedTiles[tileX][tileY] = false;
			map.tileLayer.getCell(tileX, tileY).setTile(new StaticTiledMapTile(map.tiledMap.getTileSets().getTile(1).getTextureRegion()));
		} else {
			map.blockedTiles[tileX][tileY] = true;
			if(horizontal) 
				map.tileLayer.getCell(tileX, tileY).setTile(new StaticTiledMapTile(map.tiledMap.getTileSets().getTile(7).getTextureRegion()));
			else
				map.tileLayer.getCell(tileX, tileY).setTile(new StaticTiledMapTile(map.tiledMap.getTileSets().getTile(3).getTextureRegion()));
		}
	}
}
