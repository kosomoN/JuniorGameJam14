package com.juniorjam.crawler.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;
import com.juniorjam.crawler.game.entities.enemies.Bat;
import com.juniorjam.crawler.game.entities.enemies.Rat;

public class DungeonMap {
	private int tileWidth, tileHeight;
	private TiledMap tiledMap;
	private OrthogonalTiledMapRenderer renderer;
	public boolean[][] blockedTiles;
	public TiledMapTileLayer tileLayer;
	public static TiledMapTileLayer enemyLayer;
	
	public DungeonMap(GameState gs, String path, SpriteBatch batch) {
		tiledMap = new TmxMapLoader().load(path);
		renderer = new OrthogonalTiledMapRenderer(tiledMap, batch);
		
		tileLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Tile Layer 1");
		
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
	
	public void spawnEnemies(GameState gs) {
		enemyLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Enemies");
		Array<Player> initialPlayer = new Array<Player>();
		initialPlayer.add(gs.getPlayer());
		
		for(int i = 0; i < enemyLayer.getWidth(); i++) {
			for(int j = 0; j < enemyLayer.getHeight(); j++) {
				Cell cell = enemyLayer.getCell(i, j);
				if(cell != null) {
					int enemyID = cell.getTile().getId();
					switch (enemyID) {
					case 1:
						gs.addEnemy(new Bat(gs, i * 32 + 16, j * 32 + 16));
						break;
					case 2:
						gs.addEnemy(new Rat(gs, i * 32 + 16, j * 32 + 16));
						break;
					default:
						break;
					}
				}
			}
		}
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