package com.geode.gfsh;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.execute.FunctionException;
import com.gemstone.gemfire.cache.execute.FunctionService;
import com.gemstone.gemfire.cache.execute.ResultCollector;
import com.gemstone.gemfire.distributed.DistributedMember;
import com.gemstone.gemfire.management.internal.cli.CliUtil;
import com.geode.gfsh.function.GfshFunction;

public class GfshTest {

  private static GfshCommand gfshCommand;
  private static Cache cache;
  private Random random = new Random(System.currentTimeMillis());
  
    @BeforeClass
    public static void init() {
      CacheFactory cf = new CacheFactory();
          cf.set("cache-xml-file", "./grid/locator1/cluster_config/cluster/cluster.xml");
          cf.set("locators", "localhost[10334]");
          
      cache = cf.create();
      gfshCommand = new GfshCommand(cache.getLogger());
    }

    @Test(expected=RuntimeException.class)
    public void testCreateRegionBadOption() throws IllegalAccessException, InvocationTargetException, RuntimeException {
      Random r = new Random(System.currentTimeMillis());
      String regionName = "Test" + r.nextInt(1000);
      String command = "create region --name="
    		  + regionName
    		  + " --type=PARTITION_REDUNDANT_HEAP_LRU"
    		  + " --badOption=badValue";
      gfshCommand.execute(command);
      fail("This should have failed on a bad option");
    }

    @Test
    public void testCreateRegionPositive() 
    		throws IllegalAccessException, 
    			InvocationTargetException, 
    			RuntimeException {
      Random r = new Random(System.currentTimeMillis());
      String regionName = "Test" + r.nextInt(1000);
      String command = "create region --name="
    		  + regionName
    		  + " --type=PARTITION_REDUNDANT_HEAP_LRU";
      String stringResult = gfshCommand.execute(command);
      assert(stringResult.contains("Region \"/" + regionName + "\" created on") || 
    		 stringResult.contains("Region \"" + regionName + "\" created on") );
    }

    @Test
    public void testCreateRegionFunction() 
    		throws IllegalAccessException, 
    			InvocationTargetException, 
    			RuntimeException {
      Random r = new Random(System.currentTimeMillis());
      String regionName = "Test" + r.nextInt(1000);
      String command = "create region --name="
    		  + regionName
    		  + " --type=PARTITION_REDUNDANT_HEAP_LRU";
      
      try {
    	DistributedMember member = selectRandomMember();
        @SuppressWarnings("unchecked")
		ResultCollector<List<String>, List<String>> rCollector = (ResultCollector<List<String>, List<String>>) FunctionService.onMember(member).withArgs(command).execute(GfshFunction.class.getSimpleName());
        List<String> results = rCollector.getResult();
        boolean isCreated = false;
        for (String result : results) {
    	  if (result.contains("Region \"/" + regionName + "\" created on")) {
    		isCreated = true;
    		break;
    	  }
        }

      assertTrue("The region was not reported as created", isCreated);
      } catch (FunctionException e) {
          e.printStackTrace();
          fail("Region " + regionName + " was not created");
      }
    }
    
    /**
     * Select a random distributed member where we will execute the function call.
     * @return
     */
    private DistributedMember selectRandomMember() {
      Set<DistributedMember> members = CliUtil.getAllNormalMembers(cache);
   	  int i = random.nextInt(members.size());
   	  return (DistributedMember) members.toArray()[i];
    }
}
