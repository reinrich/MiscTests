package com.sap.jvm.tools;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;

import com.sap.jvm.profiling.ProfilingFactory;
import com.sap.jvm.profiling.ProfilingSession;
import com.sap.jvm.profiling.core.ProfilingReader;
import com.sap.jvm.profiling.impl.core.ProfilingPacketImpl;
import com.sap.jvm.profiling.impl.memory.event.GcStatisticImpl;
import com.sap.jvm.profiling.memory.event.GcEventBase;
import com.sap.jvm.profiling.memory.event.GcEventCmsInitialMarking;
import com.sap.jvm.tracing.Trace;


/**
 * A simple profiling reader, which can dump the read data to stdout.
 *
 */
public class PacketFilterExperiment {

    private static long prevMsAfter;
    private static long prevCcsAfter;
    private static long prevTs;
    private static long prevOldAfter;

    /**
     * Dumps the event packets of a specified file.
     *
     * @param args the filename for the mapping packet and additional flags.
     */
    public static void main(String[] args) {
        int offset = 0;
        boolean skipEvents = false;

        // print help when the wrong nr of arguments was given.
        if (args.length < 1) {
            System.out.println("Syntax: PacketFilter <prof-file>");
            return;
        }

        // the profiling packet reader
        ProfilingReader reader = null;

        try {
            // create the session
            ProfilingSession session = ProfilingFactory.createSession(args[offset]);

            // create the profiling packet reader
            reader = session.getReader();

            // if we want to skip events, only read the first event packet,
            // since it triggers reading of all mapping packets.
            if (skipEvents) {
                reader.nextPacket();

                return;
            }

            // iterate over the packets
            ProfilingPacketImpl packet = null;
            while ((packet = (ProfilingPacketImpl) reader.nextPacket()) != null) {
                if (packet instanceof GcStatisticImpl) {
                    processPacket((GcStatisticImpl) packet);
                }

            }
        } catch (IOException ex) {
            Trace.error(ex, "Could not process profiling file");
        }
    }

    private static void processPacket(GcStatisticImpl p) {
        PrintStream out = System.out;
        long ts = p.getLastEventEndTimeStamp();
        Date tsDate = new Date(ts);
        out.println();
        trc("GC Nr.  : #" + p.getGcNr());
        trc("TS end: " + tsDate);
        trc("Cause : " + p.getCause());
        trc("Type : " + (p.isFullGc() ? "full GC" : getGcType(p)));
        out.format("%-20s : %16d\n", "", 210987654321L);
        trcBefAfter("old gen", p.getBytesInOldBeforeGc(), p.getBytesInOldAfterGc(), prevOldAfter, p.getMaxOldGenSize());
        trcBefAfter("ms", p.getBytesInPermBeforeGc(), p.getBytesInPermAfterGc(), prevMsAfter, p.getMaxPermGenSize());
        trcBefAfter("ccs", p.getBytesInClassSpaceBeforeGc(), p.getBytesInClassSpaceAfterGc(), prevCcsAfter, p.getMaxClassSpaceSize());
        trc("mutatur duration : " + (ts - prevTs) + "ms");

        prevTs = ts;
        prevOldAfter = p.getBytesInOldAfterGc();
        prevMsAfter = p.getBytesInPermAfterGc();
        prevCcsAfter = p.getBytesInClassSpaceAfterGc();
    }

    private static String getGcType(GcStatisticImpl p) {
        GcEventBase[] evts = p.getEvents();
        if (evts[0] instanceof GcEventCmsInitialMarking)
            return "concurrent GC";
        return "young GC";
    }

    private static void trcBefAfter(String attrName, long bef, long aft, long prevAft, long max) {
        PrintStream out = System.out;
        out.format("%-20s : %16d (%.1f%% of %dMB) (diff to prev: %dKB)\n",
                attrName+" bef", bef, (float)bef/max*100f, max>>20, (bef-prevAft)>>10);
        out.format("%-20s : %16d (%.1f%% of %dMB) (diff %dKB)\n",
                attrName+" aft", aft, (float)aft/max*100f, max>>20, (aft-bef)>>10);
    }

    private static void trc(String s) {
        System.out.println(s);
    }
}
