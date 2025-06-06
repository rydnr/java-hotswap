package org.acmsl.hotswap.test;

import net.bytebuddy.agent.ByteBuddyAgent;
import org.acmsl.hotswap.agent.AgentBootstrap;

public class AgentRunner {
    public static void main(String[] args) throws Exception {
        var inst = ByteBuddyAgent.install();
        AgentBootstrap.premain("", inst);
        while (true) {
            Thread.sleep(1000);
        }
    }
}
