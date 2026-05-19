/*
 * Minimal source-compatibility shim backed by NeoForge runtime metadata.
 */
package net.fabricmc.loader.api;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import net.fabricmc.loader.api.metadata.ModMetadata;

public final class FabricLoader
{
	private static final FabricLoader INSTANCE = new FabricLoader();
	
	private FabricLoader()
	{}
	
	public static FabricLoader getInstance()
	{
		return INSTANCE;
	}
	
	public Optional<ModContainer> getModContainer(String modId)
	{
		return getAllMods().stream()
			.filter(mod -> mod.getMetadata().getId().equals(modId)).findFirst();
	}
	
	public Collection<ModContainer> getAllMods()
	{
		List<ModContainer> mods = new ArrayList<>();
		readNeoForgeMods(mods);
		return mods;
	}
	
	public boolean isDevelopmentEnvironment()
	{
		try
		{
			Class<?> loader = Class.forName("net.neoforged.fml.loading.FMLLoader");
			Method isProduction = loader.getMethod("isProduction");
			return !((Boolean)isProduction.invoke(null));
			
		}catch(ReflectiveOperationException | RuntimeException e)
		{
			return Boolean.getBoolean("wurst.neoforge.dev")
				|| Boolean.getBoolean("neoforge.development");
		}
	}
	
	public Path getGameDir()
	{
		try
		{
			Class<?> pathsClass =
				Class.forName("net.neoforged.fml.loading.FMLPaths");
			Object gameDir = pathsClass.getField("GAMEDIR").get(null);
			return (Path)gameDir.getClass().getMethod("get").invoke(gameDir);
			
		}catch(ReflectiveOperationException | RuntimeException e)
		{
			return Path.of(".").toAbsolutePath().normalize();
		}
	}
	
	private void readNeoForgeMods(List<ModContainer> mods)
	{
		try
		{
			Class<?> modListClass = Class.forName("net.neoforged.fml.ModList");
			Object modList = modListClass.getMethod("get").invoke(null);
			Object modInfos = modListClass.getMethod("getMods").invoke(modList);
			if(!(modInfos instanceof Iterable<?> iterable))
				return;
			
			for(Object modInfo : iterable)
			{
				String id = invokeString(modInfo, "getModId");
				String version = invokeString(modInfo, "getVersion");
				if(id != null)
					mods.add(new SimpleModContainer(id, version));
			}
			
		}catch(ReflectiveOperationException | RuntimeException e)
		{
			String version =
				FabricLoader.class.getPackage().getImplementationVersion();
			mods.add(new SimpleModContainer("wurst",
				version != null ? version : "unknown"));
		}
	}
	
	private static String invokeString(Object target, String method)
		throws ReflectiveOperationException
	{
		Object value = target.getClass().getMethod(method).invoke(target);
		return Objects.toString(value, null);
	}
	
	private record SimpleVersion(String value) implements Version
	{
		@Override
		public String toString()
		{
			return value;
		}
	}
	
	private record SimpleMetadata(String id, Version version)
		implements ModMetadata
	{
		private SimpleMetadata(String id, String version)
		{
			this(id, new SimpleVersion(version));
		}
		
		@Override
		public String getId()
		{
			return id;
		}
		
		@Override
		public Version getVersion()
		{
			return version;
		}
	}
	
	private record SimpleModContainer(ModMetadata metadata)
		implements ModContainer
	{
		private SimpleModContainer(String id, String version)
		{
			this(new SimpleMetadata(id, version));
		}
		
		@Override
		public ModMetadata getMetadata()
		{
			return metadata;
		}
	}
}
