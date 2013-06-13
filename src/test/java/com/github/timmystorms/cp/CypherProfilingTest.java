package com.github.timmystorms.cp;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

@FixMethodOrder(MethodSorters.JVM)
public class CypherProfilingTest {

    private GraphDatabaseService db;

    private ExecutionEngine execEngine;

    @Before
    public void init() {
        db = new GraphDatabaseFactory().newEmbeddedDatabase("db");
        execEngine = new ExecutionEngine(db);
        warmCaches();
    }
    
    @After
    public void destroy() {
        db.shutdown();
    }
    
    @Test
    public void executeWithoutBrackets() {
        long start = System.currentTimeMillis();
        ExecutionResult result = execEngine.execute("start n=node:Person(\"name:*Andres*\") where id(n)> 10000 and n.version < 40 return n as actors order by n.name limit 10");
        System.out.println("Without brackets: " + (System.currentTimeMillis() - start) + " ms");
        System.out.println(result.executionPlanDescription());
    }
    
    @Test
    public void executeWithWayToMuchBrackets() {
        long start = System.currentTimeMillis();
        ExecutionResult result = execEngine.execute("start n=node:Person(\"name:*Andres*\") where ((id(n)> 10000) and (n.version < 40)) return n as actors order by n.name limit 10");
        System.out.println("With brackets: " + (System.currentTimeMillis() - start) + " ms");
        System.out.println(result.executionPlanDescription());
    }

    private void warmCaches() {
        for (int i = 0; i < 3; i++) {
            execEngine.execute("start n=node(*) return count(n)");
            execEngine.execute("start r=rel(*) return count(r)");
        }
    }

}
