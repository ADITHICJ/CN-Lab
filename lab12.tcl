# ================================
# Create the Simulator object
# ================================
set ns [new Simulator]
# 'ns' controls the entire simulation (events, nodes, links, tracing)

# ================================
# Use colors to differentiate traffics in NAM
# ================================
$ns color 1 Blue
# Flow ID 1 packets will appear Blue in NAM

$ns color 2 Red
# Flow ID 2 packets will appear Red in NAM

# ================================
# Open trace and NAM trace files
# ================================
set ntrace [open lab12.tr w]
# Open a trace file to store simulation events

$ns trace-all $ntrace
# Enable tracing for all simulation events

set namfile [open lab12.nam w]
# Open NAM animation trace file

$ns namtrace-all $namfile
# Enable NAM tracing

# ================================
# Files to store congestion window values
# ================================
set winFile0 [open WinFile0 w]
# File for congestion window values of TCP flow 0

set winFile1 [open WinFile1 w]
# File for congestion window values of TCP flow 1

# ================================
# Finish Procedure
# ================================
proc Finish {} {
    # Declare global variables
    global ns ntrace namfile

    # Flush all pending trace data
    $ns flush-trace

    # Close trace files
    close $ntrace
    close $namfile

    # Open NAM animation
    exec nam lab12.nam &

    # Plot congestion window graphs using xgraph
    exec xgraph WinFile0 WinFile1 &

    # Exit the simulation
    exit 0
}

# ================================
# Procedure to plot TCP congestion window
# ================================
proc PlotWindow {tcpSource file} {
    global ns

    # Time interval between samples
    set time 0.1

    # Current simulation time
    set now [$ns now]

    # Get current congestion window value
    set cwnd [$tcpSource set cwnd_]

    # Write time and cwnd to file
    puts $file "$now $cwnd"

    # Schedule next cwnd recording
    $ns at [expr $now+$time] "PlotWindow $tcpSource $file"
}

# ================================
# Create 6 nodes
# ================================
for {set i 0} {$i<6} {incr i} {
    set n($i) [$ns node]
}
# Creates nodes n(0) to n(5)

# ================================
# Create duplex links between nodes
# ================================
$ns duplex-link $n(0) $n(2) 2Mb 10ms DropTail
# Link from node 0 to node 2 with:
# bandwidth = 2 Mbps, delay = 10 ms, DropTail queue

$ns duplex-link $n(1) $n(2) 2Mb 10ms DropTail
# Link from node 1 to node 2

$ns duplex-link $n(2) $n(3) 0.6Mb 100ms DropTail
# Bottleneck link with low bandwidth and high delay
# This causes congestion

# ================================
# Create a LAN between nodes 3, 4, and 5
# ================================
set lan [$ns newLan "$n(3) $n(4) $n(5)" 0.5Mb 40ms LL Queue/DropTail MAC/802_3 Channel]
# LAN parameters:
# Bandwidth = 0.5 Mbps
# Delay = 40 ms
# Ethernet MAC (802.3)

# ================================
# Set orientation for NAM visualization
# ================================
$ns duplex-link-op $n(0) $n(2) orient right-down
$ns duplex-link-op $n(1) $n(2) orient right-up
$ns duplex-link-op $n(2) $n(3) orient right

# ================================
# Configure queue between n(2) and n(3)
# ================================
$ns queue-limit $n(2) $n(3) 20
# Queue size limited to 20 packets

$ns duplex-link-op $n(2) $n(3) queuePos 0.5
# Position of queue in NAM animation

# ================================
# Set error model on bottleneck link
# ================================
set loss_module [new ErrorModel]
# Create error model object

$loss_module ranvar [new RandomVariable/Uniform]
# Use uniform random loss pattern

$loss_module drop-target [new Agent/Null]
# Drop packets using Null agent

$ns lossmodel $loss_module $n(2) $n(3)
# Apply loss model on link from node 2 to node 3

# ================================
# TCP connection 1: n(0) → n(4)
# ================================
set tcp0 [new Agent/TCP/Newreno]
# Create TCP NewReno agent

$tcp0 set fid_ 1
# Flow ID = 1 (Blue)

$tcp0 set window_ 8000
# Maximum congestion window size

$tcp0 set packetSize_ 552
# Packet size in bytes

$ns attach-agent $n(0) $tcp0
# Attach TCP agent to node 0

set sink0 [new Agent/TCPSink/DelAck]
# TCP sink with delayed ACKs

$ns attach-agent $n(4) $sink0
# Attach sink to node 4

$ns connect $tcp0 $sink0
# Connect TCP source and sink

# ================================
# FTP over TCP (Flow 1)
# ================================
set ftp0 [new Application/FTP]
# Create FTP application

$ftp0 attach-agent $tcp0
# Attach FTP to TCP agent

$ftp0 set type_ FTP
# Set application type

# ================================
# TCP connection 2: n(5) → n(1)
# ================================
set tcp1 [new Agent/TCP/Newreno]
# Second TCP NewReno agent

$tcp1 set fid_ 2
# Flow ID = 2 (Red)

$tcp1 set window_ 8000
# Maximum congestion window size

$tcp1 set packetSize_ 552
# Packet size

$ns attach-agent $n(5) $tcp1
# Attach TCP to node 5

set sink1 [new Agent/TCPSink/DelAck]
# TCP sink

$ns attach-agent $n(1) $sink1
# Attach sink to node 1

$ns connect $tcp1 $sink1
# Connect TCP source and sink

# ================================
# FTP over TCP (Flow 2)
# ================================
set ftp1 [new Application/FTP]
# Create FTP application

$ftp1 attach-agent $tcp1
# Attach FTP to TCP agent

$ftp1 set type_ FTP
# Set type

# ================================
# Schedule simulation events
# ================================
$ns at 0.1 "$ftp0 start"
# Start FTP flow 1 at 0.1s

$ns at 0.1 "PlotWindow $tcp0 $winFile0"
# Start recording congestion window for flow 1

$ns at 0.5 "$ftp1 start"
# Start FTP flow 2 at 0.5s

$ns at 0.5 "PlotWindow $tcp1 $winFile1"
# Start recording congestion window for flow 2

$ns at 25.0 "$ftp0 stop"
# Stop FTP flow 1

$ns at 25.1 "$ftp1 stop"
# Stop FTP flow 2

$ns at 25.2 "Finish"
# End simulation

# ================================
# Run the simulation
# ================================
$ns run
