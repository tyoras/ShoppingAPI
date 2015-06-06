/**
 * 
 */
package yoan.shopping.root.repository.fake;

import yoan.shopping.root.BuildInfo;
import yoan.shopping.root.repository.BuildInfoRepository;

/**
 * Fake implementation of Build info repository
 * Test purpose only
 * @author yoan
 */
public class BuildInfoFakeRepository implements BuildInfoRepository {

	@Override
	public BuildInfo getCurrentBuildInfos() {
		return BuildInfo.DEFAULT;
	}

}
