/*
 * Minimal source-compatibility shim for Fabric API's Screens helper.
 */
package net.fabricmc.fabric.api.client.screen.v1;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;

public final class Screens
{
	private Screens()
	{}
	
	public static List<AbstractWidget> getButtons(Screen screen)
	{
		List<AbstractWidget> buttons = new ArrayList<>();
		for(Renderable renderable : screen.renderables)
			if(renderable instanceof AbstractWidget widget)
				buttons.add(widget);
			
		return buttons;
	}
}
