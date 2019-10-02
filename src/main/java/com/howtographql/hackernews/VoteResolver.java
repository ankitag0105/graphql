package com.howtographql.hackernews;

import com.coxautodev.graphql.tools.GraphQLResolver;

public class VoteResolver implements GraphQLResolver<Vote> {
    
    private final LinkRepositoryMongo linkRepositoryMongo;
    private final UserRepository userRepository;

    public VoteResolver(LinkRepositoryMongo linkRepositoryMongo, UserRepository userRepository) {
        this.linkRepositoryMongo = linkRepositoryMongo;
        this.userRepository = userRepository;
    }

    public User user(Vote vote) {
        return userRepository.findById(vote.getUserId());
    }
    
    public Link link(Vote vote) {
        return linkRepositoryMongo.findById(vote.getLinkId());
    }
}
