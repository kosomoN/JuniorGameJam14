package com.juniorjam.crawler.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class DungeonMap {
	private int tileWidth, tileHeight;
	private TiledMap tiledMap;
	private OrthogonalTiledMapRenderer renderer;
	private boolean[][] blockedTiles;
	
	public DungeonMap(String path, SpriteBatch batch) {
		tiledMap = new TmxMapLoader().load(path);
		renderer = new OrthogonalTiledMapRenderer(tiledMap, batch);
		
		TiledMapTileLayer blockedLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Blocked");
		blockedTiles = new boolean[blockedLayer.getWidth()][blockedLayer.getHeight()];
		
		for(int i = 0; i < blockedLayer.getWidth(); i++) {
			for(int j = 0; j < blockedLayer.getHeight(); j++) {
				blockedTiles[i][j] = (blockedLayer.getCell(i, j) != null);
			}
		}
		
		tileWidth = (int) blockedLayer.getTileWidth();
		tileHeight = (int) blockedLayer.getTileHeight();
	}
	
	public void render(OrthographicCamera camera) {
		renderer.setView(camera);
		renderer.render();
	}

	public boolean isBlocked(int tilex, int tiley) {
		if(tilex < 0 || tilex > blockedTiles.length - 1 || tiley < 0 || tiley > blockedTiles[0].length - 1)
			return true;
		
		return blockedTiles[tilex][tiley];
	}
	
	public int getTileWidth() {
		return tileWidth;
	}
	
	public int getTileHeight() {
		return tileHeight;
	}
}
