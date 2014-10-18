package com.juniorjam.crawler.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class DungeonMap {
	private TiledMap tiledMap;
	private OrthogonalTiledMapRenderer renderer;
	private boolean[][] blockedTiles;
	
	public DungeonMap(String path, SpriteBatch batch) {
		tiledMap = new TmxMapLoader().load(path);
		renderer = new OrthogonalTiledMapRenderer(tiledMap, batch);
	}
	
	public void render(OrthographicCamera camera) {
		renderer.setView(camera);
		renderer.render();
	}

	public boolean isBlocked(int tilex, int tiley) {
		return blockedTiles[tilex][tiley];
	}
}
