package com.juniorjam.crawler;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.juniorjam.crawler.game.GameState;

public class Launcher extends Game {
	private GameState gameState;
	
	private SpriteBatch batch;
	private OrthographicCamera camera;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		gameState = new GameState(batch, camera);
		
		setScreen(gameState);
	}
}
