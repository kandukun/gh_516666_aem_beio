package com.bms.beio.events;

import com.bms.beio.resource.BEIOAutoClosingResourceResolverFactory;
import com.day.cq.replication.AgentManager;
import com.day.cq.replication.Replicator;

@FunctionalInterface
public interface ResourceReplicator {
	public void replicateResource(String path,BEIOAutoClosingResourceResolverFactory autoClosingResourceResolverFactory,Replicator replicator,AgentManager agentManager,String[] replicationAgents);
}
