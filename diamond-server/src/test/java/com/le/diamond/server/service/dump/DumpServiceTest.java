package com.le.diamond.server.service.dump;

import com.le.diamond.server.service.PersistService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public class DumpServiceTest {

    private PersistService persistService;
    private DumpService dumpService;
    
    @Before
    public void setUp() throws Exception {
        persistService = new PersistService();
        dumpService = new DumpService(persistService);
    }
    
    @Test
    public void testDumpAll() {
        DumpAllProcessor processor = new DumpAllProcessor(dumpService);
        processor.process(null, null);
    }
}
