# -------------------------------------------------
# Create a Simulator object
# This is the main controller for the NS-2 simulation
# -------------------------------------------------
set ns [new Simulator]


# -------------------------------------------------
# Use colors to differentiate traffic flows in NAM
# Class 1 traffic → Blue
# Class 2 traffic → Red
# -------------------------------------------------
$ns color 1 Blue
$ns color 2 Red


# -------------------------------------------------
# Open trace file to store simulation events
# lab11.tr → used for analysis (drops, sends, receives)
# -------------------------------------------------
set ntrace [open lab11.tr w]
$ns trace-all $ntrace


# -------------------------------------------------
# Open NAM trace file for animation
# lab11.nam → visual animation
# -------------------------------------------------
set namfile [open lab11.nam w]
$ns namtrace-all $namfile


# -------------------------------------------------
# Finish procedure
# This procedure is called at the end of simulation
# -------------------------------------------------
proc Finish {} {
    global ns ntrace namfile

    # Flush remaining trace data
    $ns flush-trace

    # Close trace files
    close $ntrace
    close $namfile

    # Launch NAM animation
    exec nam lab11.nam &

    # Count dropped ping packets from trace file
    # ^d   → lines starting with 'd' (drop events)
    # field 5 → packet type
    puts "The number of ping packets dropped are "
    exec grep "^d" lab11.tr | cut -d " " -f 5 | grep -c "ping" &

    # Exit the simulation
    exit 0
}


# -------------------------------------------------
# Create six nodes: n(0) to n(5)
# -------------------------------------------------
for {set i 0} {$i < 6} {incr i} {
    set n($i) [$ns node]
}


# -------------------------------------------------
# Connect nodes in a linear topology
# n0 - n1 - n2 - n3 - n4 - n5
# Bandwidth = 0.1 Mb
# Delay = 10 ms
# Queue type = DropTail
# -------------------------------------------------
for {set j 0} {$j < 5} {incr j} {
    $ns duplex-link $n($j) $n([expr ($j+1)]) 0.1Mb 10ms DropTail
}


# -------------------------------------------------
# Define recv function for Ping agent
# This executes when a ping reply is received
# -------------------------------------------------
Agent/Ping instproc recv {from rtt} {
    $self instvar node_

    # Print which node received the ping and RTT
    puts "node [$node_ id] received ping answer from $from with round trip time $rtt ms"
}


# -------------------------------------------------
# Create Ping agent p0 and attach it to node n(0)
# -------------------------------------------------
set p0 [new Agent/Ping]
$p0 set class_ 1              ;# Class 1 → Blue color
$ns attach-agent $n(0) $p0


# -------------------------------------------------
# Create Ping agent p1 and attach it to node n(5)
# -------------------------------------------------
set p1 [new Agent/Ping]
$p1 set class_ 1              ;# Same class → Blue
$ns attach-agent $n(5) $p1


# -------------------------------------------------
# Connect ping agents
# p0 sends ping → p1 replies
# -------------------------------------------------
$ns connect $p0 $p1


# -------------------------------------------------
# Set queue size between n(2) and n(3)
# Queue limit = 2 packets (very small)
# This helps observe packet drops
# -------------------------------------------------
$ns queue-limit $n(2) $n(3) 2


# -------------------------------------------------
# Position the queue visually in the middle of the link (NAM)
# -------------------------------------------------
$ns duplex-link-op $n(2) $n(3) queuePos 0.5


# -------------------------------------------------
# Create congestion using TCP + CBR
# -------------------------------------------------

# Create TCP agent at node n(2)
set tcp0 [new Agent/TCP]
$tcp0 set class_ 2            ;# Class 2 → Red color
$ns attach-agent $n(2) $tcp0


# Create TCP sink at node n(4)
set sink0 [new Agent/TCPSink]
$ns attach-agent $n(4) $sink0


# Connect TCP source to TCP sink
$ns connect $tcp0 $sink0


# -------------------------------------------------
# Apply CBR traffic over TCP
# -------------------------------------------------
set cbr0 [new Application/Traffic/CBR]
$cbr0 set packetSize_ 500     ;# Packet size = 500 bytes
$cbr0 set rate_ 1Mb           ;# Sending rate = 1 Mbps
$cbr0 attach-agent $tcp0      ;# Attach CBR to TCP agent


# -------------------------------------------------
# Schedule events (timeline)
# -------------------------------------------------

$ns at 0.2 "$p0 send"         ;# Ping from n(0)
$ns at 0.4 "$p1 send"         ;# Ping from n(5)
$ns at 0.4 "$cbr0 start"      ;# Start congestion traffic
$ns at 0.8 "$p0 send"         ;# Ping during congestion
$ns at 1.0 "$p1 send"         ;# Ping during congestion
$ns at 1.2 "$cbr0 stop"       ;# Stop CBR traffic
$ns at 1.4 "$p0 send"         ;# Ping after congestion
$ns at 1.6 "$p1 send"         ;# Ping after congestion
$ns at 1.8 "Finish"           ;# End simulation


# -------------------------------------------------
# Run the simulation
# -------------------------------------------------
$ns run
