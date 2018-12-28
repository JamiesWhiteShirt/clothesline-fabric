package com.jamieswhiteshirt.clotheslinefabric.util;

import com.jamieswhiteshirt.clotheslinefabric.common.impl.NetworkImpl;
import com.jamieswhiteshirt.clotheslinefabric.common.util.BasicNetwork;
import org.junit.jupiter.api.Test;

class BasicNetworkTest {
    @Test
    void persistsEquivalency() {
        NetworkImpl a = NetworkTests.ab.network;
        BasicNetwork basicNetwork = BasicNetwork.fromAbsolute(a);
        NetworkImpl b = basicNetwork.toAbsolute();
        NetworkTests.assertNetworksEquivalent(a, b);
    }
}
