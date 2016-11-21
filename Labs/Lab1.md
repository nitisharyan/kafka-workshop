# Kafka Lab 1 - Generating a Generic Cpu And Memory Producer

In this lab you will receive a starter project in Java that has a code that can sample CPU Usage and Memory Usage in the computer where the process is generated.

Your task will be to implement a the KafkaProducer API to send these metrics every 5 seconds to a Kafka cluster, to a topic named “**cpuMetrics**”.

In order to complete the lab, you need to send every message to a specific partition, that will be calculated using a hash on mac address (also available through a ready method).

Every message sent to the Kafka cluster should have the following properties (example follows):
    1. **macAddress** - the MAC address provided by the appropriate method as a String
    2. **cpuTime** - the CPU usage parmeter provided by the appropriate method as a float number (indicating percent)
    3. **freeRam** - the freem RAM usage paramter provided by the appropriate method as a long integer (indicating bytes)
    4. **measuredTimeNano** - the current nano second usage (use System.nanotTime() method) when the sampling occured as a long integer.

Example:
```Json
{
    "macAddress": "12345-qwer-asdf-zxcv",
    "cpuTime": 12.34,
    "freeRam": 1234567,
    "measuredTimeNano": 8765431786234
}
```

The endpoint for the Kafka cluster (and other configurations) will be made available by the instructor.