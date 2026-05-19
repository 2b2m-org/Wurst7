/*
 * Minimal source-compatibility shim for Fabric API login callbacks.
 */
package net.fabricmc.fabric.api.client.networking.v1;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;

public final class ClientLoginConnectionEvents
{
	public static final Init INIT = new Init();
	
	private ClientLoginConnectionEvents()
	{}
	
	@FunctionalInterface
	public interface InitCallback
	{
		void onLoginStart(ClientHandshakePacketListenerImpl handler,
			Minecraft client);
	}
	
	public static final class Init
	{
		private final List<InitCallback> callbacks = new ArrayList<>();
		
		public void register(InitCallback callback)
		{
			callbacks.add(callback);
		}
		
		public void invoker(ClientHandshakePacketListenerImpl handler,
			Minecraft client)
		{
			for(InitCallback callback : List.copyOf(callbacks))
				callback.onLoginStart(handler, client);
		}
	}
}
