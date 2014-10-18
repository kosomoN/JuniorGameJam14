package com.juniorjam.crawler.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameState extends ScreenAdapter {
	
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private DungeonMap map;
	
	public GameState(SpriteBatch batch, OrthographicCamera camera) {
		this.batch = batch;
		this.camera = camera;
		map = new DungeonMap("maps/Test.tmx", batch);
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.position.x += 0.4f;
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		map.render(camera);
	}
}
