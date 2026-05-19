/*
 * Minimal source-compatibility shim for Fabric Loader metadata.
 */
package net.fabricmc.loader.api.metadata;

import net.fabricmc.loader.api.Version;

public interface ModMetadata
{
	String getId();
	
	Version getVersion();
}
