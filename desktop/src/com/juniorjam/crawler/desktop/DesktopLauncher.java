package com.juniorjam.crawler.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.juniorjam.crawler.Launcher;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Settings s = new Settings();
		TexturePacker.process(s, "C:/Users/Onni.jkosomaa01/Documents/workspace/JuniorGameJam14/core/assets/unpacked", "C:/Users/Onni.jkosomaa01/Documents/workspace/JuniorGameJam14/core/assets/packed", "Sprites");
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1920;
		config.height = 1080;
		new LwjglApplication(new Launcher(), config);
	}
}
