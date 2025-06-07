package net.martree.marspeemod;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class Keybinds {
    public static final String CATEGORY = "key.categories.tposetutorial";

    public static final KeyMapping TPOSE_KEY = new KeyMapping(
            "key.tposetutorial.tpose",                           // Translation key
            InputConstants.Type.KEYSYM,                 // Input type
            GLFW.GLFW_KEY_P,                            // Default key
            CATEGORY                                     // Category shown in Controls menu
    );
}