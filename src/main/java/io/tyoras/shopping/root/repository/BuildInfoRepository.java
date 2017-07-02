/**
 * 
 */
package io.tyoras.shopping.root.repository;

import io.tyoras.shopping.root.BuildInfo;

/**
 * Build information repository
 * @author yoan
 */
public interface BuildInfoRepository {
	
	/**
	 * Get information about the current build
	 * @return build information, null if not found
	 */
	public BuildInfo getCurrentBuildInfos();
}
