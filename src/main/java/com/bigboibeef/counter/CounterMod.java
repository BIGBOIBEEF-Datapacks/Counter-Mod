package com.bigboibeef.counter;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class CounterMod implements ClientModInitializer {
	public static final String MOD_ID = "counter-mod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static KeyBinding INCREMENT;
	private static KeyBinding DECREMENT;
	private static KeyBinding RESET;
	private static KeyBinding DISPLAY;

	private static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting()
			.create();
	private static final Path SAVE_FILE = Paths.get("counter.json");

	private static Integer counter;
	@Override
	public void onInitializeClient() {
		INCREMENT = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"Increment Counter",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_UNKNOWN,
				"Counter Mod"
		));

		DECREMENT = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"Decrement Counter",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_UNKNOWN,
				"Counter Mod"
		));

		RESET = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"Reset Counter",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_UNKNOWN,
				"Counter Mod"
		));

		DISPLAY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"Display Counter",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_UNKNOWN,
				"Counter Mod"
		));


		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (INCREMENT.wasPressed()) {
				increment();
			}

			if (DECREMENT.wasPressed()) {
				decrement();
			}

			if (RESET.wasPressed()) {
				reset();
			}

			if (DISPLAY.wasPressed()) {
				display();
			}
		});
	}

	public static void loadData() {
		if (Files.exists(SAVE_FILE)) {
			try (Reader reader = Files.newBufferedReader(SAVE_FILE)) {
				Type type = new TypeToken<Integer>(){}.getType();
				counter = GSON.fromJson(reader, type);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			counter = 0;
		}
	}

	public static void saveData() {
		try (Writer writer = Files.newBufferedWriter(SAVE_FILE)) {
			GSON.toJson(counter, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void increment () {
		loadData();
		counter++;
		MinecraftClient.getInstance().player.sendMessage(Text.literal("Counter +1"), false);
		saveData();
	}

	public static void decrement () {
		loadData();
		counter--;
		MinecraftClient.getInstance().player.sendMessage(Text.literal("Counter -1"), false);
		saveData();
	}

	public static void reset () {
		loadData();
		counter = 0;
		MinecraftClient.getInstance().player.sendMessage(Text.literal("Reset counter."), false);
		saveData();
	}

	public static void display () {
		loadData();
		MinecraftClient.getInstance().player.sendMessage(Text.literal("Counter: " + counter), false);
	}
}