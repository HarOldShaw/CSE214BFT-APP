/**
Copyright (c) 2007-2013 Alysson Bessani, Eduardo Alchieri, Paulo Sousa, and the authors indicated in the @author tags

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package bftsmart.app.shoppingcart;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import bftsmart.tom.MessageContext;
import bftsmart.tom.ReplicaContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;


/**
 *
 * @Edited by Haoru
 * This class will create a ServiceReplica and will initialize
 * it with a implementation of Executable and Recoverable interfaces. 
 */
public class CartServer extends DefaultSingleRecoverable {

    MapOfCarts cartMap = null;
    //ServiceReplica replica = null;
    //private ReplicaContext replicaContext;

    //The constructor passes the id of the server to the super class
    public CartServer(int id) {
        
        cartMap = new MapOfCarts();
        new ServiceReplica(id, this, this);
    }

    public static void main(String[] args){
        if(args.length < 1) {
            System.out.println("Use: java CartServer <processId>");
            System.exit(-1);
        }
        new CartServer(Integer.parseInt(args[0]));
    }
    

    @Override
    public byte[] appExecuteOrdered(byte[] command, MessageContext msgCtx) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(command);
            ByteArrayOutputStream out = null;
            byte[] reply = null;
            int cmd = new DataInputStream(in).readInt();
            switch (cmd) {
                //TODO: READ, READALL
                //operations on the hashmap
                case CartRequestType.PUT:
                    String tableName = new DataInputStream(in).readUTF();
                    String key = new DataInputStream(in).readUTF();
                    String value = new DataInputStream(in).readUTF();
                    byte[] valueBytes = value.getBytes();
                    System.out.println("Key received: " + key);
                    byte[] ret = cartMap.addProduct(tableName, key, valueBytes);
                    if (ret == null) {
//                        System.out.println("Return is null, so there was no data before");
                        ret = new byte[0];
                    }
                    reply = valueBytes;
                    break;
                case CartRequestType.REMOVE:
                    tableName = new DataInputStream(in).readUTF();
                    key = new DataInputStream(in).readUTF();
//                    System.out.println("Key received: " + key);
                    valueBytes = cartMap.removeEntry(tableName, key);
                    value = new String(valueBytes);
                    System.out.println("Value removed is : " + value);
                    out = new ByteArrayOutputStream();
                    new DataOutputStream(out).writeBytes(value);
                    reply = out.toByteArray();
                    break;
                case CartRequestType.CART_CREATE:
                    tableName = new DataInputStream(in).readUTF();
                    //ByteArrayInputStream in1 = new ByteArrayInputStream(command);
                    ObjectInputStream objIn = new ObjectInputStream(in);
                    Map<String, byte[]> table = null;
                    try {
                        table = (Map<String, byte[]>) objIn.readObject();
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(CartServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Map<String, byte[]> tableCreated = cartMap.addCart(tableName, table);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream objOut = new ObjectOutputStream(bos);
                    objOut.writeObject(tableCreated);
                    objOut.close();
                    in.close();
                    reply = bos.toByteArray();
                    break;
                case CartRequestType.READ: //newly added read
                    System.out.println("server read request Ordered");
                    tableName = new DataInputStream(in).readUTF();
                    System.out.println("tablename: " + tableName);
                    table = cartMap.getCart(tableName);
                    System.out.println("Order read cart -- table: " + table);
                    bos = new ByteArrayOutputStream();
                    objOut = new ObjectOutputStream(bos);
                    objOut.writeObject(table);
                    objOut.close();
                    objOut.close();
                    reply = bos.toByteArray();
                    break;

                case CartRequestType.CART_REMOVE:
                    tableName = new DataInputStream(in).readUTF();
                    table = cartMap.removeCart(tableName);
                    System.out.println("Order remove cart -- table: " + table);
                    bos = new ByteArrayOutputStream();
                    objOut = new ObjectOutputStream(bos);
                    objOut.writeObject(table);
                    objOut.close();
                    objOut.close();
                    reply = bos.toByteArray();
                    break;
                case CartRequestType.SIZE_CART:
                    int size1 = cartMap.getNumOfCarts();
                    System.out.println("Ordered Size " + size1);
                    out = new ByteArrayOutputStream();
                    new DataOutputStream(out).writeInt(size1);
                    reply = out.toByteArray();
                    break;
                case CartRequestType.GET:
                    tableName = new DataInputStream(in).readUTF();
                    System.out.println("tablename: " + tableName);
                    key = new DataInputStream(in).readUTF();
//                    System.out.println("Key received: " + key);
                    valueBytes = cartMap.getEntry(tableName, key);
                    value = new String(valueBytes);
                    System.out.println("The value to be get is: " + value);
                    out = new ByteArrayOutputStream();
                    new DataOutputStream(out).writeBytes(value);
                    reply = out.toByteArray();
                    break;
                case CartRequestType.SIZE:
                    String tableName2 = new DataInputStream(in).readUTF();
                    int size = cartMap.getSize(tableName2);
                    out = new ByteArrayOutputStream();
                    new DataOutputStream(out).writeInt(size);
                    reply = out.toByteArray();
                    break;
	            case CartRequestType.CHECK:
                    System.out.println("CHECK here 1");
	                tableName = new DataInputStream(in).readUTF();
	                key = new DataInputStream(in).readUTF();
  	                System.out.println("Table Key received: " + key);
	                valueBytes = cartMap.getEntry(tableName, key);
	                boolean entryExists = valueBytes != null;
	                out = new ByteArrayOutputStream();
	                new DataOutputStream(out).writeBoolean(entryExists);
	                reply = out.toByteArray();
	                break;
			    case CartRequestType.CART_CREATE_CHECK:
                    System.out.println("CART_CREATE_CHECK here 1");
			        tableName = new DataInputStream(in).readUTF();
			        System.out.println("Table of Table Key received: " + tableName);
			        table = cartMap.getCart(tableName);
			        boolean tableExists = (table != null);
			        System.out.println("Table exists 1: " + tableExists);
			        out = new ByteArrayOutputStream();
			        new DataOutputStream(out).writeBoolean(tableExists);
			        reply = out.toByteArray();
			        break;
            }
            return reply;
        } catch (IOException ex) {
            Logger.getLogger(CartServer.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public byte[] appExecuteUnordered(byte[] command, MessageContext msgCtx) {
    	try {
	        ByteArrayInputStream in = new ByteArrayInputStream(command);
	        ByteArrayOutputStream out = null;
	        byte[] reply = null;
	        int cmd = new DataInputStream(in).readInt();
	        switch (cmd) {
                case CartRequestType.SIZE_CART:
                    int size1 = cartMap.getNumOfCarts();
                    System.out.println("Unordered Size " + size1);
                    out = new ByteArrayOutputStream();
                    new DataOutputStream(out).writeInt(size1);
                    reply = out.toByteArray();
                    break;

                case CartRequestType.GET:
                    String tableName = new DataInputStream(in).readUTF();
                    System.out.println("tablename: " + tableName);
                    String key = new DataInputStream(in).readUTF();
//                    System.out.println("Key received: " + key);
                    byte[] valueBytes = cartMap.getEntry(tableName, key);
                    String value = new String(valueBytes);
                    System.out.println("GET -- The value to be get is: " + value);
                    out = new ByteArrayOutputStream();
                    new DataOutputStream(out).writeBytes(value);
                    reply = out.toByteArray();
                    break;
                case CartRequestType.SIZE:
                    String tableName2 = new DataInputStream(in).readUTF();
                    int size = cartMap.getSize(tableName2);
//                    System.out.println("Size " + size);
                    out = new ByteArrayOutputStream();
                    new DataOutputStream(out).writeInt(size);
                    reply = out.toByteArray();
                    break;
                case CartRequestType.READ: //newly added reaFd
                    System.out.println("server read request Ordered");
                    tableName = new DataInputStream(in).readUTF();
                    System.out.println("tablename: " + tableName);
                    Map<String,byte[]> map = cartMap.getCart(tableName);
                    value = "";
                    for (String k : map.keySet())
                    {
                        System.out.println("key: "+k+", value: "+map.get(k));
//                        value += map.get(k) + " ";
                        value = new String(map.get(k));
                    }
                    System.out.println("READ -- The value to be get is: " + value);
                    out = new ByteArrayOutputStream();
                    new DataOutputStream(out).writeBytes(value);
                    reply = out.toByteArray();
                    break;
                case CartRequestType.CHECK:
                    System.out.println("CHECK 2");
	                tableName = new DataInputStream(in).readUTF();
	                key = new DataInputStream(in).readUTF();
	                System.out.println("Table Key received: " + key);
	                valueBytes = cartMap.getEntry(tableName, key);
	                boolean entryExists = valueBytes != null;
	                out = new ByteArrayOutputStream();
	                new DataOutputStream(out).writeBoolean(entryExists);
	                reply = out.toByteArray();
	                break;
			    case CartRequestType.CART_CREATE_CHECK:
                    System.out.println("CART_CREATE_CHECK here 2");
			        tableName = new DataInputStream(in).readUTF();
			        System.out.println("Table of Table Key received: " + tableName);
			        Map<String, byte[]> table = cartMap.getCart(tableName);
			        boolean tableExists = (table != null);
			        System.out.println("Table exists 2: " + tableExists);
			        out = new ByteArrayOutputStream();
			        new DataOutputStream(out).writeBoolean(tableExists);
			        reply = out.toByteArray();
			        break;
	        }
	        return reply;
	    } catch (IOException ex) {
	        Logger.getLogger(CartServer.class.getName()).log(Level.SEVERE, null, ex);
	        return null;
	    }
    }

    @Override
    public byte[] getSnapshot() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(cartMap);
            out.flush();
            bos.flush();
            out.close();
            bos.close();
            return bos.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(CartServer.class.getName()).log(Level.SEVERE, null, ex);
            return new byte[0];
        }   
    }

    @Override
    public void installSnapshot(byte[] state) {
        try {

            // serialize to byte array and return
            ByteArrayInputStream bis = new ByteArrayInputStream(state);
            ObjectInput in = new ObjectInputStream(bis);
            cartMap = (MapOfCarts) in.readObject();
            in.close();
            bis.close();

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CartServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CartServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
}