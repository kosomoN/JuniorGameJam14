package com.juniorjam.crawler.utils;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Utils {

	public static float getDistSqrd(float dx, float dy) {
		return dx * dx + dy * dy;
	}
	
	public static void drawCentered(SpriteBatch batch, TextureRegion region, float centerx, float centery, float width, float height) {
		batch.draw(region, centerx - width / 2, centery - height / 2, width, height);
	}
}
