package com.howtographql.hackernews;

import java.util.List;

import com.coxautodev.graphql.tools.GraphQLRootResolver;

public class Query implements GraphQLRootResolver {
    
    private final LinkRepository linkRepository;
    private final LinkRepositoryMongo linkRepositoryMongo;
    
    public Query(LinkRepository linkRepository,LinkRepositoryMongo linkRepositoryMongo) {
        this.linkRepository = linkRepository;
        this.linkRepositoryMongo = linkRepositoryMongo;
    }
    

    public List<Link> allLinks() {
        return linkRepository.getAllLinks();
    }
    

    public List<Link> allLinksMongo() {
        return linkRepositoryMongo.getAllLinks();
    }
    
    public List<Link> allLinksFilter(LinkFilter filter, Number skip, Number first) {
        return linkRepositoryMongo.getAllLinksFilter(filter, skip.intValue(), first.intValue());
    }
}