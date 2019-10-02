package com.howtographql.hackernews;


import com.coxautodev.graphql.tools.SchemaParser;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import graphql.ExceptionWhileDataFetching;
import graphql.GraphQLError;
import graphql.schema.GraphQLSchema;
import graphql.servlet.GraphQLContext;
import graphql.servlet.SimpleGraphQLServlet;



@WebServlet(urlPatterns = "/graphql")
public class GraphQLEndpoint extends SimpleGraphQLServlet { 
	
	 private static final LinkRepository linkRepository = new LinkRepository();
	 private static final LinkRepositoryMongo linkRepositoryMongo;
	 private static final UserRepository userRepository;
	 private static final VoteRepository voteRepository;
	
	 static{

   	 	MongoDatabase mongo = new MongoClient().getDatabase("graphql");
   	 	linkRepositoryMongo = new LinkRepositoryMongo(mongo.getCollection("hackernews"));
   	 	userRepository = new UserRepository(mongo.getCollection("users"));
   	 	voteRepository = new VoteRepository(mongo.getCollection("votes"));
   	   
	}
	   
    public GraphQLEndpoint() {
        super(buildSchema());
    }

    private static GraphQLSchema buildSchema() {
    	 
        return SchemaParser.newParser()
                .file("schema.graphqls")
                .resolvers(new Query(linkRepository,linkRepositoryMongo), 
                		new Mutation(linkRepository,linkRepositoryMongo, userRepository, voteRepository),
                		new SigninResolver(),
                		new LinkResolver(userRepository),
                		new VoteResolver(linkRepositoryMongo, userRepository))
                .scalars(Scalars.dateTime)
                .build()
                .makeExecutableSchema();
    }
    
    
    @Override
    protected GraphQLContext createContext(Optional<HttpServletRequest> request, Optional<HttpServletResponse> response) {
        User user = request
            .map(req -> req.getHeader("Authorization"))
            .filter(id -> !id.isEmpty())
            .map(id -> id.replace("Bearer ", ""))
            .map(userRepository::findById)
            .orElse(null);
        return new AuthContext(user, request, response);
    }
    
    @Override
    protected List<GraphQLError> filterGraphQLErrors(List<GraphQLError> errors) {
        return errors.stream()
                .filter(e -> e instanceof ExceptionWhileDataFetching || super.isClientError(e))
                .map(e -> e instanceof ExceptionWhileDataFetching ? new SanitizedError((ExceptionWhileDataFetching) e) : e)
                .collect(Collectors.toList());
    }
}