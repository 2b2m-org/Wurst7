/*
 * Minimal source-compatibility shim for Fabric Loader metadata.
 */
package net.fabricmc.loader.api;

import net.fabricmc.loader.api.metadata.ModMetadata;

public interface ModContainer
{
	ModMetadata getMetadata();
}
