package com.juniorjam.crawler.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

public class Button {

	public boolean isToggled;
	private boolean isToggleBtn;
	private static Sound sound;
	private GameState gs;
	private DungeonMap map;
	private int tileX, tileY;
	
	public Button(GameState gs, DungeonMap map, boolean isToggleBtn, int tileX, int tileY) {
		this.isToggleBtn = isToggleBtn;
		this.gs = gs;
		this.map = map;
		this.tileX = tileX;
		this.tileY = tileY;
	}
	
	public static void load(TextureAtlas atlas) {
		//sound = Gdx.audio.newSound(Gdx.files.internal("sounds/Door.ogg"));
	}
	
	public void toggle() {
		isToggled = !isToggled;
		//sound.play();
		System.out.println("Door sound");
	}
	
	public void update() {
		if((int) gs.getPlayer().getX() / 32 == tileX && (int) gs.getPlayer().getY() / 32 == tileY) {
			if(isToggleBtn) {
				toggle();
			} else {
				isToggled = true;
				//sound.play();
			}
			
		} else if(!isToggleBtn) {
			isToggled = false;
			//sound.play();
		}
		
		for(Player p : gs.getGhosts()) {
			if(!p.ghostFinished) {
				if((int) p.getX() / 32 == tileX && (int) p.getY() / 32 == tileY) {
					if(isToggleBtn) {
						toggle();
					} else {
						isToggled = true;
						break;
						//sound.play();
					}
					
				} else if(!isToggleBtn) {
					isToggled = false;
					//sound.play();
				}
			}
		}
		
		if(isToggled) {
			map.tileLayer.getCell(tileX, tileY).setTile(new StaticTiledMapTile(map.tiledMap.getTileSets().getTile(9).getTextureRegion()));
		} else {
			map.tileLayer.getCell(tileX, tileY).setTile(new StaticTiledMapTile(map.tiledMap.getTileSets().getTile(8).getTextureRegion()));
		}
	}
}
