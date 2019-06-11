package com.sap.jvm.tools;

import java.io.IOException;

import com.sap.jvm.profiling.ProfilingFactory;
import com.sap.jvm.profiling.ProfilingSession;
import com.sap.jvm.profiling.core.ProfilingReader;
import com.sap.jvm.profiling.impl.core.ProfilingPacketImpl;
import com.sap.jvm.tracing.Trace;


/**
 * A simple profiling reader, which can dump the read data to stdout.
 *
 */
public class PacketFilter {

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
            System.out.println("Syntax: PacketFilter [-silent-events] " +
                    "[-ignore-line-nrs] [-skip-events] " +
                    "<prof-file>");
            return;
        }

        boolean printEventPackets = true;

        if (args[offset].equals("-silent-events")) {
            printEventPackets = false;
            offset += 1;
        }

        if (args[offset].equals("-skip-events")) {
            skipEvents = true;
            offset += 1;
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
                if (printEventPackets) {
                    packet.print(System.out);
                    System.out.println();
                }
            }
        } catch (IOException ex) {
            Trace.error(ex, "Could not process profiling file");
        }
    }
}
