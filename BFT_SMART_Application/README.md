# BFT_SMART_Application
A byzantantine fault tolerant transaction application based on BFT-SMART
#### Introduction
The application provides 10 request types for clients to generate new shopping carts and manipulate the data inside the shopping cart database, including CREATE, PUT, GET, SIZE, READ, READALL, REMOVE and INITIALIZE.


#### configuration
The server host configuration can be changed in ==config/hosts.config==
```
server id, address and port (the ids from 0 to n-1 are the service replicas) 
0 127.0.0.1 12000 12001
1 127.0.0.1 12010 12011
2 127.0.0.1 12020 12021
3 127.0.0.1 12030 12031
```

#### Quick Start
**Shopping Cart application**: to run the shopping cart application, first need to initiate 4 server replicas. Users can run:
```
./runscripts/smartrun.sh bftsmart.app.shoppingcart.CartServer 0
./runscripts/smartrun.sh bftsmart.app.shoppingcart.CartServer 1
./runscripts/smartrun.sh bftsmart.app.shoppingcart.CartServer 2
./runscripts/smartrun.sh bftsmart.app.shoppingcart.CartServer 3
```

After the replicas are all set, can start the client by running: 
```
./runscripts/smartrun.sh bftsmart.app.shoppingcart.CartInteractiveClient 0
```

**YCSB**: to run through the Yahoo! Cloud serving Benchmark, 4 replicas running YCSB server need to initiated, users can run:

```
./runscripts/smartrun.sh bftsmart.app.ycsb.YCSBServer 0
./runscripts/smartrun.sh bftsmart.app.ycsb.YCSBServer 1
./runscripts/smartrun.sh bftsmart.app.ycsb.YCSBServer 2
./runscripts/smartrun.sh bftsmart.app.ycsb.YCSBServer 3
```

After that, simply run the YCSB client:
```
./runscripts/ycsbClient.sh 
```