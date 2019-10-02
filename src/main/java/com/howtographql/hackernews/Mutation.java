package com.howtographql.hackernews;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.coxautodev.graphql.tools.GraphQLRootResolver;

import graphql.GraphQLException;
import graphql.schema.DataFetchingEnvironment;

public class Mutation implements GraphQLRootResolver {
    
    private final LinkRepository linkRepository;
    private final LinkRepositoryMongo linkRepositoryMongo;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;

    public Mutation(LinkRepository linkRepository, LinkRepositoryMongo linkRepositoryMongo,
    		UserRepository userRepository, VoteRepository voteRepository) {
        this.linkRepository = linkRepository;
        this.linkRepositoryMongo = linkRepositoryMongo;
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
    }
    
    public Link createLink(String url, String description) {
    	
        Link newLink = new Link(url, description);
        linkRepository.saveLink(newLink);
        return newLink;
    }
   
    
    public Link createLinkMongo(String url, String description, DataFetchingEnvironment env) {
    	AuthContext context = env.getContext();
    	Link newLink = new Link(url, description, context.getUser().getId());
        linkRepositoryMongo.saveLink(newLink);
        return newLink;
    }
    
    
    public User createUser(String name, AuthData auth){
    	User user = new User(name, auth.getEmail(), auth.getPassword());
    	return userRepository.saveUser(user);
    }
   
    
    public SigninPayload signinUser(AuthData auth) throws IllegalAccessException {
        User user = userRepository.findByEmail(auth.getEmail());
        if (user.getPassword().equals(auth.getPassword())) {
            return new SigninPayload(user.getId(), user);
        }
        throw new GraphQLException("Invalid credentials");
    }
    
    
    public Vote createVote(String linkId, String userId) {
        ZonedDateTime now = Instant.now().atZone(ZoneOffset.UTC);
        return voteRepository.saveVote(new Vote(now, userId, linkId));
    }
}
